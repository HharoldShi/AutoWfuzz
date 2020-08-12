package ProxyServer;


import socks.ProxyMessage;
import socks.SocksException;
import socks.UDPEncapsulation;
import socks.server.ServerAuthenticator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.Socket;


public class ProxyAuthenticator implements ServerAuthenticator{

	static final byte[] socks5response = {5,0};

	InputStream in, in2 ; 
	OutputStream out, out2 ;

	/**
Creates new instance of the ServerAuthenticatorNone.
	 */
	public ProxyAuthenticator(){
		this.in = null;
		this.out = null;
	}
	/**
Constructs new ServerAuthenticatorNone object suitable for returning
from the startSession function.
@param in Input stream to return from getInputStream method.
@param out Output stream to return from getOutputStream method.
	 */
	public ProxyAuthenticator(InputStream in, OutputStream out){
		this.in = in;
		this.out = out;
	}
	/**
Grants access to everyone.Removes authentication related bytes from
the stream, when a SOCKS5 connection is being made, selects an
authentication NONE.
	 */
	public ServerAuthenticator startSession(Socket s)
	throws IOException{

		PushbackInputStream in =  new PushbackInputStream(s.getInputStream());
		OutputStream out = s.getOutputStream();

		int version = in.read();
		if(version == 5){
			if(!selectSocks5Authentication(in,out,0))
				return null;
		}else if(version == 4){
			//Else it is the request message allready, version 4
			in.unread(version);
		}else
			return null;

		return new ProxyAuthenticator(in,out);
	}

	/**
 Get input stream.
 @return Input stream speciefied in the constructor.
	 */
	public InputStream getInputStream(){
		
		return in ;
	}
	/**
 Get output stream.
 @return Output stream speciefied in the constructor.
	 */
	public OutputStream getOutputStream(){
		
		return out ;
	}
	/**
 Allways returns null.
 @return null
	 */
	public UDPEncapsulation getUdpEncapsulation(){
		return null;
	}

	/**
Allways returns true.
	 */
	public boolean checkRequest(ProxyMessage msg){
		//System.out.println(msg.toString() ) ;
	
		return true;
	}

	/**
Allways returns true.
	 */
	public boolean checkRequest(java.net.DatagramPacket dp, boolean out){
		return true;
	}

	/**
Does nothing.
	 */
	public void endSession(){
	}

	/**
 Convinience routine for selecting SOCKSv5 authentication.
 <p>
 This method reads in authentication methods that client supports,
 checks wether it supports given method. If it does, the notification
 method is written back to client, that this method have been chosen
 for authentication. If given method was not found, authentication
 failure message is send to client ([5,FF]).
 @param in Input stream, version byte should be removed from the stream
           before calling this method.
 @param out Output stream.
 @param methodId Method which should be selected.
 @return true if methodId was found, false otherwise.
	 */
	static public boolean selectSocks5Authentication(InputStream in, 
			OutputStream out,
			int methodId)
	throws IOException{

		int num_methods = in.read();
		if (num_methods <= 0) return false;
		byte method_ids[] = new byte[num_methods];
		byte response[] = new byte[2];
		boolean found = false;

		response[0] = (byte) 5;    //SOCKS version
		response[1] = (byte) 0xFF; //Not found, we are pessimistic

		int bread = 0; //bytes read so far
		while(bread < num_methods)
			bread += in.read(method_ids,bread,num_methods-bread);

		for(int i=0;i<num_methods;++i)
			if(method_ids[i] == methodId){
				found = true;
				response[1] = (byte) methodId;
				break;
			}

		out.write(response);
		return found;
	}
}
