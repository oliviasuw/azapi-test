/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.models;

import bc.swing.models.ConsoleModel.Listener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;

/**
 *
 * @author Inna
 */
public class AgentLogConsoleModel extends DefaultStyledDocument {

    private static int LOG_RECORDS_SIZE_LIMIT = 10000;
    private static int LOG_RECORDS_DELETE_SIZE = 1000;
    private int numLogRecords = 0;
    boolean isEmpty = true;
    private String lastAgent = "";
    private int off = 0;
    private HashMap<String, SimpleAttributeSet> AttributeSets;
    private Color[] colors;   
    
    public AgentLogConsoleModel() {
        super();
        this.AttributeSets = new HashMap<String, SimpleAttributeSet>();
        generateColors();
    }


    public void addLog(String agentName, String text, Level lvl) {
        try {
            writeLock();
            
            int preOff = off;
            
            if (this.numLogRecords == this.LOG_RECORDS_SIZE_LIMIT) {
                for (int i = this.LOG_RECORDS_DELETE_SIZE; i > 0; i--) {
                    deleteLine();
                }
            }
            addColor(agentName);
            String newAgentName = "Agent " + agentName + ":";
            if (lastAgent.equals(agentName)) {
                String spacing = repeatSpacing(newAgentName);
                insertString(off, spacing, styleRepeatSpacing(agentName));
                off += spacing.length();
            } else {
                insertString(off, newAgentName, styleAgentName(agentName));
                lastAgent = agentName;
                off += newAgentName.length();
            }
            String newText = parseText(text, newAgentName, spacing(newAgentName));
            insertString(off, newText + "\n", styleText(lvl));
            off += newText.length() + 1;
         //   super.fireInsertUpdate(new DefaultDocumentEvent(preOff, off-preOff, EventType.INSERT));
            this.numLogRecords++;
        } catch (BadLocationException ex) {
            Logger.getLogger(AgentLogConsoleModel.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            writeUnlock();
        }
        
    }

    private void generateColors() {
        colors = new Color[]{
            new Color(51, 153, 255),
            new Color(250, 65, 136),
            new Color(255, 255, 102),
            new Color(51, 255, 51),
            new Color(196, 108, 255),
            new Color(255, 204, 0),
            new Color(2, 170, 170),
            new Color(255, 51, 255),
            new Color(70, 233, 233),
            new Color(205, 205, 80),
            new Color(170, 170, 170),
            new Color(255, 148, 47),
            new Color(0, 247, 105),
            new Color(53, 197, 208),
            new Color(105, 251, 44),
            new Color(255, 170, 134),
            new Color(153, 255, 153),
            new Color(255, 153, 153),
            new Color(153, 153, 255),
            new Color(255, 255, 153),
            new Color(250, 250, 250),
            new Color(255, 0, 0),
            new Color(255, 204, 0)};

    }

    private String parseText(String text, String agentName, String spacing) {
        String tmp[] = text.split("\\n");
        String ans = spacing;
        for (int i = 0; i < tmp.length; i++) {
            ans += tmp[i];
            if (!(i + 1 == tmp.length)) {
                ans += "\n";
                ans += spacing;
                for (int j = 0; j < agentName.length(); j++) {
                    ans += " ";
                }
                this.numLogRecords++;
            }
        }
        return ans;
    }

    private String repeatSpacing(String agentName) {
        int size = agentName.length();
        int mod = size % 4;
        String ans = "#";
        for (int i = mod + size - 1; i > 0; i--) {
            ans += " ";
        }
        return ans;
    }

    private AttributeSet styleRepeatSpacing(String agentName) {
        SimpleAttributeSet ans = this.AttributeSets.get(agentName);
        StyleConstants.setUnderline(ans, false);
        return ans;
    }

    private AttributeSet styleAgentName(String agentName) {
        SimpleAttributeSet ans = this.AttributeSets.get(agentName);
        StyleConstants.setUnderline(ans, true);
        return ans;
    }

    private String spacing(String agentName) {
        int size = agentName.length();
        int mod = size % 4;
        String ans = "";
        for (int i = 3 - mod; i > 0; i--) {
            ans += " ";
        }
        return ans;
    }

    private AttributeSet styleText(Level lvl) {
        SimpleAttributeSet textColor = new SimpleAttributeSet();
        if (lvl.equals(Level.SEVERE)) {
            StyleConstants.ColorConstants.setForeground(textColor, colors[21]);
        } else if (lvl.equals(Level.WARNING)) {
            StyleConstants.ColorConstants.setForeground(textColor, colors[22]);
        } else {
            StyleConstants.ColorConstants.setForeground(textColor, colors[20]);
        }
        return textColor;
    }

    private void addColor(String agentName) {
        if (AttributeSets.containsKey(agentName)) {
            return;
        }
        int index = this.AttributeSets.size();
        index = index % 20;
        Color tmp = colors[index];
        SimpleAttributeSet ats = new SimpleAttributeSet();
        StyleConstants.ColorConstants.setForeground(ats, tmp);
        AttributeSets.put(agentName, ats);
    }

    private void deleteLine() throws BadLocationException {
        int length = this.getLength();
        length = Math.min(length, 200);
        String tmp = this.getText(0, length);
        int index = tmp.indexOf("\n") + 1;
        //System.out.println(index);
        this.remove(0, index);
        off -= index;
        this.numLogRecords--;
    }
}
