package org.qmul.csar.code.postprocess;

import org.qmul.csar.code.parse.java.statement.ClassStatement;
import org.qmul.csar.code.parse.java.statement.EnumStatement;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.lang.descriptor.ClassDescriptor;
import org.qmul.csar.lang.descriptor.EnumDescriptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PostProcessUtils {

    public static List<String> superClasses(TypeStatement typeStatement) {
        if (typeStatement instanceof ClassStatement) {
            ClassStatement classStatement = (ClassStatement) typeStatement;
            ClassDescriptor descriptor = classStatement.getDescriptor();
            List<String> superClasses = new ArrayList<>();
            descriptor.getExtendedClass().ifPresent(superClasses::add);
            superClasses.addAll(descriptor.getImplementedInterfaces());
            return Collections.unmodifiableList(superClasses);
        } else if (typeStatement instanceof EnumStatement) {
            EnumStatement enumStatement = (EnumStatement) typeStatement;
            EnumDescriptor descriptor = enumStatement.getDescriptor();
            return Collections.unmodifiableList(descriptor.getSuperClasses());
        }
        return Collections.unmodifiableList(new ArrayList<>());
    }

    public static String extendedClass(TypeStatement typeStatement) {
        if (typeStatement instanceof ClassStatement) {
            ClassStatement classStatement = (ClassStatement) typeStatement;
            ClassDescriptor descriptor = classStatement.getDescriptor();
            return descriptor.getExtendedClass().orElse(null);
        }
        return null;
    }
}
