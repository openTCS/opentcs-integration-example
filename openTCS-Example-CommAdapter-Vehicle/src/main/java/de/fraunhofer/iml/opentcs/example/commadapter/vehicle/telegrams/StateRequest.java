/**
 * Copyright (c) Fraunhofer IML
 */
package de.fraunhofer.iml.opentcs.example.commadapter.vehicle.telegrams;

import static com.google.common.base.Ascii.ETX;
import static com.google.common.base.Ascii.STX;
import com.google.common.primitives.Ints;
import de.fraunhofer.iml.opentcs.example.common.telegrams.Request;
import static de.fraunhofer.iml.opentcs.example.common.telegrams.Telegram.getCheckSum;

/**
 * Represents a state request addressed to the vehicle.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class StateRequest
    extends Request {

  /**
   * The request type.
   */
  public static final byte TYPE = 1;
  /**
   * The expected length of a telegram of this type.
   */
  public static final int TELEGRAM_LENGTH = 7;
  /**
   * The size of the payload (the raw content, without STX, SIZE, CHECKSUM and ETX).
   */
  public static final int PAYLOAD_LENGTH = TELEGRAM_LENGTH - 4;
  /**
   * The position of the checksum byte.
   */
  public static final int CHECKSUM_POS = TELEGRAM_LENGTH - 2;

  /**
   * Creates a new instance.
   *
   * @param requestId The request's id.
   */
  public StateRequest(int requestId) {
    super(TELEGRAM_LENGTH);
    this.id = requestId;

    encodeTelegramContent();
  }

  @Override
  public void updateRequestContent(int requestId) {
    id = requestId;
    encodeTelegramContent();
  }

  @Override
  public String toString() {
    return "StateRequest{" + "requestId=" + id + '}';
  }

  private void encodeTelegramContent() {
    rawContent[0] = STX;
    rawContent[1] = PAYLOAD_LENGTH;

    rawContent[2] = TYPE;

    byte[] tmpWord = Ints.toByteArray(id);
    rawContent[3] = tmpWord[2];
    rawContent[4] = tmpWord[3];

    rawContent[CHECKSUM_POS] = getCheckSum(rawContent);
    rawContent[TELEGRAM_LENGTH - 1] = ETX;
  }
}
