/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package com.example.operationsdesk.panels.statistics;

import static com.example.operationsdesk.panels.statistics.I18nPlantOverviewPanelStatistics.BUNDLE_PATH;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

/**
 * A table model for vehicle statistics.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
class VehiclesTableModel
    extends AbstractTableModel {

  /**
   * This class's resources bundle.
   */
  private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_PATH);
  /**
   * The column names.
   */
  private static final String[] COLUMN_NAMES = new String[] {
    BUNDLE.getString("vehiclesTableModel.column_name.headerText"),
    BUNDLE.getString("vehiclesTabelModel.column_runtimeUsage.headerText"),
    BUNDLE.getString("vehiclesTabelModel.column_waitingTime.headerText"),
    BUNDLE.getString("vehiclesTabelModel.column_ordersProcessed.headerText"),
    BUNDLE.getString("vehiclesTabelModel.column_chargingTime.headerText")
  };
  /**
   * The column classes.
   */
  private static final Class<?>[] COLUMN_CLASSES = new Class<?>[] {
    String.class,
    Long.class,
    String.class,
    Integer.class,
    Long.class
  };
  /**
   * The actual content.
   */
  private final List<VehicleStats> vehicles = new ArrayList<>();

  /**
   * Creates a new instance.
   */
  VehiclesTableModel() {
  }

  /**
   * Adds statistics data at the end of the table.
   *
   * @param vehicle The vehicle statistics data to be added.
   */
  public void addData(VehicleStats vehicle) {
    int newIndex = vehicles.size();
    vehicles.add(vehicle);
    fireTableRowsInserted(newIndex, newIndex);
  }

  @Override
  public int getRowCount() {
    return vehicles.size();
  }

  @Override
  public int getColumnCount() {
    return COLUMN_NAMES.length;
  }

  @Override
  public String getColumnName(int columnIndex) {
    try {
      return COLUMN_NAMES[columnIndex];
    }
    catch (ArrayIndexOutOfBoundsException exc) {
      return "FEHLER";
    }
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return COLUMN_CLASSES[columnIndex];
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    VehicleStats vehicle = vehicles.get(rowIndex);

    switch (columnIndex) {
      case 0:
        return vehicle.getName();
      case 1:
        return vehicle.getTotalTimeProcessing() * 100 / vehicle.getTotalRuntime()
            + "%";
      case 2:
        return TimePeriodFormat.formatHumanReadable(
            vehicle.getTotalTimeWaiting());
      case 3:
        return vehicle.getTotalOrdersProcessed();
      case 4:
        return TimePeriodFormat.formatHumanReadable(
            vehicle.getTotalTimeCharging());
      default:
        throw new IllegalArgumentException("Invalid columnIndex: "
            + columnIndex);
    }
  }
}
