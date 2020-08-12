package BioinfoFieldExtractor;

import java.util.ArrayList;

import BioinfoFieldExtractor.GenericSequence.MessageBlock;

/**
 * This class provides functionality to extract primitive type 
 * from a sample of data values. It takes a consensusSeq object as a parameter
 * and returns a generic seq object.
 * 
 *
 * The following are type identified:
 * 0 - constant data field 
 * ( The rest are data values )
 * 1 - long
 * 2 - double
 * 3 - alpha-numeric string 
 * 4 - string with any chars including special chars
 * 
 * @author Serge Gorbunov
 */

public class GenericSeqConstructor {

	ASCIIconverter converter = new ASCIIconverter() ; 
	/**
	 * This function constructs a generic message from a consensus message
	 * 
	 * @param alignments An array list of similar messages aligned using global alignment algorithms
	 * 
	 * @return A generic message object corresponding to the consensus sequence
	 */
	public GenericSequence constructGenericSeq( ArrayList<ArrayList<Integer>> alignments )
	{
		GenericSequence genSeq = new GenericSequence() ;
		ArrayList<ArrayList<Integer>> sequence = new ArrayList<ArrayList<Integer>> ();

		
		if ( this.verifyLengths( alignments ) == false )
			return null ;
		
		// Initialize the sequence
		// At this point we know that there is at least one sequence, since
		// we just verified it. 
		
		for ( int i = 0; i < alignments.get(0).size() ; i++ )
		{
			ArrayList<Integer> chars = new ArrayList<Integer> () ;
			chars.add( alignments.get( 0 ).get( i ) ) ;
			sequence.add( chars ) ;
		}
		
		for( int j = 1; j < alignments.size(); j++ )
		{
			ArrayList<Integer> seq = alignments.get( j ) ;
			for ( int i = 0; i < seq.size(); i++ )
			{
				if ( ! sequence.get( i ).contains( seq.get( i ) ) )
					sequence.get( i ).add( seq.get( i ) ) ;
			}
		}
	
		// We now find the static and dynamic partitions and record it into the generic sequence
	
		ArrayList<Integer> constantDataField = new ArrayList<Integer>() ;
		int dataValueStart = -1 ;
		int dataValueEnd = -1 ;
		
		for ( int e = 0; e < sequence.size(); e++ )
		{
			// Check whether we are currently recording a value
			if ( dataValueStart == -1 )
			{
				// the position is a value
				if ( sequence.get( e ).size() <= 1 )
					constantDataField.add( sequence.get( e ).get( 0 ) ) ;
				else
				{
					// check if there is a constant data filed, we add it as a message block
					if ( ! ( constantDataField.size() == 0 ) )
					{
						genSeq.addConstantDataMsgBlock( constantDataField ) ;
						constantDataField = new ArrayList<Integer>() ;
					}
					dataValueStart = e ;
				}
			}
			else
			{
				if ( sequence.get( e ).size() > 1 )
					continue ;
				else if ( ( e == sequence.size() - 1 ) || ( sequence.get( e + 1 ).size() > 1 ) ) 
					continue ;
				
				dataValueEnd = e - 1 ;
				ArrayList<ArrayList<Integer>> subList = new ArrayList<ArrayList<Integer>>() ;
				for ( int i = dataValueStart; i <= dataValueEnd ; i++ )
				{
					ArrayList<Integer> chars = new ArrayList<Integer>() ;
					for ( int j = 0; j < alignments.size(); j++ )
						chars.add( alignments.get( j ).get( i ) ) ;
					subList.add( chars ) ;
				}
					
				this.addDataValueBlock( subList, genSeq ) ;
				constantDataField.add( sequence.get( e ).get( 0 ) ) ;
				dataValueStart = -1 ;
				dataValueEnd = -1 ;
			}
		}		
		
		if ( ! ( constantDataField.size() == 0 ) )
			genSeq.addConstantDataMsgBlock( constantDataField ) ;
		
		if ( dataValueStart != -1 )
		{
			dataValueEnd = sequence.size() - 1 ;
			ArrayList<ArrayList<Integer>> subList = new ArrayList<ArrayList<Integer>>() ;
			for ( int i = dataValueStart; i <= dataValueEnd ; i++ )
			{
				ArrayList<Integer> chars = new ArrayList<Integer>() ;
				for ( int j = 0; j < alignments.size(); j++ )
					chars.add( alignments.get( j ).get( i ) ) ;
				subList.add( chars ) ;
			}
			this.addDataValueBlock( subList, genSeq ) ;
		}
		
		return genSeq ;
	}


/**
 * Verifies that all sequences are the same length
 */
public boolean verifyLengths( ArrayList<ArrayList<Integer>> seqs )
{
	int len = -1 ;
	if ( seqs.size() >= 1 )
		len = seqs.get( 0 ).size() ;
	else
		return false ;
	
	for ( int i = 1; i < seqs.size(); i++ )
	{
		if ( len != seqs.get( i ).size() )
			return false ;
	}
	return true ;
}
	
