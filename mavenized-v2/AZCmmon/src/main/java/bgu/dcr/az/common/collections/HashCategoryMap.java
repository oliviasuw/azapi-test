/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.common.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public class HashCategoryMap<V> implements CategoryMap<V> {

    private Map<String, HashCategoryMap<V>> subCategories = null;
    private List<V> values = createValueList();

    public HashCategoryMap() {
    }

    /**
     * if this function return true then when ever a collection (values/
     * children) become empty it will get nullify to reduce memory consumption
     *
     * @return
     */
    protected boolean nullifyUnusedCollection() {
        return true;
    }

    protected Map<String, HashCategoryMap<V>> createChildrenMap() {
        return new HashMap<>();
    }

    protected List<V> createValueList() {
        return new LinkedList<>();
    }

    protected List<V> getValueList() {
        if (values == null && !nullifyUnusedCollection()) {
            values = createValueList();
        }

        return values;
    }

    protected Map<String, HashCategoryMap<V>> getChildrenMap() {
        if (subCategories == null && !nullifyUnusedCollection()) {
            subCategories = createChildrenMap();
        }

        return subCategories;
    }

    /**
     * create a new sub category for the path[indexInPath] element
     *
     * @param path
     * @param indexInPath
     * @return
     */
    protected HashCategoryMap<V> createSubCategory(String category, CategoryMap<V> parent) {
        return new HashCategoryMap<>();
    }

    @Override
    public void add(V value, String... category) {
        HashCategoryMap<V> root = reachCategory(category, 0, category.length, true);
        root.values().add(value);
    }

    private Map<String, HashCategoryMap<V>> getSubCategories(boolean create) {
        if (subCategories == null && create) {
            subCategories = createChildrenMap();
        }

        return subCategories;
    }

    @Override
    public void addAll(Collection<V> values, String... category) {
        HashCategoryMap<V> root = reachCategory(category, 0, category.length, true);
        root.values().addAll(values);
    }

    @Override
    public void remove(V value, String... category) {
        HashCategoryMap<V> cat = reachCategory(category, 0, category.length, false);
        if (cat != null && cat.values != null) {
            cat.values.remove(value);
        }
    }

    @Override
    public void removeChildCategory(String... category) {
        switch (category.length) {
            case 0:
                if (nullifyUnusedCollection()) {
                    subCategories = null;
                }
                break;
            case 1:
                if (subCategories != null) {
                    subCategories.remove(category[0]);
                }
                break;
            default:
                HashCategoryMap<V> cat = reachCategory(category, 0, category.length - 1, true);
                if (cat.getSubCategories(false) != null) {
                    cat.getSubCategories(false).remove(category[category.length - 1]);
                }
        }
    }

    @Override
    public void clearCategory(String... category) {
        HashCategoryMap<V> cat = reachCategory(category, 0, category.length, false);
        if (cat != null && cat.values != null) {
            if (nullifyUnusedCollection()) {
                cat.values = null;
            } else {
                cat.values.clear();
            }

            if (cat.subCategories != null) {
                if (nullifyUnusedCollection()) {
                    cat.subCategories = null;
                } else {
                    cat.subCategories.clear();
                }
            }
        }
    }

    @Override
    public CategoryMap<V> addChildCategory(String... category) {
        return reachCategory(category, 0, category.length, true);
    }

    @Override
    public CategoryMap<V> getChildCategory(String... category) {
        return reachCategory(category, 0, category.length, false);
    }

    @Override
    public Iterator<Map.Entry<String[], List<V>>> iterator() {
        return new Iterator<Entry<String[], List<V>>>() {
            Map.Entry<String[], List<V>> next = computeNext();
            LinkedList<String> cat = new LinkedList<>();
            LinkedList<Iterator<Entry<String, HashCategoryMap<V>>>> stack = null;

            private Entry<String, HashCategoryMap<V>> nextFromStack() {
                while (!stack.isEmpty() && !stack.getLast().hasNext()) {
                    stack.removeLast();
                    if (!cat.isEmpty()) {
                        cat.removeLast();
                    }
                }

                if (!stack.isEmpty()) {
                    return stack.getLast().next();
                }
                return null;
            }

            protected Entry<String[], List<V>> computeNext() {
                Ent next = new Ent();

                if (stack == null) {
                    List<V> v = HashCategoryMap.this.values;
                    if (v == null) {
                        v = Collections.emptyList();
                    }
                    next.v = v;
                    next.key = new String[0];

                    stack = new LinkedList<>();
                    if (HashCategoryMap.this.subCategories != null) {
                        stack.add(HashCategoryMap.this.subCategories.entrySet().iterator());
                    }
                } else {
                    Entry<String, HashCategoryMap<V>> n = nextFromStack();
                    if (n == null) {
                        return null;
                    }
                    cat.addLast(n.getKey());
                    next.key = cat.toArray(new String[cat.size()]);
                    next.v = n.getValue().values == null ? Collections.emptyList() : n.getValue().values;

                    if (!cat.isEmpty() && (n.getValue().subCategories == null || n.getValue().subCategories.isEmpty())) {
                        cat.removeLast();
                    } else {
                        stack.add(n.getValue().subCategories.entrySet().iterator());
                    }
                }

                return next;
            }

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public Entry<String[], List<V>> next() {
                Entry<String[], List<V>> temp = next;
                next = computeNext();
                return temp;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }

    private HashCategoryMap<V> reachCategory(String[] category, int offset, int length, boolean create) {
        HashCategoryMap<V> root = this;
        HashCategoryMap<V> next;
        for (int i = 0; i < length && root != null; i++) {
            Map<String, HashCategoryMap<V>> cat = root.getSubCategories(create);
            if (cat == null) {
                return null;
            }
            next = cat.get(category[i]);
            if (next == null && create) {
                next = createSubCategory(category[i + offset], root);
                cat.put(category[i + offset], next);
            }

            root = next;
        }
        return root;
    }

    @Override
    public Set<String> children() {
        return getSubCategories(true).keySet();
    }

    @Override
    public List<V> values() {
        return values;
    }

    private class Ent<V> implements Entry<String[], List<V>> {

        private String[] key;
        private List<V> v;

        @Override
        public String[] getKey() {
            return key;
        }

        @Override
        public List<V> getValue() {
            return v;
        }

        @Override
        public List<V> setValue(List<V> value) {
            List<V> old = v;
            v = value;
            return old;
        }
    }
}
