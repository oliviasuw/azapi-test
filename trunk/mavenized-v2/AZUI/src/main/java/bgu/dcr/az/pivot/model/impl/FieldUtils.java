/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model.impl;

import bgu.dcr.az.pivot.model.AggregationFunction;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author User
 */
public class FieldUtils {

    public static <F> Set<AggregationFunction> getDefaultAggregationFunctions() {
        Set<AggregationFunction> result = new HashSet<>();

        result.add(new AggregationFunction<Double>() {
            @Override
            public String getName() {
                return "StdDev";
            }

            @Override
            public String toString() {
                return getName();
            }

            @Override
            public Double aggregate(Iterable<Double> values) {
                Double avg = 0D;
                int count = 0;

                for (Double v : values) {
                    avg += v;
                    count++;
                }

                avg = count == 0 ? 0 : avg / count;

                Double sum2 = 0D;
                for (Double v : values) {
                    sum2 += (v - avg) * (v - avg);
                }

                return count == 0 ? 0 : sum2 / count;
            }
        });

        result.add(new AggregationFunction<Double>() {
            @Override
            public String getName() {
                return "Avg";
            }

            @Override
            public String toString() {
                return getName();
            }
            
            @Override
            public Double aggregate(Iterable<Double> values) {
                Double sum = 0D;
                int count = 0;

                for (Double v : values) {
                    sum += v;
                    count++;
                }

                return count == 0 ? 0 : sum / count;
            }
        });

        result.add(new AggregationFunction<Double>() {
            @Override
            public String getName() {
                return "Count";
            }

            @Override
            public String toString() {
                return getName();
            }
            
            @Override
            public Integer aggregate(Iterable<Double> values) {
                int count = 0;

                for (Double v : values) {
                    count++;
                }

                return count;
            }
        });

        return result;
    }
}
