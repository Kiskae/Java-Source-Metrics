package nl.rug.jbi.jsm.metrics.ckjm;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;
import nl.rug.jbi.jsm.bcel.*;
import nl.rug.jbi.jsm.core.calculator.IsolatedMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.Subscribe;

import java.util.Set;

public class CBO extends IsolatedMetric {
    private final static Supplier<Set<String>> EMPTY_SET = new Supplier<Set<String>>() {
        @Override
        public Set<String> get() {
            return Sets.newHashSet();
        }
    };

    public CBO() {
        super(MetricScope.CLASS);
    }

    private static void registerCoupling(final MetricState state, final String className) {
        if (!(isJdkClass(className) || state.getIdentifier().equals(className))) {
            final Set<String> coupledClasses = state.getValueOrCreate("coupledClasses", EMPTY_SET);
            coupledClasses.add(className);
        }
    }

    private static boolean isJdkClass(final String className) {
        //Definition taken from CKJM
        return (className.startsWith("java.") ||
                className.startsWith("javax.") ||
                className.startsWith("org.omg.") ||
                className.startsWith("org.w3c.dom.") ||
                className.startsWith("org.xml.sax."));
    }

    @Subscribe
    public void onClass(final MetricState state, final JavaClassDefinition clazz) {
        //Superclass
        registerCoupling(state, clazz.getSuperClass());

        //Direct interface implementations
        for (final String className : clazz.getInterfaces()) {
            registerCoupling(state, className);
        }
    }

    @Subscribe
    public void onField(final MetricState state, final FieldDefinition field) {
        //Object field
        registerCoupling(state, field.getType());
    }

    @Subscribe
    public void onMethod(final MetricState state, final MethodDefinition method) {
        //Return type
        registerCoupling(state, method.getReturnType());

        //Arguments
        for (final String className : method.getArgumentTypes()) {
            registerCoupling(state, className);
        }

        //Exceptions
        for (final String className : method.getExceptions()) {
            registerCoupling(state, className);
        }
    }

    @Subscribe
    public void onLocalVariable(final MetricState state, final LocalVariableDefinition lVar) {
        //Variable in method scope
        registerCoupling(state, lVar.getType());
    }

    @Subscribe
    public void onFieldAccess(final MetricState state, final FieldAccessInstr instr) {
        //<Class Type>.<Field Type>
        registerCoupling(state, instr.getFieldType());

        //CKJM missed this one
        registerCoupling(state, instr.getClassName());
    }

    @Subscribe
    public void onTypeUseInstruction(final MetricState state, final TypeUseInstruction instr) {
        //Casts etc
        registerCoupling(state, instr.getTypeName());
    }

    @Subscribe
    public void onMethodInvoke(final MetricState state, final InvokeMethodInstr instr) {
        //Method return type
        registerCoupling(state, instr.getReturnType());

        //Call arguments
        for (final String className : instr.getArgumentTypes()) {
            registerCoupling(state, className);
        }

        //CKJM missed this one
        registerCoupling(state, instr.getClassName());
    }

    @Subscribe
    public void onExceptionHandler(final MetricState state, final ExceptionHandlerDefinition ehDef) {
        //Exception caught in catch block
        final String catchType = ehDef.getCatchType();
        if (catchType != null)
            registerCoupling(state, catchType);
    }

    @Override
    public MetricResult getResult(String identifier, MetricState state) {
        return new MetricResult(identifier, this, state.getValueOrCreate("coupledClasses", EMPTY_SET).size());
    }
}
