/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bc.swing.pfrm.ano;

import bc.swing.pfrm.params.ParamType;
import bc.swing.pfrm.params.ParamView;
import bc.swing.pfrm.params.views.CustomPV;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author BLutati
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ParamExtractor {
    Param[] params();
}
