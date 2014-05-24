package nl.rug.jbi.jsm.metrics.packagemetrics.iipud.case_b.q;

import nl.rug.jbi.jsm.metrics.packagemetrics.iipud.case_b.q1.Target1;
import nl.rug.jbi.jsm.metrics.packagemetrics.iipud.case_b.q1.Target2;
import nl.rug.jbi.jsm.metrics.packagemetrics.iipud.case_b.q2.Target3;
import nl.rug.jbi.jsm.metrics.packagemetrics.iipud.case_b.q3.Target4;

/**
 * Created by David on 24-5-2014.
 */
public class User {

    public void execute() {
        Target1.use();
        Target2.use();
        Target3.use();
        Target4.use();
    }
}
