package org.qmul.csar.code.postprocess;

import org.qmul.csar.code.parse.java.statement.ClassStatement;
import org.qmul.csar.code.parse.java.statement.EnumStatement;
import org.qmul.csar.code.parse.java.statement.PackageStatement;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.lang.descriptor.ClassDescriptor;
import org.qmul.csar.lang.descriptor.EnumDescriptor;
import org.qmul.csar.lang.descriptor.MethodDescriptor;
import org.qmul.csar.lang.descriptor.VisibilityModifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    /**
     * Returns if the given child method descriptor can access the given super method descriptor.
     *
     * @param childDesc
     * @param superDesc
     * @param childPkg
     * @param superPkg
     * @param superType
     * @return
     */
    public static boolean isAccessible(MethodDescriptor childDesc, MethodDescriptor superDesc,
            Optional<PackageStatement> childPkg, Optional<PackageStatement> superPkg, TypeStatement superType) {
        // is the super class an interface
        boolean isSuperInterface = false;

        if (superType instanceof ClassStatement) {
            isSuperInterface = ((ClassStatement) superType).getDescriptor().getInterfaceModifier().orElse(false);
        }

        // compute result
        VisibilityModifier childVis = childDesc.getVisibilityModifier().get();
        VisibilityModifier superVis = superDesc.getVisibilityModifier().get();

        if (isSuperInterface) {
            return childVis == VisibilityModifier.PUBLIC;
        } else {
            if (superVis == VisibilityModifier.PUBLIC && childVis == VisibilityModifier.PUBLIC) {
                return true;
            } else if (superVis == VisibilityModifier.PROTECTED
                    && (childVis == VisibilityModifier.PROTECTED || childVis == VisibilityModifier.PUBLIC)) {
                return true;
            } else if (superVis == VisibilityModifier.PACKAGE_PRIVATE
                    && childPkg.isPresent() && childPkg.equals(superPkg) && childVis != VisibilityModifier.PRIVATE) {
                return true;
            }
        }
        return false;
    }
}
