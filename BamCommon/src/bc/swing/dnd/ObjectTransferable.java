/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bc.swing.dnd;

import java.util.LinkedList;
import java.util.List;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import static bc.dsl.JavaDSL.*;

/**
 *
 * @author BLutati
 */
public class ObjectTransferable implements Transferable {
    private static int transferedId = 0;
    private static HashMap<String, WeakReference> transferedMap = new HashMap<String, WeakReference>();

    StringSelection delegate;

    public ObjectTransferable(Object transfered) {
        maintanance();

        String id = ""  + (transferedId++);
        transferedMap.put(id, new WeakReference(transfered));
        delegate = new StringSelection(id);
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return delegate.getTransferDataFlavors();
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return delegate.isDataFlavorSupported(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return delegate.getTransferData(flavor);
    }


    private static void maintanance(){
        List<String> deadKeys = new LinkedList<String>();
        for (Entry<String, WeakReference> e : transferedMap.entrySet()) if (e.getValue().get() == null) deadKeys.add(e.getKey());
        for (String dk : deadKeys) transferedMap.remove(dk);
    }

    public static Object extractData(Transferable t){
        try {
            String id = t.getTransferData(DataFlavor.stringFlavor).toString();
            if (nummeric(id)) {
                return transferedMap.remove(id).get();
            } else {
                return null;
            }
        } catch (UnsupportedFlavorException ex) {
            Logger.getLogger(ObjectTransferable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ObjectTransferable.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

}
