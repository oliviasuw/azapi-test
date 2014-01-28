/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.orm.api;

import java.util.Iterator;

/**
 * represents a collection of records
 *
 * @author Benny Lutati
 */
public interface Data extends Iterator<RecordAccessor> {

    /**
     * @return the amount of records that is stored in this data
     */
    int numRecords();

    /**
     * @return the records columns metadata
     */
    FieldMetadata[] columns();

    /**
     * return an object that allow to query the i'th record in this data
     *
     * @param i
     * @return
     */
    RecordAccessor getRecord(int i);

}
