/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.ui.statistics;

import bgu.csp.az.dev.ui.pages.ExecutionStatisticalPage;
import bam.utils.ui.mvc.pages.PagePart;

/**
 *
 * @author bennyl
 */
public abstract class StatisticalPagePart extends PagePart implements ExecutionStatisticalPage.Listener {

    public StatisticalPagePart(String name, String description) {
        super(name, description);
    }
    
}
