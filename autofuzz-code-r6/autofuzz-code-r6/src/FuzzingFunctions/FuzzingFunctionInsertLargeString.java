package FuzzingFunctions;

import java.util.ArrayList;
import java.util.Random;

import BioinfoFieldExtractor.GenericSequence;
import BioinfoFieldExtractor.GenericSequence.MessageBlock;

/**
 * This function inserts a large string into the dynamic data components of the message
 * 
 * @author Serge Gorbunov
 *
 */
public class FuzzingFunctionInsertLargeString implements FuzzingFunctionInterface
{
	
	public ArrayList<Integer> fuzzInputMsg(GenericSequence genericMessage,
			ArrayList<Integer> originalMessage) {
		ArrayList<Integer> fuzzedMsg = new ArrayList<Integer>() ;
		
		for ( MessageBlock block : genericMessage.getGenericSequence() ) 
		{
			if ( block.getType() == 0 )
				fuzzedMsg.addAll( block.getConstantFieldData() ) ;
			else 
			{
				for ( int i = 0; i < 50000; i ++ )
					fuzzedMsg.add( 65 ) ;
			}
		}
		return fuzzedMsg ;
	}

}
