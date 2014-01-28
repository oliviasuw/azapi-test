/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.orm.impl;

import bgu.dcr.az.orm.api.FieldMetadata;
import bgu.dcr.az.orm.api.TableMetadata;
import bgu.dcr.az.stat.util.SQLUtils;

/**
 *
 * @author User
 */
public class FieldMetadataImpl implements FieldMetadata {

    private String name;
    private Class type;

    public FieldMetadataImpl(String name, Class type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Class type() {
        return type;
    }

    public static FieldMetadataImpl fromSQLData(String name, int dataType) {
        return new FieldMetadataImpl(name, SQLUtils.sqlTypeToClass(dataType));
    }
}
