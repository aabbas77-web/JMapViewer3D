package ige.worldwind.layers;

import gov.nasa.worldwind.render.UserFacingIcon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.IconLayer;

/** Extended UserFacingIcon class to hold extra information 
 *  like name, description and url
 *  @author Patrick Murris
 **/
public class ClickableIcon extends UserFacingIcon {
	private IconLayer parent;
	private String description;
	private String name;
	private String url;
	
	
	public ClickableIcon(String iconPath, Position iconPosition) {
		super(iconPath, iconPosition);
	}

	public IconLayer getParent() {
		return parent;
	}

	public void setParent(IconLayer layer) {
		this.parent = layer;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}	// End ClickableIcon class
