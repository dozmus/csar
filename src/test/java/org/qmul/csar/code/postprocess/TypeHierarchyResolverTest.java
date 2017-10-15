package org.qmul.csar.code.postprocess;

import org.junit.Test;
import org.qmul.csar.code.parse.ProjectCodeParser;
import org.qmul.csar.io.ProjectIteratorFactory;
import org.qmul.csar.lang.Statement;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeHierarchyResolverTest {

    private static final String SAMPLES_DIRECTORY = "src/test/resources/org/qmul/csar/postprocess/";
    private static final TypeHierarchyResolver resolver = new TypeHierarchyResolver();

    static {
        // Parse sample directory
        Iterator<Path> it = ProjectIteratorFactory.createFilteredIterator(Paths.get(SAMPLES_DIRECTORY), false);
        ProjectCodeParser parser = new ProjectCodeParser(it);
        Map<Path, Statement> code = parser.results();

        // Resolve type hierarchy
        resolver.resolve(code);
    }

    private void assertIsSubtypeOfObject(String type2) {
        assertTrue(resolver.isSubtype("java.lang.Object", type2));
    }

    private void assertIsSubtype(String type1, String type2) {
        assertTrue(resolver.isSubtype(type1, type2));
    }

    private void assertIsNotSubtype(String type1, String type2) {
        assertFalse(resolver.isSubtype(type1, type2));
    }

    @Test
    public void testIsSubtype() {
        // Extending in the current package
        assertIsSubtype("base.A", "base.B");
        assertIsSubtype("base.A", "base.C");
        assertIsNotSubtype("base.B", "base.A");
        assertIsNotSubtype("base.B", "base.C");

        // Extending from another package
        assertIsSubtype("base.A", "base.base2.D1");
        assertIsSubtype("base.A", "base.base2.D2");

        // Extending an inner-interface
        assertIsSubtype("base.E$IE", "base.F");
        assertIsSubtype("base.E$IE2", "base.G");
        assertIsSubtype("base.E$IEX$I", "base.H");

        // Multiple-inheritance
        assertIsSubtype("base.A", "base.I");
        assertIsSubtype("base.B", "base.I");

        // Enums
        assertIsSubtype("base.A", "base.K");

        // Java API
        assertIsSubtype("java.lang.Runnable", "base.L");

        // Fully qualified name
        assertIsSubtype("java.lang.Runnable", "base.L");

        // Default package
        assertIsSubtype("base.A", "P");
        assertIsSubtype("N", "O");
    }

    @Test
    public void testIsSubtypeOfObject() {
        assertIsSubtypeOfObject("base.A");
        assertIsSubtypeOfObject("base.B");
        assertIsSubtypeOfObject("base.C");
        assertIsSubtypeOfObject("base.E");
        assertIsSubtypeOfObject("base.E$IE");
        assertIsSubtypeOfObject("base.E$IE2");
        assertIsSubtypeOfObject("base.E$IEX");
        assertIsSubtypeOfObject("base.E$IEX$I");
        assertIsSubtypeOfObject("base.F");
        assertIsSubtypeOfObject("base.base2.D1");
        assertIsSubtypeOfObject("base.base2.D2");
        assertIsSubtypeOfObject("base.I");
        assertIsSubtypeOfObject("base.J");
        assertIsSubtypeOfObject("base.K");
        assertIsSubtypeOfObject("base.L");
        assertIsSubtypeOfObject("base.M");
        assertIsSubtypeOfObject("N");
        assertIsSubtypeOfObject("P");
        assertIsSubtypeOfObject("O");
    }

    @Test
    public void testIsSubtypeSameArgument() {
        assertIsSubtype("B", "B");
        assertIsSubtype("base.B", "base.B");
    }
}
