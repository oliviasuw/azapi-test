/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.beans;

/**
 *
 * @author Inka
 */
public class RowCode {
    private Integer id;
    private String codeName;
    private String type;

    public RowCode(Integer id, String code, String type) {
        this.id = id;
        this.codeName = code;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getType() {
        return type;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCodeName(String code) {
        this.codeName = code;
    }

    public void setType(String type) {
        this.type = type;
    }
    
}
