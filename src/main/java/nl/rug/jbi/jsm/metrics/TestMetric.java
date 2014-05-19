package nl.rug.jbi.jsm.metrics;

import nl.rug.jbi.jsm.bcel.FieldAccessInstr;
import nl.rug.jbi.jsm.bcel.InvokeMethodInstr;
import nl.rug.jbi.jsm.bcel.LocalVariableDefinition;
import nl.rug.jbi.jsm.bcel.TypeUseInstruction;
import nl.rug.jbi.jsm.core.calculator.IsolatedMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.Subscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestMetric extends IsolatedMetric {
    private final static Logger logger = LogManager.getLogger(TestMetric.class);

    public TestMetric() {
        super(MetricScope.CLASS);
    }

    @Subscribe
    public void onTypeUse(final MetricState state, final TypeUseInstruction instruction) {
        logger.debug("Ref type '{}' used.", instruction.getTypeName());
    }

    @Subscribe
    public void onFieldAccess(final MetricState state, final FieldAccessInstr instruction) {
        logger.debug(
                "Accessing '{}.{}' ({})",
                instruction.getClassName(),
                instruction.getFieldName(),
                instruction.getFieldType()
        );
    }

    @Subscribe
    public void onMethodInvoke(final MetricState state, final InvokeMethodInstr instruction) {
        logger.debug(
                "Accessing '{}.{}({})' ({})",
                instruction.getClassName(),
                instruction.getMethodName(),
                instruction.getArgumentTypes(),
                instruction.getReturnType()
        );
    }

    @Subscribe
    public void onLocalVar(final MetricState state, final LocalVariableDefinition lVar) {
        logger.debug("Local var '{}' ({})", lVar.getName(), lVar.getType());
    }

    @Override
    public MetricResult getResult(final String identifier, MetricState state) {
        return new MetricResult(identifier, this.getClass(), this.getScope()) {
            @Override
            public Object getValue() {
                return "PARSERD";
            }
        };
    }
}
