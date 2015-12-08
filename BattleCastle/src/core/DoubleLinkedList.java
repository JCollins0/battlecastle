package core;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoubleLinkedList<T> implements Iterable<T>
{
	
	public class Node
	{
		
		private T value;
		private Node next,prev;
		
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
		
		public void setNext(Node next){this.next=next;}
		
		public void setPrev(Node prev){this.prev=prev;}
		
	}
	
	Node front,rear;
	int count;
	
	public DoubleLinkedList()
	{
		count=0;
		rear=front=null;
	}
	
	public int getSize()
	{
		return count;
	}
	
	public void addFront(T value)
	{
		if(count==0)
			rear=front=new Node(value,null,null);
		else
		{
			front=new Node(value,front,null);
		}
	}
	
	public void addRear(T value)
	{
		if(count==0)
			rear=front=new Node(value,null,null);
		else
		{
			rear=new Node(value,null,rear);
		}
	}
	
	public T removeFront()
	{
		if(count==0)
			throw new NoSuchElementException();
		T temp=front.getValue();
		front=front.getNext();
		return temp;
	}
	
	public T removeRear()
	{
		if(count==0)
			throw new NoSuchElementException();
		T temp=rear.getValue();
		rear=rear.getPrev();
		return temp;
	}
	
	public boolean remove(T value)
	{
		if(count==0)
			throw new NoSuchElementException();
		Node current=front;
		if(value.equals(front.getValue()))
		{
			removeFront();
			return true;
		}
		if(value.equals(rear.getValue()))
		{
			removeRear();
			return true;
		}
		while(current!=null)
		{
			if(current.getValue().equals(value))
			{
				current.getPrev().setNext(current.getNext());
				current.getNext().setPrev(current.getPrev());
				return true;
			}
			current=current.getNext();
		}
	}

	@Override
	public Iterator<T> iterator()
	{
		return null;
	}

}
