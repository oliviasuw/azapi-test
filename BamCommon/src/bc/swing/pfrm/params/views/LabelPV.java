/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.params.views;

import bc.swing.pfrm.Page;
import bc.swing.pfrm.PageView;
import bc.swing.pfrm.ano.ViewHints;
import bc.swing.pfrm.params.ParamModel;
import bc.swing.pfrm.params.ParamView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 *
 * @author bennyl
 */
public class LabelPV extends JLabel implements ParamView {

    public void setModel(final ParamModel model) {
        onChange(model, model.getValue(), null);
        ViewHints vh = model.getViewHints();

        if (vh.autoSyncEvery() > 0) {
            final Timer t = new Timer(vh.autoSyncEvery(), new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    onChange(model, model.getValue(), null);
                }
            });

            t.setRepeats(true);
            t.start();

            model.getPage().addToDisposeList(new PageView() {

                public void setModel(Page model) {
                }

                public void onDispose() {
                    t.stop();
                }
            });
        }
    }

    public void reflectChanges(ParamModel to) {
        to.setValue(getText());
    }

    public void onChange(ParamModel model, Object newValue, Object deltaHint) {
        String data = (newValue == null? "????" : newValue.toString());
        setText(data);
        setIcon(model.getIcon());
    }
}
