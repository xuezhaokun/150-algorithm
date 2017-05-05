package parallel_rbtree;

import java.util.LinkedList;
import java.util.List;

public class LockedRBTree implements RBTree{
	int size = 0;
	LockedRBNode root;
	
	public LockedRBTree() {
		this.root = null;
	}
	
//	public LockedRBNode find(int value) {
//		if (root == null) return null;
//		LockedRBNode temp = root;
//		while(temp != null && temp.getValue() != null) {
//			if (value < temp.getValue()) {
//				temp = temp.getLeft();
//			} else if (value > temp.getValue()) {
//				temp = temp.getRight();
//			} else {
//				return temp;
//			}
//		}
//		return temp;
//	}
//	
	public synchronized Integer search (int value) {
		if (root == null) return null;
		LockedRBNode temp = root;
		while(temp != null && temp.getValue() != null) {
			if (value < temp.getValue()) {
				temp = temp.getLeft();
			} else if (value > temp.getValue()) {
				temp = temp.getRight();
			} else {
				return temp.getValue();
			}
		}
		return temp==null? null : temp.getValue();
	}
	
	public synchronized void insert (int value) {
		LockedRBNode physicallyInsertedNode = physicallyInsert(value);
		rbInsertFixup(physicallyInsertedNode);
	}
	
	private void rbInsertFixup(LockedRBNode x) {
		while (!x.equals(this.root) && x.getParent().isRed()) {
			if (x.getParent().equals(x.getParent().getParent().getLeft())) {
				LockedRBNode right_uncle = x.getParent().getParent().getRight();
				if (right_uncle.isRed()) {
					// case 1
					x.getParent().setRed(false);
					right_uncle.setRed(false);
					x.getParent().getParent().setRed(true);
					x = x.getParent().getParent();
				} else {
					// case 2
					if (x.equals(x.getParent().getRight())) {
						x = x.getParent();
						leftRotation(x);
					}
					// case 3
					x.getParent().setRed(false);
					x.getParent().getParent().setRed(true);
					rightRotation(x.getParent().getParent());
				}
			} else {
				LockedRBNode left_uncle = x.getParent().getParent().getLeft();
				if (left_uncle.isRed()) {
					// case 1
					x.getParent().setRed(false);
					left_uncle.setRed(false);
					x.getParent().getParent().setRed(true);
					x = x.getParent().getParent();
				} else {
					// case 2
					if (x.equals(x.getParent().getLeft())) {
						x = x.getParent();
						rightRotation(x);
					}
					// case 3
					x.getParent().setRed(false);
					x.getParent().getParent().setRed(true);
					leftRotation(x.getParent().getParent());
				}
			}
		}
		this.root.setRed(false);
	}
	
	private LockedRBNode physicallyInsert (int value) {
		LockedRBNode insertedNode = new LockedRBNode(value);
		if (root == null || root.getValue() == null) {
			root = insertedNode;
			root.setRed(false);
			insertedNode.setLeft(new LockedRBNode());
			insertedNode.setRight(new LockedRBNode());
			insertedNode.getLeft().setParent(insertedNode);
			insertedNode.getRight().setParent(insertedNode);
			return insertedNode;
		}
		
		LockedRBNode temp = root;
		while(temp.getValue() != null) {
			if (value < temp.getValue()) {
				temp = temp.getLeft();
			} else if (value > temp.getValue()) {
				temp = temp.getRight();
			} else {
				return null;
			}
		}
		if (temp.equals(temp.getParent().getLeft())) {
			temp.getParent().setLeft(insertedNode);
		} else {
			temp.getParent().setRight(insertedNode);
		}
		insertedNode.setParent(temp.getParent());
		insertedNode.setLeft(new LockedRBNode());
		insertedNode.setRight(new LockedRBNode());
		insertedNode.getLeft().setParent(insertedNode);
		insertedNode.getRight().setParent(insertedNode);
		return insertedNode;
	}
	
	private void leftRotation(LockedRBNode x) {
		if (x == null) return;
		LockedRBNode y = x.getRight();
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
	
	private void rightRotation(LockedRBNode y) {
		if (y == null) return;
		LockedRBNode x = y.getLeft();
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
	
	public void print(){
		List<List<String>> res = new LinkedList<List<String>>();
		res = printHelp(root,0,res);
		for(List<String> list:res){
			for(String word: list){
				System.out.print(word+" ");
			}
			System.out.print("\n");
		}
	}
	
	protected List<List<String>> printHelp(LockedRBNode root,int height,List<List<String>> res){
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
			list.add(root.getValue() + (root.isRed()?"_R":"_B"));
		}
		printHelp(root.getLeft(),height+1,res);
		printHelp(root.getRight(),height+1,res);
		return res;
	}

	
}
