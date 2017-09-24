import org.qmul.csar.lang.SerializableCode;

public class Sample11 {

    public static void main(String[] args) {
        Runnable r1 = () -> System.out.println("Hello World");
        Runnable r2 = () -> {
            System.out.println("Hello World");
        };
        final BinaryOperation bo = (a, b) -> {
            System.out.println("Hello World");
        };
        BinaryOperation bo = (int a, int b) -> {
            System.out.println("Hello World");
        };
        variables.stream().map(SerializableCode::toPseudoCode);
    }
}