package ProtocolLearner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import AbstractionFuncs.AbstractionFunct;
import AbstractionFuncs.MSNinputAbstraction;
import AbstractionFuncs.MSNoutputAbstraction;


public class TesterTemp {
	public static void main(String[] args)
	{
		AppDataParser parser = new AppDataParser() ;
		ArrayList<MessageTrace> traces = parser.getMsgTraces("<ApplicationData><Trace><Output>VER 1 MSNP15 CVR0</Output><Input> VER 1 MSNP15</Input><Output>CVR 2 0x0409 winnt 5.1 i386 MSNMSGR 8.5.1302 BC01 sgorbunov@hotmail.com</Output><Input> CVR 2 14.0.8089 14.0.8089 14.0.8089 http://msgruser.dlservice.microsoft.com/download/7/5/8/758BFCC9-1744-48F7-8162-E0AC1E7BF5C8/en/wlsetup-cvr.exe http://download.live.com/?sku=messenger</Input><Output>USR 3 SSO I sgorbunov@hotmail.com 8.5.1302 BC01 sgorbunov@hotmail.com</Output><Input> XFR 3 NS 65.54.189.131:1863 U D89 http://msgruser.dlservice.microsoft.com/download/7/5/8/758BFCC9-1744-48F7-8162-E0AC1E7BF5C8/en/wlsetup-cvr.exe http://download.live.com/?sku=messenger</Input><Output>VER 4 MSNP15 CVR0</Output></Trace></ApplicationData>") ;
		AbstractionFunct iabs = new MSNinputAbstraction() ;
		AbstractionFunct oabs = new MSNoutputAbstraction() ;
		
		PassiveLearner learner = new PassiveLearner( ) ;
		FSAutomaton test = learner.constructAutomaton( traces, iabs, oabs) ;
		
	//	test = learner.minimizeAutomaton( test ) ;
		
		
		/*
		 * Testing color inc function
		 
		HashMap<Integer, Integer> color = new HashMap<Integer, Integer>() ;
		color.put( 0, 1) ;
		color.put(1, 255 ) ;
		color.put(2, 30 ) ;
		for ( int i = 0; i < 300; i++ )
		{
			color = learner.incrementColor( color ) ;
			System.out.println( color ) ;
		}
		*/
		
		/*
		// Testing application parser that converts separate msn streams into 
		// packet traces (ie sequences of <IO> messages
		
		AppDataParser parser = new AppDataParser() ;
		ArrayList<MessageTrace> traces = parser.getMsgTraces() ;
		for ( int i = 0; i < traces.size(); i++ )
		{
			MessageTrace t = (MessageTrace)traces.get(i) ;
			ArrayList<HashMap<String, ArrayList<String>>> ios= t.getTrace() ;
			for ( HashMap<String, ArrayList<String>> map : ios )
			{
			    Iterator it = map.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry pairs = (Map.Entry)it.next();
			        System.out.println();
			        System.out.print("{ " +  pairs.getKey() + "," ) ;
			        for ( String s : (ArrayList<String>) pairs.getValue() )
			        {
			        	System.out.print( s + ", " ) ;
			        }
			        System.out.print( " }") ;
			    }

			}
		}
		
		*/
		
		// Testing abstraction functions
		/*
		MSNinputAbstraction testInput = new MSNinputAbstraction() ;
		System.out.println(testInput.abstractMsg("bWFpbm1zblwubmV0\" ")) ;
		*/
		
	}
}
