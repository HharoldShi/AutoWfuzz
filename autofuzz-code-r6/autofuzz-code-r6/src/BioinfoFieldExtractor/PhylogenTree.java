package BioinfoFieldExtractor;

import java.util.ArrayList;

/**
 * This is a generic class implementing a phylogenetic tree. It is used to
 * construct clusters of similar messages for global alignment. 
 * 
 * @author Serge Gorbunov
 *
 */
public class PhylogenTree {
	private PhylogenTree parent = null ;
	private PhylogenTree leftChild = null ;
	private PhylogenTree rightChild = null ;
	private ArrayList<Integer> value ;
	private ArrayList<Integer> gapsToBeInserted ; 
	private int height ;
	private int index ;
	private float distance ;

	public PhylogenTree()
	{
		gapsToBeInserted = null ;
		value = null ;
		height = 0 ;
	}
	/**
	 * Check if the node is a leaf
	 * @return True if the node has no children, false otherwise
	 */
	public boolean getIsLeaf()
	{
		if ( leftChild == null && rightChild == null )
			return true ;
		else 
			return false ;
	}

	/**
	 * Returns the height of the tree
	 */
	public int getHeight()
	{
		return height ;
	}

	/**
	 * Returns the parent node
	 */
	public PhylogenTree getParent()
	{
		return parent ;
	}

	/**
	 * Sets the parent
	 */
	public void setParent( PhylogenTree p )
	{
		parent = p ;
	}

	/**
	 * Returns the left child
	 */
	public PhylogenTree getLeftChild()
	{
		return leftChild ;
	}

	/**
	 * Sets the left child
	 */
	public void setLeftChild( PhylogenTree l )
	{
		leftChild = l ;
	}

	/**
	 * Returns the right child
	 */
	public PhylogenTree getRightChild()
	{
		return rightChild ;
	}

	/**
	 * Sets the right child
	 */
	public void setRightChild( PhylogenTree r )
	{
		rightChild = r ;
	}

	/** 
	 * Sets the value
	 */
	public void setValue( ArrayList<Integer> newVal )
	{
		value = newVal ;
	}

	/**
	 * Returns the value
	 */
	public ArrayList<Integer> getValue()
	{
		return value ;
	}
	
	/** 
	 * Sets the index
	 */
	public void setIndex( int i )
	{
		index = i ;
	}
	
	/**
	 * Returns the index
	 */
	public int getIndex()
	{
		return index ;
	}
	
	/**
	 * Sets the distance score between the children
	 */
	public void setDistance( float d )
	{
		this.distance = d ;
	}
	
	/**
	 * Returns the ditance score between the children
	 */
	public float getDistance()
	{
		return distance ;
	}
	
	/**
	 * Sets the gaps 
	 */
	public void setGapsToBoInserted( ArrayList<Integer> g )
	{
		gapsToBeInserted = g ;
	}
	
	/**
	 * Returns the array list of gaps 
	 */
	public ArrayList<Integer> getGapsToBeInserted()
	{
		return gapsToBeInserted ;
	}

}
