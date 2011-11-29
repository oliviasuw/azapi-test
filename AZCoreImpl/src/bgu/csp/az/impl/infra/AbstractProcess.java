/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.infra;

import bgu.csp.az.api.infra.EventPipe;
import bgu.csp.az.api.infra.Process;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.HashMap;

/* TODO: remove event pipe implementation in favor of piped event bus implementation event bus */
/**
 * process is the basic unit that can run 
 * @author bennyl
 */
public abstract class AbstractProcess implements Process {

    private static final Gson jsonBuilder = new Gson();
    private EventPipe<JsonElement> epipe = null;
    private boolean finished;
    private Thread executingThread;

    public AbstractProcess() {
        finished = false;
    }

    @Override
    public void setEventPipe(EventPipe epipe) {
        this.epipe = epipe;
    }

    @Override
    public EventPipe getEventPipe() {
        return epipe;
    }

    @Override
    public void fire(String name, Object... params) {
        Object val;
        String key;
        if (epipe != null) {
            HashMap<String, Object> paramsmap = new HashMap<String, Object>();
            paramsmap.put("event", name);
            for (int i = 0; i < params.length; i += 2) {
                val = params[i + 1];

                if (val instanceof Exception) {
                    val = new FlatException((Exception) val);
                }

                key = params[i].toString();
                paramsmap.put(key, val);
            }

            epipe.append(jsonBuilder.toJsonTree(paramsmap));
            //System.out.println("Json Cerated: " + jsonBuilder.toJson(paramsmap));

        }
    }

    public void fire(JsonElement element) {
        epipe.append(element);
    }

    @Override
    public void stop() {
        if (executingThread != null) {
            executingThread.interrupt();
        }
    }

    @Override
    public void run() {
        finished = false;
        executingThread = Thread.currentThread();
        _run();
        finished = true;
    }

    protected abstract void _run();

    @Override
    public boolean isFinished() {
        return finished;
    }

    public static class FlatException {

        String message;
        String causeMessage;
        String exceptionName;
        String causeExceptionName;

        public FlatException(Exception ex) {
            this.message = ex.getMessage();
            this.exceptionName = ex.getClass().getSimpleName();
            final Throwable cause = ex.getCause();

            if (cause != null) {
                this.causeMessage = cause.getMessage();
                this.causeExceptionName = cause.getClass().getSimpleName();
            }
        }

        public String getMessage() {
            return message;
        }

        public String getCauseMessage() {
            return causeMessage;
        }
    }
}
