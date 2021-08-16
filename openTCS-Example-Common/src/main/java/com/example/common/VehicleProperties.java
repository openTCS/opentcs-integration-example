/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package com.example.common;

/**
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public interface VehicleProperties {

  /**
   * The key of the vehicle property containing the vehicle's host name/IP address.
   */
  String PROPKEY_VEHICLE_HOST = "example:vehicleHost";
  /**
   * The key of the vehicle property containing the vehicle's TCP port.
   */
  String PROPKEY_VEHICLE_PORT = "example:vehiclePort";
}
