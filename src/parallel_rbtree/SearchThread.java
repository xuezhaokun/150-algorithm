package parallel_rbtree;

import java.util.Random;
import java.util.concurrent.locks.Lock;


public class SearchThread extends Thread{
	public static int thread_id =100 ;
	
	public int id;
	private RBTree rbTree;
	private int num;
	private int nodes;
	private Lock lock = null;
	
	public SearchThread(RBTree rbTree, int num, int nodes){
		this.id = thread_id++;
		this.rbTree = rbTree;
		this.num = num;
		this.nodes = nodes;
		this.lock = null;
	}
	
	public SearchThread(RBTree rbTree, int num, int nodes, Lock lock){
		this.id = thread_id++;
		this.rbTree = rbTree;
		this.num = num;
		this.nodes = nodes;
		this.lock = lock;
	}
	
	@Override
	public void run(){
		Random rand = new Random();
		for(int i=0;i<100;i++){
			if(lock != null) lock.lock();
			Integer target = rand.nextInt(nodes*num);
			//System.out.println("Thread "+id+" search "+target);
			Integer result = this.rbTree.search(target);
			if(lock != null) lock.unlock();
		}
	}
}
