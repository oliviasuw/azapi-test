package agt0.dev.util.ds;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import agt0.dev.util.JavaUtils.Fn1;

public class PrefixMap<T> {
	T value;
	HashMap<Character, PrefixMap<T>> map;
	
	public PrefixMap() {
		value = null;
		map = new HashMap<Character, PrefixMap<T>>();
	}
	
	
	public void map(Fn1<T, T> fn){
		if (this.value != null) {
			this.value = fn.invoke(this.value);
		}
		
		for (PrefixMap<T> v : map.values()) v.map(fn);
	}
	
	public void put(String prefix, T value){
		put(prefix.toCharArray(), 0, value);
	}
	
	private void put(char[] prefix, int idx, T value){
		if (prefix.length -1 == idx) {
			this.value = value; 
		}else {
			nextMap(prefix[idx]).put(prefix, idx+1, value);
		}
	}
	
	private PrefixMap<T> nextMap(char c){
		PrefixMap<T> ret = map.get(c);
		if (ret == null){
			ret = new PrefixMap<T>();
			map.put(c, ret);
		}
		
		return ret;
	}
	
	/**
	 * given a data - retrive all elements that in all the prefixes of this data
	 * @param data
	 * @return
	 */
	public List<T> get(String data){
		LinkedList<T> ret = new LinkedList<T>();
		get(data.toCharArray(), 0, ret);
		return ret;
	}
	
	private void get(char[] data, int idx, List<T> into){
		if (data.length == idx) return;
		
		if (value != null) into.add(value);
		if (map.containsKey(data[idx])){
			nextMap(data[idx]).get(data, idx+1, into);
		}
	}
	
	/**
	 * return the value in the exact prefix - without returning items in the passing path
	 * @param data
	 * @return
	 */
	public T getExact(String data){
		return getExact(data.toCharArray(), 0);
	}
	
	private T getExact(char[] data, int idx){
		if (data.length-1 == idx){
			return value;
		}else if (map.containsKey(data[idx])){
			return nextMap(data[idx]).getExact(data, idx+1);
		}else {
			return null;
		}
	}
	
	/**
	 * @return all the values in this map
	 */
	public Set<T> values(){
		HashSet<T> ret = new HashSet<T>();
		values(ret);
		return ret;
	}
	
	private void values(HashSet<T> set){
		if (value != null) set.add(value);
		
		for (PrefixMap<T> pmap : map.values()){
			pmap.values(set);
		}
	}
	
	
	
}
