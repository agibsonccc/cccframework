/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not posess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.util.datastructure.graph.implementations;




import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.ccc.util.datastructure.graph.exceptions.BadBoundaryException;
import com.ccc.util.datastructure.graph.exceptions.EmptyListException;
import com.ccc.util.datastructure.graph.interfaces.Pointer;
import com.ccc.util.datastructure.graph.interfaces.PointerList;


/**
 * A Linked node based Sequence
 *
 * Course: CS2321 Section ALL
 * Assignment: #3
 * @author Adam Gibson
 */
//SCJ: SpaceComplexity is O(n) due to n nodes stored in list.
public class LinkedPointerList<E> implements PointerList<E>,Iterable<E> {
	//*********************************Fields************************************
	private int size;//size of the list
	private  Node<E> head;//sentinel node for signaling the beginning of the list
	private  Node<E> tail;//sentinel node for signaling end of the list
	//***************************************************************************
	/**
	 * This sets up the Linked Sequence. The implementation is sentinel nodes,
	 * so it creates a head and tail and initializes the size to be zero.
	 */
	public LinkedPointerList() {
		head = new Node<E>(tail,null,null);
		tail =new Node<E>(null,head,null);
		size=0;
		head.setNext(tail);
		tail.setPrev(head);
	}
	//********************************Bridge methods*****************************
	/**
	 * This returns the Pointer at a given index.
	 * @param r index to check for, r has to be >=0 or <size
	 * @return the Pointer at the given index
	 * @throws BadBoundaryException Index has to be within the list
	 * @see Sequence#atIndex(int)
	 */
	public Pointer<E> atIndex(int r) throws BadBoundaryException {
		//Bad index for a get: >= size || <0
		if(r>=size || r<0)
			throw new BadBoundaryException("atIndex failed: no elements  " + r);
		//Get the last item.
		else if(r==size-1)
			return tail.getPrev();

		//Get the first item.
		else if(r==0)
			return head.getNext();

		else {
			//Run through the list..
			Node<E> runner=head.getNext();
			int pos=0;
			//TCJ: n elements to loop through
			while(runner.getNext()!=null && ! (pos>=r)){
				runner=runner.getNext();
				pos++;
			}

			return runner;
		}

	}//end of atIndex

	/**
	 *  This gives the index for a given Pointer
	 * @param p a given Pointer
	 * @return the index of the given Pointer
	 * @throws IllegalArgumentException Pointer needs to be valid
	 * @see Sequence#indexOf(Pointer)
	 */
	public int indexOf(Pointer<E> p) throws IllegalArgumentException {
		//Helper method for exception checking
		p=checkPointer(p);

		//Run through the list..
		int pos=0;
		Node<E> runner =head.getNext();
		//TCJ: n elements to loop through
		while(runner.getNext()!= null && runner!=p){
			runner=runner.getNext();
			pos++;
		}
		return pos;
	}//end of indexOf
	//**************************************End bridge methods******************
	//*********************************Remove methods*****************************

