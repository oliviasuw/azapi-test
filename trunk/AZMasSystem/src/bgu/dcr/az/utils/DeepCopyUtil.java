/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.utils;

import bgu.dcr.az.api.DeepCopyable;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 *
 * @author bennyl
 */
public class DeepCopyUtil {

//    private static Cloner cloner = new Cloner();
    private static ThreadLocal<Kryo> kloner;
//    private static com.ajexperience.utils.DeepCopyUtil dcu = null;

    static {

        kloner = new ThreadLocal<Kryo>() {

            @Override
            protected Kryo initialValue() {
                Kryo k = new Kryo();
                k.setAsmEnabled(true);
                k.setRegistrationRequired(false);
                k.setInstantiatorStrategy(new StdInstantiatorStrategy());
                k.addDefaultSerializer(DeepCopyable.class, new Serializer() {

                    @Override
                    public void write(Kryo kryo, Output output, Object t) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public Object read(Kryo kryo, Input input, Class type) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public Object copy(Kryo kryo, Object original) {
                        return ((DeepCopyable)original).deepCopy();
                    }
                    
                    
                });
                k.setReferences(false);
                return k;
            }

        };

//        try {
//            dcu = new com.ajexperience.utils.DeepCopyUtil();
//        } catch (DeepCopyException ex) {
//            Logger.getLogger(DeepCopyUtil.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    /**
     * @param <T>
     * @param orig
     * @return deep copy of orig using a generic deep copy framework
     */
    public static <T> T deepCopy(T orig) {

//        if (orig instanceof Enum || orig instanceof Throwable) {
//            return orig;
//        }

//        if (orig instanceof DeepCopyable) {
//            return (T) ((DeepCopyable) orig).deepCopy();
//        }

//        try {
//            return dcu.deepCopy(orig);//cloner.deepClone(orig);
//            return cloner.deepClone(orig);
        return kloner.get().copy(orig);
//        } catch (DeepCopyException ex) {
//            Agt0DSL.throwUncheked(ex);
//            return null; //SHOULD NEVER HAPPENED...
//        }
    }
}
