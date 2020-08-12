package ProtocolLearner;

import java.util.ArrayList;

import BioinfoFieldExtractor.GenericSequence;
import FuzzingEngine.StringForFuzzing;

/** 
 * Inner class that stores information relevant for each transition such as
 * the destination state, input symbol and output symbol sequences.
 * Every state is associated with a list of transactions in a hash map.
 */
public class FSATransition
{
	private int newState = -1 ;
	private String inputSymbol ;
	private StringForFuzzing fuzzingString = null ;
	private int stepsBeforeAdding = - 1 ; 
	// An input symbol can be mapped to an array list of output message sequences
	// Each output message sequence is an array list of output messages 
	// The order of the messages in the output message sequences matters.
	// If two output message sequences have identical messages but do not
	// have them in the same order, those sequences are considered different.
	private ArrayList<ArrayList<String>> outputMsgSequences ;

	/**
	 * Constructor 1 (with output message sequence) 
	 * 
	 * @param inputSymbol Input symbol for the transition
	 * @param newState    Destination state
	 * @param outputMsgSeq A sequence of output messages associated with the transition
	 */
	public FSATransition( String inputSymbol, int newState, ArrayList<String> outputMsgSeq )
	{
		this.newState = newState ;
		this.inputSymbol = inputSymbol ;
		this.outputMsgSequences = new ArrayList<ArrayList<String>>() ;
		this.outputMsgSequences.add( outputMsgSeq ) ;
	}
	/**
	 * Constructor 2 (without output message sequence) 
	 * 
	 * @param inputSymbol Input symbol for the transition
	 * @param newState    Destination state
	 * @param outputMsgSeq A sequence of output messages associated with the transition
	 */
	public FSATransition( String inputSymbol, int newState )
	{
		this.newState = newState ;
		this.inputSymbol = inputSymbol ;
		this.outputMsgSequences = new ArrayList<ArrayList<String>> () ;	
	}
	/**
	 * Returns the next available index for a new state
	 * @return Unique ID of the new state
	 */
	public int getNewState()
	{
		return newState ;
	}
	/**
	 * Returns the input symbol of the transition 
	 * @return The input symbol
	 */
	public String getInputSymbol()
	{
		return inputSymbol ;
	}
	/**
	 * Returns the list of output symbol messages
	 * @return A list of output symbol messages
	 */
	public ArrayList<ArrayList<String>> getOutputMsgSeq()
	{
		return outputMsgSequences ;
	}

	/**
	 * Checks if there is an output message sequences that matches 
	 * to the input out. msg seq. 
	 *           
	 * @param inputList ArrayList of output symbols ( ie output msg sequence )
	 * 
	 * @return True if such sequence is already present, false otherwise
	 */
	public boolean checkOutMsgSeqPresense( ArrayList<String> inputList  )
	{
		boolean matchFound = false ;
		// We iterate through every list of output messages assocciated with
		// the transition. For every list we check if all messages in the list
		// match to all messages in the input list of output messages.
		for ( ArrayList<String> list : outputMsgSequences )
		{
			if ( list.size() != inputList.size() )
				continue ;
			else 
			{
				matchFound = true ;
				for ( int i = 0; i < list.size(); i++ )
				{
					if ( ! list.get(i).equalsIgnoreCase( inputList.get( i ) ) )
					{
						matchFound = false ;
						break ;
					}														
				}
				if ( matchFound == true )
					return true ;
			}
		}
		return false ;
	}

	/**
	 * Adds an output message sequence to the list of output message sequences
	 *            
	 * @param outSeq An output message sequence
	 * 
	 * @return True - if everything finished ok, false otherwise
	 */
	public boolean addOutputSequence( ArrayList<String> outSeq )
	{
		return outputMsgSequences.add( outSeq ); 
	}

	/**
	 * @return The generic sequence 
	 */
	public GenericSequence getGenericSequence()
	{
		if ( fuzzingString == null )
			return null ;
		return fuzzingString.getGenSequence() ;
	}
	/**
	 * Sets the fuzzing string for the transition
	 */
	public void setFuzzingString( StringForFuzzing g )
	{
		this.fuzzingString = g ;
	}
	/**
	 * @return The fuzzer engine corresponding to the transition
	 */
	public StringForFuzzing getFuzzingString( )
	{
		return fuzzingString ;
	}
	
	/**
	 * Steps before adding the transition is used when restoring the loops 
	 * while traversing the message trace 
	 */
	public void setStepsBeforeAdding( int s )
	{
		stepsBeforeAdding = s ;
	}
	public int getStepsBeforeAdding()
	{
		return stepsBeforeAdding ;
	}
	public void decrementStepsBeforeAdding()
	{
		stepsBeforeAdding -- ;
	}

}