public class MethodReferenceSample  {
    public void bar() {
        Function<Computer, Integer> getAge = Computer::getAge;
        Integer computerAge = getAge.apply(c1);

        TriFunction <Integer, String, Integer, Computer> c6Function = Computer::new;
        Computer c3 = c6Function.apply(2008, "black", 90);

        Function <Integer, Computer[]> computerCreator = Computer[]::new;
        Computer[] computerArray = computerCreator.apply(5);
    }
}
