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

public class OverriddenMethodsResolverTest {

    private static final String SAMPLES_DIRECTORY = "src/test/resources/org/qmul/csar/postprocess/";
    private static final OverriddenMethodsResolver resolver = new OverriddenMethodsResolver();

    static {
        // Parse sample directory
        Iterator<Path> it = ProjectIteratorFactory.createFilteredIterator(Paths.get(SAMPLES_DIRECTORY), false);
        ProjectCodeParser parser = new ProjectCodeParser(it);
        Map<Path, Statement> code = parser.results();

        // Resolve overridden methods
        resolver.resolve(code);
    }

    private void assertIsOverridden(String methodSignature) {
        assertTrue(resolver.isOverridden(methodSignature));
    }

    private void assertIsNotOverridden(String methodSignature) {
        assertFalse(resolver.isOverridden(methodSignature));
    }

    @Test
    public void testOverridden() {
        // Check overridden methods
        assertIsOverridden("base.SumImpl1#int add(int,int)");
        assertIsOverridden("base.SumImpl2#int add(int,int)");
        assertIsOverridden("base.SumImpl3#int add(int,int)");
        assertIsOverridden("base.base2.D4#int add(int,int)");

        // Check methods with similar signatures which aren't overridden
        assertIsNotOverridden("base.SumImpl1#String add(int,int)");
        assertIsNotOverridden("base.SumImpl1#int add(String,String)");

        // Check classes with identical methods, which don't extend the interface
        assertIsNotOverridden("base.A#int add(int,int)");
        assertIsNotOverridden("base.base2.D3#int add(int,int)");

        // Check overridden methods with generic arguments
        assertIsOverridden("base.GenericFnArgInterfaceImpl1#void sort(List<String>)");
        assertIsOverridden("base.GenericFnArgInterfaceImpl2#void sort(List)");

        // Check methods with generic arguments with similar signatures which aren't overridden
        assertIsNotOverridden("base.GenericFnArgInterfaceImpl3#void sort(List<? super String>)");
        assertIsNotOverridden("base.GenericFnArgInterfaceImpl4#void sort(List<? extends String>)");
        assertIsNotOverridden("base.GenericFnArgInterfaceImpl5#void sort(List<?>)");

        // Check overridden methods with varargs arguments
        assertIsOverridden("base.VarArgsInterfaceImpl1#void print(String...)");
        assertIsOverridden("base.VarArgsInterfaceImpl2#void print(String[])");
    }
}
