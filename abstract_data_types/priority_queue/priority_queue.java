class PriorityQueueException extends RuntimeException{    
	public PriorityQueueException(String err) {
		super(err);
	}
}

/**
 *
 * Implementation of Simple Priority Queue Algorithm
 *
 * @author Matthew Frankland
 *
 */

public class PriorityQueue {
	
	private int[] heap;
	int last;
	
	public PriorityQueue(int max) {
		heap = new int[max+1];
		last = 0;
	}
	
	public int size(){
		return last;
	}
	
	public boolean isEmpty(){
		return last == 0;
	}
	
	public int min(){
		if(isEmpty())
			throw new PriorityQueueException("Empty Priority Queue");
		return heap[1];
	}
	
	public int removeMin(){
		if(isEmpty())
			throw new PriorityQueueException("Empty Priority Queue");
		int min = heap[1];
		heap[1] = heap[last--];
		downHeap();
		return min;
	}
	
	public void insert(int j){
		if(last == heap.length-1)
			throw new PriorityQueueException("Full Priority Queue");			
		heap[++last] = j;
		upHeap();
	}
	
	private int findMin(int ind){
		if(ind+1 > last)
			return ind;
		if (heap[ind] <= heap[ind+1])
			return ind;
		else 
			return ind+1;
	}
	
	private void swap(int i,int j){
		int tmp = heap[i];
		heap[i] = heap[j];
		heap[j] = tmp;
	}
	
	private void downHeap(){
		int index = 1;
		int minchild;
		while(index*2 <= last){
			minchild = findMin(2*index);
			if(heap[index] < heap[minchild])
				break;
			swap(minchild,index);
			index = minchild;
		}
	}
	
	private void upHeap(){
		int index = last;
		int parent;
		while(index > 1){
			parent = index/2;
			if(heap[index] >= heap[parent])
				break;
			swap(index,parent);
			index = parent;
		}
	}
}
