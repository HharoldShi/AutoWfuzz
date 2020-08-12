package FuzzingEngine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import AbstractionFuncs.AbstractionFunct;
import AutoFuzzMain.ConfigVariables;
import AutoFuzzMain.LoggingFacility;
import BioinfoFieldExtractor.ASCIIconverter;
import BioinfoFieldExtractor.ConsensusSeq;
import BioinfoFieldExtractor.GenericSeqConstructor;
import BioinfoFieldExtractor.GenericSequence;
import BioinfoFieldExtractor.GlobalAlignment;
import BioinfoFieldExtractor.LocalAlignment;
import BioinfoFieldExtractor.MultiSequenceAligner;
import BioinfoFieldExtractor.PhylogenTree;
import BioinfoFieldExtractor.UPGMA;
import FuzzingFunctions.FuzzingFunctionReturnPair;
import ProtocolLearner.FSAutomaton;
import ProtocolLearner.MessageTrace;
import ProtocolLearner.FSATransition;

/**
 * This class is the main interface for the fuzzing engine. It keeps a local
 * copy of the FSA to be fuzzed. It starts the fuzzing at the initial state; 
 * only the input messages can be fuzzed. It decides whether to modify the
 * message or not based on the current state in the automaton and the current 
 * content of the message.
 *  
 * @author Serge Gorbunov
 *
 */
public class FuzzerMain {
	private FSAutomaton automaton = null ;
	private int currentStateID = -1 ;
	private ArrayList<StringForFuzzing> strings = null ;
	private AbstractionFunct inaf = null ;
	private ArrayList<MessageTrace> traces = null ;
	private JTextArea fuzzingView = null ;
	private LoggingFacility loggingFacility = null ;
	private ConfigVariables variables = null ;
	
	ASCIIconverter converter = null ;

	public FuzzerMain( FSAutomaton automatonToFuzz, AbstractionFunct iabf, ArrayList<MessageTrace> traces, JTextArea fuzzArea )
	{
		automaton = automatonToFuzz ;
		fuzzingView = fuzzArea ;
		currentStateID = automaton.getInitStateIndex() ;
		inaf = iabf ;
		strings = new ArrayList<StringForFuzzing> () ;
		converter = new ASCIIconverter() ;
		loggingFacility = new LoggingFacility() ;
		variables = new ConfigVariables() ;
		this.traces = traces ;
		this.initFuzzingStrings() ;
	}

