package FuzzingFunctions;

import java.util.ArrayList;
import java.util.Random;

import AutoFuzzMain.ConfigVariables;
import BioinfoFieldExtractor.GenericSequence;
import BioinfoFieldExtractor.GenericSequence.MessageBlock;

public class FuzzingFunctionIncreaseStringLength implements FuzzingFunctionInterface {
	private ConfigVariables variables = new ConfigVariables() ;

	public ArrayList<Integer> fuzzInputMsg(GenericSequence genericMessage,
			ArrayList<Integer> originalMessage) {
		ArrayList<Integer> fuzzedMsg = new ArrayList<Integer>() ;
		
		for ( MessageBlock block : genericMessage.getGenericSequence() ) 
		{
			Random generator = new Random() ;
			if ( block.getType() == 0 )
				fuzzedMsg.addAll( block.getConstantFieldData() ) ;
			else if ( block.getType() == 3 || block.getType() == 4 )
			{
				int random = generator.nextInt( 100 ) ;
				// We find the string with max length in the sample set
				ArrayList<Integer> max = new ArrayList<Integer>() ;
				for ( ArrayList<Integer> sample : block.getSampleDataSet() )
				{
					if ( sample.size() > max.size() )
						max = sample ;
				}
				
				for ( int i = 0 ; i <= random ; i++ )
					fuzzedMsg.addAll( max ) ;
			}
			else
			{
				int randomIndex = generator.nextInt( block.getSampleDataSet().size() ) ;
				fuzzedMsg.addAll( block.getSampleDataSet().get( randomIndex ) ) ;
			}	
		}
		return fuzzedMsg ;
	}

}
