/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import java.io.File;
import java.util.Date;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

/**
 *
 * @author Inna
 */
@ManagedBean(eager = true)
@ApplicationScoped
public class Search {

    ExperimentResult[] results = {new ExperimentResult("An amzing popular experiment result", "result1", new Date()),
            new ExperimentResult("Another popular experiment result", "result2", new Date()),
            new ExperimentResult("Last popular experiment result", "result3", new Date())};

    public Search() {
    }

    public ExperimentResult[] getResults() {
        return results;
    }
    
    

    public String recent() {
        ExperimentResult[] temp = {new ExperimentResult("An amzing new experiment result", "result1", new Date()),
            new ExperimentResult("Another new experiment result", "result2", new Date()),
            new ExperimentResult("Last new experiment result", "result3", new Date())};
        results = temp;
        return "News";
    }

    public void tags() {
        ExperimentResult[] temp = {new ExperimentResult("DPOP experiment result", "result1", new Date()),
            new ExperimentResult("Another DPOP experiment result", "result2", new Date()),
            new ExperimentResult("Last DPOP experiment result", "result3", new Date())};
        results = temp;
    }

    public void comments() {
        ExperimentResult[] temp = {new ExperimentResult("An amzing experiment result", "result1", new Date()),
            new ExperimentResult("Another experiment result", "result2", new Date()),
            new ExperimentResult("Last experiment result", "result3", new Date())};
        results = temp;
    }

    public void popular() {
        ExperimentResult[] temp = {new ExperimentResult("An amzing popular experiment result", "result1", new Date()),
            new ExperimentResult("Another popular experiment result", "result2", new Date()),
            new ExperimentResult("Last popular experiment result", "result3", new Date())};
        results = temp;
    }
}