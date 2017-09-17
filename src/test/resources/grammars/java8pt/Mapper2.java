import java.util.ArrayList;
import java.util.Formattable;

public class Mapper2<T extends ArrayList & Formattable, V> {
    public void add(T array, V item) {
        // array has add method because it is an ArrayList subclass
        array.add(item);

        /* Mapper is created with CustomList as T and Integer as V.
        CustomList must be a subclass of ArrayList and implement Formattable */
        myapplication.mylibrary.Mapper<CustomList, Integer> mapper = new myapplication.mylibrary.Mapper<CustomList, Integer>();

        myapplication.mylibrary.Mapper<CustomList, ?> mapper;
        mapper = new myapplication.mylibrary.Mapper<CustomList, Boolean>();
        mapper = new myapplication.mylibrary.Mapper<CustomList, Integer>();
    }
}
