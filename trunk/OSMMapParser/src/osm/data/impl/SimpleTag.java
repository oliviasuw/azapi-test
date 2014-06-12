/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package osm.data.impl;

import java.util.Objects;
import osm.data.api.Tag;

/**
 *
 * @author Shl
 */
public class SimpleTag implements Tag {

    private String k;
    private String v;

    public SimpleTag(String k, String v) {
        this.k = k;
        this.v = v;
    }

    @Override
    public String getK() {
        return k;
    }

    @Override
    public String getV() {
        return v;
    }

    @Override
    public String toString() {
        return "{" + k + ", " + v + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.k);
        hash = 29 * hash + Objects.hashCode(this.v);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SimpleTag other = (SimpleTag) obj;
        if (!Objects.equals(this.k, other.k)) {
            return false;
        }
        return Objects.equals(this.v, other.v);
    }
}
