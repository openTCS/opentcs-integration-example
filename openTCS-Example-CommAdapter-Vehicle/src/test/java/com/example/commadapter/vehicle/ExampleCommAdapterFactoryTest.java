/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package com.example.commadapter.vehicle;

import com.example.common.VehicleProperties;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import org.opentcs.data.model.Vehicle;

/**
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class ExampleCommAdapterFactoryTest {

  private ExampleCommAdapterFactory commAdapterFactory;

  @Before
  public void setUp() {
    commAdapterFactory = new ExampleCommAdapterFactory(mock(ExampleAdapterComponentsFactory.class));
  }

  @Test
  public void provideAdapterForVehicleWithAllProperties() {
    assertTrue(commAdapterFactory.providesAdapterFor(
        new Vehicle("Some vehicle")
            .withProperty(VehicleProperties.PROPKEY_VEHICLE_HOST, "127.0.0.1")
            .withProperty(VehicleProperties.PROPKEY_VEHICLE_PORT, "8888")
    ));
  }

  @Test
  public void provideAdapterForVehicleMissingPort() {
    assertFalse(commAdapterFactory.providesAdapterFor(
        new Vehicle("Some vehicle")
            .withProperty(VehicleProperties.PROPKEY_VEHICLE_HOST, "127.0.0.1")
    ));
  }

  @Test
  public void provideAdapterForVehicleMissingHost() {
    assertFalse(commAdapterFactory.providesAdapterFor(
        new Vehicle("Some vehicle")
            .withProperty(VehicleProperties.PROPKEY_VEHICLE_PORT, "8888")
    ));
  }

  @Test
  public void provideAdapterForVehicleWithUnparsablePort() {
    assertFalse(commAdapterFactory.providesAdapterFor(
        new Vehicle("Some vehicle")
            .withProperty(VehicleProperties.PROPKEY_VEHICLE_HOST, "127.0.0.1")
            .withProperty(VehicleProperties.PROPKEY_VEHICLE_PORT, "xyz")
    ));
  }

}
