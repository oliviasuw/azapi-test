/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model;

/**
 *
 * @author User
 */
public interface Table {
    Object [][] getColumnHeaders();
    
    Object [][] getRowHeaders();
    
    Object getCell(int column, int row);
}
