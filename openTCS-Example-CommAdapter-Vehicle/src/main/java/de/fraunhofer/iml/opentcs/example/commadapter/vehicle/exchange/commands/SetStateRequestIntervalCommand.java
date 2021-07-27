/**
 * Copyright (c) Fraunhofer IML
 */
package de.fraunhofer.iml.opentcs.example.commadapter.vehicle.exchange.commands;

import de.fraunhofer.iml.opentcs.example.commadapter.vehicle.ExampleCommAdapter;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;

/**
 * A command to set the adapter's state request interval.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class SetStateRequestIntervalCommand
    implements AdapterCommand {

  /**
   * The new interval.
   */
  private final int interval;

  /**
   * Creates a new instance.
   *
   * @param interval The new interval
   */
  public SetStateRequestIntervalCommand(int interval) {
    this.interval = interval;
  }

  @Override
  public void execute(VehicleCommAdapter adapter) {
    if (!(adapter instanceof ExampleCommAdapter)) {
      return;
    }

    ExampleCommAdapter exampleAdapter = (ExampleCommAdapter) adapter;
    exampleAdapter.getProcessModel().setStateRequestInterval(interval);
  }
}
