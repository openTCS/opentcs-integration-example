/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.contrib.communication.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.List;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import static org.opentcs.util.Assertions.checkArgument;
import static org.opentcs.util.Assertions.checkState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages a TCP connection to a peer, with the peer being the client.
 * <p>
 * Note that the name of the {@link LoggingHandler} that this class optionally registers is this
 * class's fully qualified name.
 * </p>
 *
 * @author Martin Grzenia (Fraunhofer IML)
 * @author Stefan Walter (Fraunhofer IML)
 * @param <I> The type of incoming messages on this TcpServerChannelManager.
 * @param <O> The type of outgoing messages on this TcpServerChannelManager.
 */
public class TcpServerChannelManager<I, O> {

  /**
   * This class's Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(TcpServerChannelManager.class);
  /**
   * The name for logging handlers.
   */
  private static final String LOGGING_HANDLER_NAME = "ChannelLoggingHandler";
  /**
   * Bootstraps the channel.
   */
  private ServerBootstrap bootstrap;
  /**
   * The port to listen on.
   */
  private final int port;
  /**
   * A pool of clients that may connect to this manager.
   */
  private final Map<Object, ClientEntry<I>> clientEntries;
  /**
   * A supplier for lists of {@link ChannelHandler} instances to be added to the pipeline of each
   * new connection.
   */
  private final Supplier<List<ChannelHandler>> channelSupplier;
  /**
   * Whether to enable logging for channels initially.
   */
  private final boolean loggingInitially;
  /**
   * Manages the server channel.
   */
  private ChannelFuture serverChannelFuture;
  /**
   * Whether this component is initialized or not.
   */
  private boolean initialized;
  /**
   * The read timeout (in milliseconds). Zero if disabled.
   */
  private final int readTimeout;

  /**
   * Creates a new instance.
   *
   * @param port The port on which to listen for incoming connections.
   * @param clientEntries Entries for clients accepting connections via this channel manager.
   * @param channelSupplier A supplier for lists of {@link ChannelHandler} instances that should be
   * added to the pipeline of each new connection.
   * @param readTimeout A timeout in milliseconds after which a connection should be closed if no
   * data was received over it. May be zero to disable.
   * @param loggingInitially Whether to turn on logging by default for new connections.
   */
  public TcpServerChannelManager(int port,
                                 Map<Object, ClientEntry<I>> clientEntries,
                                 Supplier<List<ChannelHandler>> channelSupplier,
                                 int readTimeout,
                                 boolean loggingInitially) {
    checkArgument(port > 0, "port <= 0: %s", port);
    this.port = port;
    this.clientEntries = requireNonNull(clientEntries, "clientEntries");
    this.channelSupplier = requireNonNull(channelSupplier, "channelSupplier");
    checkArgument(readTimeout >= 0, "readTimeout < 0: %s", readTimeout);
    this.readTimeout = readTimeout;
    this.loggingInitially = loggingInitially;
  }

