package nl.rug.jbi.jsm.frontend.ui.element;

public class SortableResult implements Comparable<SortableResult> {
    private final Object obj;

    public SortableResult(final Object obj) {
        this.obj = obj;
    }

    public Object getValue() {
        return this.obj;
    }

    @Override
    public int compareTo(SortableResult o) {
        if (this.obj == null || o.obj == null) {
            return this.obj != null ? 1 : -1;
        } else if (this.obj instanceof Number && o.obj instanceof Number) {
            return Double.compare(((Number) this.obj).doubleValue(), ((Number) o.obj).doubleValue());
        } else {
            return this.obj.toString().compareTo(o.obj.toString());
        }
    }
}
