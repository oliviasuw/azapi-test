/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.impl.entities;

import data.events.impl.ParkingEvent;
import data.events.impl.ParkingEvent.CarType;
import data.map.impl.wersdfawer.AZVisVertex;
import data.map.impl.wersdfawer.groupbounding.HasId;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 * An entity for parking lots that also creates charge histogram for incoming
 * cars.
 *
 * @author Shlomi
 */
public class ParkingLotEntity implements HasId{

    private AZVisVertex graphNode;
    private NumberAxis xAxis = new NumberAxis();
    private CategoryAxis yAxis = new CategoryAxis();
    private BarChart<Number, String> bc = new BarChart<>(xAxis, yAxis);
    private int[] eData = new int[10];
    private int[] fData = new int[10];

    public ParkingLotEntity(AZVisVertex graphNode) {
        this.graphNode = graphNode;
        bc.setTitle("Charge Data for ParkingLot ID " + graphNode.getId() );
        xAxis.setLabel("%ChargeLevel");
        yAxis.setLabel("Quantity");
    }

    public void addToData(double charge, ParkingEvent.CarType type) {
        if (charge == 100) {
            charge = 99;
        }
        if (type == CarType.ELECTRIC) {
            eData[(int) (charge / 10)]++;
        }
        if (type == CarType.FUEL) {
            fData[(int) (charge / 10)]++;
        }
    }

    public BarChart<Number, String> getChart() {
        bc.getData().clear();
        XYChart.Series eSeries = new XYChart.Series();
        eSeries.setName("Electric");
        XYChart.Series fSeries = new XYChart.Series();
        eSeries.setName("Fuel");

        for (int i = 0; i < eData.length; i++) {
            String name = "" + i*10 + "-" + (i+1)*10 ;
            eSeries.getData().add(new XYChart.Data(name,eData[i]));
            fSeries.getData().add(new XYChart.Data(name,fData[i]));
        }
        bc.getData().addAll(eSeries, fSeries);
        return bc;
    }

    @Override
    public String getId() {
        return "parking" + graphNode.getId();
    }

}
