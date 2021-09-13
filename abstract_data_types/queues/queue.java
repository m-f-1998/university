class QueueException extends RuntimeException{    
	public QueueException(String err) {
		super(err);
	}
}

/**
 *
 * Implementation of Simple Queue Algorithm
 *
 * @author Matthew Frankland
 *
 */

public class Queue {
	private int f; // front of queue
	private int r; // rear of queue
	private int capacity;
    private Object[] Q;
    private static int MAX = 100; 

    public Queue(int capacity){
       this.capacity = capacity;
       Q =  new Object[capacity];
       f = 0;
       r = 0;
    } 

    public Queue(){
       this(MAX);
    }

    public int size(){
        return (Q.length - f + r) % capacity;
    }
    public boolean isEmpty(){
        return (f==r);
    }

    public Object front() throws QueueException {
        if (isEmpty())
           throw new QueueException("Queue is empty.");
        return Q[f];
     }

    public Object dequeue() throws QueueException {
        if (isEmpty())
          throw new QueueException("Queue is empty.");
        Object tmp = Q[f];
        Q[f] = null;
        f = (f+1) % capacity;
        return tmp;
    }
    
    public void enqueue(Object e) throws QueueException {
        if (size() == Q.length - 1)
          throw new QueueException("Queue is full.");
        Q[r] = e;
        r = (r+1) % Q.length;
    }

	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		if(size() > 0)
			buf.append(Q[f]);
		for(int i = 1; i < size();i++){
			buf.append(", " + Q[f + i % capacity]);
		}
		buf.append("]");
		return buf.toString();
	}
}
