package nl.rug.jbi.jsm.bcel;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.bcel.classfile.JavaClass;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a declared class/interface, will always be the first object emitted for a new class.
 *
 * @author David van Leusen
 * @since 1.0
 */
public class JavaClassDefinition {

    private final JavaClass jc;

    public JavaClassDefinition(final JavaClass jc) {
        this.jc = jc;
    }

    /**
     * It is important that this method is used instead of extracting the package identifier from the class name.
     * This is because nested classes might exhibit some weird properties when it comes to the class name.
     *
     * @return Identifier of the package that this class belongs to.
     */
    public String getPackageName() {
        return this.jc.getPackageName();
    }

    /**
     * @return Name of this class.
     */
    public String getClassName() {
        return this.jc.getClassName();
    }

    /**
     * Because this is Java, each class has a superclass. The only class without a superclass is {@link java.lang.Object}
     *
     * @return Name of the direct superclass of this class.
     */
    public String getSuperClass() {
        return this.jc.getSuperclassName();
    }

    /**
     * @return Transitive list of all results from {@link #getSuperClass()}.
     * @throws nl.rug.jbi.jsm.bcel.MissingDataException
     */
    public List<String> getSuperClasses() {
        try {
            return Lists.transform(Arrays.asList(this.jc.getSuperClasses()), new Function<JavaClass, String>() {
                @Override
                public String apply(JavaClass javaClass) {
                    return javaClass.getClassName();
                }
            });
        } catch (ClassNotFoundException e) {
            throw new MissingDataException("Missing Super-Class in call to JavaClass.getSuperClasses()", e);
        }
    }

    /**
     * @return List of interfaces directly implemented by this class.
     */
    public List<String> getInterfaces() {
        return Arrays.asList(this.jc.getInterfaceNames());
    }

    /**
     * @return Whether this class has the 'public' modifier.
     */
    public boolean isPublic() {
        return this.jc.isPublic();
    }

    /**
     * @return Whether this class has the 'protected' modifier.
     */
    public boolean isProtected() {
        return this.jc.isProtected();
    }

    /**
     * @return Whether this class has the 'abstract' modifier.
     */
    public boolean isAbstract() {
        return this.jc.isAbstract();
    }

    /**
     * @return Whether this class is an interface.
     */
    public boolean isInterface() {
        return this.jc.isInterface();
    }

    /**
     * @return Whether this class is an enum. (Extends {@link java.lang.Enum})
     */
    public boolean isEnum() {
        return this.jc.isEnum();
    }
}
