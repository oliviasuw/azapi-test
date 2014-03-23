/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.visitors;

import bgu.dcr.az.anop.utils.ProcessorUtils;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.SimpleTypeVisitor7;

/**
 * used for identification of type (mainly for debugging)
 *
 * @author User
 */
public class PrintingTypeVisitor extends SimpleTypeVisitor7 {

    @Override
    public Object visitUnion(UnionType t, Object p) {
        ProcessorUtils.note("found 'Union' type: " + t);
        return null;
    }

    @Override
    public Object visitNoType(NoType t, Object p) {
        ProcessorUtils.note("found 'NoType' type: " + t);
        return null;
    }

    @Override
    public Object visitExecutable(ExecutableType t, Object p) {
        ProcessorUtils.note("found 'Executable' type: " + t);
        return null;
    }

    @Override
    public Object visitWildcard(WildcardType t, Object p) {
        ProcessorUtils.note("found 'Wildcard' type: " + t);
        return null;
    }

    @Override
    public Object visitTypeVariable(TypeVariable t, Object p) {
        ProcessorUtils.note("found 'TypeVariable' type: " + t);
        return null;
    }

    @Override
    public Object visitError(ErrorType t, Object p) {
        ProcessorUtils.note("found 'Error' type: " + t);
        return null;
    }

    @Override
    public Object visitDeclared(DeclaredType t, Object p) {
        ProcessorUtils.note("found 'Declared' type: " + t);
        return null;
    }

    @Override
    public Object visitArray(ArrayType t, Object p) {
        ProcessorUtils.note("found 'Array' type: " + t);
        return null;
    }

    @Override
    public Object visitNull(NullType t, Object p) {
        ProcessorUtils.note("found 'Null' type: " + t);
        return null;
    }

    @Override
    public Object visitPrimitive(PrimitiveType t, Object p) {
        ProcessorUtils.note("found 'Primitive' type: " + t);
        return null;
    }

    @Override
    public Object visitUnknown(TypeMirror t, Object p) {
        ProcessorUtils.note("found 'Unknown' type: " + t);
        return null;
    }

}
