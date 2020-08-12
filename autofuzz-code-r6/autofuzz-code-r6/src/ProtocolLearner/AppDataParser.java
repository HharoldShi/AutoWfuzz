package ProtocolLearner;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import java.io.File;
import org.w3c.dom.*;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 


public class AppDataParser 
{
	/**
	 * This function parses the traffic string (from the traffic text ares) 
	 * and returns an array list of messages traces to be used for FSM construction
	 * @param traffic A plain-Text traffic string in the following format:
	 * <ApplicationData>
	 * 	<Trace>
	 * 		<Input> ...InputMessage... </Input>
	 * 		...
	 * 		<Output> ...OuputMessage... </Input>
	 * 		...
	 * 		And so on. Any combination of input/output messages
	 * 	</Trace>
	 * 	<Trace>
	 * 		... Another trace
	 * 	</Trace> 
	 * </ApplicationData>
	 * Note that currently if an input or output message does not have valid
	 * xml tags or there is a mismatching in the tags, this message will be ignored,
	 * no error generated with all other messages added to the msgs of the trace.
	 * @return An array list of message traces 
	 */
	public ArrayList<MessageTrace> getMsgTraces( String traffic )
	{
		ArrayList<MessageTrace> msgTraceArr = new ArrayList<MessageTrace>() ;
		MessageTrace currentTrace = null ;
		String inputStr = null ;
		ArrayList<String> outputArr = new ArrayList<String>() ;

		int bodyStart = traffic.indexOf( "<ApplicationData>" ) ;
		int bodyEnd   = traffic.indexOf( "</ApplicationData>" ) ;
		int traceStart = -1, traceEnd = -1 ;
		int traceIndex = 0 ;

		// If no application data scope found then there is a problem with the syntax
		if ( bodyStart == -1 || bodyEnd == -1 )
			return null ;

		traffic = traffic.substring( bodyStart+17, bodyEnd ) ;

		// Iterate over all traces
		while ( ( traceStart = traffic.indexOf( "<Trace>")) != -1 &&
				( traceEnd = traffic.indexOf( "</Trace>") ) != -1 )
		{
			String trace = traffic.substring( traceStart + 7, traceEnd ) ;
			traffic = traffic.substring( traceEnd + 8 ) ;
			currentTrace = new MessageTrace( traceIndex ) ;
			outputArr = new ArrayList<String>() ;
			inputStr = null ;

			int inputStart = -1, inputEnd = -1, outputStart = -1, outputEnd = -1 ;
			// Iterate over all msgs in every trace
			while ( true )
			{
				String msg = null ;
				inputStart = trace.indexOf( "<Input>" ) ;
				inputEnd   = trace.indexOf( "</Input>" ) ;
				outputStart = trace.indexOf( "<Output>" ) ;
				outputEnd   = trace.indexOf( "</Output>" ) ;

				// Add an input msg to the current trace
				if  ( ( ( inputStart < outputStart && inputEnd < outputEnd ) || 
						( outputStart == -1 && outputEnd == -1 ) ) &&
						( inputStart != -1 && inputEnd != -1 ) ) 
				{
					msg = trace.substring( inputStart + 7, inputEnd ) ;
					trace = trace.substring( inputEnd + 8 ) ;
					//while( msg.length() > 0 && ( msg.charAt(0) == '\t' || msg.charAt(0) == '\n' || msg.charAt(0) == ' ') )
						//msg = msg.substring(1) ;
					if ( inputStr != null )
						currentTrace.addIOPair( inputStr ,  outputArr ) ;
					inputStr = msg ;
					outputArr = new ArrayList<String>() ;
				}
				// Add an ouput msg to the current trace
				else if ( ( ( outputStart < inputStart && outputEnd < inputEnd ) || 
						  ( inputEnd == -1 && inputStart == -1  ) ) &&
						  ( outputStart != -1 && outputEnd != -1 ) ) 
				{
					msg = trace.substring( outputStart + 8, outputEnd ) ;
					trace = trace.substring( outputEnd + 9 ) ;
					//while( msg.length() > 0 && ( msg.charAt(0) == '\t' || msg.charAt(0) == '\n' || msg.charAt(0) == ' ') )
						//msg = msg.substring(1) ;
					if ( inputStr != null )
						outputArr.add( msg ) ;
				}
				// No more input/output msg traces were found so we break 
				else if ( inputEnd == -1 && outputEnd == -1 && inputStart == -1 && outputStart == -1 )
					break ;
				// Otherwise there is an invalid tagging situation so return null.
				else 
					return null ;
			}
			if ( inputStr != null )
				currentTrace.addIOPair( inputStr, outputArr ) ;
			if ( currentTrace != null )
				msgTraceArr.add( currentTrace ) ;
		}		
		return msgTraceArr ;
	}	
}