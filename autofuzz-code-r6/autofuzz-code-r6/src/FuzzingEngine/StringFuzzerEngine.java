package FuzzingEngine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JOptionPane;

import AbstractionFuncs.AbstractionFunct;
import AutoFuzzMain.ConfigVariables;
import AutoFuzzMain.LoggingFacility;
import BioinfoFieldExtractor.ASCIIconverter;
import BioinfoFieldExtractor.GenericSequence;
import FuzzingFunctions.FuzzingFunctionInterface;
import FuzzingFunctions.FuzzingFunctionInterface2;
import FuzzingFunctions.FuzzingFunctionReturnPair;
import ProtocolLearner.FSAutomaton;

public class StringFuzzerEngine {
	private ArrayList<String> fuzzingFuncs = null ;
	private int currentFuzzingIndex = -1 ;
	private static FuzzingFunctionInterface currentFuzzingSet = null ;
	private static FuzzingFunctionInterface2 currentFuzzingSet2 = null ;
	
	private static AbstractionFunct abstractionFunction = null ;
	private ASCIIconverter converter = null ;
	private ConfigVariables systemConfig = null ;
	
	private LoggingFacility loggingFacility = null ;
	private ConfigVariables variables = null ;
	
	public StringFuzzerEngine( )
	{
		systemConfig = new ConfigVariables() ;
		fuzzingFuncs = new ArrayList<String> () ;
		converter = new ASCIIconverter() ;
		loggingFacility = new LoggingFacility() ;
		variables = new ConfigVariables() ; 
		this.loadFuzzingFunction() ;
		currentFuzzingIndex = 0 ;
	}
	
	/** 
	 * Modifies the message based on the 
	 * currently active set of fuzzing functions.
	 * 
	 * @param genMsg Generic input message 
	 * @param origMsg The original input message
	 * @param automaton The protocol's FSA 
	 * @param currentStateID The current state of the fuzzing engine
	 * 
	 * @return A pair of a modified message with the new state in the FSA 
	 */
	public FuzzingFunctionReturnPair fuzzMessage( GenericSequence genMsg, ArrayList<Integer> origMsg, FSAutomaton automaton, int currentStateID )
	{
		if ( fuzzingFuncs == null || currentFuzzingIndex >= fuzzingFuncs.size() )
			return null ;

		// We then instantiate a static set of fuzzing functions.
		// When all functions in the set have been applied the set returns null
		// When all sets have been iterated over return null, that is no more
		// fuzzing functions can be applied at the current state.
		while ( true )
		{
			if ( currentFuzzingIndex >= fuzzingFuncs.size() )
				return null ;
			boolean instantiatedSetOne = false ;
			try {
				Class c = Class.forName( "FuzzingFunctions." + fuzzingFuncs.get(currentFuzzingIndex) );
				loggingFacility.writeLogMessage( variables.getCurrentFuzzingLogFile(), variables.getLoggingLevelInfo(), "Performing Fuzzing Func: " + fuzzingFuncs.get(currentFuzzingIndex) ) ;
			
				try { 
					currentFuzzingSet = (FuzzingFunctionInterface) c.newInstance() ;
					instantiatedSetOne = true ;
				}
				catch ( Exception e )
				{	
					instantiatedSetOne = false ;
				}
				if ( instantiatedSetOne == false )
				{
					currentFuzzingSet2 = (FuzzingFunctionInterface2) c.newInstance() ;
				}
			
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, "Could not find class " + fuzzingFuncs.get(currentFuzzingIndex)) ;
				currentFuzzingIndex ++ ;
				continue ;
			}

			
			FuzzingFunctionReturnPair returnPair = null  ;
			if ( instantiatedSetOne == true )
			{
				ArrayList<Integer> returnMsg = currentFuzzingSet.fuzzInputMsg( genMsg, origMsg ) ;
				returnPair = new FuzzingFunctionReturnPair( returnMsg, -1 ) ;
			}				
			else
			{
				returnPair = currentFuzzingSet2.fuzzInputMsg( automaton, currentStateID, origMsg, genMsg ) ;
			}
			if ( returnPair.getModifiedMessage() == null )
			{
				currentFuzzingIndex ++ ;
				continue ;
			}
			else
			{ 
				currentFuzzingIndex ++ ;
				return returnPair ;
			}				
		}
	}
	
	/**
	 * Loads the fuzzing functions
	 * 
	 * @return True if the fuzzingSources.txt file was successfully parsed and the fuzzing
	 * functions extracted. False otherwise
	 */
	public boolean loadFuzzingFunction( ) 
	{
		try { 
			BufferedReader in = new BufferedReader(new FileReader( systemConfig.getSystemConfigPath() + "//fuzzingSources.txt") ) ;
			String str; 
			while ((str = in.readLine()) != null) 
			{ 
				if ( str.startsWith("#"))
					continue ;
				else
				{
					String[] strs = str.split( " " ) ;
					if ( strs.length == 1 )
						fuzzingFuncs.add( strs[0] ) ;
					else
					{
						JOptionPane.showMessageDialog(null, "Error parsing fuzzingSources.txt. Please check the file syntax.") ;
						return false ;
					}
				}
			} 
			in.close(); 
		} catch (IOException ex) 
		{
			JOptionPane.showMessageDialog(null, "The source with abstraction functions was not found. Please check framework/fuzzingSources.txt" ) ;
			return false ;
		}
		return true ;
	}
	
	/**
	 * Returns the current index of the next fuzzing function
	 */
	public int getNextFuzzingFuncIndex()
	{
		return currentFuzzingIndex ;
	}
	/**
	 * Returns the total number of available fuzzing functions
	 */
	public int getTotalNumberOfFuzzingFuncs()
	{
		return fuzzingFuncs.size() ;
	}
}
