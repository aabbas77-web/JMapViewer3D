/*************************************************************************/
/*************************************************************************/
/*************************************************************************/
/*****************Chat Server ********************************************/
/*************************************************************************/
/*************************************************************************/
/*************************************************************************/

package oghab.mapviewer.tcp_server; 

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.Properties;
import java.io.Serializable;
import java.io.InputStream;
import java.util.ArrayList;
import java.awt.Button;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Frame;
import java.awt.Color;
import java.awt.Label;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.BorderLayout;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

public class ChatServer extends Frame implements Serializable, ActionListener,Runnable,CommonSettings {
	
	public ChatServer() {
		/*******Intialize all components*********/
		this.setTitle("Chat Server");
		Image iconImage = Toolkit.getDefaultToolkit().getImage("images/icon.gif");
		this.setIconImage(iconImage);
		this.setResizable(false);
		this.setBackground(Color.yellow);		
		this.setLayout(new BorderLayout());
		
		/********Top Panel Coding*********/
		Panel topPanel = new Panel(new BorderLayout());
		topPanel.setBackground(Color.black);
		Label lblTitle = new Label("CHAT SERVER V1.0",1);
		lblTitle.setForeground(Color.white);
		lblTitle.setFont(new Font("Helvitica",Font.BOLD,20));
		topPanel.add("Center",lblTitle);		
		add("North",topPanel);
		
		/********Center Panel Coding*********/
		Panel centerPanel = new Panel(null);
		cmdStart = new Button("START SERVER");
		cmdStart.setBounds(125,10,150,30);
		cmdStart.addActionListener(this);
		centerPanel.add(cmdStart);
		
		cmdStop = new Button("STOP SERVER");
		cmdStop.setBounds(125,50,150,30);
		cmdStop.setEnabled(false);
		cmdStop.addActionListener(this);
		centerPanel.add(cmdStop);
		add("Center",centerPanel);		
		
		setSize(400,150);
		show();
		
		/*****Window Closing Event Section*******/
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {				
				ExitServer();
				dispose();
				System.exit(0);
			}
		});
	}
	
	/*********Action Listener Coding Starts*************/
	public void actionPerformed(ActionEvent evt)
	{
			if(evt.getActionCommand().equalsIgnoreCase("Start Server"))
				{				
					DBProperties = GetDBProperties();
					/*********Initialize the Server Socket*********/
					try {												
						RoomList = "";
						if(DBProperties.getProperty("roomlist") != null)
						{
							RoomList = 	DBProperties.getProperty("roomlist");
						}
						
						int m_portNo = 1436;
						if(DBProperties.getProperty("portno") != null)
							m_portNo = Integer.parseInt(DBProperties.getProperty("portno"));
						serversocket = new ServerSocket(m_portNo);
					}catch(IOException _IOExc) { }
					
					/********Initialize the Array List**********/
					userarraylist = new ArrayList();
					messagearraylist = new ArrayList();	
					
					/********Initialize the thread*************/
					thread = new Thread(this);
					thread.start();
					
					cmdStart.setEnabled(false);
					cmdStop.setEnabled(true);
				}
			
			if(evt.getActionCommand().equalsIgnoreCase("Stop Server"))
				{
					ExitServer();
					cmdStop.setEnabled(false);
					cmdStart.setEnabled(true);	
				}			
	}
	
	/*************Thread Implementation***************/
	public void run()
	{
		/*********Accepting all the client connections and create a seperate thread******/
		while(thread != null)
		{
			try
			{
				/********Accepting the Server Connections***********/
				socket = serversocket.accept();				
				/******* Create a Seperate Thread for that each client**************/
				chatcommunication = new ChatCommunication(this,socket);
				
				thread.sleep(THREAD_SLEEP_TIME);	
			}
			catch(InterruptedException _INExc) 	{ ExitServer(); }
			catch(IOException _IOExc) 			{ ExitServer();	}	
		}	
	}
	
	/*****	Function To Send a Message to Client	**********/
	private void SendMessageToClient(Socket clientsocket,String message)
	{
			try {
                                dataoutputstream = new BufferedWriter(new OutputStreamWriter(clientsocket.getOutputStream()));
				dataoutputstream.write(message+"\r\n");
			}catch(IOException _IOExc) { }
	}
	
	/*********Function To Get the Object Of Given User Name*********/
	private ClientObject GetClientObject(String UserName)
	{
		ClientObject returnClientObject = null;
		ClientObject TempClientObject;
		int m_userListSize = userarraylist.size();
		for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
		{
			TempClientObject = (ClientObject) userarraylist.get(G_ILoop);
			if(TempClientObject.getUserName().equalsIgnoreCase(UserName))
			{
				returnClientObject = TempClientObject;
				break;
			}
		}
		return returnClientObject;
	}
	
	/*****Function To Check whether the Username is Already Exists**********/
	private boolean IsUserExists(String UserName)
	{		
		if(GetClientObject(UserName) != null)
			return true;
		else
			return false;	
	}
	
	/***********Function to get the Index of specified User Name********/
	private int GetIndexOf(String UserName)
	{
		int m_userListSize = userarraylist.size();
		for(G_ILoop = 0; G_ILoop < 	m_userListSize; G_ILoop++)
		{
			clientobject = (ClientObject) userarraylist.get(G_ILoop);
			if(clientobject.getUserName().equalsIgnoreCase(UserName))
				return G_ILoop;					
		}
		return -1;
	}
	
	/********Function To Add a New Client in to the Server List**********/
	protected void AddUser(Socket ClientSocket,String UserName)
	{
			/***If User name Exists return**/
			if(IsUserExists(UserName))
			{
				SendMessageToClient(ClientSocket,"EXIS");
				return;	
			}
			
			/********Send a Room List ********/
			SendMessageToClient(ClientSocket,"ROOM "+RoomList);
			
			/********Send the New User Detail into All Other Users****/					
			int m_userListSize = userarraylist.size();
			String m_addRFC = "ADD  "+UserName;
			StringBuffer stringbuffer = new StringBuffer("LIST ");
			for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
			{
				clientobject = (ClientObject) userarraylist.get(G_ILoop);				
				/***Check the Room Name*****/
				if(clientobject.getRoomName().equals(ROOM_NAME))
				{
					SendMessageToClient(clientobject.getSocket(),m_addRFC);
					stringbuffer.append(clientobject.getUserName());													
					stringbuffer.append(";");									
				}
			}
			
			/*****Add a user in to array list***/
			clientobject = new ClientObject(ClientSocket,UserName,ROOM_NAME);
			userarraylist.add(clientobject);
			
			/********Sending the Complte User List to the New User***********/
			stringbuffer.append(UserName);
			stringbuffer.append(";");
			SendMessageToClient(ClientSocket,stringbuffer.toString());	
												
	}
	
	
	/**********Function to Remove User From Server**************/
	public void RemoveUser(String UserName, String RoomName, int RemoveType)
	{
		ClientObject removeclientobject = GetClientObject(UserName);
		if(removeclientobject != null)
		{
			userarraylist.remove(removeclientobject);	
			userarraylist.trimToSize();
			int m_userListSize = userarraylist.size();
			String m_RemoveRFC=null;
			if(RemoveType == REMOVE_USER)
				 m_RemoveRFC = "REMO "+UserName;
			if(RemoveType == KICK_USER)
				 m_RemoveRFC = "INKI "+UserName;
			/*****Send a REMO RFC to all other Users****/
			for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
			{
				clientobject = 	(ClientObject) userarraylist.get(G_ILoop);
				if(clientobject.getRoomName().equals(RoomName))
					SendMessageToClient(clientobject.getSocket(),m_RemoveRFC);
			}			
		}			
	}
	
	/**********Remove User When Exception Occurs **************/
	protected void RemoveUserWhenException(Socket clientsocket)
	{
			int m_userListSize = userarraylist.size();
			ClientObject removeclientobject;
			for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
			{
				removeclientobject = (ClientObject) userarraylist.get(G_ILoop);
				if(removeclientobject.getSocket().equals(clientsocket))
				{
					String m_RemoveUserName = removeclientobject.getUserName();
					String m_RemoveRoomName = removeclientobject.getRoomName();
					userarraylist.remove(removeclientobject);	
					userarraylist.trimToSize();					
					m_userListSize = userarraylist.size();
					String m_RemoveRFC="REMO "+m_RemoveUserName;
																 
					/*****Send a REMO RFC to all other Users****/
					for(int m_ILoop = 0; m_ILoop < m_userListSize; m_ILoop++)
					{
						clientobject = 	(ClientObject) userarraylist.get(m_ILoop);
						if(clientobject.getRoomName().equals(m_RemoveRoomName))
							SendMessageToClient(clientobject.getSocket(),m_RemoveRFC);
					}
					return;	
				}	
			}
	}
		
	/*********Function To Change the Room *****************/
	public void ChangeRoom(Socket ClientSocket,String UserName, String NewRoomName)
	{
		int m_clientIndex = GetIndexOf(UserName);		
		if(m_clientIndex >= 0)
		{
			/********Update the Old Room to New Room and send the RFC**********/
			ClientObject TempClientObject = (ClientObject) userarraylist.get(m_clientIndex);
			String m_oldRoomName = TempClientObject.getRoomName();
			TempClientObject.setRoomName(NewRoomName);
			userarraylist.set(m_clientIndex,TempClientObject);
			SendMessageToClient(ClientSocket,"CHRO "+NewRoomName);
			
			/****Send all the Users list of that particular room to that client socket****/
			int m_userListSize = userarraylist.size();
			StringBuffer stringbuffer = new StringBuffer("LIST ");			
			for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
			{
				clientobject = (ClientObject) userarraylist.get(G_ILoop);				
				/***Check the Room Name*****/
				if(clientobject.getRoomName().equals(NewRoomName))
				{					
					stringbuffer.append(clientobject.getUserName());
					stringbuffer.append(";");									
				}
			}
			SendMessageToClient(ClientSocket,stringbuffer.toString());
			
			
			/**********Inform to Old Room and New Room Users**********/			
			String m_OldRoomRFC = "LERO "+UserName+"~"+NewRoomName;
			String m_NewRoomRFC = "JORO "+UserName;
			for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
			{
				clientobject = (ClientObject) userarraylist.get(G_ILoop);
				if(clientobject.getRoomName().equals(m_oldRoomName))
					SendMessageToClient(clientobject.getSocket(),m_OldRoomRFC);
				if((clientobject.getRoomName().equals(NewRoomName)) && (!(clientobject.getUserName().equals(UserName))))
					SendMessageToClient(clientobject.getSocket(),m_NewRoomRFC);
			} 			
		}
		
		
	}
	
	/********Function to Send General Message ***************/
	protected void SendGeneralMessage(Socket ClientSocket, String Message,String UserName,String RoomName)
	{
		boolean m_floodFlag = false;
		messagearraylist.add(UserName);		
		if(messagearraylist.size() > MAX_MESSAGE)
		{
			messagearraylist.remove(0);
			messagearraylist.trimToSize();
			
			/*********Chk Whether the User is flooding the message*********/
			String m_firstMessage = (String) messagearraylist.get(0);
			int m_messageListSize = messagearraylist.size();			
			for(G_ILoop = 1; G_ILoop < 	m_messageListSize; G_ILoop++)
			{
				if(messagearraylist.get(G_ILoop).equals(m_firstMessage))
				{
						m_floodFlag = true;
				}
				else
				{
						m_floodFlag = false;
						break;
				}	
			}						
		}
		
		/********Sending a General Message to All the Users*******/
		int m_userListSize = userarraylist.size();
		String m_messageRFC = "MESS "+UserName+":"+Message;
		for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
		{
			clientobject = (ClientObject) userarraylist.get(G_ILoop);
			if((clientobject.getRoomName().equals(RoomName)) && (!(clientobject.getUserName().equals(UserName))))
			{				
				SendMessageToClient(clientobject.getSocket(),m_messageRFC);	
			}	
		}
		
		/********Kick Off the User If he/she flooding the message********/
		if(m_floodFlag)
		{
			SendMessageToClient(ClientSocket,"KICK ");
			messagearraylist.clear();		
		}
				
	}
	
	/*************Function To Send Private Message *************/
	protected void SendPrivateMessage(String Message , String ToUserName)
	{		
		clientobject = GetClientObject(ToUserName);
		if(clientobject != null)
		{
			SendMessageToClient(clientobject.getSocket(),"PRIV "+Message);	
		}
			
	}
	
		/*************Function To Request User For Voice Chat *************/
	protected void RequestForVoiceChat(Socket ClientSocket,String FromUserName,String ToUserName)
	{		
		clientobject = GetClientObject(ToUserName);
		if(clientobject != null)
		{
			SendMessageToClient(clientobject.getSocket(),"REQU "+ GetClientObject(FromUserName).getSocket().getInetAddress().getHostAddress() + "~" + FromUserName);	
		}
			
	}
	
	/*************Function To Quit Voice Chat *************/
	protected void QuitVoiceChat(String FromUserName,String ToUserName)
	{		
		System.out.println(FromUserName + "-->" + ToUserName);
		clientobject = GetClientObject(ToUserName);
		if(clientobject != null)
		{
			SendMessageToClient(clientobject.getSocket(),"QVCT "+ FromUserName + "~" + ToUserName);	
		}
			
	}
	
	/*************Function To Send User IP For Voice Chat *************/
	protected void SendUserIP(Socket ClientSocket,String FromUserName,String ToUserName)
	{		
		
		clientobject = GetClientObject(ToUserName);
		if(ClientSocket != null)
		{
			SendMessageToClient(clientobject.getSocket(),"ADDR "+ GetClientObject(FromUserName).getSocket().getInetAddress().getHostAddress() + "~" + FromUserName);	
		}
	
	}
	
		/*************Function To Reject The Request For Voice Chat *************/
	protected void RejectCall(String FromUserName,String ToUserName)
	{		
		clientobject = GetClientObject(ToUserName);
		if(clientobject != null)
		{
			SendMessageToClient(clientobject.getSocket(),"REJC "+ FromUserName + "~" + ToUserName);	
		}
			
	}
	
	/***********Function to Get Remote User Address ******************/
	protected void GetRemoteUserAddress(Socket ClientSocket,String ToUserName, String FromUserName)
	{
		clientobject = GetClientObject(ToUserName);
		if(clientobject != null)
		{			
			SendMessageToClient(clientobject.getSocket(),"REIP "+ FromUserName +"~"+ClientSocket.getInetAddress().getHostAddress());			
		}
			
	}
	
	/***********Function to Get Remote User Address ******************/
	protected void SendRemoteUserAddress(Socket ClientSocket,String ToUserName, String FromUserName)
	{
		clientobject = GetClientObject(FromUserName);		
		if(clientobject != null)
		{			
			SendMessageToClient(clientobject.getSocket(),"AEIP "+ ToUserName +"~"+ClientSocket.getInetAddress().getHostAddress());						
		}			
	}
	
	/***********Function to Quit Video Chat ******************/
	protected void QuitVideoChat(String ToUserName)
	{
		clientobject = GetClientObject(ToUserName);	
		if(clientobject != null)
		{			
			SendMessageToClient(clientobject.getSocket(),"QUVC");						
		}
	}	
				
	/*********Function to get the User Count in the Room***********/
	protected void GetUserCount(Socket ClientSocket,String RoomName)
	{
		int m_userListSize = userarraylist.size();
		int m_userCount = 0;
		for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
		{
			clientobject = (ClientObject) userarraylist.get(G_ILoop);
			if(clientobject.getRoomName().equals(RoomName))
				m_userCount++;	
		}
		
		SendMessageToClient(ClientSocket,"ROCO "+RoomName+"~"+m_userCount);
	}
	
	/***********Function to Destroy the Objects***********/
	private void ExitServer()
	{
		if(thread != null)
		{
			thread.stop();
			thread = null;
		}
		try {
			if(serversocket != null)
			{				
				serversocket.close();
				serversocket = null;
			}
		}catch(IOException _IOExc) { }		
		userarraylist = null;
		messagearraylist = null;
		cmdStop.setEnabled(false);
		cmdStart.setEnabled(true);
	}
	
	/*********Loading Properties File*******************/
	private	Properties	GetDBProperties()
	{
		/******************  Getting the Property Value From Propeyty File ************************/
		Properties	DBProperties = new Properties();	
		try
		{			
			InputStream	inputstream = this.getClass().getClassLoader().getResourceAsStream("server.properties");
			DBProperties.load(inputstream);
			inputstream.close();
		}
		catch (IOException _IOExc){ }
		finally
		{
			return (DBProperties);
		}
	}
	
	public static void main(String[] args) {		
		ChatServer mainFrame = new ChatServer();				
		mainFrame.setVisible(true);
	}
	
	/********Global Variable Declarations***********/
	Properties DBProperties;
	Button cmdStart,cmdStop;
	ServerSocket serversocket;
	Socket socket;
	ArrayList userarraylist,messagearraylist;
	Thread thread;
	ChatCommunication chatcommunication;	
	BufferedWriter dataoutputstream;
	int G_ILoop;
	ClientObject clientobject;
	String RoomList;	
}
