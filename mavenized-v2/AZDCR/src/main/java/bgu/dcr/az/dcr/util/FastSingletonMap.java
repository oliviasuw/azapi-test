/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.util;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author User
 */
public class FastSingletonMap<K, V> implements Map<K, V> {

    private K key;
    private V value;

    public FastSingletonMap() {
    }

    @Override
    public int size() {
        return key == null ? 0 : 1;
    }

    @Override
    public boolean isEmpty() {
        return key == null;
    }

    @Override
    public boolean containsKey(Object key) {
        return key.equals(this.key);
    }

    @Override
    public boolean containsValue(Object value) {
        return value.equals(this.value);
    }

    @Override
    public V get(Object key) {
        return value;
    }

    @Override
    public V put(K key, V value) {
        V old = this.value;
        this.key = key;
        this.value = value;
        return old;
    }

    @Override
    public V remove(Object key) {
        this.key = null;
        return this.value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        remove(key);
    }

    @Override
    public Set<K> keySet() {
        return Collections.singleton(key);
    }

    @Override
    public Collection<V> values() {
        return Collections.singleton(value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return Collections.<Entry<K, V>>singleton(new AbstractMap.SimpleEntry<>(key, value));
    }

}
