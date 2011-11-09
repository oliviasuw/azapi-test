/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package bgu.csp.az.impl.async;

import bgu.csp.az.impl.AbstractMailer;
import bgu.csp.az.impl.DefaultMessageQueue;

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
