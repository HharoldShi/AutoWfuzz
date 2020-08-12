package AbstractionFuncs;


/**
 * Abstraction function for the input messages of the MSN protocol
 * 
 * @author Serge Gorbunov
 *
 */
public class MSNinputAbstraction implements AbstractionFunct{
	
	// This is a pseudo random skip me string. That is when an abstraction
	// function returns this string from a msg, this message will not be abstracted and will be skipped
	// during FSM construction and fuzzing.
	private static String skipString = "@#DF<<<SkipMe>>>^%#D" ;
	
	public String abstractMsg( String input )
	{
		// Check the first 3 chars are not separated from the message
		// then it is probably not the message type so we return empty string
		if ( input.charAt( 3 ) != ' ' )
			return skipString ;
		String firstChars = input.substring( 0, 3 ) ;
		// Also confirm that all characters are upper cased
		for ( int i = 0; i < firstChars.length(); i ++ )
		{
			char c = firstChars.charAt( i ) ;
			if ( ! Character.isUpperCase( c ) )
				return skipString ;
		}
		return firstChars ;
	}
	
	public String getSkipString()
	{
		return skipString ;
	}
}
