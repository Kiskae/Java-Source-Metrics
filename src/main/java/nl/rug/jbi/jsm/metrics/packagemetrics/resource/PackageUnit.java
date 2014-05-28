package nl.rug.jbi.jsm.metrics.packagemetrics.resource;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * PackageUnit presents all the relationship data between the various inspected packages as specified in the paper
 * "Modularization Metrics: Assessing Package Organization in Legacy Large Object-Oriented Software"
 *
 * @author David van Leusen
 * @see nl.rug.jbi.jsm.metrics.packagemetrics.resource.PackageProducer
 * @since 2014-05-28
 */
public class PackageUnit {
    private final Map<String, PackageData> packagedata;
    private final PackageData data;

    PackageUnit(
            final Map<String, PackageData> packagedata,
            final PackageData data
    ) {
        this.packagedata = Collections.unmodifiableMap(packagedata);
        this.data = data;
    }

    /**
     * @return The uniquely identifying name of this package.
     */
    public String getName() {
        return this.data.getPackageName();
    }

    @Deprecated
    public int getPackageCount() {
        return this.packagedata.size();
    }

    /**
     * Retrieve a specific PackageUnit by identifier, it can return NULL if that package isn't within the scope of this
     * execution.
     *
     * @param packageIdentifier Identifier of the package to retrieve
     * @return The PackageUnit if it exists, otherwise NULL.
     */
    public PackageUnit getPackageByName(final String packageIdentifier) {
        return new PackageUnit(packagedata, this.packagedata.get(packageIdentifier));
    }

    /**
     * @return The collection that contains this package, for ease of representation it picks the collection that the
     * first class added to the internal data set belongs to.
     */
    public String getSourceIdentifier() {
        return this.data.getPackageSource();
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
        return Collections.unmodifiableSet(this.data.getOutClasses().keySet());
    }

    /**
     * InInt(package) in C
     *
     * @return Set of all classes in this package which are used by other packages.
     */
    public Set<String> InInt() {
        return Collections.unmodifiableSet(this.data.getInClasses().keySet());
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
        final ClassData cData = this.data.getMemberClasses().get(className);
        return FluentIterable.from(Iterables.concat(cData.getExtendedByMap().values(), cData.getUsedByMap().values()))
                .transform(new Function<ClassData, String>() {
                    @Override
                    public String apply(ClassData classData) {
                        return classData.getPackageName();
                    }
                }).toSet();
    }

    /**
     * ProvidersP(class) in P
     *
     * @param className c
     * @return Set of all packages that get used by c
     */
    public Set<String> ProvidersP(final String className) {
        final ClassData cData = this.data.getMemberClasses().get(className);
        return FluentIterable.from(Iterables.concat(cData.getExtendsMap().values(), cData.getUsesMap().values()))
                .transform(new Function<ClassData, String>() {
                    @Override
                    public String apply(ClassData classData) {
                        return classData.getPackageName();
                    }
                }).toSet();
    }

    /**
     * ClientsC(package) in C
     *
     * @return Set of all classes that use classes in this package.
     */
    public Set<String> ClientsC() {
        return Collections.unmodifiableSet(
                Sets.union(this.data.getExtendedByClasses().keySet(), this.data.getUsedByClasses().keySet())
        );
    }

    /**
     * ProvidersC(package) in C
     *
     * @return Set of all classes that get used by classes in this package.
     */
    public Set<String> ProvidersC() {
        return Collections.unmodifiableSet(
                Sets.union(this.data.getExtendsClasses().keySet(), this.data.getUsesClasses().keySet())
        );
    }

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

    /**
     * UsesSum(package) in C
     *
     * @return All classes used by members of this package, including internal Uses.
     */
    public Set<String> UsesSum() {
        return FluentIterable.from(this.data.getMemberClasses().values())
                .transformAndConcat(new Function<ClassData, Iterable<String>>() {
                    @Override
                    public Iterable<String> apply(ClassData member) {
                        return member.getUsesMap().keySet();
                    }
                })
                .toSet();
    }

    /**
     * ExtSum(package) in C
     *
     * @return All classes extended by members of this package, including internal Extends.
     */
    public Set<String> ExtSum() {
        return FluentIterable.from(this.data.getMemberClasses().values())
                .transformAndConcat(new Function<ClassData, Iterable<String>>() {
                    @Override
                    public Iterable<String> apply(ClassData member) {
                        return member.getExtendsMap().keySet();
                    }
                })
                .toSet();
    }
}
