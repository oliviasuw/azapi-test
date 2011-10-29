/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.models;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;

/**
 *
 * @author bennyl
 */
public class LimitedBatchDocument extends BatchDocument{
    
    int maxDocSize;
    int minDelSize;

    public LimitedBatchDocument() {
        this.maxDocSize = 10000;
        this.minDelSize = 1000;
    }

    public LimitedBatchDocument(int maxDocSize, int minDelSize) {
        this.maxDocSize = maxDocSize;
        this.minDelSize = minDelSize;
    }

    @Override
    public synchronized void processBatchUpdates() throws BadLocationException {
        super.processBatchUpdates();
        int len = this.getLength() ; 
        final int delta = len - maxDocSize;
        int toDelete = (delta>0? Math.max(delta, minDelSize): 0);

        this.remove(0, toDelete);
        //System.out.println("TEST Removing " + toDelete + " from doc of size " + len + " now the length is " + getLength());
        
        //System.out.println("TEST Before Inserting length is " + getLength() + " adding " + getBatchSize());
    }
    
    
    
    
    
}
