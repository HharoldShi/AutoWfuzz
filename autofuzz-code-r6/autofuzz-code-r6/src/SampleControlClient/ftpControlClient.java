package SampleControlClient;

import java.io.*;
import java.net.*;

public class ftpControlClient {

	public static void main(String[] args) throws IOException, InterruptedException {

		Socket echoSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		try {
//			echoSocket = new Socket("192.168.192.1", 22223 ) ;
			echoSocket = new Socket("127.0.0.1", 22223 ) ;
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					echoSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Unable to connect to 192.168.192.1 port 22223");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for "
					+ "the connection to: 192.168.192.1");
			System.exit(1);
		}


		BufferedReader stdIn = new BufferedReader(
				new InputStreamReader(System.in));
		String userInput;

		/**
		 * Here all the automation portion takes place. 
		 * Once a message is send, we reset the state of the automaton
		 * by sending "RESET" message to the server control socket.
		 */
		for ( int i = 0; i < 20; i++ )
		{
			for ( int j = 1; j < 8; j++ )
			{
				System.out.println( "Running ftp client. Counter: " + i + "." + j ) ;
				String cmd = "ftp -s:ftp" + j + ".txt" ;
				Process p = Runtime.getRuntime().exec( cmd ) ;			
				Thread.sleep( 10000 ) ;
				p.destroy() ;
				p = null ;
				out.println( "RESET" ) ;
			}
		}

		out.close();
		in.close();
		stdIn.close();
		echoSocket.close();
	}
}
