/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model.impl;

import bgu.dcr.az.pivot.model.AggregatedField;
import bgu.dcr.az.pivot.model.Field;
import bgu.dcr.az.pivot.model.FilterField;
import bgu.dcr.az.pivot.model.Pivot;
import java.util.Iterator;

/**
 *
 * @author bennyl
 */
public class PivotUtils {

    public static boolean isInUse(Pivot p, Field element) {
        if (p.getSelectedAxisFields().contains(element) || p.getSelectedSeriesFields().contains(element)) {
            return true;
        }

        return isInValueFields(p, element);
    }

    public static boolean isInValueFields(Pivot p, Field element) {
        for (AggregatedField e : p.getSelectedValuesFields()) {
            if (e.getFieldId() == element.getFieldId()) {
                return true;
            }
        }
        return false;
    }

    public static boolean removeUseOfNonValueField(Pivot p, Field field) {
        Iterator<FilterField> it = p.getSelectedFilterFields().iterator();

        while (it.hasNext()) {
            if (it.next().getFieldId() == field.getFieldId()) {
                it.remove();
                return true;
            }
        }

        return p.getSelectedAxisFields().remove(field) || p.getSelectedSeriesFields().remove(field);
    }

    public static void removeUseOf(Pivot p, Field element) {

        if (element instanceof AggregatedField) {
            AggregatedField field = (AggregatedField) element;

            for (Iterator<AggregatedField> it = p.getSelectedValuesFields().iterator(); it.hasNext();) {
                AggregatedField e = it.next();
                if (e.getAggregatedFieldId() == field.getAggregatedFieldId()) {
                    it.remove();
                    return;
                }
            }
            return;
        }

        removeUseOfNonValueField(p, element);

        if (!(element instanceof AggregatedField)) {
            for (Iterator<AggregatedField> it = p.getSelectedValuesFields().iterator(); it.hasNext();) {
                AggregatedField e = it.next();
                if (e.getField().getFieldId() == element.getFieldId()) {
                    it.remove();
                    return;
                }
            }
        }

    }

}
