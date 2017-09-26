class Sample12 {

    static {
        System.out.println("Hello World");
    }

    static {

    }

    void a() {
        // If
        if (a == 3)
            a();

        if (a == 3) {
            b();
        } else {
            c();
        }

        if (a == 3) {
            a();
        } else if (b == 3) {
            b();
        }

        if (a == 3) {
            a();
        } else if (b == 3) {
            b();
        } else {
            c();
        }

        // While
        while (true)
            System.out.println("Hello World");

        while (a + b != 3) {
            System.out.println("Hello World");
        }

        // Do-While
        do {
            b();
        } while (false);

        do {
            System.out.println("Hello World");
        } while (a == 3);

        // Synchronized
        synchronized (this) {
            a();
        }

        synchronized (getLock()) {
            System.out.println("Hello World");
        }

        // For-loop
        for (int i = 0; i < 10; i++) {
            a();
        }

        for (int i = 0, k = 3; i < k; i += k) {

        }

        // For-loop with labels
        for1:
        for (int i = 0; i < 10; i++) {
            break for1;
        }

        // For-each
        for (String s : list) {
            System.out.println(s);
        }

        for (final Type<K> t : getTypes(3)) {
            a();
        }

        // Try-catch
        try {
            a();
        } finally {
            b();
        }

        try {
            a();
        } catch (Exception e) {
            error();
        } finally {
            b();
        }

        try {
            a();
        } catch (Exception | RuntimeException e) {
            error();
        }

        // Try-with-resources
        try (StringSupplier s = supplier()) {
            System.out.println(s);
        }

        try (StringSupplier s = supplier()) {
            System.out.println(s);
        } catch (Exception e) {
            error();
        }

        try (StringSupplier s = supplier()) {
            System.out.println(s);
        } catch (Exception | RuntimeException e) {
            error();
        }

        try (StringSupplier s = supplier()) {
            System.out.println(s);
        } catch (Exception | RuntimeException e) {
            error();
        } finally {
            b();
        }

        // Switch
        switch (s) {
            case "a":
                a();
                break;
            default:
                error();
        }

        switch (x = getTypes(3)) {
            case "a":
            default:
                a();
            case 1 + 1:
            case "":
                b();
        }

        switch (3) {

        }
    }

    void b() {
        // Assert
        assert (a != 3);
        assert a[3] != 3 : "a is 3";

        // Break
        break;
        break ident;

        // Return
        return;
        return a ? (1_000) * 1d : 300;

        // Throw
        throw new RuntimeException("rtex");

        // Post-fixed
        i++;
        i--;

        // Pre-fixed
        +i;
        -i;
        ++i;
        --i;
        ~i;
        !i;

        // Array expression
        final int[] a = {
                1, "a", supplier()
        };

        // Array definition
        int[] a = new int[3];
        int[] a = new int[3][];
    }
}