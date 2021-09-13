/**
 *
 * Implementation of Simple Doubly-Linked List Algorithm
 *
 * @author Matthew Frankland
 *
 */

public class DLinkedList {
	private class Node {
		private Object value;
		private Node nextNode;
		private Node prevNode;

		public Node(Object value) {
			this.value = value;
			nextNode = null;
			prevNode = null;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object val) {
			value = val;
		}
		
		public Node getNextNode() {
			return nextNode;
		}

		public void setNextNode(Node node) {
			nextNode = node;
		}
		
		public Node getPrevNode() {
			return prevNode;
		}

		public void setPrevNode(Node node) {
			prevNode = node;
		}
	}

	private Node headNode;
	private Node tailNode;

	public DLinkedList() {
		headNode = null;
		tailNode = null;
	}
	
	public Object getHeadValue(){
		if (headNode == null) {
			return null;
        }
		return headNode.value;
	}
	
	public Object getTailValue(){
		if (tailNode == null){
			return null;
        }
		return tailNode.value;
	}
	
	public void addAtHead(Object obj) {
		Node newNode = new Node(obj); 
		newNode.setNextNode(headNode); 
		if (headNode != null){
			headNode.setPrevNode(newNode);
        }
		headNode = newNode; 
		if (tailNode == null){
			tailNode = newNode;
        }
	}

	public void addAtTail(Object obj) {
		Node newNode = new Node(obj);
		if(tailNode == null){
			tailNode = newNode;
			headNode = newNode;
		}else{
			newNode.setPrevNode(tailNode);
			tailNode.setNextNode(newNode);
			tailNode = newNode;
		}
	}
		
	public Object deleteAtHead() {
		if(headNode == null){
			headNode = null;
			tailNode = null;
			return null;
		}
		if(headNode == tailNode){
			Object res = headNode.getValue();
			headNode = null;
			tailNode = null;
			return res;
		}
		Object res = headNode.getValue();
		headNode = headNode.getNextNode();
		headNode.setPrevNode(null);
		return res;
	}

	public Object deleteAtTail() {
		if(tailNode == null){
			headNode = null;
			tailNode = null;
			return null;
		}
		if(headNode == tailNode){
			Object res = tailNode.getValue();
			headNode = null;
			tailNode = null;
			return res;
		}
		Object res = tailNode.getValue();
		tailNode = tailNode.getPrevNode();
		tailNode.setNextNode(null);
		return res;
	}

	public void reverse(){
		Node temp = headNode;
		headNode = tailNode;
		tailNode = temp;
		
		Node prev = headNode;
		
		while(prev!=null){
			temp=prev.nextNode;
			prev.nextNode=prev.prevNode;
			prev.prevNode = temp;
			prev = prev.nextNode;
		}
	}	
}
