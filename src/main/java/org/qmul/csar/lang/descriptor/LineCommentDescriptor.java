package org.qmul.csar.lang.descriptor;

import java.util.Optional;

public class LineCommentDescriptor extends AbstractCommentDescriptor {

    public LineCommentDescriptor(Optional<String> content) {
        super(content);
    }
}
