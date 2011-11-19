/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.frm;

import bc.dsl.ReflectionDSL;
import bgu.csp.az.api.exp.InvalidValueException;
import bgu.csp.az.api.infra.Configureable;
import bgu.csp.az.api.infra.Experiment;
import bgu.csp.az.api.infra.Round;
import bgu.csp.az.api.infra.VariableMetadata;
import bgu.csp.az.impl.Registary;
import bgu.csp.az.impl.infra.ExperimentImpl;
import java.io.File;
import static bc.dsl.XNavDSL.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Element;
import nu.xom.ParsingException;

/**
 *
 * @author bennyl
 */
public class TestXMLReader {

    public static Experiment read(File from) throws IOException, InstantiationException, IllegalAccessException {
        try {
            Experiment exp = new ExperimentImpl();
            Element root = xload(from).getRootElement();

            configure(exp, root);
            
            return exp;

        } catch (ParsingException ex) {
            Logger.getLogger(TestXMLReader.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException("cannot parse file", ex);
        }
    }

    private static void configure(Configureable c, Element root) throws InstantiationException, IllegalAccessException, InvalidValueException {
        Configureable cc;
        for (Element child : childs(root)) {
            String name = child.getLocalName();
            Class cls = Registary.UNIT.getXMLEntity(name);
            if (cls == null) {
                throw new InvalidValueException("cannot parse " + name + " xml entity: no entity with that name on the registery");
            } else if (!c.canAccept(cls)) {
                throw new InvalidValueException("element '" + root.getLocalName() + "' cannot contain child '" + name + "'");
            } else {
                cc = (Configureable) cls.newInstance();
                configure(cc, child);
                c.addSubConfiguration(cc);
            }
        }
        HashMap<String, Object> conf = new HashMap<String, Object>();
        VariableMetadata[] evar = c.provideExpectedVariables();
        Map<String, VariableMetadata> varmap = new HashMap<String, VariableMetadata>();

        for (VariableMetadata v : evar) {
            varmap.put(v.getName(), v);
        }
        VariableMetadata var;

        for (Entry<String, String> a : attributes(root).entrySet()) {
            if (varmap.containsKey(a.getKey())) {
                var = varmap.get(a.getKey());
                Object value = ReflectionDSL.valueOf(a.getValue(), var.getType());
                conf.put(a.getKey(), value);
            } else {
                System.out.println("found attribute '" + a.getKey() + "' in element '" + root.getLocalName() + "' but this element not expecting this attribute - ignoring.");
            }
        }
        c.configure(conf);
    }
    
    public static void main(String[] args) throws Exception{
        Experiment exp = read(new File("exp.xml"));
        for (Round r : exp.getRounds()){
            System.out.println(r.toString());
        }
    }
}
