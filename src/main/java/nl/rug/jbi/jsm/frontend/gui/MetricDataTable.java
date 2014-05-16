package nl.rug.jbi.jsm.frontend.gui;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.util.CSVExporter;

import javax.swing.table.AbstractTableModel;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MetricDataTable extends AbstractTableModel {
    private final String identifierName;
    private final List<Class> metricClasses = Lists.newLinkedList();
    private final List<String> identifierLookup = Lists.newArrayList();
    private final Map<String, ResultSet> results = Maps.newHashMap();

    public MetricDataTable(final String identifierName) {
        this.identifierName = Preconditions.checkNotNull(identifierName);
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
    public String getColumnName(int column) {
        return column == 0 ? "" : this.metricClasses.get(column - 1).getSimpleName();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        final String className = this.identifierLookup.get(rowIndex);
        return this.results.get(className).getResult(columnIndex);
    }

    public void processResult(final MetricResult result) {
        final String indentifier = result.getIdentifier();
        final int index = this.identifierLookup.indexOf(indentifier);
        final int columnIndex = this.metricClasses.indexOf(result.getMetricClass());
        Preconditions.checkState(columnIndex != -1, "Unknown metric class: %s", result);
        if (index == -1) {
            this.identifierLookup.add(indentifier);

            final ResultSet results = new ResultSet(indentifier, this.metricClasses.size());
            results.setResult(columnIndex, result.getValue());
            this.results.put(indentifier, results);

            final int size = this.identifierLookup.size();
            this.fireTableRowsInserted(size - 1, size - 1);
        } else {
            this.results.get(indentifier).setResult(columnIndex, result.getValue());
            this.fireTableCellUpdated(index, columnIndex + 1);
        }
    }

    public void setMetricClasses(final List<Class> metricClasses) {
        this.metricClasses.clear();
        this.metricClasses.addAll(metricClasses);
        this.identifierLookup.clear();
        this.results.clear();
        this.fireTableStructureChanged();
    }

    public void export(final CSVExporter export) {
        final List<String> headers = Lists.newLinkedList();
        headers.add(this.identifierName);
        headers.addAll(Collections2.transform(this.metricClasses, new Function<Class, String>() {
            @Override
            public String apply(Class aClass) {
                return aClass.getSimpleName();
            }
        }));
        export.writeDataRow(headers);

        for (final Map.Entry<String, ResultSet> entry : this.results.entrySet()) {
            final List<Object> row = Lists.newLinkedList();
            row.add(entry.getKey());
            row.addAll(Arrays.asList(entry.getValue().getObjects()));
            export.writeDataRow(row);
        }
    }

    private static class ResultSet {
        private final Object[] results;
        private final String identifier;

        public ResultSet(final String identifier, final int setSize) {
            this.identifier = identifier;
            this.results = new Object[setSize];
        }

        public Object[] getObjects() {
            return this.results;
        }

        public void setResult(final int index, final Object result) {
            this.results[index] = result;
        }

        public Object getResult(final int index) {
            return index == 0 ? this.identifier : this.results[index - 1];
        }
    }
}
