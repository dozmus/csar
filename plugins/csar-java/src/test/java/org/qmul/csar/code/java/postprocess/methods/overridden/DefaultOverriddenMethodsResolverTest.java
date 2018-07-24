package org.qmul.csar.code.java.postprocess.methods.overridden;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.qmul.csar.code.CodeBase;
import org.qmul.csar.code.java.parse.JavaCodeParser;
import org.qmul.csar.code.java.postprocess.methods.MethodTestUtils;
import org.qmul.csar.code.java.postprocess.methods.types.MethodQualifiedTypeResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.DefaultTypeHierarchyResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.code.parse.CodeParserFactory;
import org.qmul.csar.code.parse.DefaultProjectCodeParser;
import org.qmul.csar.io.it.ProjectIteratorFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

@RunWith(Parameterized.class)
public class DefaultOverriddenMethodsResolverTest {

    private static final String SAMPLES_DIRECTORY = "src/test/resources/org/qmul/csar/postprocess/";
    private static final QualifiedNameResolver qnr = new QualifiedNameResolver();
    private static CodeBase code;
    private DefaultOverriddenMethodsResolver resolver;

    @Parameterized.Parameters
    public static Collection threadCounts() {
        return Arrays.asList(1, 2, 3, 4);
    }

    public DefaultOverriddenMethodsResolverTest(int threadCount) throws IllegalAccessException, InstantiationException {
        // Parse sample directory
        CodeParserFactory factory = new CodeParserFactory(JavaCodeParser.class);
        Iterator<Path> it = ProjectIteratorFactory.createFilteredIterator(Paths.get(SAMPLES_DIRECTORY), false, factory);
        DefaultProjectCodeParser parser = new DefaultProjectCodeParser(factory, it);
        code = parser.results();

        // Resolve type hierarchy
        TypeHierarchyResolver thr = new DefaultTypeHierarchyResolver(qnr);
        thr.postprocess(code);

        // Resolve method types
        new MethodQualifiedTypeResolver().postprocess(code);

        // Resolve overridden methods
        resolver = new DefaultOverriddenMethodsResolver(threadCount, qnr, thr);
        resolver.postprocess(code);
    }

    private void assertIsOverridden(String fileName, String signature) {
        MethodTestUtils.assertIsOverridden(code, SAMPLES_DIRECTORY, fileName, signature);
    }

    private void assertIsNotOverridden(String fileName, String signature) {
        MethodTestUtils.assertIsNotOverridden(code, SAMPLES_DIRECTORY, fileName, signature);
    }

    @Test
    public void testOverriddenForRegularOverriddenMethods() {
        assertIsOverridden("SumImpl1.java", "int add(int,int)");
        assertIsOverridden("SumImpl2.java", "int add(int,int)");
        assertIsOverridden("SumImpl3.java", "int add(int,int)");
        assertIsOverridden("base2/D4.java", "int add(int,int)");
    }

    @Test
    public void testOverriddenForMethodsWithSimilarSignaturesWhichArentOverridden() {
        assertIsNotOverridden("SumImpl1.java", "String add(int,int)");
        assertIsNotOverridden("SumImpl1.java", "int add(String,String)");
    }

    @Test
    public void testOverriddenForMethodsWithIdenticalSignaturesWhichArentOverridden() {
        assertIsNotOverridden("A.java", "int add(int,int)");
        assertIsNotOverridden("base2/D3.java", "int add(int,int)");
    }

    @Test
    public void testOverriddenForMethodsWithDifferentParameterTypeDimensions() {
        assertIsNotOverridden("GenericFnArgInterfaceImpl1.java", "void sort2(List)");
    }

    @Test
    public void testOverriddenForMethodsWithDifferentReturnTypeDimensions() {
        assertIsNotOverridden("GenericFnArgInterfaceImpl1.java", "int time()");
    }

    @Test
    public void testOverriddenForMethodsWithGenericArguments() {
        assertIsOverridden("GenericFnArgInterfaceImpl1.java", "void sort(List<String>)");
        assertIsOverridden("GenericFnArgInterfaceImpl2.java", "void sort(List)");
    }

    @Test
    public void testOverriddenForMethodsWithGenericArgumentsWithSimilarSignaturesWhichArentOverridden() {
        assertIsNotOverridden("GenericFnArgInterfaceImpl3.java", "void sort(List<?superString>)");
        assertIsNotOverridden("GenericFnArgInterfaceImpl4.java", "void sort(List<?extendsString>)");
        assertIsNotOverridden("GenericFnArgInterfaceImpl5.java", "void sort(List<?>)");
    }

    @Test
    public void testOverriddenForMethodsWithVarArgsArguments() {
        assertIsOverridden("VarArgsInterfaceImpl1.java", "void print(String...)");
        assertIsOverridden("VarArgsInterfaceImpl2.java", "void print(String[])");
    }

    @Test
    public void testOverriddenForMethodsInInnerClasses() {
        assertIsOverridden("SumImpl4.java", "InnerImpl#int add(int,int)");
    }

    @Test
    public void testOverriddenForInnerClassInMethod() {
        assertIsOverridden("SumImpl5.java", "void method()$InnerImpl#int add(int,int)");
    }

    @Test
    public void testOverriddenForSubtypeReturnTypeMethod() {
        assertIsNotOverridden("Class1.java", "A method()");
        assertIsOverridden("Class2.java", "B method()");
    }

    @Test
    public void testOverriddenForSubtypeParameterMethod() {
        assertIsNotOverridden("Class1.java", "void method2(A)");
        assertIsOverridden("Class2.java", "void method2(B)");
    }
}
