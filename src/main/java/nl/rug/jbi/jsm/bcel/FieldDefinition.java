package nl.rug.jbi.jsm.bcel;

import org.apache.bcel.classfile.Field;

/**
 * Represents a field member of the class inspected in the current scope.
 *
 * @author David van Leusen
 * @since 1.0
 */
public class FieldDefinition {
    private final Field field;

    public FieldDefinition(final Field field) {
        this.field = field;
    }

    /**
     * @return Name of this field.
     */
    public String getName() {
        return this.field.getName();
    }

    /**
     * @return String representing the type of this field.
     */
    public String getType() {
        return BCELTools.type2className(this.field.getType());
    }

    /**
     * @return Whether this field has the 'public' modifier.
     */
    public boolean isPublic() {
        return this.field.isPublic();
    }

    /**
     * @return Whether this field has the 'protected' modifier.
     */
    public boolean isProtected() {
        return this.field.isProtected();
    }

    /**
     * @return Whether this field has the 'private' modifier.
     */
    public boolean isPrivate() {
        return this.field.isPrivate();
    }

    /**
     * @return Whether this field has the 'static' modifier.
     */
    public boolean isStatic() {
        return this.field.isStatic();
    }
}
