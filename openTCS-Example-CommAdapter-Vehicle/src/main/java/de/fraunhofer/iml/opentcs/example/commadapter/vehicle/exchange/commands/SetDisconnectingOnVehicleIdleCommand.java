/**
 * Copyright (c) Fraunhofer IML
 */
package de.fraunhofer.iml.opentcs.example.commadapter.vehicle.exchange.commands;

import de.fraunhofer.iml.opentcs.example.commadapter.vehicle.ExampleCommAdapter;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;

/**
 * A command to set the adapter's disconnecot on vehicle idle flag.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class SetDisconnectingOnVehicleIdleCommand
    implements AdapterCommand {

  /**
   * The flag state to set.
   */
  private final boolean disconnect;

  /**
   * Creates a new instance.
   *
   * @param disconnect The flag state to set
   */
  public SetDisconnectingOnVehicleIdleCommand(boolean disconnect) {
    this.disconnect = disconnect;
  }

  @Override
  public void execute(VehicleCommAdapter adapter) {
    if (!(adapter instanceof ExampleCommAdapter)) {
      return;
    }

    ExampleCommAdapter exampleAdapter = (ExampleCommAdapter) adapter;
    exampleAdapter.getProcessModel().setDisconnectingOnVehicleIdle(disconnect);
  }
}
