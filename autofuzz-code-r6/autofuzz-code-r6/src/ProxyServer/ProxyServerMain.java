package ProxyServer;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;

import socks.*;
import socks.server.ServerAuthenticatorNone;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import ProtocolLearner.MessageTrace;

/**
 * This is the main proxy server class that provides functionality to instantiate
 * a proxy server on a given port and start/stop listening.
 *  
 * @author Serge Gorbunov
 * 
 *
 */
public class ProxyServerMain extends Thread 
{

	private static socks.ProxyServer server ;
	private static ProxyAuthenticator auth ;
	private static int port ;
	private static final int defaultPort  = 22222 ; 
	ProxyServerMain serverThread ;
	
	public ProxyServerMain()
	{
		this.port = defaultPort ;
	}
	
	public ProxyServerMain( int p ) 
	{
		this.port = p  ;	
	}
	
	/**
	 * Starts a proxy server on the specified port
	 * 
	 * @param port Port on which the proxy should be started
	 * 
	 * @return True if the server is started successfully
	 */
	public boolean startProxyServer( int port )
	{
		try
		{
			auth = new ProxyAuthenticator() ;
			server = new socks.ProxyServer( auth )  ;
			try
			{
				BufferedOutputStream streamLog = new BufferedOutputStream(new FileOutputStream("log.txt" ) ) ;
				server.setLog( streamLog ) ;
			}
			catch (Exception e) {
				// TODO: handle exception
				System.err.println("Could not append log.txt to the proxy server. ProxyServer::ProxyServerMain.java") ;
			}
			serverThread = new ProxyServerMain( port ) ;
			serverThread.start() ;
			return true ;
		}
		catch (Exception e) {
			return false ;
		}
	}
	public void run()
	{
		try
		{
			server.start( this.port ) ;
		}
		catch (Exception e) {
			
		}
	}
	
	
	/**
	 * Stops recording traffic on the proxy 
	 */
	public void stopProxyServer() 
	{
		if ( serverThread != null )
			serverThread.stopServerThread() ;
	}
	public void stopServerThread()
	{
		server.stop() ;
	}
	
	/** 
	 * Starts recording traffic 
	 */
	public void startRec()
	{
		if ( serverThread != null )
			serverThread.startThreadRec() ;
	}
	public void startThreadRec()
	{
		server.startRecording() ;
	}
	
	/**
	 * Stops recording traffic
	 */
	public void stopRec()
	{
		if ( serverThread != null )
			serverThread.stopThreadRec() ;
	}
	public void stopThreadRec()
	{
		server.stopRecording() ;
	}
	
	public void startFuzzer( )
	{
		if ( serverThread != null )
			serverThread.startThreadFuzzer(  ) ;
	}
	public void startThreadFuzzer(  )
	{
		server.startFuzzer(  ) ;
	}
	
	public void stopFuzzer()
	{
		if ( serverThread != null )
			serverThread.stopThreadFuzzer() ;
	}
	public void stopThreadFuzzer()
	{
		server.stopFuzzer() ;
	}
}


