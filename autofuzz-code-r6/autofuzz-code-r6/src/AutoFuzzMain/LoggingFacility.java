package AutoFuzzMain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.swing.JOptionPane;


/**
 * This class is used to control logging of AutoFuzz during fuzzing process.
 * All messages should go through functions provided by this class
 * 
 * @author Serge Gorbunov
 *
 */
public class LoggingFacility {

	/**
	 * Creates a new log file in <MainDrive>:\<ProgramFiles>\AutoFuzz\Logs\ to store the progress of the fuzzer
	 * 
	 * @return A file object if the file was successfully created, otherwise null 
	 */
	public File instantiateNewFuzzingLogFile()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		Date date = new Date();
		String fileName = "AutoFuzzLogFuzzing_" + dateFormat.format(date).toString() + ".txt" ; 
		try
		{
			Map<String, String> env = System.getenv();
			String programFiles = (String) env.get( "ProgramFiles(x86)" ) ;
			if ( programFiles == null )
				programFiles = (String) env.get( "ProgramFiles" ) ;
			if ( programFiles == null )
			{
				JOptionPane.showMessageDialog( null, "Unable to read the location of windows \"program files\" from env. variables." ) ;
				return null ;
			}

			File f = new File( programFiles + "\\AutoFuzz\\Logs\\" + fileName ) ;

			return f ;
		}
		catch ( Exception e )
		{
			System.out.println( e.getMessage() ) ;
			return null ;
		}
	}
	
	/**
	 * Creates a new system log file in <MainDrive>:\<ProgramFiles>\AutoFuzz\Logs\ 
	 * 
	 * @return A file object if the file was successfully created, otherwise null 
	 */
	public File instantiateNewSystemLogFile( String logName )
	{
		String fileName = "AutoFuzzLog_" + logName + ".txt" ; 
		try
		{
			Map<String, String> env = System.getenv();
			String programFiles = (String) env.get( "ProgramFiles(x86)" ) ;
			if ( programFiles == null )
				programFiles = (String) env.get( "ProgramFiles" ) ;
			if ( programFiles == null )
			{
				JOptionPane.showMessageDialog( null, "Unable to read the location of windows \"program files\" from env. variables." ) ;
				return null ;
			}

			File f = new File( programFiles + "\\AutoFuzz\\Logs\\" + fileName ) ;

			return f ;
		}
		catch ( Exception e )
		{
			System.out.println( e.getMessage() ) ;
			return null ;
		}
	}

	/**
	 * This function should be used to log all AutoFuzz messages 
	 * 
	 * @param f File name to which log message will be written
	 * @param severity Severity level of the error. The following levels are recognized: 0 - Info, 1 - Warning, 2 - Error, 3 - Critical
	 * @param msg The actual message to be written 
	 * 
	 * @return True if the message was successfully written, false otherwise
	 */
	public boolean writeLogMessage( File f, int severity, String msg )
	{
		if ( f == null )
			return false ;

		ConfigVariables variables = new ConfigVariables() ; 

		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(f , true) ) ; 

			String severityPrefix = "" ;
			if ( severity == variables.getLoggingLevelInfo() )
				severityPrefix = "INFO::" ;
			else if ( severity == variables.getLoggingLevelWarning() )
				severityPrefix = "WARN::" ;
			else if ( severity == variables.getLoggingLevelError() )
				severityPrefix = "ERR::" ;
			else if ( severity == variables.getLoggingLevelCritical() )
				severityPrefix = "CRIT::" ;
			else
				severityPrefix = "INFO::" ;

			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			String msgToWrite = severityPrefix + dateFormat.format(date).toString() + ":: " + msg + "\n" ;

			out.write( msgToWrite ) ; 
			out.close() ;

		}
		catch ( IOException e )
		{
			System.out.println( "Unable to record log msg into " + f.toString() ) ;
			System.out.println( "Severity: " + severity + "; Message: " + msg ) ;
			return false ;
		}
		return true ;

	}
	/*
	 * Used for testing
	 */
	/*
	public static void main( String[] args )
	{
		File f = instantiateNewLogFile() ;
		writeLogMessage( f , 0, "TESTING") ;
	}
	 */
}
