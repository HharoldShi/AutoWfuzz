package BioinfoFieldExtractor;

import java.util.ArrayList;

public class BioTester {
	public static void main(String[] args)
	{
		ArrayList<String> seq = new ArrayList<String>() ;
		/*
		seq.add( "GET /index.html HTTP/1.0 Host: www.yahoo.com User-Agent: Mozilla/5.0 Accept: text/xml") ;
		seq.add( "GET / HTTP/1.0 Host: www.google.com User-Agent: IE4.0 Accept: text/xml") ;
		seq.add( "GET /cgi-bin/whois.pl HTTP/1.0 Host: arin.net User-Agent: Opera Accept: text/xml") ;
		//seq.add("GET/4") ;
		 * 
		 */
		
		seq.add( "MAIL FROM:<test@test.com>") ;
		seq.add( "MAIL FROM:<anonymous@domain.ca>") ;
		seq.add( "MAIL FROM:<sample@pc.ru>") ;
		
		//seq.add( "GET /cgi-bin/im.pl HTTP/1.0 Host: someverylongDomainName.ru User-Agent: Opera3 Accept: text/xml") ;
		//seq.add( "GET /index.html HTTP/1.0 Host: www.youtube.com User-Agent: IE5.0 Accept: text/xml") ;

		
		LocalAlignment s = new LocalAlignment() ;
		//System.out.println( s.performSmithWatermanAlignment( s.convertToDec( "VER 1 MSNasdfP15 CVR4"),  s.convertToDec("VER 4 MSrP15 CV"))) ;
		GlobalAlignment g = new GlobalAlignment() ;
		MultiSequenceAligner a = new MultiSequenceAligner() ;
		//g.alignGlobally( "send", "ansendd") ;
		float[][] d =  s.alignLocally( seq ) ;
		UPGMA treeBuilder  = new UPGMA( d, seq ) ;
		PhylogenTree t = treeBuilder.buildTree() ;
		
		treeBuilder.clusterTrees( t ) ;
		ArrayList<PhylogenTree> trees = treeBuilder.getClusters() ;
		
		PhylogenTree initTree = trees.get( 1 ) ;
		
		PhylogenTree e = a.assignTreeAlignments( initTree ) ;
		//System.out.println( convertArrListToString( e.getValue() ) );
		
		ArrayList<ArrayList<Integer>> l = a.alignMultipleSequences(new ArrayList<ArrayList<Integer>>(), new ArrayList<ArrayList<Integer>>(), e) ;
		System.out.println( "Printing sequences" ) ;
		for ( ArrayList w : l )
		{
			System.out.println(convertArrListToString( w ) ) ;
		}
		ConsensusSeq ss = new ConsensusSeq() ;
		ss.generateSeq( l ) ;
		System.out.println( "The sequence is:" ) ;
		System.out.println( ss.getConsSeq() ) ;
		/*
		
		
		for ( PhylogenTree r : treeBuilder.getClusters() )
		{
			//System.out.println( "Printing a cluster: ") ;
			//printCluster( r ) ; 
			//System.out.println() ;
			PhylogenTree e = a.assignTreeAlignments( r ) ;
			//System.out.println( convertArrListToString( e.getValue() ) );
			
			ArrayList<ArrayList<Integer>> l = a.alignMultipleSequences(new ArrayList<ArrayList<Integer>>(), new ArrayList<ArrayList<Integer>>(), e) ;
			System.out.println( "Printing sequences" ) ;
			for ( ArrayList w : l )
			{
				System.out.println(convertArrListToString( w ) ) ;
			}
			
			
		//	System.out.println( convertArrListToString( a.assignTreeAlignments( r ).getValue() ) ) ;
		}
		*/		
		/*
		float[][] d = new float[5][5] ;
		d[0][0] = 0 ;
		d[1][0] = d[0][1] = 20 ;
		d[1][1] = 0 ;
		d[2][0] = d[0][2] = 60 ;
		d[2][1] = d[1][2] = 50 ;
		d[2][2] = 0 ;
		d[3][0] = d[0][3] = 100 ;
		d[3][1] = d[1][3] = 90 ;
		d[3][2] = d[2][3] = 40 ;
		d[3][3] = 0 ;
		d[4][0] = d[0][4] = 90 ;
		d[4][1] = d[1][4] = 80 ;
		d[4][2] = d[2][4] = 50 ;
		d[4][3] = d[3][4] = 30 ;
		d[4][4] = 0 ;
		
		UPGMA treeBuilder  = new UPGMA( d, seq ) ;
		PhylogenTree t = treeBuilder.buildTree() ;
		
		treeBuilder.clusterTrees( t ) ;
		treeBuilder.getClusters() ;
		for ( PhylogenTree r : treeBuilder.getClusters() )
		{
			System.out.println( "Printing a cluster: ") ;
			printCluster( r ) ; 
			System.out.println() ;
		}
		*/
	}
	public static void printCluster( PhylogenTree t )
	{
		if ( t.getIsLeaf() == true )
			System.out.print( t.getIndex() + " ") ;
		if ( t.getLeftChild() != null )
			printCluster( t.getLeftChild() ) ;
		if ( t.getRightChild() != null )
			printCluster( t.getRightChild()) ;
	}
	
	public static String convertArrListToString( ArrayList<Integer> arr )
	{
		String str = "" ;
		for ( int e = 0; e < arr.size(); e++ )
		{
			if ( arr.get( e ) != 256 )
				str = str + String.valueOf( Character.toChars( arr.get( e ) ) ) ;
			else
				str = str + "_" ; 
		}
		return str ;
	}
	
}
