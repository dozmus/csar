package org.qmul.csar.code.java.postprocess.overriddenmethods;

import org.junit.BeforeClass;
import org.junit.Test;
import org.qmul.csar.code.parse.CodeParserFactory;
import org.qmul.csar.code.parse.ProjectCodeParser;
import org.qmul.csar.code.java.parse.JavaCodeParser;
import org.qmul.csar.code.java.postprocess.methodtypes.MethodQualifiedTypeResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
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
    private static final QualifiedNameResolver qualifiedNameResolver = new QualifiedNameResolver();
    private static final TypeHierarchyResolver typeHierarchyResolver = new TypeHierarchyResolver(qualifiedNameResolver
    );
    private static final MethodQualifiedTypeResolver methodQualifiedTypeResolver
            = new MethodQualifiedTypeResolver(qualifiedNameResolver);
    private static final OverriddenMethodsResolver resolver = new OverriddenMethodsResolver(qualifiedNameResolver,
            typeHierarchyResolver);

    @BeforeClass
    public static void setUp() throws IllegalAccessException, InstantiationException {
        // Parse sample directory
        CodeParserFactory factory = new CodeParserFactory(JavaCodeParser.class);
        Iterator<Path> it = ProjectIteratorFactory.createFilteredIterator(Paths.get(SAMPLES_DIRECTORY), false, factory);
        ProjectCodeParser parser = new ProjectCodeParser(factory, it);
        Map<Path, Statement> code = parser.results();

        // Resolve type hierarchy
        typeHierarchyResolver.analyze(code);

        // Resolve method types
        methodQualifiedTypeResolver.analyze(code);

        // Resolve overridden methods
        resolver.analyze(code);
    }

    private static void assertIsOverridden(String methodSignature) {
        assertTrue(resolver.isOverridden(methodSignature));
    }

    private static void assertIsNotOverridden(String methodSignature) {
        assertFalse(resolver.isOverridden(methodSignature));
    }

    @Test
    public void testOverriddenForRegularOverriddenMethods() {
        assertIsOverridden("base.SumImpl1#int add(int,int)");
        assertIsOverridden("base.SumImpl2#int add(int,int)");
        assertIsOverridden("base.SumImpl3#int add(int,int)");
        assertIsOverridden("base.base2.D4#int add(int,int)");
    }

    @Test
    public void testOverriddenForMethodsWithSimilarSignaturesWhichArentOverridden() {
        assertIsNotOverridden("base.SumImpl1#String add(int,int)");
        assertIsNotOverridden("base.SumImpl1#int add(String,String)");
    }

    @Test
    public void testOverriddenForMethodsWithIdenticalSignaturesWhichArentOverridden() {
        assertIsNotOverridden("base.A#int add(int,int)");
        assertIsNotOverridden("base.base2.D3#int add(int,int)");
    }

    @Test
    public void testOverriddenForMethodsWithDifferentParameterTypeDimensions() {
        assertIsNotOverridden("base.GenericFnArgInterfaceImpl1#void sort2(List)");
    }

    @Test
    public void testOverriddenForMethodsWithDifferentReturnTypeDimensions() {
        assertIsNotOverridden("base.GenericFnArgInterfaceImpl1#int time()");
    }

    @Test
    public void testOverriddenForMethodsWithGenericArguments() {
        assertIsOverridden("base.GenericFnArgInterfaceImpl1#void sort(List<String>)");
        assertIsOverridden("base.GenericFnArgInterfaceImpl2#void sort(List)");
    }

    @Test
    public void testOverriddenForMethodsWithGenericArgumentsWithSimilarSignaturesWhichArentOverridden() {
        assertIsNotOverridden("base.GenericFnArgInterfaceImpl3#void sort(List<? super String>)");
        assertIsNotOverridden("base.GenericFnArgInterfaceImpl4#void sort(List<? extends String>)");
        assertIsNotOverridden("base.GenericFnArgInterfaceImpl5#void sort(List<?>)");
    }

    @Test
    public void testOverriddenForMethodsWithVarArgsArguments() {
        assertIsOverridden("base.VarArgsInterfaceImpl1#void print(String...)");
        assertIsOverridden("base.VarArgsInterfaceImpl2#void print(String[])");
    }

    @Test
    public void testOverriddenForMethodsInInnerClasses() {
        assertIsOverridden("base.SumImpl4$InnerImpl#int add(int,int)");
    }

    @Test
    public void testOverriddenForInnerClassInMethod() {
        assertIsOverridden("base.SumImpl5#void method()$InnerImpl#int add(int,int)");
    }

    @Test
    public void testOverriddenForSubtypeReturnTypeMethod() {
        assertIsNotOverridden("base.Class1#A method()");
        assertIsOverridden("base.Class2#B method()");
    }

    @Test
    public void testOverriddenForSubtypeParameterMethod() {
        assertIsNotOverridden("base.Class1#void method2(A)");
        assertIsOverridden("base.Class2#void method2(B)");
    }
}
