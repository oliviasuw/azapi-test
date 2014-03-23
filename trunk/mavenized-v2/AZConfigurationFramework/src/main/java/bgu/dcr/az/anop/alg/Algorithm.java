/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.alg;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * annotation to define the algorithm metadata
 * @author bennyl
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface Algorithm {

    /**
     * @return the algorithm name
     */
    String name();
    
    /**
     * if this set to true the algorithm requires an idle detector
     * @See bgu.csp.az.api.tools.IdleDetector
     * @deprecated do not use - this will be ignored.
     */
    @Deprecated
    boolean useIdleDetector() default false;

}
