package nl.rug.jbi.jsm.bcel;

import org.apache.bcel.classfile.Field;

public class FieldData {
    private final Field field;

    public FieldData(final Field field) {
        this.field = field;
    }

    public String getName() {
        return this.field.getName();
    }

    public String getType() {
        return this.field.getType().toString();
    }

    public boolean isPublic() {
        return this.field.isPublic();
    }

    public boolean isProtected() {
        return this.field.isProtected();
    }

    public boolean isPrivate() {
        return this.field.isPrivate();
    }

    public boolean isStatic() {
        return this.field.isStatic();
    }
}
