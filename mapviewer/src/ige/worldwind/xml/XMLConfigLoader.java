package ige.worldwind.xml;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;

import gov.nasa.worldwind.util.*;

import ige.tools.checktree.CheckTreeNode;
import ige.worldwind.layers.*;

public class XMLConfigLoader 
{
	LayerSet layerset = null;
	
	public XMLConfigLoader()
	{
	}
	
	/**
	 * Load a LayerSet from a WW XML file
	 * @param file the XML file
	 * @return void
	 */
	public LayerSet getLayerSetFromFile(File file) {
		Document doc = readXML(file);
		if(doc != null) 
		{
			Node root = doc.getDocumentElement();
			if(root.getNodeName().compareToIgnoreCase("LayerSet") == 0) 
			{
				layerset = getLayerSetFromNode(root);
			}
			
		}
		else
			System.out.println("Erreur de lecture : fichier "+ file+ "inexistant ou chemin erron");
		return layerset;
	}
	
	/**
	 * Load a LayerSet from a WW XML LayerSet node
	 * @param root The XML LayerSet node
	 * @return layerset The corresponding LayerSet tree
	 */
	public LayerSet getLayerSetFromNode(Node root) {
		Node attr;
		// LayerSet name
		String name= root.getAttributes().getNamedItem("Name").getTextContent();
		LayerSet lset = new LayerSet();
		lset.setName(name);
		lset.setEnabled(false);
		// LayerSet enabled
		attr = root.getAttributes().getNamedItem("ShowAtStartup");
		if(attr != null) lset.setEnabled(attr.getTextContent().compareToIgnoreCase("True") == 0 ? true : false);
		
		// LayerSet show only one item
		attr = root.getAttributes().getNamedItem("ShowOnlyOneLayer");
		if(attr != null) lset.setShowOnlyOne(attr.getTextContent().compareToIgnoreCase("True") == 0 ? true : false);
		
		// Process child nodes
		for (int i = 0; i < root.getChildNodes().getLength(); i++)
		{
			Node node = root.getChildNodes().item(i);
			String nodeName = node.getNodeName();
			if(nodeName.compareToIgnoreCase("ChildLayerSet") == 0) {
				// Process ChildLayerSet node - recurse
				LayerSet child = getLayerSetFromNode(node);
				lset.add(child);
			} else if(nodeName.compareToIgnoreCase("QuadTileSet") == 0) {
				// Layer enabled
				attr = node.getAttributes().getNamedItem("ShowAtStartup");
				boolean showatstartup = false;
				if(attr != null) showatstartup = attr.getTextContent().compareToIgnoreCase("True") == 0 ? true : false;
				// Process QuadTileSet node
				XMLLoader loader = new XMLLoader();
				LayerSetItem item = new LayerSetItem(loader.loadRasterFromNode(node));
				lset.add(item);
				item.setEnabled(showatstartup);
				item.setParent(lset);
				//return new CheckTreeNode(showatstartup, WWXMLRasterLayer.getLayerFromNode(node));
			} else if(nodeName.compareToIgnoreCase("Icon") == 0) {
				// Layer enabled
				attr = node.getAttributes().getNamedItem("ShowAtStartup");
				boolean showatstartup = false;
				if(attr != null) showatstartup = attr.getTextContent().compareToIgnoreCase("True") == 0 ? true : false;
				XMLLoader loader = new XMLLoader();
				LayerSetItem item = new LayerSetItem(loader.loadIconFromNode(node));
				lset.add(item);
				item.setEnabled(showatstartup);
				item.setParent(lset);
			} else if(nodeName.compareToIgnoreCase("LineFeature") == 0) {
				//	Layer enabled
				attr = node.getAttributes().getNamedItem("ShowAtStartup");
				boolean showatstartup = false;
				if(attr != null) showatstartup = attr.getTextContent().compareToIgnoreCase("True") == 0 ? true : false;

				//process line node			
				XMLLoader loader = new XMLLoader();
				LayerSetItem item = new LayerSetItem(loader.loadPolylineFromNode(node));
				lset.add(item);
				item.setEnabled(showatstartup);
				item.setParent(lset);
			} else if(nodeName.compareToIgnoreCase("PolygonFeature") == 0) {
				//	Layer enabled
				attr = node.getAttributes().getNamedItem("ShowAtStartup");
				boolean showatstartup = false;
				if(attr != null) showatstartup = attr.getTextContent().compareToIgnoreCase("True") == 0 ? true : false;

				//process line node			
				XMLLoader loader = new XMLLoader();
				LayerSetItem item = new LayerSetItem(loader.loadPolygonFromNode(node));
				lset.add(item);
				item.setEnabled(showatstartup);
				item.setParent(lset);
			} 
		}
		return  lset;
	}

	// Read an XML document from a file
	private Document readXML(File file)
	{
		if (file == null)
		{
	        String message = Logging.getMessage("nullValue.FileIsNull");
	        Logging.logger().severe(message);
	        throw new IllegalArgumentException(message);
		}

		try
		{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(false);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);
			return doc;
		}
		// TODO: use proper error message strings - not GeoRSS
		catch (ParserConfigurationException e)
		{
			String message = "Parser Configuration Error";
	        Logging.logger().severe(message);
	        throw new IllegalArgumentException(message);
		}
		catch (IOException e)
		{
			String message = "IO Exception while parsing xml file";
	        Logging.logger().severe(message);
	        throw new IllegalArgumentException(message);
		}
		catch (SAXException e)
		{
			String message = "IO Exception while parsing xml file";
	        Logging.logger().severe(message);
	        throw new IllegalArgumentException(message);
		}
	}

}