  public void initialize() {
    if (initialized) {
      return;
    }

    bootstrap = new ServerBootstrap();
    bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup());
    bootstrap.channel(NioServerSocketChannel.class);
    bootstrap.option(ChannelOption.SO_BACKLOG, 1);
    bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
    bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
    bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(SocketChannel ch) {
        if (loggingInitially) {
          ch.pipeline().addFirst(LOGGING_HANDLER_NAME,
                                 new LoggingHandler(TcpServerChannelManager.this.getClass()));
        }
        if (readTimeout > 0) {
          ch.pipeline().addLast(new IdleStateHandler(readTimeout, 0, 0, TimeUnit.MILLISECONDS));
        }
        for (ChannelHandler handler : channelSupplier.get()) {
          ch.pipeline().addLast(handler);
        }
        ch.pipeline().addLast(new ServerConnectionStateNotifier<>(clientEntries));
      }

    });
    serverChannelFuture = bootstrap.bind(port);

    initialized = true;
  }

  /**
   * Disconnects the channel if it is connected and frees all resources.
   * This method should be called when this instance isn't needed any more. Once it has been called,
   * the behaviour of all other methods is undefined.
   */
  public void terminate() {
    if (!initialized) {
      return;
    }

    serverChannelFuture.channel().close();
    serverChannelFuture = null;
    for (ClientEntry<I> clientEntry : clientEntries.values()) {
      clientEntry.disconnect();
    }
    clientEntries.clear();
    bootstrap.config().group().shutdownGracefully();
    bootstrap.config().childGroup().shutdownGracefully();

    initialized = false;
  }

  public boolean isInitialized() {
    return initialized;
  }

  public void register(Object key,
                       ConnectionEventListener<I> connectionEventListener,
                       boolean enableLogging) {
    checkState(initialized, "Not initialized.");

    if (clientEntries.containsKey(key)) {
      LOG.warn("A handler for '{}' is already registered.", key);
      return;
    }

    LOG.debug("Registering handler for client '{}'", key);
    clientEntries.put(key, new ClientEntry<>(key, connectionEventListener));
  }

  public void unregister(Object key) {
    checkState(initialized, "Not initialized.");

    ClientEntry<I> client = clientEntries.remove(key);
    if (client != null) {
      client.disconnect();
    }
  }

  public void reregister(Object key,
                         ConnectionEventListener<I> messageHandler,
                         boolean enableLogging) {
    unregister(key);
    register(key, messageHandler, enableLogging);
  }

  public void closeClientConnection(Object key) {
    checkState(initialized, "Not initialized.");

    if (isClientConnected(key)) {
      LOG.debug("Closing connection to client {}", key);
      clientEntries.get(key).getChannel().disconnect();
      clientEntries.get(key).setChannel(null);
    }
  }

  /**
   * Checks whether a connection to the given client exists.
   *
   * @param key The key associated with the client.
   * @return <code>true</code> if, and only if, a connection has been initiated and is active.
   */
  public boolean isClientConnected(Object key) {
    return serverChannelFuture != null
        && clientEntries.containsKey(key)
        && clientEntries.get(key).getChannel() != null
        && clientEntries.get(key).getChannel().isActive();
  }

  /**
   * Encodes and sends a telegram to the peer, if connected.
   *
   * @param telegram The telegram.
   * @param key The key associated to the client the telegram should be sent to.
   */
  public void send(Object key, O telegram) {
    checkState(initialized, "Not initialized.");

    if (!isClientConnected(key)) {
      LOG.warn("Failed sending telegram {}. {} is not connected.", telegram, key);
      return;
    }
    LOG.debug("Sending telegram {} to {}.", telegram, key);

    clientEntries.get(key).getChannel().writeAndFlush(telegram);
  }

  /**
   * Enables or disables logging for the client entry with the registered key.
   *
   * @param key The key identifying the client entry.
   * @param enabled Indicates whether to enable or disable logging for the client entry.
   */
  public void setLoggingEnabled(Object key, boolean enabled) {
    checkState(initialized, "Not initialized.");

    ClientEntry<I> entry = clientEntries.get(key);
    checkArgument(entry != null, "No client registered for key '%s'", key);

    Channel channel = entry.getChannel();
    if (channel == null) {
      LOG.debug("No channel/pipeline for key '%s', doing nothing.");
      return;
    }

    ChannelPipeline pipeline = channel.pipeline();
    if (enabled && pipeline.get(LOGGING_HANDLER_NAME) == null) {
      pipeline.addFirst(LOGGING_HANDLER_NAME,
                        new LoggingHandler(TcpServerChannelManager.this.getClass()));
    }
    else if (!enabled && pipeline.get(LOGGING_HANDLER_NAME) != null) {
      pipeline.remove(LOGGING_HANDLER_NAME);
    }
  }

  /**
   * Returns the port on which this channel manager listens on for incoming connections.
   *
   * @return The port.
   */
  public int getPort() {
    return port;
  }

  /**
   * Returns the timeout in milliseconds after which a connection is closed if no data was received
   * over it.
   *
   * @return The read timeout in milliseconds. Zero if disabled.
   */
  public int getReadTimeout() {
    return readTimeout;
  }
}
