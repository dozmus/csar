package org.qmul.csar.code;

import com.github.dozmus.iterators.ConcurrentIterator;
import org.qmul.csar.lang.Statement;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * A code base.
 */
public class CodeBase implements Iterable<Map.Entry<Path, Statement>> {

    /**
     * The internal code base, represented as a mapping of source files to their content.
     */
    private final Map<Path, Statement> code;

    public CodeBase(Map<Path, Statement> code) {
        this.code = code;
    }

    @Override
    public Iterator<Map.Entry<Path, Statement>> iterator() {
        return code.entrySet().iterator();
    }

    /**
     * Returns a thread-safe iterator, which is a wrapper of {@link #iterator()}.
     */
    public Iterator<Map.Entry<Path, Statement>> threadSafeIterator() {
        return new ConcurrentIterator<>(iterator());
    }

    public int size() {
        return code.size();
    }

    public Statement get(Path path) {
        return code.get(path);
    }

    public void forEach(BiConsumer<? super Path, ? super Statement> action) {
        code.forEach(action);
    }
}
