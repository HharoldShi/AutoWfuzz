package AutoFuzzMain;

import java.io.File;

public class ConfigVariables {
	private static File currentSystemLogFile = null ;
	
	private static String systemConfigPath = null ;
	private static final int loggingLevelInfo = 0 ;
	private static final int loggingLevelWarning = 1 ;
	private static final int loggingLevelError = 2 ;
	private static final int loggingLevelCritical = 3 ;
	private static File currentFuzzingLogFile = null ;
	
	private static int trafficOrderIndex = 0 ;
	private static int currentRandomSessionsIndex = 0 ;
	
	public void setRandomSessionIndex( int i )
	{
		currentRandomSessionsIndex = i ;
	}
	public int getRandomSessionsIndex()
	{
		return currentRandomSessionsIndex ;
	}
	public void reverseTrafficOrder()
	{
		if ( trafficOrderIndex == 0 )
			trafficOrderIndex = 1 ;
		else
			trafficOrderIndex = 0 ;
	}
	public int getTrafficOrderIndex()
	{
		return trafficOrderIndex ;
	}
	public void setSystemConfigPath( String path )
	{
		systemConfigPath = path ;
	}
	public String getSystemConfigPath() 
	{
		return systemConfigPath ;
	}
	

	public int getLoggingLevelInfo()
	{
		return loggingLevelInfo ;
	}
	public int getLoggingLevelWarning()
	{
		return loggingLevelWarning ;
	}
	public int getLoggingLevelError()
	{
		return loggingLevelError ;
	}
	public int getLoggingLevelCritical()
	{
		return loggingLevelCritical ;
	}
	
	public void setNewFuzzingLogFile( File f )
	{
		currentFuzzingLogFile = f ;
	}
	public File getCurrentFuzzingLogFile()
	{
		return currentFuzzingLogFile ;
	}
	public void setNewSystemLogFile( File f )
	{
		currentSystemLogFile = f ;
	}
	public File getCurrentSystemLogFile()
	{
		return currentSystemLogFile ;
	}
}
