/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.modules;

import bgu.dcr.az.conf.modules.info.ModuleUninstalled;
import bgu.dcr.az.common.collections.IterableUtils;
import bgu.dcr.az.conf.modules.info.InfoStream;
import bgu.dcr.az.conf.modules.info.ModuleContainerParentChangedInfo;
import bgu.dcr.az.conf.registery.Register;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * a class which is responsible to installInto and provide modules, a module
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
    private boolean eagerInitialization = false;

    private Map<Module, ModuleInfo> reverseLookup = new IdentityHashMap<>();
    private Map<Module, ModuleInfo> awaitingInitializationModules = new IdentityHashMap<Module, ModuleInfo>();

    /**
     * construct new module container which will delay initialization of new
     * modules until a call to the {@link #initializeModules() } is made or they
     * are explicitely required
     */
    public ModuleContainer() {
    }

    /**
     * a nasty hack to allow the configuration framework to work with module
     * container - do not call this method yourself!
     *
     * @UIVisibility false
     * @return
     */
    public Collection<Module> getAllModules() {
        return new ModuleCollectionHack();
    }

    /**
     * construct new module container which its initialization strategy is
     * dependant on the given argumenr
     *
     *
     * @param eagerInitialization if this argument is set to true this
     * constructor will create a module container which will initialize new
     * modules immidiatly, in this case a call to {@link #initializeModules()}
     * will result in an exception
     */
    public ModuleContainer(boolean eagerInitialization) {
        this.eagerInitialization = eagerInitialization;
    }

    /**
     * initialize all the supplied modules that wasn't been initialized yet
     * (added after the previous call to this method of before the first call to
     * this method and was never required or got), for each supplied module
     * container recursively call the start method.
     */
    protected void initializeModules() {
        if (eagerInitialization) {
            throw new UnsupportedOperationException("explisit initialization is not allowed for this module container");
        }

        while (!awaitingInitializationModules.isEmpty()) {
            try {
                for (Iterator<Map.Entry<Module, ModuleInfo>> it = awaitingInitializationModules.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<Module, ModuleInfo> e = it.next();

                    initModule(e.getKey(), e.getValue());

                    it.remove();
                }
            } catch (ConcurrentModificationException ex) {
            }
        }

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
     * moduleObject
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
            initModule(result);
        }

        return result;
    }

    private <T extends Module> void initModule(T result) {
        if (awaitingInitializationModules.containsKey(result)) {
            ModuleInfo info = awaitingInitializationModules.remove(result);
            reverseLookup.put(result, info);
            result.installInto(this);
        }
    }

    private void initModule(Module result, ModuleInfo info) {
        reverseLookup.put(result, info);
        result.installInto(this);
    }

    /**
     * same as {@link #require(java.lang.Class) } but will return null if not
     * exists instead of throwing exception.Object
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
     * same as {@link #requireAll(java.lang.Class) } but will return list of all
     * the modules which are supplied by the given key directly to this
     * container = without taking into consideration the modules that supplied
     * to the parent container
     *
     * @param <T>
     * @param moduleClass
     * @return
     */
    public <T extends Module> Iterable<T> requireAllLocal(Class<T> moduleClass) {
        List<Module> result = supplied.get(moduleClass);
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }

        return (Iterable<T>) result;
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

    public boolean isEagerInitialization() {
        return eagerInitialization;
    }

    /**
     * installInto all the given modules under the given type. - initialization
     * is dependent on the value of {@link #isEagerInitialization()}
     *
     * @see #install(java.lang.Class, bgu.dcr.az.conf.modules.Module)
     * @param modules
     * @param moduleType
     */
    public void installAll(Class moduleType, Iterable<? extends Module> modules) {
        installAll(moduleType, modules, eagerInitialization);
    }

    /**
     * installInto all the given modules under the given type. - initialization
     * is dependent on the value of {@link #isEagerInitialization()}
     *
     * @see #install(java.lang.Class, bgu.dcr.az.conf.modules.Module)
     * @param modules
     * @param moduleType
     */
    public void installAll(Class moduleType, Module... modules) {
        installAll(moduleType, Arrays.asList(modules), eagerInitialization);
    }

    /**
     * installInto all the given modules under the given type.
     *
     * @param immidiateInstall if true all the modules will be initialize
     * immidiatly
     * @see #install(java.lang.Class, bgu.dcr.az.conf.modules.Module)
     * @param modules
     * @param moduleType
     */
    public void installAll(Class moduleType, Iterable<? extends Module> modules, boolean immidiateInstall) {
        modules.forEach(m -> ModuleContainer.this.install(moduleType, m, immidiateInstall));
    }

    /**
     * @param moduleClass
     * @return the amount of different elements that was supplied with the given
     * key
     */
    public int amountInstalled(Class<? extends Module> moduleClass) {
        List<Module> result = supplied.get(moduleClass);
        return (result == null ? 0 : result.size()) + (parent == null ? 0 : parent.amountInstalled(moduleClass));
    }

    /**
     * same as {@link #amountInstalled(java.lang.Class) } but take into
     * consideration only the modules that were supplied directly into this
     * container - not considering parents
     *
     * @param moduleClass
     * @return
     */
    public int amountInstalledLocally(Class<? extends Module> moduleClass) {
        List<Module> result = supplied.get(moduleClass);
        return result == null ? 0 : result.size();
    }

    /**
     * @param moduleClass
     * @return true if and only if there is at least one module supplied with
     * the given module class
     */
    public boolean isInstalled(Class<? extends Module> moduleClass) {
        List<Module> result = supplied.get(moduleClass);
        if (result == null || result.isEmpty()) {
            return parent != null && parent.isInstalled(moduleClass);
        }

        return true;
    }

    /**
     * installInto the given module with the given module key - initialization
     * is dependent on the value of {@link #isEagerInitialization()}
     *
     * @param moduleKey
     * @param module
     */
    public void install(Class<? extends Module> moduleKey, Module module) {
        ModuleContainer.this.install(moduleKey, module, eagerInitialization);
    }

    /**
     * installInto the given module with the given module key
     *
     * @param moduleKey
     * @param module
     * @param immidiateInit - if true the module will be immidiatly initialized,
     * otherwise it will be initialized upon the next call to {@link #initializeModules()
     * } or if another module will require it
     */
    private void install(Class<? extends Module> moduleKey, Module module, boolean immidiateInit) {

        if (module == null) {
            throw new NullPointerException("cannot supply null module");
        }

        if (module == this) {
            throw new UnsupportedOperationException("module container cannot contain itself");
        }

        List<Module> result = supplied.get(moduleKey);
        if (result == null) {
            result = new ArrayList<>();
            supplied.put(moduleKey, result);
        }

        result.add(module);
        if (immidiateInit) {
            initModule(module, new ModuleInfo(moduleKey));
        } else {
            awaitingInitializationModules.computeIfAbsent(module, m -> new ModuleInfo()).registration.add(moduleKey);
        }
    }

    /**
     * uninstall the given module from the module container, if this module
     * container contains {@link InfoStream} then he will notify on the stream
     * about this removal using the info-class: {@link ModuleUninstalled}
     *
     * @param module
     */
    public void uninstall(Module module) {
        if (awaitingInitializationModules.remove(module) != null) {
            return;
        }

        ModuleInfo info = reverseLookup.remove(module);
        if (info != null) {
            for (Class registered : info.registration) {
                supplied.get(registered).remove(module);
            }
        }

        InfoStream i = get(InfoStream.class);
        if (i != null) {
            i.writeIfListening(() -> new ModuleUninstalled(module, this), ModuleUninstalled.class);
        }
    }

    /**
     * @param m
     * @param c
     * @return true if the given module is supplied with the given moduleClass
     */
    public boolean isInstalled(Module m, Class c) {
        ModuleInfo minfo = reverseLookup.get(m);
        if (minfo == null) {
            minfo = awaitingInitializationModules.get(m);
        }

        if (minfo == null) {
            return false;
        }

        return minfo.registration.contains(c);
    }

    /**
     * installInto the given module with all the keys that can be retrieved from
     * its inheritance tree - initialization is dependent on the value of
     * {@link #isEagerInitialization()}
     *
     * @param module
     */
    public void install(Module module) {

        LinkedList<Class> open = new LinkedList<>();
        open.add(module.getClass());
        while (!open.isEmpty()) {
            Class c = open.remove();
            if (Module.class.isAssignableFrom(c) && c != Module.class) {
                final Class superclass = c.getSuperclass();
                if (superclass != null) {
                    open.add(superclass);
                }
                open.addAll(Arrays.asList(c.getInterfaces()));
                install(c, module, false);
            }
        }

        if (isEagerInitialization()) {
            initModule(module);
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
    public void installInto(ModuleContainer mc) {
        parent = mc;
        if (!eagerInitialization) {
            initializeModules();
        }
    }

    /**
     * sets to the given parent, if this module container contains
     * {@link InfoStream} this parent change will be notified via the
     *
     * @param mc
     */
    protected void setParent(ModuleContainer mc) {
        ModuleContainer oldParent = parent;
        parent = mc;

        InfoStream is = get(InfoStream.class);
        if (is != null) {
            is.writeIfListening(() -> new ModuleContainerParentChangedInfo(this), ModuleContainerParentChangedInfo.class);
        }
        if (oldParent != null) {
            is = oldParent.get(InfoStream.class);
            if (is != null) {
                is.writeIfListening(() -> new ModuleContainerParentChangedInfo(this), ModuleContainerParentChangedInfo.class);
            }
        }
    }

    private class ModuleCollectionHack implements Collection<Module> {

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
            return Collections.EMPTY_LIST.iterator();
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
            install(e);
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
//            supplied.clear();
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
            install(key, e);
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

    private static class ModuleInfo {

        ArrayList<Class> registration = new ArrayList<>();

        public ModuleInfo() {
        }

        public ModuleInfo(Class... registrations) {
            this.registration.addAll(Arrays.asList(registrations));
        }

    }

}
