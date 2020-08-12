package BioinfoFieldExtractor;

import java.util.ArrayList;

/**
 * This class provides functionality for global alignment for 2 messages sequences
 * based on the NeedlemanWunsch algorithm.
 * 
 * @author Serge Gorbunov
 */
public class GlobalAlignment {
	public NeedlemanWunschHolder alignGlobally( ArrayList<Integer> msg1, ArrayList<Integer> msg2 )
	{
		// Create a default similarity identity matrix
		float[][] simMatrix = new float[257][257] ;
		for ( int i = 0; i < 257; i++ )
		{
			for ( int j = 0; j < 257; j++ )
			{
				if ( i == j )
					simMatrix[i][j] = 1 ;
				else
					simMatrix[i][j] = 0 ;
			}
		}
		return alignGlobally( msg1, msg2, simMatrix ) ;
	}
	/**
	 * Converts the strings into a byte array and calls the main alignGlobaly method.
	 */
	public NeedlemanWunschHolder alignGlobally( ArrayList<Integer> msg1, ArrayList<Integer> msg2, float[][] similarityMatrix )
	{
		return alignGlobally( this.convertToArray( msg1 ), this.convertToArray( msg2 ), similarityMatrix ) ;
	}
	/**
	 * Performs NeedlemanWunsch global alignment algorithm to calculate similarity
	 * between message sequences. 
	 * TODO: implement the use of the similarity matrix 
	 * 
	 * @param msg1 The first message
	 * @param msg2 The second message
	 * @param similarityMtx similarity matrix
	 *  
	 * @return The needlemanWunch Holder object with the alignment info
	 */
	public NeedlemanWunschHolder alignGlobally( int[] msg1, int[] msg2, float[][] similarityMtx )
	{
		int rows = msg1.length + 1 ;
		int cols = msg2.length + 1 ;
		
		// We initially reset the GAP position indicator to 257, perform all operations
		// and then restore it back
		for ( int i = 0; i < msg1.length; i++ )
			if ( msg1[i] == 256 )
				msg1[i] = 257 ;
		
		for ( int i = 0; i < msg2.length; i++ )
			if ( msg2[i] == 256 )
				msg2[i] = 257 ;
		
		int[][] scoreTable = new int[rows][cols] ;
		// Fill out the table
		for ( int i = 0; i < rows; i++ )
			scoreTable[i][0] = 0 ;
		for ( int i = 0; i < cols; i++ )
			scoreTable[0][i] = 0 ;
	
		for ( int i = 1; i < rows; i++ )
		{
			for ( int j = 1; j < cols; j++ )
			{
				int max1 = scoreTable[i-1][j-1] + this.scoreChars(msg1[i-1], msg2[j-1]) ;
				int max2 = scoreTable[i-1][j] ;
				int max3 = scoreTable[i][j-1] ;				
				scoreTable[i][j] = this.findMax( max1, max2, max3, 0 ) ;	
			}
		}
		
		// Trace back the table and align the messages ;
		int m ;
		if ( msg1.length >= msg2.length )
			m = msg1.length ;
		else 
			m = msg2.length ;
		
		ArrayList<Integer> alignment1 = new ArrayList<Integer>() ;
		ArrayList<Integer> alignment2 = new ArrayList<Integer>() ;
		ArrayList<Integer> gapsA1 = new ArrayList<Integer>() ;
		ArrayList<Integer> gapsA2 = new ArrayList<Integer>() ;
		
		int i = rows - 1 ;
		int j = cols - 1 ;
		int gapsInA1 = 0 ;
		int gapsInA2 = 0 ;
		while ( i > 0 && j > 0 )
		{
			int score = scoreTable[i][j] ;
			int scoreDiag = scoreTable[i-1][j-1] ;
			int scoreUp = scoreTable[i-1][j] ;
			int scoreLeft = scoreTable[i][j-1] ;
			if ( score == scoreDiag + this.scoreChars( msg1[i-1], msg2[j-1] ) )
			{
				alignment1.add( 0, msg1[i-1] ) ;
				alignment2.add( 0, msg2[j-1] ) ;
				i -- ;
				j -- ;
			}
			else if ( score == scoreLeft )
			{
				alignment1.add( 0, 256 ) ;
				alignment2.add( 0, msg2[j-1] ) ;
				j -- ;
				gapsInA1 ++ ;
			}
			else
			{
				alignment1.add( 0, msg1[i-1] ) ;
				alignment2.add( 0, 256 ) ;
				i -- ;
				gapsInA2 ++ ;
			}
		}
		
		while ( i > 0 )
		{
			alignment1.add( 0, msg1[i-1] ) ;
			alignment2.add( 0, 256 ) ;
			i -- ;
			gapsInA2 ++ ;
		}
		while ( j > 0 )
		{
			alignment1.add( 0, 256 ) ;
			alignment2.add( 0, msg2[j-1] ) ;
			j -- ;
			gapsInA1 ++ ;
		}
		
		NeedlemanWunschHolder alignmentResult = new NeedlemanWunschHolder() ;
		
		
		alignmentResult.setSimilatiryScore( scoreTable[rows-1][cols-1] ) ;
		alignmentResult.setTotalGaps( gapsInA1 + gapsInA2 ) ;
		alignmentResult.setGapsInAlignment1( gapsInA1 ) ;
		alignmentResult.setGapsInAlignment2( gapsInA2 ) ;
		// Record the position of the gaps in Alignment 1
		for ( int g = 0; g < alignment1.size(); g++ )
			if ( alignment1.get( g ) == 256 )
				gapsA1.add( g ) ;
		
		// Record the position of the gaps in Alignment 2
		for ( int g = 0; g < alignment2.size(); g++ )
			if ( alignment2.get( g ) == 256 )
				gapsA2.add( g ) ;
		alignmentResult.setGapsArrInA1( gapsA1 ) ;
		alignmentResult.setGapsArrInA2( gapsA2 ) ;
		
		for ( int g = 0; g < alignment1.size(); g++ )
			if ( alignment1.get( g ) == 257 )
			{
				alignment1.remove( g ) ;
				alignment1.add( g, 256 ) ;
			}
		alignmentResult.setAlignment1( alignment1 ) ;
		for ( int g = 0; g < alignment2.size(); g++ )
			if ( alignment2.get( g ) == 257 )
			{
				alignment2.remove( g ) ;
				alignment2.add( g, 256 ) ;
			}
		alignmentResult.setAlignment2( alignment2 ) ;
		
		
		return alignmentResult ;
	}
	
