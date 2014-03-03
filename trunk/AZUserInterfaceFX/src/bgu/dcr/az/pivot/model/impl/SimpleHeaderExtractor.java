/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model.impl;

import bgu.dcr.az.pivot.model.TableData;

/**
 *
 * @author Zovadi
 */
public class SimpleHeaderExtractor implements TableData.HeaderExtractor {

    private final int index;

    public SimpleHeaderExtractor() {
        index = -1;
    }

    public SimpleHeaderExtractor(int index) {
        this.index = index;
    }

    @Override
    public String extract(Object[] objs) {
        if (index != -1) {
            return objs[index].toString();
        }

        return join(objs);
    }

    private String join(Object[] objs) {
        StringBuffer sb = new StringBuffer();

        for (Object o : objs) {
            sb.append(o.toString()).append(" - ");
        }

        if (sb.length() != 0) {
            sb.delete(sb.length() - " - ".length(), sb.length());
        }

        return sb.toString();
    }
}
