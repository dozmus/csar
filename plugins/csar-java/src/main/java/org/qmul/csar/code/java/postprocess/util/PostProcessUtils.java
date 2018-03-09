package org.qmul.csar.code.java.postprocess.util;

import org.qmul.csar.code.java.parse.statement.*;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.lang.descriptors.ClassDescriptor;
import org.qmul.csar.lang.descriptors.EnumDescriptor;
import org.qmul.csar.lang.descriptors.MethodDescriptor;
import org.qmul.csar.lang.descriptors.VisibilityModifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class PostProcessUtils {

    public static boolean isStaticTypeStatement(TypeStatement typeStatement) {
        if (typeStatement instanceof ClassStatement) {
            return ((ClassStatement)typeStatement).getDescriptor().getStaticModifier().orElse(false);
        }
        return true;
    }

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

    public static BlockStatement getBlock(TypeStatement type) {
        if (type instanceof ClassStatement) {
            return ((ClassStatement)type).getBlock();
        } else if (type instanceof EnumStatement) {
            return ((EnumStatement)type).getBlock();
        } else { // fall-back: annotation
            return ((AnnotationStatement)type).getBlock();
        }
    }

    public static String getIdentifierName(TypeStatement type) {
        if (type instanceof ClassStatement) {
            return ((ClassStatement)type).getDescriptor().getIdentifierName().toString();
        } else if (type instanceof EnumStatement) {
            return ((EnumStatement)type).getDescriptor().getIdentifierName().toString();
        } else { // fall-back: annotation
            return ((AnnotationStatement)type).getDescriptor().getIdentifierName().toString();
        }
    }

    /**
     *
     * @param isChildClass if the context it's being accessed in, is a child class to where the variable is.
     * @return
     */
    public static boolean isAccessible(VisibilityModifier variableVisibilityModifier, boolean isChildClass,
            Optional<PackageStatement> superPkg, Optional<PackageStatement> callerPkg) {
        // TODO make sure the stuff to do with accessibility is right: if no pkg then check folder instead
        if (variableVisibilityModifier == VisibilityModifier.PUBLIC)
            return true;
        if (variableVisibilityModifier == VisibilityModifier.PRIVATE)
            return false;
        if (variableVisibilityModifier == VisibilityModifier.PROTECTED)
            return isChildClass;

        // no modifier, check packages
        return (!superPkg.isPresent() && !callerPkg.isPresent()) || superPkg.equals(callerPkg);
    }
}
