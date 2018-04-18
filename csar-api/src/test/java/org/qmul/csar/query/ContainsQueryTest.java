package org.qmul.csar.query;

import org.junit.Assert;
import org.junit.Test;
import org.qmul.csar.lang.descriptors.ClassDescriptor;

import java.util.Arrays;

public final class ContainsQueryTest {

    /**
     * Returns a {@link ContainsQuery} containing the argument elements.
     */
    private static ContainsQuery create(ContainsQueryElement... elements) {
        return new ContainsQuery(Arrays.asList(elements));
    }

    /**
     * Returns an arbitrary valid descriptor.
     */
    private static ContainsQueryElement.TargetDescriptor dummyDescriptor() {
        TargetDescriptor desc = new TargetDescriptor(new ClassDescriptor.Builder("Clazz").build());
        return new ContainsQueryElement.TargetDescriptor(desc);
    }

    private static ContainsQueryElement.LogicalOperator andOperator() {
        return new ContainsQueryElement.LogicalOperator(LogicalOperator.AND);
    }

    private static ContainsQueryElement.LogicalOperator orOperator() {
        return new ContainsQueryElement.LogicalOperator(LogicalOperator.OR);
    }

    private static ContainsQueryElement.LogicalOperator notOperator() {
        return new ContainsQueryElement.LogicalOperator(LogicalOperator.NOT);
    }

    @Test
    public void testValidateDescriptor() {
        ContainsQuery query = create(dummyDescriptor());
        Assert.assertTrue(ContainsQuery.validate(query));
    }

    @Test
    public void testValidateInvertedDescriptor() {
        ContainsQuery query = create(notOperator(), dummyDescriptor());
        Assert.assertTrue(ContainsQuery.validate(query));
    }

    @Test
    public void testValidateCombinedDescriptors1() {
        ContainsQuery query = create(dummyDescriptor(), andOperator(), notOperator(), dummyDescriptor());
        Assert.assertTrue(ContainsQuery.validate(query));
    }

    @Test
    public void testValidateCombinedDescriptors2() {
        ContainsQuery query = create(notOperator(), dummyDescriptor(), orOperator(), notOperator(), dummyDescriptor());
        Assert.assertTrue(ContainsQuery.validate(query));
    }

    @Test
    public void testValidateInvalidConsecutiveOperators1() {
        ContainsQuery query = create(dummyDescriptor(), andOperator(), andOperator(), dummyDescriptor());
        Assert.assertFalse(ContainsQuery.validate(query));
    }

    @Test
    public void testValidateInvalidConsecutiveOperators2() {
        ContainsQuery query = create(dummyDescriptor(), andOperator(), orOperator(), dummyDescriptor());
        Assert.assertFalse(ContainsQuery.validate(query));
    }

    @Test
    public void testValidateInvalidConsecutiveDescriptors1() {
        ContainsQuery query = create(dummyDescriptor(), andOperator(), orOperator(), dummyDescriptor());
        Assert.assertFalse(ContainsQuery.validate(query));
    }

    @Test
    public void testValidateInvalidConsecutiveDescriptors2() {
        ContainsQuery query = create(dummyDescriptor(), andOperator(), dummyDescriptor(), dummyDescriptor());
        Assert.assertFalse(ContainsQuery.validate(query));
    }

    @Test
    public void testValidateInvalidSingleOperator1() {
        ContainsQuery query = create(andOperator());
        Assert.assertFalse(ContainsQuery.validate(query));
    }

    @Test
    public void testValidateInvalidSingleOperator2() {
        ContainsQuery query = create(notOperator());
        Assert.assertFalse(ContainsQuery.validate(query));
    }

    @Test
    public void testValidateInvalidLeadingOperator() {
        ContainsQuery query = create(andOperator(), dummyDescriptor());
        Assert.assertFalse(ContainsQuery.validate(query));
    }
}