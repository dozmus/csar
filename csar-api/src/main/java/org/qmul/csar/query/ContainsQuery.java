package org.qmul.csar.query;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ContainsQuery {

    private final List<ContainsQueryElement> elements;

    /**
     * Creates a new instance of <tt>ContainsQuery</tt> with the argument elements.
     *
     * @param elements the elements this instance will contain
     */
    public ContainsQuery(List<ContainsQueryElement> elements) {
        this.elements = elements;
    }

    /**
     * Returns whether the argument is valid or not. It is valid if the following three criterion hold for its elements:
     * <ol>
     *     <li>No consecutive descriptors.</li>
     *     <li>No consecutive logical operators (unless if <tt>AND</tt> or <tt>OR</tt>, followed by <tt>NOT</tt>).</li>
     *     <li>The only connective operators are <tt>AND</tt> and <tt>OR</tt>.</li>
     *     <li>First element is not <tt>AND</tt> or <tt>OR</tt>.</li>
     *     <li>Last element is not <tt>AND</tt>, <tt>NOT</tt> or <tt>OR</tt>.</li>
     * </ol>
     *
     * @param containsQuery the query whose elements to validate
     * @return if the argument is valid
     */
    public static boolean validate(ContainsQuery containsQuery) {
        List<ContainsQueryElement> elements = containsQuery.getElements();
        int n = elements.size();

        // No elements
        if (n == 0)
            return true;

        // First element
        if (elements.get(0) instanceof ContainsQueryElement.LogicalOperator) {
            ContainsQueryElement.LogicalOperator op = ((ContainsQueryElement.LogicalOperator) elements.get(0));

            if (op.getLogicalOperator() != LogicalOperator.NOT) {
                return false;
            }
        }

        // Last element
        if (elements.get(n - 1) instanceof ContainsQueryElement.LogicalOperator) {
            return false;
        }

        // Body
        for (int i = 0; i < n; i++) {
            ContainsQueryElement currentElement = elements.get(i);

            if (i + 1 >= n) // has no next element
                continue;
            ContainsQueryElement nextElement = elements.get(i + 1);

            // Consecutive descriptors
            if (currentElement instanceof ContainsQueryElement.TargetDescriptor
                    && nextElement instanceof ContainsQueryElement.TargetDescriptor) {
                return false;
            }

            // Consecutive logical operators
            if (currentElement instanceof ContainsQueryElement.LogicalOperator
                    && nextElement instanceof ContainsQueryElement.LogicalOperator) {
                LogicalOperator curOp = ((ContainsQueryElement.LogicalOperator) currentElement).getLogicalOperator();
                LogicalOperator nextOp = ((ContainsQueryElement.LogicalOperator) nextElement).getLogicalOperator();

                if (curOp == LogicalOperator.NOT || nextOp != LogicalOperator.NOT) {
                    return false;
                }
            }

            // Illegal connective logical operator
            if (i + 2 >= n) // has no two next elements
                continue;
            ContainsQueryElement e2 = elements.get(i + 2);

            if (currentElement instanceof ContainsQueryElement.TargetDescriptor
                    && nextElement instanceof ContainsQueryElement.LogicalOperator
                    && e2 instanceof ContainsQueryElement.TargetDescriptor) {
                LogicalOperator curOp = ((ContainsQueryElement.LogicalOperator) nextElement).getLogicalOperator();

                if (curOp == LogicalOperator.NOT) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<ContainsQueryElement> getElements() {
        return elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContainsQuery that = (ContainsQuery) o;
        return Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("elements", elements)
                .toString();
    }

    public static class Builder {

        private final List<ContainsQueryElement> elements = new ArrayList<>();

        public Builder addLogicalOperator(LogicalOperator operator) {
            return add(new ContainsQueryElement.LogicalOperator(operator));
        }

        public Builder addTargetDescriptor(TargetDescriptor element) {
            return add(new ContainsQueryElement.TargetDescriptor(element));
        }

        public Builder add(ContainsQueryElement element) {
            elements.add(element);
            return this;
        }

        public Builder addAll(Collection<ContainsQueryElement> elements) {
            this.elements.addAll(elements);
            return this;
        }

        public ContainsQuery build() {
            return new ContainsQuery(elements);
        }
    }
}
