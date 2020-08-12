package ProxyServer;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class test extends Thread{

	private static socks.ProxyServer server ;
	private static ProxyAuthenticator auth ;

	public void StartThread() throws InterruptedException, FileNotFoundException
	{
		auth = new ProxyAuthenticator() ;
		server = new socks.ProxyServer( auth )  ;

		BufferedOutputStream streamLog = new BufferedOutputStream(new FileOutputStream("log.txt" ) ) ;
		server.setLog( streamLog ) ;
		
		test t = new test() ;
		t.start() ;
		System.out.println(" hello ") ;
		Thread.sleep( 10000 ) ;
		t.stopme() ;
	}

	public void run() {
		System.out.println( "Starting server ") ;
		server.start( 22222) ;
	}
	
	public void stopme()
	{
		server.stop() ;
	}
}