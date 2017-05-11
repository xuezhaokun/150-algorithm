package parallel_rbtree;

import java.util.ArrayList;

import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFrame;

import drawer.LockBasedRBTreeGUI;
import drawer.LockFreeRBTreeGUI;


public class TestTrees {

	public static void main(String[] args) throws InterruptedException {
	
		long start = 0, end = 0, duration = 0;
		// ---- customize ----
		int num_threads = 0;
		int insert_nodes_per_thread = 0;
		boolean visulize_locked_tree = false;
		boolean visulize_lock_free_tree = false;
		// ---------
		List<Thread> locked_threads = new ArrayList<Thread>();
		List<Thread> lock_free_threads = new ArrayList<Thread>();
		Lock lock = new ReentrantLock();
		locked_threads.clear();
		lock_free_threads.clear();
		LockedRBTree locked_tree = new LockedRBTree();
		LockFreeRBTree lock_free_tree = new LockFreeRBTree();

		System.out.println(" -------- Insertion Test --------");
		System.out.println("Each thread inserts: " + insert_nodes_per_thread + " nodes");
		System.out.println("Lock-Based Threads:");
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
		start = System.currentTimeMillis();
		for(Thread thread: locked_threads){
			thread.start();
		}
		for(Thread thread: locked_threads){
			thread.join();
		}
		
		end = System.currentTimeMillis();
		duration = end - start;
		System.out.println("Lock-Based RBTree uses " +(double)duration + " ms");
	
		System.out.println("Lock-Free Threads:");
		start = System.currentTimeMillis();
		for(Thread thread: lock_free_threads){
			thread.start();
		}
		for(Thread thread: lock_free_threads){
			thread.join();
		}
		end = System.currentTimeMillis();
		duration = end - start;
		System.out.println("Lock-Free RBTree uses " +(double)duration + " ms");
		
		if (visulize_locked_tree) {
			locked_tree.breadth(locked_tree.root);
			LockBasedRBTreeGUI locked_tree_gui = new LockBasedRBTreeGUI(locked_tree);
		}
		
		if (visulize_lock_free_tree) {
			lock_free_tree.breadth(lock_free_tree.root);
			LockFreeRBTreeGUI lock_free_tree_gui = new LockFreeRBTreeGUI(lock_free_tree);	
		}

		System.out.println(" -------- Search Test --------");
		System.out.println("Each thread search 10000 times");
		locked_threads.clear();
		for (int i = 0; i < num_threads; i++) {
			locked_threads.add(new SearchThread(locked_tree,num_threads, insert_nodes_per_thread, lock));
		}
		start = System.currentTimeMillis(); 
		for(Thread thread: locked_threads) {
			thread.start();
		}
		for(Thread thread: locked_threads){
			thread.join();
		}
		duration = System.currentTimeMillis() - start;
		System.out.println("Lock-Based RBTree uses: " +(double)duration + " ms");
		
		lock_free_threads.clear();
		for (int i = 0; i<num_threads; i++) {
			lock_free_threads.add(new SearchThread(lock_free_tree, num_threads, insert_nodes_per_thread));
		}
		start = System.currentTimeMillis();
		for(Thread thread: lock_free_threads) {
			thread.start();
		}
		for(Thread thread: lock_free_threads){
			thread.join();
		}
		end = System.currentTimeMillis();
		duration = end - start;
		System.out.println("Lock-Free RBTree uses " +(double)duration + " ms");
	}

}
