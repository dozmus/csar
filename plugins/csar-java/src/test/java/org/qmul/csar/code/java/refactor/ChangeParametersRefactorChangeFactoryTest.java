package org.qmul.csar.code.java.refactor;

import org.junit.Test;
import org.qmul.csar.code.java.TestUtils;
import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.code.java.parse.statement.ParameterVariableStatement;
import org.qmul.csar.code.java.postprocess.typehierarchy.DefaultTypeHierarchyResolver;
import org.qmul.csar.code.java.postprocess.util.TypeHelper;
import org.qmul.csar.code.java.postprocess.util.TypeInstance;
import org.qmul.csar.code.refactor.RefactorChange;
import org.qmul.csar.lang.IdentifierName;
import org.qmul.csar.lang.descriptors.ParameterVariableDescriptor;
import org.qmul.csar.util.FilePosition;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChangeParametersRefactorChangeFactoryTest {

    private final ChangeParametersRefactorChangeFactory factory = new ChangeParametersRefactorChangeFactory(
            new DefaultTypeHierarchyResolver() // XXX we use inheritance hierarchy of Java primitives in subtype tests
    );

    @Test
    public void testMethodStatement_NoArgsToNoArgs_NoChange() {
        // Configure MethodStatement instance
        Path path = Paths.get(".");
        int lineNumber = 40;
        int startOffset = 103;
        int endOffset = 104;

        MethodStatement ms = mockMethodStatement(path, lineNumber, startOffset, endOffset);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Collections.emptyList();

        // Create change
        List<RefactorChange> changes = factory.changes(ms, newParameters);

        // Assert
        assertEquals(Collections.emptyList(), changes);
    }

    @Test
    public void testMethodCallExpression_NoArgsToNoArgs_NoChange() {
        // Configure MethodCallExpression instance
        Path path = Paths.get(".");
        int lineNumber = 40;
        int startOffset = 103;
        int endOffset = 104;

        MethodCallExpression mce = mockMethodCallExpression(path, lineNumber, startOffset, endOffset);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Collections.emptyList();

        // Create change
        List<RefactorChange> changes = factory.changes(mce, newParameters);

        // Assert
        assertEquals(Collections.emptyList(), changes);
    }

    @Test
    public void testMethodStatement_NoArgsTo1Arg_1Change() {
        // Configure MethodStatement instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 5;
        int endOffset = 6;

        MethodStatement ms = mockMethodStatement(path, lineNumber, startOffset, endOffset);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Collections.singletonList(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int[]", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(ms, newParameters);

        // Assert
        RefactorChange expected = new RefactorChange(path, lineNumber, endOffset, endOffset, "int[] a");

        assertEquals(1, changes.size());
        assertEquals(expected, changes.get(0));
    }

    @Test
    public void testMethodCallExpression_NoArgsTo1Arg_1Change() {
        // Configure MethodCallExpression instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 5;
        int endOffset = 6;

        MethodCallExpression mce = mockMethodCallExpression(path, lineNumber, startOffset, endOffset);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Collections.singletonList(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int[]", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(mce, newParameters);

        // Assert
        RefactorChange expected = new RefactorChange(path, lineNumber, endOffset, endOffset, "a");

        assertEquals(1, changes.size());
        assertEquals(expected, changes.get(0));
    }

    @Test
    public void testMethodStatement_NoArgsTo2Args_1Change() {
        // Configure MethodStatement instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 5;
        int endOffset = 6;

        MethodStatement ms = mockMethodStatement(path, lineNumber, startOffset, endOffset);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Arrays.asList(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int[]", false),
                new ParameterVariableDescriptor(new IdentifierName.Static("identifier"), "IdentifierName", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(ms, newParameters);

        // Assert
        String args = "int[] a, IdentifierName identifier";
        RefactorChange expected = new RefactorChange(path, lineNumber, endOffset, endOffset, args);

        assertEquals(1, changes.size());
        assertEquals(expected, changes.get(0));
    }

    @Test
    public void testMethodCallExpression_NoArgsTo2Args_1Change() {
        // Configure MethodCallExpression instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 5;
        int endOffset = 6;

        MethodCallExpression mce = mockMethodCallExpression(path, lineNumber, startOffset, endOffset);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Arrays.asList(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int[]", false),
                new ParameterVariableDescriptor(new IdentifierName.Static("s"), "String", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(mce, newParameters);

        // Assert
        String args = "a, s";
        RefactorChange expected = new RefactorChange(path, lineNumber, endOffset, endOffset, args);

        assertEquals(1, changes.size());
        assertEquals(expected, changes.get(0));
    }

    @Test
    public void testMethodStatement_1ArgToNoArgs_1Change() {
        // Configure MethodStatement instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 4;
        int endOffset = 8;

        ParameterVariableStatement parameter = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int", false), Collections.emptyList()
        );
        parameter.setTypeInstance(new TypeInstance("int", 1));
        List<ParameterVariableStatement> parameters = Collections.singletonList(parameter);

        MethodStatement ms = mockMethodStatement(path, lineNumber, startOffset, endOffset, parameters);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Collections.emptyList();

        // Create change
        List<RefactorChange> changes = factory.changes(ms, newParameters);

        // Assert
        RefactorChange expected = new RefactorChange(path, lineNumber, startOffset + 1, endOffset, "");

        assertEquals(1, changes.size());
        assertEquals(expected, changes.get(0));
    }

    @Test
    public void testMethodCallExpression_1ArgToNoArgs_1Change() {
        // Configure MethodCallExpression instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 4;
        int endOffset = 8;

        ParameterVariableStatement parameter = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int", false), Collections.emptyList()
        );
        parameter.setTypeInstance(new TypeInstance("int", 1));
        List<ParameterVariableStatement> parameters = Collections.singletonList(parameter);

        MethodCallExpression mce = mockMethodCallExpression(path, lineNumber, startOffset, endOffset, parameters);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Collections.emptyList();

        // Create change
        List<RefactorChange> changes = factory.changes(mce, newParameters);

        // Assert
        RefactorChange expected = new RefactorChange(path, lineNumber, startOffset + 1, endOffset, "");

        assertEquals(1, changes.size());
        assertEquals(expected, changes.get(0));
    }

    @Test
    public void testMethodStatement_1ArgToSameArgs_NoChange() {
        // Configure MethodStatement instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 1;
        int endOffset = 5;

        ParameterVariableStatement parameter = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int", false), Collections.emptyList()
        );
        parameter.setTypeInstance(new TypeInstance("int", 0));
        List<ParameterVariableStatement> parameters = Collections.singletonList(parameter);

        MethodStatement ms = mockMethodStatement(path, lineNumber, startOffset, endOffset, parameters);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Collections.singletonList(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(ms, newParameters);

        // Assert
        assertEquals(0, changes.size());
    }

    @Test
    public void testMethodCallExpression_1ArgToSameArgs_NoChange() {
        // Configure MethodCallExpression instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 1;
        int endOffset = 5;

        ParameterVariableStatement parameter = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int", false), Collections.emptyList()
        );
        parameter.setTypeInstance(new TypeInstance("int", 0));
        List<ParameterVariableStatement> parameters = Collections.singletonList(parameter);

        MethodCallExpression mce = mockMethodCallExpression(path, lineNumber, startOffset, endOffset, parameters);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Collections.singletonList(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(mce, newParameters);

        // Assert
        assertEquals(0, changes.size());
    }
    
    @Test
    public void testMethodCallExpression_1ArgToSubtypeArgs_NoChange() {
        // Configure MethodCallExpression instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 1;
        int endOffset = 5;

        ParameterVariableStatement parameter = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int", false), Collections.emptyList()
        );
        parameter.setTypeInstance(new TypeInstance("java.lang.Integer", 0));
        List<ParameterVariableStatement> parameters = Collections.singletonList(parameter);

        MethodCallExpression mce = mockMethodCallExpression(path, lineNumber, startOffset, endOffset, parameters);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Collections.singletonList(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "short", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(mce, newParameters);

        // Assert
        assertEquals(0, changes.size());
    }

    @Test
    public void testMethodCallExpression_1ArgTo2Args1SubtypeArgs_1Change() {
        // Configure MethodCallExpression instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 1;
        int endOffset = 5;

        ParameterVariableStatement parameter = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int", false), Collections.emptyList()
        );
        parameter.setTypeInstance(new TypeInstance("java.lang.Integer", 0));
        List<ParameterVariableStatement> parameters = Collections.singletonList(parameter);

        MethodCallExpression mce = mockMethodCallExpression(path, lineNumber, startOffset, endOffset, parameters);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Arrays.asList(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "short", false),
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "String", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(mce, newParameters);

        // Assert
        RefactorChange expected = new RefactorChange(path, lineNumber, endOffset, endOffset, ", b");

        assertEquals(1, changes.size());
        assertEquals(expected, changes.get(0));
    }

    @Test
    public void testMethodStatement_1ArgTo1Arg_1Change() {
        // Configure MethodStatement instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 2;
        int endOffset = 5;

        ParameterVariableStatement parameter = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int", false), Collections.emptyList()
        );
        parameter.setTypeInstance(new TypeInstance("int", 0));
        List<ParameterVariableStatement> parameters = Collections.singletonList(parameter);

        MethodStatement ms = mockMethodStatement(path, lineNumber, startOffset, endOffset, parameters);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Collections.singletonList(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "String[]", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(ms, newParameters);

        // Assert
        RefactorChange expected = new RefactorChange(path, lineNumber, startOffset + 1, endOffset, "String[] a");
        assertEquals(1, changes.size());
        assertEquals(expected, changes.get(0));
    }

    @Test
    public void testMethodCallExpression_1ArgTo1Arg_1Change() {
        // Configure MethodCallExpression instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 2;
        int endOffset = 5;

        ParameterVariableStatement parameter = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int", false), Collections.emptyList()
        );
        parameter.setTypeInstance(new TypeInstance("int", 0));
        List<ParameterVariableStatement> parameters = Collections.singletonList(parameter);

        MethodCallExpression mce = mockMethodCallExpression(path, lineNumber, startOffset, endOffset, parameters);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Collections.singletonList(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "String[]", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(mce, newParameters);

        // Assert
        RefactorChange expected = new RefactorChange(path, lineNumber, startOffset + 1, endOffset, "a");
        assertEquals(1, changes.size());
        assertEquals(expected, changes.get(0));
    }

    @Test
    public void testMethodStatement_1ArgTo2Args_1Change() {
        // Configure MethodStatement instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 2;
        int endOffset = 5;

        ParameterVariableStatement parameter = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int", false), Collections.emptyList()
        );
        parameter.setTypeInstance(new TypeInstance("int", 0));
        List<ParameterVariableStatement> parameters = Collections.singletonList(parameter);

        MethodStatement ms = mockMethodStatement(path, lineNumber, startOffset, endOffset, parameters);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Arrays.asList(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int", false),
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "String", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(ms, newParameters);

        // Assert
        RefactorChange expected = new RefactorChange(path, lineNumber, endOffset, endOffset, ", String b");

        assertEquals(1, changes.size());
        assertEquals(expected, changes.get(0));
    }

    @Test
    public void testMethodCallExpression_1ArgTo2Args_1Change() {
        // Configure MethodCallExpression instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 2;
        int endOffset = 5;

        ParameterVariableStatement parameter = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int", false), Collections.emptyList()
        );
        parameter.setTypeInstance(new TypeInstance("int", 0));
        List<ParameterVariableStatement> parameters = Collections.singletonList(parameter);

        MethodCallExpression mce = mockMethodCallExpression(path, lineNumber, startOffset, endOffset, parameters);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Arrays.asList(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int", false),
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "String", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(mce, newParameters);

        // Assert
        RefactorChange expected = new RefactorChange(path, lineNumber, endOffset, endOffset, ", b");
        assertEquals(1, changes.size());
        assertEquals(expected, changes.get(0));
    }

    @Test
    public void testMethodStatement_1ArgTo2Args_2Changes() {
        // Configure MethodStatement instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 2;
        int endOffset = 5;

        ParameterVariableStatement parameter = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int", false), Collections.emptyList()
        );
        parameter.setTypeInstance(new TypeInstance("int", 0));
        List<ParameterVariableStatement> parameters = Collections.singletonList(parameter);

        MethodStatement ms = mockMethodStatement(path, lineNumber, startOffset, endOffset, parameters);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Arrays.asList(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "String[]", false),
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "String", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(ms, newParameters);

        // Assert
        List<RefactorChange> expected = Arrays.asList(
                new RefactorChange(path, lineNumber, startOffset + 1, endOffset, "String[] a"),
                new RefactorChange(path, lineNumber, endOffset, endOffset, ", String b")
        );

        TestUtils.assertEquals(expected, changes);
    }

    @Test
    public void testMethodCallExpression_1ArgTo2Args_2Changes() {
        // Configure MethodCallExpression instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 2;
        int endOffset = 5;

        ParameterVariableStatement parameter = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int", false), Collections.emptyList()
        );
        parameter.setTypeInstance(new TypeInstance("int", 0));
        List<ParameterVariableStatement> parameters = Collections.singletonList(parameter);

        MethodCallExpression mce = mockMethodCallExpression(path, lineNumber, startOffset, endOffset, parameters);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Arrays.asList(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "String[]", false),
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "String", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(mce, newParameters);

        // Assert
        List<RefactorChange> expected = Arrays.asList(
                new RefactorChange(path, lineNumber, startOffset + 1, endOffset, "a"),
                new RefactorChange(path, lineNumber, endOffset, endOffset, ", b")
        );

        TestUtils.assertEquals(expected, changes);
    }

    @Test
    public void testMethodStatement_1ArgTo3Args_2Changes() {
        // Configure MethodStatement instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 2;
        int endOffset = 5;

        ParameterVariableStatement parameter = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int", false), Collections.emptyList()
        );
        parameter.setTypeInstance(new TypeInstance("int", 0));
        List<ParameterVariableStatement> parameters = Collections.singletonList(parameter);

        MethodStatement ms = mockMethodStatement(path, lineNumber, startOffset, endOffset, parameters);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Arrays.asList(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "String[]", false),
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "String", false),
                new ParameterVariableDescriptor(new IdentifierName.Static("z"), "int", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(ms, newParameters);

        // Assert
        List<RefactorChange> expected = Arrays.asList(
                new RefactorChange(path, lineNumber, startOffset + 1, endOffset, "String[] a"),
                new RefactorChange(path, lineNumber, endOffset, endOffset, ", String b, int z")
        );

        TestUtils.assertEquals(expected, changes);
    }

    @Test
    public void testMethodCallExpression_1ArgTo3Args_2Changes() {
        // Configure MethodCallExpression instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 2;
        int endOffset = 5;

        ParameterVariableStatement parameter = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int", false), Collections.emptyList()
        );
        parameter.setTypeInstance(new TypeInstance("int", 0));
        List<ParameterVariableStatement> parameters = Collections.singletonList(parameter);

        MethodCallExpression mce = mockMethodCallExpression(path, lineNumber, startOffset, endOffset, parameters);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Arrays.asList(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "String[]", false),
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "String", false),
                new ParameterVariableDescriptor(new IdentifierName.Static("z"), "int", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(mce, newParameters);

        // Assert
        List<RefactorChange> expected = Arrays.asList(
                new RefactorChange(path, lineNumber, startOffset + 1, endOffset, "a"),
                new RefactorChange(path, lineNumber, endOffset, endOffset, ", b, z")
        );

        TestUtils.assertEquals(expected, changes);
    }

    @Test
    public void testMethodStatement_2ArgsToNoArgs_1Change() {
        // Configure MethodStatement instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 4;
        int endOffset = 8;
        int commaOffset = 6;

        ParameterVariableStatement param1 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("z"), "Class", false), Collections.emptyList()
        );
        param1.setTypeInstance(new TypeInstance("java.lang.Class", 1));
        ParameterVariableStatement param2 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "Runnable", false),
                Collections.emptyList()
        );
        param2.setTypeInstance(new TypeInstance("java.lang.Runnable", 0));
        List<ParameterVariableStatement> parameters = Arrays.asList(param1, param2);
        List<FilePosition> commaPositions = Collections.singletonList(new FilePosition(0, commaOffset));

        MethodStatement ms = mockMethodStatement(path, lineNumber, startOffset, endOffset, parameters, commaPositions);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Collections.emptyList();

        // Create change
        List<RefactorChange> changes = factory.changes(ms, newParameters);

        // Assert
        List<RefactorChange> expected = Arrays.asList(
                new RefactorChange(path, lineNumber, startOffset + 1, commaOffset, ""),
                new RefactorChange(path, lineNumber, commaOffset, endOffset, "")
        );

        TestUtils.assertEquals(expected, changes);
    }

    @Test
    public void testMethodCallExpression_2ArgsToNoArgs_1Change() {
        // Configure MethodCallExpression instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 4;
        int endOffset = 8;
        int commaOffset = 6;

        ParameterVariableStatement param1 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("z"), "Class", false), Collections.emptyList()
        );
        param1.setTypeInstance(new TypeInstance("java.lang.Class", 1));
        ParameterVariableStatement param2 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "Runnable", false),
                Collections.emptyList()
        );
        param2.setTypeInstance(new TypeInstance("java.lang.Runnable", 0));
        List<ParameterVariableStatement> parameters = Arrays.asList(param1, param2);
        List<FilePosition> commaPositions = Collections.singletonList(new FilePosition(0, commaOffset));

        MethodCallExpression mce = mockMethodCallExpression(path, lineNumber, startOffset, endOffset, parameters,
                commaPositions);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Collections.emptyList();

        // Create change
        List<RefactorChange> changes = factory.changes(mce, newParameters);

        // Assert
        List<RefactorChange> expected = Arrays.asList(
                new RefactorChange(path, lineNumber, startOffset + 1, commaOffset, ""),
                new RefactorChange(path, lineNumber, commaOffset, endOffset, "")
        );

        TestUtils.assertEquals(expected, changes);
    }

    @Test
    public void testMethodStatement_2ArgsTo1Arg_1Change() {
        // Configure MethodStatement instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 4;
        int endOffset = 8;
        int commaOffset = 6;

        ParameterVariableStatement param1 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("z"), "Class", false), Collections.emptyList()
        );
        param1.setTypeInstance(new TypeInstance("java.lang.Class", 0));
        ParameterVariableStatement param2 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "Runnable", false),
                Collections.emptyList()
        );
        param2.setTypeInstance(new TypeInstance("java.lang.Runnable", 0));
        List<ParameterVariableStatement> parameters = Arrays.asList(param1, param2);
        List<FilePosition> commaPositions = Collections.singletonList(new FilePosition(0, commaOffset));

        MethodStatement ms = mockMethodStatement(path, lineNumber, startOffset, endOffset, parameters, commaPositions);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Collections.singletonList(param1.getDescriptor());

        // Create change
        List<RefactorChange> changes = factory.changes(ms, newParameters);

        // Assert
        List<RefactorChange> expected = Collections.singletonList(
                new RefactorChange(path, lineNumber, commaOffset, endOffset, "")
        );

        TestUtils.assertEquals(expected, changes);
    }

    @Test
    public void testMethodCallExpression_2ArgsTo1Arg_1Change() {
        // Configure MethodCallExpression instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 4;
        int endOffset = 8;
        int commaOffset = 6;

        ParameterVariableStatement param1 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("z"), "Class", false), Collections.emptyList()
        );
        param1.setTypeInstance(new TypeInstance("java.lang.Class", 0));
        ParameterVariableStatement param2 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "Runnable", false),
                Collections.emptyList()
        );
        param2.setTypeInstance(new TypeInstance("java.lang.Runnable", 0));
        List<ParameterVariableStatement> parameters = Arrays.asList(param1, param2);
        List<FilePosition> commaPositions = Collections.singletonList(new FilePosition(0, commaOffset));

        MethodCallExpression mce = mockMethodCallExpression(path, lineNumber, startOffset, endOffset, parameters,
                commaPositions);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Collections.singletonList(param1.getDescriptor());

        // Create change
        List<RefactorChange> changes = factory.changes(mce, newParameters);

        // Assert
        List<RefactorChange> expected = Collections.singletonList(
                new RefactorChange(path, lineNumber, commaOffset, endOffset, "")
        );

        TestUtils.assertEquals(expected, changes);
    }

    @Test
    public void testMethodStatement_2ArgsToSameArgs_NoChange() {
        // Configure MethodStatement instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 4;
        int endOffset = 8;
        int commaOffset = 6;

        ParameterVariableStatement param1 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("z"), "Class", false), Collections.emptyList()
        );
        param1.setTypeInstance(new TypeInstance("java.lang.Class", 0));
        ParameterVariableStatement param2 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "Runnable", false),
                Collections.emptyList()
        );
        param2.setTypeInstance(new TypeInstance("java.lang.Runnable", 0));
        List<ParameterVariableStatement> parameters = Arrays.asList(param1, param2);
        List<FilePosition> commaPositions = Collections.singletonList(new FilePosition(0, commaOffset));

        MethodStatement ms = mockMethodStatement(path, lineNumber, startOffset, endOffset, parameters, commaPositions);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Arrays.asList(param1.getDescriptor(), param2.getDescriptor());

        // Create change
        List<RefactorChange> changes = factory.changes(ms, newParameters);

        // Assert
        assertEquals(0, changes.size());
    }

    @Test
    public void testMethodCallExpression_2ArgsToSameArgs_NoChange() {
        // Configure MethodCallExpression instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 4;
        int endOffset = 8;
        int commaOffset = 6;

        ParameterVariableStatement param1 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("z"), "Class", false), Collections.emptyList()
        );
        param1.setTypeInstance(new TypeInstance("java.lang.Class", 0));
        ParameterVariableStatement param2 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "Runnable", false),
                Collections.emptyList()
        );
        param2.setTypeInstance(new TypeInstance("java.lang.Runnable", 0));
        List<ParameterVariableStatement> parameters = Arrays.asList(param1, param2);
        List<FilePosition> commaPositions = Collections.singletonList(new FilePosition(0, commaOffset));

        MethodCallExpression mce = mockMethodCallExpression(path, lineNumber, startOffset, endOffset, parameters,
                commaPositions);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Arrays.asList(param1.getDescriptor(), param2.getDescriptor());

        // Create change
        List<RefactorChange> changes = factory.changes(mce, newParameters);

        // Assert
        assertEquals(0, changes.size());
    }
    
    @Test
    public void testMethodCallExpression_2ArgsTo1SubtypeArg_1Change() {
        // Configure MethodCallExpression instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 4;
        int endOffset = 8;
        int commaOffset = 6;

        ParameterVariableStatement param1 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("z"), "int", false), Collections.emptyList()
        );
        param1.setTypeInstance(new TypeInstance("java.lang.Integer", 0));
        ParameterVariableStatement param2 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "float", false),
                Collections.emptyList()
        );
        param2.setTypeInstance(new TypeInstance("java.lang.Float", 0));
        List<ParameterVariableStatement> parameters = Arrays.asList(param1, param2);
        List<FilePosition> commaPositions = Collections.singletonList(new FilePosition(0, commaOffset));

        MethodCallExpression mce = mockMethodCallExpression(path, lineNumber, startOffset, endOffset, parameters,
                commaPositions);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Collections.singletonList(
                new ParameterVariableDescriptor(new IdentifierName.Static("s"), "short", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(mce, newParameters);

        // Assert
        List<RefactorChange> expected = Collections.singletonList(
                new RefactorChange(path, lineNumber, commaOffset, endOffset, "")
        );

        TestUtils.assertEquals(expected, changes);
    }

    @Test
    public void testMethodCallExpression_2ArgsTo2SubtypeArgs_NoChange() {
        // Configure MethodCallExpression instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 4;
        int endOffset = 8;
        int commaOffset = 6;

        ParameterVariableStatement param1 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("z"), "int", false), Collections.emptyList()
        );
        param1.setTypeInstance(new TypeInstance("java.lang.Integer", 0));
        ParameterVariableStatement param2 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "float", false),
                Collections.emptyList()
        );
        param2.setTypeInstance(new TypeInstance("java.lang.Float", 0));
        List<ParameterVariableStatement> parameters = Arrays.asList(param1, param2);
        List<FilePosition> commaPositions = Collections.singletonList(new FilePosition(0, commaOffset));

        MethodCallExpression mce = mockMethodCallExpression(path, lineNumber, startOffset, endOffset, parameters,
                commaPositions);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Arrays.asList(
                new ParameterVariableDescriptor(new IdentifierName.Static("z"), "short", false),
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "long", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(mce, newParameters);

        // Assert
        assertEquals(0, changes.size());
    }

    @Test
    public void testMethodStatement_2ArgsTo2Args_1Change() {
        // Configure MethodStatement instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 4;
        int endOffset = 8;
        int commaOffset = 6;

        ParameterVariableStatement param1 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("z"), "Class", false), Collections.emptyList()
        );
        param1.setTypeInstance(new TypeInstance("java.lang.Class", 0));
        ParameterVariableStatement param2 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "Runnable", false),
                Collections.emptyList()
        );
        param2.setTypeInstance(new TypeInstance("java.lang.Runnable", 0));
        List<ParameterVariableStatement> parameters = Arrays.asList(param1, param2);
        List<FilePosition> commaPositions = Collections.singletonList(new FilePosition(0, commaOffset));

        MethodStatement ms = mockMethodStatement(path, lineNumber, startOffset, endOffset, parameters, commaPositions);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Arrays.asList(
                new ParameterVariableDescriptor(new IdentifierName.Static("i"), "int", false),
                param2.getDescriptor());

        // Create change
        List<RefactorChange> changes = factory.changes(ms, newParameters);

        // Assert
        List<RefactorChange> expected = Collections.singletonList(
                new RefactorChange(path, lineNumber, startOffset + 1, commaOffset, "int i")
        );

        TestUtils.assertEquals(expected, changes);
    }

    @Test
    public void testMethodCallExpression_2ArgsTo2Args_1Change() {
        // Configure MethodCallExpression instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 4;
        int endOffset = 8;
        int commaOffset = 6;

        ParameterVariableStatement param1 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("z"), "Class", false), Collections.emptyList()
        );
        param1.setTypeInstance(new TypeInstance("java.lang.Class", 0));
        ParameterVariableStatement param2 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "Runnable", false),
                Collections.emptyList()
        );
        param2.setTypeInstance(new TypeInstance("java.lang.Runnable", 0));
        List<ParameterVariableStatement> parameters = Arrays.asList(param1, param2);
        List<FilePosition> commaPositions = Collections.singletonList(new FilePosition(0, commaOffset));

        MethodCallExpression mce = mockMethodCallExpression(path, lineNumber, startOffset, endOffset, parameters,
                commaPositions);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Arrays.asList(
                new ParameterVariableDescriptor(new IdentifierName.Static("i"), "int", false),
                param2.getDescriptor());

        // Create change
        List<RefactorChange> changes = factory.changes(mce, newParameters);

        // Assert
        List<RefactorChange> expected = Collections.singletonList(
                new RefactorChange(path, lineNumber, startOffset + 1, commaOffset, "i")
        );

        TestUtils.assertEquals(expected, changes);
    }

    @Test
    public void testMethodStatement_2ArgsTo2Args_2Changes() {
        // Configure MethodStatement instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 4;
        int endOffset = 8;
        int commaOffset = 6;

        ParameterVariableStatement param1 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("z"), "Class", false), Collections.emptyList()
        );
        param1.setTypeInstance(new TypeInstance("java.lang.Class", 0));
        ParameterVariableStatement param2 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "Runnable", false),
                Collections.emptyList()
        );
        param2.setTypeInstance(new TypeInstance("java.lang.Runnable", 0));
        List<ParameterVariableStatement> parameters = Arrays.asList(param1, param2);
        List<FilePosition> commaPositions = Collections.singletonList(new FilePosition(0, commaOffset));

        MethodStatement ms = mockMethodStatement(path, lineNumber, startOffset, endOffset, parameters, commaPositions);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Arrays.asList(
                new ParameterVariableDescriptor(new IdentifierName.Static("i"), "int", false),
                new ParameterVariableDescriptor(new IdentifierName.Static("ai"), "AtomicInteger", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(ms, newParameters);

        // Assert
        List<RefactorChange> expected = Arrays.asList(
                new RefactorChange(path, lineNumber, startOffset + 1, commaOffset, "int i"),
                new RefactorChange(path, lineNumber, commaOffset + 1, endOffset, " AtomicInteger ai")
        );

        TestUtils.assertEquals(expected, changes);
    }

    @Test
    public void testMethodCallExpression_2ArgsTo2Args_2Changes() {
        // Configure MethodCallExpression instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 4;
        int endOffset = 8;
        int commaOffset = 6;

        ParameterVariableStatement param1 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("z"), "Class", false), Collections.emptyList()
        );
        param1.setTypeInstance(new TypeInstance("java.lang.Class", 0));
        ParameterVariableStatement param2 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "Runnable", false),
                Collections.emptyList()
        );
        param2.setTypeInstance(new TypeInstance("java.lang.Runnable", 0));
        List<ParameterVariableStatement> parameters = Arrays.asList(param1, param2);
        List<FilePosition> commaPositions = Collections.singletonList(new FilePosition(0, commaOffset));

        MethodCallExpression mce = mockMethodCallExpression(path, lineNumber, startOffset, endOffset, parameters,
                commaPositions);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Arrays.asList(
                new ParameterVariableDescriptor(new IdentifierName.Static("i"), "int", false),
                new ParameterVariableDescriptor(new IdentifierName.Static("ai"), "AtomicInteger", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(mce, newParameters);

        // Assert
        List<RefactorChange> expected = Arrays.asList(
                new RefactorChange(path, lineNumber, startOffset + 1, commaOffset, "i"),
                new RefactorChange(path, lineNumber, commaOffset + 1, endOffset, " ai")
        );

        TestUtils.assertEquals(expected, changes);
    }

    @Test
    public void testMethodStatement_2ArgsTo3Args_2Changes() {
        // Configure MethodStatement instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 4;
        int endOffset = 8;
        int commaOffset = 6;

        ParameterVariableStatement param1 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("z"), "Class", false), Collections.emptyList()
        );
        param1.setTypeInstance(new TypeInstance("java.lang.Class", 0));
        ParameterVariableStatement param2 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "Runnable", false),
                Collections.emptyList()
        );
        param2.setTypeInstance(new TypeInstance("java.lang.Runnable", 0));
        List<ParameterVariableStatement> parameters = Arrays.asList(param1, param2);
        List<FilePosition> commaPositions = Collections.singletonList(new FilePosition(0, commaOffset));

        MethodStatement ms = mockMethodStatement(path, lineNumber, startOffset, endOffset, parameters, commaPositions);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Arrays.asList(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int", false),
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "Runnable", false),
                new ParameterVariableDescriptor(new IdentifierName.Static("c"), "int[]", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(ms, newParameters);

        // Assert
        List<RefactorChange> expected = Arrays.asList(
                new RefactorChange(path, lineNumber, startOffset + 1, commaOffset, "int a"),
                new RefactorChange(path, lineNumber, endOffset, endOffset, ", int[] c")
        );

        TestUtils.assertEquals(expected, changes);
    }

    @Test
    public void testMethodCallExpression_2ArgsTo3Args_2Changes() {
        // Configure MethodCallExpression instance
        Path path = Paths.get(".");
        int lineNumber = 0;
        int startOffset = 4;
        int endOffset = 8;
        int commaOffset = 6;

        ParameterVariableStatement param1 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("z"), "Class", false), Collections.emptyList()
        );
        param1.setTypeInstance(new TypeInstance("java.lang.Class", 0));
        ParameterVariableStatement param2 = new ParameterVariableStatement(
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "Runnable", false),
                Collections.emptyList()
        );
        param2.setTypeInstance(new TypeInstance("java.lang.Runnable", 0));
        List<ParameterVariableStatement> parameters = Arrays.asList(param1, param2);
        List<FilePosition> commaPositions = Collections.singletonList(new FilePosition(0, commaOffset));

        MethodCallExpression mce = mockMethodCallExpression(path, lineNumber, startOffset, endOffset, parameters,
                commaPositions);

        // New parameters
        List<ParameterVariableDescriptor> newParameters = Arrays.asList(
                new ParameterVariableDescriptor(new IdentifierName.Static("a"), "int", false),
                new ParameterVariableDescriptor(new IdentifierName.Static("b"), "Runnable", false),
                new ParameterVariableDescriptor(new IdentifierName.Static("c"), "int[]", false)
        );

        // Create change
        List<RefactorChange> changes = factory.changes(mce, newParameters);

        // Assert
        List<RefactorChange> expected = Arrays.asList(
                new RefactorChange(path, lineNumber, startOffset + 1, commaOffset, "a"),
                new RefactorChange(path, lineNumber, endOffset, endOffset, ", c")
        );

        TestUtils.assertEquals(expected, changes);
    }

    @Test
    public void testMethodCallExpression_2ArgsTo3Args1SubtypeArgs_NoChange() {
        // TODO impl: subtype args
    }

    @Test
    public void testMethodCallExpression_2ArgsTo3Args2SubtypeArgs_NoChange() {
        // TODO impl: subtype args
    }

    private static MethodStatement mockMethodStatement(Path path, int lineNumber, int lParenOffset, int rParenOffset) {
        return mockMethodStatement(path, lineNumber, lParenOffset, rParenOffset, Collections.emptyList(),
                Collections.emptyList());
    }

    private static MethodStatement mockMethodStatement(Path path, int lineNumber, int lParenOffset, int rParenOffset,
            List<ParameterVariableStatement> parameters) {
        return mockMethodStatement(path, lineNumber, lParenOffset, rParenOffset, parameters, Collections.emptyList());
    }

    private static MethodStatement mockMethodStatement(Path path, int lineNumber, int lParenOffset, int rParenOffset,
            List<ParameterVariableStatement> parameters, List<FilePosition> commaPositions) {
        MethodStatement ms = mock(MethodStatement.class);
        when(ms.getPath()).thenReturn(path);
        when(ms.getIdentifierFilePosition()).thenReturn(new FilePosition(lineNumber, 0));
        when(ms.getLeftParenFilePosition()).thenReturn(new FilePosition(lineNumber, lParenOffset));
        when(ms.getRightParenFilePosition()).thenReturn(new FilePosition(lineNumber, rParenOffset));
        when(ms.getCommaFilePositions()).thenReturn(commaPositions);
        when(ms.getParameters()).thenReturn(parameters);
        return ms;
    }

    private static MethodCallExpression mockMethodCallExpression(Path path, int lineNumber, int startOffset,
            int endOffset) {
        return mockMethodCallExpression(path, lineNumber, startOffset, endOffset, Collections.emptyList(),
                Collections.emptyList());
    }

    private static MethodCallExpression mockMethodCallExpression(Path path, int lineNumber, int startOffset,
            int endOffset, List<ParameterVariableStatement> parameters) {
        return mockMethodCallExpression(path, lineNumber, startOffset, endOffset, parameters, Collections.emptyList());
    }

    private static MethodCallExpression mockMethodCallExpression(Path path, int lineNumber, int startOffset,
            int endOffset, List<ParameterVariableStatement> parameters, List<FilePosition> commaPositions) {
        MethodCallExpression mce = mock(MethodCallExpression.class);
        when(mce.getPath()).thenReturn(path);
        when(mce.getIdentifierFilePosition()).thenReturn(new FilePosition(lineNumber, 100));
        when(mce.getLeftParenFilePosition()).thenReturn(new FilePosition(lineNumber, startOffset));
        when(mce.getRightParenFilePosition()).thenReturn(new FilePosition(lineNumber, endOffset));
        when(mce.getCommaFilePositions()).thenReturn(commaPositions);
        when(mce.getArgumentTypes()).thenReturn(parameters.stream()
                .map(ParameterVariableStatement::getDescriptor)
                .map(p -> {
                    String type = TypeHelper.removeDimensions(p.getIdentifierType().get());
                    int dimensions = TypeHelper.dimensions(p.getIdentifierType().get());
                    return new TypeInstance(type, dimensions);
                })
                .collect(Collectors.toList())
        );
        return mce;
    }
}
