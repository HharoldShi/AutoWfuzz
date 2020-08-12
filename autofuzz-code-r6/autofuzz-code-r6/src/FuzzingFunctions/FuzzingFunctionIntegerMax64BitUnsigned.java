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
public class FuzzingFunctionIntegerMax64BitUnsigned implements FuzzingFunctionInterface{
	private ConfigVariables variables = new ConfigVariables() ;

	public ArrayList<Integer> fuzzInputMsg(GenericSequence genericMessage,
			ArrayList<Integer> originalMessage) {
		ArrayList<Integer> fuzzedMsg = new ArrayList<Integer>() ;
		int[] maxValue = { 49, 56, 52, 52, 54, 55, 52, 52, 48, 55, 51, 55, 48, 57, 53, 53, 49, 54, 49, 53  } ;
		
		for ( MessageBlock block : genericMessage.getGenericSequence() ) 
		{
			if ( block.getType() == 0 )
				fuzzedMsg.addAll( block.getConstantFieldData() ) ;
			else if ( block.getType() == 1 )
			{
				for ( int v : maxValue )
				{
					fuzzedMsg.add( v ) ;
				}
			}
			else
			{
				Random generator = new Random() ;
				int randomIndex = generator.nextInt( block.getSampleDataSet().size() ) ;
				fuzzedMsg.addAll( block.getSampleDataSet().get( randomIndex ) ) ;
			}	
		}
		return fuzzedMsg ;
	}
	
}
