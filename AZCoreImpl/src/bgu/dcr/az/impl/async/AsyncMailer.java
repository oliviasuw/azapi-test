/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package bgu.dcr.az.impl.async;

import bgu.dcr.az.impl.AbstractMailer;
import bgu.dcr.az.impl.DefaultMessageQueue;

/**
 *
 * @author bennyl
 */
public class AsyncMailer extends AbstractMailer {
   
    @Override
    protected DefaultMessageQueue generateNewMessageQueue() {
        return new DefaultMessageQueue();
    }
}
