package FuzzingFunctions;

import java.util.ArrayList;
import java.util.Random;

import AutoFuzzMain.ConfigVariables;
import BioinfoFieldExtractor.GenericSequence;
import BioinfoFieldExtractor.GenericSequence.MessageBlock;

/**
 * @author Serge Gorbunov
 *
 */
public class FuzzingFunctionInsertRandomSample implements FuzzingFunctionInterface{

	// We will perform the fuzzing function 3 times on each transition. 
	private static int counter = 10 ;
	private ConfigVariables variables = new ConfigVariables() ;

	public ArrayList<Integer> fuzzInputMsg(GenericSequence genericMessage,	ArrayList<Integer> originalMessage) 
	{
		if ( counter <= 0 )
			return null ;
		ArrayList<Integer> fuzzedMsg = new ArrayList<Integer>() ;
		for ( MessageBlock block : genericMessage.getGenericSequence() ) 
		{
			if ( block.getType() == 0 )
				fuzzedMsg.addAll( block.getConstantFieldData() ) ;
			else
			{
				Random generator = new Random() ;
				int randomIndex = generator.nextInt( block.getSampleDataSet().size() ) ;
				fuzzedMsg.addAll( block.getSampleDataSet().get( randomIndex ) ) ;
			}
		}
		counter -- ;
		return fuzzedMsg ;
	}
	
}
