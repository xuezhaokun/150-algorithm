package parallel_rbtree;

import java.util.concurrent.locks.Lock;

public class insertThread extends Thread{
	public static int thread_id = 0;
	public int id;
	private RBTree rbTree;
	private int[] values;
	private Lock lock;
	
	public insertThread(RBTree rbTree, int[] values) {
		this.id = thread_id++;
		this.rbTree = rbTree;
		this.values = values;
		this.lock = null;
	}
	
	public insertThread(RBTree rbTree, int[] values, Lock lock) {
		this.id = thread_id++;
		this.rbTree = rbTree;
		this.values = values;
		this.lock = lock;
	}
	
	@Override
	public void run() {
		for (int value : values) {
			System.out.println("Thread "+id+" add "+ value);
			if (lock != null) lock.lock();
			try {
				this.rbTree.insert(value);
			} catch (NullPointerException ne){
				// 
			}
			if (lock != null) lock.unlock();
		}
//		this.rbTree.print();
//		System.out.println("");
	}
}
