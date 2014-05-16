package nl.rug.jbi.jsm.metrics.packagemetrics;

import java.util.Set;

public class Package {
    public Set<String> getUsedClasses() {
        return null;
    }

    public String getPackageName() {
        return "Santa";
    }

    //C:
    //Int(p)
    //InInt(p)
    //ClientsP(p)
    //OutInt(p)
    //ProvidersP(p)
    //ClientsP(c)
    //ProvidersP(c)
    //ClientsC(p)
    //ProvidersC(p)

    //D:
    //Ext(c1, c2)
    //Ext(p1, p2)
    //Ext(c)
    //Ext(p)

    //Uses(c1, c2)
    //Uses(p1, p2)
    //Uses(c)
    //Uses(p)
}
