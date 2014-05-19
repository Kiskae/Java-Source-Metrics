package nl.rug.jbi.jsm.bcel;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

import java.util.Arrays;
import java.util.List;

import static nl.rug.jbi.jsm.bcel.BCELTools.type2className;

public class MethodDefinition {
    private final MethodGen mg;

    public MethodDefinition(MethodGen mg) {
        this.mg = mg;
    }

    public String getSignature() {
        return mg.getSignature();
    }

    public String getMethodName() {
        return mg.getName();
    }

    public String getReturnType() {
        return mg.getReturnType().toString();
    }

    public String[] getExceptions() {
        return mg.getExceptions();
    }

    public List<String> getArgumentTypes() {
        return Lists.transform(Arrays.asList(this.mg.getArgumentTypes()), new Function<Type, String>() {
            @Override
            public String apply(final Type type) {
                return type2className(type);
            }
        });
    }

    public boolean isPublic() {
        return mg.isPublic();
    }

    public boolean isProtected() {
        return mg.isProtected();
    }

    public boolean isAbstract() {
        return mg.isAbstract();
    }

    public boolean isPrivate() {
        return mg.isPrivate();
    }
}
