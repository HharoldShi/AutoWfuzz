/**
 * ProtocolLeaner is a set of classes that provides functionality to passively learn
 * an application protocol from a large set of sample protocol traces.
 * 
 * @author Serge Gorbunov
 */
package ProtocolLearner;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import AbstractionFuncs.AbstractionFunct;
import ProtocolLearner.FSATransition ; 

/**
 * A passive synthesis protocol learner that builds Finite State Machine 
 * from a set of protocol messages traces
 * 
 * @author Serge Gorbunov
 */
public class PassiveLearner {

	private HashMap<Integer, ArrayList<MessageTrace>> loops ;
	/**
	 * Constructs a FSM from a sample of message traces and input/output abstraction function
	 * 
	 * @param traces ArrayList of message traces
	 * @param input Input abstraction function
	 * @param output Output abstraction function
	 * 
	 * @return Generated FSM 
	 */
	public FSAutomaton constructAutomaton( ArrayList<MessageTrace> traces, AbstractionFunct input, AbstractionFunct output )
	{

		// Remove the loops from the trace
		for ( MessageTrace t : traces )
			this.setTraceLoops( t,  input ) ;

		FSAutomaton automaton = new FSAutomaton() ;

		int initialState = automaton.getInitStateIndex() ;
		int currentState = initialState, dstState ;
		String inputSymbol ;
		ArrayList<String> outputSymbols = new ArrayList<String> ();

		for ( MessageTrace trace : traces )
		{
			currentState = initialState ;
			// These msgs will be skipped since they are a part of a loop
			ArrayList<Integer> skipMsgs = trace.getSkipMsgs() ;
			for ( int i = 0; i < trace.getTraceSize(); i++ )
			{
				if ( skipMsgs.contains( i ) )
					continue ;
				Map<String,ArrayList<String>> msg = trace.getMsg( i ) ;
				Iterator it = msg.entrySet().iterator();
				while ( it.hasNext() ) {
					// IOpaier: key - input string
					//          value - array of output strings
					Map.Entry IOpairs = (Map.Entry)it.next();

					inputSymbol = input.abstractMsg( (String) IOpairs.getKey() ) ;
					if ( inputSymbol.equals( input.getSkipString() ) )
						continue ;
					else if ( inputSymbol == "" || inputSymbol == null )
						return null ;

					outputSymbols = new ArrayList<String> () ;
					for ( String outMsg : (ArrayList<String>) IOpairs.getValue())
					{
						String outputSym = output.abstractMsg( outMsg ) ;
						if ( outputSym.equals( output.getSkipString() ) ) 
							continue ;
						if ( outputSym == "" || outputSym == null )
							return null ;
						else
							outputSymbols.add( outputSym ) ;
					}
					dstState = automaton.checkTransition( currentState, inputSymbol ) ;
					// Check if transition from the current state is not defined
					if ( dstState == -1 )
					{
						// Add a new state to the automaton 
						// change the current state to the just added state
						int stateID = automaton.addState() ;
						automaton.addTransition( currentState, inputSymbol, stateID, outputSymbols ) ;
						currentState = stateID ;
					}
					// Check if the output array doesn't contain the output symbol encountered
					// then we add it, associated it with the input string. 
					else if ( ! automaton.checkOutputMsg( currentState, inputSymbol, outputSymbols ) )
					{
						// Add the output message sequence to the array of output
						// message sequences for the given input
						automaton.addOuputSymbol( currentState, dstState, inputSymbol, outputSymbols ) ;
						currentState = dstState ;
					}
					else 
					{
						// current state = result state of the transition function 
						currentState = dstState ;
					}
				}
			}
		}		
		return automaton ;
	}

