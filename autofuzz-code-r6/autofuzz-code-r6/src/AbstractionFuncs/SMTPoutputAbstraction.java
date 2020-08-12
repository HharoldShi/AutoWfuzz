package AbstractionFuncs;


/**
 * An abstraction function for the output messages of the SMTP protocol
 * @author Serge Gorbunov
 *
 */
public class SMTPoutputAbstraction implements AbstractionFunct {
	
	// This is a pseudo random skip me string. That is when an abstraction
	// function returns this string from a msg, this message will not be abstracted and will be skipped
	// during FSM construction and fuzzing.
	private static String skipString = "@#DF<<<SkipMe>>>^%#D" ;
	
	public String abstractMsg( String input )
	{
		// Check the first 4 chars are not separated from the message
		// then it is probably not the message type so we return empty string
		//if ( input.length() > 4 && input.charAt( 4 ) != ' ' )
		//	return skipString ;
		
		String firstChars = input.substring( 0, 4 ) ;
		firstChars = firstChars.toLowerCase() ;
		for ( int i = 0; i < firstChars.length(); i++ )
		{
			if ( firstChars.charAt( i ) < 97 || firstChars.charAt( i ) > 122 )
				return skipString ;
		}
		// Also confirm that all characters are upper cased
		/*
		for ( int i = 0; i < firstChars.length(); i ++ )
		{
			char c = firstChars.charAt( i ) ;
			if ( ! Character.isUpperCase( c ) )
				return skipString ;
		}*/
		
		return firstChars ;
	}
	
	public String getSkipString( )
	{
		return skipString ;
	}
}