	/**
	 * Converts string to an array of dec representations of the chars
	 */
	public int[] convertToDec( String msg )
	{
		int[] arr = new int[ msg.length() ] ;
		for ( int i = 0; i < msg.length(); i ++ )
		{
			arr[i] = msg.charAt(i) ;
		}
		return arr ;
	}
	
	/**
	 * Finds a maximum number amongst 4 other numbers
	 * @param i1
	 * @param i2
	 * @param i3
	 * @param i4
	 * @return The maximum number
	 */
	private int findMax( int i1, int i2, int i3, int i4 ) {
		if ( i1 >= i2 && i1 >= i3 && i1 >= i4 )
			return i1 ;
		else if ( i2 >= i1 && i2 >= i3 && i2 >= i4 )
			return i2 ;
		else if ( i3 >= i1 && i3 >= i2 && i3 >= i4 )
			return i3 ;
		else
			return i4 ;
	}
	
	/**
	 * This function scores two characters based on their similarity
	 * TODO: This should use the similarity matrix at some point.
	 */
	private int scoreChars( int char1, int char2 )
	{
		if ( char1 == char2 )
			return 1 ;
		else 
			return 0 ;
	}
	
	/**
	 * Converts array list of chars into an array
	 */
	private int[] convertToArray( ArrayList<Integer> list )
	{
		int[] arr = new int[list.size()] ;
		for ( int i = 0; i < list.size(); i++ )
		{
			arr[i] = list.get( i ) ;
		}
		return arr ;
	}
}
