/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.api.modules;

import bgu.dcr.az.common.collections.IterableUtils;
import bgu.dcr.az.conf.registery.Register;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * a class which is responsible to supply and provide modules, a module
 * container can include other modules which are themselves module containers -
 * in this case upon initialization the module container children will assign
 * its container as parent and connect its listenable stream into the parent
 * stream.
 *
 * @author bennyl
 */
@Register("mc-base")
public class ModuleContainer implements Module {

    private final Map<Class, List<Module>> supplied = new HashMap<>();
    private ModuleContainer parent = null;
    private Set<Module> awaitingInitializationModules = new LinkedHashSet<>();

    /**
     * initialize all the supplied modules, for each supplied module container
     * recursively call the start method. once the module container was started
     * there will be no module additions allowed!
     */
    protected void start() {
        List<ModuleContainer> awaitingModuleContainers = new LinkedList<>();

        while (!awaitingInitializationModules.isEmpty()) {
            final Iterator<Module> i = awaitingInitializationModules.iterator();
            Module module = i.next();
            i.remove();
            module.initialize(this);
            if (module instanceof ModuleContainer) {
                awaitingModuleContainers.add((ModuleContainer) module);
            }
        }

        awaitingInitializationModules = null; //no more module addition allowed!
        awaitingModuleContainers.forEach(ModuleContainer::start);
    }

    /**
     * attempt to get the first requirement which is supplied by the given
     * module class, if no such a requirement is supplied to the given key an
     * {@link  UnmetRequirementException} is thrown.
     *
     * @param <T> the resulted type
     * @param moduleClass
     * @return
     */
    public <T extends Module> T require(Class<T> moduleClass) {
        List<Module> result = supplied.get(moduleClass);
        if (result == null || result.isEmpty()) {

            if (parent == null) {
                throw new UnmetRequirementException("no such module provided: '" + moduleClass.getCanonicalName() + "'");
            } else {
                return parent.require(moduleClass);
            }
        }

        return (T) result.get(0);
    }

    /**
     * same as {@link #require(java.lang.Class) } but will return list of all
     * the modules which are supplied by the given key, note that this method
     * will not throw an exception in the case where there is no module to
     * return, instead it will return an "empty iterable"
     *
     * @param <T>
     * @param moduleClass
     * @return
     */
    public <T extends Module> Iterable<T> requireAll(Class<T> moduleClass) {
        List<Module> result = supplied.get(moduleClass);
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }
        if (parent == null) {
            return IterableUtils.unmodifiableIterable((Iterable<T>) result);
        } else {
            return IterableUtils.combine((Iterable<T>) result, parent.requireAll(moduleClass));
        }
    }

    /**
     * @param moduleClass
     * @return true if and only if there is at least one module supplied with
     * the given module class
     */
    public boolean hasRequirement(Class<? extends Module> moduleClass) {
        List<Module> result = supplied.get(moduleClass);
        if (result == null || result.isEmpty()) {
            return parent != null && parent.hasRequirement(moduleClass);
        }

        return true;
    }

    /**
     * supply the given module with the given module key
     *
     * @param moduleKey
     * @param module
     */
    public void supply(Class<? extends Module> moduleKey, Module module) {

        if (awaitingInitializationModules == null) {
            throw new UnsupportedOperationException("cannot supply new modules on a started module container");
        }

        List<Module> result = supplied.get(moduleKey);
        if (result == null || result.isEmpty()) {
            result = new LinkedList<>();
            supplied.put(moduleKey, result);
        }

        result.add(module);
        awaitingInitializationModules.add(module);

        System.out.println("Added Module: " + moduleKey.getSimpleName() + " ( ");
    }

    /**
     * supply the given module with all the keys that can be retrieved from its
     * inheritance tree
     *
     * @param module
     */
    public void supply(Module module) {
        if (module == null) {
            throw new NullPointerException("cannot supply null module");
        }

        LinkedList<Class> open = new LinkedList<>();
        open.add(module.getClass());
        while (!open.isEmpty()) {
            Class c = open.remove();
            if (Module.class.isAssignableFrom(c) && c != Module.class) {
                open.add(c.getSuperclass());
                open.addAll(Arrays.asList(c.getInterfaces()));
                supply(c, module);
            }
        }
    }

    /**
     * @return the parent which contains this module container or null if no
     * such parent provided
     */
    public ModuleContainer parent() {
        return parent;
    }

    @Override
    public void initialize(ModuleContainer mc) {
        parent = mc;
    }

    public Collection<Module> getAllModules() {
        return new ModuleCollection();
    }

    private class ModuleCollection implements Collection<Module> {

        @Override
        public int size() {
            throw new UnsupportedOperationException("Not supported"); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException("Not supported"); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean contains(Object o) {
            throw new UnsupportedOperationException("Not supported"); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Iterator<Module> iterator() {
            return supplied.values().stream().flatMap(v -> v.stream()).collect(Collectors.toSet()).iterator();
        }

        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException("Not supported"); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException("Not supported"); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean add(Module e) {
            supply(e);
            return true;
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException("Not supported"); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            throw new UnsupportedOperationException("Not supported"); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean addAll(Collection<? extends Module> c) {
            c.forEach(this::add);
            return true;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException("Not supported"); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException("Not supported"); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void clear() {
            supplied.clear();
        }

    }

}