	/**
	 * Minimizes a finite state automaton by reducing similar states
	 * And restores the loops that were initially removed from the automaton
	 * 
	 * @param inputAutomaton Finite state automaton
	 * @param traces A collection of protocol traces that were used to construct the automaton
	 * @param input An abstraction function for the input messages
	 * @param output An abstraction function for the output messages 
	 * 
	 * @return Minimized finite state automaton
	 */
	public FSAutomaton minimizeAutomaton( FSAutomaton inputAutomaton, ArrayList<MessageTrace> traces, AbstractionFunct input, AbstractionFunct output )
	{
		// RGB color iterators
		HashMap<Integer, Integer> color = new HashMap<Integer, Integer>() ;
		color.put( 0,  0 ) ;
		color.put( 1,  0 ) ;
		color.put( 2,  0 ) ;

		// Every leaf added in the automaton, won't have any transitions
		// associated with it, so to find all leaves we subtract from the set of 
		// all states that have transitions.
		Set<Integer> allStates = inputAutomaton.getStates().keySet() ; 
		Set<Integer> statesWithTrans = inputAutomaton.getStatesWithTrans() ;
		Set<Integer> leaves = new HashSet<Integer>() ;
		for ( Integer state : allStates )
		{
			if ( ! statesWithTrans.contains( state ) )
				leaves.add( state ) ;
		}
		// Mark all leaves with the same color
		for ( Integer leaf : leaves )
		{
			Color c = new Color( color.get(0), color.get(1), color.get(2) ) ;
			inputAutomaton.setStateColor( leaf, c ) ;
		}
		color = this.incrementColor( color ) ;

		// Set height for each node in the tree 
		findStateHeights(inputAutomaton, inputAutomaton.getInitStateIndex() ) ;

		// We iterate over all states with height 1,2,3... and so on, comparing
		// all of their transitions. If all transitions of the state match and 
		// the its subtrees have the same color, we mark the states as identical
		for ( int height = 1; height < inputAutomaton.getStateHeight( inputAutomaton.getInitStateIndex()); height++ )
		{
			// Find all states with the current height
			ArrayList<Integer> states = inputAutomaton.getStatesWithSpecHeight( height ) ;
			// Compare the transitions of the states 
			for ( int i = 0; i < states.size()-1; i++ )
			{
				int state1 = states.get( i ) ;
				
				Color initColor = new Color( color.get(0), color.get(1), color.get(2) ) ;
				inputAutomaton.setStateColor( state1,  initColor ) ;
				color = this.incrementColor( color ) ;
				
				for ( int j = 0; j < states.size(); j++ )
				{
					int state2 = states.get( j ) ;
					
					// If both states are identical we set them with the same color
					if ( inputAutomaton.checkIfStatesAreIdentical( state1, state2 ) == true )
					{
						inputAutomaton.setStateColor( state2,  initColor ) ;
					}
					// Otherwise set different colors for the states
					else
					{
						Color c = new Color( color.get(0), color.get(1), color.get(2) ) ;
						inputAutomaton.setStateColor( state2,  c ) ;
						color = this.incrementColor( color ) ;
					}
				}
			}
		}
		inputAutomaton = this.combineEquivalentStates( inputAutomaton, inputAutomaton.getInitStateIndex() );
		return this.restoreLoopTransitions( inputAutomaton , traces, input, output) ;
	}

	/**
	 * A recursive function that combined equivalent states in the FSA.
	 * Two states are equivalent if they have the same height and the same color. 
	 * 
	 * @param inputAutomaton Input Finite state automaton
	 * @param height Height for which states should be combined
	 * 
	 * @returns A FSA with equivalent states of the given height combined
	 */
	private FSAutomaton combineEquivalentStates( FSAutomaton inputAutomaton, int heightPointer )
	{
		HashMap<Integer, Integer> parentChildAssoc = this.findStatesParent(inputAutomaton, inputAutomaton.getInitStateIndex());

		for ( int height = (inputAutomaton.getStateHeight( inputAutomaton.getInitStateIndex()) - 1); height > 0 ; height-- )
		{
			// Find all states with the current height
			ArrayList<Integer> states = inputAutomaton.getStatesWithSpecHeight( height ) ;
			// For every state of equivalent height check the color, 
			// if both have equivalent color, then we don't need either one of them
			HashSet<Integer> statesRemoved = new HashSet<Integer>() ;
			for ( int i = 0; i < states.size() - 1; i ++ )
			{
				int s1 = states.get( i ) ;
				Color s1Color = inputAutomaton.getStateColor( s1 ) ;
				
				if ( statesRemoved.contains( s1 ) ) 
					continue ;
				for ( int j = i+1; j < states.size(); j++ )
				{
					int s2 = states.get( j ) ;
					if ( statesRemoved.contains( s2 ) ) 
						continue ;
					if ( s1Color.equals( inputAutomaton.getStateColor( s2 ) ) ) 
					{
						// Redirect the transition of the parent of s1 to the s2. Then we recursively
						// remove s1 and its subtree. 
						int parent = parentChildAssoc.get( s2 ) ;
						for ( FSATransition t: inputAutomaton.getStateTransition( parent ) )
							if ( t.getNewState() == s2 )
							{
								for ( ArrayList<String> outputMsg : t.getOutputMsgSeq() )
									inputAutomaton.addTransition( parent, t.getInputSymbol(), s1, outputMsg ) ;
								inputAutomaton.removeTransitionFromState( parent,  s2 ) ;
								break ;
							}
						// Remove s1 subtree 
						inputAutomaton.removeSubtree( s2 ) ;
						statesRemoved.add( s2 ) ;
					}
				}
			}
		}
		return inputAutomaton ;
	}


