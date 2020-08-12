package BioinfoFieldExtractor;

import java.util.ArrayList;

/**
 * This class provides methods allowing to convert strings to decimal
 * representation and vice-versa.
 * 
 * @author Serge Gorbunov
 *
 */
public class ASCIIconverter {

	/**
	 * Converts string to an array of dec representations of the chars
	 */
	public int[] convertToDecArray( String msg )
	{
		int[] arr = new int[ msg.length() ] ;
		for ( int i = 0; i < msg.length(); i ++ )
		{
			arr[i] = msg.charAt(i) ;
		}
		return arr ;
	}
	public ArrayList<Integer> convertToDecArrayList( String msg )
	{
		ArrayList<Integer> arr = new ArrayList<Integer>() ;
		for ( int i = 0; i < msg.length(); i ++ )
		{
			arr.add( i, (int)msg.charAt(i) );
		}
		return arr ;
	}
	
	/**
	 * Converts a string to its ascii dec representation.
	 */
	public  String convertArrListToString( ArrayList<Integer> arr )
	{
		String str = "" ;
		if ( arr == null )
			return str ;
		for ( int e = 0; e < arr.size(); e++ )
		{
			if ( arr.get( e ) != 256 )
				str = str + String.valueOf( Character.toChars( arr.get( e ) ) ) ;
			else
				str = str + "_" ; 
		}
		return str ;
	}
	
	/**
	 * Converts an array list of strings into a double array of integers where a row is a message id, and columns is the message 
	 * itself.
	 */
	public ArrayList<ArrayList<Integer>> convertArrayListOfStringsToInts( ArrayList<String> originalList )
	{
		ArrayList<ArrayList<Integer>> newList = new ArrayList<ArrayList<Integer>>() ;
		for ( String s : originalList )
		{
			newList.add( this.convertToDecArrayList( s ) ) ;
		}
		return newList ;
	}
	
}
