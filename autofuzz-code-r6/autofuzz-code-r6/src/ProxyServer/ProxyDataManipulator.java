package ProxyServer;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JTextArea;

import AbstractionFuncs.AbstractionFunct;
import AutoFuzzMain.ConfigVariables;
import BioinfoFieldExtractor.ASCIIconverter;
import FuzzingEngine.FuzzerMain;
import FuzzingEngine.StringForFuzzing;
import ProtocolLearner.FSAutomaton;
import ProtocolLearner.MessageTrace;
import ProtocolLearner.FSATransition;

/**
 * This class provides functionality to manipulate Input/Output streams from the proxy server.  
 */
public class ProxyDataManipulator {


	private static ArrayList<MessageTrace> messageTraces ;
	private static MessageTrace currentTrace ;
	private static int traceIndex ;
	private static ArrayList<String> rawTraffic ; 
	private static FuzzerMain fuzzer ;
	private ASCIIconverter converter ;
	// We need to know who the destination (or src) is in order to distinguish the input from output msgs. 
	private static String dstIP ;
	private static JTextArea textAreaTrafficView = null ;
	private static JTextArea textAreaFuzzingView = null ;
	private ConfigVariables variables = null ;
	private boolean fuzzerInitialized = false ;

	/** The constructor
	 * 
	 */
	public ProxyDataManipulator()
	{
		messageTraces = new ArrayList<MessageTrace> () ;
		traceIndex = 0 ;
		currentTrace = new MessageTrace( traceIndex ) ;
		rawTraffic = new ArrayList<String>() ;
		converter = new ASCIIconverter() ;
		variables = new ConfigVariables() ;
	}

	public ProxyDataManipulator( JTextArea area, JTextArea fuzzArea )
	{
		textAreaTrafficView = area ;
		textAreaFuzzingView = fuzzArea ;
		messageTraces = new ArrayList<MessageTrace> () ;
		traceIndex = 0 ;
		currentTrace = new MessageTrace( traceIndex ) ;
		rawTraffic = new ArrayList<String>() ;
		converter = new ASCIIconverter() ;
	}
	public void initializerFuzzer( FSAutomaton in, AbstractionFunct abf )
	{
		fuzzer = new FuzzerMain( in, abf, messageTraces, textAreaFuzzingView ) ;
		fuzzerInitialized = true ;
	}

	/** 
	 * Extracts the message from the stream and records it in the protocol traces object 
	 * 
	 * @param input Input byte array
	 * @return True if the input stream was successfully recorded 
	 * @throws IOException 
	 */
	public boolean recordMsg( byte[] input, int size, String ip ) throws IOException
	{
		String msgStart = "" ;
		String msgBody = "" ;
		String msgEnd = "" ;

		if ( variables.getTrafficOrderIndex() == 0 )
		{
			if ( ip != null )
				msgStart = "<Input>" ;
			else
				msgStart = "<Output>" ;
		}
		else
		{

			if ( ip == null )
				msgStart = "<Input>" ;
			else
				msgStart = "<Output>" ;
		}

		for ( int i = 0; i < size; i++ )
		{
			if ( input[i] != 0 )
			{
				msgBody = msgBody + (char)input[i] ;
			}
		}
		// Escape any character inside in order to preserve the integrity of our xml schema
		// Later we mind want to remember the positions of where the substitutions were made
		/*
		msgBody = msgBody.replaceAll("&", "&amp;" ) ;
		msgBody = msgBody.replaceAll(">", "&gt;" ) ;
		msgBody = msgBody.replaceAll("<", "&lt;" ) ;		
		msgBody = msgBody.replaceAll("\"", "&quot;" ) ;
		msgBody = msgBody.replaceAll("'", "&apos;" ) ;
		 */
		if ( variables.getTrafficOrderIndex() == 0 )
		{
			if ( ip != null )
				msgEnd = "</Input>" ;
			else
				msgEnd = "</Output>" ;
		}
		else
		{
			if ( ip == null )
				msgEnd = "</Input>" ;
			else
				msgEnd = "</Output>" ;
		}

		rawTraffic.add( msgStart + msgBody + msgEnd ) ;
		textAreaTrafficView.append( msgStart + msgBody + msgEnd + "\n\n" ) ;
		return true ;
	}

