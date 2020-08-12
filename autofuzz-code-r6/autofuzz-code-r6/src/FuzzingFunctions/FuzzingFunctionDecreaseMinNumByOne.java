package FuzzingFunctions;

import java.util.ArrayList;
import java.util.Random;

import AutoFuzzMain.ConfigVariables;
import BioinfoFieldExtractor.ASCIIconverter;
import BioinfoFieldExtractor.GenericSequence;
import BioinfoFieldExtractor.GenericSequence.MessageBlock;

/**
 * This function finds the minimum number element in the sample data if the value is numeric and
 * decreases it by one
 * @author Serge Gorbunov
 *
 */
public class FuzzingFunctionDecreaseMinNumByOne implements FuzzingFunctionInterface {
	
	private ConfigVariables variables = new ConfigVariables() ;
	public ArrayList<Integer> fuzzInputMsg(GenericSequence genericMessage,
			ArrayList<Integer> originalMessage) {
		ArrayList<Integer> fuzzedMsg = new ArrayList<Integer>() ;
		ASCIIconverter converter = new ASCIIconverter() ;
		for ( MessageBlock block : genericMessage.getGenericSequence() ) 
		{
			if ( block.getType() == 0 )
				fuzzedMsg.addAll( block.getConstantFieldData() ) ;
			else if ( block.getType() == 1 )
			{
				ArrayList<Integer> sampleData = new ArrayList<Integer>() ;
				for ( ArrayList<Integer> sample : block.getSampleDataSet() )
				{
					String s = converter.convertArrListToString( sample ) ;
					try
					{
						sampleData.add( Integer.parseInt( s ) ) ;
					}
					catch ( NumberFormatException e )
					{}
				}
				// now we find the largest number in the set
				int minNum = Integer.MAX_VALUE ;
				for ( Integer i : sampleData )
				{
					if ( i < minNum )
						minNum = i ;
				}
				minNum-- ;
				fuzzedMsg.addAll( converter.convertToDecArrayList( String.valueOf( minNum ) ) ) ;
			}
			else if ( block.getType() == 2 )
			{
				ArrayList<Double> sampleData = new ArrayList<Double>() ;
				for ( ArrayList<Integer> sample : block.getSampleDataSet() )
				{
					String s = converter.convertArrListToString( sample ) ;
					try
					{
						sampleData.add( Double.parseDouble( s ) ) ;
					}
					catch ( NumberFormatException e )
					{}
				}
				// now we find the largest number in the set
				double minNum = Double.MAX_VALUE ;
				for ( Double i : sampleData )
				{
					if ( i < minNum )
						minNum = i ;
				}
				minNum-- ;
				fuzzedMsg.addAll( converter.convertToDecArrayList( String.valueOf( minNum ) ) ) ;
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
