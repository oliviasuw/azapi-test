/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.pui;

import bc.swing.models.LimitedBatchDocument;
import java.awt.Color;
import java.util.AbstractMap.SimpleEntry;
import java.util.Formatter;
import java.util.HashMap;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private static final Color AGENT_NAME_BACKGROUND = new Color(71,71,71);
    private static final Color NORMAL_TEXT_COLOR = new Color(51,204,0);

    private String lastAgent = "";
    private HashMap<String, SimpleAttributeSet> AttributeSets;
    private Color[] colors;
    StringBuilder tokenBuilder = new StringBuilder();
    private Formatter formatter = new Formatter(tokenBuilder);

    private Matcher match=null;
    
    
    public AgentLogDocument() {
        super();
        this.AttributeSets = new HashMap<String, SimpleAttributeSet>();
        generateColors();

    }

    public SimpleEntry<Integer,Integer> search(String what,boolean ragex,boolean matchCase,int offset){
            Pattern p =(Pattern.compile(what,(matchCase ? Pattern.LITERAL :Pattern.CASE_INSENSITIVE)));
            try {
                match=p.matcher(getText(offset, getLength()-offset));
            } catch (BadLocationException ex) {
                Logger.getLogger(AgentLogDocument.class.getName()).log(Level.SEVERE, null, ex);
                return new SimpleEntry<Integer,Integer>(-1,-1);
            }
            return (match.find()? new SimpleEntry<Integer,Integer>(match.start(),match.end()):new SimpleEntry<Integer,Integer>(-1,-1));
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
            new Color(50,101,255),
            new Color(152,203,50),
            new Color(204,50,152),
            new Color(87,225,255),
            new Color(203,203,50),
            new Color(200,50,255),
            new Color(203,203,203),
            new Color(255,203,101),
            new Color(255,201,201),
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
            StyleConstants.ColorConstants.setForeground(textColor, NORMAL_TEXT_COLOR);
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
