/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.x.view;

import bc.swing.pfrm.params.BaseParamModel;
import java.awt.GridBagConstraints;
import java.util.List;
import bc.swing.comp.JStackPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import bc.swing.comp.TextualComponent;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import bc.swing.comp.JFilePath;
import bc.swing.comp.JHinteableTextField;
import bc.swing.comp.JIPAddress;
import bc.swing.comp.JLinkButton;
import bc.swing.comp.JNummericField;
import bc.swing.comp.JRepeatPanel;
import bc.swing.comp.JTextualCheckBox;
import bc.swing.comp.JTextualComboBox;
import bc.swing.comp.JYesNoQuestion;
import bc.swing.pfrm.params.ParamView;
import bc.x.model.XFrame;
import bc.x.model.XIf;
import bc.x.model.XLoop;
import bc.x.model.XObject;
import bc.x.model.XParameter;
import static bc.dsl.SwingDSL.*;
import static bc.dsl.JavaDSL.*;
import bc.x.model.SystemVariables;
import nu.xom.Element;

/**
 *
 * @author bennyl
 */
public class XParametersPanel extends JStackPanel implements ParamView{

    public static final String USER_DEFINED_VALUE = "User Defined Value";
    private XObject model;
    private boolean withVars = true;
    
    private void init(List<XObject> fragments) {
        //columns: NAME | COMPONENT | VARIABLE

        for (XObject f : fragments) {
            if (f instanceof XParameter) {
                if (typed(f, "IP")) {
                    addIP((XParameter) f);
                } else if (typed(f, "PORT")) {
                    addPort((XParameter) f);
                } else if (typed(f, "BOOLEAN")) {
                    addBoolean((XParameter) f);
                } else if (typed(f, "STRING")) {
                    addString((XParameter) f);
                } else if (typed(f, "INTEGER")) {
                    addInteger((XParameter) f);
                } else if (typed(f, "YNQ")) {
                    addYNQ((XParameter) f);
                } else if (typed(f, "FILE_PATH")) {
                    addFilePath((XParameter) f);
                } else if (((XParameter) f).getType().startsWith("OPTIONS")) {
                    addOptions((XParameter) f);
                } else {
                    log("Throwing parameter: " + f.getName() + " of type: " + ((XParameter) f).getType());
                }
            } else if (f instanceof XIf) {
                addIf((XIf) f);
            } else if (f instanceof XLoop) {
                addLoop((XLoop) f);
            } else if (f instanceof XFrame) {
                addFrame((XFrame) f);
            } else {
                log("Throwing command fragment: " + f.getName() + " of type: " + f.getClass().getSimpleName());
            }
        }
        if (lastSeperator != null) {
            lastSeperator.setVisible(false);
        }
        seal();
    }

    public XParametersPanel() {
    }

    public void setModel(XObject model) {
        this.model = model;
        init(model.getChilds());
    }

    
    public XParametersPanel(XObject model) {
        this(model, true);
    }
    
    public XParametersPanel(XObject model, boolean withVars) {
        this.withVars = withVars;
        this.model = model;
        init(model.getChilds());
    }

    protected void push(TextualComponent cn, final XParameter c) {
        cn.addValueChangedListener(new TextualComponent.ValueChangedListener() {

            @Override
            public void onChange(TextualComponent source) {
                c.setValue(source.getValueText());
            }
        });

        JLabel lbl = addParameterLabel(c.getName());
        lbl.setToolTipText(c.getRemark());
        push(lbl);
        cn.setValueText(c.getValue());
        push((JComponent) cn, expand(constraint()));

        createVarbox((JComponent) cn, c);

        /*JLinkButton rest = new JLinkButton();
        rest.setText("Restore Defaults");
        rest.setBackground(XParametersPanel.SEPERATOR_COLOR);
        rest.setGradient(false);
        rest.setForeground(Color.black);
        push(rest);*/
        eol();
    }

    public boolean isWithVars() {
        return withVars;
    }

    public void setWithVars(boolean withVars) {
        this.withVars = withVars;
    }

    private void createVarbox(final JComponent component, final XParameter c) {
        if (! withVars) return;
        final JComboBox vc = new JComboBox();
        fill(vc, SystemVariables.class);
        append(vc, USER_DEFINED_VALUE);
        final ActionListener acl = new ActionListener() {

            String preValue = c.getValue();

            @Override
            public void actionPerformed(ActionEvent e) {
                if (eq(vc.getSelectedItem(), USER_DEFINED_VALUE)) {
                    component.setEnabled(true);
                    styleFont(vc, Font.PLAIN);
                    vc.setForeground(Color.BLACK);
                    c.setValue(preValue);
                } else {
                    component.setEnabled(false);
                    styleFont(vc, Font.BOLD);
                    vc.setForeground(Color.RED.darker());
                    if (vc.getSelectedItem() != null) {
                        c.setValue("$" + vc.getSelectedItem().toString());
                    } else {
                        c.setValue("");
                    }
                }
            }
        };
        c.addValueChangedListener(new XObject.ValueChangedHandler(c.getName()) {

            @Override
            public void onValueChanged(XObject obj, String oldValue, String newValue) {
                if (newValue.startsWith("$")) {
                    select(vc, enumarate(newValue, SystemVariables.class));
                } else {
                    select(vc, USER_DEFINED_VALUE);
                }
            }
        });
        vc.addActionListener(acl);
        push(vc);
        final String def = c.getDefaultVariableName();
        if (def.isEmpty()) {
            select(vc, USER_DEFINED_VALUE);
        } else {
            select(vc, enumarate(def, SystemVariables.class));
        }
        acl.actionPerformed(null);
    }

