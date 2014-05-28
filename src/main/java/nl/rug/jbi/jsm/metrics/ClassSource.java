package nl.rug.jbi.jsm.metrics;

/**
 * Additional data type representing the source a class originates from.
 *
 * @author David van Leusen
 * @since 2014-05-29
 */
public class ClassSource {
    private final String identifier;
    private final String sourceLocation;

    ClassSource(final String identifier, final String sourceLocation) {
        this.identifier = identifier;
        this.sourceLocation = sourceLocation;
    }

    /**
     * @return Name of the referenced class.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @return String representing the class-loader of the class, can be Internal, a directory or a .JAR
     */
    public String getSourceLocation() {
        return sourceLocation;
    }
}
