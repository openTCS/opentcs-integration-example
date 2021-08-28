/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.contrib.communication.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import static org.opentcs.util.Assertions.checkState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages a TCP connection to a peer, with the peer being the server.
 * <p>
 * Note that the name of the {@link LoggingHandler} that this class optionally registers is this
 * class's fully qualified name.
 * </p>
 *
 * @author Martin Grzenia (Fraunhofer IML)
 * @author Stefan Walter (Fraunhofer IML)
 * @param <O> The type of outgoing messages on this TcpClientChannelManager.
 * @param <I> The type of incoming messages on this TcpClientChannelManager.
 */
public class TcpClientChannelManager<O, I> {

  /**
   * This class's Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(TcpClientChannelManager.class);
  /**
   * The name for logging handlers.
   */
  private static final String LOGGING_HANDLER_NAME = "ChannelLoggingHandler";
  /**
   * Handles incoming telegrams.
   */
  private final ConnectionEventListener<I> connectionEventListener;
  /**
   * A supplier for unique channel handlers for this channel manager.
   */
  private final Supplier<List<ChannelHandler>> channelSupplier;
  /**
   * The read timeout (in milliseconds).
   */
  private final int readTimeout;
  /**
   * Bootstraps the channel.
   */
  private Bootstrap bootstrap;
  /**
   * Manages the bootstrap's threads.
   */
  private EventLoopGroup workerGroup;
  /**
   * Manages the current channel/connection.
   */
  private ChannelFuture channelFuture;
  /**
   * Whether this component is initialized or not.
   */
  private boolean initialized;
  /**
   * A future for a waiting connection attempt.
   */
  private ScheduledFuture<?> connectFuture;
  /**
   * Whether logging should be enabled or not.
   */
  private final boolean loggingEnabled;

  /**
   * Creates a new instance.
   *
   * @param connEventListener A handler for incoming state telegrams.
   * @param channelSupplier A supplier for unique channel handlers for this channel manager.
   * @param readTimeout The read timeout (in milliseconds).
   * @param enableLogging Enables or disables logging for this ChannelManager.
   */
  public TcpClientChannelManager(
      @Nonnull ConnectionEventListener<I> connEventListener,
      Supplier<List<ChannelHandler>> channelSupplier,
      int readTimeout,
      boolean enableLogging) {
    this.connectionEventListener = requireNonNull(connEventListener, "connEventListener");
    this.channelSupplier = requireNonNull(channelSupplier, "channelSupplier");
    this.readTimeout = readTimeout;
    this.loggingEnabled = enableLogging;
  }

  public void initialize() {
    if (initialized) {
      return;
    }

    bootstrap = new Bootstrap();
    workerGroup = new NioEventLoopGroup();
    bootstrap.group(workerGroup);
    bootstrap.channel(NioSocketChannel.class);
    bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
    bootstrap.option(ChannelOption.TCP_NODELAY, true);
    bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
    bootstrap.handler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(SocketChannel ch) {
        if (loggingEnabled) {
          ch.pipeline().addFirst(LOGGING_HANDLER_NAME,
                                 new LoggingHandler(TcpClientChannelManager.this.getClass()));
        }
        if (readTimeout > 0) {
          ch.pipeline().addLast(new IdleStateHandler(readTimeout, 0, 0, TimeUnit.MILLISECONDS));
        }
        ch.pipeline().addLast(new ClientConnectionDropNotifier(connectionEventListener));
        for (ChannelHandler handler : channelSupplier.get()) {
          ch.pipeline().addLast(handler);
        }
      }
    });

    initialized = true;
  }

  public boolean isInitialized() {
    return initialized;
  }

  public void terminate() {
    if (!initialized) {
      return;
    }

    cancelConnect();
    disconnect();
    workerGroup.shutdownGracefully();
    workerGroup = null;
    bootstrap = null;

    initialized = false;
  }

  /**
   * Initiates a connection (attempt) to the remote host and port.
   *
   * @param host The host to connect to.
   * @param port The port to connect to.
   */
  public void connect(@Nonnull String host, int port) {
    requireNonNull(host, "host");
    checkState(isInitialized(), "Not initialized");
    if (isConnected()) {
      LOG.debug("Already connected, doing nothing.");
      return;
    }

    LOG.debug("Initiating connection attempt to {}:{}...", host, port);
    channelFuture = bootstrap.connect(host, port);
    channelFuture.addListener((ChannelFuture future) -> {
      if (future.isSuccess()) {
        connectionEventListener.onConnect();
      }
      else {
        connectionEventListener.onFailedConnectionAttempt();
      }
    });
    connectFuture = null;
  }

  /**
   * Schedules a connection attempt to be executed after the given delay.
   * <p>
   * This method does not block but merely schedules the connection attempt to be executed.
   * </p>
   *
   * @param host The host to connect to.
   * @param port The port to connect to.
   * @param delay The delay in milliseconds to wait before executing.
   */
  public void scheduleConnect(@Nonnull String host, int port, long delay) {
    requireNonNull(host, "host");
    checkState(isInitialized(), "Not initialized");
    checkState(connectFuture == null, "Connection attempt already scheduled");

    connectFuture = workerGroup.schedule(() -> connect(host, port), delay, TimeUnit.MILLISECONDS);
  }

  /**
   * Cancels a scheduled connection attempt.
   */
  public void cancelConnect() {
    if (connectFuture == null) {
      return;
    }
    connectFuture.cancel(false);
    connectFuture = null;
  }

  /**
   * Terminates any existing connection.
   */
  public void disconnect() {
    if (!isConnected()) {
      return;
    }
    if (channelFuture != null) {
      channelFuture.channel().disconnect();
      channelFuture = null;
    }
  }

  /**
   * Checks whether a connection exists.
   *
   * @return <code>true</code> if, and only if, a connection has been initiated and is active.
   */
  public boolean isConnected() {
    return channelFuture != null && channelFuture.channel().isActive();
  }

  /**
   * Encodes and sends a telegram to the peer, if connected.
   *
   * @param telegram The telegram.
   */
  public void send(O telegram) {
    if (!isConnected()) {
      return;
    }
    channelFuture.channel().writeAndFlush(telegram);
  }

  /**
   * Enables or disables logging for the client.
   *
   * @param enabled Indicates whether to enable or disable logging for the client.
   */
  public void setLoggingEnabled(boolean enabled) {
    checkState(initialized, "Not initialized.");

    if (channelFuture == null) {
      LOG.debug("No channel future available, doing nothing.");
      return;
    }

    ChannelPipeline pipeline = channelFuture.channel().pipeline();
    if (enabled && pipeline.get(LOGGING_HANDLER_NAME) == null) {
      pipeline.addFirst(LOGGING_HANDLER_NAME,
                        new LoggingHandler(TcpClientChannelManager.this.getClass()));
    }
    else if (!enabled && pipeline.get(LOGGING_HANDLER_NAME) != null) {
      pipeline.remove(LOGGING_HANDLER_NAME);
    }
  }
}
