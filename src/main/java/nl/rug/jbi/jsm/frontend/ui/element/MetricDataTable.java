package nl.rug.jbi.jsm.frontend.ui.element;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import nl.rug.jbi.jsm.core.calculator.MetricResult;

import javax.swing.table.AbstractTableModel;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

public class MetricDataTable extends AbstractTableModel {
    private final List<Class> metricClasses = Lists.newArrayList();
    private final List<String> identifierLookup = Lists.newArrayList();
    private final Table<String, Class, Object> resultTable = HashBasedTable.create();

    public MetricDataTable() {
    }

    @Override
    public int getRowCount() {
        return this.identifierLookup.size();
    }

    @Override
    public int getColumnCount() {
        return this.metricClasses.size() + 1;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 0 ? String.class : SortableResult.class;
    }

    @Override
    public String getColumnName(int column) {
        return column == 0 ? "" : this.metricClasses.get(column - 1).getSimpleName();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        final String identifier = this.identifierLookup.get(rowIndex);
        if (columnIndex == 0) {
            return identifier;
        } else {
            final Class metricClass = metricClasses.get(columnIndex - 1);
            return new SortableResult(this.resultTable.get(identifier, metricClass));
        }
    }

    public void processResult(final MetricResult result) {
        final String identifier = result.getIdentifier();
        final int index = this.identifierLookup.indexOf(identifier);
        final int columnIndex = this.metricClasses.indexOf(result.getMetricClass());

        checkState(columnIndex != -1, "Unknown metric class: %s", result);

        if (index == -1) {
            this.identifierLookup.add(identifier);

            this.resultTable.put(identifier, result.getMetricClass(), result.getValue());

            final int size = this.identifierLookup.size();
            this.fireTableRowsInserted(size - 1, size - 1);
        } else {
            this.resultTable.put(identifier, result.getMetricClass(), result.getValue());

            this.fireTableCellUpdated(index, columnIndex + 1);
        }
    }

    public void setMetricClasses(final List<Class> metricClasses) {
        this.metricClasses.clear();
        this.metricClasses.addAll(metricClasses);
        this.identifierLookup.clear();
        this.resultTable.clear();
        this.fireTableStructureChanged();
    }

    public Table<String, Class, Object> getResultsMap() {
        return Tables.unmodifiableTable(this.resultTable);
    }

    public void clear() {
        this.resultTable.clear();
        this.identifierLookup.clear();
        this.fireTableDataChanged();
    }
}
