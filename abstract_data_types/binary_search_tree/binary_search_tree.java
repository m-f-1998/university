/**
 *
 * Implementation of Simple Binary Search Tree Algorithm
 *
 * @author Matthew Frankland
 *
 */

public class BinarySearchTree {
	private class BSTNode {

		private int value;
		private BSTNode leftChild;
		private BSTNode rightChild;

		public BSTNode(int value) {
			this.value = value;
			leftChild = null;
			rightChild = null;
		}

		public BSTNode getLeftChild() {
			return leftChild;
		}

		public BSTNode getRightChild() {
			return rightChild;
		}

		public void setLeftChild(BSTNode node) {
			leftChild = node;
		}

		public void setRightChild(BSTNode node) {
			rightChild = node;
		}

		public int getValue() {
			return value;
		}

		public void insertNode(BSTNode node) {
			if (node.value < this.value) {
				if (this.getLeftChild() == null) {
					this.setLeftChild(node);
				} else {
					this.getLeftChild().insertNode(node);
				}
			} else if (node.value > this.value) {
				if (this.getRightChild() == null) {
					this.setRightChild(node);
				} else {
					this.getRightChild().insertNode(node);
				}
			} else {
				return;
			}
		}

		public boolean containsNodeWithValue(int value) {
			if (value < this.value) {
				if (this.getLeftChild() == null) {
					return false;
				} else {
					return this.getLeftChild().containsNodeWithValue(value);
				}
			} else if (value > this.value) {
				if (this.getRightChild() == null) {
					return false;
				} else {
					return this.getRightChild().containsNodeWithValue(value);
				}
			}
			return true;
		}

		public BSTNode getLargestValueNode() {
			if (this.getRightChild() == null) {
				return this;
			} else {
				return this.getRightChild().getLargestValueNode();
			}
		}

		public BSTNode deleteNodeWithValue(int value) {
			if (value == this.value) {
				if (this.getLeftChild() != null && this.getRightChild() != null) {
					BSTNode largestLeft = this.getLeftChild().getLargestValueNode();
					this.value = largestLeft.value;
					this.leftChild = this.getLeftChild().deleteNodeWithValue(largestLeft.value);
					return this;
				} else if (this.getLeftChild() != null) {
					return this.getLeftChild();
				} else if (this.getRightChild() != null) {
					return this.getRightChild();
				} else {
					System.out.println("Deleting Node with value " + this.value + " Returning Null");
					return null;
				}
			} else {
				if (value > this.value && this.getRightChild() != null) {
					this.rightChild = this.getRightChild().deleteNodeWithValue(value);
					return this;
				} else if (value < this.value && this.getLeftChild() != null) {
					this.leftChild = this.getLeftChild().deleteNodeWithValue(value);
					return this;
				} else {
					return this;
				}
			}
		}

		public int numberOfNodes() {
			int returnValue = 1;
			if (leftChild != null)
				returnValue += leftChild.numberOfNodes();
			if (rightChild != null)
				returnValue += rightChild.numberOfNodes();											
			return returnValue; 
		}


		public void inOrderTraversal() {
			System.out.println(rootNode.value);
			if (rootNode.getLeftChild() != null) {
				rootNode = rootNode.leftChild;
				rootNode.inOrderTraversal();
			} else if (rootNode.getRightChild() != null) {
				rootNode = rootNode.rightChild;
				rootNode.inOrderTraversal();
			}
		}
	}

	private BSTNode rootNode = null;

	public void insert(int value) {
		if (rootNode == null) {
			rootNode = new BSTNode(value);
		} else {
			rootNode.insertNode(new BSTNode(value));
		}
	}

	public void remove(int value) {
		if (rootNode != null) {
			rootNode = rootNode.deleteNodeWithValue(value);
		}
	}

	public boolean contains(int value) {
		if (rootNode != null) {
			return rootNode.containsNodeWithValue(value);
		} else {
			return false;
		}
	}

	public void inOrderTraversal() {
		if (rootNode != null) {
			rootNode.inOrderTraversal();
        }
	}

	public DLinkedList returnInOrderTraversal() {
		DLinkedList list = new DLinkedList();
		BSTNode root = rootNode;

		if (rootNode != null) {
			if (rootNode.getLeftChild() != null) {
				rootNode = rootNode.getLeftChild();
			}

			list.addAtHead(rootNode.getValue());

			if (rootNode.getRightChild() != null) {
				rootNode = rootNode.getRightChild();
				list.addAtTail(rootNode.getValue());
				while (rootNode.leftChild == null && rootNode.rightChild != null) {
					rootNode = rootNode.getRightChild();
				}
			}

			if (root.leftChild != null) {
				list.addAtTail(root.getValue());
			} else {
				list.addAtTail(rootNode.getValue());
			}
		}
		return list;
	}

	public static void main(String[] args) {
		System.out.println("******* Tree 1 : 3 nodes ***********");
		BinarySearchTree bst1 = new BinarySearchTree();
		bst1.insert(1);
		bst1.insert(2);
		bst1.insert(3);
		bst1.insert(4);
		bst1.inOrderTraversal();

		System.out.println("******* Tree 2 : 1 node ***********");
		BinarySearchTree bst2 = new BinarySearchTree();
		bst2.insert(1);
		bst2.inOrderTraversal();

		System.out.println("******* Tree 3 : empty ***********");
		BinarySearchTree bst3 = new BinarySearchTree();
		bst3.inOrderTraversal();
	}
}