/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui;

import bgu.dcr.az.conf.registery.Registery;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *
 * @author bennyl
 */
public class ControllerRegistery {

    private static ControllerRegistery registery = null;
    private TreeMap<String, List<ControllerManipulator>> manipulators = new TreeMap<>();
    private Map<Class, ControllerAttributes> attributes = new IdentityHashMap<>();

    public static ControllerRegistery get() {
        if (registery == null) {
            registery = new ControllerRegistery();
            Registery.get();
        }

        return registery;
    }

    public void register(ControllerManipulator vm, String category) {
        System.err.println("Controller " + vm + " is submited to " + category);
        List<ControllerManipulator> list = manipulators.get(category);
        if (list == null) {
            list = new LinkedList<>();
            manipulators.put(category, list);
        }

        attributes.put(vm.controllerClass(), new ControllerAttributes(category, vm.doc()));
        list.add(vm);
    }

    public ControllerAttributes getAttributes(Controller v) {
        return attributes.get(v.getClass());
    }

    public Controller createController(String name, Controller container) {
        List<ControllerManipulator> ms = manipulators.get(name);
        if (ms == null) {
            return null;
        }

        return ms.stream()
                .filter(v -> v.accept(container))
                .map(v -> v.create(container))
                .findFirst().orElse(null);
    }

    public Iterable<Controller> createControllers(String group, Controller container) {
        return manipulators.subMap(group, group + Character.MAX_VALUE).entrySet().stream()
                .filter(e -> e.getKey().lastIndexOf(".") == group.length())
                .flatMap(e -> e.getValue().stream())
                .filter(v -> v.accept(container))
                .map(v -> v.create(container))
                .collect(Collectors.toList());
    }

}
