package BioinfoFieldExtractor;

import java.util.ArrayList;

public class NeedlemanWunschHolder {

	private ArrayList<Integer> alignment1 ;
	private ArrayList<Integer> alignment2 ;
	private ArrayList<Integer> gapsInA1 ;
	private ArrayList<Integer> gapsInA2 ;
	private int totalGapsInAlignment1 ;
	private int totalGapsInAlignment2 ;
	private int totalGaps ;
	private int simScore ;
	
	public void setAlignment1( ArrayList<Integer> a1 )
	{
		alignment1 = a1 ; 
	}
	public void setAlignment2( ArrayList<Integer> a2 )
	{
		alignment2 = a2 ; 
	}
	public void setTotalGaps( int g )
	{
		totalGaps = g ;
	}
	public void setSimilatiryScore( int s )
	{
		simScore = s ;
	}
	public void setGapsInAlignment1( int g )
	{
		totalGapsInAlignment1 = g ;
	}
	public void setGapsInAlignment2( int g )
	{
		totalGapsInAlignment2 = g ;
	}
	
	public ArrayList<Integer> getAlignment1()
	{
		return alignment1 ; 
	}
	public ArrayList<Integer> getAlignment2()
	{
		return alignment2 ; 
	}
	public int getTotalGaps()
	{
		return totalGaps ;
	}
	public int getSimilatiryScore()
	{
		return simScore ;
	}
	public int getGapsInAlignment1()
	{
		return totalGapsInAlignment1 ;
	}
	public int getGapsInAlignment2()
	{
		return totalGapsInAlignment2 ;
	}
	
	public ArrayList<Integer> getGapsArrInA1()
	{
		return gapsInA1 ;
	}
	public ArrayList<Integer> getGapsArrInA2()
	{
		return gapsInA2 ;
	}
	public void setGapsArrInA1( ArrayList<Integer> g )
	{
		gapsInA1 = g ;
	}
	public void setGapsArrInA2( ArrayList<Integer> g )
	{
		gapsInA2 = g ;
	}
}
