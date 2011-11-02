/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.pui;

import bc.swing.models.LimitedBatchDocument;
import java.awt.Color;
import java.util.Formatter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author bennyl
 */
public class AgentLogDocument extends LimitedBatchDocument {
    public static final String LEFT_JUSTIFIED_SPACED_FORMAT = "%-18s";
    public static final String REPEAT_INDICATOR_STRING = "#";
    private static final Color AGENT_NAME_BACKGROUND = new Color(245,245,245);

    private String lastAgent = "";
    private HashMap<String, SimpleAttributeSet> AttributeSets;
    private Color[] colors;
    StringBuilder tokenBuilder = new StringBuilder();
    private Formatter formatter = new Formatter(tokenBuilder);

    public AgentLogDocument() {
        super();
        this.AttributeSets = new HashMap<String, SimpleAttributeSet>();
        generateColors();

    }

    private String createToken(String format, Object... args) {
        tokenBuilder.setLength(0);
        formatter.format(format, args);
        return tokenBuilder.toString();
    }

    public synchronized void addLog(String agentName, String text, Level lvl) {
        for (String t : text.split("\n")){
            addSingleLineLog(agentName, " " + t, lvl);
        }
    }
    
    private synchronized void addSingleLineLog(String agentName, String text, Level lvl){
        addColor(agentName);


        if (lastAgent.equals(agentName)) {
            String newAgentName = createToken(LEFT_JUSTIFIED_SPACED_FORMAT, REPEAT_INDICATOR_STRING);
            appendBatchString(newAgentName, styleRepeatSpacing(agentName));

        } else {
            String newAgentName = createToken(LEFT_JUSTIFIED_SPACED_FORMAT, agentName + ":");
            appendBatchString(newAgentName, styleAgentName(agentName));
            lastAgent = agentName;
        }

        
        //StringBuilder sb = parseLogText(text);
        appendBatchString(text+"\n", styleText(lvl));
//        appendBatchLineFeed(null);
    }

    private void generateColors() {

        colors = new Color[]{
            new Color(0,51,255),
            new Color(102,153,0),
            new Color(204,0,102),
            new Color(37,175,228),
            new Color(153,153,0),
            new Color(150,0,210),
            new Color(153,153,153),
            new Color(255,153,51),
            new Color(245,151,151),
            /***************************/
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
            Color.BLACK,
            new Color(255, 0, 0),
            new Color(255, 204, 0)};




    }

    private AttributeSet styleRepeatSpacing(String agentName) {
        SimpleAttributeSet ans = this.AttributeSets.get(agentName);
        StyleConstants.setUnderline(ans, false);
        return ans;

    }

    private AttributeSet styleAgentName(String agentName) {
        SimpleAttributeSet ans = this.AttributeSets.get(agentName);
        StyleConstants.setUnderline(ans, false);
        StyleConstants.setBackground(ans, AGENT_NAME_BACKGROUND);

//        StyleConstants.setUnderline(ans, true);
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
}
