package org.qmul.csar.lang.descriptor;

import org.qmul.csar.lang.SerializableCode;
import org.qmul.csar.util.StringUtils;

public enum VisibilityModifier implements SerializableCode {
    PUBLIC {
        @Override
        public String toPseudoCode(int indentation) {
            return StringUtils.indentation(indentation) + "public";
        }
    },
    PRIVATE {
        @Override
        public String toPseudoCode(int indentation) {
            return StringUtils.indentation(indentation) + "private";
        }
    },
    PROTECTED {
        @Override
        public String toPseudoCode(int indentation) {
            return StringUtils.indentation(indentation) + "protected";
        }
    },
    PACKAGE_PRIVATE {
        @Override
        public String toPseudoCode(int indentation) {
            return StringUtils.indentation(indentation) + "";
        }
    }
}
