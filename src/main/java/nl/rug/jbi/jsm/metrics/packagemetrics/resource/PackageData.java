package nl.rug.jbi.jsm.metrics.packagemetrics.resource;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

class PackageData {
    private final String packageName;
    private final String packageSource;
    private final Map<String, ClassData> memberData = Maps.newHashMap();

    private final Map<String, ClassData> usesClasses = Maps.newHashMap();
    private final Map<String, PackageData> usesPackages = Maps.newHashMap();

    private final Map<String, ClassData> extendsClasses = Maps.newHashMap();
    private final Map<String, PackageData> extendsPackages = Maps.newHashMap();

    private final Map<String, ClassData> usedByClasses = Maps.newHashMap();
    private final Map<String, PackageData> usedByPackages = Maps.newHashMap();

    private final Map<String, ClassData> extendedByClasses = Maps.newHashMap();
    private final Map<String, PackageData> extendedByPackages = Maps.newHashMap();

    public PackageData(final String packageName, final String packageSource) {
        this.packageName = packageName;
        this.packageSource = packageSource;
    }

    void registerMember(final ClassData classData) {
        Preconditions.checkNotNull(classData);
        Preconditions.checkArgument(this.packageName.equals(classData.getPackageName()));

        this.memberData.put(classData.getClassName(), classData);
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getPackageSource() {
        return this.packageSource;
    }

    public Map<String, ClassData> getMemberClasses() {
        return this.memberData;
    }

    public Map<String, ClassData> getUsesClasses() {
        return this.usesClasses;
    }

    public Map<String, PackageData> getUsesPackages() {
        return this.usesPackages;
    }

    public Map<String, ClassData> getExtendsClasses() {
        return this.extendsClasses;
    }

    public Map<String, PackageData> getExtendsPackages() {
        return this.extendsPackages;
    }

    public Map<String, ClassData> getUsedByClasses() {
        return this.usedByClasses;
    }

    public Map<String, PackageData> getUsedByPackages() {
        return this.usedByPackages;
    }

    public Map<String, ClassData> getExtendedByClasses() {
        return this.extendedByClasses;
    }

    public Map<String, PackageData> getExtendedByPackages() {
        return this.extendedByPackages;
    }

    public boolean isInThisPackage(final ClassData classData) {
        return this.packageName.equals(classData.getPackageName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PackageData)) return false;

        PackageData that = (PackageData) o;

        return packageName.equals(that.packageName);

    }

    @Override
    public int hashCode() {
        return packageName.hashCode();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("packageName", packageName)
                .add("packageSource", packageSource)
                .add("memberData", memberData.keySet())
                .add("usesClasses", usesClasses.keySet())
                .add("usesPackages", usesPackages.keySet())
                .add("extendsClasses", extendsClasses.keySet())
                .add("extendsPackages", extendsPackages.keySet())
                .add("usedByClasses", usedByClasses.keySet())
                .add("usedByPackages", usedByPackages.keySet())
                .add("extendedByClasses", extendedByClasses.keySet())
                .add("extendedByPackages", extendedByPackages.keySet())
                .toString();
    }

    void calculateMappings(final Map<String, PackageData> packageDataMap) {
        for (final ClassData member : this.memberData.values()) {
            registerPackageClass(
                    usesClasses, usesPackages,
                    member.getUsesMap().values(),
                    packageDataMap
            );
            registerPackageClass(
                    usedByClasses, usedByPackages,
                    member.getUsedByMap().values(),
                    packageDataMap
            );
            registerPackageClass(
                    extendsClasses, extendsPackages,
                    member.getExtendsMap().values(),
                    packageDataMap
            );
            registerPackageClass(
                    extendedByClasses, extendedByPackages,
                    member.getExtendedByMap().values(),
                    packageDataMap
            );

            //TODO: collect sets of classes which get used/extended (check size of sets, add if > 0)
        }
    }

    private void registerPackageClass(
            final Map<String, ClassData> classOut,
            final Map<String, PackageData> packageOut,
            final Collection<ClassData> classes,
            final Map<String, PackageData> packageDataMap
    ) {
        for (final ClassData clazz : classes) {
            if (!isInThisPackage(clazz)) {
                classOut.put(clazz.getClassName(), clazz);
                final String packageName = clazz.getPackageName();
                packageOut.put(packageName, packageDataMap.get(packageName));
            }
        }
    }
}