	/** 
	 * Modifies the message based on the current state in the automaton.
	 * 
	 * @param msg Input message to be modified
	 * 
	 * @return The modified message or null if no functions are active. 
	 */
	public String fuzzMessage( String msg )
	{
		/*
		 * Now, here the process that we take in order to fuzz:
		 * 1) Read an input message
		 * 2) Abstract the message
		 * 3) If the abstraction of the message matches to one of the transitions of the
		 *    current state, then we perform a fuzzing function on that input message. 
		 *    Advance the state
		 * 4) If the abstraction of the message matches to none of the transition of the
		 *    current state, then we do nothing. 
		 */
		
		String abstractedMsg = inaf.abstractMsg( msg ) ;
		if ( abstractedMsg.equals( inaf.getSkipString() ) )
		{			
			return msg ;
		}
		FuzzingFunctionReturnPair returnPair = new FuzzingFunctionReturnPair( converter.convertToDecArrayList( msg ), -1 ) ;
		
		//Append an information message to the fuzzing area
		if ( fuzzingView != null )
		{
			fuzzingView.append( "Current state: " + currentStateID + "\n") ;
			fuzzingView.append( "Input abs. message: " + abstractedMsg + "\n\n" ) ;
			loggingFacility.writeLogMessage( variables.getCurrentFuzzingLogFile(), variables.getLoggingLevelInfo(), "CurrentState: " + currentStateID ) ;
			loggingFacility.writeLogMessage( variables.getCurrentFuzzingLogFile(), variables.getLoggingLevelInfo(), "InputMsg: " + msg ) ;
			loggingFacility.writeLogMessage( variables.getCurrentFuzzingLogFile(), variables.getLoggingLevelInfo(), "InputAbsMsg: " + abstractedMsg ) ;
		}
		/*
		if ( abstractedMsg.equals( inaf.getSkipString() ) )
		{
			fuzzingView.append( "Fuzzed message: " + outputMsg ) ;
			return outputMsg ;
		}
		*/
		
		for ( FSATransition t : automaton.getStateTransition( currentStateID ) )
		{		
			if ( t.getInputSymbol().equalsIgnoreCase( abstractedMsg ) )
			{
				StringForFuzzing fuzzableString = t.getFuzzingString() ;
				// if all the fuzzing functions have been applied, then the modified message will be null
				returnPair = fuzzableString.fuzzGenericMsg( converter.convertToDecArrayList( msg ), automaton, currentStateID ) ;
				if ( returnPair.getNewStateID() != -1 ) 
					currentStateID = returnPair.getNewStateID() ;
				else
					currentStateID = t.getNewState() ;
			}
		}
	
		if ( returnPair.getModifiedMessage() == null )	
		{
			loggingFacility.writeLogMessage( variables.getCurrentFuzzingLogFile(), variables.getLoggingLevelInfo(), "OutputMsg: " + msg ) ;
			return msg ;
		}
		else
		{
			loggingFacility.writeLogMessage( variables.getCurrentFuzzingLogFile(), variables.getLoggingLevelInfo(), "OutputMsg: " + 
					converter.convertArrListToString( returnPair.getModifiedMessage() ) ) ;
			return converter.convertArrListToString( returnPair.getModifiedMessage() ) ;
		}
	}
	
	private void initFuzzingStrings()
	{
		HashMap<String, ArrayList<String>> absMsgs = this.generateAbstractedStrings() ;
		HashMap<String, GenericSequence> genMsgs = this.alingSimilarMsgs( absMsgs ) ;
		this.initFuzzingStrings( automaton.getInitStateIndex(), genMsgs, new ArrayList<Integer>() ) ;
	}

	/**
	 * This function traverses the FSA, records all input strings that will be fuzzed
	 * and generates a generic sequence for the strings that abstract to the same string.
	 * 
	 */
	private void initFuzzingStrings( int stateID, HashMap<String, GenericSequence> genSequences, ArrayList<Integer> visitedStates )
	{
		visitedStates.add( stateID ) ;
		for ( FSATransition t : automaton.getStateTransition( stateID ) )
		{
			if ( ! visitedStates.contains( t.getNewState() ) )
				this.initFuzzingStrings( t.getNewState(), genSequences, visitedStates ) ;
			String abstractedMsg = t.getInputSymbol() ;
			if ( genSequences.containsKey( abstractedMsg ) ) 
			{
				StringForFuzzing str = new StringForFuzzing( genSequences.get( abstractedMsg ) ) ;
				strings.add( str ) ;
				t.setFuzzingString( str ) ;
			}
		}
		// We now have an automaton where each transition has an associated
		// StringForFuzzing, which includes the generic string corresponding to the abstraction
		// and a copy of the FuzzerEngine, which is responsible for fuzzing this 
		// particular string.
	}

