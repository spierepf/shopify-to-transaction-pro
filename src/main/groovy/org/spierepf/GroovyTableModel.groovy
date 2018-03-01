package org.spierepf

import javax.swing.table.*

class GroovyTableModel extends AbstractTableModel {
    def source
    def columnNames = []

    public GroovyTableModel(def source) {
        this.source = source
        source.forEach { columnNames.addAll(it.keySet()) }
    }

    public int getRowCount() {
        return source.size()
    }

    public int getColumnCount() {
        return columnNames.size()
    }

    public Object getValueAt(int row, int column) {
        return source[row][columnNames[column]]
    }

    public String getColumnName(int column) {
        return columnNames[column]
    }
}