/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package com.example.commadapter.vehicle;

import com.example.commadapter.vehicle.telegrams.OrderRequest;
import com.example.commadapter.vehicle.telegrams.OrderRequest.OrderAction;
import com.example.common.telegrams.BoundedCounter;
import static com.example.common.telegrams.BoundedCounter.UINT16_MAX_VALUE;
import com.example.common.telegrams.Telegram;
import org.opentcs.data.model.Point;
import org.opentcs.drivers.vehicle.MovementCommand;

/**
 * Maps {@link MovementCommand}s from openTCS to a telegram sent to the vehicle.
 *
 * @author Mats Wilhelm (Fraunhofer IML)
 */
public class OrderMapper {

  /**
   * Counts the order id's sent to the vehicle.
   */
  private final BoundedCounter orderIdCounter = new BoundedCounter(1, UINT16_MAX_VALUE);

  /**
   * Creates a new instance.
   */
  public OrderMapper() {
  }

  /**
   * Maps the given command to an order request that can be sent to the vehicle.
   *
   * @param command The command to be mapped.
   * @return The order request to be sent.
   * @throws IllegalArgumentException If the movement command could not be mapped properly.
   */
  public OrderRequest mapToOrder(MovementCommand command)
      throws IllegalArgumentException {
    return new OrderRequest(Telegram.ID_DEFAULT,
                            orderIdCounter.getAndIncrement(),
                            extractDestinationId(command.getStep().getDestinationPoint()),
                            OrderAction.stringToAction(command.getFinalOperation()));
  }

  private static int extractDestinationId(Point point)
      throws IllegalArgumentException {
    try {
      return Integer.parseInt(point.getName());
    }
    catch (NumberFormatException e) {
      throw new IllegalArgumentException("Cannot parse point name: " + point.getName(), e);
    }
  }
}
