/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.contrib.communication.tcp;

import io.netty.channel.Channel;
import static java.util.Objects.requireNonNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a client.
 *
 * @author Stefan Walter (Fraunhofer IML)
 * @param <I> The type of this entity's message handler.
 */
public class ClientEntry<I> {

  /**
   * The key of this entry.
   * Identifies a specific client in a pool of clients.
   */
  private final Object key;
  /**
   * The handler to send events about the connection to.
   */
  private final ConnectionEventListener<I> connectionEventListener;
  /**
   * Manages the current channel/connection.
   */
  private Channel channel;

  /**
   * Creates a new instance.
   *
   * @param key The key identifying the client.
   * @param connectionEventListener The handler to send events about the connection to.
   */
  public ClientEntry(@Nonnull Object key,
                     @Nonnull ConnectionEventListener<I> connectionEventListener) {
    this.key = requireNonNull(key, "key");
    this.connectionEventListener = requireNonNull(connectionEventListener);
  }

  @Nonnull
  public Object getKey() {
    return key;
  }

  @Nonnull
  public ConnectionEventListener<I> getConnectionEventListener() {
    return connectionEventListener;
  }

  @Nullable
  public Channel getChannel() {
    return channel;
  }

  public void setChannel(@Nullable Channel channel) {
    this.channel = channel;
  }

  public boolean isConnected() {
    return channel != null && channel.isActive();
  }

  public void disconnect() {
    if (!isConnected()) {
      return;
    }
    channel.disconnect();
    channel = null;
  }

}