	/**
	 * This removes the first item in the list.
	 * @throws EmptyDequeException The list can't be empty
	 * @see Deque#removeFirst()
	 */
	public E removeFirst() throws EmptyListException {
		Node<E> next=head.getNext();
		E element=next.getValue();

		//TCJ: All method calls are O(1)
		if(next==tail)
			throw new EmptyListException("Tried to remove from empty queue.");

		//General case
		Node<E> after=next.getNext();
		head.setNext(after);
		after.setPrev(head);


		//Get rid of all references of node to be removed.
		next.setPrev(null);
		next.setNext(null);
		next=null;
		size--;

		return element;
	}//end of removeFirst
	/**
	 * This removes the last item in the list.
	 * @return the element of the item removed
	 * @throws EmptyDequeException The list can't be empty.
	 * @see Deque#removeLast()
	 */
	public E removeLast() throws EmptyListException {
		//Last element in list.
		Node<E> prev=tail.getPrev();

		//Empty list
		if(prev==head || prev==null)
			throw new EmptyListException("You can't remove from an empty stack");

		//Element of last item
		E element=prev.getValue();

		//TCJ: All method calls are O(1)
		//General case
		Node<E> before =prev.getPrev();
		tail.setPrev(before);
		before.setNext(tail);


		//Get rid of all references of the node removed.
		prev.setNext(null);
		prev.setPrev(null);
		prev=null;
		size--;
		return element;
	}//end of removeLast
	/**
	 * This removes an item at a given index
	 * @param i the index of the item to remove, i has to be >0 or <size
	 * @return the item removed
	 * @throws IndexOutOfBoundsException Index has to be within list
	 * @see IndexList#remove(int)
	 */
	public E remove(int i) throws IndexOutOfBoundsException {
		//TCJ: All method calls are O(1)
		//Helper method for exception checking
		checkIndex(i);
		if(head.getNext()==tail)
			throw new IndexOutOfBoundsException("Can't remove from an empty list");
		//TCJ: Both removeFirst and Last are O(1)
		if(i==0)
			try {
				return removeFirst();
			} catch (EmptyListException e1) {
				return null;
			}
		else if(i==size)
			try {
				return removeLast();
			} catch (EmptyListException e1) {
				return null;
			}
		else {
			//TCJ: atIndex is O(n)
			Node<E> removeNode=null;
			try {
				removeNode = (Node<E>) atIndex(i);
			} catch (BadBoundaryException e) {
				
			}
			//TCJ: method call is (O(1)
			return remove(removeNode);
		}//end else
	}//end of remove
	/**
	 * This removes a given Pointer from the sequence
	 * @param p the Pointer to remove
	 * @return the Pointer removed
	 * @throws IllegalArgumentException Pointer has to be valid
	 * @see PointerList#remove(Pointer)
	 */

	public E remove(Pointer<E> p) throws IllegalArgumentException {


		//Remove first item in list.
		if(p==head.getNext())
			try {
				return removeFirst();
			} catch (EmptyListException e) {
				return null;
			}
		else if(p==tail.getPrev())
			try {
				return removeLast();
			} catch (EmptyListException e) {
				return null;
			}
		else {
			//Exception checking and returns a node.
			Node<E>curr=checkPointer(p);
			E element=curr.getValue();

			Node<E>next=curr.getNext();
			Node<E>prev=curr.getPrev();

			//Relink nodes.
			next.setPrev(prev);
			prev.setNext(next);

			//Remove all traces of node removed.
			curr.setNext(null);
			curr.setPrev(null);
			curr=null;
			size--;
			return element;
		}
	}//end of remove
	//**********************************End of remove methods***********************

	//**********************************Adder methods*******************************
	/**
	 * This adds a given item at a specified index.
	 * @param i the index to add at, i has to be >=0 or <=size
	 * @param e the element to add
	 * @throws IndexOutOfBoundsException The index has to be within the list.
	 * @see IndexList#add(int, Object)
	 */
	public void add(int i, E e) throws IndexOutOfBoundsException {
		//Helper method for exception checking
		checkIndex(i);
		//adding at first index of list.
		if(i==0)
			addFirst(e);

		//adding at last element.
		else if(i==size)
			addLast(e);

		//Add somewhere in the middle.
		else {
			Node<E> runner=head.getNext();
			//TCJ: n elements to loop through
			for(int j=0;j!=i && runner.getNext()!=null;j++)
				runner=runner.getNext();

			Node<E> prev=runner.getPrev();
			Node<E> newNode = new Node<E>(runner,prev,e);
			prev.setNext(newNode);
			runner.setPrev(newNode);
			size++;

		}//end of else


	}//end of add
	/**
	 * This adds an element after a specified Pointer.
	 * @param p the Pointer to add after
	 * @param e the element to add
	 * @throws IllegalArgumentException The Pointer has to be valid.
	 * @see PointerList#addAfter(Pointer, Object)
	 */

	public void addAfter(Pointer<E> p, E e) throws IllegalArgumentException {
		//TCJ: method call is O(1)
		//Helper method for exception checking
		Node<E> curr=checkPointer(p);
		checkBoundary(curr);
		Node<E> next=curr.getNext();
		Node<E> newNode =new Node<E>(next,curr,e);

		curr.setNext(newNode);
		next.setPrev(newNode);

		size++;

	}//end of addAfter
	/**
	 * This adds an element before a given Pointer.
	 * @param p the Pointer to add before
	 * @param e the element to add
	 * @throws IllegalArgumentException The Pointer has to be valid.
	 * @see PointerList#addBefore(Pointer, Object)
	 */
	public void addBefore(Pointer<E> p, E e) throws IllegalArgumentException {
		//Helper method for exception checking
		//TCJ: method call is O(1)
		Node<E>curr=checkPointer(p);
		checkBoundary(curr);


		Node<E>prev=curr.getPrev();
		Node<E> newNode = new Node<E>(curr,prev,e);

		curr.setPrev(newNode);
		prev.setNext(newNode);

		size++;

	}//end of addBefore

