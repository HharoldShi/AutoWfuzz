package AbstractionFuncs;

public class FTPoutputAbstraction implements AbstractionFunct {

	private static String skipString = "@#DF<<<SkipMe>>>^%#D" ;
	
	public String abstractMsg( String input )
	{
		if ( input.length() > 3 && input.charAt( 3 ) != ' ' )
			return skipString ;
		String firstChars = input.substring( 0, 3 ) ;
		return firstChars ;
	}
	
	public String getSkipString()
	{
		return skipString ;
	}
}
