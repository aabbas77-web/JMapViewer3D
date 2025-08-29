package ige.tools.checktree;

import javax.swing.*;
import javax.swing.tree.*;

import java.awt.*;

public class CheckTreeCellRenderer extends JPanel implements TreeCellRenderer{ 
    private CheckTreeSelectionModel selectionModel; 
    private TreeCellRenderer delegate; 
    private JCheckBox checkBox = new JCheckBox(); 
 
    public CheckTreeCellRenderer(TreeCellRenderer delegate, CheckTreeSelectionModel selectionModel){ 
        this.delegate = delegate; 
        this.selectionModel = selectionModel; 
        setLayout(new BorderLayout()); 
        setOpaque(false); 
        checkBox.setOpaque(false); 
    } 
 
 
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus){ 
        Component renderer = delegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus); 
 
        TreePath path = tree.getPathForRow(row); 
        if(path!=null){ 
            /*if(selectionModel.isPathSelected(path, true)) 
                checkBox.setState(Boolean.TRUE); 
            else 
                checkBox.setState(selectionModel.isPartiallySelected(path) ? null : Boolean.FALSE);
            */ 
            checkBox.setSelected(selectionModel.isChecked(path));
        } 
        removeAll(); 
        add(checkBox, BorderLayout.WEST); 
        add(renderer, BorderLayout.CENTER); 
        return this; 
    } 
} 