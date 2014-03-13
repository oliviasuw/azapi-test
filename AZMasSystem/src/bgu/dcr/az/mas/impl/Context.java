/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.impl;

import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.ExecutionService;
import java.util.HashMap;

/**
 *
 * @author User
 */
public class Context {

    private final String contextRepresentation;
    private final long contextId;

    private Context(String contextRepresentation, long contextId) {
        this.contextRepresentation = contextRepresentation;
        this.contextId = contextId;
    }

    public long getContextId() {
        return contextId;
    }

    public String getContextRepresentation() {
        return contextRepresentation;
    }
    
    public static class ContextGenerator implements ExecutionService {

        private HashMap<String, Context> contextMapper;
        private long contextId;

        public synchronized Context getContext(String repr) {
            Context context = contextMapper.get(repr);

            if (context == null) {
                context = new Context(repr, contextId++);
                contextMapper.put(repr, context);
            }

            return context;
        }

        @Override
        public void initialize(Execution ex) throws InitializationException {
            contextMapper = new HashMap<>();
            contextId = 0;
        }
    }
}
