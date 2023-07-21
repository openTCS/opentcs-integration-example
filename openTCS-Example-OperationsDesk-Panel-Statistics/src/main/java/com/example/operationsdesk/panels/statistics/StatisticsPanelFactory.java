/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package com.example.operationsdesk.panels.statistics;

import static com.example.operationsdesk.panels.statistics.I18nPlantOverviewPanelStatistics.BUNDLE_PATH;
import static java.util.Objects.requireNonNull;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Provider;
import org.opentcs.access.Kernel;
import org.opentcs.components.plantoverview.PluggablePanel;
import org.opentcs.components.plantoverview.PluggablePanelFactory;

/**
 * Creates statistics panels.
 */
public class StatisticsPanelFactory
    implements PluggablePanelFactory {

  /**
   * This class's bundle.
   */
  private final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PATH);
  /**
   * A provider for the actual panels.
   */
  private final Provider<StatisticsPanel> panelProvider;

  /**
   * Creates a new instance.
   *
   * @param panelProvider A provider for the actual panels.
   */
  @Inject
  public StatisticsPanelFactory(Provider<StatisticsPanel> panelProvider) {
    this.panelProvider = requireNonNull(panelProvider, "panelProvider");
  }

  @Override
  public String getPanelDescription() {
    return bundle.getString("statisticsPanelFactory.panelDescription");
  }

  @Override
  public PluggablePanel createPanel(Kernel.State state) {
    return panelProvider.get();
  }

  @Override
  public boolean providesPanel(Kernel.State state) {
    return true;
  }
}
