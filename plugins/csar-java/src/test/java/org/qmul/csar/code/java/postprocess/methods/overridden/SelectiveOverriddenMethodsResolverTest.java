package org.qmul.csar.code.java.postprocess.methods.overridden;

import org.junit.Test;
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
import org.qmul.csar.lang.descriptors.MethodDescriptor;
import org.qmul.csar.lang.descriptors.VisibilityModifier;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SelectiveOverriddenMethodsResolverTest {

    private static final String SAMPLES_DIRECTORY = "src/test/resources/org/qmul/csar/postprocess/";
    private static final QualifiedNameResolver qnr = new QualifiedNameResolver();
    private static CodeBase code;
    private SelectiveOverriddenMethodsResolver resolver;

    public SelectiveOverriddenMethodsResolverTest() throws IllegalAccessException, InstantiationException {
        // Search target
        MethodDescriptor searchTarget = new MethodDescriptor.Builder("add").build();

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
        resolver = new SelectiveOverriddenMethodsResolver(qnr, thr, searchTarget);
        resolver.postprocess(code);
    }

    private void assertIsOverridden(String fileName, String signature) {
        MethodTestUtils.assertIsOverridden(code, SAMPLES_DIRECTORY, fileName, signature);
    }

    private void assertIsNotOverridden(String fileName, String signature) {
        MethodTestUtils.assertIsNotOverridden(code, SAMPLES_DIRECTORY, fileName, signature);
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
        assertIsOverridden("SumImpl1.java", "int add(int,int)");
        assertIsOverridden("SumImpl2.java", "int add(int,int)");
        assertIsOverridden("SumImpl3.java", "int add(int,int)");
        assertIsOverridden("base2/D4.java", "int add(int,int)");

        assertIsOverridden("SumImpl4.java", "InnerImpl#int add(int,int)");
        assertIsOverridden("SumImpl5.java", "void method()$InnerImpl#int add(int,int)");

        assertIsNotOverridden("A.java", "int add(int,int)");
        assertIsNotOverridden("base2/D3.java", "int add(int,int)");
        assertIsNotOverridden("SumImpl1.java", "String add(int,int)");
        assertIsNotOverridden("SumImpl1.java", "int add(String,String)");
    }

    @Test
    public void testOverriddenForNonSelectedMethods() {
        assertIsNotOverridden("GenericFnArgInterfaceImpl1.java", "void sort2(List)");
        assertIsNotOverridden("GenericFnArgInterfaceImpl1.java", "int time()");
        assertIsNotOverridden("GenericFnArgInterfaceImpl1.java", "void sort(List<String>)");
        assertIsNotOverridden("GenericFnArgInterfaceImpl2.java", "void sort(List)");
        assertIsNotOverridden("GenericFnArgInterfaceImpl3.java", "void sort(List<?superString>)");
        assertIsNotOverridden("GenericFnArgInterfaceImpl4.java", "void sort(List<?extendsString>)");
        assertIsNotOverridden("GenericFnArgInterfaceImpl5.java", "void sort(List<?>)");
        assertIsNotOverridden("VarArgsInterfaceImpl1.java", "void print(String...)");
        assertIsNotOverridden("VarArgsInterfaceImpl2.java", "void print(String[])");
        assertIsNotOverridden("Class1.java", "void method2(A)");
        assertIsNotOverridden("Class2.java", "void method2(B)");
        assertIsNotOverridden("Class1.java", "A method()");
        assertIsNotOverridden("Class2.java", "B method()");
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
