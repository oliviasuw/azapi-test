/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import bgu.dcr.az.lab.data.Users;
import bgu.dcr.az.lab.db.DBManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author kdima85
 */
@ManagedBean(eager = true)
public class RegisteryBean {

    String name;
    String password;
    String email;

    public String handleRegistry() {
        if (validateDetails()) {
            DBManager.UNIT.registerNewUser(new Users(email, password, name, new byte[0], false));
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sample info message", "PrimeFaces rocks!"));
            return "Laboratory";
        }
        else{
             FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Wrong details, please try again!", ""));  
             return "Register";
        }
        

    }

    public void handleFileUpload(FileUploadEvent event) {
//        System.out.println("BLAAAAAA");
        UploadedFile file = event.getFile();
        System.out.println(file.getSize());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private boolean validateDetails() {
        Pattern p = Pattern.compile("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$");

        //Match the given string with the pattern
        Matcher m = p.matcher(email);

        //Check whether match is found
        boolean matchFound = m.matches();

        if (!matchFound) {
            return false;
        }
        System.out.println("good email!!!");
        return true;
    }
}
