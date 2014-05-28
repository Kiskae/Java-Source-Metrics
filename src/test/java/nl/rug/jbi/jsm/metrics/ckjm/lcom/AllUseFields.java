package nl.rug.jbi.jsm.metrics.ckjm.lcom;

public class AllUseFields {
    private int field1;
    private int field2;
    private int field3;

    public AllUseFields() {
        //The constructor is also a method.
        field1++;
        field2++;
        field3++;
    }

    public void method1() {
        field1++;
        field2++;
        field3++;
    }

    public void method2() {
        field1++;
        field2++;
        field3++;
    }
}
