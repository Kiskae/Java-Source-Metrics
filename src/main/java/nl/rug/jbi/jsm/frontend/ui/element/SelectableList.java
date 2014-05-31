package nl.rug.jbi.jsm.frontend.ui.element;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class SelectableList extends JList<String> {
    private final DefaultListModel<String> items;

    public SelectableList() {
        this(new DefaultListModel<String>());
    }

    private SelectableList(final DefaultListModel<String> items) {
        super(items);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setLayoutOrientation(JList.VERTICAL);
        this.setVisibleRowCount(-1);

        this.items = items;
    }

    public List<String> getOptions() {
        return Collections.list(this.items.elements());
    }

    public void addOption(final String item) {
        if (!this.items.contains(item)) {
            items.addElement(item);
        }
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public int removeSelection() {
        final int selectedIndex = this.getSelectedIndex();

        if (selectedIndex != -1)
            this.items.remove(selectedIndex);

        return this.items.size();
    }
}
