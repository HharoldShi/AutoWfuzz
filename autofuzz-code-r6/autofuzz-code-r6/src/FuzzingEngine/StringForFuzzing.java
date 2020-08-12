package FuzzingEngine;

import java.awt.dnd.Autoscroll;
import java.util.ArrayList;

import BioinfoFieldExtractor.ASCIIconverter;
import BioinfoFieldExtractor.GenericSequence;
import FuzzingFunctions.FuzzingFunctionReturnPair;
import ProtocolLearner.FSAutomaton;

/**
 * This class is used to store string which are to be fuzzed. 
 * Each string string is stored in its original format, and the generic
 * format generated from the sequence alignment. It is also associated with
 * a particular state.
 * 
 * @author Serge Gorbunov
 *
 */
public class StringForFuzzing {
	// this is a replacement for the arraylist representation of the generic sequence
	private GenericSequence genericSeq ;
	private boolean fuzzingCompleteStatus ;
	
	private StringFuzzerEngine engine ;
	
	public StringForFuzzing( GenericSequence seq )
	{
		genericSeq = seq ;
		engine = new StringFuzzerEngine( ) ;
		fuzzingCompleteStatus = false ;
	}
	
	
	/**
	 * @param origMsg The original input message received
	 * @param automaton The protocol's FSA 
	 * @param currentStateID The current state of the fuzzing engine
	 * 
	 * @return A pair of the modified message with the new state. Returns null if the fuzzing
	 * is complete or all fuzzing function have been applied. 
	 * 
	 */
	public FuzzingFunctionReturnPair fuzzGenericMsg( ArrayList<Integer> origMsg, FSAutomaton automaton, int currentStateID )
	{
		FuzzingFunctionReturnPair returnPair = engine.fuzzMessage( genericSeq, origMsg, automaton, currentStateID ) ;
		
		if ( returnPair == null || fuzzingCompleteStatus == true )
		{
			fuzzingCompleteStatus = true ;
			return null ;
		}
		else
		{
			return returnPair ;			
		}
	}
	

	public GenericSequence getGenSequence()
	{
		return genericSeq ;
	}
	
	/**
	 * Returns the current index of the next fuzzing function
	 */
	public int getNextFuzzingFuncIndex()
	{
		return engine.getNextFuzzingFuncIndex() ;
	}
	/**
	 * Returns the total number of available fuzzing functions
	 */
	public int getTotalNumberOfFuzzingFuncs()
	{
		return engine.getTotalNumberOfFuzzingFuncs() ;
	}

}
