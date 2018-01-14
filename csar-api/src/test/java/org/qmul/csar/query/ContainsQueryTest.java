package org.qmul.csar.query;

import org.junit.Assert;
import org.junit.Test;
import org.qmul.csar.lang.descriptor.ClassDescriptor;

import java.util.Arrays;

public final class ContainsQueryTest {

    private static ContainsQuery create(ContainsQueryElement... elements) {
        return new ContainsQuery(Arrays.asList(elements));
    }

    private static ContainsQueryElement.TargetDescriptor dummyDescriptor() {
        TargetDescriptor desc = new TargetDescriptor(new ClassDescriptor.Builder("Clazz").build());
        return new ContainsQueryElement.TargetDescriptor(desc);
    }

    private static ContainsQueryElement.LogicalOperator operator(LogicalOperator operator) {
        return new ContainsQueryElement.LogicalOperator(operator);
    }

    @Test
    public void testValidateSingleDescriptor() {
        ContainsQuery query = create(dummyDescriptor());
        Assert.assertTrue(ContainsQuery.validate(query));
    }

    @Test
    public void testValidateInvertedDescriptor() {
        ContainsQuery query = create(operator(LogicalOperator.NOT), dummyDescriptor());
        Assert.assertTrue(ContainsQuery.validate(query));
    }

    @Test
    public void testValidateAndInvertedDescriptor() {
        ContainsQuery query = create(dummyDescriptor(), operator(LogicalOperator.AND), operator(LogicalOperator.NOT),
                dummyDescriptor());
        Assert.assertTrue(ContainsQuery.validate(query));
    }

    @Test
    public void testValidateAndInvertedDescriptor2() {
        ContainsQuery query = create(operator(LogicalOperator.NOT), dummyDescriptor(), operator(LogicalOperator.OR),
                operator(LogicalOperator.NOT), dummyDescriptor());
        Assert.assertTrue(ContainsQuery.validate(query));
    }

    @Test
    public void testInvalidValidateConsecutiveOperator1() {
        ContainsQuery query = create(dummyDescriptor(), operator(LogicalOperator.AND), operator(LogicalOperator.AND),
                dummyDescriptor());
        Assert.assertFalse(ContainsQuery.validate(query));
    }

    @Test
    public void testInvalidValidateConsecutiveOperator2() {
        ContainsQuery query = create(dummyDescriptor(), operator(LogicalOperator.AND), operator(LogicalOperator.OR),
                dummyDescriptor());
        Assert.assertFalse(ContainsQuery.validate(query));
    }

    @Test
    public void testInvalidValidateConsecutiveDescriptor1() {
        ContainsQuery query = create(dummyDescriptor(), operator(LogicalOperator.AND), operator(LogicalOperator.OR),
                dummyDescriptor());
        Assert.assertFalse(ContainsQuery.validate(query));
    }

    @Test
    public void testInvalidValidateConsecutiveDescriptor2() {
        ContainsQuery query = create(dummyDescriptor(), operator(LogicalOperator.AND), dummyDescriptor(),
                dummyDescriptor());
        Assert.assertFalse(ContainsQuery.validate(query));
    }

    @Test
    public void testInvalidValidateSingleOperator1() {
        ContainsQuery query = create(operator(LogicalOperator.AND));
        Assert.assertFalse(ContainsQuery.validate(query));
    }

    @Test
    public void testInvalidValidateSingleOperator2() {
        ContainsQuery query = create(operator(LogicalOperator.NOT));
        Assert.assertFalse(ContainsQuery.validate(query));
    }

    @Test
    public void testInvalidLeadingOperator() {
        ContainsQuery query = create(operator(LogicalOperator.AND), dummyDescriptor());
        Assert.assertFalse(ContainsQuery.validate(query));
    }
}