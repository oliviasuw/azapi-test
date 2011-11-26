/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.tests;

import bc.proxies.Operation;
import bc.proxies.Wrapper;
import bc.proxies.Wrapper.WrapConfigurator;

/**
 *
 * @author bennyl
 */
public class Test {
    
    public static void main(String[] args){
        IFC impl = new IMPL();
        WrapConfigurator<IFC> conf = Wrapper.configure(IFC.class);
        conf.perform(new Operation() {

            @Override
            public void operate(Object[] args) {
                System.out.println("WRAPPED");
            }
        }).before().bla();
        impl = conf.applyOn(impl);
        
        impl.bla();
    }
    
    public static interface IFC{
        void work();
        int bla();
    }
    
    public static class IMPL implements IFC{

        @Override
        public void work() {
            System.out.println("REAL");
        }

        @Override
        public int bla() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
}
