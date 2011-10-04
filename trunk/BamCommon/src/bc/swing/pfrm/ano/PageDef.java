/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bc.swing.pfrm.ano;

import bc.swing.pfrm.PageView;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author BLutati
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PageDef {
    String name() default "UNKNOWN";
    String icon() default "";
    Class<? extends PageView> view() default DefaultPageView.class;


    public static abstract class DefaultPageView implements PageView{}
}