	/**
	 * This adds an element to the first Pointer in the list
	 * @param element the element to add
	 * @see PointerList#addFirst(Object)
	 */
	public void addFirst(E element) {
		Node<E> newNode = new Node<E>(head.getNext(),head,element);
		Node<E> next=head.getNext();
		next.setPrev(newNode);
		head.setNext(newNode);
		size++;
	}//end of addFirst

	/**
	 * This adds an element to the last Pointer in the list.
	 * @param element the element to add
	 * @see PointerList#addLast(Object)
	 */
	public void addLast(E element) {
		Node<E> newNode = new Node<E>(tail,tail.getPrev(),element);
		Node<E> prev=tail.getPrev();

		prev.setNext(newNode);
		tail.setPrev(newNode);
		size++;
	}//end of addLast
	/**
	 * This joins two linked sequences.
	 * @param l1 list to combine with
	 * @param l2 list to be concactneated on to other list
	 * @return a joined LinkedPointerList
	 * @throws EmptyListException 
	 * @throws IllegalArgumentException 
	 */
	public LinkedPointerList<E> join(LinkedPointerList<E> l1,LinkedPointerList<E> l2) throws IllegalArgumentException, EmptyListException{
		System.out.println(l1.size());
		System.out.println(l2.size());
		//TCJ: always takes same amount of time
		Node<E> l2First=checkPointer(l2.first());
		Node<E> l1Last=checkPointer(l1.last());
		l1Last.setNext(l2First);
		l2First.setPrev(l1Last);
		size=l1.size()+l2.size();
		return l1;
	}
	public LinkedPointerList<E> copy(LinkedPointerList<E> s){
		LinkedPointerList<E> ret = new LinkedPointerList<E>();
		for(E e: s)
			ret.addLast(e);
		return ret;
	}
	//************************************************End of adder methods***********

	/**
	 * This returns an element at the given Pointer.
	 * @param i the index of the element to get, i has to be >=0 or < size
	 * @return the element at the given index
	 * @throws IndexOutOfBoundsException Index has to be within the list
	 * @see IndexList#get(int)
	 */
	public E get(int i) throws IndexOutOfBoundsException {
		//Helper method for exception checking
		if(i>size || i<0)
			throw new IndexOutOfBoundsException("Can't get the max size in a list");

		//TCJ: atIndex can be O(n)
		try {
			return atIndex(i).getValue();
		} catch (BadBoundaryException e) {
			return null;
		}
	}//end of get

	/**
	 * This replaces an element at a given index.
	 * @param i the index to replace i has to be >=0 or < size
	 * @param e the element to replace with.
	 * @return the element replaced.
	 * @throws IndexOutOfBoundsException Index has to be within the list
	 * @see IndexList#set(int, Object)
	 */
	public E set(int i, E e) throws IndexOutOfBoundsException {
		//Helper method for exception checking
		checkIndex(i);
		if(head.getNext()==tail)
			throw new IndexOutOfBoundsException("Can't set anything in an empty list");
		//TCJ: atIndex can be O(n)

		E element=null;
		try {
			element = atIndex(i).getValue();
		} catch (BadBoundaryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			((Node<E>) atIndex(i)).setElement(e);
		} catch (BadBoundaryException e1) {
			
		}
		return element;
	}//end of set
	/**
	 * This returns the size of the list.
	 * @return the number of elements in the list
	 * @see PointerList,Deque,IndexList{@link #size}
	 */
	public int size() {return size;}
	/**
	 * This checks to see whether the list is empty.
	 * @return whether the list has 0 items in it.
	 * @see PointerList,Deque,IndexList{@link #isEmpty()}
	 */
	public boolean isEmpty() {return size==0;}

	/**
	 * This returns an iterator for the list
	 * @return an iterator for the list
	 * @see PointerList#iterator()
	 */
	public Iterator<E> iterator() {return new DyIterator<E>();}
	//************************************************Pointeral methods*************************************



	

