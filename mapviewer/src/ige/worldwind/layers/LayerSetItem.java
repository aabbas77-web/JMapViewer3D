package ige.worldwind.layers;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.WWObjectImpl;
import gov.nasa.worldwind.layers.*;

/**
 * Represent an item inside a <code>LayerSet</code>
 * @author Patrick Murris
 *
 */
public class LayerSetItem extends WWObjectImpl {

    private Object layer = null;
    protected boolean enabled = true;
    private LayerSet parent = null;
    
    public LayerSetItem() {}
    
    public LayerSetItem(Object layer) {
    	this.layer = layer;
    }
    
    public boolean isEnabled()
    {
//    	if(this.layer != null && this.layer instanceof Layer)
//    		return ((Layer)this.layer).isEnabled();
    	
        return this.enabled;
    }

    public void setEnabled(boolean enabled)
    {
        if (this.parent.isLayerSetVisible())
        {
        	this.setEnableLayerOnly(enabled);
        	this.enabled = enabled;
        }
        else
            this.enabled = enabled;
       	
    }
      
    public void setEnableLayerOnly(boolean enabled)
    {
    	if(this.layer != null && this.layer instanceof Layer)
    		((Layer)this.layer).setEnabled(enabled);
   }
    
/*    public void trySetEnabled(boolean enabled)
    {
    	//if we deactivate the layer, no pb
    	if (enabled == false)
    		this.setEnabled(enabled);
    	//if we activate the layer, but it has no parent, no pb
    	else if (this.getParent() == null)
    		this.setEnabled(enabled);
    	//if we activate the layer, and it has one or more parents, 
    	//we have to check if all the parents are activated
    	//in either cas, we enable the layersetitem, but maybe not the underlying layer 
    	//(that inherits its visibility from the parents
    	else
    	{
    		this.enabled = enabled ;
    		((Layer)this.layer).setEnabled(enabled & this.getParent().isLayerSetVisible());
    	}
    }
*/        
    public LayerSet getParent() {
    	if (this.parent !=null)
    		return this.parent;
    	else
    		return null;
    }
    
    public void setParent(LayerSet layerSet) {
    	this.parent = layerSet;
    }

    public Object getLayer() {
    	return this.layer;
    }
    
    public void setLayer(Object layer) {
    	this.layer = layer;
    }

    @Override
    public String toString()
    {
    	return this.getName();
    }
    
    public String getName()
    {
        return this.myName();
    }

    public void setName(String name)
    {
    	if(this.layer != null && this.layer instanceof Layer)
    		((Layer)this.layer).setName(name);
    	else
    		this.setValue(AVKey.DISPLAY_NAME, name);
    }

    private String myName()
    {
    	if(this.layer != null && this.layer instanceof Layer)
    		return ((Layer)this.layer).getName();
    	
        String myName = null;
        Object name = this.getValue(AVKey.DISPLAY_NAME);
        if (null != name)
            myName = name.toString();
        if (myName == null)
            myName = "LayerSetItem";
        return myName;
    }
}

