/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package com.example.commadapter.vehicle;

import com.example.common.telegrams.RequestResponseMatcher;
import com.example.common.telegrams.StateRequesterTask;
import com.example.common.telegrams.TelegramSender;
import java.awt.event.ActionListener;
import org.opentcs.data.model.Vehicle;

/**
 * A factory for various instances specific to the comm adapter.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public interface ExampleAdapterComponentsFactory {

  /**
   * Creates a new ExampleCommAdapter for the given vehicle.
   *
   * @param vehicle The vehicle
   * @return A new ExampleCommAdapter for the given vehicle
   */
  ExampleCommAdapter createExampleCommAdapter(Vehicle vehicle);

  /**
   * Creates a new {@link RequestResponseMatcher}.
   *
   * @param telegramSender Sends telegrams/requests.
   * @return The created {@link RequestResponseMatcher}.
   */
  RequestResponseMatcher createRequestResponseMatcher(TelegramSender telegramSender);

  /**
   * Creates a new {@link StateRequesterTask}.
   *
   * @param stateRequestAction The actual action to be performed to enqueue requests.
   * @return The created {@link StateRequesterTask}.
   */
  StateRequesterTask createStateRequesterTask(ActionListener stateRequestAction);
}
