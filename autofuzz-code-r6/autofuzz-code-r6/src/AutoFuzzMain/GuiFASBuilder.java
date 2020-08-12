package AutoFuzzMain;

import java.awt.Dimension;

import javax.swing.JPanel;

import ProtocolLearner.FSAutomaton;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class GuiFASBuilder {
	
	
	/**
	 * Draws a finite automaton on the specified jpanel 
	 * 
	 * @param inputAutomaton The FSM to be drawn
	 * @param panel JPanel on which to draw 
	 */
	
	public void drawFSA( FSAutomaton inputAutomaton, JPanel panel )
	{
		GuiGraphBuilderFromFSA graphBuilder = new GuiGraphBuilderFromFSA() ;
		
		DirectedSparseMultigraph<Integer,String> g = graphBuilder.populateGraphFromFSA(inputAutomaton, inputAutomaton.getInitStateIndex() ) ;
		
		ISOMLayout<Integer, String> layout = new ISOMLayout<Integer, String>(g ) ;
		
		BasicVisualizationServer<Integer,String> vv =
		new BasicVisualizationServer<Integer,String>(layout);
		vv.setPreferredSize(new Dimension(995 , 2000 )); //Sets the viewing area size
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Integer>());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		panel.add( vv ) ;
		/*
		GuiGraphBuilderFromFSA graphBuilder = new GuiGraphBuilderFromFSA() ;
		
		DelegateForest<Integer,String> g = graphBuilder.populateGraphFromFSA(inputAutomaton, inputAutomaton.getInitStateIndex() ) ;
		
		TreeLayout<Integer, String> tree = new TreeLayout<Integer, String>( g, 50, 75 ) ;
		
		BasicVisualizationServer<Integer,String> vv =
		new BasicVisualizationServer<Integer,String>( tree );
	
		
		vv.setPreferredSize(new Dimension( 995 , 2000 ) ); //Sets the viewing area size
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Integer>()) ;
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<Integer, String>());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<String>()) ;
		//vv.getRenderContext().setVe
		//vv.getRenderContext().setEdgeLabelClosenessTransformer( new BasicRenderer() ) ;
        

		//vv.getRenderer().getEdgeLabelRenderer().labelEdge(arg0, arg1, arg2, arg3)
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR) ;
		
		panel.add( vv ) ;
		*/
	}
}
