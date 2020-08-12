package ProtocolLearner;
import java.awt.Color;
import java.util.*;

import FuzzingEngine.StringForFuzzing;
import FuzzingEngine.StringFuzzerEngine;

/**
 * Abstract Finite State Automaton class. It provides basic operations 
 * to populate and manipulate finite state automaton. 
 * This is an extended version of the FSA since it allows sequences of 
 * output messages to be associated with the input sequences. 
 * 
 * @author Serge Gorbunov
 */
public class FSAutomaton 
{
	/*
	 * Description of some of the finite state automaton attributes:
	 * - states maps index of every state to a particular color, initially
	 *   this color is set to null. It is used for minimization
	 * - stateHeights maps index of every state to its height after 
	 *   construction of the FSA is finished. It is needed for FSA minimization
	 */
	private Set<String>    inputAlphabet  = null ;
	private Set<String>    outputAlphabet = null ;
	private HashMap<Integer, Color>   states  ;
	private HashMap<Integer, Integer> stateHeights ;
	private int            initialState   = 0    ;
	private int			   nextStateIndex = 0    ;
	private HashMap<Integer, ArrayList<FSATransition>> transFuncs = new HashMap<Integer, ArrayList<FSATransition>>() ;

	/**
	 * Constructor. Sets the initial state in the automaton is 0, index of the next states to be added is 1
	 */
	public FSAutomaton()
	{
		initialState = 0 ;
		// maps every state to its color
		states = new HashMap<Integer, Color>() ;
		// maps every state to its height, which is dynamically updated when a new transition is added
		stateHeights = new HashMap<Integer, Integer>() ;
		states.put( initialState, null ) ;
		stateHeights.put( initialState,  0 ) ;
		nextStateIndex = states.size() ;
		inputAlphabet = new HashSet<String>() ;
		outputAlphabet = new HashSet<String>() ;

	}

	/**
	 * Returns the index of the initial state
	 * 
	 * @return Index of the root state
	 */
	public int getInitStateIndex()
	{
		return initialState ;
	}
	/**
	 * Adds a new state to the set of states
	 *
	 * @return The index of the added state
	 */
	public int addState()
	{
		int stateID = nextStateIndex++ ;
		states.put( stateID, null ) ;
		stateHeights.put( stateID,  0 ) ;
		transFuncs.put( stateID, new ArrayList<FSATransition>() ) ;
		return stateID ;
	}
	/**
	 * Adds a next transition function to the automaton
	 * 
	 * @param srcState Source state
	 * @param inputSymbol Input symbol
	 * @param dstState Destination state
	 * 
	 * @return 0 if successfully added the transition, -1 if no source state exists,
	 * 			 -2 if no destination state exists
	 * 			 
	 */
	public int addTransition( int srcState, String intputSymbol, int dstState )
	{
		return addTransition(srcState, intputSymbol, dstState, null ) ;
	}
	/**
	 * Adds a next transition function to the automaton
	 * 
	 * @param srcState source state ID
	 * @param inputSymbol input symbol
	 * @param dstState destination state
	 * @param outputSymbols A list of output symbols associated with the input
	 * 
	 * @return 0 if successfully added the transition, -1 if no source state exists,
	 * 			 -2 if no destination state exists
	 * 			 
	 */
	public int addTransition( int srcState, String inputSymbol, int dstState, ArrayList<String> outputSymbols )
	{
		if ( ! states.containsKey( srcState ) ) 
			return -1 ;
		if ( ! states.containsKey( dstState ) )
			return -2 ;
		if ( ! inputAlphabet.contains( inputSymbol ) )
			inputAlphabet.add( inputSymbol ) ;
		if ( outputSymbols != null )
		{
			for ( String out : outputSymbols )
			{
				if ( ! outputAlphabet.contains( outputSymbols ) )
					outputAlphabet.add( out ) ;
			}
		}	
		FSATransition transitionObj = null ;

		// Use appropriate constructor based on whether the output symbol has been specified
		if ( outputSymbols != null )
			transitionObj = new FSATransition( inputSymbol, dstState, outputSymbols ) ;
		else
			transitionObj = new FSATransition( inputSymbol, dstState ) ;

		// If the function map contains the key we simply add a new transition
		// otherwise we add the srcState as the key and then add the transition
		ArrayList<FSATransition> stateTransactions = transFuncs.get( srcState ) ;
		if ( stateTransactions == null )
			stateTransactions = new ArrayList<FSATransition>();

		// Add the transition into the list
		stateTransactions.add( transitionObj ) ;
		transFuncs.put( srcState, stateTransactions ) ;
		return 0 ;
	}

	/**
	 * Checks if a transition exists from a states based on the certain input
	 * 
	 * @param srcState Source state ID
	 * @param input Input string to be searched for in the set of transitions of the state 
	 * 
	 * @return  Index to the destination transition where transition leads to, or -1 if no such exists
	 */
	public int checkTransition( int srcState, String input )
	{
		Iterator it = transFuncs.entrySet().iterator();
		while (it.hasNext()) 
		{
			Map.Entry pairs = (Map.Entry)it.next();
			if ( (Integer)pairs.getKey() == srcState )
			{
				for ( FSATransition t : (ArrayList<FSATransition>) pairs.getValue() )
				{
					if ( t.getInputSymbol().equalsIgnoreCase(input) )
					{
						// new State is the destination state
						return t.getNewState() ;
					}
				}
			}
		}
		return -1 ;
	}

