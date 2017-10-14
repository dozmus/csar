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

    private void assertSubtypeOfObject(String qualifiedName) {
        assertTrue(resolver.isSubtype("java.lang.Object", qualifiedName));
    }

    @Test
    public void testIsSubtype() {
        // Extending in the current package
        assertTrue(resolver.isSubtype("base.A", "base.B"));
        assertTrue(resolver.isSubtype("base.A", "base.C"));
        assertFalse(resolver.isSubtype("base.B", "base.A"));
        assertFalse(resolver.isSubtype("base.B", "base.C"));

        // Extending from another package
        assertTrue(resolver.isSubtype("base.A", "base.base2.D1"));
        assertTrue(resolver.isSubtype("base.A", "base.base2.D2"));

        // Extending an inner-interface
        assertTrue(resolver.isSubtype("base.E$IE", "base.F"));
        assertTrue(resolver.isSubtype("base.E$IE2", "base.G"));
        assertTrue(resolver.isSubtype("base.E$IEX$I", "base.H"));

        // Multiple-inheritance
        assertTrue(resolver.isSubtype("base.A", "base.I"));
        assertTrue(resolver.isSubtype("base.B", "base.I"));

        // Enums
        assertTrue(resolver.isSubtype("base.A", "base.K"));

        // Java API
        assertTrue(resolver.isSubtype("java.lang.Runnable", "base.L"));

        // Fully qualified name
        assertTrue(resolver.isSubtype("java.lang.Runnable", "base.L"));
    }

    @Test
    public void testIsSubtypeOfObject() {
        assertSubtypeOfObject("base.A");
        assertSubtypeOfObject("base.B");
        assertSubtypeOfObject("base.C");
        assertSubtypeOfObject("base.E");
        assertSubtypeOfObject("base.E$IE");
        assertSubtypeOfObject("base.E$IE2");
        assertSubtypeOfObject("base.E$IEX");
        assertSubtypeOfObject("base.E$IEX$I");
        assertSubtypeOfObject("base.F");
        assertSubtypeOfObject("base.base2.D1");
        assertSubtypeOfObject("base.base2.D2");
        assertSubtypeOfObject("base.I");
        assertSubtypeOfObject("base.J");
        assertSubtypeOfObject("base.K");
        assertSubtypeOfObject("base.L");
        assertSubtypeOfObject("base.M");
    }

    @Test
    public void testIsSubtypeSameArgument() {
        assertTrue(resolver.isSubtype("base.B", "base.B"));
    }
}
