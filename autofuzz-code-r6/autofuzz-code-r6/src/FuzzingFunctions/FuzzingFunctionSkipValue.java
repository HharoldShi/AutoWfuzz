package FuzzingFunctions;

import java.util.ArrayList;
import java.util.Random;

import AutoFuzzMain.ConfigVariables;
import BioinfoFieldExtractor.GenericSequence;
import BioinfoFieldExtractor.GenericSequence.MessageBlock;

/**
 * The function chooses randomly whether or not to add a particular message block in the message
 * @author Serge Gorbunov
 *
 */
public class FuzzingFunctionSkipValue implements FuzzingFunctionInterface{
	private ConfigVariables variables = new ConfigVariables() ;

	public ArrayList<Integer> fuzzInputMsg(GenericSequence genericMessage,
			ArrayList<Integer> originalMessage) {
		ArrayList<Integer> fuzzedMsg = new ArrayList<Integer>() ;

		for ( MessageBlock block : genericMessage.getGenericSequence() ) 
		{
			// Choose a number randomly between 0 and 1, if 0 = do not include the current block. otherwise include it
			Random generator = new Random() ;
			int random = generator.nextInt( 2 ) ;
			if ( random == 1 )
			{
				if ( block.getType() == 0 )
					fuzzedMsg.addAll( block.getConstantFieldData() ) ;
				else 
				{
					int randomIndex = generator.nextInt( block.getSampleDataSet().size() ) ;
					fuzzedMsg.addAll( block.getSampleDataSet().get( randomIndex ) ) ;
				}
			}	
		}
		return fuzzedMsg ;
	}
}
