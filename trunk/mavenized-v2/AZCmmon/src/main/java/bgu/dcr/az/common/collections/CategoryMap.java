/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.common.collections;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public interface CategoryMap<V> extends Iterable<Map.Entry<String[], List<V>>> {

    void add(V value, String... category);

    void addAll(Collection<V> values, String... categories);

    void remove(V value, String... category);

    void removeChildCategory(String... category);

    void clearCategory(String... category);

    /**
     * will return sub category as can be reached from the given path. if this
     * category does not exists it will get created.
     *
     * @param category
     * @return
     */
    CategoryMap<V> addChildCategory(String... category);

    /**
     * like {@link addChildCategory} but if the child category does not exists then it will not get created and null will be returned
     *
     * @param category
     * @return
     */
    CategoryMap<V> getChildCategory(String... category);

    Set<String> children();

    List<V> values();
}
