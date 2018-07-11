public class MethodReferenceSample  {
    public void bar() {
        Function<Computer, Integer> getAge = Computer::getAge;
        Integer computerAge = getAge.apply(c1);

        Function<Computer, Integer> getAgeAlt1 = this::getAge;
        Function<Computer, Integer> getAgeAlt2 = MyClass.this::getAge;
        Function<Computer, Integer> getAgeAlt3 = generate()::getAge;
        Function<Computer, Integer> getAgeAlt4 = MyClass.generate()::getAge;
        Function<Computer, Integer> getAgeAlt5 = MyClass.twice().nested()::getAge;
        Function<Computer, Integer> getAgeAlt6 = twice().nested()::getAge;

        TriFunction <Integer, String, Integer, Computer> c6Function = Computer::new;
        Computer c3 = c6Function.apply(2008, "black", 90);

        Function <Integer, Computer[]> computerCreator = Computer[]::new;
        Computer[] computerArray = computerCreator.apply(5);

        // The following method references were taken from the elasticsearch github repository
        // Source: https://github.com/elastic/elasticsearch
        Function f1 = ((RoundingValuesSource) vs)::round;
        Function f2 = InternalAggregation.class::cast;
        Function f3 = ""::equals;
        Function f4 = "Hello World"::toCharArray;
        Function f5 = new String[]{"foo:bar", "cluster_1:bar"}::equals;
        Function f6 = this.actions::add;
    }
}
