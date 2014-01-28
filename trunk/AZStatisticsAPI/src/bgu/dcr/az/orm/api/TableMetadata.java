/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.orm.api;

/**
 *
 * @author User
 */
public interface TableMetadata {

    String name();

    FieldMetadata[] fields();
}
