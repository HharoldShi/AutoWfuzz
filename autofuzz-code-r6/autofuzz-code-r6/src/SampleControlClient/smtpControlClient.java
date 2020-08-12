package SampleControlClient;

import java.io.*;
import java.net.*;

public class smtpControlClient {

	public static void main(String[] args) throws IOException, InterruptedException {

		Socket echoSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;

		try {
			echoSocket = new Socket("192.168.192.1", 22223 ) ;
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
		for ( int i = 0; i < 30; i++ )
		{
			System.out.println( "Running smtp client. Counter: " + i ) ;
			String cmd = "python \"C:\\Users\\rtd\\Desktop\\RealSamples\\proxyclient.py\"" ;
			Process p = Runtime.getRuntime().exec( cmd ) ;			
			out.println( "RESET" ) ;
			Thread.sleep( 20000 ) ;
			//System.out.println("echo: " + in.readLine());
		}

		out.close();
		in.close();
		stdIn.close();
		echoSocket.close();
	}
}
