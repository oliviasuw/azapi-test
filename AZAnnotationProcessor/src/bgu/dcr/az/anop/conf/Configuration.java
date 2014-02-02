/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.conf;

import java.util.Collection;

/**
 * class that represent a configuration of a specific class this class allow you
 * to examine the class needed properties set their values, using the
 * ConfigurationUtils you can also save and load those settings to and from xml
 * and finally generate an object based on this configuration
 *
 * this class can also be generated with a visual data - this may allow a UI to
 * show and modify this configuration with good visual feedback to the user
 *
 * @author Benny Lutati
 */
public interface Configuration extends Iterable<Property> , Documented{

    Collection<Property> properties();

    TypeInfo typeInfo();

    VisualData visualData();

    <T> T create() throws ConfigurationException;
    
    void configure(Object o) throws ConfigurationException;

    Property get(String name);

    Configuration loadFrom(Object o) throws ConfigurationException;
}
