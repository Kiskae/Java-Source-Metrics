package nl.rug.jbi.jsm.bcel;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.bcel.classfile.JavaClass;

import java.util.Arrays;
import java.util.List;

public class JavaClassDefinition {

    private final JavaClass jc;

    public JavaClassDefinition(final JavaClass jc) {
        this.jc = jc;
    }

    public String getPackageName() {
        return this.jc.getPackageName();
    }

    public String getClassName() {
        return this.jc.getClassName();
    }

    public String getSuperClass() {
        return this.jc.getSuperclassName();
    }

    public List<String> getSuperClasses() {
        try {
            return Lists.transform(Arrays.asList(this.jc.getSuperClasses()), new Function<org.apache.bcel.classfile.JavaClass, String>() {
                @Override
                public String apply(org.apache.bcel.classfile.JavaClass javaClass) {
                    return javaClass.getClassName();
                }
            });
        } catch (ClassNotFoundException e) {
            throw new MissingDataException("JavaClass#getSuperClasses()", e);
        }
    }

    public List<String> getInterfaces() {
        return Arrays.asList(this.jc.getInterfaceNames());
    }
}
