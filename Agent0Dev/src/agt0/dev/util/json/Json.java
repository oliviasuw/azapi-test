package agt0.dev.util.json;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static agt0.dev.util.JavaUtils.*;

public class Json {
	Type type;
	Object data;
	
	public Json(String text) throws JsonParseException {
		parse(text.toCharArray(), 0);
	}
	
	private Json(){
		//do nothing..
	}
	
	public Type getType() {
		return type;
	}
	
	/**
	 * call it only if type is value .. 
	 * @return
	 */
	public String getValue(){
		if (type == Type.VALUE){
			return data.toString();
		}else {
			return null;
		}
	}
	
	/**
	 * call it only if type is map.. 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Json> getMap(){
		if (type == Type.MAP){
			return ((Map<String, Json>) data);
		}else {
			return null;
		}
	}
	
	/**
	 * call it only if type is array.. 
	 * @return
	 */
	public List<Json> getArray(){
		if (type == Type.ARRAY){
			return (List<Json>) data;
		}else {
			return null;
		}
	}
	
	private int eatSpace(char[] text, int idx){
		while (idx < text.length && text[idx] == ' ') idx++;
		return idx;
	}
	
	/**
	 * 
	 * @param text
	 * @param idx
	 * @return the next index for consuming elements
	 * @throws JsonParseException 
	 */
	private int parse(char[] text, int idx) throws JsonParseException {
		idx = eatSpace(text, idx);
		switch(text[idx]){
		case '"':
			type = Type.VALUE;
			return parseValue(text, idx+1);
		case '{':
			type = Type.MAP;
			data = new HashMap<String, Json>();
			return parseMap(text, idx+1);
		case '[':
			type = Type.ARRAY;
			data = new LinkedList<Json>();
			return parseArray(text, idx+1);
		default:
			throw new JsonParseException("cannot parse token at " + idx);
		}
	}

	@SuppressWarnings("unchecked")
	private int parseArray(char[] text, int idx) throws JsonParseException {
		List<Json> l = (List<Json>) data;
		idx = eatSpace(text, idx);
		
		while (idx < text.length && text[idx] != ']'){
			Json el = new Json();
			idx = el.parse(text, idx);
			l.add(el);
			
			idx = eatSpace(text, idx);
			if (idx < text.length && text[idx] == ',') eatSpace(text, ++idx);
		}
		
		if (idx != text.length) idx++;
		return idx;
	}

	private int parseMap(char[] text, int idx) throws JsonParseException {
		Map<String, Json> map = (Map<String, Json>) data;
		idx = eatSpace(text, idx);
		
		while (idx < text.length && text[idx] != '}'){
			Json el = new Json();
			idx = el.parse(text, idx);
			String key = el.data.toString();
			
			idx = eatSpace(text, idx);
			if (idx >= text.length || text[idx] != ':'){
				throw new JsonParseException("cannot parse map - missing ':' at index " + idx);
			}else {
				idx = el.parse(text, idx+1);
				map.put(key, el);
				
				idx = eatSpace(text, idx);
				if (idx < text.length && text[idx] == ',') idx = eatSpace(text, ++idx);
			}
		}
		
		if (idx != text.length) idx++;
		return idx;
	}

	private int parseValue(char[] text, int idx) {
		StringBuilder sb = new  StringBuilder(text.length - idx);
		boolean escape = false;
		for (int i=idx; i<text.length; i++){
			switch (text[i]){
			case '"':
				if (!escape) {
					data = sb.toString();
					return i+1;
				}else {
					sb.append(text[i]);
				}
				break;
			case '\\':
				escape = !escape;
				if (!escape){
					sb.append(text[i]);
				}
				break;
			default:
				escape = false;
				sb.append(text[i]);
				break;
			}
		}
		
		data = sb.toString();
		return text.length;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		switch(type){
		case VALUE:
			sb.append("\"" + getValue() + "\"");
			break;
		case ARRAY:
			sb.append("[");
			for (Json e : getArray()){
				sb.append(e.toString() + ",");
			}
			sb.replace(sb.length()-1, sb.length(), "]");
			break;
		case MAP:
			sb.append("{");
			for (Entry<String, Json> e : getMap().entrySet()){
				sb.append("\"" + e.getKey() + "\":" + e.getValue().toString() + ",");
			}
			sb.replace(sb.length()-1, sb.length(), "}");
			break;
		}
		
		return sb.toString();
	}
	
	public static enum Type{
		VALUE, ARRAY, MAP;
	}
	
	public static class JsonParseException extends Exception{

		public JsonParseException() {
			super();
		}

		public JsonParseException(String arg0, Throwable arg1) {
			super(arg0, arg1);
		}

		public JsonParseException(String arg0) {
			super(arg0);
		}

		public JsonParseException(Throwable arg0) {
			super(arg0);
		}
		
	}
}
