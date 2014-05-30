/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.modules;

import bgu.dcr.az.common.collections.IterableUtils;
import bgu.dcr.az.conf.registery.Register;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
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

    // THE FOLLOWING ONLY USED FOR THE INITIALIZATION STEP AND THEN THEY ARE NULLED OUT!
    private Set<Module> awaitingInitializationModules = new LinkedHashSet<>();
    private Set<Module> initializedModules = new HashSet<>();

    /**
     * initialize all the supplied modules, for each supplied module container
     * recursively call the start method. once the module container was started
     * there will be no module additions allowed!
     */
    protected void initializeModules() {
        List<ModuleContainer> awaitingModuleContainers = new LinkedList<>();

        while (!awaitingInitializationModules.isEmpty()) {
            final Iterator<Module> i = awaitingInitializationModules.iterator();
            Module module = i.next();
            i.remove();
            initializedModules.add(module);
            module.initialize(this);
            if (module instanceof ModuleContainer) {
                awaitingModuleContainers.add((ModuleContainer) module);
            }
        }

        awaitingInitializationModules = null; //no more module addition allowed!
        initializedModules = null;
        awaitingModuleContainers.forEach(ModuleContainer::initializeModules);
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
        return require(moduleClass, 0);
    }

    /**
     * same as {@link #require(java.lang.Class)} but requires the i'th supplied
     * module
     *
     * @param <T>
     * @param moduleClass
     * @param i
     * @return
     */
    public <T extends Module> T require(Class<T> moduleClass, int i) {
        T result = get(moduleClass, i);
        if (result == null) {
            throw new UnmetRequirementException("no such module provided: '" + moduleClass.getCanonicalName() + "'");
        }
        return result;
    }

    /**
     * same as {@link #require(java.lang.Class, int) } but will return null if
     * not exists instead of throwing exception.
     *
     * @param <T>
     * @param moduleClass
     * @param i
     * @return
     */
    public <T extends Module> T get(Class<T> moduleClass, int i) {
        List<Module> resultList = supplied.get(moduleClass);
        if (resultList == null || resultList.isEmpty()) {
            if (parent == null) {
                return null;
            } else {
                return parent.get(moduleClass, i);
            }
        }

        T result = (T) resultList.get(i);
        if (result != null && awaitingInitializationModules != null) {
            if (awaitingInitializationModules.contains(result)) {
                awaitingInitializationModules.remove(result);
                initializedModules.add(result);
                result.initialize(this);
            }
        }

        return result;
    }

    /**
     * same as {@link #require(java.lang.Class) } but will return null if not
     * exists instead of throwing exception.
     *
     * @param <T>
     * @param moduleClass
     * @return
     */
    public <T extends Module> T get(Class<T> moduleClass) {
        return get(moduleClass, 0);
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
     * return a list that contain all the supplied object for the given module
     * class in this container only (=not including parent containers). a
     *
     * the list is backed by this module container, any additions/deletions to
     * the container will be reflected to the list and vice versa.
     *
     * @param <T>
     * @param moduleClass
     * @return
     */
    protected <T extends Module> List<T> getDirectList(Class<T> moduleClass) {
        List<Module> result = supplied.get(moduleClass);
        if (result == null) {
            result = new ArrayList<>();
            supplied.put(moduleClass, result);
        }

        return (List<T>) new BackedList(result, moduleClass);
    }

    /**
     * supply all the given modules under the given type.
     *
     * @see #supply(java.lang.Class, bgu.dcr.az.conf.modules.Module)
     * @param modules
     * @param moduleType
     */
    public void supplyAll(Class moduleType, Collection<? extends Module> modules) {
        getDirectList(moduleType).addAll(modules);
    }

    /**
     * @param moduleClass
     * @return the amount of different elements that was supplied with the given
     * key
     */
    public int amountSupplied(Class<? extends Module> moduleClass) {
        List<Module> result = supplied.get(moduleClass);
        return (result == null ? 0 : result.size()) + (parent == null ? 0 : parent.amountSupplied(moduleClass));
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
            result = new ArrayList<>();
            supplied.put(moduleKey, result);
        }

        result.add(module);
        awaitingInitializationModules.add(module);
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

    protected void setParent(ModuleContainer mc) {
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

    private class BackedList implements List<Module> {

        List<Module> internal;
        Class key;

        public BackedList(List<Module> internal, Class key) {
            this.internal = internal;
            this.key = key;
        }

        @Override
        public int size() {
            return internal.size();
        }

        @Override
        public boolean isEmpty() {
            return internal.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return internal.contains(o);
        }

        @Override
        public Iterator<Module> iterator() {
            return Collections.unmodifiableList(internal).iterator();
        }

        @Override
        public Object[] toArray() {
            return internal.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return internal.toArray(a);
        }

        @Override
        public boolean add(Module e) {
            supply(key, e);
            return true;
        }

        @Override
        public boolean remove(Object o) {
            if (isEmpty()) {
                return false;
            }
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return internal.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends Module> c) {
            c.forEach(this::add);
            return true;
        }

        @Override
        public boolean addAll(int index, Collection<? extends Module> c) {
            c.stream().skip(index).forEach(this::add);
            return true;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            if (isEmpty()) {
                return false;
            }
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            if (isEmpty()) {
                return false;
            }
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void clear() {
            if (isEmpty()) {
                return;
            }
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Module get(int index) {
            return internal.get(index);
        }

        @Override
        public Module set(int index, Module element) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void add(int index, Module element) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Module remove(int index) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int indexOf(Object o) {
            return internal.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return internal.lastIndexOf(o);
        }

        @Override
        public ListIterator<Module> listIterator() {
            return Collections.unmodifiableList(internal).listIterator();
        }

        @Override
        public ListIterator<Module> listIterator(int index) {
            return Collections.unmodifiableList(internal).listIterator(index);
        }

        @Override
        public List<Module> subList(int fromIndex, int toIndex) {
            return new BackedList(internal.subList(fromIndex, toIndex), key);
        }

    }

}