	/**
	 * Increments color by one. A 3 value color (ie RGB, each from 0-255) is 
	 * used to identify identical states.
	 * 
	 * @param input HashMap of colors where key 0 = red, 1=green, 2=blue
	 * 
	 * @return Incremented color, if all colors are set to 255, loop back to 0,0,0
	 */
	private HashMap<Integer, Integer> incrementColor( HashMap<Integer,Integer> input )
	{
		int colorR = input.get( 0 ) ;
		int colorG = input.get( 1 ) ;
		int colorB = input.get( 2 ) ;
		if ( colorB >= 255 )
		{
			colorB = 0 ;
			if ( colorG >= 255 )
			{
				colorG = 0 ;
				if ( colorR >= 255 )
					colorR = 0 ; 
				else
					colorR ++ ;
			}
			else
				colorG ++ ;
		}
		else
			colorB ++ ;

		input.put( 0, colorR ) ;
		input.put( 1, colorG ) ;
		input.put( 2, colorB ) ;
		return input ;
	}

	/**
	 * Checks whether the the children of the root of the tree have associated colors.
	 * This method is used for FSM minimization
	 * 
	 * @param inputAutomaton FS automaton
	 * 
	 * @return True if all children of the root have colors, false otherwise
	 */
	public boolean checkRootsChildrenColors( FSAutomaton inputAutomaton )
	{
		int root = inputAutomaton.getInitStateIndex() ;
		ArrayList<FSATransition> rootTransitions = inputAutomaton.getStateTransition( root ) ;
		for ( FSATransition t : rootTransitions )
		{
			int dstState = t.getNewState() ;
			if ( inputAutomaton.getStateColor( dstState ) == null )
				return false ;
		}
		return true;
	}

	/**
	 * Returns a list of states in the finite state automaton that
	 * have their children colored. 
	 * 
	 * @param inputAutomaton Input finite state automaton
	 * 
	 * @return An array list of states those children have colors
	 */
	/*
	public ArrayList<Integer> findStatesWithColoredChildren( FSAutomaton inputAutomaton )
	{
		ArrayList<Integer> outputList = new ArrayList<Integer>() ;
		ArrayList<Integer> traversalQueue = new ArrayList<Integer>() ;
		int root = inputAutomaton.getInitStateIndex() ;
		traversalQueue.add( root ) ;
		while( ! traversalQueue.isEmpty() )
		{
			int state = traversalQueue.remove( 0 ) ;
			ArrayList<Transition> stateTransitions = inputAutomaton.getStateTransition( state ) ;
			for ( Transition t : stateTransitions )
			{
				int dstState = t.getNewState() ;
				if ( inputAutomaton.getStateColor( dstState ) == null )
					traversalQueue.add( traversalQueue.size(),  dstState ) ;
				else if ( state != 0 )
					outputList.add( state ) ;

			}
		}
	}
	 */