	/**
	 * Check if the output strings of the given input symbol in the automaton
	 * math to the output symbols of the given input
	 * 
	 * @param src State Source state ID
	 * @param input Input symbol
	 * @param output Output message sequence
	 * 
	 * @return true - if the output message sequence matches to the output message sequence 
	 *  of the transition. False - otherwise
	 */
	public boolean checkOutputMsg( int srcState, String input, ArrayList<String> output )
	{
		for ( FSATransition t : transFuncs.get( srcState ) )
		{
			// Check if the output messages sequence matches to an
			// output message sequence in the transaction
			if ( t.getInputSymbol().equalsIgnoreCase( input ) && t.checkOutMsgSeqPresense( output ))
				return true ;
		}
		return false ;	
	}

	/**
	 * Add an output message to an array of output message
	 * sequences for the particular input on a given state
	 * 
	 * @param srcState Source state ID
	 * @param dstState Destination state ID
	 * @param inSymbol Input symbol
	 * @param outSymbols Output message sequence
	 * 
	 * @return True: if output sequence was added successfully, false otherwise
	 */
	public boolean addOuputSymbol( int srcState, int dstState, String inSymbol, ArrayList<String> outSymbols )
	{
		// Iterate through the transitions for the given input state,
		// find the transition with the desired output 
		for ( FSATransition t : transFuncs.get( srcState ) )
		{
			if ( t.getInputSymbol().equalsIgnoreCase( inSymbol ) && t.getNewState() == dstState )
			{
				return t.addOutputSequence( outSymbols ) ;
			}
		}
		return false ; 
	}

	/**
	 * Returns all states in the automaton
	 * 
	 * @return A map of States, and their associated colors
	 */
	public HashMap<Integer, Color> getStates()
	{
		return states ;
	}

	/**
	 * Modifies the color of a particular state
	 * 
	 * @param State ID
	 * 
	 * @return True if the color was successfully changed, false if no such states exists
	 */
	public boolean setStateColor( int stateID, Color c )
	{
		if ( ! states.containsKey( stateID ))
			return false ;
		else
		{
			states.put( stateID,  c ) ;
			return true ;
		}
	}

	/**
	 * Returns the list of states that have transitions
	 * 
	 * @return A set of states from the transition hash map
	 */
	public Set<Integer> getStatesWithTrans()
	{
		Set<Integer> states = new HashSet<Integer>() ;
		Iterator it = transFuncs.entrySet().iterator();
		while (it.hasNext()) 
		{
			Map.Entry pairs = (Map.Entry)it.next();
			int state = (Integer)pairs.getKey() ;
			ArrayList<FSATransition> transitions = (ArrayList<FSATransition>)pairs.getValue() ;
			if ( transitions.size() != 0 )
				states.add( state ) ;
		}
		return states ; 
	}

	/**
	 * Returns the set of transition functions for a state.
	 * <p> 
	 * This method returns null if no state ID is present in the automaton.
	 * 
	 * @param   stateID Unique state identifier
	 * @return  An array list of transitions or null if no transitions found/no state found. 
	 * @see     FSAutomaton
	 */
	public ArrayList<FSATransition> getStateTransition( int stateID )
	{
		return transFuncs.get( stateID ) ;
	}

	/**
	 * Returns the color of the state
	 * @param statID Unique state ID
	 * @return State color or null if no such state exists
	 */
	public Color getStateColor( int stateID )
	{
		return states.get( stateID ) ;
	}

	/**
	 * Sets the height of the state to a specific value
	 * 
	 * @param stateID Unique state ID
	 * @param height New state height
	 * 
	 * @return True is the state height was successfully set.
	 * False if no stateID was found in the list. 
	 */
	public boolean setStateHeight( int stateID, int height )
	{
		if ( ! stateHeights.containsKey( stateID ) )
			return false ;
		else
		{
			stateHeights.put( stateID, height ) ;
			return true ;
		}
	}

	/**
	 * Returns the height of the state
	 * 
	 * @param stateID Unique state ID
	 * 
	 * @return The height of the state, or -1 if the state is not found
	 */
	public int getStateHeight( int stateID )
	{
		if ( ! stateHeights.containsKey( stateID ) )
			return -1 ;
		else
			return stateHeights.get( stateID ) ;
	}

	/**
	 * Returns the array list of states with a particular height
	 * 
	 * @param height Height to be searched for among all states
	 * @return An array list of state IDs with the specified height
	 */
	public ArrayList<Integer> getStatesWithSpecHeight( int height )
	{
		ArrayList<Integer> states = new ArrayList<Integer>() ;
		Iterator it = stateHeights.entrySet().iterator();
		while (it.hasNext()) 
		{
			Map.Entry pairs = (Map.Entry)it.next();
			int state = (Integer)pairs.getKey() ;
			int stateHeight = (Integer)pairs.getValue() ;
			if ( stateHeight == height )
				states.add( state ) ;
		}
		return states ; 
	}

