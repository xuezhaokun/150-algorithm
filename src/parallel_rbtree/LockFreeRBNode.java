package parallel_rbtree;

import java.util.concurrent.atomic.AtomicBoolean;

public class LockFreeRBNode {
	private Integer value;
	private LockFreeRBNode left;
	private LockFreeRBNode right;
	private LockFreeRBNode parent;
	private boolean isRed;
	public AtomicBoolean flag;
	
	public LockFreeRBNode() {
		this.value = null;
		this.left = null;
		this.right = null;
		this.parent = null;
		this.isRed = false;
		this.flag = new AtomicBoolean(false);
	}
	
	public LockFreeRBNode(int value) {
		this.value = value;
		this.left = null;
		this.right = null;
		this.parent = null;
		this.isRed = true;
		this.flag = new AtomicBoolean(false);
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public LockFreeRBNode getLeft() {
		return left;
	}

	public void setLeft(LockFreeRBNode left) {
		this.left = left;
	}

	public LockFreeRBNode getRight() {
		return right;
	}

	public void setRight(LockFreeRBNode right) {
		this.right = right;
	}

	public LockFreeRBNode getParent() {
		return parent;
	}

	public void setParent(LockFreeRBNode parent) {
		this.parent = parent;
	}

	public boolean isRed() {
		return isRed;
	}

	public void setRed(boolean isRed) {
		this.isRed = isRed;
	}
	
}