	/**
	 * This gives the first Pointer in the list
	 * @return the first Pointer in the list
	 * @see PointerList#first()
	 * @throws EmptyListException Can't get the first element of an empty list.
	 */
	public Pointer<E> first() throws EmptyListException {
		if(head.getNext()==tail)
			throw new EmptyListException("First element is null");

		return head.getNext();
	}//end first

	/**
	 * This gives the last Pointer in the list.
	 * @return the last Pointer in the list.
	 * @see PointerList#last()
	 * @throws EmptyListException Can't get the last element of an empty list.
	 */
	public Pointer<E> last() throws EmptyListException {
		if(tail.getPrev()==head)
			throw new EmptyListException("Last element is null");

		return tail.getPrev();
	}//end last

	/**
	 * This returns the element after a given Pointer.
	 * @param p the Pointer to look for; p can't be null and has to be within the list
	 * @return the element after p
	 * @throws IllegalArgumentException The Pointer has to be valid.
	 * @throws BadBoundaryException The element has to be within the list.
	 * @see PointerList#next(Pointer)
	 */
	public Pointer<E> next(Pointer<E> p) throws IllegalArgumentException,
	BadBoundaryException {


		//TCJ: Both methods are O(1)
		//Helper methods for exception checking
		Node<E> curr=checkPointer(p);
		checkBoundary(curr);
		if(curr.getNext()==tail)
			throw new BadBoundaryException("No next item");
		return curr.getNext();
	}//end of next
	//**************************************End Pointeral methods********************************************

	/**
	 * This gives a collection of the Pointers in the list.
	 * @return an iterable collection of the Pointers in the list.
	 * @see PointerList#Pointers()
	 */

	public Iterable<Pointer<E>> Pointers() {
		LinkedPointerList<Pointer<E>> list = new LinkedPointerList<Pointer<E>>();

		Node<E> runner=head.getNext();

		//TCJ: n elements to loop through
		while(runner.getNext()!=null){
			list.addLast(runner);
			runner=runner.getNext();
		}

		return list;
	}//end of Pointers


	/**
	 * This returns the element before a given Pointer.
	 * @param p the Pointer to check for
	 * @return the element before p.
	 * @throws IllegalArgumentException The Pointer has to be valid.
	 * @throws BadBoundaryException The Pointer has to be in the list.
	 * @see PointerList#prev(Pointer)
	 */
	public Pointer<E> prev(Pointer<E> p) throws IllegalArgumentException,
	BadBoundaryException {
		//Helper methods for exception checking
		Node<E> curr=checkPointer(p);
		checkBoundary(curr);

		if(curr.getPrev()==head)
			throw new BadBoundaryException("Can't go off list");
		return curr.getPrev();
	}//end of prev

	/**
	 * This sets an element at a given Pointer.
	 * @param p the Pointer to set an element at.
	 * @param e the element to replace p's element with.
	 * @return the replaced element
	 * @throws IllegalArgumentException The Pointer has to be within the list.
	 * @see PointerList#set(Pointer, Object)
	 */
	public E set(Pointer<E> p, E e) throws IllegalArgumentException {
		//Helper method for exception checking
		Node<E> curr=checkPointer(p);
		E element=curr.getValue();
		curr.setElement(e);
		return element;
	}//end of set

	
	public String toString() {

		/* TCJ: Construction of string for n items is >= O(n)
		 *      (Assuming simple, fixed-size items O(n))
		 */
		/* SCJ: Space complexity is >=O(n) due to concatenation to this String
		 *      (Assuming simple, fixed-size items O(n))
		 */
		String s="[";
		//TCJ: n elements equal to size to loop through
		for(int i=0;i<size();i++){
			s+=get(i).toString();
			if((i+1)!=size())
				s+=",";
		}
		s+="]";
		return s;
	}//end of toString

	//***********************************Nested Classes***********************************
	@SuppressWarnings("hiding")
	public class DyIterator<E> implements Iterator<E> {
		private int index=0;//current index of the iterator

		//List to iterate over
		private LinkedPointerList<E> list;

		//Specifies list to use
		@SuppressWarnings("unchecked")
		public DyIterator(){
			//SCJ: n elements in the list given.
			list = new LinkedPointerList<E>();
			Node<E> runner= (Node<E>) head.getNext();

			//Copy all elements over.
			//TCJ: n elements getting copied
			while(runner.getNext()!=null){
				list.addLast(runner.getValue());
				runner=runner.getNext();
			}
		}//end constructor

