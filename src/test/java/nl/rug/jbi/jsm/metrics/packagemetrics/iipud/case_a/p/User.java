package nl.rug.jbi.jsm.metrics.packagemetrics.iipud.case_a.p;

import nl.rug.jbi.jsm.metrics.packagemetrics.iipud.case_a.p1.Target1;
import nl.rug.jbi.jsm.metrics.packagemetrics.iipud.case_a.p1.Target2;
import nl.rug.jbi.jsm.metrics.packagemetrics.iipud.case_a.p1.Target3;

/**
 * Created by David on 24-5-2014.
 */
public class User {

    public void execute() {
        Target1.use();
        Target2.use();
        Target3.use();
    }
}
