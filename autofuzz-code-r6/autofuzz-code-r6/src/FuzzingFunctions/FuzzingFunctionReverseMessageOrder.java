package FuzzingFunctions;

import java.util.ArrayList;
import java.util.Random;

import AutoFuzzMain.ConfigVariables;
import BioinfoFieldExtractor.GenericSequence;
import BioinfoFieldExtractor.GenericSequence.MessageBlock;

/**
 * The function simply reverses the order of the message blocks
 * @author Serge Gorbunov
 *
 */
public class FuzzingFunctionReverseMessageOrder implements FuzzingFunctionInterface {
	private ConfigVariables variables = new ConfigVariables() ;

	public ArrayList<Integer> fuzzInputMsg(GenericSequence genericMessage,
			ArrayList<Integer> originalMessage) {
		ArrayList<Integer> fuzzedMsg = new ArrayList<Integer>() ;
		
		for ( int i = genericMessage.getGenericSequence().size() - 1 ; i >= 0; i-- )
		{
			MessageBlock block = genericMessage.getGenericSequence().get( i ) ;
			if ( block.getType() == 0 )
				fuzzedMsg.addAll( block.getConstantFieldData() ) ;
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
