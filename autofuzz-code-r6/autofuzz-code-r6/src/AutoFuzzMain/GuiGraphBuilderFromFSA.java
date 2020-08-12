package AutoFuzzMain;

import java.util.ArrayList;

import ProtocolLearner.FSAutomaton;
import ProtocolLearner.FSATransition;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;

public class GuiGraphBuilderFromFSA {

	/** 
	 * A recursive function that add children and edges for a particular state in the FSA
	 * 
	 * @param inputAutomaton Input FSA
	 * @param statePointer A pointer to the state for which children to be added
	 * 
	 * @return A directed Graph with the states, its children and edges added
	 *  
	 */
	public DirectedSparseMultigraph<Integer,String> populateGraphFromFSA( FSAutomaton inputAutomaton, int statePointer )
	{
		ArrayList<Integer> vertices = new ArrayList<Integer>() ;
		return this.populateGraphFromFSA(inputAutomaton, statePointer, vertices) ;
	}
	public DirectedSparseMultigraph<Integer,String> populateGraphFromFSA( FSAutomaton inputAutomaton, int statePointer, ArrayList<Integer> vertices )
	{	

		DirectedSparseMultigraph<Integer,String> graph = new DirectedSparseMultigraph<Integer,String>() ;


		// Add the current vertex to the graph
		graph.addVertex( statePointer ) ;
		vertices.add( statePointer ) ;

		// Add the children to the graph

		for ( FSATransition t: inputAutomaton.getStateTransition( statePointer ) )
		{
			if ( ! vertices.contains( t.getNewState() ) )
			{
				DirectedSparseMultigraph<Integer,String> tempGraph = populateGraphFromFSA( inputAutomaton,  t.getNewState(), vertices ) ;
				for ( Integer v : tempGraph.getVertices() )
					graph.addVertex( v ) ;
				for ( String e : tempGraph.getEdges() )
				{
					graph.addEdge( e, tempGraph.getSource( e ), tempGraph.getDest( e ) )  ;
				}
			}
			String edgeID = + statePointer + ":" + t.getNewState() + "=" + t.getInputSymbol() ;
			graph.addEdge( edgeID, statePointer, t.getNewState() ) ;
		}
		return graph ;
	}
}
