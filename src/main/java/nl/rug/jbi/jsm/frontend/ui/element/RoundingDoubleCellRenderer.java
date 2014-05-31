package nl.rug.jbi.jsm.frontend.ui.element;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;

public class RoundingDoubleCellRenderer extends DefaultTableCellRenderer {
    private final NumberFormat nf;

    public RoundingDoubleCellRenderer(final int fractionDigits) {
        this.setHorizontalAlignment(RIGHT);
        this.nf = NumberFormat.getNumberInstance();
        this.nf.setMinimumFractionDigits(0);
        this.nf.setMaximumFractionDigits(fractionDigits);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
    ) {
        //Unpack SortableResult
        value = ((SortableResult) value).getValue();
        if (value != null && value instanceof Number) {
            value = this.nf.format(((Number) value).doubleValue());
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