		/**
		 * See if there's still an item in the list.
		 * @return whether the iterator has a next item.
		 *
		 */
		public boolean hasNext(){
			return index<size;
		}//end of hasNext


		/**
		 * This gets the next item in the list and moves the index forward.
		 * @return the next element in the list.
		 */
		public E next(){
			if(index>=size)
				throw new NoSuchElementException();
			index++;
			return (E) list.get(index-1);
		}//end of next

		//No remove method supported.
		/**
		 * No remove method supported for this class.
		 * @throws UnsupportedOperationException Operation not supported.
		 */
		public void remove(){
			throw new UnsupportedOperationException();
		}//end of remove

	}//end DyIterator

	@SuppressWarnings({ "hiding", "unchecked" })
	
	private class Node<E> implements Pointer<E>{
		//All methods in this class are O(1),including method calls
		// to getHead and getTail.
		//*********************Fields**********************************************
		private Node<E> prev;//pointer to node's preceding neighbor.
		private Node<E> next;//pointer to node after this one.
		private E e;//element the node holds

		//Node can see head and tail. Purely for checking for proper instances
		//of the list.
		private Node<E> head=(Node<E>) getHead();
		private Node<E> tail=(Node<E>) getTail();
		//*************************************************************************
		//Creates a node setting the fields for the node
		private Node(Node<E> next,Node<E> prev,E e){
			setNext(next);
			setPrev(prev);
			setElement(e);
		}

		//Getters and setters for pointers
		private void setNext(Node<E> next){this.next=next;}
		private void setPrev(Node<E> prev){this.prev=prev;}

		private Node<E> getNext(){return next;}
		private Node<E> getPrev(){return prev;}

		private void setHead(Node head){this.head= head;}
		private void setTail(Node tail){this.tail=tail;}

		//Getter and setter for element.
		private void setElement(E e){this.e=e;}
		public E element() {return e;}

		@Override
		public String toString(){return e+"";}

		@Override
		public E getValue() {
			return e;
		}
	}//end of Node

	//*********************************************End nested classes*********************
	//*********************************Helpers********************************************
	//Helper method for throwing an exception for a given bad index: <0 || >=size
	private void checkIndex(int i) throws IndexOutOfBoundsException{
		if(i<0|| i>size)
			throw new IndexOutOfBoundsException("Bad index " + i);
	}//end checkIndex

	//Helper method for checking if a given node is valid; A valid node isn't null
	//and isn't the head or tail of the list. User shouldn't have access to these regardless.
	private void checkBoundary(Node<E> p) throws IllegalArgumentException  {
		if(p==null)
			throw new IllegalArgumentException("Item is null.");

		else if(p==tail || p==head)
			throw new IllegalArgumentException("Item not within list");
	}//end checkBoundary

	// Convert a Pointer to a Node<E> and see if the node is valid.
	private Node<E> checkPointer (Pointer <E> p) throws
	IllegalArgumentException {
		// Check for an object
		if ( p==null )
			throw new IllegalArgumentException("null value");

		Node<E> node=null;


		try {
			// See if the object is a Node<E> (try a typecast)
			node = (Node<E>)p;
			// See if the node has been removed from the list

			if(node.getPrev()==null && node.getNext()==null)
				throw new IllegalArgumentException(	"Pointer not in a list.") ;
			/*
			if(node.head!=this.head || node.tail!=this.tail)
				throw new IllegalArgumentException("Wrong instance of list");
			 */
		}

		catch ( Exception e ) {
			e.printStackTrace();

			System.out.println(p);
			// Typecast failed :
			throw new IllegalArgumentException(
			" Pointer is of wrong type.") ;
		}
		return node ;
	}//end checkPointer
	//Helper methods for instances of class
	private Node<E> getHead(){return head;}

	private Node<E> getTail(){return tail;}
	
	
	
	@Override
	public boolean add(E arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean addAll(int arg0, Collection<? extends E> arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean contains(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean containsAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public int indexOf(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int lastIndexOf(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public ListIterator<E> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ListIterator<E> listIterator(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean remove(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean removeAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean retainAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public List<E> subList(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public <T> T[] toArray(T[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	//**************************************End helpers***********************************

} // End LinkedPointerList

