/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package osm.data.api;

import java.util.Collection;

/**
 *
 * @author Shl
 */
public interface HasTags {
    Collection<Tag> getTags();
    
    Collection<Tag> getTags(String key);
}
