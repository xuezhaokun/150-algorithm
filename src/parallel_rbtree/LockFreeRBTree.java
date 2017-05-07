package parallel_rbtree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LockFreeRBTree implements RBTree{
	int size = 0;
	LockFreeRBNode root;
	
	public LockFreeRBTree() {
		this.root = new LockFreeRBNode();
	}
	
	public int search(int value) {
		if (root == null) {
			return Integer.MIN_VALUE;
		}
		LockFreeRBNode temp = root;
		while (temp != null && temp.getValue() > 0) {
			if (value < temp.getValue()) {
				temp = temp.getLeft();
			}else if (value > temp.getValue()) {
				temp = temp.getRight();
			} else{
				return temp.getValue();
			}
		}
		return temp==null?null:temp.getValue();
	}
	
	public void insert(int value) throws NullPointerException{
		LockFreeRBNode insertedNode = new LockFreeRBNode(value);
		LockFreeRBNode temp1, temp2;
		insertedNode.flag.set(true);
		while (true) {
			temp1 = this.root;
			temp2 = null;
			while (temp1 != null && temp1.getValue() > 0) {
				temp2 = temp1;
				if (value < temp1.getValue()) {
					temp1 = temp1.getLeft();
				} else {
					temp1 = temp1.getRight();
				}
			}
			if (!SetupLocalAreaForInsert(temp2)) {
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
	
	private boolean SetupLocalAreaForInsert(LockFreeRBNode x) {
		if (x == null) {
			return true;
		}
		LockFreeRBNode parent = x.getParent();
		LockFreeRBNode uncle;
		if (parent == null) return true;
		if (!x.flag.compareAndSet(false, true)) {
			return false;
		}
		if (!parent.flag.compareAndSet(false, true)) {
			return false;
		}
		if (parent != x.getParent()) {
			parent.flag.set(false);
			return false;
		}
		if (x == x.getParent().getLeft()) {
			uncle = x.getParent().getRight();
		} else {
			uncle = x.getParent().getLeft();
		}
		if (uncle != null && !uncle.flag.compareAndSet(false, true)) {
			x.getParent().flag.set(false);
			return false;
		}
		return true;
	}
	
	private void rbInsertFixup(LockFreeRBNode x) {
		LockFreeRBNode temp, parent, uncle = null, gradparent = null;
		parent = x.getParent();
		ArrayList<LockFreeRBNode> local_area = new ArrayList<LockFreeRBNode>();
		local_area.add(x);
		local_area.add(parent);
		
		if (parent != null) {
			gradparent = parent.getParent();	
		}
		
		if (gradparent != null) {
			if (gradparent.getLeft() == parent) {
				uncle = gradparent.getRight();
			} else {
				uncle = gradparent.getLeft();		
			}
		}

		local_area.add(uncle);
		local_area.add(gradparent);

		while (x.getParent()!= null && x.getParent().isRed()) {
			parent = x.getParent();
			gradparent = gradparent.getParent();
			
			if (x.getParent() == x.getParent().getParent().getLeft()) {
				temp = x.getParent().getParent().getRight();
				uncle = temp;
				local_area.add(x);
				local_area.add(parent);
				local_area.add(gradparent);
				local_area.add(uncle);
				
				if (temp.isRed()) {
					x.getParent().setRed(false);
					temp.setRed(false);
					x.getParent().getParent().setRed(true);
					x = MoveLocalAreaUpward(x, local_area);
				} else {
					if (x == x.getParent().getRight()) {
						// Case 2
						x = x.getParent();
						leftRotate(x);
					}
					// Case 3
					x.getParent().setRed(false);
					x.getParent().getParent().setRed(true);
					rightRotate(x.getParent().getParent());
				}
			} else {
				temp = x.getParent().getParent().getLeft();
				uncle = temp;
				
				local_area.add(x);
				local_area.add(parent);
				local_area.add(gradparent);
				local_area.add(uncle);
				
				if (temp.isRed()) {
					// Case 1
					x.getParent().setRed(false);
					temp.setRed(false);
					x.getParent().getParent().setRed(true);
					x = MoveLocalAreaUpward(x, local_area);
				} else {
					if (x == x.getParent().getLeft()) {
						// Case 2
						x = x.getParent();
						rightRotate(x);
					}
					// Case 3
					x.getParent().setRed(false);
					x.getParent().getParent().setRed(true);
					leftRotate(x.getParent().getParent());
				}
			}
		}
		
		this.root.setRed(false);

		for (LockFreeRBNode node : local_area) {
			if (node!= null) node.flag.set(false);
		}
	}
	
	private LockFreeRBNode MoveLocalAreaUpward(LockFreeRBNode x, ArrayList<LockFreeRBNode> working) {
		LockFreeRBNode parent = x.getParent();
		LockFreeRBNode grandparent = parent.getParent();
		LockFreeRBNode uncle;
		if (parent == grandparent.getLeft()){
			uncle = grandparent.getRight();
		} else {
			uncle = grandparent.getLeft();
		}
		
		LockFreeRBNode updated_x, updated_parent = null, updated_uncle = null, updated_grandparent = null;
		updated_x = grandparent;
		
		while (true && updated_x.getParent()!= null) {
			updated_parent = updated_x.getParent();
			if (!updated_parent.flag.compareAndSet(false, true)) {
				continue;
			}
			updated_grandparent = updated_parent.getParent();
			if (updated_grandparent == null) break;
			if (!updated_grandparent.flag.compareAndSet(false, true)) {
				updated_parent.flag.set(false);
				continue;
			}
			if (updated_parent == updated_grandparent.getLeft()) {
				updated_uncle = updated_grandparent.getRight();
			} else {
				updated_uncle = updated_grandparent.getLeft();
			}
			
			if (updated_uncle != null && !updated_uncle.flag.compareAndSet(false, true)) {
				updated_grandparent.flag.set(false);
				updated_parent.flag.set(false);
				continue;
			}
			break;
		}
		
		working.add(updated_x);
		working.add(updated_parent);
		working.add(updated_grandparent);
		working.add(updated_uncle);
		
		return updated_x;	
	}
	
	private  void leftRotate(LockFreeRBNode x) {
		if (x == null) return;
		LockFreeRBNode y = x.getRight();
		x.setRight(y.getLeft());
		if (y.getLeft() != null) {
			y.getLeft().setParent(x);
		}
		y.setParent(x.getParent());
		if (x.getParent() == null) this.root = y;
		else{
			if (x == x.getParent().getLeft())
				x.getParent().setLeft(y);
			else
				x.getParent().setRight(y);
		}
		y.setLeft(x);
		x.setParent(y);
	}
	
	private void rightRotate(LockFreeRBNode y) {
		if (y == null) return;
		LockFreeRBNode x = y.getLeft();
		y.setLeft(x.getRight());
		if (x.getRight() != null) {
			x.getRight().setParent(y);
		}
		x.setParent(y.getParent());
		if (y.getParent() == null) this.root = x;
		else{
			if (y == y.getParent().getLeft())
				y.getParent().setLeft(x);
			else
				y.getParent().setRight(x);
		}
		x.setRight(y);
		y.setParent(x);
	}
	
	public synchronized void print() {
		List<List<String>> res = new LinkedList<List<String>>();
		res = printHelp(root,0,res);
		//int id = ((test.testThread<V>)Thread.currentThread()).id;
		//System.out.println("Thread "+id+"printing:");
		for (List<String> list:res) {
			for (String word: list) {
				System.out.print(word+" ");
			}
			System.out.print("\n");
		}
	}
	
	protected synchronized List<List<String>> printHelp(LockFreeRBNode root,int height,List<List<String>> res) {
		if (root == null) return res;
		List<String> list;
		if (height >= res.size()) {
			list = new LinkedList<String>();
			res.add(list);
		} else {
			list = res.get(height);
		}
		if (root.getValue() < 0) {
			list.add(" _ ");
		} else {
			list.add(root.getValue()+(root.isRed()?"_R":"_B")+(root.flag.get()?"T":"F"));
		}
		printHelp(root.getLeft(),height+1,res);
		printHelp(root.getRight(),height+1,res);
		return res;
	}
	
	
} 
