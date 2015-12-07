package core;

import java.util.Iterator;

public class DoubleLinkedList<T> implements Iterable<T>
{
	
	public class Node
	{
		
		T value;
		Node next,prev;
		
		public Node(T value,Node next,Node prev)
		{
			this.value=value;
			this.next=next;
			this.prev=prev;
		}
		
		public T getValue(){return value;}
		
		public Node getNext(){return next;}
		
		public Node getPrev(){return prev;}
		
		public void setValue(T value){this.value=value;}
		
	}

	@Override
	public Iterator<T> iterator()
	{
		return null;
	}

}
