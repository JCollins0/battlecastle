package core;

import java.util.ConcurrentModificationException;
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
	
	private Node front,rear;
	private int count;
	
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
		count++;
	}
	
	public void addRear(T value)
	{
		if(count==0)
			rear=front=new Node(value,null,null);
		else
		{
			rear=new Node(value,null,rear);
		}
		count++;
	}
	
	public T removeFront()
	{
		if(count==0)
			throw new NoSuchElementException();
		T temp=front.getValue();
		front=front.getNext();
		count--;
		return temp;
	}
	
	public T peekFront()
	{
		if(count==0)
			throw new NoSuchElementException();
		return front.getValue();
	}
	
	public T removeRear()
	{
		if(count==0)
			throw new NoSuchElementException();
		T temp=rear.getValue();
		rear=rear.getPrev();
		count--;
		return temp;
	}
	
	public T peekRear()
	{
		if(count==0)
			throw new NoSuchElementException();
		return rear.getValue();
	}
	
	public boolean remove(T value)
	{
		if(count==0)
			throw new NoSuchElementException();
		Node current=front;
		if(value.equals(front.getValue()))
		{
			removeFront();
			count--;
			return true;
		}
		if(value.equals(rear.getValue()))
		{
			removeRear();
			count--;
			return true;
		}
		while(current!=null)
		{
			if(current.getValue().equals(value))
			{
				current.getPrev().setNext(current.getNext());
				current.getNext().setPrev(current.getPrev());
				count--;
				return true;
			}
			current=current.getNext();
		}
		return false;
	}

	@Override
	public String toString() {
		String s="[";
		Node current=front;
		while(current.getNext()!=null)
		{
			s+=current.getValue()+",";
			current=current.getNext();
		}
		s+=current.getValue()+"]";
		return s;
	}

	@Override
	public Iterator<T> iterator()
	{
		return null;
	}
	
	private class HashTableIteratorForward implements Iterator<T>
	{
		private int expectedCount;
		private Node current;
		private int lastIndex;
		
		public HashTableIteratorForward(int size)
		{
			expectedCount = size;
			last = null;
			next = null;
		}
		
		public boolean hasNext()
		{
			return next != null;
		}
		
		public T next()
		{
			checkForComodification();
			if(next == null)
				throw new NoSuchElementException();
			return next.getNext().getValue();
		}
		
		
		public void remove()
		{
			if(count==0)
				throw new NoSuchElementException();
			Node current=front;
			if(front=next)
			{
				removeFront();
				count--;
				return true;
			}
			if(value.equals(rear.getValue()))
			{
				removeRear();
				count--;
				return true;
			}
			while(current!=null)
			{
				if(current.getValue().equals(value))
				{
					current.getPrev().setNext(current.getNext());
					current.getNext().setPrev(current.getPrev());
					count--;
					return true;
				}
				current=current.getNext();
			}
			return false;
			count--;
			expectedCount--;
		}
		
		public void checkForComodification()
		{
			if(count != expectedCount)
				throw new ConcurrentModificationException();
		}
	}

}