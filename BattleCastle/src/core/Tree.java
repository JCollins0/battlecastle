package core;

public class Tree<K extends Comparable<K>,V> {

	private TreeNode root;
	private int count;
	
	public Tree()
	{
		count = 0;
		root = null;
	}
	
	public void put(K key, V value)
	{
		TreeNode node = new TreeNode(key,value);
		
		if(root == null)
		{
			root = node;
		}else
		{
			TreeNode temp = getNextNode(node.key, root);
//			System.out.println(node + ", Parent Node: " + temp);
			if(key.compareTo(temp.key ) <= 0)
				temp.left = node;
			else
				temp.right = node;
		}
		count++;
	}
	
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
	
	public TreeNode pollFirstEntry()
	{
		TreeNode temp = root;
//		System.out.println("ROOT NODE " + root);
		while(temp.left != null && temp.left.left != null)
		{
			temp = temp.left;
		//	System.out.println("TEST");
		}
//		System.out.println("Parent of Node being Removed: " + temp);		
		if(temp == root && temp.left == null)
		{
			//System.out.println(root);
			root = temp.right;
			count--;
			return temp;
		}else
		{
			TreeNode t = temp.left;
			count--;
			temp.left = t.right;
			//temp.left = null;
			return t;
		}
		
	}
	
	public boolean isEmpty()
	{
		return count == 0;
	}
	
	public void clear()
	{
		root = null;
		count = 0;
	}
	
	public int size(){
		return count;
	}
	
	public String toString()
	{
		if(root == null) return "[]";
		String s = "[";
		s+=toStringBuilder(root);
		return s + "]";
		
	}
	
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
