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




public class bgu_dcr_az_exen_pgen_ConnectedDCSPGen extends AbstractConfiguration{

    public static final TypeInfo TYPE_INFO = JavaTypeParser.parse("bgu.dcr.az.exen.pgen.ConnectedDCSPGen");
    
    public static final JavaDocInfo JAVADOC = JavaDocParser.parse("\n @author bennyl\n");

    
    private bgu.dcr.autogen.bgu_dcr_az_exen_pgen_UnstructuredDCSPGen parent;
    
    public bgu_dcr_az_exen_pgen_ConnectedDCSPGen() {
        this.properties = new HashMap<>();
        
        //fill properties
        VisualData vd = null;
        
        
        parent = new bgu.dcr.autogen.bgu_dcr_az_exen_pgen_UnstructuredDCSPGen();
        this.properties.putAll(parent.propertiesMap());
        

        this.type = TYPE_INFO;
        
    }

    @Override
    public Object create() throws ConfigurationException {
        bgu.dcr.az.exen.pgen.ConnectedDCSPGen result = new bgu.dcr.az.exen.pgen.ConnectedDCSPGen();

        configure(result);
        
        return result;
    }

    @Override
    public void configure(Object obj) throws ConfigurationException {
        bgu.dcr.az.exen.pgen.ConnectedDCSPGen o = (bgu.dcr.az.exen.pgen.ConnectedDCSPGen) obj;
        Property property = null;
        
        parent.configure(obj);
        
        
        configureVariables(o);
    }    
}
