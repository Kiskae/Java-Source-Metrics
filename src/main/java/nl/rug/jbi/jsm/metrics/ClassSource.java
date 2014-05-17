package nl.rug.jbi.jsm.metrics;

public class ClassSource {
    private final String identifier;
    private final String sourceLocation;

    ClassSource(final String identifier, final String sourceLocation) {
        this.identifier = identifier;
        this.sourceLocation = sourceLocation;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getSourceLocation() {
        return sourceLocation;
    }
}
