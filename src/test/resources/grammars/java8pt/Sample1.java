package grammars.java8pt

public strictfp abstract class Sample1 extends AbstractSample {

    public Sample1() {

    }

    public <E> Sample1(String... names) {

    }

    private final String className = "Sample1";
    String str;

    public abstract void add(final int[] a, int b);

    protected final int getResult() {
        return result;
    }

    final <E extends AbstractSample> void setResult(int result) {
        this.result = result;
    }
}
