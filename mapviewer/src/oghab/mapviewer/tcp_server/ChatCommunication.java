/*************************************************************************/
/*************************************************************************/
/*************************************************************************/
/*****************Chat Server Client Communcation Object******************/
/*************************************************************************/
/*************************************************************************/
/*************************************************************************/

 
package oghab.mapviewer.tcp_server;
import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ChatCommunication implements Runnable,CommonSettings
{
	/********Initialize the Socket to the Client***********/
	ChatCommunication(ChatServer chatserver,Socket clientsocket)
	{				
		Parent = chatserver;
		socket = clientsocket;	
		try {	
                inputstream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch(IOException _IOExc) { }
		thread = new Thread(this);
		thread.start();	
	}
	
	/********Implement the Thread Interface*********/
	public void run()
	{
		while(thread != null)
		{
			try {				
                                RFC = inputstream.readLine();
                                System.out.println(RFC);
				/*******RFC Checking**************/
				if(RFC.startsWith("HELO"))
				{					
					Parent.AddUser(socket,RFC.substring(5));														
				}
				
				if(RFC.startsWith("QUIT"))
				{
					Parent.RemoveUser(RFC.substring(5,RFC.indexOf("~")),RFC.substring(RFC.indexOf("~")+1),REMOVE_USER);
					QuitConnection();	
				}
				
				if(RFC.startsWith("KICK"))
				{
					Parent.RemoveUser(RFC.substring(5,RFC.indexOf("~")),RFC.substring(RFC.indexOf("~")+1),KICK_USER);
					QuitConnection();
				}
				
				if(RFC.startsWith("CHRO"))
				{
					Parent.ChangeRoom(socket,RFC.substring(5,RFC.indexOf("~")),RFC.substring(RFC.indexOf("~")+1));	
				}
				
				if(RFC.startsWith("MESS"))
				{
					Parent.SendGeneralMessage(socket,RFC.substring(RFC.indexOf(":")+1),RFC.substring(RFC.indexOf("~")+1,RFC.indexOf(":")),RFC.substring(5,RFC.indexOf("~")));	
				}
				
				if(RFC.startsWith("PRIV"))
				{
					Parent.SendPrivateMessage(RFC.substring(RFC.indexOf("~")+1),RFC.substring(5,RFC.indexOf("~")));	
				}
				
				if(RFC.startsWith("ROCO"))
				{
					Parent.GetUserCount(socket,RFC.substring(5));	
				}								
				if(RFC.startsWith("CALL"))
				{
					Parent.RequestForVoiceChat(socket,RFC.substring(5,RFC.indexOf("~")),RFC.substring(RFC.indexOf("~")+1));	
				}
				
				if(RFC.startsWith("ACCE"))
				{
					Parent.SendUserIP(socket,RFC.substring(5,RFC.indexOf("~")),RFC.substring(RFC.indexOf("~")+1));	
				}
				
				if(RFC.startsWith("CANC"))
				{
					Parent.RejectCall(RFC.substring(5,RFC.indexOf("~")),RFC.substring(RFC.indexOf("~")+1));	
				}
				
				if(RFC.startsWith("QVCT"))
				{
					Parent.QuitVoiceChat(RFC.substring(5,RFC.indexOf("~")),RFC.substring(RFC.indexOf("~")+1));	
				}
				
				if(RFC.startsWith("REIP"))
				{
					Parent.GetRemoteUserAddress(socket,RFC.substring(5,RFC.indexOf("~")),RFC.substring(RFC.indexOf("~")+1));	
				}
				
				if(RFC.startsWith("AEIP"))
				{
					Parent.SendRemoteUserAddress(socket,RFC.substring(5,RFC.indexOf("~")),RFC.substring(RFC.indexOf("~")+1));	
				}	
				if(RFC.startsWith("QUVC"))
				{
					Parent.QuitVideoChat(RFC.substring(5));	
				}	
				
					
			}catch(Exception _Exc) { Parent.RemoveUserWhenException(socket);QuitConnection();}	
		}
	}
	
	private void QuitConnection()
	{
		thread.stop();
		thread = null;		
		try {
		socket.close();
		}catch(IOException _IOExc) { }
		socket = null;	
	}
	/**********Global Variable Declarations***************/
	Thread thread;
	Socket socket;
        BufferedReader inputstream;
	String RFC;
	ChatServer Parent;	
}