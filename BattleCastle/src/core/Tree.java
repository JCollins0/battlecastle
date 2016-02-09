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
			
			if(key.compareTo(temp.key ) <= 0)
				temp.left = node;
			else
				temp.right = node;
		}
		count++;
	}
	
	public TreeNode getNextNode(K key, TreeNode temp)
	{
		if(temp == null)
			return null;
		if(key.compareTo(temp.key) <= 0)
		{
			TreeNode node = getNextNode(key, temp.left);
			if(node == null)
				return temp;
			else
				return temp.left;
		}
		if(key.compareTo(temp.key) > 0)
		{
			TreeNode node = getNextNode(key, temp.right);
			if(node == null)
				return temp;
			else
				return temp.right;
		}
		
		return null;
	}
	
	public TreeNode pollFirstEntry()
	{
		TreeNode temp = root;
		
		while(temp.left != null && temp.left.left != null)
		{
			temp = temp.left;
			System.out.println("TEST");
		}
		
		if(temp == root)
		{
			System.out.println(root);
			if(temp.right != null)
			{
				temp.right.left = root.left;
				root = temp.right;
			}
			count--;
			return temp;
		}else
		{
			TreeNode t = temp.left;
			count--;
			temp.left = null;
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
		//TODO: toString method for left and right recursive
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
	
	private class TreeNode
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
	}
}
