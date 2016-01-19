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
			Node temp = new Node(value,front,null);
			front.setPrev(temp);
			front = temp;
		}
		count++;
	}
	
	public void addRear(T value)
	{
		if(count==0)
			rear=front=new Node(value,null,null);
		else
		{
			Node temp = new Node(value,null,rear);
			rear.setNext(temp);
			rear = temp;
		}
		count++;
	}
	
	public T removeFront()
	{
		System.out.println(count);
		if(count==0)
			throw new NoSuchElementException();
		T temp=front.getValue();
		front=front.getNext();
		if(--count>0)
			front.prev=null;
		else
		{
			front=rear=null;
		}
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
		if(--count>0)
			rear.next=null;
		else
		{
			front=rear=null;
		}
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
			//count--;
			return true;
		}
		if(value.equals(rear.getValue()))
		{
			removeRear();
			//count--;
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
	
	public void linkToFront(T value)
	{
		remove(value);
		addFront(value);
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
		return new IteratorForward();
	}
	
	public Iterator<T> iteratorb()
	{
		return new IteratorBackward();
	}
	
	private class IteratorForward implements Iterator<T>
	{
		private int expectedCount;
		private Node current;
		
		public IteratorForward()
		{
			expectedCount = count;
			current=new Node(null,front,null);
		}
		
		public boolean hasNext()
		{
			return current.getNext() != null;
		}
		
		public T next()
		{
			checkForComodification();
			if(current.getNext() == null)
				throw new NoSuchElementException();
			current=current.getNext();
			return current.getValue();
		}
		
		
		public void remove()
		{
			if(count==0)
				throw new NoSuchElementException();
			if(front==current)
			{
				removeFront();
				//count--;
				current=front;
			}
			else if(current==rear)
			{
				removeRear();
				//count--;
				current=rear;
			}
			else
			{
				Node temp=current;
				current.getPrev().setNext(current.getNext());
				current.getNext().setPrev(current.getPrev());
				current=temp.getPrev();
				count--;
			}
			expectedCount--;
		}
		
		public void checkForComodification()
		{
			if(count != expectedCount)
				throw new ConcurrentModificationException();
		}
	}
	
	private class IteratorBackward implements Iterator<T>
	{
		private int expectedCount;
		private Node current;
		
		public IteratorBackward()
		{
			expectedCount = count;
			current=new Node(null,null,rear);
		}
		
		public boolean hasNext()
		{
			return current.getPrev() != null;
		}
		
		public T next()
		{
			checkForComodification();
			if(current.getPrev() == null)
				throw new NoSuchElementException();
			current=current.getPrev();
			return current.getValue();
		}
		
		
		public void remove()
		{
			if(count==0)
				throw new NoSuchElementException();
			if(front==current)
			{
				removeFront();
				//count--;
				current=front;
			}
			else if(current==rear)
			{
				removeRear();
				//count--;
				current=rear;
			}
			else
			{
				Node temp=current;
				current.getPrev().setNext(current.getNext());
				current.getNext().setPrev(current.getPrev());
				current=temp.getNext();
				count--;
			}
			expectedCount--;
		}
		
		public void checkForComodification()
		{
			if(count != expectedCount)
				throw new ConcurrentModificationException();
		}
	}

}
