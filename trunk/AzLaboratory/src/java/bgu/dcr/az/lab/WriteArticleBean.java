/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import javax.faces.bean.ManagedBean;

/**
 *
 * @author kdima85
 */
@ManagedBean(eager = true)
public class WriteArticleBean {

    String password;
    String title;
    String value;
    boolean submitted = false;

    public WriteArticleBean() {
    }

    public void handleSave() {

            System.out.println("second submit " + value);

    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
