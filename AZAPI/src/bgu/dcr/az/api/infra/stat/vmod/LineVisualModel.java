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
public class LineVisualModel implements VisualModel{
    String xAxisName;
    String yAxisName;
    String title;
    Map<Double, Double> values = new LinkedHashMap<Double, Double>();

    public LineVisualModel(String xAxisName, String yAxisName, String title) {
        this.xAxisName = xAxisName;
        this.yAxisName = yAxisName;
        this.title = title;
    }
    
    public void setPoint(double x, double y){
        values.put(x, y);
    }

    public Map<Double, Double> getValues() {
        return values;
    }

    public String getTitle() {
        return title;
    }

    public String getxAxisName() {
        return xAxisName;
    }

    public String getyAxisName() {
        return yAxisName;
    }

    @Override
    public void exportToCSV(File csv) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(csv);
            pw.println(getxAxisName() + ", " + getyAxisName());
            for (Entry<Double, Double> v : getValues().entrySet()){
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
