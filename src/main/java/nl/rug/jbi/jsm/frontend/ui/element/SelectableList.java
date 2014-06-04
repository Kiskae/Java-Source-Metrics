package nl.rug.jbi.jsm.frontend.ui.element;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class SelectableList extends JList {
    private final DefaultListModel items;

    public SelectableList() {
        this(new DefaultListModel());
    }

    private SelectableList(final DefaultListModel items) {
        super(items);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setLayoutOrientation(JList.VERTICAL);
        this.setVisibleRowCount(-1);

        this.items = items;
    }

    public List<String> getOptions() {
        return FluentIterable.from(Collections.list(this.items.elements()))
                .transform(new Function<Object, String>() {
                    @Override
                    public String apply(Object o) {
                        return o != null ? o.toString() : null;
                    }
                })
                .toList();
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