	/**
	 * A recursive function that sets a height for each state in the FSA.
	 * 
	 * @param inputAutomaton A finite state automaton
	 * @param statePointer Index of a state for which height is calculated.
	 * 
	 * @return The height of the state
	 */
	public int findStateHeights( FSAutomaton inputAutomaton, int statePointer )
	{
		ArrayList<Integer> childrenHeights = new ArrayList<Integer>() ;
		// Calculate the height for each child of the state
		for ( FSATransition t: inputAutomaton.getStateTransition( statePointer ) )
		{
			childrenHeights.add( findStateHeights( inputAutomaton, t.getNewState() ) ) ;
		}
		// Find the child with the maximum height, hence update the height
		// of the current state based on it.
		int maxHeight = -1 ; 
		for ( int height : childrenHeights )
		{
			if ( height > maxHeight )
				maxHeight = height ;
		}
		inputAutomaton.setStateHeight( statePointer,  maxHeight + 1 ) ;
		return maxHeight + 1 ;
	}

	/**
	 * Function traverses the FSA and stores the parent ID for each child. 
	 * 
	 * @param inputAtomaton input FAS
	 * 
	 * @return A hash map where state is the key, and its parent is the value
	 */
	public HashMap<Integer, Integer> findStatesParent( FSAutomaton inputAutomaton, int statePointer )
	{
		HashMap<Integer, Integer> childParentAssoc = new HashMap<Integer, Integer>() ; 
		for ( FSATransition t : inputAutomaton.getStateTransition( statePointer ) ) 
		{
			childParentAssoc.putAll( findStatesParent(inputAutomaton, t.getNewState() ) ) ;
			childParentAssoc.put( t.getNewState(), statePointer ) ;
		}
		return childParentAssoc ;
	}

	/**
	 * Identifies loops in the trace
	 */
	public void setTraceLoops( MessageTrace trace, AbstractionFunct inab )
	{
		int[][] matrix = new int[trace.getTraceSize()][trace.getTraceSize()] ;

		for ( int i = 0; i < trace.getTraceSize(); i++ )
		{
			HashMap<String, ArrayList<String>> msg1 = trace.getMsg( i ) ;
			for ( int j = 0; j < trace.getTraceSize(); j++ )
			{
				HashMap<String, ArrayList<String>> msg2 = trace.getMsg( j ) ;
				Iterator it = msg1.entrySet().iterator() ;
				Iterator it2 = msg2.entrySet().iterator() ;
				while ( it.hasNext() ) {
					// IOpaier: key - input string
					//          value - array of output strings
					Map.Entry IOpairs1 = (Map.Entry)it.next();
					Map.Entry IOpairs2 = (Map.Entry)it2.next();
					String abs1 = inab.abstractMsg( (String) IOpairs1.getKey()) ;
					String abs2 = inab.abstractMsg( (String) IOpairs2.getKey()) ;
					if ( abs1.equals( abs2 ))
					{
						matrix[i][j] = 1 ;
					}
				}
			}
		}
		// We now have a martix that marks all loops in the trace.
		Loop l ;
		while ( ( l = this.findLoopWithMaxLength(matrix, trace.getTraceSize(), trace.getTraceSize()) ) != null )
		{
			matrix = this.markIdentifiedLoopInMatrix(matrix, l.getTraceID(), l.getLoopLength(), l.getNumOfLoops() ) ;
			// Before adding the loop confirm that it doesnt overlap with any of the existing loops in the trace. 
			if ( loopOverlaps( trace.getLoops(), l ) == false )
				trace.addLoop( l ) ;
		}
	}

