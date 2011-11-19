/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev;

import bgu.csp.az.api.infra.EventPipe;
import bgu.csp.az.impl.infra.ExperimentImpl;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public class EventServer extends Thread {

    private static final Gson jsonBuilder = new Gson();
    
    public static final int DEFAULT_PORT = 9090;
    private int port;
    private ExperimentImpl exp;
    private EventPipe ep;

    /**
     * will set an event pipe on the expirament
     * @param port
     * @param exp 
     */
    public EventServer(int port, ExperimentImpl exp) {
        this.port = port;
        this.exp = exp;
        this.ep = exp.getEventPipe();
        if (this.ep == null) {
            this.ep = new EventPipe();
            exp.setEventPipe(ep);
        }
    }

    @Override
    public void run() {
        Socket c = null;
        try {
            ServerSocket ss = new ServerSocket(port);
            c = ss.accept();

            BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
            String line;
            OutputStream out = c.getOutputStream();

            while (!Thread.currentThread().isInterrupted() && (line = br.readLine()) != null) {
                if (line.startsWith("take")) {
                    out.write(toJsonArray(ep.takeAll()));
                    if (exp.isFinished()) {
                        out.write("\"BYE\"".getBytes());
                    }
                } else {
                    System.out.println("Event Server Cannot Parse Message: " + line + " dropping..");
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(EventServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.close();
                } catch (IOException ex) {
                    Logger.getLogger(EventServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private byte[] toJsonArray(List<JsonElement> elements){
        StringBuilder sb = new StringBuilder("[");
        boolean delete = false;
        for (JsonElement e : elements){
            delete = true;
            sb.append(e.toString()).append(",");
        }
        if (delete) sb.delete(sb.length()-1, sb.length());
        sb.append("]");
        return sb.toString().getBytes();
    }

//    private byte[] toJson(List<Event> events) {
//        StringBuilder sb = new StringBuilder("[");
//        for (Event e : events) {
//            sb.append(toJson(e)).append(",");
//        }
//        if (!events.isEmpty()) {
//            sb.delete(sb.length() - 1, sb.length());
//        }
//        sb.append("]");
//
//        return sb.toString().getBytes();
//    }
//
//    private String toJson(Event e) {
//        StringBuilder sb = new StringBuilder("{");
//        writeKV(sb, "event", e.getName());
//        sb.append(",");
//        for (Entry<String, String> kv : e.getParams().entrySet()) {
//            writeKV(sb, kv.getKey(), kv.getValue());
//            sb.append(",");
//        }
//
//        sb.delete(sb.length() - 1, sb.length());
//        sb.append("}");
//        return sb.toString();
//    }
//
//    private void writeKV(StringBuilder sb, String k, String v) {
//        sb.append("\"").append(k).append("\":\"").append(v.replace("\"", "\\\"")).append("\"");
//    }
}
