package BioinfoFieldExtractor;

import java.util.ArrayList;

/**
 * This is the generic class for storing a consensus sequence.
 * A sequence is stored as a double array list where an index of a row is the position
 * of character in a sequence. The columns of a row are all the possible characters
 * that are met at that position retrieved from multiple-aligned sequences.
 * If only one column corresponds to a row, meaning that the symbol is unique and
 * identical at that position across all generated sequences.
 * If more than one column assigned to a row, it then contains all possible
 * characters met at that position across the generated sequences.
 *
 * @author Serge Gorbunov
 *
 */
public class ConsensusSeq {
	private ArrayList<ArrayList<Integer>> sequence = new ArrayList<ArrayList<Integer>> ();
	
	/**
	 * Populates the consensus sequence from the multiple aligned sequences.
	 * 
	 * @param seqs An array list of aligned sequences
	 */
	public boolean generateSeq( ArrayList<ArrayList<Integer>> seqs )
	{
		if ( this.verifyLengths( seqs ) == false )
			return false ;
		
		// Initialize the sequence
		// At this point we know that there is at least one sequence, since
		// we just verified it. 
		
		for ( int i = 0; i < seqs.get(0).size() ; i++ )
		{
			ArrayList<Integer> chars = new ArrayList<Integer> () ;
			chars.add( seqs.get( 0 ).get( i ) ) ;
			sequence.add( chars ) ;
		}
		
		
		for( int j = 1; j < seqs.size(); j++ )
		{
			ArrayList<Integer> seq = seqs.get( j ) ;
			for ( int i = 0; i < seq.size(); i++ )
			{
				if ( ! sequence.get( i ).contains( seq.get( i ) ) )
					sequence.get( i ).add( seq.get( i ) ) ;
			}
		}
		return true ;
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
	 * Returns a consensus sequence with ? inserted whenever there is a mutation
	 * at a certain position in the consensus sequence
	 */
	public String getConsSeq()
	{
		String str = "" ;
		for ( int e = 0; e < sequence.size(); e++ )
		{
			if ( sequence.get( e ).size() > 1 )
				str = str + "?" ;
			else
				str = str + String.valueOf( Character.toChars( sequence.get( e ).get( 0 ) ) ) ; 
		}
		return str ;
	}
	
	/**
	 * @return The consensus sequence as an array list of integers, where each integer
	 * is the decimal representation of a letter with 256 indicating a gap
	 */
	public ArrayList<Integer> getConsSeqAsList()
	{
		ArrayList<Integer> seq = new ArrayList<Integer> () ;
		for ( int e = 0; e < sequence.size(); e++ )
		{
			if ( sequence.get( e ).size() > 1 )
				seq.add( e, 256 ) ;
			else
				seq.add( e, sequence.get(e).get( 0 ) ) ; 
		}
		return seq ;
	}
	
	public ArrayList<ArrayList<Integer>> getConsensusSeq()
	{
		return sequence ;
	}
	
	public void setConsensusSeq( ArrayList<ArrayList<Integer>> seq )
	{
		sequence = seq ;
	}
}