	/**
	 * This functions takes an array of data values, find its types and returns a message block
	 * with the type associated with it along with sample data extracted.
	 * 
	 * @param dataValues is a doule array where a row corresponds to a position
	 * of a value, and columns correspond to sample data found at those positions
	 * after sequence alignment
	 */
	private void addDataValueBlock( ArrayList<ArrayList<Integer>> dataValues, GenericSequence genSeq )
	{
		int length = dataValues.size() ;
		if ( length == 0 )
			return ;
		int type = 1 ;
		ArrayList<ArrayList<Integer>> sampleData = new ArrayList<ArrayList<Integer>>() ;
		for ( int i = 0; i < dataValues.get( 0 ).size(); i++ )
		{
			ArrayList<Integer> sample = new ArrayList<Integer>() ;
			for ( int j = 0; j < dataValues.size(); j++ )
			{
				if ( dataValues.get( j ).size() > i && dataValues.get( j ).get( i ) != 256 )
					sample.add( dataValues.get( j ).get( i ) ) ;
			}
			if ( sample.size() > 0 )
				sampleData.add( sample ) ;
 		}

		if ( this.isSampleDataLongType( sampleData ) )
			type = 1 ;
		else if ( this.isSampleDataDoubleType( sampleData ) )
			type = 2 ;
		else if ( this.isSampleDataAlphaNumber( sampleData ) )
			type = 3 ;
		else type = 4 ;
		
		genSeq.addValueDataMsgBlock( sampleData, type, length ) ;	
	}
	
	/**
	 * Confirms that every element in the arraylist of sample data
	 * is an alpha-numeric string
	 * 
	 * @return True if all strings in the sample data are alpha numeric,
	 * false otherwise.
	 */
	private boolean isSampleDataAlphaNumber( ArrayList<ArrayList<Integer>> sampleData )
	{
		for ( ArrayList<Integer> value : sampleData )
		{
			if ( value.size() < 1 )
				continue ;
			for ( int ch : value )
			{
				if ( ! this.isAlphaNumeric( ch ) )
					return false ;
			}
		}
		return true ;
	}
	/**
	 * Confirms that every element in the arraylist of sample data
	 * is a long data type. 
	 * @return True if every element in the sample data list is a long, false otherwise
	 */
	private boolean isSampleDataLongType( ArrayList<ArrayList<Integer>> sampleData )
	{
		for ( ArrayList<Integer> value : sampleData )
		{
			if ( value.size() < 1 )
				continue ;
			for ( int j : value )
			{
				if ( ! this.isNumeric( j ) ) 
					return false ;
			}
		}
		return true ;
	}
	
	/**
	 * Confirms that every element ins the arraylist of the sample data is a double data type. 
	 * 
	 * @return True if every element in the list can be parsed as a double type, false otherwise
	 */
	private boolean isSampleDataDoubleType( ArrayList<ArrayList<Integer>> sampleData )
	{
		for ( ArrayList<Integer> value : sampleData )
		{
			if ( value.size() < 1 )
				continue ;
			String s = converter.convertArrListToString( value ) ;
			try
			{
				Double.parseDouble( s ) ;
			}
			catch ( NumberFormatException e )
			{
				return false ;
			}
		}
		return true ;
	}	
	/**
	 * @return True if the char represented in its decimal representation is numeric (0-9), otherwise false
	 */
	private boolean isNumeric( int c )
	{
		if ( ( c >= 48 && c <= 57 ) || c == 10 )
			return true ;
		else
			return false ;
	}
	/**
	 * @return True if the char is alpha-numeric, false otherwise
	 */
	private boolean isAlphaNumeric( int c )
	{
		if ( ( c >= 48 && c <= 57 ) || ( c >= 65 && c <= 90 ) || ( c >= 97 && c <= 122 ) )
			return true ;
		else 
			return false ;
	}
}
