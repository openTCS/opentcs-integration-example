/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.contrib.communication.tcp;

/**
 * A user event triggered when a channel is being associated to a key.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class ConnectionAssociatedEvent {

  /**
   * The key a connection was associated to.
   */
  private final Object key;

  public ConnectionAssociatedEvent(Object key) {
    this.key = key;
  }

  public Object getKey() {
    return key;
  }
}
