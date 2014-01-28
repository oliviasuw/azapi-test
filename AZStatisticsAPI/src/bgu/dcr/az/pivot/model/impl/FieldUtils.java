/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model.impl;

import bgu.dcr.az.pivot.model.AggregationFunction;
import bgu.dcr.az.pivot.model.Field;
import bgu.dcr.az.pivot.model.Pivot;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
                    sum2 += (v - avg) * (v  - avg);
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

    public <F> List<Field<?, F>> extractFields(Pivot<F> pivot, F from, String... fieldNames) throws Exception {
        List<Field<?, F>> result = new LinkedList<>();

        Class c = from.getClass();
        int id = 0;
        for (String f : fieldNames) {
            final Method m = c.getMethod("get" + f, c);
            m.setAccessible(true);

            result.add(new AbstractField<Object, F>(pivot, humanized(f), id++, (Class) m.getReturnType()) {
                @Override
                public Object getValue(F o) {
                    try {
                        return m.invoke(o);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        }

        return result;
    }

    public static String humanized(String f) {
        char[] chars = f.toCharArray();
        StringBuilder sb = new StringBuilder(chars[0]);

        for (int i = 1; i < chars.length; i++) {
            if (Character.isUpperCase(chars[i])) {
                sb.append(" ");
            }

            sb.append(chars[i]);
        }

        return sb.toString();
    }
}
