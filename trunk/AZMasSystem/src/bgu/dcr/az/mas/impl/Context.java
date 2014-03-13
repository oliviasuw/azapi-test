/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.impl;

import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.ExecutionService;
import bgu.dcr.az.mas.impl.InitializationException;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author User
 */
public class Context {

    private final long contextId;

    private Context(long contextId) {
        this.contextId = contextId;
    }

    public long getContextId() {
        return contextId;
    }

    public static class ContextGenerator implements ExecutionService {

        private ConcurrentHashMap<String, Context> contextMapper;
        private long contextId;

        public Context getContext(String repr) {
            Context context = contextMapper.get(repr);
            
            if (context == null) {
                contextMapper.putIfAbsent(repr, new Context(contextId++));
                context = contextMapper.get(repr);
            }

            return context;
        }

        @Override
        public void initialize(Execution ex) throws InitializationException {
            contextMapper = new ConcurrentHashMap<>();
            contextId = 0;
        }
    }
}
