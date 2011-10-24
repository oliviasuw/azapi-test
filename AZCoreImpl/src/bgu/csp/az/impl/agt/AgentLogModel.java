/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.agt;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author Inna
 */
public class AgentLogModel extends HTMLDocument {

    private static int LOG_RECORDS_SIZE_LIMIT = 4;
    private HTMLEditorKit kit;
    private int numLogRecords = 0;
    private int lastElementEndIndex = 0;
    private int lastElementStartIndex = 0;
    boolean isEmpty = true;

    @SuppressWarnings("LeakingThisInConstructor")
    public AgentLogModel(HTMLEditorKit kit) {
        super();
        this.kit = kit;
    }

    private void appendToTableEnd(String what) {
        try {
            if (this.numLogRecords < LOG_RECORDS_SIZE_LIMIT) {
                String html = "<span id='" + this.numLogRecords + "'>" + what + "</span>";
                System.out.println(html);
                kit.insertHTML(this, lastElementEndIndex, html, 0, 0, null);
                lastElementEndIndex = this.getElement(String.valueOf(this.numLogRecords)).getEndOffset();
                lastElementStartIndex = this.getElement(String.valueOf(this.numLogRecords)).getStartOffset();
                this.numLogRecords++;
                return;
            }
            this.replace(this.lastElementStartIndex, this.lastElementEndIndex-this.lastElementStartIndex, "", null);
            lastElementEndIndex = lastElementStartIndex;
            this.numLogRecords--;
            appendToTableEnd(what);

        } catch (BadLocationException ex) {
            Logger.getLogger(AgentLogModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AgentLogModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String addLog(String agentName, String text, Level lvl) {
        String logRow = createLogRow(agentName, text, lvl);
        appendToTableEnd(logRow);
        return logRow;
    }

    private String createLogRow(String agentName, String text, Level lvl) {
        String ans = "";
        ans = ans + "<span id='f" + this.numLogRecords + "' style=color:" + selectAgentColor(agentName) + ">" + agentName + "</span>";
        ans = ans + "<span id='s" + this.numLogRecords + "' style=color:" + selectSeverityColor(text) + ">" + text + "</span>";
        return ans;
    }

    public static void main(String args[]) throws BadLocationException {
        JFrame f = new JFrame("JEditorPane Sample");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container content = f.getContentPane();
        JEditorPane editor = new JEditorPane();
        HTMLEditorKit k = new HTMLEditorKit();
        AgentLogModel alm = new AgentLogModel(k);
        editor.setEditorKit(k);
        editor.setDocument(alm);

        alm.addLog("agent1", "doneSomething1", Level.INFO);
        alm.addLog("agent2", "doneSomething2", Level.INFO);
        alm.addLog("agent3", "doneSomething3", Level.INFO);
        alm.addLog("agent3", "doneSomething4", Level.INFO);
        alm.addLog("agent3", "doneSomething5", Level.INFO);




//        alm.appendToTableEnd("hello world1");
//        alm.appendToTableEnd("hello world2");
//        alm.appendToTableEnd("hello world3");
//        alm.appendToTableEnd("hello world4");
//        alm.appendToTableEnd("hello world5");

        editor.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(editor);
        content.add(scrollPane, BorderLayout.CENTER);
        f.setSize(300, 200);
        f.setVisible(true);
    }

    private String selectAgentColor(String agentName) {
        return "blue";
    }

    private String selectSeverityColor(String text) {
        return "red";
    }
}
