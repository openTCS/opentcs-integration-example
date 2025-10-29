/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package com.example.kernel.extensions.statistics;

import org.opentcs.customizations.kernel.KernelInjectionModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configures the statistics extension.
 */
public class StatisticsModule
    extends KernelInjectionModule {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(StatisticsModule.class);

  /**
   * Creates a new instance.
   */
  public StatisticsModule() {
  }

  @Override
  protected void configure() {
    StatisticsCollectorConfiguration configuration
        = getConfigBindingProvider().get(StatisticsCollectorConfiguration.PREFIX,
                                         StatisticsCollectorConfiguration.class);
    if (!configuration.enable()) {
      LOG.info("Statistics disabled by configuration.");
      return;
    }

    bind(StatisticsCollectorConfiguration.class)
        .toInstance(configuration);
    extensionsBinderOperating().addBinding()
        .to(StatisticsCollector.class);
  }
}
