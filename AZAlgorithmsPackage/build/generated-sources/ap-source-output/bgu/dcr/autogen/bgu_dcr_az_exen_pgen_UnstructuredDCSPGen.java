package bgu.dcr.autogen;

import bgu.dcr.az.anop.conf.TypeInfo;
import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.conf.VisualData;
import bgu.dcr.az.anop.conf.impl.AbstractConfiguration;
import bgu.dcr.az.anop.conf.impl.PropertyImpl;
import bgu.dcr.az.anop.conf.impl.VisualDataImpl;
import bgu.dcr.az.anop.utils.JavaTypeParser;
import bgu.dcr.az.anop.conf.ConfigurationException;
import java.util.HashMap;
import java.util.Collection;
import java.util.Map;
import bgu.dcr.az.anop.conf.JavaDocInfo;
import bgu.dcr.az.anop.utils.JavaDocParser;




public class bgu_dcr_az_exen_pgen_UnstructuredDCSPGen extends AbstractConfiguration{

    public static final TypeInfo TYPE_INFO = JavaTypeParser.parse("bgu.dcr.az.exen.pgen.UnstructuredDCSPGen");
    
    public static final TypeInfo P1_TYPE_INFO = JavaTypeParser.parse("java.lang.Float");
    public static final JavaDocInfo P1_JAVADOC_INFO = JavaDocParser.parse(" @propertyName p1\n @return\n");
    
    public static final TypeInfo D_TYPE_INFO = JavaTypeParser.parse("java.lang.Integer");
    public static final JavaDocInfo D_JAVADOC_INFO = JavaDocParser.parse(" @propertyName d\n @return\n");
    
    public static final TypeInfo N_TYPE_INFO = JavaTypeParser.parse("java.lang.Integer");
    public static final JavaDocInfo N_JAVADOC_INFO = JavaDocParser.parse(" @propertyName n\n @return\n");
    
    public static final TypeInfo P2_TYPE_INFO = JavaTypeParser.parse("java.lang.Float");
    public static final JavaDocInfo P2_JAVADOC_INFO = JavaDocParser.parse(" @propertyName p2\n @return\n");
    
    public static final JavaDocInfo JAVADOC = JavaDocParser.parse("\n @author bennyl\n");

    
    public bgu_dcr_az_exen_pgen_UnstructuredDCSPGen() {
        this.properties = new HashMap<>();
        
        //fill properties
        VisualData vd = null;
        
        properties.put("p1", new PropertyImpl("p1", this, P1_TYPE_INFO , P1_JAVADOC_INFO));
        
        properties.put("d", new PropertyImpl("d", this, D_TYPE_INFO , D_JAVADOC_INFO));
        
        properties.put("n", new PropertyImpl("n", this, N_TYPE_INFO , N_JAVADOC_INFO));
        
        properties.put("p2", new PropertyImpl("p2", this, P2_TYPE_INFO , P2_JAVADOC_INFO));
        
        

        this.type = TYPE_INFO;
    }

    @Override
    public Object create() throws ConfigurationException {
        bgu.dcr.az.exen.pgen.UnstructuredDCSPGen result = new bgu.dcr.az.exen.pgen.UnstructuredDCSPGen();

        configure(result);
        
        return result;
    }

    @Override
    public void configure(Object obj) throws ConfigurationException {
        bgu.dcr.az.exen.pgen.UnstructuredDCSPGen o = (bgu.dcr.az.exen.pgen.UnstructuredDCSPGen) obj;
        Property property = null;
        
        
        property = properties.get("p1");
        if (property.get() != null){
            try{          
                
                o.setP1( property.get().<java.lang.Float>create(property.typeInfo()));
                
            } catch (Exception ex){
                throw new ConfigurationException("cannot configure property p1", ex);
            }
        }
        
        property = properties.get("d");
        if (property.get() != null){
            try{          
                
                o.setD( property.get().<java.lang.Integer>create(property.typeInfo()));
                
            } catch (Exception ex){
                throw new ConfigurationException("cannot configure property d", ex);
            }
        }
        
        property = properties.get("n");
        if (property.get() != null){
            try{          
                
                o.setN( property.get().<java.lang.Integer>create(property.typeInfo()));
                
            } catch (Exception ex){
                throw new ConfigurationException("cannot configure property n", ex);
            }
        }
        
        property = properties.get("p2");
        if (property.get() != null){
            try{          
                
                o.setP2( property.get().<java.lang.Float>create(property.typeInfo()));
                
            } catch (Exception ex){
                throw new ConfigurationException("cannot configure property p2", ex);
            }
        }
        
        configureVariables(o);
    }    
}
