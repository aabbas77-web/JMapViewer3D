/*************************************************************************/
/*************************************************************************/
/*************************************************************************/
/*****************Chat Server Client Object*******************************/
/*************************************************************************/
/*************************************************************************/
/*************************************************************************/
 
package oghab.mapviewer.tcp_server;
import java.net.Socket;
public class ClientObject
{
	ClientObject(Socket socket,String UserName,String RoomName)
	{
		ClientSocket = socket;
		ClientUserName = UserName;
		ClientRoomName = RoomName;
	}
			
	public void setSocket(Socket socket)
	{
		ClientSocket =  socket;
	}
	
	public void setUserName(String UserName)
	{
		ClientUserName = UserName;
	}
	
	public void setRoomName(String RoomName)
	{
		ClientRoomName = RoomName;
	}
	
	public Socket getSocket()
	{
		return ClientSocket;
	}
	
	public String getUserName()
	{
		return ClientUserName;
	}
	
	public String getRoomName()
	{
		return ClientRoomName;
	}
	
Socket ClientSocket;
String ClientUserName,ClientRoomName;
}