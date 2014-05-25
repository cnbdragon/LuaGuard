/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */

package luaguard.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.swing.JTextField;

/**
 *
 * @author Will
 */
class DragDrop implements DropTargetListener {

    @Override
    public void drop(DropTargetDropEvent event) {

        // Accept copy drops
        event.acceptDrop(DnDConstants.ACTION_COPY);

        // Get the transfer which can provide the dropped item data
        Transferable transferable = event.getTransferable();

        // Get the data formats of the dropped item
        DataFlavor[] flavors = transferable.getTransferDataFlavors();

        // Loop through the flavors
        for (DataFlavor flavor : flavors) {

            try {

                // If the drop items are files
                if (flavor.isFlavorJavaFileListType()) {

                    // Get all of the dropped files
                    List files = (List) transferable.getTransferData(flavor);
                    for (Iterator it = files.iterator(); it.hasNext();) {
                        File file = (File) it.next();
                        DropTarget target = (DropTarget) event.getSource();
                        JTextField text = (JTextField) target.getComponent();
                        text.setText(file.getPath());
                    }

                }

            } catch (UnsupportedFlavorException | IOException e) {
            }
        }

        // Inform that the drop is complete
        event.dropComplete(true);

    }

    @Override
    public void dragEnter(DropTargetDragEvent event) {
    }

    @Override
    public void dragExit(DropTargetEvent event) {
    }

    @Override
    public void dragOver(DropTargetDragEvent event) {
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent event) {
    }

}