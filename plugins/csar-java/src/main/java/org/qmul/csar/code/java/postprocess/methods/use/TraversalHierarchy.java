package org.qmul.csar.code.java.postprocess.methods.use;

import org.qmul.csar.code.java.parse.statement.*;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;

import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * Returns all {@link TypeStatement}.
     * This is in order from ascending 'closeness' to the current context.
     */
    public List<TypeStatement> typeStatements() {
        List<TypeStatement> types = new ArrayList<>();
        Iterator<Statement> it = descendingIterator();
        while (it.hasNext()) {
            Statement item = it.next();

            if (item instanceof TypeStatement) {
                types.add((TypeStatement)item);
            }
        }
        return types;
    }

    public boolean isCurrentLocalContextStatic() {
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

    /**
     * Returns all {@link MethodStatement} and {@link ConstructorStatement} parameters visible in the current context.
     * This is in order from ascending 'closeness' to the current context.
     */
    public List<ParameterVariableStatement> currentContextParameters() {
        Iterator<Statement> it = descendingIterator();
        List<ParameterVariableStatement> parameters = new ArrayList<>();

        while (it.hasNext()) {
            Statement item = it.next();

            if (item instanceof MethodStatement) {
                parameters.addAll(((MethodStatement)item).getParameters());
            } else if (item instanceof ConstructorStatement) {
                parameters.addAll(((ConstructorStatement)item).getParameters());
            }
        }
        return parameters;
    }

    /**
     * Returns all local variables visible in the current context, this includes ones from for statements, etc.
     * This is in order from ascending 'closeness' to the current context.
     */
    public List<LocalVariableStatements> currentContextLocalVariables() {
        Iterator<Statement> it = descendingIterator();
        List<LocalVariableStatements> locals = new ArrayList<>();
        Statement previous = null, current;

        while (it.hasNext()) {
            current = it.next();

            if (current instanceof StaticBlockStatement) {
                locals.addAll(localVariables(((StaticBlockStatement)current).getBlock()));
            } else if (current instanceof MethodStatement) {
                locals.addAll(localVariables(((MethodStatement)current).getBlock()));
            } else if (current instanceof ConstructorStatement) {
                locals.addAll(localVariables(((ConstructorStatement)current).getBlock()));
            } else if (current instanceof ForStatement) {
                ((ForStatement)current).getInitVariables().ifPresent(locals::add);
            } else if (current instanceof ForEachStatement) {
                locals.add(new LocalVariableStatements(Arrays.asList(((ForEachStatement)current).getVariable())));
            } else if (current instanceof TryWithResourcesStatement) {
                TryWithResourcesStatement twr = (TryWithResourcesStatement)current;

                if (twr.getBlock().equals(previous)) { // in main body
                    locals.add(twr.getResources());
                } else { // in one of the catch statements
                    for (CatchStatement c : twr.getCatches()) {
                        if (c.equals(previous)) {
                            locals.add(c.getVariable());
                        }
                    }
                }
            } else if (current instanceof TryStatement) {
                TryStatement twr = (TryStatement)current;

                // We may be in one of the catch statements
                for (CatchStatement c : twr.getCatches()) {
                    if (c.equals(previous)) {
                        locals.add(c.getVariable());
                    }
                }
            }
            previous = current;
        }
        return locals;
    }

    private static List<LocalVariableStatements> localVariables(BlockStatement block) {
        return block.getStatements().stream()
                .filter(s -> s instanceof LocalVariableStatements)
                .map(s -> (LocalVariableStatements)s)
                .collect(Collectors.toList());
    }

    public List<ImportStatement> getImports() {
        Iterator<Statement> it = iterator();

        while (it.hasNext()) {
            Statement item = it.next();

            if (item instanceof CompilationUnitStatement) {
                return ((CompilationUnitStatement)item).getImports();
            }
        }
        throw new NoSuchElementException();
    }

    public Optional<PackageStatement> getPackageStatement() {
        Iterator<Statement> it = iterator();

        while (it.hasNext()) {
            Statement item = it.next();

            if (item instanceof CompilationUnitStatement) {
                return ((CompilationUnitStatement)item).getPackageStatement();
            }
        }
        throw new NoSuchElementException();
    }
}
