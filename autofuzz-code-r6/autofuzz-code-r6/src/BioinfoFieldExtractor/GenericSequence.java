package BioinfoFieldExtractor;

import java.util.ArrayList;

/**
 * This class is a replacement of ConsensusSeq class.
 * 
 * This class is used to store information of a generic sequence
 * resulted from multiple sequence alignment. 
 * A generic message is a sequence of message blocks.
 * Each message block is either a static data filed, or 
 * a data value. We identify the following primitive data values:
 * booleanInt (ie 0 or 1), short, int, long, double, char, string
 * If the message block is a value block then it also contains
 * a list of sample data extracted from the messages. These data
 * can be used in fuzzing. 
 * 
 * 
 * @author Serge Gorbunov
 * 
 *
 * */
public class GenericSequence 
{
	ArrayList<MessageBlock> blocks = null ;
	private int totalNumOfSamples = -1 ;
	public GenericSequence()
	{
		blocks = new ArrayList<MessageBlock>() ;
	}

	public void addMessageBlock( MessageBlock b )
	{
		blocks.add( b ) ;
	}
	public ArrayList<MessageBlock> getGenericSequence()
	{
		return blocks ;
	}
	public void addConstantDataMsgBlock( ArrayList<Integer> data )
	{
		MessageBlock b = new MessageBlock() ;
		b.setType( 0 ) ;
		b.setConstantFieldData( data ) ;
		blocks.add( b ) ;
	}
	public MessageBlock addValueDataMsgBlock( ArrayList<ArrayList<Integer>> sampleData, int type, int length )
	{
		MessageBlock b = new MessageBlock() ;
		b.setType( type ) ;
		b.setSampleDataSet( sampleData ) ;
		b.setLength( length) ;
		blocks.add( b ) ;
		totalNumOfSamples = sampleData.size() ;
		return b ;
	}
	public int getTotalNumOfSamples()
	{
		return totalNumOfSamples ;
	}

	public class MessageBlock
	{
		/*
		 * The following are type identified:
		 * 0 - constant data field 
		 * ( The rest are data values )
		 * 1 - long
		 * 2 - double
		 * 3 - alpha-numeric string 
		 * 4 - string with any chars including special chars
		 */
		private int type = -1 ;
		private int length = - 1 ;

		// if the type of the message is 0 then sampleDataSet contains only
		// one element which is the data of the field itself. Otherwise, 
		// its a value field so we collect the sample values for the fuzzer use.
		private ArrayList<ArrayList<Integer>> sampleDataSet = new ArrayList<ArrayList<Integer>>() ;

		public void setType( int t ) 
		{
			type = t ;
		}
		public int getType()
		{
			return type ;
		}
		public void setLength( int t ) 
		{
			length = t ;
		}
		public int getLength() 
		{
			return length ;
		}
		public void setConstantFieldData( ArrayList<Integer> d ) 
		{
			sampleDataSet.clear() ;
			sampleDataSet.add( d ) ;
			length = d.size() ;
		}
		public ArrayList<Integer> getConstantFieldData()
		{
			if ( sampleDataSet.size() > 0 )
				return sampleDataSet.get( 0 ) ;
			else
				return null ;
		}
		public ArrayList<ArrayList<Integer>> getSampleDataSet()
		{
			return sampleDataSet ;
		}
		public void setSampleDataSet( ArrayList<ArrayList<Integer>> sampleData ) 
		{
			sampleDataSet = sampleData ;
		}
		
	}
	
	public String toString()
	{
		String s = "" ;
		ASCIIconverter converter = new ASCIIconverter() ;
		for ( MessageBlock block : blocks )
		{
			if ( block.type == 0 )
				s = s + converter.convertArrListToString( block.getSampleDataSet().get( 0 ) ) ;
			else if ( block.type == 1 )
			{
				for ( int i = 0; i < block.length; i++ )
					s = s + "L" ;
			}
			else if ( block.type == 2 )
			{
				for ( int i = 0; i < block.length; i++ )
					s = s + "D" ;
			}
			else if ( block.type == 3 )
			{
				for ( int i = 0; i < block.length; i++ )
					s = s + "S" ;
			}
			else 
				for ( int i = 0; i < block.length; i++ )
					s = s + "A" ;
		}
		return s ;
	}
}
