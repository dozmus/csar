// Internal contents are partially taken from Source: https://github.com/google/guava/
public class AnnotationUse {

    @NullableDecl transient volatile Cell[] cells;

    public static int hashCode(@NullableDecl Object... objects) {
        return Arrays.hashCode(objects);
    }

    @NullableDecl
    public void a() {

    }
}