	public void setDstIP( String ip )
	{
		dstIP = ip ;
	}

	public byte[] modifyMsg( byte[] input, int size, String ip )
	{
		if ( ip == null || fuzzer == null )
			return input ;

		String msg = "" ;
		for ( int i = 0; i < size; i++ )
		{
			if ( input[i] != 0 )
			{
				msg = msg + (char)input[i] ;
			}
			else
				break ;
		}

		String newMsg = fuzzer.fuzzMessage( msg ) ;
		/*
		System.out.println( "ProxyDataManipulator.modifyMsg():: Ori Msg: " ) ;
		System.out.println( "---" ) ;
		System.out.println( msg ) ;
		System.out.println( "---" ) ;
		System.out.println( "ProxyDataManipulator.modifyMsg():: New Msg: "  ) ;
		System.out.println( "---" ) ;
		System.out.println( newMsg ) ;
		System.out.println( "---" ) ;
		 */
		if ( newMsg != null )
		{
			byte[] output = new byte[newMsg.length()] ;
			for ( int i = 0; i < newMsg.length(); i++ )
			{
				output[i] = (byte) newMsg.charAt( i ) ;
			}
			return output ;
		}
		else 
			return input ;
	}

	public void setTraces( ArrayList<MessageTrace> traces )
	{
		messageTraces = traces ;
	}

	/**
	 * @return The fuzzing data (ie, transition ID, Input Sym, Generic Message ) in
	 * a two-dimentional object form
	 */
	public Object[][] getFuzzingData( )
	{
		if ( fuzzer.getAutomaton() == null )
			return null ;
		ArrayList<ArrayList<Object>> data = 
			this.getStateFuzzingData( fuzzer.getAutomaton(), fuzzer.getAutomaton().getInitStateIndex(), new ArrayList<ArrayList<Object>>(), new HashSet<Integer>() ) ;
		Object[][] dataSet = null ;
		int cols = 0 ;
		if ( data.size() > 0 )
		{
			dataSet = new Object[data.size()][data.get(0).size()] ;
			cols = data.get( 0 ).size() ;
		}
		else
			return null ;

		for ( int i = 0; i < data.size(); i++ )
		{
			ArrayList<Object> row = data.get( i ) ;
			for ( int j = 0; j < row.size() && j < cols; j++ )
			{
				dataSet[i][j] = row.get( j ) ;
			}
		}
		return dataSet ;
	}

	public ArrayList<ArrayList<Object>> getStateFuzzingData( FSAutomaton automaton, int stateID, ArrayList<ArrayList<Object>> currentObjectSet, HashSet<Integer> visitedStates )
	{		
		for ( FSATransition t : automaton.getStateTransition( stateID ) ) 
		{
			ArrayList<Object> newRow = new ArrayList<Object>() ;
			
			newRow.add( stateID ) ;
			newRow.add( t.getInputSymbol() ) ;
			 
				
			if ( t.getGenericSequence() == null )
				newRow.add( "NULL" ) ;
			else
				newRow.add( t.getGenericSequence() ) ;
			if ( ! currentObjectSet.contains( newRow ) )
				currentObjectSet.add( newRow ) ;
			if ( ! visitedStates.contains( t.getNewState() ) ){ 
				visitedStates.add( t.getNewState() ) ;
				currentObjectSet = this.getStateFuzzingData(automaton, t.getNewState(), currentObjectSet, visitedStates ) ;
			}
		}		
		return currentObjectSet ;
	}

	/**
	 * @return Returns true if the fuzzer was initialized, false otherwise
	 */
	public boolean isFuzzerInitialized()
	{
		return fuzzerInitialized ;
	}

	/**
	 * Returns the completion %
	 */
	public String getCompletionStatus() 
	{
		if ( fuzzerInitialized == false || fuzzer == null )
			return "NULL" ;
		else
			return fuzzer.calculateCompletionPerc() ;
	}

	/**
	 * Returns the instance of the fuzzer object
	 */
	public FuzzerMain getFuzzer()
	{
		return fuzzer ;
	}
}
