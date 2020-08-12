package BioinfoFieldExtractor;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This class provides functionality allowing to align multiple messages
 * using the global alignment techniques. 
 * 
 * @author Serge Gorbunov
 *
 */
public class MultiSequenceAligner {
	
	private GlobalAlignment globalAligner ;
	
	public MultiSequenceAligner()
	{
		globalAligner = new GlobalAlignment() ;
	}
	/**
	 * Assigns the aligned messages sequences to the tree
	 * 
	 * @param root Root of the phylogenetic tree
	 * 
	 */
	public PhylogenTree assignTreeAlignments( PhylogenTree root )
	{
		if ( root.getValue() == null )
		{
			if ( root.getLeftChild().getValue() == null )
				this.assignTreeAlignments( root.getLeftChild() ) ;
			if ( root.getRightChild().getValue() == null )
				this.assignTreeAlignments( root.getRightChild() ) ;
			
			ArrayList<Integer> seq1 = root.getLeftChild().getValue() ;
			ArrayList<Integer> seq2 = root.getRightChild().getValue() ;
			NeedlemanWunschHolder results = globalAligner.alignGlobally( seq1, seq2 ) ;
			
			// Sets the gap positions to the children. It will be used later
			// to recover the best alignment
			root.getLeftChild().setValue( seq1 ) ;
			root.getLeftChild().setGapsToBoInserted( results.getGapsArrInA1() ) ;
			root.getRightChild().setValue( seq2 ) ;
			root.getRightChild().setGapsToBoInserted( results.getGapsArrInA2() ) ;
			
			// Set the parent to the result of the alignment with the smallest number of gaps
			if ( root.getLeftChild().getGapsToBeInserted().size() < root.getRightChild().getGapsToBeInserted().size() )
				root.setValue( results.getAlignment1() ) ;
			else
				root.setValue( results.getAlignment2() ) ;
		}
		return root;
	}
	
	public ArrayList<ArrayList<Integer>> alignMultipleSequences( ArrayList<ArrayList<Integer>> list, ArrayList<ArrayList<Integer>> gaps, PhylogenTree root )
	{
		if ( root.getLeftChild() == null && root.getRightChild() == null )
		{
			ArrayList<Integer> seq = root.getValue() ;	
			seq = this.applyGaps( gaps,  seq ) ;
			list.add( seq ) ;
			//System.out.println( "SEQ: " + seq ) ;
			//System.out.println( "GAP: " + gaps ) ;
			return list ; 
		}
		else
		{
			ArrayList<Integer> g = root.getLeftChild().getGapsToBeInserted() ;
			gaps.add( g ) ;
			list = this.alignMultipleSequences(list, gaps, root.getLeftChild() ) ;
			gaps.remove( g ) ;
			
			g = root.getRightChild().getGapsToBeInserted() ;
			gaps.add( g ) ;
			list = this.alignMultipleSequences(	list, gaps, root.getRightChild() ) ;
			gaps.remove( g ) ;
			return list ;
		}
	}
	
	/**
	 * Add gaps in the positions specified by the gaps array into the message sequence.
	 * 
	 * @param gaps A list of positions where gaps should be inserted
	 * @param seq A string converted into a decimal representation
	 * 
	 * @return A new sequence with the gaps applied
	 */
	private ArrayList<Integer> applyGaps( ArrayList<ArrayList<Integer>> gaps, ArrayList<Integer> seq )
	{
		int gap = 256 ;
		
		for ( int i = gaps.size() -1 ; i >= 0; i--  )
		{
			ArrayList<Integer> gapSeq = gaps.get( i ) ;
			Collections.sort( gapSeq ) ;
			for ( Integer g : gapSeq )
			{
				seq.add( g, gap ) ;
			}
		}
		return seq ;
	}
	public static String convertArrListToString( ArrayList<Integer> arr )
	{
		String str = "" ;
		for ( int e = 0; e < arr.size(); e++ )
		{
			if ( arr.get( e ) != 256 )
				str = str + String.valueOf( Character.toChars( arr.get( e ) ) ) ;
			else
				str = str + "_" ; 
		}
		return str ;
	}
}
