package ige.apps;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.layers.LayerList;
//import gov.nasa.worldwind.layers.Earth.BMNGSurfaceLayer;
import gov.nasa.worldwind.event.*;

import ige.tools.checktree.CheckTreeManager;
import ige.tools.checktree.CheckTreeNode;
import ige.worldwind.globes.EarthGlobe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;


public class BasicAppletWithUI extends JApplet{

	private final WorldWindowGLCanvas wwd = new WorldWindowGLCanvas();
	private AppWindowUI mainPanel;
	private LayerList layersList = new LayerList();
	private Model model;

	public void stop()
	{
		WorldWind.getMemoryCacheSet().clear();
		System.out.println("Cache cleared");
	}

	public void start() 
	{
		System.out.println("Java run-time version: " + System.getProperty("java.version"));
		System.out.println(gov.nasa.worldwind.Version.getVersion());
	}

	public void init()
	{   
		Policy.setPolicy(new Policy() {
			public void refresh() {
			}

			public PermissionCollection getPermissions(CodeSource arg0) {
				Permissions perms = new Permissions();
				perms.add(new AllPermission());
				return (perms);
			}
		});

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		WorldWind.getMemoryCacheSet().clear();
		System.out.println("Cache cleared");
		
		this.initComponents();
	}


	public void initComponents()
	{

		try 
		{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			BasicApp mrpropre = new BasicApp();
			this.setSize(new Dimension(1200, 800));
			mainPanel = new AppWindowUI();
			this.setContentPane(mainPanel);
			mainPanel.addGraphicComponent(mrpropre.getWWD());
			mainPanel.setLayerManager(mrpropre.getLayerManager());
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}


	}
}
