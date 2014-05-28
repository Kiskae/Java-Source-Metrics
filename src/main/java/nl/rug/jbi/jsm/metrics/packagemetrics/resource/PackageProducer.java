package nl.rug.jbi.jsm.metrics.packagemetrics.resource;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import nl.rug.jbi.jsm.bcel.FieldAccessInstr;
import nl.rug.jbi.jsm.bcel.InvokeMethodInstr;
import nl.rug.jbi.jsm.bcel.JavaClassDefinition;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.calculator.ProducerMetric;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.core.event.UsingProducer;
import nl.rug.jbi.jsm.metrics.ClassSource;
import nl.rug.jbi.jsm.metrics.ClassSourceProducer;
import nl.rug.jbi.jsm.util.DefaultValues;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Producer object for the packagemetrics package.
 *
 * @author David van Leusen
 * @see nl.rug.jbi.jsm.metrics.packagemetrics.resource.PackageUnit
 * @since 1.0
 */
public class PackageProducer extends ProducerMetric {
    private final static Logger logger = LogManager.getLogger(PackageProducer.class);

    public PackageProducer() {
        //    Data Scope         Produce Scope
        super(MetricScope.CLASS, MetricScope.PACKAGE);
    }

    private static Set<String> getExtendsSet(final MetricState state) {
        return state.getValueOrCreate("extends", DefaultValues.<String>emptySet());
    }

    private static Set<String> getUsesSet(final MetricState state) {
        return state.getValueOrCreate("uses", DefaultValues.<String>emptySet());
    }

    private static boolean isSuperClass(final MetricState state, final String className) {
        final Set<String> superClasses = state.getValue("superClasses");
        return superClasses.contains(className);
    }

    private static Map<String, ClassData> buildClassData(final Collection<MetricState> states) {
        final Map<String, ClassData> classDataMap = Maps.newHashMap();

        //Build initial files.
        for (final MetricState state : states) {
            final String className = state.getValue("className");
            classDataMap.put(className,
                    new ClassData(
                            className,
                            state.<String>getValue("packageName"),
                            state.<String>getValue("classSource")
                    )
            );
        }

        //Build map between a class and the classes it uses.
        for (final MetricState state : states) {
            final Map<String, ClassData> usesMap = Maps.newHashMap();
            for (final String usesClass : getUsesSet(state)) {
                final ClassData cData = classDataMap.get(usesClass);
                if (cData != null) {
                    usesMap.put(usesClass, cData);
                }
            }
            final Map<String, ClassData> extendsMap = Maps.newHashMap();
            for (final String extendsClass : getExtendsSet(state)) {
                final ClassData cData = classDataMap.get(extendsClass);
                if (cData != null) {
                    extendsMap.put(extendsClass, cData);
                }
            }

            //This also creates the reverse-mappings
            classDataMap.get(state.<String>getValue("className")).setRelationMaps(usesMap, extendsMap);
        }

        return classDataMap;
    }

    private static Map<String, PackageData> buildPackageDataBase(final Collection<ClassData> classDatas) {
        final Map<String, PackageData> packageDataMap = Maps.newHashMap();

        for (final ClassData cData : classDatas) {
            final PackageData pData;
            if (packageDataMap.containsKey(cData.getPackageName())) {
                pData = packageDataMap.get(cData.getPackageName());
            } else {
                pData = new PackageData(cData.getPackageName(), cData.getClassSource());
                packageDataMap.put(cData.getPackageName(), pData);
            }

            pData.registerMember(cData);
        }

        //Calculate all mappings between this package and other packages/classes
        for (final PackageData pData : packageDataMap.values()) {
            pData.calculateMappings(packageDataMap);
        }

        return packageDataMap;
    }

    @Subscribe
    public void onClass(final MetricState state, final JavaClassDefinition clazz) {
        final Set<String> extendsSet = getExtendsSet(state);
        extendsSet.add(clazz.getSuperClass());
        extendsSet.addAll(clazz.getInterfaces());

        //get transitive superclasses to act as 'subclass' filter
        final Set<String> superClasses = Sets.newHashSet(clazz.getSuperClasses());
        //Note: add class name to act as an additional filter.
        superClasses.add(clazz.getClassName());
        state.setValue("superClasses", superClasses);

        state.setValue("className", clazz.getClassName());
        state.setValue("packageName", clazz.getPackageName());
    }

    @Subscribe
    @UsingProducer(ClassSourceProducer.class)
    public void onClassSource(final MetricState state, final ClassSource source) {
        state.setValue("classSource", source.getSourceLocation());
    }

    @Subscribe
    public void onFieldAccess(final MetricState state, final FieldAccessInstr instr) {
        final String className = instr.getClassName();
        if (!isSuperClass(state, className)) {
            getUsesSet(state).add(className);
        }
    }

    @Subscribe
    public void onMethodInvoke(final MetricState state, final InvokeMethodInstr instr) {
        final String className = instr.getClassName();
        if (!isSuperClass(state, className)) {
            getUsesSet(state).add(className);
        }
    }

    @Override
    public List<Produce> getProduce(final Map<String, MetricState> states, final int invalidMembers) {
        if (invalidMembers != 0) {
            logger.warn(
                    "PackageProducer: Unsuccessful calculation for {} classes(s), " +
                            "'PackageUnit'-based metrics might be inaccurate.",
                    invalidMembers
            );
        }

        //Build objects representing all the classes.
        final Map<String, ClassData> classDataMap = Collections.unmodifiableMap(buildClassData(states.values()));

        //Build objects representing the packages and the classes they contain.
        final Map<String, PackageData> packageDataMap =
                Collections.unmodifiableMap(buildPackageDataBase(classDataMap.values()));

        return FluentIterable.from(packageDataMap.entrySet()).transform(new Function<Map.Entry<String, PackageData>, Produce>() {
            @Override
            public Produce<PackageUnit> apply(Map.Entry<String, PackageData> entry) {
                return new Produce<PackageUnit>(entry.getKey(), new PackageUnit(packageDataMap, entry.getValue()));
            }
        }).toList();
    }

    @Override
    public Class getProducedClass() {
        return PackageUnit.class;
    }
}
