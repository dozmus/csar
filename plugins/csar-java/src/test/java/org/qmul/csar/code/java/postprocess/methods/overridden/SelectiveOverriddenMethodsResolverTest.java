package org.qmul.csar.code.java.postprocess.methods.overridden;

import org.junit.Test;
import org.qmul.csar.code.java.parse.JavaCodeParser;
import org.qmul.csar.code.java.postprocess.methods.types.MethodQualifiedTypeResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.DefaultTypeHierarchyResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.code.parse.CodeParserFactory;
import org.qmul.csar.code.parse.DefaultProjectCodeParser;
import org.qmul.csar.io.it.ProjectIteratorFactory;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.descriptors.MethodDescriptor;
import org.qmul.csar.lang.descriptors.VisibilityModifier;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SelectiveOverriddenMethodsResolverTest {

    private static final String SAMPLES_DIRECTORY = "src/test/resources/org/qmul/csar/postprocess/";
    private static final QualifiedNameResolver qnr = new QualifiedNameResolver();
    private SelectiveOverriddenMethodsResolver resolver;

    public SelectiveOverriddenMethodsResolverTest() throws IllegalAccessException, InstantiationException {
        // Search target
        MethodDescriptor searchTarget = new MethodDescriptor.Builder("add").build();

        // Parse sample directory
        CodeParserFactory factory = new CodeParserFactory(JavaCodeParser.class);
        Iterator<Path> it = ProjectIteratorFactory.createFilteredIterator(Paths.get(SAMPLES_DIRECTORY), false, factory);
        DefaultProjectCodeParser parser = new DefaultProjectCodeParser(factory, it);
        Map<Path, Statement> code = parser.results();

        // Resolve type hierarchy
        TypeHierarchyResolver thr = new DefaultTypeHierarchyResolver(qnr);
        thr.postprocess(code);

        // Resolve method types
        new MethodQualifiedTypeResolver(qnr).postprocess(code);

        // Resolve overridden methods
        resolver = new SelectiveOverriddenMethodsResolver(qnr, thr, searchTarget);
        resolver.postprocess(code);
    }

    private void assertIsOverridden(String methodSignature) {
        assertTrue(resolver.isOverridden(methodSignature));
    }

    private void assertIsNotOverridden(String methodSignature) {
        assertFalse(resolver.isOverridden(methodSignature));
    }

    private static void assertIsIgnored(MethodDescriptor other, MethodDescriptor target) {
        MethodDescriptor normalizedTarget = SelectiveOverriddenMethodsResolver.removeOverridden(target);
        assertTrue(SelectiveOverriddenMethodsResolver.isIgnored(other, normalizedTarget));
    }

    private static void assertIsNotIgnored(MethodDescriptor other, MethodDescriptor target) {
        MethodDescriptor normalizedTarget = SelectiveOverriddenMethodsResolver.removeOverridden(target);
        assertFalse(SelectiveOverriddenMethodsResolver.isIgnored(other, normalizedTarget));
    }

    @Test
    public void testOverriddenForSelectedMethods() {
        assertIsOverridden("base.SumImpl1#int add(int,int)");
        assertIsOverridden("base.SumImpl2#int add(int,int)");
        assertIsOverridden("base.SumImpl3#int add(int,int)");
        assertIsOverridden("base.base2.D4#int add(int,int)");
        assertIsOverridden("base.SumImpl4$InnerImpl#int add(int,int)");
        assertIsOverridden("base.SumImpl5#void method()$InnerImpl#int add(int,int)");

        assertIsNotOverridden("base.A#int add(int,int)");
        assertIsNotOverridden("base.base2.D3#int add(int,int)");
        assertIsNotOverridden("base.SumImpl1#String add(int,int)");
        assertIsNotOverridden("base.SumImpl1#int add(String,String)");
    }

    @Test
    public void testOverriddenForNonSelectedMethods() {
        assertIsNotOverridden("base.GenericFnArgInterfaceImpl1#void sort2(List)");
        assertIsNotOverridden("base.GenericFnArgInterfaceImpl1#int time()");
        assertIsNotOverridden("base.GenericFnArgInterfaceImpl1#void sort(List<String>)");
        assertIsNotOverridden("base.GenericFnArgInterfaceImpl2#void sort(List)");
        assertIsNotOverridden("base.GenericFnArgInterfaceImpl3#void sort(List<? super String>)");
        assertIsNotOverridden("base.GenericFnArgInterfaceImpl4#void sort(List<? extends String>)");
        assertIsNotOverridden("base.GenericFnArgInterfaceImpl5#void sort(List<?>)");
        assertIsNotOverridden("base.VarArgsInterfaceImpl1#void print(String...)");
        assertIsNotOverridden("base.VarArgsInterfaceImpl2#void print(String[])");
        assertIsNotOverridden("base.Class1#void method2(A)");
        assertIsNotOverridden("base.Class2#void method2(B)");
        assertIsNotOverridden("base.Class1#A method()");
        assertIsNotOverridden("base.Class2#B method()");
    }

    @Test
    public void testIsNotIgnored() {
        MethodDescriptor target = new MethodDescriptor.Builder("add")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .returnType("int")
                .overridden(true)
                .build();
        MethodDescriptor other1 = MethodDescriptor.Builder.allFalse("add")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .returnType("int")
                .overridden(false)
                .build();
        MethodDescriptor other2 = MethodDescriptor.Builder.allFalse("add")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .returnType("void")
                .overridden(false)
                .build();
        MethodDescriptor other3 = MethodDescriptor.Builder.allFalse("sum")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .returnType("int")
                .build();

        // Assert against themselves
        assertIsNotIgnored(target, target);
        assertIsNotIgnored(other1, other1);
        assertIsNotIgnored(other2, other2);
        assertIsNotIgnored(other3, other3);

        // Assert against each other
        assertIsNotIgnored(other1, target);
        assertIsIgnored(other2, target);
        assertIsIgnored(other3, target);
    }
}
