/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bc.swing.pfrm.ano;

import bc.swing.pfrm.viewtypes.ParamType;
import bc.swing.pfrm.ParamView;
import bc.swing.pfrm.views.CustomPV;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author BLutati
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Param {
    String name();
    String icon() default "";
    ParamType type() default ParamType.CUSTOM;
    String role() default "";
    String baseName() default "";
    Class<? extends ParamView> customView() default CustomPV.class;
    ViewHints vhints() default @ViewHints;
}
