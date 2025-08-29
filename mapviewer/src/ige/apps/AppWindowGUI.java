package ige.apps;

import java.awt.Component;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.Dimension;
import javax.swing.JSplitPane;
import java.awt.GridBagConstraints;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JSlider;
import java.awt.ComponentOrientation;
import java.awt.BorderLayout;
import javax.swing.event.*;

import ige.tools.Notifier;
import javax.swing.JTabbedPane;
import java.awt.Color;
import javax.swing.JCheckBox;

public class AppWindowGUI extends JPanel {

	private static final long serialVersionUID = 1L;
	private JSplitPane jSplitPane = null;
	private JScrollPane jScrollPane = null;
	private JPanel jRightPanel = null;
	private JSlider jSlider = null;
	private JPanel jGraphicPanel = null;
	private JTree jTree = null;
	/**
	 * This is the default constructor
	 */
	public AppWindowGUI() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.gridx = 0;
		this.setSize(674, 377);
		this.setLayout(new GridBagLayout());
		this.add(getJSplitPane(), gridBagConstraints);
	}

	/**
	 * This method initializes jSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setDividerLocation(200);
			jSplitPane.setContinuousLayout(false);
			jSplitPane.setOneTouchExpandable(true);
			jSplitPane.setLeftComponent(getJScrollPane());
			jSplitPane.setRightComponent(getJRightPanel());
			jSplitPane.getRightComponent().setMinimumSize(new Dimension(50,100));
			jSplitPane.setDividerSize(10);
		}
		return jSplitPane;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setForeground(new Color(163, 184, 204));
			jScrollPane.setViewportView(getJTree());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jRightPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJRightPanel() {
		if (jRightPanel == null) {
			jRightPanel = new JPanel();
			jRightPanel.setLayout(new BorderLayout());
			jRightPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			jRightPanel.add(getJSlider(), BorderLayout.WEST);
			jRightPanel.add(getJGraphicPanel(), BorderLayout.CENTER);
		}
		return jRightPanel;
	}

	/**
	 * This method initializes jSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getJSlider() {
		if (jSlider == null) {
			jSlider = new JSlider();
			jSlider.setOrientation(JSlider.VERTICAL);
			jSlider.setOrientation(JSlider.VERTICAL);
			jSlider.setInverted(false);
			jSlider.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			jSlider.setToolTipText("Vertical Exaggeration");
			jSlider.setValue(2);
			jSlider.setMaximum(20);
			jSlider.setMajorTickSpacing(2);
			jSlider.setPaintTicks(true);
			jSlider.setPaintLabels(false);
			jSlider.setMinorTickSpacing(1);
			jSlider.setSnapToTicks(true);
			jSlider.setPaintTrack(true);
			jSlider.addChangeListener(new ChangeListener() {
				  public void stateChanged(ChangeEvent e) {
				     JSlider source = (JSlider)e.getSource();  // get the slider
				     if (!source.getValueIsAdjusting()) {
				        //System.out.println(source.getValue());  // get slider value
				        //sliderValueHasChanged(source.getValue());
				        int newValue =source.getValue(); 
						Notifier.getInstance().firePropertyChange(source,"sliderAdjusted",newValue);

				     }
				  }
			});
		}
		return jSlider;
	}

	public void addGraphicComponent(Component wwd)
	{
		wwd.setPreferredSize(new Dimension(10,10));
		this.getJGraphicPanel().add(wwd, BorderLayout.CENTER);
	}
	
	public void setLayerManager(JTree tree)
	{
		this.jScrollPane.setViewportView(tree);
	}
	
	public JTree getLayerManager()
	{
		return this.getJTree();
	}
	
	//Events
	protected void sliderValueHasChanged(int value)
	{
		System.out.println(value);
		jSlider.setToolTipText("Vertical Exaggeration : "+value);
	}

	/**
	 * This method initializes jGraphicPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJGraphicPanel() {
		if (jGraphicPanel == null) {
			jGraphicPanel = new JPanel();
			jGraphicPanel.setLayout(new BorderLayout());
		}
		return jGraphicPanel;
	}

	/**
	 * This method initializes jTree	
	 * 	
	 * @return javax.swing.JTree	
	 */
	private JTree getJTree() {
		if (jTree == null) {
			jTree = new JTree();
		}
		return jTree;
	}


}  //  @jve:decl-index=0:visual-constraint="10,10"
