/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package com.example.common.dispatching;

import org.opentcs.data.order.DriveOrder;

/**
 * Defines (configurable) strings for loading and unloading that can be used for vehicle actions in
 * the kernel's model.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public interface LoadAction {

  public static final String NONE = DriveOrder.Destination.OP_NOP;
  /**
   * A constant for adding load.
   */
  public static final String LOAD = "Load cargo";
  /**
   * A constant for removing load.
   */
  public static final String UNLOAD = "Unload cargo";
  /**
   * A constant for charging the battery.
   */
  public static final String CHARGE = "Charge";
}
