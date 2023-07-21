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
 * A table model for order statistics.
 */
class OrdersTableModel
    extends AbstractTableModel {

  /**
   * This class's resources bundle.
   */
  private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_PATH);
  /**
   * The column names.
   */
  private static final String[] COLUMN_NAMES = new String[] {
    BUNDLE.getString("ordersTabelModel.column_name.headerText"),
    BUNDLE.getString("ordersTabelModel.column_timeToAssignment.headerText"),
    BUNDLE.getString("ordersTabelModel.column_processingTime.headerText"),
    BUNDLE.getString("ordersTabelModel.column_successful.headerText"),
    BUNDLE.getString("ordersTabelModel.column_deadlineCrossed.headerText")
  };
  /**
   * The column classes.
   */
  private static final Class<?>[] COLUMN_CLASSES = new Class<?>[] {
    String.class,
    Long.class,
    Long.class,
    Boolean.class,
    Boolean.class
  };
  /**
   * The actual content.
   */
  private final List<OrderStats> orders = new ArrayList<>();

  /**
   * Creates a new instance.
   */
  OrdersTableModel() {
  }

  /**
   * Adds statistics data at the end of the table.
   *
   * @param order The order statistics data to be added.
   */
  public void addData(OrderStats order) {
    int newIndex = orders.size();
    orders.add(order);
    fireTableRowsInserted(newIndex, newIndex);
  }

  @Override
  public int getRowCount() {
    return orders.size();
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
      return "ERROR";
    }
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return COLUMN_CLASSES[columnIndex];
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    OrderStats order = orders.get(rowIndex);

    switch (columnIndex) {
      case 0:
        return order.getName();
      case 1:
        return TimePeriodFormat.formatHumanReadable(
            order.getAssignmentTime() - order.getActivationTime());
      case 2:
        return TimePeriodFormat.formatHumanReadable(
            order.getFinishedTime() - order.getAssignmentTime());
      case 3:
        return order.isFinishedSuccessfully();
      case 4:
        return order.hasCrossedDeadline();
      default:
        throw new IllegalArgumentException("Invalid columnIndex: "
            + columnIndex);
    }
  }
}
