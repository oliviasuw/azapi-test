/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.infra.Test;
import bgu.dcr.az.api.infra.VariableMetadata;
import bgu.dcr.az.api.infra.stat.StatisticCollector;
import bgu.dcr.az.api.pgen.ProblemGenerator;
import bgu.dcr.az.impl.AlgorithmMetadata;
import bgu.dcr.az.impl.Registery;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public class Item {

    public String type = "Item";
    public String display;
    public String name;
    public String description = "";
    public List<Var> vars = new LinkedList<Var>();

    private Item(Class c, boolean isAlgorithm) {
        if (!isAlgorithm) {
            Register ano = (Register) c.getAnnotation(Register.class);
            this.display = ano.display();
            this.name = ano.name();
        } else {

            Algorithm ano = (Algorithm) c.getAnnotation(Algorithm.class);
            this.display = ano.name();
            this.name = "__ALGORITHM__";
        }

        VariableMetadata[] mvars = VariableMetadata.scan(c);
        for (VariableMetadata mv : mvars) {
            vars.add(new Var(mv.getName(), "" + mv.getCurrentValue(), "" + mv.getType(), mv.getDescription()));
        }
    }

    public static List<Item> getItems(String type) throws ItemResolveException, UnknownItemTypeException {
        Registery.UNIT.scanEntityInheritance();
        List<Item> ret = new LinkedList<Item>();
        Set<String> resolvedEntities = null;
        boolean isAlgorithm = false;

        if (type.equals(Test.class.getSimpleName())) {
            resolvedEntities = Registery.UNIT.getExtendingEntities(Test.class);
        } else if (type.equals(ProblemGenerator.class.getSimpleName())) {
            resolvedEntities = Registery.UNIT.getExtendingEntities(ProblemGenerator.class);
        } else if (type.equals(AlgorithmMetadata.class.getSimpleName())) {
            resolvedEntities = Registery.UNIT.getAllAlgorithmNames();
            isAlgorithm = true;
        } else if (type.equals(StatisticCollector.class.getSimpleName())) {
            resolvedEntities = Registery.UNIT.getExtendingEntities(StatisticCollector.class);
        } else {
            throw new UnknownItemTypeException("unknown type: " + type);
        }

        for (String i : resolvedEntities) {
            if (!isAlgorithm) {
                ret.add(new Item(Registery.UNIT.getXMLEntity(i), isAlgorithm));
            } else {
                ret.add(new Item(Registery.UNIT.getAgentByAlgorithmName(i), isAlgorithm));
            }
        }

        return ret;
    }

    public static class UnknownItemTypeException extends Exception {

        public String type = "UnknownItemTypeException";

        public UnknownItemTypeException(Throwable cause) {
            super(cause);
        }

        public UnknownItemTypeException(String message, Throwable cause) {
            super(message, cause);
        }

        public UnknownItemTypeException(String message) {
            super(message);
        }

        public UnknownItemTypeException() {
        }
    }

    public static class ItemResolveException extends Exception {

        public String type = "ItemResolveException";

        public ItemResolveException(Throwable cause) {
            super(cause);
        }

        public ItemResolveException(String message, Throwable cause) {
            super(message, cause);
        }

        public ItemResolveException(String message) {
            super(message);
        }

        public ItemResolveException() {
        }
    }
}
