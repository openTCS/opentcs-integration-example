/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package com.example.operationsdesk.panels.statistics;

import org.opentcs.configuration.ConfigurationEntry;
import org.opentcs.configuration.ConfigurationPrefix;

/**
 * Provides methods to configure the continuous load panel.
 */
@ConfigurationPrefix(StatisticsPanelConfiguration.PREFIX)
public interface StatisticsPanelConfiguration {

  /**
   * This configuration's prefix.
   */
  String PREFIX = "example.statisticspanel";

  @ConfigurationEntry(
      type = "Boolean",
      description = "Whether to enable to register/enable the statistics panel.",
      orderKey = "0_enable")
  boolean enable();
}
