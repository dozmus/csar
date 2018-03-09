package grammars.java8;

interface Expandable<T extends Number> {
    void addItem(T item);

    class Array<T extends Number> implements Expandable<T> {
        void addItem(T item) {
        }
    }

    class IntegerArray implements Expandable<Integer> {
        void addItem(Integer item) {
        }
    }
}
