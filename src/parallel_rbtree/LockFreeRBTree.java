package parallel_rbtree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class LockFreeRBTree implements RBTree {
	int size = 0;
	LockFreeRBNode root;
	
	public LockFreeRBTree() {
		this.root = new LockFreeRBNode();
	}
	
	public Integer search(int value) {
		if (this.root == null) return null;
		LockFreeRBNode temp = root;
		while (temp != null && temp.getValue() != null) {
			if (value < temp.getValue()) {
				temp = temp.getLeft();
			} else if(value > temp.getValue()) {
				temp = temp.getRight();
			} else {
				return temp.getValue();
			}
		}
		return temp==null? null : temp.getValue();
	}

	
	public void insert(int value) {
		LockFreeRBNode insertedNode = new LockFreeRBNode (value);
		insertedNode.flag.set(true);
		LockFreeRBNode temp1, temp2;
		while (true){
			temp1 = this.root;
			temp2 = null;
			while(temp1 != null && temp1.getValue() != null) {
				temp2 = temp1;
				if (value < temp1.getValue()) {
					temp1 = temp1.getLeft();
				} else {
					temp1 = temp1.getRight();
				}
			}
			if (!setupLocalAreaForInsert(temp2)) {
				temp2.flag.set(false);
				continue;
			} else {
				break;
			}
		}
		insertedNode.setParent(temp2);
		if (temp2 == null) {
			this.root = insertedNode;
		} else if (value < temp2.getValue()) {
			temp2.setLeft(insertedNode);
		} else {
			temp2.setRight(insertedNode);
		}
		insertedNode.setLeft(new LockFreeRBNode());
		insertedNode.setRight(new LockFreeRBNode());
		insertedNode.setRed(true);
		rbInsertFixup(insertedNode);
	}

	private boolean setupLocalAreaForInsert(LockFreeRBNode x) {
		if (x == null) return true;
		LockFreeRBNode uncle;
		LockFreeRBNode parent = x.getParent();
		if (parent == null) return true;
		if (!x.flag.compareAndSet(false, true)){
			return false;
		}
		
		if (!parent.flag.compareAndSet(false, true)){
			return false;
		}
		
		if (!parent.equals(x.getParent())) {
			//parent.setFlag(new AtomicBoolean(false));
			parent.flag.set(false);
			return false;
		}
		
		if (x.equals(x.getParent().getLeft())) {
			uncle = x.getParent().getRight();
		} else {
			uncle = x.getParent().getLeft();
		}
		
		if (uncle != null && !uncle.flag.compareAndSet(false, true)) {
			//x.getParent().setFlag(new AtomicBoolean(false));
			x.getParent().flag.set(false);
			return false;
		}
		return true;
	}
	
	public void rbInsertFixup(LockFreeRBNode x) {
		LockFreeRBNode parent, uncle = null, grandpa = null;
		List<LockFreeRBNode> local_area = new ArrayList<LockFreeRBNode>();
		parent = x.getParent();
		if (parent != null) {
			grandpa = parent.getParent();
		}
		
		if (grandpa != null) {
			if (parent.equals(grandpa.getLeft())) {
				uncle = grandpa.getRight();
			} else {
				uncle = grandpa.getLeft();
			}
		}
		local_area.add(x);
		local_area.add(parent);
		local_area.add(uncle);
		local_area.add(grandpa);
		
		while (x.getParent() != null && x.getParent().isRed()) {
			parent = x.getParent();
			grandpa = parent.getParent();
			local_area.add(x);
			local_area.add(parent);
			local_area.add(grandpa);
			if (parent.equals(grandpa.getLeft())) {
				uncle = grandpa.getRight();
				local_area.add(uncle);
				if (uncle.isRed()) {
					parent.setRed(false);
					uncle.setRed(false);
					grandpa.setRed(true);
					x = moveAreaUpward(x, local_area);
				} else {
					if (x.equals(parent.getRight())) {
						x = parent;
						leftRotation(x);
					}
					parent.setRed(false);
					grandpa.setRed(true);
					rightRotation(grandpa);
				}
			} else {
				uncle = grandpa.getLeft();
				local_area.add(uncle);
				if (uncle.isRed()) {
					parent.setRed(false);
					uncle.setRed(false);
					grandpa.setRed(true);
					x = moveAreaUpward(x, local_area);
				} else {
					if (x.equals(parent.getLeft())) {
						x = parent;
						rightRotation(x);
					}
					parent.setRed(false);
					grandpa.setRed(true);
					leftRotation(grandpa);
				}
			}
		}
		
		this.root.setRed(false);
		
		for (LockFreeRBNode node : local_area) {
			if (node != null) {
				//node.setFlag(new AtomicBoolean(false));
				node.flag.set(false);
			} 
		}
		
	}
	
	private LockFreeRBNode moveAreaUpward(LockFreeRBNode x, List<LockFreeRBNode> local_area) {
		LockFreeRBNode parent, uncle = null, grandpa = null;
		parent = x.getParent();
		if (parent != null) {
			grandpa = parent.getParent();
		}
		
		if (grandpa != null) {
			if (parent.equals(grandpa.getLeft())) {
				uncle = grandpa.getRight();
			} else {
				uncle = grandpa.getLeft();
			}
		}
		LockFreeRBNode update_x, update_parent = null, update_uncle = null, update_grandpa = null;
		update_x = grandpa;
		
		while (update_x.getParent() != null) {
			update_parent = update_x.getParent();
			update_grandpa = update_parent.getParent();
			if (update_grandpa == null) break;
			if (!update_grandpa.flag.compareAndSet(false, true)) {
				update_grandpa.flag.set(false);
				//update_grandpa.setFlag(new AtomicBoolean(false));
			}
			if (update_parent.equals(update_grandpa.getLeft())) {
				update_uncle = update_grandpa.getRight();
			} else {
				update_uncle = update_grandpa.getLeft();
			}
			if (update_uncle != null && !update_uncle.flag.compareAndSet(false, true)) {
				update_grandpa.flag.set(false);
				update_uncle.flag.set(false);
//				update_grandpa.setFlag(new AtomicBoolean(false));
//				update_uncle.setFlag(new AtomicBoolean(false));
			}
			break;
		}
		local_area.add(update_x);
		local_area.add(update_parent);
		local_area.add(update_uncle);
		local_area.add(update_grandpa);
		return update_x;
	}
	
	private void leftRotation(LockFreeRBNode x) {
		if (x == null) return;
		LockFreeRBNode y = x.getRight();
		x.setRight(y.getLeft());
		if (y.getLeft() != null) {
			y.getLeft().setParent(x);
		}
		y.setParent(x.getParent());
		
		if (x.getParent() == null) {
			this.root = y;
		} else {
			if (x.equals(x.getParent().getLeft())) {
				x.getParent().setLeft(y);
			} else {
				x.getParent().setRight(y);
			}
		}
		y.setLeft(x);
		x.setParent(y);
	}
	
	private void rightRotation(LockFreeRBNode y) {
		if (y == null) return;
		LockFreeRBNode x = y.getLeft();
		y.setLeft(x.getRight());
		if (x.getRight() != null) {
			x.getRight().setParent(y);
		}
		x.setParent(y.getParent());
		
		if (y.getParent() == null) {
			this.root = x;
		} else {
			if (y.equals(y.getParent().getLeft())) {
				y.getParent().setLeft(x);
			} else {
				y.getParent().setRight(x);
			}
		}
		x.setRight(y);
		y.setParent(x);
	}
	
	public synchronized void print(){
		List<List<String>> res = new LinkedList<List<String>>();
		res = printHelp(root,0,res);
		//int id = ((test.testThread<V>)Thread.currentThread()).id;
		//System.out.println("Thread "+id+"printing:");
		for(List<String> list:res){
			for(String word: list){
				System.out.print(word+" ");
			}
			System.out.print("\n");
		}
	}
	
	protected synchronized List<List<String>> printHelp(LockFreeRBNode root,int height,List<List<String>> res){
		if(root == null) return res;
		List<String> list;
		if(height >= res.size()){
			list = new LinkedList<String>();
			res.add(list);
		}else{
			list = res.get(height);
		}
		if(root.getValue() == null){
			list.add(" _ ");
		}else{
			list.add(root.getValue()+(root.isRed()?"_R":"_B")+(root.flag.get()?"T":"F"));
		}
		printHelp(root.getLeft(),height+1,res);
		printHelp(root.getRight(),height+1,res);
		return res;
	}

}
