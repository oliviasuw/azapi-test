/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model.impl;

import bgu.dcr.az.execs.orm.api.Data;
import bgu.dcr.az.pivot.model.TableData;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author User
 */
public class SimplePivot extends AbstractPivot {

    public SimplePivot(Data data) {
        super(data);
    }

    @Override
    public Service<TableData> getPivotedDataService() {
        return new PivotGenerationService(this);

    }

    private static class PivotGenerationService extends Service<TableData> {

        private final AbstractPivot pivot;

        public PivotGenerationService(AbstractPivot pivot) {
            this.pivot = pivot;
        }

        @Override
        protected Task<TableData> createTask() {
            return new InMemoryPivotTableGenerator(pivot);
        }
    }
}
