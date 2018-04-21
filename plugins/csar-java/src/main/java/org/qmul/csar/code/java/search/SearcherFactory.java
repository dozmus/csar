package org.qmul.csar.code.java.search;

import org.qmul.csar.lang.Descriptor;
import org.qmul.csar.lang.descriptors.MethodDescriptor;
import org.qmul.csar.query.SearchType;
import org.qmul.csar.query.TargetDescriptor;

/**
 * A searcher factory.
 */
public final class SearcherFactory {

    private SearcherFactory() {
    }

    /**
     * Returns a searcher supporting the argument descriptor.
     *
     * @throws IllegalArgumentException if the argument descriptor is not supported
     */
    public static Searcher create(TargetDescriptor targetDescriptor) {
        SearchType searchType = targetDescriptor.getSearchType().orElse(null);
        Descriptor target = targetDescriptor.getDescriptor();

        if (target instanceof MethodDescriptor) {
            // Get results for file
            if (searchType == SearchType.DEF) {
                return new MethodDefinitionSearcher();
            } else if (searchType == SearchType.USE){
                return new MethodUseSearcher();
            }
        }
        throw new IllegalArgumentException("unsupported search target: " + target.getClass().getName());
    }
}

