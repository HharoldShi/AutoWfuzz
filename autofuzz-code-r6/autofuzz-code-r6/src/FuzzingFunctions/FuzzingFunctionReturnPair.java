package FuzzingFunctions;

import java.util.ArrayList;

/**
 * The instance of this class is used with the FuzzingFunctionInteface2 as the return value.
 * It contains the modified message and the new state ID after the modifications
 * 
 * @author Serge Gorbunov
 *
 */
public class FuzzingFunctionReturnPair {
	private ArrayList<Integer> modifiedMessage ;
	private int newStateID ;

	public  FuzzingFunctionReturnPair( ArrayList<Integer> msg, int state )
	{
		modifiedMessage = msg ;
		newStateID = state ;
	}
	
	public ArrayList<Integer> getModifiedMessage()
	{
		return modifiedMessage ;
	}
	
	public int getNewStateID()
	{
		return newStateID ;
	}
}
