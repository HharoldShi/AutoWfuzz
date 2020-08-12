package ProtocolLearner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import ProtocolLearner.PassiveLearner.Loop;

/**
 * An abstract class for a message trace. Message trace is defined as a sequence
 * of input/output messages. 
 * 
 * @author Serge Gorbunov
 *
 */
public class MessageTrace implements Iterable<HashMap<String, ArrayList<String>>> ,Iterator<HashMap<String, ArrayList<String>>>
{
	private int index ;
	private int traceId ;
	private ArrayList<HashMap<String,ArrayList<String>>> trace ;
	// Loops of the trace, Key = Position after which input message loop starts, value = length of the loop 
	private ArrayList<Loop> loops ;
	/**
	 * Constructor.
	 * @param id Message trace identifier
	 */
	public MessageTrace( int id )
	{
		traceId = id ;
		trace = new ArrayList<HashMap<String, ArrayList<String>>>() ;
		loops = new ArrayList<Loop>() ;
		index = 0 ;
	}

	/**
	 * Returns the trace identifier
	 * 
	 * @return Trace identifier
	 */
	public int getTraceId()
	{
		return traceId ;
	}

	/**
	 * Adds a pair of input/output messages to the message trace
	 * @param input Input message 
	 * @param output Output message
	 * @return True if the IO pair is successfully added
	 */
	public boolean addIOPair( String input, ArrayList<String> output )
	{
		HashMap<String,ArrayList<String>> io = new HashMap<String, ArrayList<String>>() ;
		io.put(input, output) ;
		trace.add(io) ;
		return true ;
	}
	/**
	 * Returns the whole message trace
	 * @return The message trace
	 */
	public ArrayList<HashMap<String, ArrayList<String>>> getTrace()
	{
		return trace ;
	}

	/**
	 * Iterator method
	 */
	public Iterator<HashMap<String, ArrayList<String>>> iterator()
	{
		index = 0 ;
		return this ;
	}

	/** 
	 * Checks if there is a next IO pair in the trace
	 * 
	 * @return True if there is one, false otherwise
	 */
	public boolean hasNext()
	{
		if ( index < trace.size() )
			return true ;
		return false ;
	}

	/**
	 * Returns the next IO pair from the trace
	 * 
	 * @return IO pair 
	 */
	public HashMap<String, ArrayList<String>> next()
	{
		if ( index >= trace.size() )
			throw new NoSuchElementException() ;
		return trace.get( index++ ) ;
	}
	public void remove()
	{
		throw new UnsupportedOperationException() ;
	}

	/**
	 * Identify a new loop in the trace
	 */
	public void addLoop( Loop l ) 
	{
		loops.add( l ) ;
	}

	public ArrayList<Loop> getLoops()
	{
		return loops ;
	}

	/**
	 * Returns the number of IO pais in the trace
	 */
	public int getTraceSize()
	{
		return trace.size() ;
	}

	/**
	 * @return A IO message at the specified index
	 */
	public HashMap<String, ArrayList<String>> getMsg( int index )
	{
		if ( index >= 0 && index < trace.size() )
			return trace.get( index ) ;
		else
			return null ;
	}

	/**
	 * @return An arraylist of messsageIds that should be skipped while constructing a FSA
	 * due to those traces being a part of a loop
	 */
	public ArrayList<Integer> getSkipMsgs()
	{
		ArrayList<Integer> skip = new ArrayList<Integer>() ;
		for ( Loop loop : loops )
		{
			for ( int i = 1; i <= loop.getNumOfLoops(); i++ )
			{
				int startId = loop.getTraceID() + loop.getLoopLength()*i ;
				for ( int j = 0; j < loop.getLoopLength(); j++ )
				{
					if ( ! skip.contains( startId + j ) )
						skip.add( startId + j ) ;
				}
			}
		}
		return skip ;

	}
}
