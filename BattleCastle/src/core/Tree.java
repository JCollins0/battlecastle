package core;

public class Tree<K extends Comparable<K>,V> {

	private TreeNode root;
	private int count;
	
	public Tree()
	{
		count = 0;
		root = null;
	}
	
	public void add(K key, V value)
	{
		TreeNode node = new TreeNode(key,value);
		
		if(root == null)
		{
			root = node;
		}else
		{
			TreeNode temp = root;
			
			while(temp.left != null || temp.right != null)
			{
				if(temp.left != null && node.key.compareTo(temp.key) <= 0)
				{
					temp = temp.left;
				}else if( temp.right != null&&node.key.compareTo(temp.key) > 0 )
					temp = temp.right;
			}
			
			if(node.key.compareTo(temp.key) <= 0)
				temp.left = node;
			else
				temp.right = node;
				
				
		}
		count++;
	}
	
	public int size(){
		return count;
	}
	
	public String toString()
	{
		if(root == null) return "[]";
		String s = "[";
		//TODO: toString method for left and right recursive
		return s;
		
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
	}
}
