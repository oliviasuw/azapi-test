/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.test;

import bgu.dcr.az.orm.api.FieldMetadata;
import bgu.dcr.az.orm.impl.FieldMetadataImpl;
import bgu.dcr.az.orm.impl.SimpleData;
import bgu.dcr.az.pivot.model.Pivot;
import bgu.dcr.az.pivot.model.impl.AbstractPivot;
import bgu.dcr.az.pivot.model.impl.SimplePivot;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author User
 */
public class PivotTestUtils {

    public static Pivot readPivotFromCSV(String filename) throws Exception {
        try (InputStream is = new FileInputStream(filename)) {
            return readPivotFromCSV(is);
        }
    }

    public static AbstractPivot readPivotFromCSV(InputStream stream) throws Exception {
        Scanner s = new Scanner(stream);

        String line = s.nextLine();

        ArrayList<String> headers = new ArrayList<>();

        for (String v : line.split(",")) {
            headers.add(v.trim());
        }

        final int size = headers.size();

        ArrayList<Object[]> data = new ArrayList<>();

        while (s.hasNext()) {
            line = s.nextLine();
            Object[] row = new Object[size];
            int i = 0;
            for (String v : line.split(",")) {
                try {
                    row[i] = Float.valueOf(v.trim());
                } catch (NumberFormatException e) {
                    row[i] = v.trim();
                }
                i++;
            }
            data.add(row);
        }

        FieldMetadata[] fields = new FieldMetadata[headers.size()];
        int i = 0;
        for (String v : headers) {
            fields[i] = new FieldMetadataImpl(v, data.get(0)[i].getClass());
            i++;
        }

        return new SimplePivot(new SimpleData(data, fields));
    }

    public static AbstractPivot generateSimplePivot1() throws Exception {
        ArrayList<Object[]> data = new ArrayList<>();

        data.add(new Object[]{1, "x", 1, "a"});
        data.add(new Object[]{2, "x", 1, "a"});
        data.add(new Object[]{3, "y", 1, "a"});
        data.add(new Object[]{4, "y", 2, "a"});
        data.add(new Object[]{5, "x", 2, "b"});
        data.add(new Object[]{6, "x", 2, "b"});
        data.add(new Object[]{7, "y", 1, "b"});
        data.add(new Object[]{8, "y", 1, "b"});
        data.add(new Object[]{9, "x", 1, "a"});
        data.add(new Object[]{10, "x", 2, "a"});
        data.add(new Object[]{11, "y", 2, "a"});
        data.add(new Object[]{12, "y", 2, "a"});

        FieldMetadata[] meta = new FieldMetadata[4];
        meta[0] = new FieldMetadataImpl("Field 0", Integer.class);
        meta[1] = new FieldMetadataImpl("Field 1", String.class);
        meta[2] = new FieldMetadataImpl("Field 2", Integer.class);
        meta[3] = new FieldMetadataImpl("Field 3", String.class);

        SimplePivot p = new SimplePivot(new SimpleData(data, meta));

        p.getSelectedValuesFields().add(new AbstractPivot.SimpleAggregatedField(p, p.getAvailableRawFields().get(0), 11));
//        p.getSelectedValuesFields().add(new AbstractPivot.SimpleAggregatedField(p, p.getAvailableRawFields().get(2), 12));
        p.getSelectedAxisFields().add(p.getAvailableRawFields().get(2));
        p.getSelectedSeriesFields().add(p.getAvailableRawFields().get(1));
        p.getSelectedSeriesFields().add(p.getAvailableRawFields().get(3));
//        p.selectAxisField(f3);
//        p.selectFilterField(f4);
//        p.getSelectedFilterFields().iterator().next().restrictValue("y");
//        p.selectSeriesField(f2);
//        p.selectValuesField(f1);

//        p.selectFilterField(f1);
//        p.selectFilterField(f2);
//        p.selectFilterField(f3);
//        p.selectFilterField(f4);
        return p;

    }

    public static Pivot generateSimplePivot2() throws Exception {
//        int[] generator = new int[]{2, 3, 4, 5, 6, 7};
//
//        ArrayList<Integer[]> data = generateRawData(generator);
//        SimplePivot p = new SimplePivot(data);
//
//        Set<Field> rawFields = generateFields(p, generator);
//
////        System.out.println("" + joinI(data));
//        int turn = 0;
//
//        for (Field f : (Set<Field>) p.getAvailableRawFields()) {
//            if (turn < 2) {
//                p.selectAxisField(f);
//            } else if (turn < 4) {
//                final Field ff = f;
//                p.selectValuesField(ff);
//            } else {
//                p.selectSeriesField(f);
//            }
//            turn++;
//        }
//
//        return p;
        return null;
    }

    private static ArrayList<Integer[]> generateRawData(int[] generator) {
        ArrayList<Integer[]> data = new ArrayList<>();
        int size = 1;

        for (int t : generator) {
            size *= t;
        }

        for (int i = 0; i < size; i++) {
            Integer[] row = new Integer[generator.length];

            for (int j = 0; j < generator.length; j++) {
                row[j] = 10 * generator[j] + i % generator[j];
            }

            data.add(row);
        }

        return data;
    }

}
