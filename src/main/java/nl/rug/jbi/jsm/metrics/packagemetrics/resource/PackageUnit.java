package nl.rug.jbi.jsm.metrics.packagemetrics.resource;

import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class PackageUnit {
    private final Map<String, PackageData> packagedata;
    private final Map<String, ClassData> classData;
    private final PackageData data;

    PackageUnit(
            final Map<String, PackageData> packagedata,
            final Map<String, ClassData> classData,
            final PackageData data
    ) {
        this.packagedata = Collections.unmodifiableMap(packagedata);
        this.classData = Collections.unmodifiableMap(classData);
        this.data = data;
    }

    public String getName() {
        return this.data.getPackageName();
    }

    public int getPackageCount() {
        return this.packagedata.size();
    }

    public int getClassCount() {
        return this.classData.size();
    }

    public Set<String> getAllPackageNames() {
        return Collections.unmodifiableSet(this.packagedata.keySet());
    }

    public Set<String> getAllClassNames() {
        return Collections.unmodifiableSet(this.classData.keySet());
    }

    public PackageUnit getPackageByName(final String packageName) {
        return new PackageUnit(packagedata, classData, this.packagedata.get(packageName));
    }

    /**
     * Int(p) in C
     *
     * @return Set of all classes in this packages communicates with other packages with.
     */
    public Set<String> Int() {
        return Sets.union(InInt(), OutInt());
    }

    /**
     * OutInt(package) in C
     *
     * @return Set of all classes in this package which uses classes in other packages.
     */
    public Set<String> OutInt() {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    /**
     * InInt(package) in C
     *
     * @return Set of all classes in this package which are used by other packages.
     */
    public Set<String> InInt() {
        //TODO: requires all classes TARGETTTED
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    /**
     * ClientsP(package) in P
     *
     * @return All packages that depend on this package in some way.
     */
    public Set<String> ClientsP() {
        return Collections.unmodifiableSet(
                Sets.union(this.data.getExtendedByPackages().keySet(), this.data.getUsedByPackages().keySet())
        );
    }

    /**
     * ProvidersP(package) in P
     *
     * @return All packages that this package depends on.
     */
    public Set<String> ProvidersP() {
        return Collections.unmodifiableSet(
                Sets.union(this.data.getExtendsPackages().keySet(), this.data.getUsesPackages().keySet())
        );
    }

    /**
     * ClientsP(class) in P
     *
     * @param className c
     * @return Set of all packages that use c
     */
    public Set<String> ClientsP(final String className) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    /**
     * ProvidersP(class) in P
     *
     * @param className c
     * @return Set of all packages that get used by c
     */
    public Set<String> ProvidersP(final String className) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    /**
     * ClientsC(package) in C
     *
     * @return Set of all classes that use classes in this package.
     */
    public Set<String> ClientsC() {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    /**
     * ProvidersC(package) in C
     *
     * @return Set of all classes that get used by classes in this package.
     */
    public Set<String> ProvidersC() {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    //D:
    //Ext(c1, c2)
    //Ext(p1, p2)

    //Uses(c1, c2)
    //Uses(p1, p2)

    /**
     * Uses(package) in P
     *
     * @return Set of all packages used by this package.
     */
    public Set<String> Uses() {
        return Collections.unmodifiableSet(this.data.getUsesPackages().keySet());
    }

    /**
     * UsesC(package) in C
     *
     * @return Set of all classes used by this package.
     */
    public Set<String> UsesC() {
        return Collections.unmodifiableSet(this.data.getUsesClasses().keySet());
    }

    /**
     * Ext(package) in P
     *
     * @return Set of all packages extended by this package.
     */
    public Set<String> Ext() {
        return Collections.unmodifiableSet(this.data.getExtendsPackages().keySet());
    }

    /**
     * ExtC(package) in C
     *
     * @return Set of all classes extended by this package.
     */
    public Set<String> ExtC() {
        return Collections.unmodifiableSet(this.data.getExtendsClasses().keySet());
    }
}
