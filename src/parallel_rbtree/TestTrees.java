package parallel_rbtree;

import java.util.ArrayList;

import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFrame;

import drawer.TreeGUI;


public class TestTrees {

	public static void main(String[] args) throws InterruptedException {
	
		//for (int num_threads = 1; num_threads < 11; num_threads++) {
			int counter = 0;
			long locked_time = 0;
			long lock_free_time = 0;
			//while (counter < 10) {
				long start = 0, end = 0, duration = 0;
				int num_threads = 4;
				int insert_nodes_per_thread = 250;
				boolean vis = false;
				List<Thread> locked_threads = new ArrayList<Thread>();
				List<Thread> lock_free_threads = new ArrayList<Thread>();
				Lock lock = new ReentrantLock();
				locked_threads.clear();
				lock_free_threads.clear();
				LockedRBTree locked_tree = new LockedRBTree();
				LockFreeRBTree lock_free_tree = new LockFreeRBTree();
				
				for (int i = 0; i < num_threads; i++) {
					int[] values = new int[insert_nodes_per_thread];
					for (int j = 0; j < insert_nodes_per_thread; j++) {
						Random rand = new Random();

						int  n = rand.nextInt(1000) + 1;
						values[j] = n;
					}
					locked_threads.add(new insertThread(locked_tree, values, lock));
					lock_free_threads.add(new insertThread(lock_free_tree, values));
				}
				//start = System.nanoTime(); 
				start = System.currentTimeMillis();
				for(Thread thread: locked_threads){
					thread.start();
				}
				for(Thread thread: locked_threads){
					thread.join();
				}
				end = System.currentTimeMillis();
				duration = end - start;
				//locked_time += duration;
				//locked_tree.print();
				System.out.println("");
				System.out.println("RBTree each thread insert "+insert_nodes_per_thread+" nodes using " +(double)duration + " ms");
				System.out.println("----------------------------------");
		    
//				threads.clear();
//				LockFreeRBTree lock_free_tree = new LockFreeRBTree();
//				for (int i = 0; i < num_threads; i++) {
//					int[] values = new int[insert_nodes_per_thread];
//					for (int j = 0; j < insert_nodes_per_thread; j++) {
//						values[j] = insert_nodes_per_thread*i+j;
//					}
//					threads.add(new insertThread(lock_free_tree, values));
//				}
//		    
		  
				//start = System.nanoTime(); 
				start = System.currentTimeMillis();
				for(Thread thread: lock_free_threads){
					thread.start();
				}
				for(Thread thread: lock_free_threads){
					thread.join();
				}
				end = System.currentTimeMillis();
				duration = end - start;
				//lock_free_time += duration;
				//lock_free_tree.print();
				//System.out.println("");
				System.out.println("LockFreeRBTree each thread insert "+insert_nodes_per_thread+" nodes using " +(double)duration + " ms");
				
				if (vis){
					lock_free_tree.breadth(lock_free_tree.root);

					TreeGUI gui = new TreeGUI(lock_free_tree);					
				}

				
//				System.out.println("\n\rLock-Free Red-Black Tree Search test");
//		       //tree2.print();
//		       //System.out.println("");
//		      // tree1.print();
//		      // System.out.println("");
//		       
//				locked_threads.clear();
//		       for(int i = 0;i<num_threads;i++){
//		    	   		locked_threads.add(new SearchThread(locked_tree,num_threads, insert_nodes_per_thread, lock));
//		       }
//		       start = System.currentTimeMillis(); 
//		       for(Thread thread: locked_threads) {
//		    	   		thread.start();
//		       }
//		       for(Thread thread: locked_threads){
//		    	   	thread.join();
//		       }
//		       duration = System.currentTimeMillis() - start;
//		       System.out.println("RBTree each thread search 10000 times using " +(double)duration + " ms");
//		       
//		       lock_free_threads.clear();
//		       for(int i = 0;i<num_threads;i++){
//		    	   lock_free_threads.add(new SearchThread(lock_free_tree, num_threads, insert_nodes_per_thread));
//		       }
//		       start = System.currentTimeMillis();
//		       for(Thread thread: lock_free_threads) {
//		    	   		thread.start();
//		       }
//		       for(Thread thread: lock_free_threads){
//		    	   		thread.join();
//		       }
//		       duration = System.currentTimeMillis() - start;
//		       System.out.println("LockFreeRBTree each thread search 10000 times using " +(double)duration + " ms");
	}

}
