/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package com.example.common.telegrams;

/**
 * A request represents a telegram sent from the control system to vehicle control and expects
 * a response with the same id to match.
 *
 * @author Mats Wilhelm (Fraunhofer IML)
 */
public abstract class Request
    extends Telegram {

  /**
   * Creates a new instance.
   *
   * @param telegramLength The request's length.
   */
  public Request(int telegramLength) {
    super(telegramLength);
  }

  /**
   * Updates the content of the request to include the given id.
   *
   * @param telegramId The request's new id.
   */
  public abstract void updateRequestContent(int telegramId);
}
