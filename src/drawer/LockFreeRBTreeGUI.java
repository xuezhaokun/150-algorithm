// Reference :https://github.com/EslaMx7/AI-Tasks-JADE-Tests/blob/master/src/trees/tasks/treeGUI.java

package drawer;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import parallel_rbtree.LockFreeRBNode;
import parallel_rbtree.LockFreeRBTree;

public class LockFreeRBTreeGUI extends JFrame {

	private JPanel contentPane;
	public LockFreeRBTree tree;
	public DrawLockFreeTree drawer;
	
	/**
	 * Create the frame.
	 */
	public LockFreeRBTreeGUI(LockFreeRBTree tree) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 1000);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.setBorder(BorderFactory.createTitledBorder("Lock-Free Tree"));
		drawer = new DrawLockFreeTree(tree);
		
		contentPane.add(drawer);
		setContentPane(contentPane);
		this.tree = tree;
		setVisible(true);
	}
}

class DrawLockFreeTree extends JPanel{
	
	public LockFreeRBTree tree;
	
	public DrawLockFreeTree(LockFreeRBTree tree){
		this.tree = tree;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
	
		g.setFont(new Font("Tahoma", Font.BOLD, 10));
		//g.drawString(String.valueOf(tree.root.data), this.getWidth()/2, 30);
		

			//DrawNode(g, tree.root,100, 50,2);

		DrawLockFreeTree(g, 0, getWidth(), 0, getHeight() / tree.getheight(tree.root), tree.root);
	}
	

	
    public void DrawLockFreeTree(Graphics g, int StartWidth, int EndWidth, int StartHeight, int Level, LockFreeRBNode node) {
        String data = "";
        if (node == null || node.getValue() < 0) {
    			data = "nil";
    		} else {
    			data = String.valueOf(node.getValue());
    		}
        
        g.setFont(new Font("Tahoma", Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();
        int dataWidth = fm.stringWidth(data);
        int ovalSize = 40;
        if (node.isRed()) {
        		g.setColor(Color.red);
        } else {
        		g.setColor(Color.black);
        }
        g.fillOval((StartWidth + EndWidth) / 2 - 20, StartHeight + Level/3,ovalSize,ovalSize);
        if (node.isRed()) {
    			g.setColor(Color.black);
	    } else {
	    		g.setColor(Color.white);
	    }
        g.drawString(data, (StartWidth + EndWidth) / 2 - dataWidth / 2, StartHeight + Level / 2);
        g.setColor(Color.black);
        if (node.getLeft() != null){
        		int newEndWidth = (StartWidth + EndWidth) / 2;
        		int newStartHeight = StartHeight + Level;
        		
        		g.drawLine((StartWidth + EndWidth) / 2 - dataWidth / 2, StartHeight + Level / 2 + 2, (StartWidth + newEndWidth) / 2 - dataWidth / 2, newStartHeight + Level / 2 - 2);
        		DrawLockFreeTree(g, StartWidth, (StartWidth + EndWidth) / 2, StartHeight + Level, Level, node.getLeft());
        }
        
        if (node.getRight() != null){
	        	int newStartWidth = (StartWidth + EndWidth) / 2;
	    		int newStartHeight = StartHeight + Level;
	    		g.drawLine((StartWidth + EndWidth) / 2 - dataWidth / 2, StartHeight + Level / 2 + 2, (newStartWidth + EndWidth) / 2 - dataWidth / 2, newStartHeight + Level / 2 - 2);
        		DrawLockFreeTree(g, (StartWidth + EndWidth) / 2, EndWidth, StartHeight + Level, Level, node.getRight());
        }
    }
	
}
