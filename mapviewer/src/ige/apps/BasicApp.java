package ige.apps;

import ige.tools.checktree.CheckTreeManager;
import ige.tools.checktree.CheckTreeNode;
import ige.worldwind.layers.LayerSet;
import ige.worldwind.layers.LayerSetItem;
import ige.worldwind.xml.XMLConfigLoader;
import ige.tools.Notifier;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.layers.Earth.BMNGWMSLayer;
import gov.nasa.worldwind.layers.LayerList;
//import gov.nasa.worldwind.layers.Earth.BMNGSurfaceLayer;



public class BasicApp {
	private final WorldWindowGLCanvas wwd = new WorldWindowGLCanvas();
	private LayerList layersList = new LayerList();
	private Model model;
	private JTree layermanager;

	public BasicApp()
	{
		wwd.setPreferredSize(new Dimension(200,600));
		wwd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		
		//add the layers
		AbstractLayer bmng = new BMNGWMSLayer();
		bmng.setEnabled(true);
		layersList.add(bmng);

		
		//create the layermanager tree
		layermanager = new JTree();
		layermanager.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		CheckTreeNode racine = new CheckTreeNode(false, "Couches de donn�es") ;
		layermanager.setShowsRootHandles(true);
		layermanager.setRootVisible(false);
		DefaultTreeModel tmodel = new DefaultTreeModel(racine);
		layermanager.setModel(tmodel);
		racine.add(new CheckTreeNode(bmng.isEnabled(), bmng));
		tmodel.reload();
		CheckTreeManager checkTreeManager = new CheckTreeManager(layermanager, wwd); 		

		XMLConfigLoader loader = new XMLConfigLoader();
		LayerSet layerset = loader.getLayerSetFromFile( new File("D:/tmp/WW_images.xml"));
		CheckTreeNode xmlls = checkTreeManager.makeTreeNodeFromLayerSet(layerset);
		RegisterLayerSet(xmlls, layersList, racine, tmodel);
		
		model = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
		model.setLayers(layersList);
		model.setShowWireframeExterior(false);
		model.setShowWireframeInterior(false);
		model.setShowTessellationBoundingVolumes(false);
		wwd.setModel(model);
		
		Notifier.getInstance().addPropertyChangeListener(
				"sliderAdjusted", new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent e) {
						System.out.println("event fired : "+e.getNewValue());
						wwd.getSceneController().setVerticalExaggeration(((Integer)e.getNewValue()).doubleValue()/2);
					}
				});
		
	}
	
	private void RegisterLayerSet(LayerSet ls, LayerList wwLayerList)
	{
		//on ajoute les couches (WWJ Layers), sous-objets des LayerSetItems au mod�le
		System.out.println("* { LayerSet " +ls.toString() +" } : ");
		System.out.print("\t");
		for (Object layer : ls.getLayerList()) 
		{
			if (layer instanceof LayerSet)
			{
				RegisterLayerSet((LayerSet) layer, wwLayerList);
				System.out.println();
				System.out.print("\t*");
			}
			else if (layer instanceof LayerSetItem)
			{
				//we add to the list the layer stored in the LayerSetItem
				wwLayerList.add((AbstractLayer) ((LayerSetItem)layer).getLayer() );
				System.out.print("["+((AbstractLayer) ((LayerSetItem)layer).getLayer() ).getName()+"], ");				
			}
			else if (layer instanceof AbstractLayer)
			{
				wwLayerList.add((AbstractLayer) layer);
				System.out.print("["+((AbstractLayer) layer).getName()+"], ");
			}
			else
				System.out.print("Not loaded (type unknown) : "+layer.getClass().toString());
		}

	}
	
	public void RegisterLayerSet(CheckTreeNode tnode, LayerList wwLayerList, CheckTreeNode rootNode, DefaultTreeModel treemodel )
	{
		LayerSet ls = (LayerSet) tnode.getUserObject();
		RegisterLayerSet(ls, wwLayerList);
		
		//on ajoute le checkTreeNode (l'arborescence des couches charg�es) dans la liste des couches (arborescence JTree)
		rootNode.add(tnode);
		treemodel.reload();
	}

	public void RegisterLayer(AbstractLayer layer, LayerList wwLayerList, CheckTreeNode rootNode, DefaultTreeModel treemodel )
	{
		//on ajoute la couche au mod�le
		wwLayerList.add(layer);
		
		//on ajoute la couche dans la liste des couches (arborescence JTree)
		rootNode.add(new CheckTreeNode(layer.isEnabled(), layer));
		treemodel.reload();
	}

	public WorldWindowGLCanvas getWWD()
	{
		return wwd;
	}
	
	public JTree getLayerManager()
	{
		return layermanager;
	}
}