	/**
	 * Function performs multiple sequence alignment amongst the sequences that abstract to the same message.
	 */
	private HashMap<String, GenericSequence> alingSimilarMsgs( HashMap<String, ArrayList<String>> abstractedStrings )
	{
		HashMap<String, GenericSequence> genSequences = new HashMap<String, GenericSequence>() ;
		Iterator it = abstractedStrings.entrySet().iterator() ;
		LocalAlignment localAligner = new LocalAlignment() ;
		MultiSequenceAligner multiAligner = new MultiSequenceAligner() ;
		GenericSeqConstructor genConstructor = new GenericSeqConstructor() ;
		
		while ( it.hasNext() )
		{
			Map.Entry<String, ArrayList<String>> IOpairs = (Map.Entry<String, ArrayList<String>>)it.next() ;
			ArrayList<String> strings = IOpairs.getValue() ;
			
			// check if there is only one sample of the input message, then there is no need to
			// do any alignment and that message becomes the generic message
			if ( strings.size() == 0 )
				continue ;
			else if ( strings.size() == 1 )
			{
				GenericSequence s = new GenericSequence() ;
				ArrayList<Integer> constantBlock = new ArrayList<Integer>() ;
				
				for ( int i = 0; i < strings.get( 0 ).length(); i++ )
				{					
					constantBlock.add( (int) strings.get( 0 ).charAt( i ) ) ;
				}
				s.addConstantDataMsgBlock( constantBlock ) ;
				genSequences.put( IOpairs.getKey(), s ) ;
				continue ;
			}
			// We first perform local alignment
			float[][] simMatrix =  localAligner.alignLocally( strings ) ;
			// We now construct a phylogenetic tree
			UPGMA treeBuilder  = new UPGMA( simMatrix, strings ) ;
			PhylogenTree t = treeBuilder.buildTree() ;
			treeBuilder.clusterTrees( t ) ;
			ArrayList<PhylogenTree> trees = treeBuilder.getClusters() ;
			// Extract the root of the tree
			PhylogenTree initTree = trees.get( trees.size() - 1 ) ;
			PhylogenTree e = multiAligner.assignTreeAlignments( initTree ) ;
			// We now have an array list of alignmets generated from the sample messages abstracted to the same 
			ArrayList<ArrayList<Integer>> alignments = multiAligner.alignMultipleSequences(new ArrayList<ArrayList<Integer>>(), new ArrayList<ArrayList<Integer>>(), e) ;
			GenericSequence seq = genConstructor.constructGenericSeq( alignments ) ;
			
			genSequences.put( IOpairs.getKey(), seq ) ;
		}
		return genSequences ;
	}
	/**
	 * LEGACY FUNCTION:
	 * Function performs multiple sequence alignment amongst the sequences that abstract to the same message.
	 */
	/*
	private HashMap<String, ArrayList<ArrayList<Integer>>> alingSimilarMsgs( HashMap<String, ArrayList<String>> abstractedStrings )
	{
		HashMap<String, ArrayList<ArrayList<Integer>>> genSequences = new HashMap<String, ArrayList<ArrayList<Integer>>>() ;
		Iterator it = abstractedStrings.entrySet().iterator() ;
		LocalAlignment localAligner = new LocalAlignment() ;
		MultiSequenceAligner multiAligner = new MultiSequenceAligner() ;
		
		while ( it.hasNext() )
		{
			Map.Entry<String, ArrayList<String>> IOpairs = (Map.Entry<String, ArrayList<String>>)it.next() ;
			ArrayList<String> strings = IOpairs.getValue() ;
			
			// check if there is only one sample of the input message, then there is no need to
			// do any alignment and that message becomes the generic message
			if ( strings.size() == 0 )
				continue ;
			else if ( strings.size() == 1 )
			{
				ArrayList<ArrayList<Integer>> seq = new ArrayList<ArrayList<Integer>>() ;
				
				for ( int i = 0; i < strings.get( 0 ).length(); i++ )
				{					
					ArrayList<Integer> c = new ArrayList<Integer>() ;
					c.add( (int) strings.get( 0 ).charAt( i ) ) ;
					seq.add( c ) ;
				}
				
				genSequences.put( IOpairs.getKey(), seq ) ;
				continue ;
			}
			// We first perform local alignment
			float[][] simMatrix =  localAligner.alignLocally( strings ) ;
			// We now construct a phylogenetic tree
			UPGMA treeBuilder  = new UPGMA( simMatrix, strings ) ;
			PhylogenTree t = treeBuilder.buildTree() ;
			treeBuilder.clusterTrees( t ) ;
			ArrayList<PhylogenTree> trees = treeBuilder.getClusters() ;
			// Extract the root of the tree
			PhylogenTree initTree = trees.get( trees.size() - 1 ) ;
			PhylogenTree e = multiAligner.assignTreeAlignments( initTree ) ;
			// We now have an array list of alignmets generated from the sample messages abstracted to the same 
			ArrayList<ArrayList<Integer>> alignments = multiAligner.alignMultipleSequences(new ArrayList<ArrayList<Integer>>(), new ArrayList<ArrayList<Integer>>(), e) ;
			
			// Generate a generic sequence ( ie the consensus sequence ) 
			ConsensusSeq ss = new ConsensusSeq() ;
			ss.generateSeq( alignments ) ;	
			
			genSequences.put( IOpairs.getKey(), ss.getConsensusSeq() ) ;
		}
		return genSequences ;
	}
	*/
	
	
	/**
	 * This function records the messages that are abstracted so the can be abstracted to the same
	 * string for global alignment and generic sequence generation.
	 * 
	 * @return A hash map of an abstracted msg associated with an array of input msgs
	 */
	private HashMap<String, ArrayList<String>> generateAbstractedStrings()
	{		
		if ( traces == null )
			return null ;
		HashMap<String, ArrayList<String>> abstractedStrings = new HashMap<String, ArrayList<String>>() ;
		for ( MessageTrace trace : traces )
		{
			for ( Map<String, ArrayList<String>> msg : trace )
			{				
				Iterator it = msg.entrySet().iterator();
				while ( it.hasNext() ) {
					// IOpaier: key - input string
					//          value - array of output strings
					Map.Entry IOpairs = (Map.Entry)it.next();
					String inputMsg = (String) IOpairs.getKey() ;
					String abstractedInputMsg = inaf.abstractMsg( inputMsg ) ;
					if ( abstractedInputMsg.equals( inaf.getSkipString() ) )
						continue ;

					// check if the message has been already added to the abstracted strings
					if ( abstractedStrings.containsKey( abstractedInputMsg ) )
					{
						boolean skipAddingMsg = false ;
						for( String s : abstractedStrings.get( abstractedInputMsg ) ) 
						{
							if ( s.equals( inputMsg ) ) 
								skipAddingMsg = true ;
						}
						if ( skipAddingMsg == false )
							abstractedStrings.get( abstractedInputMsg ).add( inputMsg ) ;
					}
					else
					{
						ArrayList<String> inputStrings = new ArrayList<String> () ;
						inputStrings.add( inputMsg ) ;
						abstractedStrings.put( abstractedInputMsg, inputStrings ) ;
					}
				}
			}
		}
		return abstractedStrings ;
	}

	
	/**
	 * @return The automaton that will be fuzzed
	 */
	public FSAutomaton getAutomaton()
	{
		return automaton ;
	}
	
	/**
	 * This function iterates over all strings available for fuzzing
	 * and checks the number of fuzzing functions performed over the
	 * total number of available fuzzing functions 
	 */
	public String calculateCompletionPerc()
	{
		if ( strings == null || strings.size() == 0 )
			return "NULL" ;
		double totalFuncsPerformed = 0 ;
		double totalFuncs = 0 ;
		for ( StringForFuzzing s : strings )
		{
			totalFuncsPerformed += s.getNextFuzzingFuncIndex() ;
			totalFuncs += s.getTotalNumberOfFuzzingFuncs() ;
		}
		//System.out.println (totalFuncs ) ;
		//System.out.println ( totalFuncsPerformed ) ;
		return Double.toString( totalFuncsPerformed / totalFuncs ) ;
	}
	
	/**
	 * Resets the current fuzzing state to the initial state of the FSA built
	 * from the traffic
	 */
	public void resetCurrentState()
	{
		currentStateID = automaton.getInitStateIndex() ;
	}
	
	
}
