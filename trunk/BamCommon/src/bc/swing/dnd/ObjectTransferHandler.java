/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.lang.ref.WeakReference;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 *
 * @author BLutati
 */
public abstract class ObjectTransferHandler<T> extends TransferHandler {

    private DragSupport support;
    private boolean canImport;
    private boolean canExport;
    WeakReference<T> exportedData;

    public ObjectTransferHandler(DragSupport support, boolean canImport, boolean canExport) {
        this.support = support;
        this.canExport = canExport;
        this.canImport = canImport;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return support.ival;
    }


    @Override
    protected Transferable createTransferable(JComponent c) {
        if (! canExport) return null;

        final T idata = exportData(c);
        exportedData = new WeakReference<T>(idata);
        return idata == null ? null : new ObjectTransferable(idata);
    }

    @Override
    public boolean canImport(TransferSupport support) {
        if (canImport) {
            support.setShowDropLocation(true);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        return canImport;
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        exportDone(exportedData.get(), source, action);
        exportedData = null;
    }

    protected abstract void exportDone(T data, JComponent source, int action);

    public abstract boolean importData(T data, TransferSupport info);

    public abstract T exportData(JComponent c);

    @Override
    public boolean importData(TransferSupport info) {
        Transferable t = info.getTransferable();
        T data = safeCast(ObjectTransferable.extractData(t));
        if (data != null ) {
            importData(data, info);
            return true;
        }else {
            return false;
        }
        
    }

    private T safeCast(Object obj) {
        try {
            return (T) obj;
        } catch (ClassCastException ex) {
            return null;
        }
    }

    public static enum DragSupport {

        MOVE(TransferHandler.MOVE),
        COPY(TransferHandler.COPY),
        LINK(TransferHandler.LINK),
        COPY_OR_MOVE(TransferHandler.COPY_OR_MOVE);
        int ival;

        private DragSupport(int ival) {
            this.ival = ival;
        }

        public int getIval() {
            return ival;
        }
    }
}
