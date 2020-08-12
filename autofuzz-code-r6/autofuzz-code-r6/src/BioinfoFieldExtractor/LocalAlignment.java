package BioinfoFieldExtractor;

import java.util.ArrayList;

public class LocalAlignment 
{

	public float[][] alignLocally( ArrayList<String> sequences )
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
		return alignLocally( sequences, simMatrix ) ;
	}

	/**
	 * Performs Smith Waterman local alignment algorithm to calculate similarity
	 * between message sequences. Returns the distance matrix representing the
	 * "distance in similarity" between its members.
	 * 
	 * @param sequences An array list of message sequences
	 * @param similarityMtx similarity matrix
	 *  
	 * @return The distance matrix
	 */
	public float[][] alignLocally( ArrayList<String> sequences, float[][] similarityMtx )
	{
		ASCIIconverter converter = new ASCIIconverter() ;
		int numOfSeq = sequences.size() ;

		// Instantiate a matrix representing "distance" between messages
		int[][] similar = new int[numOfSeq][numOfSeq] ;
		for ( int i = 0; i < numOfSeq; i++ )
		{
			for ( int j = 0; j < numOfSeq; j++ )
				similar[i][j] = -1 ;
		}

		// For every two messages perform smith waterman algorithm to check their similarity
		for ( int i = 0; i < numOfSeq; i++ )
		{
			for ( int j = 0; j < numOfSeq; j++ )
			{
				if ( similar[i][j] >= 0 )
					continue ;

				String msg1 = sequences.get( i ) ;
				String msg2 = sequences.get( j ) ;

				// Find the maximum similarity between two sequences
				int simScore = performSmithWatermanAlignment( converter.convertToDecArray( msg1 ), converter.convertToDecArray( msg2 ) ) ;
				similar[i][j] = simScore ;
				similar[j][i] = simScore ; 
			}
		}		
		return calcDistance( similar, numOfSeq, numOfSeq ) ;
	}

	/**
	 * Smith-Waterman algorithm to perform local alignment of two messages. 
	 * Returns the similarity score
	 * 
	 * @param msg1 The first message in dec ascii representation
	 * @param msg2 The second message in dec ascii representation
	 * @param sMatrix The similarity matrix
	 * 
	 * @return The similarity score between two messages
	 */
	public int performSmithWatermanAlignment( int[] msg1, int[] msg2 )
	{
		int rows = msg1.length + 1 ;
		int cols = msg2.length + 1 ;

		int[][] scoreTable = new int[rows][cols] ;

		// TODO: CHECK Why is this so???? why not fill it with 0s. 
		for ( int i = 0; i < rows; i++ )
			scoreTable[i][0] = 0 - i ;

		for ( int i = 0; i < cols; i++ )
			scoreTable[0][i] = 0 - i ;

		// Score similarities
		int max = 0 ;
		for ( int i = 1; i < rows; i++ )
		{
			for ( int j = 1; j < cols; j++ )
			{
				int match = 0 ;
				if ( msg1[i-1] == msg2[j-1] ) 
					match = scoreTable[i-1][j-1] + 2 ;
				else 
					match = scoreTable[i-1][j-1] - 1 ;

				int ins = scoreTable[i-1][j] - 1 ;
				int del = scoreTable[i][j-1] - 1 ;

				int m = findMax( match, ins, del, 0 ) ;
				if ( m > max )
					max = m ;
				scoreTable[i][j] = m ;				
			}
		}
		return max ;
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
	 * Returns the distance between similar messages
	 * 
	 * @param simMatrix The similarity matrix
	 * 
	 * @return The distance matrix
	 */
	private float[][] calcDistance( int[][] simMatrix, int rows, int cols ) 
	{
		float[][] disMatrix = new float[rows][cols] ;
		
		for ( int i = 0; i < rows; i++ )
		{
			for ( int j = 0; j < cols; j++ )
				disMatrix[i][j] = -1 ;
		}
		
		for ( int i = 0; i < rows; i++ )
		{
			for ( int j = 0; j < cols; j++ )
			{
				if ( disMatrix[i][j] >= 0 )
					continue ; 
				
				disMatrix[i][j] = 1 - ( (float)simMatrix[i][j]/(float)simMatrix[i][i] ) ;
				disMatrix[j][i] = disMatrix[i][j] ;
			}
		}
		return disMatrix ;
	}
}