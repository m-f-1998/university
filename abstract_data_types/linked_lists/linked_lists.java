/**
 *
 * Implementation of Simple Linked List Based Algorithm
 *
 * @author Matthew Frankland
 *
 */

public class LinkedList {
	private class Node {
		private int value;
		private Node nextNode;

		public Node(int value) {
			this.value = value;
			nextNode = null; 
		}

		public int getValue() {
			return value;
		}

		public void setValue(int val) {
			value = val;
		}

		public Node getNextNode() {
			return nextNode;
		}

		public void setNextNode(Node node) {
			nextNode = node;
		}

		public void addNodeAtTail(Node node) {
			if (this.nextNode == null) {
				this.nextNode = node;
			} else {
				this.nextNode.addNodeAtTail(node);
			}
		}

		public Node removeAtTail(Node valueAtTail) {
			if (this.nextNode == null) { 
				valueAtTail.setValue(this.getValue());
				return null;
			} else {
				this.nextNode = this.nextNode.removeAtTail(valueAtTail);
				return this;
			}
		}
	}

	private Node headNode;

	public LinkedList() {
		headNode = null;
	}

	public void addAtHead(int i) {
		Node newNode = new Node(i); 
		newNode.setNextNode(headNode); 
		headNode = newNode; 
	}

	public void addAtTail(int i) {
		Node newNode = new Node(i);
		if (headNode == null) { 
			headNode = newNode;
		} else {
			headNode.addNodeAtTail(newNode);
		}
	}

	public int removeAtHead() {
		if (headNode == null) {
			return -1;
		} else {
			Node returnedNode = headNode;
			headNode = headNode.getNextNode();
			return returnedNode.getValue();
		}
	}

	public int removeAtTail() {
		if (headNode == null) {
			return -1;
		} else {
			Node returnedNode = new Node(-1);
			headNode = headNode.removeAtTail(returnedNode);
			return returnedNode.getValue();
		}
	}

	public int calculateSize(){
		int count = 0;
		Node currNode = headNode;
		while (currNode!=null){
			count++;
			currNode = currNode.nextNode;
		}
		return count;
	}
	
	public int calculateTotal(){ 
		
		int total = 0;
		Node currNode = headNode;
		if (currNode!=null){
			total = currNode.getValue();
			while (currNode.nextNode!=null){
				currNode = currNode.nextNode;
				total = total + currNode.getValue();
			}
		}
		return total;
	}

	public Node reverse(){
		Node current = headNode;
		Node next = null;
		Node previous = null;
		
		while (current!=null){
			next = current.getNextNode();
			next.nextNode = previous;
			previous = current;
			current = previous;
		}
		return previous;
	}
}
