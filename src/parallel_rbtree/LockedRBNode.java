package parallel_rbtree;

public class LockedRBNode {
	private Integer value;
	private LockedRBNode left;
	private LockedRBNode right;
	private LockedRBNode parent;
	private boolean isRed;
	
	public LockedRBNode() {
		this.value = null;
		this.left = null;
		this.right = null;
		this.parent = null;
		this.isRed = false;
	}
	
	public LockedRBNode(int value) {
		this.value = value;
		this.left = null;
		this.right = null;
		this.parent = null;
		this.isRed = true;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public LockedRBNode getLeft() {
		return left;
	}

	public void setLeft(LockedRBNode left) {
		this.left = left;
	}

	public LockedRBNode getRight() {
		return right;
	}

	public void setRight(LockedRBNode right) {
		this.right = right;
	}

	public LockedRBNode getParent() {
		return parent;
	}

	public void setParent(LockedRBNode parent) {
		this.parent = parent;
	}

	public boolean isRed() {
		return isRed;
	}

	public void setRed(boolean isRed) {
		this.isRed = isRed;
	}
	
}
