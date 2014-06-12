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
public class VirtualTag implements Tag {

    private final String k;

    public VirtualTag(String k) {
        this.k = k;
    }

    @Override
    public String getK() {
        return k;
    }

    @Override
    public String getV() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.k);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VirtualTag other = (VirtualTag) obj;
        return !Objects.equals(this.k, other.k);
    }

}
