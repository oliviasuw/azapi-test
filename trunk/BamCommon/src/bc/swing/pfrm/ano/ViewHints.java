/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bc.swing.pfrm.ano;

import bc.swing.pfrm.params.ParamView;
import bc.swing.pfrm.params.views.PagePV;
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
public @interface ViewHints {
    Orianitation orianitation() default Orianitation.UNDEF;
    int minimumHeight() default -1;
    
    /**
     * on components like list - allow to press delete and remove the selection 
     * @return 
     */
    boolean allowDeleteSelection() default false;
    
    /**
     * on layouts like stack and form - height presantege (vs other components)
     * @return 
     */
    int heightExpandPrecentage() default -1;
    
    /**
     * define drag and drop
     * @return 
     */
    DND dnd() default DND.UNDEF;
    
    /**
     * created for table - there are cases when you want tables to have their first column width fixed
     * @return 
     */
    int firstColumnWidth() default -1;
    
    /**
     * affect mainly XObject parameters panel - show the varbox or not..
     * @return 
     */
    boolean withVars() default true;
        
    boolean showDescription() default true;

    /**
     * designed for the XObject
     * @return
     */
    String xmlMetadataPath() default "";

    public static enum Orianitation{
        UNDEF,
        HORIZONTAL,
        VERTICAL
    }
    
    public static enum DND{
        UNDEF,
        DRAG,
        DROP,
        DRAG_AND_DROP
    }
}
