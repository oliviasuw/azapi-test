/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.views;

import bc.swing.pfrm.Page;
import bc.swing.pfrm.PageView;
import bc.swing.pfrm.ano.ViewHints;
import bc.swing.pfrm.BaseParamModel;
import bc.swing.pfrm.ParamView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 *
 * @author bennyl
 */
public class LabelPV extends JLabel implements ParamView {

    public void setParam(final BaseParamModel model) {
        onChange(model, model.getValue(), null);
        ViewHints vh = model.getViewHints();

        configureAlignment(vh);
        
        if (vh.autoSyncEvery() > 0) {
            final Timer t = new Timer(vh.autoSyncEvery(), new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    onChange(model, model.getValue(), null);
                }
            });

            t.setRepeats(true);
            t.start();

            model.getPage().addToDisposeList(new PageView() {

                public void setPage(Page model) {
                }

                public void onDispose() {
                    t.stop();
                }
            });
        }
    }

    public void reflectChangesToParam(BaseParamModel to) {
        to.setValue(getText());
    }

    public void onChange(BaseParamModel model, Object newValue, Object deltaHint) {
        String data = (newValue == null? "????" : newValue.toString());
        setText(data);
        setIcon(model.getIcon());
    }

    private void configureAlignment(ViewHints vh) {
        switch (vh.horizontalAlignment()){
            case UNDEF:
                return;
            case LEFT: 
                setHorizontalAlignment(SwingConstants.LEFT);
                break;
            case RIGHT:
                setHorizontalAlignment(SwingConstants.RIGHT);
                break;
            case CENTER:
                setHorizontalAlignment(SwingConstants.CENTER);
                break;
        }
    }
}
