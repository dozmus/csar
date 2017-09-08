package grammars.java8pt

public strictfp abstract class Sample1 extends AbstractSample {

    public abstract void add(final int a, int b);

    protected final int getResult() {
        return result;
    }

    final void setResult(int result) {
        this.result = result;
    }
}
