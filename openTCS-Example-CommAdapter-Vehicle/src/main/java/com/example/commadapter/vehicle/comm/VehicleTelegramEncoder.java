/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package com.example.commadapter.vehicle.comm;

import com.example.commadapter.vehicle.telegrams.OrderRequest;
import com.example.commadapter.vehicle.telegrams.StateRequest;
import com.example.common.telegrams.Request;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encodes outgoing {@link StateRequest} instances.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class VehicleTelegramEncoder
    extends MessageToByteEncoder<Request> {

  /**
   * This class's Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(VehicleTelegramEncoder.class);

  @Override
  protected void encode(ChannelHandlerContext ctx, Request msg, ByteBuf out)
      throws Exception {
    LOG.debug("Encoding request of class {}", msg.getClass().getName());

    if (msg instanceof OrderRequest) {
      OrderRequest order = (OrderRequest) msg;
      LOG.debug("Encoding order telegram: {}", order.toString());
    }
    
    out.writeBytes(msg.getRawContent());
  }
}
