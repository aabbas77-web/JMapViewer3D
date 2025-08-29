package oghab.mapviewer.tcp_client;

import java.awt.*;
import java.util.ArrayList;
//import java.util.Vector;

public class BorderPanel extends Panel
{

    public BorderPanel(TapPanel tappanel, ChatClient app , CardLayout cardlayout, Panel panel, int i, int j)
    {
        xofs = 0;
        curTab = 0;
        Cframe = tappanel;
        cardLayout = cardlayout;
        cardPanel = panel;
        tabNames = new ArrayList();
        tabPos = new ArrayList();
        cardNames = new ArrayList();
        textFont = new Font("Helvetica", 1, 11);
        setBackground(Color.white);
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        setLayout(gridbaglayout);
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.fill = 1;
        gridbagconstraints.gridwidth = 0;
		chatclient = app;
        Tabs = new Border(Cframe, chatclient , this, 1, i);
        gridbaglayout.setConstraints(Tabs, gridbagconstraints);
        add(Tabs);
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.weighty = 1.0D;
        gridbagconstraints.gridwidth = 1;
        Border border = new Border(Cframe, chatclient , this, 2, j);
        gridbaglayout.setConstraints(border, gridbagconstraints);
        add(border);
        Panel panel1 = new Panel();
        GridBagLayout gridbaglayout1 = new GridBagLayout();
        panel1.setLayout(gridbaglayout1);
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.gridwidth = 0;
        gridbaglayout1.setConstraints(panel, gridbagconstraints);
        panel1.add(panel);
        gridbagconstraints.gridwidth = -1;
        gridbaglayout.setConstraints(panel1, gridbagconstraints);
        add(panel1);
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.gridwidth = 0;
        Border border1 = new Border(Cframe,chatclient, this, 3, j);
        gridbaglayout.setConstraints(border1, gridbagconstraints);
        add(border1);
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 0.0D;
        Border border2 = new Border(Cframe,chatclient, this, 4, i);
        gridbaglayout.setConstraints(border2, gridbagconstraints);
        add(border2);
        validate();
    }

    public void setTab(int i)
    {
        Tabs.setTab(i);
    }

    public int addTab(String s, String s1)
    {
        tabNames.add(s);
        cardNames.add(s1);
        return tabNames.size() - 1;
    }

    public ArrayList tabNames;
    public ArrayList tabPos;
    public ArrayList cardNames;
    public Panel cardPanel;
    public CardLayout cardLayout;
    public int xofs;
    public Font textFont;
    public int curTab;
    Dimension dim;
    Border Tabs;
    TapPanel Cframe;
	ChatClient chatclient;
}