	/**
	 * Function compares two states based on their transitions and
	 * colors of their children. States are considered identical if they 
	 * have the same input/output transition functions and their children have the same color.
	 * 
	 * @param stateID1 The first unique state ID
	 * @param stateID2 The second unique state ID
	 * 
	 * @return True if the states are identical, false otherwise
	 */
	public boolean checkIfStatesAreIdentical( int stateID1, int stateID2 )
	{
		// We iterate through the set of transitions for each states and check 
		// if they are identical
		for ( FSATransition t: this.getStateTransition( stateID1 ) )
		{
			String inputSymbol = t.getInputSymbol() ;
			int stateTwoSubtreeIndex = this.checkTransition( stateID2,  inputSymbol ) ;
			// If states two doesn't have a transition for the current input symbol,
			// they we know that the states are not identical 
			// And check if the color if the states where the transition leads to
			// are identical.
			if ( stateTwoSubtreeIndex == -1 || 
					! ( this.getStateColor( t.getNewState() ).equals(this.getStateColor( stateTwoSubtreeIndex ) ) ) ) 
				return false ;
			// Check if the output sequence of one of the states in in the output
			// array of sequences of the other state
			else
			{
				ArrayList<ArrayList<String>> outputSqs = t.getOutputMsgSeq() ;
				for ( ArrayList<String> outputMsg : outputSqs )
					if ( this.checkOutputMsg( stateID2, inputSymbol, outputMsg) == false )
						return false ;
			}
		}
		for ( FSATransition t: this.getStateTransition( stateID2 ) )
		{
			String inputSymbol = t.getInputSymbol() ;
			int stateTwoSubtreeIndex = this.checkTransition( stateID1,  inputSymbol ) ;
			if ( stateTwoSubtreeIndex == -1 || 
					! ( this.getStateColor( t.getNewState() ).equals(this.getStateColor( stateTwoSubtreeIndex ) ) ) ) 
				return false ;
			else
			{
				ArrayList<ArrayList<String>> outputSqs = t.getOutputMsgSeq() ;
				for ( ArrayList<String> outputMsg : outputSqs )
					if ( this.checkOutputMsg( stateID1, inputSymbol, outputMsg) == false )
						return false ;
			}
		}
		return true ;
	}

	/**
	 * A recursive function to remove a subtree from a FSA
	 * 
	 * @param substreeStateID Unique state ID of the subtree 
	 * 
	 * @return True if the state was successfully removed, False otherwise or if state ID is not found
	 */
	public boolean removeSubtree(  int subtreeStateID )
	{
		if ( ! transFuncs.containsKey( subtreeStateID ) )
			return false ;
		ArrayList<FSATransition> subtreeTransitions = this.getStateTransition( subtreeStateID ) ;
		for ( int i = 0; i < subtreeTransitions.size(); i++ )
		{
			FSATransition t = subtreeTransitions.get( i ) ;
			this.removeSubtree( t.getNewState() ) ;
		}
		// Remove the subtree itself and all of its attributes
		transFuncs.remove( subtreeStateID ) ;
		states.remove( subtreeStateID ) ;
		stateHeights.remove( subtreeStateID ) ;
		return true ;
	}

	/**
	 * A function that remove a specific transition for some input from a state
	 * 
	 * @param stateID Unique state ID for the state of which transition should be removed
	 * @param dstStateID Unique state ID of the destination state of the transition
	 * 
	 * @return True if the transition was successfully removed, False if state(s) was not found.
	 */
	public boolean removeTransitionFromState( int stateID, int dstStateID )
	{
		ArrayList<FSATransition> stateTransitions = this.getStateTransition( stateID ) ;
		for ( int i = 0; i < stateTransitions.size(); i++ )
		{
			FSATransition t = stateTransitions.get( i ) ;
			if ( t.getNewState() == dstStateID )
			{
				stateTransitions.remove( i ) ;
				transFuncs.put( stateID,  stateTransitions ) ;
				return true ;
			}
		}
		return false ;
	}	 

	/**
	 * Add a transition object to the automaton
	 * 
	 * @param transition A new transition object
	 * 
	 * @return -1 if no source state id exists
	 * 			0 if everything went fine
	 */
	public int addTransition( int srcState, FSATransition transition )
	{
		if ( ! states.containsKey( srcState ) ) 
			return -1 ;

		// If the function map contains the key we simply add a new transition
		// otherwise we add the srcState as the key and then add the transition
		ArrayList<FSATransition> stateTransactions = transFuncs.get( srcState ) ;
		if ( stateTransactions == null )
			stateTransactions = new ArrayList<FSATransition>();

		// Add the transition into the list
		stateTransactions.add( transition ) ;
		transFuncs.put( srcState, stateTransactions ) ;
		return 0 ;
	}



}
