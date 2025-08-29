package oghab.mapviewer.tcp_client;

import java.awt.Button;
class CustomButton extends Button
{
	public CustomButton(ChatClient Parent, String label)
	{
		chatclient = Parent;
		setLabel(label);
		setBackground(chatclient.ColorMap[3]);
	    setForeground(chatclient.ColorMap[2]);		
	}
ChatClient chatclient;
}