package FuzzingFunctions;

import java.util.ArrayList;

import BioinfoFieldExtractor.GenericSequence;
import ProtocolLearner.FSATransition;
import ProtocolLearner.FSAutomaton;

/**
 * This interface is used to perform a specific fuzzing function on a message.
 * This is the new interface, providing the functions with the access to the
 * protocol's finite state automaton, the current state and the input message.
 * The return value is the pair of the modified input message with the
 * new state, or -1 if no state changes required. The GMS is also 
 * specified, even though it can be extracted from the FSA based on the current state. 
 * 
 * @author Serge Gorbunov
 *
 */
public interface FuzzingFunctionInterface2 {

	/**
	 * Modifies the input messages
	 * @param automaton The protocol's FSA
	 * @param currentStateID The current state in the FSA
	 * @param    
	 * @param originalMessage Original Input Message
	 * 
	 * @return Instance of the fuzzingFunctionReturnPair with the modified values and 
	 * the new state ID values set. 
	 */
	public FuzzingFunctionReturnPair fuzzInputMsg( FSAutomaton automaton, int currentStateID, ArrayList<Integer> originalMessage, GenericSequence gms ) ;
	
}
