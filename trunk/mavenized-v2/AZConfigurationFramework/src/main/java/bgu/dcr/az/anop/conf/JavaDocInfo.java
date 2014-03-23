/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.conf;

import java.util.Collection;

/**
 *
 * @author User
 */
public interface JavaDocInfo extends Iterable<String> {

    /**
     * @return the description of the element without any annotated tags
     */
    String description();

    /**
     * @param tag
     * @return the descriptions provided in the annotated tag
     */
    Collection<String> tag(String tag);
    
    /**
     * 
     * @param tag
     * @return the first occurence of the given tag or null if no such exists
     */
    String first(String tag);

    /**
     * @return list of all the annotated tag names in this javadoc
     */
    Collection<String> tags();
}
