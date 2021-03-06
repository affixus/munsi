package com.example.websocket.servlet.tomcat8.test;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.example.websocket.servlet.tomcat8.CustomConfigurator;

/**
 * Servlet implementation class WebSocketServletImpl
 */
@ServerEndpoint(value = "/chat2", configurator = CustomConfigurator.class)
public class EndPointImpl extends Endpoint
{
	private String mstrId;
	private String mstrName;
	private Session mSession;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EndPointImpl()
    {
    	super();
    }

	@Override
	public void onOpen(Session pSession, EndpointConfig pEPconfig)
	{
		mSession = pSession;
    	HttpSession lHttpSession = (HttpSession) pEPconfig.getUserProperties().get(HttpSession.class.getName());
		mstrId = lHttpSession.getId();
		mstrName = (String) lHttpSession.getAttribute("USER_NAME");
		
		System.out.println("WebSocket connection opened for "+mstrName);
    	WebSocketUtil8.addWebClient(this);
		
		pSession.addMessageHandler (new MessageHandler.Whole<String>()
		{
			@Override
			public void onMessage(String pMessage)
			{
				  onMessageReceived(pMessage);
			}
		});
	}
	
	@Override
	public void onClose(Session session, CloseReason closeReason) {
		super.onClose(session, closeReason);
		System.out.println("WebSocket connection closed for "+mstrName);
		WebSocketUtil8.removeWebClient(this);
	}
	
	@Override
	public void onError(Session session, Throwable throwable) {
		System.out.println("WS Error");
		super.onError(session, throwable);
	}
	
	 public void onMessageReceived(String pMessage)
     {
		 System.out.println("Message received from "+mstrName);
		 WebSocketUtil8.sendToAllClients(pMessage, this);
     }
	 
	public void writeMessage(String pMessage)
	{
    	if( !mSession.isOpen() )
		{
			return;
		}
		
		RemoteEndpoint.Basic lRemote = mSession.getBasicRemote();
		try 
		{
			lRemote.sendText(pMessage);
		}
		catch (IOException e) {
			//e.printStackTrace();
		}
	}
	
	public String getId()
	{
		return mstrId;
	}

	public void setId(String pID)
	{
		this.mstrId = pID;
	}
	
	public String getName()
	{
		return mstrName;
	}

	public void setName(String pName)
	{
		this.mstrName = pName;
	}
	
	
	
	@Override
	public boolean equals(Object pObj)
	{
		EndPointImpl lEndPointImpl = (EndPointImpl) pObj;
		if( lEndPointImpl == null)
		{
			return false;
		}
		return mstrId.equals(lEndPointImpl.mstrId);
	}
	
	@Override
	public int hashCode()
	{
		return mstrId.hashCode();
	}
}
