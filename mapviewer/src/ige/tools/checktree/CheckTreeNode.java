package ige.tools.checktree;

import ige.worldwind.layers.LayerSet;

import javax.swing.tree.*;

public class CheckTreeNode extends DefaultMutableTreeNode {
	private boolean checked=false;

	public CheckTreeNode(Object userObject)
	{
		super(userObject);		
		checked = false;
	}
	
	public CheckTreeNode(boolean ischecked, Object userObject)
	{
		super(userObject);		
		checked = ischecked;
	}
	
	public void setChecked(boolean x)
	{
		checked = x;
		return;
	}
	
	public boolean isChecked()
	{
		return checked;
	}
	

}
