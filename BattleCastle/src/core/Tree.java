package core;

/**
 * 
 * @author Jonathan Colins
 *
 * @param <K> comparable object used as Key
 * @param <V> value
 */
public class Tree<K extends Comparable<K>,V> {

	private TreeNode root;
	private int count;
	
	/**
	 * Initialize a Tree
	 */
	public Tree()
	{
		count = 0;
		root = null;
	}
	
	/**
	 * Add a value to the Tree
	 * @param key the key 
	 * @param value the value
	 */
	public void put(K key, V value)
	{
		TreeNode node = new TreeNode(key,value);
		
		if(root == null)
		{
			root = node;
		}else
		{
			TreeNode temp = getNextNode(node.key, root);
			if(key.compareTo(temp.key ) <= 0)
				temp.left = node;
			else
				temp.right = node;
		}
		count++;
	}
	
	/**
	 * Recursive Method to get the Next Node when adding
	 * @param key
	 * @param temp
	 * @return
	 */
	public TreeNode getNextNode(K key, TreeNode temp)
	{
		if(temp.left == null && key.compareTo(temp.key) <= 0)
			return temp;
		else if(temp.right == null && key.compareTo(temp.key) >0)
			return temp;
		else if(temp != null && key.compareTo(temp.key) <= 0)
			return getNextNode(key, temp.left);
		else if(temp != null && key.compareTo(temp.key) > 0)
			return getNextNode(key, temp.right);
		else
			return null;
	}
	
	/**
	 * Gets the lowest key entry
	 * @return
	 */
	public TreeNode pollFirstEntry()
	{
		TreeNode temp = root;
		while(temp.left != null && temp.left.left != null)
		{
			temp = temp.left;
		}
		if(temp == root && temp.left == null)
		{
			root = temp.right;
			count--;
			return temp;
		}else
		{
			TreeNode t = temp.left;
			count--;
			temp.left = t.right;
			return t;
		}
		
	}
	
	/**
	 * Is this tree empty?
	 * @return True if it is empty, False if not
	 */
	public boolean isEmpty()
	{
		return count == 0;
	}
	
	/**
	 * Clears entire tree
	 */
	public void clear()
	{
		root = null;
		count = 0;
	}
	
	/**
	 * Get the number of elements
	 * @return number of elements
	 */
	public int size(){
		return count;
	}
	
	/**
	 * Returns string representation of Entries
	 */
	public String toString()
	{
		if(root == null) return "[]";
		String s = "[";
		s+=toStringBuilder(root);
		return s + "]";
		
	}
	
	/**
	 * Recursive Helper to build toString
	 * @param node the starting node
	 * @return all nodes below node
	 */
	private String toStringBuilder(TreeNode node)
	{
		if(node == null)
			return "";
		else
		{ 	
			String s = node.toString();
			if(node.left != null)
				s += "," + toStringBuilder(node.left);
			if(node.right != null)
				s += "," + toStringBuilder(node.right);
			return s;
		}
	}
	
	public class TreeNode
	{
		private K key;
		private V value;
		private TreeNode left, right;
		
		public TreeNode(K key, V value)
		{
			this.key = key;
			this.value = value;
		}
		
		public String toString()
		{
			return "["+key+":"+value+"]";
		}
		
		public V getValue()
		{
			return value;
		}
	}
}
