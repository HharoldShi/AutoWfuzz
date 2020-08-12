package AbstractionFuncs;

/**
 * Abstraction function for the input requests to ftp server.
 * 
 * @author Serge Gorbunov
 *
 */
public class FTPinputAbstraction implements AbstractionFunct
{
	
	private static String skipString = "@#DF<<<SkipMe>>>^%#D" ;
	
	public String abstractMsg( String input )
	{
		input = input.toUpperCase() ;
		return input.substring( 0 , 4 ) ;
		
	}
	
	public String getSkipString()
	{
		return skipString ;
	}
}
