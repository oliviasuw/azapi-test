/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.scan;

import bc.dsl.JavaDSL;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 *
 * @author bennyl
 */
public enum BamRegistary {

    UNIT;
    Map<EQA, Map<EQA, Class>> adapters = new HashMap<EQA, Map<EQA, Class>>();
    private Pattern p;

    private BamRegistary() {
        Reflections ref = new Reflections(new ConfigurationBuilder().addUrls(ClasspathHelper.forPackage("resources.adapt")).setScanners(new SubTypesScanner()));
        Set<Class<? extends Adapter>> types = ref.getSubTypesOf(Adapter.class);

        p = Pattern.compile("-[^\\s]+");

        for (Class<? extends Adapter> type : types) {
            Adapt ano = type.getAnnotation(Adapt.class);
            if (ano != null) {
                if (type.isInterface() || type.isAnonymousClass() || Modifier.isAbstract(type.getModifiers())) {
                    System.out.println("Found Abstract Registered Item - ignoring it: " + type.getSimpleName());
                } else {
                    System.out.println("found adapter " + type.getSimpleName() + " from " + ano.from() + " to " + ano.to());
                    EQA from = new EQA(p.split(ano.from()));
                    EQA to = new EQA(p.split(ano.to()));

                    JavaDSL.innerMap(adapters, from).put(to, type);
                }
            }
        }

    }

    public Adapter getAdapter(String from, String to) {
        EQA efrom = new EQA(p.split(from));
        Map<EQA, Class> a = adapters.get(efrom);
        Adapter c;
        Matcher m1;
        Matcher m2;
        if (a != null) {
            EQA eto = new EQA(p.split(to));
            Class b = a.get(eto);
            if (b != null) {
                try {
                    c = (Adapter) b.newInstance();
                    m1 = p.matcher(from);
                    m2 = p.matcher(to);
                    Map<String, String> params = new HashMap<String, String>();
                    while (m1.find() && m2.find()) {
                        params.put(m1.group(), m2.group());
                    }
                    c.setParams(params);

                    return c;
                } catch (InstantiationException ex) {
                    Logger.getLogger(BamRegistary.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(BamRegistary.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        System.err.println("no adapter found from " + from + " to " + to);
        return null;
    }

    public static void main(String[] args) {
        UNIT.adapters.get(BamRegistary.class);
    }

    public static class EQA {

        String[] array;

        public EQA(String... array) {
            this.array = array;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof EQA && Arrays.equals(((EQA) obj).array, this.array);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + Arrays.deepHashCode(this.array);
            return hash;
        }
    }
}