	/**
	 * This functions takes a list of loops and a new loop and checks that the new loop
	 * doesnt overlap with any of the existing loops
	 */
	private boolean loopOverlaps( ArrayList<Loop> loops, Loop l )
	{
		for ( Loop exLoop : loops )
		{
			for ( int i = 0 ; i < exLoop.getNumOfLoops(); i++ )
			{
				int s1 = exLoop.getTraceID() + i * exLoop.getLoopLength() ;
				int m1 = s1 + exLoop.getLoopLength() - 1 ;
				int f1 = m1 + exLoop.getLoopLength() ; 

				int s2 = l.getTraceID() ;
				int f2 = s2 + 2*l.getLoopLength() - 1 ;
				// check if loops overlap 
				if ( ! ( ( s2 >= s1 && s2 <= m1 && f2 >= s1 && f2 <= m1 ) || 
						( s2 < s1 && f2 < s1 ) || 
						( s2 > f1 && f2 > f1 ) ) )
					return true  ;
			}
		}
		return false  ;
	}
	/**
	 * This function finds the loop with max length over all loops in the matrix.
	 * The function also verifies that the loop doesnt overlap with any previously 
	 * marked loops. Previously marked loops are identified by 2 in the matrix.
	 * Loops have to be consecutive. 
	 * 
	 * @param matrix The matrix of loop
	 * @param rows The number of rows in the matrix
	 * @param cols The number of columns in the matrix
	 * 
	 * @return A pair of numbers where first number is a trace ID, second number of the length of the loop
	 */
	private Loop findLoopWithMaxLength( int[][] matrix, int rows, int cols  )
	{
		// num1 = the ID of the trace where the loop will begin
		// num2 = the length of the loop
		// num3 = num of consecutive loops
		Loop loop = new Loop() ;
		for ( int i = 0; i < rows;  i++ )
		{
			int maxloopLen = 0 ;
			for ( int j = i; j < cols; j++ )
			{
				int loopLen = 0 ;
				if ( i == j )
					continue ;
				int k = i ;
				int g = j ;
				while ( k < rows && g < cols && i+loopLen<j && matrix[k++][g++] == 1 )
					loopLen++ ;
				// Make sure the loops are consecutive
				if ( loopLen > maxloopLen && i + loopLen == j )
				{
					maxloopLen = loopLen ;
					// Check if the loop is repeating more than once
					int mult = 1 ;
					while ( j+maxloopLen*mult< cols && i < rows && matrix[i][j+maxloopLen*(mult)] == 1 )
					{
						// Check that the diagonal is all 1's for the length of the loop
						g = i ;
						k = j+maxloopLen*(mult) ;
						loopLen = 0 ;
						int l = 1 ;
						while ( k < rows && g < cols && l++ <= maxloopLen &&  matrix[g++][k++] == 1 )
							loopLen ++ ;
						if ( loopLen == maxloopLen )
							mult ++ ;
						else
							break ;
					}
					// Now mult is the number of consecutive loops in the trace
					// TODO: 
					// We confirm that the loop doesnt overlap any of the existing loops


					// Check if the total number of states removed is the largest
					if ( loop.getLoopLength() * loop.getNumOfLoops() < maxloopLen * mult )
					{
						loop.setTraceID( i ) ;
						loop.setLoopLength( maxloopLen ) ;
						loop.setNumOfLoops( mult ) ;
					}
				}
			}
		}
		if ( loop.getTraceID() == 0 && loop.getLoopLength() == 0 && loop.getNumOfLoops() == 0 )
			return null ;
		else
			return loop ;
		//trace.addLoop( i, maxloopLen ) ;
	}

