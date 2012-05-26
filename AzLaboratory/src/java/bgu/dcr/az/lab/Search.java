/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import java.io.File;
import java.util.Date;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Inna
 */
@ManagedBean(eager = true)
@SessionScoped
public class Search {
    
    private ExperimentResult viewed = new ExperimentResult("An amzing popular experiment result", "result1", new Date());
    
    
    ExperimentResult[] results = {new ExperimentResult("An amzing popular experiment result", "result1", new Date()),
            new ExperimentResult("Another popular experiment result", "result2", new Date()),
            new ExperimentResult("Last popular experiment result", "result3", new Date())};

    private String watchingNow = "popular";
    
    public Search() {
    }

    public ExperimentResult getViewed() {
        return viewed;
    }
    
    

    public ExperimentResult[] getResults() {
        return results;
    }  

    public void recent() {
        ExperimentResult[] temp = {new ExperimentResult("An amzing new experiment result", "result1", new Date()),
            new ExperimentResult("Another new experiment result", "result2", new Date()),
            new ExperimentResult("Last new experiment result", "result3", new Date())};
        results = temp;
        watchingNow = "recent";
//        return "News";
    }

    public void tags() {
        ExperimentResult[] temp = {new ExperimentResult("DPOP experiment result", "result1", new Date()),
            new ExperimentResult("Another DPOP experiment result", "result2", new Date()),
            new ExperimentResult("Last DPOP experiment result", "result3", new Date())};
        results = temp;
        watchingNow = "tags";
//        return "News";
    }

    public void comments() {
        ExperimentResult[] temp = {new ExperimentResult("An amzing experiment result", "result1", new Date()),
            new ExperimentResult("Another experiment result", "result2", new Date()),
            new ExperimentResult("Last experiment result", "result3", new Date())};
        results = temp;
        watchingNow = "comments";
//        return "News";
    }

    public void popular() {
        ExperimentResult[] temp = {new ExperimentResult("An amzing popular experiment result", "result1", new Date()),
            new ExperimentResult("Another popular experiment result", "result2", new Date()),
            new ExperimentResult("Last popular experiment result", "result3", new Date())};
        results = temp;
        watchingNow = "popular";
//        return "News";
    }

    public String getWatchingNow() {
        return watchingNow;
    }
    
    public void changeViewed(int id){
        System.out.println("Id is" + id);
        viewed = findById(id);
    }
    
    public ExperimentResult findById(int id){
        for(int i=0; i<results.length; i++){
            if (results[i].id == id) return results[i];
        }
        return viewed;
    }
    
}