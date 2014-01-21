/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.conf;

/**
 * this class represent a property of a configurable class a property value can
 * be "complex" or "simple", where complex values are held as a configuration
 * that will be able to generate the property value and simple values are a
 * string that can construct the property value via the property type "valueOf"
 * method
 *
 * @author User
 */
public interface Property {

    String name();

    Configuration parent();

    ConfigurableTypeInfo type();

    VisualData visualData();

    void set(PropertyValue cv);

    PropertyValue get();
}
