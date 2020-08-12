package BioinfoFieldExtractor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class is the standard implementation of 
 * Unweighted Pair Group Method with Arithmetic mean, also known as average linkage method.
 * It is used to combine similar messages into clusters for for glocal alignment.
 * 
 * @author Serge Gorbunov
 *
 */
public class UPGMA {
	
	private float[][] disMatrix ;
	private ArrayList<String> seqs ; 
	private ArrayList<PhylogenTree> clusters ; 
	/**
	 * The constructor 
	 * @param disM Distance matrix
	 * @param seq The sequences of messages   
	 */
	public UPGMA( float[][] disM, ArrayList<String> seq )
	{
		this.disMatrix = disM ;
		seqs = seq ;	
		clusters = new ArrayList<PhylogenTree>() ;
	}
	
	/**
	 * This method performs UPGMA algorithm to build a tree with clusters characterizing
	 * the closest related elements of the sequence. 
	 *  
	 * @return A single clustered phylogenetic tree.
	 */
	public PhylogenTree buildTree()
	{
		Set<PhylogenTree> treeSet = new HashSet<PhylogenTree>() ;
		int numOfTrees = seqs.size() ;
		
		// Initially we create a new tree for each sequence
		for ( int i = 0; i < numOfTrees; i++ )
		{
			PhylogenTree t = new PhylogenTree() ;
			t.setValue( this.convertToDec( seqs.get( i ) ) ) ;
			t.setIndex( i ) ;
			treeSet.add( t ) ;
		}
		
		for ( int k = 0; k < numOfTrees; k++ )
		{
			// Find the closest pair of msgs
			float minDis = Float.MAX_VALUE ;
			int indexX = -1 ;
			int indexY = -1 ;
			PhylogenTree minTree = null ;
			for ( PhylogenTree t1 : treeSet )
			{
				for ( PhylogenTree t2 : treeSet )
				{
					if ( t1.equals( t2 ) )
						continue ;
					// Find the distance between two messages
					float dis = this.findDistance( t1, t2 ) ;
					if ( dis < minDis )
					{
						minDis = dis ;
						indexX = t1.getIndex() ;
						indexY = t2.getIndex() ;
						
						// Create a new root
						minTree = new PhylogenTree() ;
						minTree.setLeftChild( t1 ) ;
						minTree.setRightChild( t2 ) ;
						
						t1.setParent( minTree ) ;
						t2.setParent( minTree ) ;	
						minTree.setDistance( minDis ) ;
					}
				}
			}
			// Remove closest clusters from the set, and add a new one
			if ( minTree != null )
			{
				//System.out.println( minDis ) ;
				treeSet.remove( minTree.getLeftChild() ) ;
				treeSet.remove( minTree.getRightChild() ) ;
				treeSet.add( minTree ) ;
				minTree = null ;
			}
		}
		// There should only be one tree left in the set at the end of the algorithm
		for ( PhylogenTree t : treeSet )
			return t ;
		
		// Something went wrong in the algorithm
		return null ;
	}
	
	/**
	 * Returns the distance between two clusters
	 * 
	 * @param tree1 Phylogenetic tree # 1
	 * @param tree2 Phylogenetic tree # 2
	 * 
	 * @returns The distance between them
	 */
	public float findDistance( PhylogenTree tree1, PhylogenTree tree2 )
	{
		// If both are leaves then return the distance from disMatrix
		if ( tree1.getIsLeaf() == true && tree2.getIsLeaf() == true )
			return disMatrix[tree1.getIndex()][tree2.getIndex()] ;
		else if ( tree1.getIsLeaf() == true )
		{
			float dis = findDistance( tree1, tree2.getLeftChild() ) + findDistance(tree1, tree2.getRightChild() ) ;
			return (float) (dis / 2.0) ;
		} 
		else
		{
			float dis = findDistance( tree1.getLeftChild(), tree2 ) + findDistance( tree1.getRightChild(), tree2 ) ;
			return (float) (dis / 2.0) ;
		}
	}
	
	/**
	 * Cluster "closely-related" trees into clusters
	 * 
	 * @param root of the tree to be clustered
	 */
	public void clusterTrees( PhylogenTree root )
	{
		if ( root == null )
			return ;
		if ( root.getIsLeaf() == true )
			return ;
		if ( root.getLeftChild() != null )
			this.clusterTrees( root.getLeftChild() ) ;
		if ( root.getRightChild() != null )
			this.clusterTrees( root.getRightChild() ) ;
		this.clusters.add( root ) ;
	}
	
	/**
	 * Returns the clusters 
	 */
	public ArrayList<PhylogenTree> getClusters()
	{
		return clusters ;
	}
	
	/**
	 * Converts string to an array of dec representations of the chars
	 */
	public ArrayList<Integer> convertToDec( String msg )
	{
		ArrayList<Integer> arr = new ArrayList<Integer>() ;
		for ( int i = 0; i < msg.length(); i ++ )
		{
			int e = msg.charAt( i ) ;
			arr.add( e ) ;
		}
		return arr ;
	}
}
