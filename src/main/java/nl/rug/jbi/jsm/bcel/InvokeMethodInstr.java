package nl.rug.jbi.jsm.bcel;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.Type;

import java.util.Arrays;
import java.util.List;

import static nl.rug.jbi.jsm.bcel.BCELTools.type2className;

public class InvokeMethodInstr {
    private final InvokeInstruction instruction;
    private final ConstantPoolGen cp;

    public InvokeMethodInstr(final InvokeInstruction instruction, final ConstantPoolGen cp) {
        this.instruction = instruction;
        this.cp = cp;
    }

    public List<String> getArgumentTypes() {
        return Lists.transform(Arrays.asList(this.instruction.getArgumentTypes(this.cp)), new Function<Type, String>() {
            @Override
            public String apply(final Type type) {
                return type2className(type);
            }
        });
    }

    public String getReturnType() {
        return type2className(this.instruction.getReturnType(this.cp));
    }

    public String getClassName() {
        return type2className(this.instruction.getReferenceType(this.cp));
    }

    public String getMethodName() {
        return this.instruction.getMethodName(this.cp);
    }
}
