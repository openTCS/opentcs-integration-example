/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package com.example.commadapter.vehicle.exchange.commands;

import com.example.commadapter.vehicle.ExampleCommAdapter;
import com.example.common.telegrams.Request;
import static java.util.Objects.requireNonNull;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;

/**
 * A command for sending a telegram to the actual vehicle.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class SendRequestCommand
    implements AdapterCommand {

  /**
   * The request to send.
   */
  private final Request request;

  /**
   * Creates a new instance.
   *
   * @param request The request to send.
   */
  public SendRequestCommand(Request request) {
    this.request = requireNonNull(request, "request");
  }

  @Override
  public void execute(VehicleCommAdapter adapter) {
    if (!(adapter instanceof ExampleCommAdapter)) {
      return;
    }

    ExampleCommAdapter exampleAdapter = (ExampleCommAdapter) adapter;
    exampleAdapter.getRequestResponseMatcher().enqueueRequest(request);
  }
}
