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
 * A table model for point statistics.
 */
class PointsTableModel
    extends AbstractTableModel {

  /**
   * This class's resources bundle.
   */
  private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_PATH);
  /**
   * The column names.
   */
  private static final String[] COLUMN_NAMES = new String[] {
    BUNDLE.getString("pointsTabelModel.column_point.headerText"),
    BUNDLE.getString("pointsTabelModel.column_timeOccupied.headerText")
  };
  /**
   * The column classes.
   */
  private static final Class<?>[] COLUMN_CLASSES = new Class<?>[] {
    String.class,
    Long.class
  };
  /**
   * The actual content.
   */
  private final List<PointStats> points = new ArrayList<>();

  /**
   * Creates a new instance.
   */
  PointsTableModel() {
  }

  /**
   * Adds statistics data at the end of the table.
   *
   * @param point The point statistics data to be added.
   */
  public void addData(PointStats point) {
    int newIndex = points.size();
    points.add(point);
    fireTableRowsInserted(newIndex, newIndex);
  }

  @Override
  public int getRowCount() {
    return points.size();
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
    PointStats point = points.get(rowIndex);

    switch (columnIndex) {
      case 0:
        return point.getName();
      case 1:
        return TimePeriodFormat.formatHumanReadable(
            point.getTotalTimeOccupied());
      default:
        throw new IllegalArgumentException("Invalid columnIndex: "
            + columnIndex);
    }
  }
}
