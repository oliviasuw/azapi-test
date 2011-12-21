/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author bennyl
 */
public class ServerError {
    public String type = "ServerError";
    public String name;
    public String description;

    public ServerError(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public ServerError(Exception ex){
        this.name = ex.getClass().getSimpleName();
        this.description = ex.getMessage();
    }
}
