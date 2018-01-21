package org.qmul.csar.code.java.postprocess.typehierarchy;

import org.junit.BeforeClass;
import org.junit.Test;
import org.qmul.csar.code.parse.CodeParserFactory;
import org.qmul.csar.code.parse.DefaultProjectCodeParser;
import org.qmul.csar.code.java.parse.JavaCodeParser;
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

    @BeforeClass
    public static void setUp() throws IllegalAccessException, InstantiationException {
        // Parse sample directory
        CodeParserFactory factory = new CodeParserFactory(JavaCodeParser.class);
        Iterator<Path> it = ProjectIteratorFactory.createFilteredIterator(Paths.get(SAMPLES_DIRECTORY), false, factory);
        DefaultProjectCodeParser parser = new DefaultProjectCodeParser(factory, it);
        Map<Path, Statement> code = parser.results();

        // Resolve type hierarchy
        resolver.postprocess(code);
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
    public void testIsSubtypeExtendingFromCurrentPackage() {
        assertIsSubtype("base.A", "base.B");
        assertIsSubtype("base.A", "base.C");
        assertIsNotSubtype("base.B", "base.A");
        assertIsNotSubtype("base.B", "base.C");
        assertIsNotSubtype("base.B", "O");

        // Enum
        assertIsSubtype("base.A", "base.K");

        // Multiple-inheritance
        assertIsSubtype("base.A", "base.I");
        assertIsSubtype("base.B", "base.I");

        // External API
        assertIsSubtype("java.lang.Runnable", "base.L");

        // Fully qualified name
        assertIsSubtype("java.lang.Runnable", "base.L");
    }

    @Test
    public void testIsSubtypeExtendingInnerInterfaces() {
        assertIsSubtype("base.E$IE", "base.F");
        assertIsSubtype("base.E$IE2", "base.G");
        assertIsSubtype("base.E$IEX$I", "base.H");
    }

    @Test
    public void testIsSubtypeExtendingSuperClass() {
        assertIsSubtype("base.Q", "base.Q$ChildOfQ");
    }

    @Test
    public void testIsSubtypeExtendingAnotherInnerClass() {
        assertIsSubtype("base.R$Interface", "base.R$ChildOfInterface");
        assertIsSubtype("base.R$Interface$Inner", "base.R$ChildOfInnerInterface");
        assertIsSubtype("base.base2.D5$Inner", "base.S");
    }

    @Test
    public void testIsSubtypeExtendingFromAnotherPackage() {
        assertIsSubtype("base.A", "base.base2.D1");
        assertIsSubtype("base.A", "base.base2.D2");
    }

    @Test
    public void testIsSubtypeDefaultPackageClasses() {
        assertIsSubtype("base.A", "P");
        assertIsSubtype("N", "O");
    }

    @Test
    public void testIsSubtypeOfObjectTopLevelClasses() {
        assertIsSubtypeOfObject("base.A");
        assertIsSubtypeOfObject("base.B");
        assertIsSubtypeOfObject("base.C");
        assertIsSubtypeOfObject("base.E");
        assertIsSubtypeOfObject("base.F");
        assertIsSubtypeOfObject("base.base2.D1");
        assertIsSubtypeOfObject("base.base2.D2");
        assertIsSubtypeOfObject("base.I");
        assertIsSubtypeOfObject("base.J");
        assertIsSubtypeOfObject("base.K");
        assertIsSubtypeOfObject("base.L");
        assertIsSubtypeOfObject("base.M");
    }

    @Test
    public void testIsSubtypeOfObjectInnerClasses() {
        assertIsSubtypeOfObject("base.E$IE");
        assertIsSubtypeOfObject("base.E$IE2");
        assertIsSubtypeOfObject("base.E$IEX");
        assertIsSubtypeOfObject("base.E$IEX$I");
    }

    @Test
    public void testIsSubtypeOfObjectDefaultPackageClasses() {
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
