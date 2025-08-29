package ige.worldwind.layers;

import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.*;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Represent a list of <code>LayerSetItem</code>s or <code>LayerSet</code>s
 * @author Patrick Murris
 */
public class LayerSet extends LayerSetItem implements Iterable<Object> {

	private java.util.List<Object> layerList = new java.util.ArrayList<Object>();
	private boolean showOnlyOne = false;

	public boolean getShowOnlyOne()
	{
		return this.showOnlyOne;
	}

	public void setShowOnlyOne(boolean state)
	{
		this.showOnlyOne = state;
	}

	public java.util.List<Object> getLayerList() {
		return this.layerList;
	}

	public boolean isLayerSetVisible()
	{
		if (this.getParent()==null)
			return this.isEnabled();
		else
			return (this.isEnabled() && this.getParent().isLayerSetVisible());

	}

	public void setEnabled(boolean enabled)
	{
		if (this.getParent()==null)
		{
			this.setSubTreeVisible(enabled);
//			System.out.println("set sub tree visible : " + enabled);
		}
		else if (this.getParent().isLayerSetVisible())
			this.setSubTreeVisible(enabled);
		
		this.enabled=enabled;
	}

	public void setSubTreeVisible(boolean visible)
	{
		if (visible)
		{
			for (Object layer : this.getLayerList()) 
			{
				if(layer instanceof LayerSet) 
				{
					LayerSet ls = ((LayerSet)layer);
					if(ls.isEnabled())
						ls.setSubTreeVisible(visible);
				} else if(layer instanceof LayerSetItem) 
				{
					// LayerSetItem
					LayerSetItem item = (LayerSetItem)layer;
					// if the layersetitem is enabled, then the underlying layer will be 
					//visible if and only if the layerset is
					if(item.isEnabled()) 
					{
						item.setEnableLayerOnly(visible);
					}
				}
			}
		}
		//else (visible = false) : we set all the sublayers invisible
		else
		{
			for (Object layer : this.getLayerList()) 
			{
				if(layer instanceof LayerSet) 
				{
					((LayerSet)layer).setSubTreeVisible(visible);
				} else if(layer instanceof LayerSetItem) 
				{
					// LayerSetItem
					LayerSetItem item = (LayerSetItem)layer;
					// if the layersetitem is enabled, then the underlying layer will be 
					//visible if and only if the layerset is
					item.setEnableLayerOnly(visible);
				}
			}
		}
	}
	
	public void add(Object layer)
	{
		if (layer == null)
		{
            String message = Logging.getMessage("nullValue.LayerIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
 		}

		if (this.layerList.contains(layer))
			return;

		if(layer instanceof LayerSetItem)
		{
			((LayerSetItem)layer).setParent(this);
			this.layerList.add(layer);
		}
		else
			this.layerList.add(layer);
	}

	public void remove(Object layer)
	{
		if (layer == null)
		{
//			String msg = WorldWind.retrieveErrMsg("nullValue.LayerIsNull");
//			WorldWind.logger().log(java.util.logging.Level.FINE, msg);
//			throw new IllegalArgumentException(msg);
		}

		if (!this.layerList.contains(layer))
			return;

		if(layer instanceof LayerSetItem)
			((LayerSetItem)layer).setParent(null);
		this.layerList.remove(layer);
	}

	public java.util.Iterator<Object> iterator()
	{
		return this.layerList.iterator();
	}

	public int getSize()
	{
		return this.layerList.size();
	}

	/**
	 * Returns a <code>DefaultMutableTreeNode</code> representing this
	 * <LayerSet> and its children
	 * @return The root node of the tree
	 */
	public DefaultMutableTreeNode makeTreeNode() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);
		for (Object layer : this.getLayerList()) {
			if(layer instanceof LayerSet) {
				root.add(((LayerSet)layer).makeTreeNode());
			} else {
				root.add(new DefaultMutableTreeNode(layer));
			}
		}
		return root;
	}

	/**
	 * Returns a <code>LayerList</code> representing this
	 * <LayerSet> enabled layers (including its children's layers)
	 * @return The LayerList of enabled layers in the tree
	 */
	public LayerList makeEnabledLayerList() {
		LayerList layerList = new LayerList();
		for (Object layer : this.getLayerList()) {
			if(layer instanceof LayerSet) {
				// LayerSet
				if(((LayerSet)layer).isEnabled()) {
					// Add layerset child layers if enabled
					LayerList childLayers = ((LayerSet)layer).makeEnabledLayerList();
					for(Layer childLayer : childLayers) layerList.add(childLayer);
				}
			} else if(layer instanceof LayerSetItem) {
				// LayerSetItem
				LayerSetItem item = (LayerSetItem)layer;
				if(item.isEnabled()) {
					if(item.getLayer() != null && item.getLayer() instanceof Layer) 
						layerList.add((Layer)item.getLayer());
				}
			}
		}
		return layerList;
	}

}

