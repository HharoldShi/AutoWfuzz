package ProxyServer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

import FuzzingEngine.FuzzerMain;

/**
 * This class keeps an instance of a command and control socket,
 * which provides functionality to control the fuzzer remotely.
 * Currently, it is only possible to reset the fuzzer current state
 * to the initial state.
 * 
 * @author Serge Gorbunov
 *
 */
public class ComAndControlSocket extends Thread {

	private static int port ;
	private static final int defaultPort  = 22222 ;
	private static FuzzerMain fuzzer ; 
	ComAndControlSocket socketThread ;

	public ComAndControlSocket( FuzzerMain f )
	{
		this.port = defaultPort ;
		fuzzer = f ;
	}

	public ComAndControlSocket( int p, FuzzerMain f ) 
	{
		this.port = p  ;	
		fuzzer = f ;
	}

	/**
	 * Opens up a socket on the specified port
	 * 
	 * @param port Port number
	 * 
	 * @return True if the socket was successfully opened
	 */
	public boolean openSocket( int port )
	{
		try
		{
			socketThread = new ComAndControlSocket( port, fuzzer ) ;
			socketThread.start() ;
			return true ;
		}
		catch (Exception e) {
			return false ;
		}
	}
	public void run()
	{
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket( port );
		} catch (IOException e) {
			JOptionPane.showMessageDialog( null, "Could not listen on port: " + port ) ;
			return ;
		}

		Socket clientSocket = null;
		try {
			clientSocket = serverSocket.accept();
		} catch (IOException e) {
			JOptionPane.showMessageDialog( null, "Could not accept command and control client connection" ) ;
			return ;
		}

		try {
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							clientSocket.getInputStream()));
			String inputLine, outputLine;


			//out.println(outputLine);

			while ((inputLine = in.readLine()) != null) {
				//System.out.println( "ProxyServer.ComAndControlSocket:: input " + inputLine ) ;
				if ( inputLine.equalsIgnoreCase( "RESET" ) )
				{
					fuzzer.resetCurrentState() ;
					//System.out.println( "ProxyServer.ComAndControlSocket:: Resetting the state" ) ;
				}
				else if ( inputLine.equalsIgnoreCase( "Bye." ) )
					break;
			}


			out.close();
			in.close();
			clientSocket.close();
			serverSocket.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

	}
}


