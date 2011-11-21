/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.infra.stat;

import java.io.File;

/**
 *
 * @author bennyl
 */
public interface VisualModel {
    String getTitle();
    void exportToCSV(File csv);
}
