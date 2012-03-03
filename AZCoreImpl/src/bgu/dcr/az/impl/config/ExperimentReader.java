/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.config;

import bc.dsl.ReflectionDSL;
import bgu.dcr.az.api.ano.Configuration;
import bgu.dcr.az.api.ano.Configuration.ConfigurationMetadata;
import bgu.dcr.az.api.exp.InvalidValueException;
import bgu.dcr.az.api.infra.Experiment;
import bgu.dcr.az.api.infra.Test;
import bgu.dcr.az.api.infra.VariableMetadata;
import bgu.dcr.az.impl.infra.ExperimentImpl;
import java.io.File;
import static bc.dsl.XNavDSL.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.ParsingException;

/**
 *
 * @author bennyl
 */
public class ExperimentReader {

    public static void write(Object conf, PrintWriter pw) {
        String confName = Registery.UNIT.getEntityName(conf);
        Element e = new Element(confName);
        write(conf, e);

        pw.append(e.toXML());
    }

    private static void write(Object conf, Element root) {
        for (VariableMetadata v : VariableMetadata.scan(conf)) {
            root.addAttribute(new Attribute(v.getName(), v.getCurrentValue().toString()));
        }


        for (ConfigurationMetadata c : Configuration.ConfigurationMetadata.scan(conf.getClass())) {
            List list;
            if (c.isList) {
                 list = (List) c.get(conf);
            } else {
                list = Arrays.asList(c.get(conf));
            }

            for (Object i : list) {
                if (i == null){
                    continue;
                }
                String confName = Registery.UNIT.getEntityName(i);
                Element e = new Element(confName);
                write(i, e);
                root.appendChild(e);
            }

        }
    }

    public static Experiment read(File from) throws IOException, InstantiationException, IllegalAccessException {
        try {
            Experiment exp = new ExperimentImpl();
            Element root = xload(from).getRootElement();

            configure(exp, root);
            
            ConfigurationMetadata.notifyAfterExternalConfiguration(exp);
            return exp;

        } catch (ParsingException ex) {
            Logger.getLogger(ExperimentReader.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException("cannot parse file", ex);
        }
    }

    private static void configure(Object c, Element root) throws InstantiationException, IllegalAccessException, InvalidValueException {
        Object cc;
        for (Element child : childs(root)) {
            String name = child.getLocalName();
            Class cls = Registery.UNIT.getXMLEntity(name);
            if (cls == null) {
                throw new InvalidValueException("cannot parse " + name + " xml entity: no entity with that name on the registery");
            } else if (! ConfigurationMetadata.canAccept(c, cls)) {
                throw new InvalidValueException("element '" + root.getLocalName() + "' cannot contain child '" + name + "'");
            } else {
                cc = cls.newInstance();
                configure(cc, child);
                ConfigurationMetadata.insertConfiguration(c, cc);
            }
        }
        HashMap<String, Object> conf = new HashMap<String, Object>();
        VariableMetadata[] evar = VariableMetadata.scan(c);
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
        VariableMetadata.assign(c, conf);
    }

    public static void main(String[] args) throws Exception {
        Experiment exp = read(new File("exp.xml"));
        for (Test r : exp.getTests()) {
            System.out.println(r.toString());
        }
    }
}
