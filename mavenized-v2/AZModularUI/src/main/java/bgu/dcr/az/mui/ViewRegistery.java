/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui;

import bgu.dcr.az.conf.registery.Registery;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author bennyl
 */
public class ViewRegistery {

    private static ViewRegistery registery = null;
    private TreeMap<String, List<ViewManipulator>> manipulators = new TreeMap<>();

    public static ViewRegistery get() {
        if (registery == null) {
            registery = new ViewRegistery();
            Registery.get();
        }

        return registery;
    }

    public void register(ViewManipulator vm, String category) {
        System.err.println("View " + vm + " is submited to " + category);
        List<ViewManipulator> list = manipulators.get(category);
        if (list == null) {
            list = new LinkedList<>();
            manipulators.put(category, list);
        }

        list.add(vm);
    }

    public View createView(String name, ViewContainer container) {
        List<ViewManipulator> ms = manipulators.get(name);
        if (ms == null) {
            return null;
        }

        return ms.stream()
                .filter(v -> v.accept(container))
                .map(v -> v.create(container))
                .findFirst().orElse(null);
    }

    public Iterable<View> createViews(String namePrefix, ViewContainer container) {
        return () -> manipulators.subMap(namePrefix, namePrefix + Character.MAX_VALUE).values().stream()
                .flatMap(List::stream)
                .filter(v -> v.accept(container))
                .map(v -> v.create(container))
                .iterator();
    }

}