	/**
	 * Replaces 1's in the matrix which previously identified loops in it with 2's meaning
	 * that those loops have already been marked. 
	 * @param matrix
	 * @param rowID
	 * @param length
	 * @param numOfLoops
	 * @return
	 */
	private int[][] markIdentifiedLoopInMatrix( int matrix[][], int rowID, int length, int numOfLoops )
	{
		// mark the loops in the matrix
		int multIndex = 1 ;
		while ( multIndex <= numOfLoops )
		{
			int k = rowID + length*(multIndex) ;
			int l = 0 ;
			while ( l < length ){
				matrix[rowID+l][k] = 2 ;
				// This is needed for loops that repeat more than once
				int w = 1 ;
				while ( w < numOfLoops )
				{
					matrix[(rowID + l) + length*w][k] = 2 ;
					w++ ;
				}
				k++ ;
				l++ ;
			}
			multIndex++ ;
		}
		return matrix ;
	}
	/**
	 * Restore the loops in the automaton based on the repetitive substrings
	 * recorded in the message trace
	 */
	private FSAutomaton restoreLoopTransitions( FSAutomaton automaton, ArrayList<MessageTrace> traces, AbstractionFunct input, AbstractionFunct output )
	{

		int currentState = -1 ;
		for ( MessageTrace trace : traces )
		{
			currentState = automaton.getInitStateIndex() ;
			ArrayList<Loop> loops = trace.getLoops() ;
			ArrayList<FSATransition> loopTransitions = new ArrayList<FSATransition>() ;

			for ( int i = 0; i < trace.getTraceSize(); i++ )
			{
				Map<String,ArrayList<String>> msg = trace.getMsg( i ) ;
				// Record the input/output messages of the previous message
				String inputStr = null ;
				ArrayList<String> outputStrs = null ;
				ArrayList<String> outputAbsStrs = new ArrayList<String>() ;
				Iterator it2 = msg.entrySet().iterator() ;
				while ( it2.hasNext() )
				{
					Map.Entry IOPair = (Map.Entry) it2.next() ;
					inputStr = (String) IOPair.getKey() ;
					outputStrs = (ArrayList<String>) IOPair.getValue() ;
				}
				for ( String s : outputStrs )
					outputAbsStrs.add( output.abstractMsg( s ) ) ;
				if ( inputStr.equals( input.getSkipString() ) )
					continue ;
				// Perform a transition
				for ( FSATransition t : automaton.getStateTransition( currentState ) )
				{
					if ( t.getInputSymbol().equals( input.abstractMsg( inputStr ) ) )
					{
						currentState = t.getNewState() ;
						break ;
					}
				}		
				
				if ( trace.getSkipMsgs().contains( i ) )
				{
					FSAwithPartialLoops a = new FSAwithPartialLoops( automaton, loopTransitions ) ;
					a = this.addLoopTransitions( a , currentState ) ;
					automaton = a.automaton ;
					loopTransitions = a.loopsToBeAdded ;
					continue ;
				}
				
				// Check if there is a loop associated with the current message id,
				// then add the current state and the counter until when a transition
				// to the current state should be added to the array of loops
				for ( Loop loop : loops )
				{
					if ( loop.getTraceID() == i && loop.getLoopLength() != 0 )
					{
						FSATransition transition = new FSATransition( input.abstractMsg( inputStr ), currentState, outputAbsStrs ) ;
						transition.setStepsBeforeAdding( loop.getLoopLength() ) ;
						loopTransitions.add( transition ) ;
					}
				}

				// Go over all loop transitions and check if its time to add
				// the transition to the automaton
				FSAwithPartialLoops a = new FSAwithPartialLoops( automaton, loopTransitions ) ;
				a = this.addLoopTransitions( a , currentState ) ;
				automaton = a.automaton ;
				loopTransitions = a.loopsToBeAdded ;				
			}

		}
		return automaton ;
	}
	
	/**
	 * This function goes over all transition in the queue of transition to be added,
	 * and if its time to add the transition, the function does so and returns 
	 * the new automaton 
	 *
	 * @param automaton the original FSA
	 * @param staetID The current state ID from which the transitions will be added
	 * 
	 * @return A new automaton with some loop transition added, if necessary
	 */
	private FSAwithPartialLoops addLoopTransitions( FSAwithPartialLoops automaton, int stateID )
	{
		ArrayList<FSATransition> loopTransitionsToRemove = new ArrayList<FSATransition>() ;
		for ( FSATransition tran : automaton.loopsToBeAdded )
		{
			if ( tran.getStepsBeforeAdding() < 1 )
			{
				automaton.automaton.addTransition( stateID, tran ) ;	
				loopTransitionsToRemove.add( tran ) ;
			}
			else
			{
				tran.decrementStepsBeforeAdding() ;
			}
		}
		for ( FSATransition t : loopTransitionsToRemove )
			automaton.loopsToBeAdded.remove( t ) ;
		return automaton ;
	}
	
	private class FSAwithPartialLoops
	{
		public FSAwithPartialLoops( FSAutomaton automaton, ArrayList<FSATransition> loops )
		{
			this.automaton = automaton ;
			this.loopsToBeAdded = loops ;
		}
		private FSAutomaton automaton ;
		private ArrayList<FSATransition> loopsToBeAdded ;
	}

	public class Loop
	{
		private int traceID ;
		private int loopLength ;
		private int numOfLoops ;

		public void setTraceID( int n )
		{
			traceID = n ;
		}
		public  int getTraceID()
		{
			return traceID ; 
		}
		public void setLoopLength( int n )
		{
			loopLength = n ;
		}
		public int getLoopLength()
		{
			return loopLength ;
		}
		public void setNumOfLoops( int n )
		{
			numOfLoops = n ;
		}
		public int getNumOfLoops()
		{
			return numOfLoops ;
		}
	}
}
