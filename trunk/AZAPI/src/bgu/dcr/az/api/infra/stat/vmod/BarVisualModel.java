/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.infra.stat.vmod;

import bgu.dcr.az.api.infra.stat.VisualModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public class BarVisualModel implements VisualModel{
    private Map<String, Double> values = new LinkedHashMap<String, Double>();
    private String title;

    public BarVisualModel(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
    
    public void putBar(String barName, double value){
        values.put(barName, value);
    }

    public Map<String, Double> getValues() {
        return values;
    }
    
    @Override
    public void exportToCSV(File csv) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(csv);
            pw.println("name, value");
            for (Entry<String, Double> v : getValues().entrySet()) {
                pw.println("" + v.getKey() + ", " + v.getValue());
            }
            pw.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LineVisualModel.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            pw.close();
        }
    }
    
}
