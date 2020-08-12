package FuzzingFunctions;

import java.util.ArrayList;
import java.util.Random;

import AutoFuzzMain.ConfigVariables;
import BioinfoFieldExtractor.GenericSequence;
import BioinfoFieldExtractor.GenericSequence.MessageBlock;

/**
 * This function tries to insert some random characters into the value fields. 
 * @author Serge Gorbunov
 *
 */
public class FuzzingFunctionMixDataTypes implements FuzzingFunctionInterface{
	private ConfigVariables variables = new ConfigVariables() ;

	public ArrayList<Integer> fuzzInputMsg(GenericSequence genericMessage,
			ArrayList<Integer> originalMessage) {
		ArrayList<Integer> fuzzedMsg = new ArrayList<Integer>() ;
		
		for ( MessageBlock block : genericMessage.getGenericSequence() ) 
		{
			if ( block.getType() == 0 )
				fuzzedMsg.addAll( block.getConstantFieldData() ) ;
			// Insert some junk in any of the data values
			else
			{
				Random generator = new Random() ;
				int maxSize = 0 ;
				for ( ArrayList<Integer> sample : block.getSampleDataSet() )
				{
					if ( sample.size() > maxSize )
						maxSize = sample.size() ;
				}
				for ( int i = 0; i < maxSize*100; i++ )
				{
					fuzzedMsg.add( generator.nextInt( 128 ) ) ;
				}
			}	
		}
		return fuzzedMsg ;
	}
}
