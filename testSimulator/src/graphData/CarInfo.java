/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package graphData;

/**
 *
 * @author Eran
 */
class CarInfo {
    private String source, target; // represent the edge that the car is driving currently
    private double percantage; // holds the progress of the car in the current road segment

    public CarInfo(double percantage) {
        this.percantage = percantage;
    }

    public double getPercantage() {
        return percantage;
    }
    
    public void setPercantage(double percantage) {
        this.percantage = percantage;
    }
    
    public void setEdge(String src, String target){
        this.source = src;
        this.target = target;
    }
    
    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }
}
