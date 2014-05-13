package bgu.dcr.az.metagen.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.lang.model.element.ExecutableElement;

public class PropertyMetadata extends Metadata<ExecutableElement> {
	private static Map<String, List<PropertyMetadata>> propertyCache = new HashMap<>();

	private MethodMetadata getter;
	private MethodMetadata setter;

	public PropertyMetadata(MethodMetadata getter, MethodMetadata setter) {
		super(getter.getElement());
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public String getName() {
		return getter.getName().substring(3);
	}

	public boolean isReadOnly() {
		return setter == null;
	}

	public static List<PropertyMetadata> findProperties(ClassMetadata c) {
		List<PropertyMetadata> properties = propertyCache.get(c.getFQN());

		if (properties == null) {

			Map<String, MethodMetadata> getters = new HashMap<>();
			Map<String, MethodMetadata> setters = new HashMap<>();

			for (MethodMetadata m : c.getMethods()) {
				if (m.getModifiers().isPublic()) {
					if (m.getName().startsWith("get") && m.getArguments().isEmpty())
						getters.put(m.getName().substring(3), m);
					if (m.getName().startsWith("set") && m.getArguments().size() == 1 && m.hasNoReturnValue())
						setters.put(m.getName().substring(3), m);
				}
			}

			properties = new LinkedList<>();
			for (Entry<String, MethodMetadata> m : getters.entrySet()) {
				properties.add(new PropertyMetadata(m.getValue(), setters.get(m.getKey())));
			}

			propertyCache.put(c.getFQN(), properties);
		}

		return properties;
	}
}
