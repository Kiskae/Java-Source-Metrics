package nl.rug.jbi.jsm.bcel;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.Type;

import java.util.Arrays;
import java.util.List;

import static nl.rug.jbi.jsm.bcel.BCELTools.type2className;

/**
 * Represents the invocation of a method.
 *
 * @author David van Leusen
 * @since 2014-05-28
 */
public class InvokeMethodInstr {
    private final InvokeInstruction instruction;
    private final ConstantPoolGen cp;

    public InvokeMethodInstr(final InvokeInstruction instruction, final ConstantPoolGen cp) {
        this.instruction = instruction;
        this.cp = cp;
    }

    /**
     * Beware that this returns the base types of the arguments that the method expects, not the types of the objects
     * put into the invocation.
     * Notice: use {@link #getExactArgumentTypes()} if difference between polymorphic methods is important.
     *
     * @return List of Strings representing the types of the arguments of the invoked method.
     */
    public List<String> getArgumentTypes() {
        return Lists.transform(Arrays.asList(this.instruction.getArgumentTypes(this.cp)), new Function<Type, String>() {
            @Override
            public String apply(final Type type) {
                return type2className(type);
            }
        });
    }

    /**
     * Use {@link #getArgumentTypes()} if only the actual base types are important.
     *
     * @return A list of strings representing the types of the arguments EXACTLY as BCEL reports them.
     */
    public List<String> getExactArgumentTypes() {
        return Lists.transform(Arrays.asList(this.instruction.getArgumentTypes(this.cp)), new Function<Type, String>() {
            @Override
            public String apply(final Type type) {
                return type.toString();
            }
        });
    }

    /**
     * @return String representing the type of the object this method returns.
     */
    public String getReturnType() {
        return type2className(this.instruction.getReturnType(this.cp));
    }

    /**
     * @return String representing the class the invoked method belongs to.
     */
    public String getClassName() {
        return type2className(this.instruction.getReferenceType(this.cp));
    }

    /**
     * @return A string representing the EXACT type of the class as BCEL reports it.
     */
    public String getExactClassName() {
        return this.instruction.getReferenceType(this.cp).getSignature();
    }

    /**
     * @return Name of the invoked method.
     */
    public String getMethodName() {
        return this.instruction.getMethodName(this.cp);
    }
}
