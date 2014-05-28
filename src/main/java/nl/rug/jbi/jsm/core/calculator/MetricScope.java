package nl.rug.jbi.jsm.core.calculator;

import java.util.EnumSet;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This enum specifies the 3 different scopes at/for which a metric can be calculated.
 * Each element of the scope can be seen as a part of an element in the next scope.
 * <br>
 * To prevent issues related to dependency cycles, the scopes are evaluated in the strict
 * order CLASS, PACKAGE and then COLLECTION.
 *
 * @author David van Leusen
 * @since 2014-05-28
 */
public enum MetricScope {
    /**
     * The class scope is the lowest scope and concerns itself with a single class/interface/enum.
     * Each CLASS belongs to a single PACKAGE and COLLECTION.
     * <p>
     * This scope can be followed by: PACKAGE, COLLECTION
     * </p>
     */
    CLASS {
        @Override
        public EnumSet<MetricScope> getAcceptableNextScopes() {
            return EnumSet.of(PACKAGE, COLLECTION);
        }
    },
    /**
     * The package scope concerns itself with a set of classes in a common package.
     * Each PACKAGE belongs to at least one COLLECTION. The java specification doesn't forbid a package being spread
     * over multiple collections, but the VM can disallow it if the 'sealed' manifest flag is set.
     * <br>
     * For the purpose of clarity, each package is assigned to the same collection as the first class it encounters
     * within that package.
     * <p>
     * This scope can be followed by: COLLECTION
     * </p>
     */
    PACKAGE {
        @Override
        public EnumSet<MetricScope> getAcceptableNextScopes() {
            return EnumSet.of(COLLECTION);
        }
    },
    /**
     * The collection scope represents a set of packages that are contained within a .JAR or java directory structure.
     * <p>
     * This scope can be followed by:
     * </p>
     */
    COLLECTION {
        @Override
        public EnumSet<MetricScope> getAcceptableNextScopes() {
            return EnumSet.noneOf(MetricScope.class);
        }
    };

    /**
     * @return A set containing all scopes that can follow this scope.
     */
    public abstract EnumSet<MetricScope> getAcceptableNextScopes();

    /**
     * Checks whether the given scope can follow up this scope.
     * It will return true if either of the following conditions are met:
     * <ul>
     * <li>The given scope is the same as the current scope.</li>
     * <li>The given scope is a member of {@link #getAcceptableNextScopes()}</li>
     * </ul>
     *
     * @param nextScope The scope to check
     * @return Whether the given scope is a valid next scope.
     */
    public boolean isValidNextScope(final MetricScope nextScope) {
        checkArgument(nextScope != null, "nextScope cannot be NULL");

        return this == nextScope
                || this.getAcceptableNextScopes().contains(nextScope);
    }
}
