package nl.rug.jbi.jsm.metrics.ckjm.cbo;

public class RefTypes {
    public final static Map access = null;

    public static Map test(List a, FakeInterface b) {
        throw new Exception();
    }

    public interface FakeInterface {

    }

    public static class Map {

    }

    public static class List {

    }

    public static class Exception extends RuntimeException {

    }
}
