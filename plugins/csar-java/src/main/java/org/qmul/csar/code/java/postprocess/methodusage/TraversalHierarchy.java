package org.qmul.csar.code.java.postprocess.methodusage;

import org.qmul.csar.code.java.parse.statement.*;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;

import java.util.*;

public class TraversalHierarchy extends ArrayDeque<Statement> {

    public TypeStatement getFirstTypeStatement() {
        return getFirstTypeStatement(iterator());
    }

    public TypeStatement getLastTypeStatement() {
        return getFirstTypeStatement(descendingIterator());
    }

    private TypeStatement getFirstTypeStatement(Iterator<Statement> it) {
        while (it.hasNext()) {
            Statement item = it.next();

            if (item instanceof TypeStatement) {
                return (TypeStatement)item;
            }
        }
        throw new NoSuchElementException();
    }

    public boolean isCurrentContextStatic() {
        Iterator<Statement> it = descendingIterator();

        while (it.hasNext()) {
            Statement item = it.next();

            if (item instanceof StaticBlockStatement) {
                return true;
            } else if (item instanceof MethodStatement) {
                return ((MethodStatement)item).getDescriptor().getStaticModifier().get();
            } else if (item instanceof ConstructorStatement) {
                return false;
            } else if (item instanceof TypeStatement) {
                return false;
            }
        }
        return false;
    }

    public BlockStatement currentContext() {
        Iterator<Statement> it = descendingIterator();

        while (it.hasNext()) {
            Statement item = it.next();

            if (item instanceof StaticBlockStatement) {
                return ((StaticBlockStatement)item).getBlock();
            } else if (item instanceof MethodStatement) {
                return ((MethodStatement)item).getBlock();
            } else if (item instanceof ConstructorStatement) {
                return ((ConstructorStatement)item).getBlock();
            } else if (item instanceof TypeStatement) {
                if (item instanceof ClassStatement) {
                    return ((ClassStatement)item).getBlock();
                } else if (item instanceof EnumStatement) {
                    return ((EnumStatement)item).getBlock();
                } else if (item instanceof AnnotationStatement) {
                    return ((AnnotationStatement)item).getBlock();
                }
            }
        }
        throw new NoSuchElementException();
    }

    public List<ParameterVariableStatement> currentContextParameters() {
        Iterator<Statement> it = descendingIterator();

        while (it.hasNext()) {
            Statement item = it.next();

            if (item instanceof MethodStatement) {
                return ((MethodStatement)item).getParameters();
            } else if (item instanceof ConstructorStatement) {
                return ((ConstructorStatement)item).getParameters();
            }
        }
        return new ArrayList<>();
    }

    public List<ImportStatement> getImports() {
        Iterator<Statement> it = iterator();

        while (it.hasNext()) {
            Statement item = it.next();

            if (item instanceof TopLevelTypeStatement) {
                return ((TopLevelTypeStatement)item).getImports();
            }
        }
        throw new NoSuchElementException();
    }

    public Optional<PackageStatement> getPackageStatement() {
        Iterator<Statement> it = iterator();

        while (it.hasNext()) {
            Statement item = it.next();

            if (item instanceof TopLevelTypeStatement) {
                return ((TopLevelTypeStatement)item).getPackageStatement();
            }
        }
        throw new NoSuchElementException();
    }
}
