package nl.rug.jbi.jsm.metrics.packagemetrics.resource;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Map;

class ClassData {
    private final String className;
    private final String packageName;
    private final String classSource;
    private final Map<String, ClassData> usesMap = Maps.newHashMap();
    private final Map<String, ClassData> extendsMap = Maps.newHashMap();
    //Reverse mapping of usesMap/extendsMap
    private final Map<String, ClassData> usedByMap = Maps.newHashMap();
    private final Map<String, ClassData> extendedByMap = Maps.newHashMap();

    public ClassData(
            final String className,
            final String packageName,
            final String classSource
    ) {
        this.className = className;
        this.packageName = packageName;
        this.classSource = classSource;
    }

    public String getClassName() {
        return this.className;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getClassSource() {
        return this.classSource;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("className", className)
                .add("packageName", packageName)
                .add("classSource", classSource)
                .add("usesClasses", usesMap.keySet())
                .add("extendsClasses", extendsMap.keySet())
                .add("usedByClasses", usedByMap.keySet())
                .add("extendedByClasses", extendedByMap.keySet())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassData)) return false;

        ClassData classData = (ClassData) o;

        return className.equals(classData.className);

    }

    @Override
    public int hashCode() {
        return className.hashCode();
    }

    public Map<String, ClassData> getUsesMap() {
        return Collections.unmodifiableMap(this.usesMap);
    }

    public Map<String, ClassData> getExtendsMap() {
        return Collections.unmodifiableMap(this.extendsMap);
    }

    public Map<String, ClassData> getExtendedByMap() {
        return Collections.unmodifiableMap(this.extendedByMap);
    }

    public Map<String, ClassData> getUsedByMap() {
        return Collections.unmodifiableMap(this.usedByMap);
    }

    private void registerUse(final ClassData cData) {
        this.usedByMap.put(cData.getClassName(), cData);
    }

    private void registerExtend(final ClassData cData) {
        this.extendedByMap.put(cData.getClassName(), cData);
    }

    void setRelationMaps(final Map<String, ClassData> usesMap, final Map<String, ClassData> extendsMap) {
        this.usesMap.putAll(usesMap);
        this.extendsMap.putAll(extendsMap);

        for (final ClassData usedClass : usesMap.values()) {
            usedClass.registerUse(this);
        }

        for (final ClassData extendedClass : extendsMap.values()) {
            extendedClass.registerExtend(this);
        }
    }
}
