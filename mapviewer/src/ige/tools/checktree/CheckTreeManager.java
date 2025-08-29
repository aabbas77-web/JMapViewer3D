package ige.tools.checktree;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import java.awt.event.*;
import java.util.Enumeration;

import gov.nasa.worldwind.globes.ElevationModel;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.awt.*;

import ige.worldwind.layers.*;

public class CheckTreeManager extends MouseAdapter implements TreeSelectionListener{ 
	private CheckTreeSelectionModel selectionModel; 
	private JTree tree = new JTree(); 
	private TreeModel tmodel;
	int hotspot = new JCheckBox().getPreferredSize().width;
	private WorldWindowGLCanvas wwcanvas;

	public CheckTreeManager(JTree tree, WorldWindowGLCanvas wwglcanvas){ 
		this.tree = tree; 
		tmodel = tree.getModel();
		selectionModel = new CheckTreeSelectionModel(tmodel); 
		tree.setCellRenderer(new CheckTreeCellRenderer(tree.getCellRenderer(), selectionModel)); 
		tree.addMouseListener(this); 
		selectionModel.addTreeSelectionListener(this); 
		wwcanvas = wwglcanvas;
	} 

	public void setPathChecked(TreePath path)
	{
		selectionModel.removeTreeSelectionListener(this); 
		selectionModel.addSelectionPath(path); 
		selectionModel.addTreeSelectionListener(this); 
		tree.treeDidChange(); 
	}

	/**
	 * Returns a <code>CheckTreeNode</code> representing this
	 * <LayerSet> and its children
	 * @return The root node of the tree
	 */
	public CheckTreeNode makeTreeNodeFromLayerSet(LayerSet ls) {
		CheckTreeNode root = new CheckTreeNode(ls.isEnabled(), ls);
		for (Object layer : ls.getLayerList()) {
			if(layer instanceof LayerSet) {
				root.add(makeTreeNodeFromLayerSet((LayerSet)layer));
			} else if (layer instanceof LayerSetItem) {
				root.add(new CheckTreeNode(((LayerSetItem)layer).isEnabled(), layer));
			} else if (layer instanceof Layer) {
				root.add(new CheckTreeNode(((Layer)layer).isEnabled(), layer));
			} else  {
				root.add(new CheckTreeNode(false, layer));
			}
		}
		return root;
	}


	public void addIt()
	{
	}
	public void mouseClicked(MouseEvent me){ 
		TreePath path = tree.getPathForLocation(me.getX(), me.getY()); 

		if(path==null) 
			return; 
		if(me.getX()>tree.getPathBounds(path).x+hotspot) 
			return; 

		boolean selected = selectionModel.isPathSelected(path, true); 
		selectionModel.removeTreeSelectionListener(this); 

		try
		{
			CheckTreeNode tn = (CheckTreeNode)tree.getLastSelectedPathComponent();
			if (tn.getUserObject() instanceof LayerSet)
			{
//				System.out.println("Layerset (un)checked");
				LayerSet ls = (LayerSet) tn.getUserObject();
				ls.setEnabled(!ls.isEnabled());

			}
			else if (tn.getUserObject() instanceof LayerSetItem)
			{
//				System.out.println("LayersetItem (un)checked");
				LayerSetItem lsi = (LayerSetItem) tn.getUserObject();
				lsi.setEnabled(!lsi.isEnabled());
			}
			else if (((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getUserObject() instanceof AbstractLayer)
			{
				//((AbstractLayer) node.getUserObject()).setEnabled(false);
				CheckTreeNode ctn  = (CheckTreeNode) tree.getLastSelectedPathComponent();
				AbstractLayer layer = (AbstractLayer) ctn.getUserObject();

				//si ctn.getChecked et layer.isEnabled ne coincident pas, c'est qu'un layerset au-dessus 
				// est cach : dans ce cas, on n'agit que sur les cases. L'affichage des couches sera revu lorsque
				// le layerset sera affich
				//
				if (ctn.isChecked()==layer.isEnabled())
					layer.setEnabled(!layer.isEnabled());
				//       		System.out.println("(un)checked : "+layer.toString() +" / state : "+layer.isEnabled());
			}
			else if (((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getUserObject() instanceof ElevationModel)
			{
				ElevationModel layer = (ElevationModel) ((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getUserObject();
				layer.setEnabled(!layer.isEnabled());
				//       		System.out.println("(un)checked : "+layer.toString() +" / state : "+layer.isEnabled());
			}
		}
		catch (Exception ex) {System.out.println(ex);};

		try{ 
			selectionModel.setChecked(path, !selectionModel.isChecked(path));
			/*if(selected) 
                selectionModel.removeSelectionPath(path); 
            else 
                selectionModel.addSelectionPath(path);
			 */ 
		} finally{ 
			selectionModel.addTreeSelectionListener(this); 
			tree.treeDidChange(); 
			wwcanvas.redrawNow();
		} 
	} 

	public CheckTreeSelectionModel getSelectionModel(){ 
		return selectionModel; 
	} 

	public void valueChanged(TreeSelectionEvent e){ 
		tree.treeDidChange(); 

	} 
}
