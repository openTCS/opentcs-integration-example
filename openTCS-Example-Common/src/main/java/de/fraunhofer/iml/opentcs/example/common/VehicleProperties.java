/**
 * Copyright (c) Fraunhofer IML
 */
package de.fraunhofer.iml.opentcs.example.common;

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