    private void addIP(final XParameter c) {
        final JIPAddress cn = new JIPAddress();
        push(cn, c);
    }

    private JLabel addParameterLabel(String name) {
        final JLabel lbl = new JLabel(name);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
        lbl.setForeground(new Color(71, 71, 71));
        return lbl;
    }

    private void addPort(XParameter c) {
        JNummericField nf = new JNummericField();
        setFieldBorder(nf);
        nf.setMin(0);
        nf.setMax(65535);
        nf.setHint(c.getRemark());

        push(nf, c);
    }

    private void addString(XParameter c) {
        JHinteableTextField field = new JHinteableTextField();
        setFieldBorder(field);
        field.setHint(c.getRemark());

        push(field, c);
    }

    private void setFieldBorder(JTextField field) {
        field.setBorder(new CompoundBorder(new LineBorder(new Color(204, 204, 204)), new LineBorder(Color.white, 2)));
    }

    private void addFilePath(XParameter c) {
        JFilePath comp = new JFilePath();
        push(comp, c);
    }

    private void addYNQ(XParameter c) {
        JYesNoQuestion ynq = new JYesNoQuestion();
        push(ynq, c);
    }

    private void addIf(final XIf c) {

        final JPanel outer = new JPanel();
        final String title = " Extra parameters (when " + c.getCheckedParameter() + " is " + c.getCheckedValue() + ") ";

        outer.setBackground(Color.white);
        outer.setLayout(new BorderLayout());

        JLabel lbt = new JLabel(title);
        lbt.setBorder(new LineBorder(Color.white, 3));
        lbt.setForeground(Color.red);
        outer.add(lbt, BorderLayout.NORTH);
        outer.setBorder(new MatteBorder(0, 3, 0, 0, SEPERATOR_COLOR));

        final XParametersPanel inner = new XParametersPanel(c /*Its not really a command but has the same stracture*/);
        outer.add(inner);

        push(outer, fullRow(constraint()));
        eol(false);
        final JSeparator sep = addSeperator();

        outer.setVisible(false);
        sep.setVisible(false);

        model.addValueChangedListener(new XObject.ValueChangedHandler(c.getCheckedParameter()) {

            @Override
            public void onValueChanged(XObject sender, String oldVal, String newVal) {
                if (eq(c.getCheckedValue(), newVal)) {
                    outer.setVisible(true);
                    sep.setVisible(true);
                } else {
                    outer.setVisible(false);
                    sep.setVisible(false);
                }
            }
        });

        if (eq(c.getParent().getValueOf(c.getCheckedParameter()), c.getCheckedValue())) {
            outer.setVisible(true);
            sep.setVisible(true);
        }

    }

    private void addOptions(XParameter c) {
        String options = take(drop(c.getType(), "OPTIONS(".length()), -1);
        String[] aopt = options.split("\\|");
        JTextualComboBox cn = new JTextualComboBox();
        fill(cn, aopt);

        push(cn, c);
    }

    private void addInteger(XParameter c) {
        JNummericField nf = new JNummericField();
        setFieldBorder(nf);
        nf.setMin(0);
        nf.setMax(Integer.MAX_VALUE);
        nf.setHint(c.getRemark());

        push(nf, c);
    }

    private void addLoop(final XLoop c) {
        JRepeatPanel con = new JRepeatPanel();
        final JRepeatPanel.Model model = new JRepeatPanel.Model() {

            @Override
            public void onNewChildRequested() {
                c.addLoopFragment();
            }

            @Override
            public void onLastChildRemoved(Object key) {
                c.removeLoop();
            }
        };

        if (c.hasMin()) {
            model.setMin(c.getMin());
        }
        con.setModel(model);

        c.addLoopChangedListener(new XLoop.LoopChangedListener() {

            @Override
            public void onLoopAdded(XObject loop) {
                model.addChild(loop, new XParametersPanel(loop));
            }

            @Override
            public void onLoopRemoved(XObject loop) {
                //model.removeChild(c);
            }
        });

        for (XObject ch : c.getChilds()) {
            model.addChild(ch, new XParametersPanel(ch));
        }

        push(con, hangOnTop(expand(fullRow(constraint()))));

    }

    private boolean typed(XObject f, String type) {
        return eq(((XParameter) f).getType(), type);
    }

    //TODO CONSIDER REMOVE..
    Element getMetadata() {
        return model.getMetadata();
    }

    public XObject getModel() {
        return model;
    }

    private void addFrame(XFrame frame) {
        final JPanel outer = new JPanel();
        final String title = frame.getName() + " ";
        outer.setBackground(Color.white);
        outer.setLayout(new BorderLayout());

        JLabel lbt = new JLabel(title);
        lbt.setBorder(new LineBorder(Color.white, 3));
        lbt.setForeground(Color.BLUE);
        outer.add(lbt, BorderLayout.NORTH);
        outer.setBorder(new MatteBorder(0, 3, 0, 0, SEPERATOR_COLOR));

        final XParametersPanel inner = new XParametersPanel(frame);
        outer.add(inner, BorderLayout.CENTER);

        push(outer, expand(fullRow(constraint())));
        eol(true);
    }

    private GridBagConstraints hangOnTop(GridBagConstraints expand) {
        expand.anchor = expand.NORTH;
        return expand;
    }

    private void addBoolean(XParameter c) {
        JTextualCheckBox field = new JTextualCheckBox();
        push(field, c);
    }

    public void setParam(BaseParamModel model) {
        XObject x = (XObject) model.getValue();
        setModel(x);
    }

    public void reflectChangesToParam(BaseParamModel to) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onChange(BaseParamModel source, Object newValue, Object deltaHint) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

}
