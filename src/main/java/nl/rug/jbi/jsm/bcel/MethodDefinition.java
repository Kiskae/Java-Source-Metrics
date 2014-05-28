package nl.rug.jbi.jsm.bcel;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

import java.util.Arrays;
import java.util.List;

import static nl.rug.jbi.jsm.bcel.BCELTools.type2className;

/**
 * Represents a method of the class inspected in the current scope, will be emitted for each method found by
 * the {@link nl.rug.jbi.jsm.bcel.ClassVisitor}.
 *
 * @author David van Leusen
 * @since 1.0
 */
public class MethodDefinition {
    private final MethodGen mg;

    public MethodDefinition(MethodGen mg) {
        this.mg = mg;
    }

    /**
     * @return The name of the method, not guaranteed to be unique because of overloading.
     */
    public String getMethodName() {
        return mg.getName();
    }

    /**
     * @return A string representing the type of object this method returns.
     */
    public String getReturnType() {
        return type2className(mg.getReturnType());
    }

    /**
     * @return An array of strings representing the types of exceptions thrown by this method.
     */
    public String[] getExceptions() {
        return mg.getExceptions();
    }

    /**
     * @return A list of strings representing the types of the arguments of this method.
     */
    public List<String> getArgumentTypes() {
        return Lists.transform(Arrays.asList(this.mg.getArgumentTypes()), new Function<Type, String>() {
            @Override
            public String apply(final Type type) {
                return type2className(type);
            }
        });
    }

    /**
     * @return Whether this method has the 'public' modifier.
     */
    public boolean isPublic() {
        return mg.isPublic();
    }

    /**
     * @return Whether this method has the 'protected' modifier.
     */
    public boolean isProtected() {
        return mg.isProtected();
    }

    /**
     * @return Whether this method has the 'abstract' modifier.
     */
    public boolean isAbstract() {
        return mg.isAbstract();
    }

    /**
     * @return Whether this method has the 'private' modifier.
     */
    public boolean isPrivate() {
        return mg.isPrivate();
    }

    /**
     * @return Whether this method has the 'native' modifier.
     */
    public boolean isNative() {
        return mg.isNative();
    }

    /**
     * @return Whether this method has the 'static' modifier.
     */
    public boolean isStatic() {
        return mg.isStatic();
    }
}
