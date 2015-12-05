package dyvil.tools.installer.ui;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;

public class JCheckBoxTree extends JTree
{

	private class CheckedNode
	{
		boolean	isSelected;
		boolean	hasChildren;
		boolean	allChildrenSelected;
				
		public CheckedNode(boolean isSelected, boolean hasChildren, boolean allChildrenSelected)
		{
			this.isSelected = isSelected;
			this.hasChildren = hasChildren;
			this.allChildrenSelected = allChildrenSelected;
		}
	}
	
	public class CheckChangeEvent extends EventObject
	{
		private static final long serialVersionUID = -8100230309044193368L;
		
		public CheckChangeEvent(Object source)
		{
			super(source);
		}
	}
	
	public interface CheckChangeEventListener extends EventListener
	{
		public void checkStateChanged(CheckChangeEvent event);
	}
	
	// Overriding cell renderer by a class that ignores the original "selection"
	// mechanism
	// It decides how to show the nodes due to the checking-mechanism
	private class CheckBoxCellRenderer extends JPanel implements TreeCellRenderer
	{
		private static final long	serialVersionUID	= -7341833835878991719L;
		JCheckBox					checkBox;
									
		public CheckBoxCellRenderer()
		{
			super();
			this.checkBox = new JCheckBox();
			this.setLayout(new BorderLayout());
			
			this.add(this.checkBox, BorderLayout.CENTER);
			this.setOpaque(false);
		}
		
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object obj = node.getUserObject();
			TreePath tp = new TreePath(node.getPath());
			CheckedNode cn = JCheckBoxTree.this.nodesCheckingState.get(tp);
			if (cn == null)
			{
				return this;
			}
			
			this.checkBox.setSelected(cn.isSelected);
			this.checkBox.setText(obj.toString());
			this.checkBox.setSize(100, 20);
			this.checkBox.setVisible(true);
			this.checkBox.setBackground(GUI.TRANSPARENT);
			return this.checkBox;
		}
	}
	
	private static final long		serialVersionUID	= -4194122328392241790L;
														
	HashMap<TreePath, CheckedNode>	nodesCheckingState;
	HashSet<TreePath>				checkedPaths		= new HashSet<TreePath>();
														
	// Defining a new event type for the checking mechanism and preparing
	// event-handling mechanism
	protected EventListenerList		listenerList		= new EventListenerList();
														
	public JCheckBoxTree()
	{
		super();
		// Disabling toggling by double-click
		this.setToggleClickCount(0);
		// Overriding cell renderer by new one defined above
		CheckBoxCellRenderer cellRenderer = new CheckBoxCellRenderer();
		this.setCellRenderer(cellRenderer);
		
		// Overriding selection model by an empty one
		DefaultTreeSelectionModel dtsm = new DefaultTreeSelectionModel()
		{
			private static final long serialVersionUID = -8190634240451667286L;
			
			// Totally disabling the selection mechanism
			@Override
			public void setSelectionPath(TreePath path)
			{
			}
			
			@Override
			public void addSelectionPath(TreePath path)
			{
			}
			
			@Override
			public void removeSelectionPath(TreePath path)
			{
			}
			
			@Override
			public void setSelectionPaths(TreePath[] pPaths)
			{
			}
		};
		
		// Calling checking mechanism on mouse click
		this.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				TreePath tp = JCheckBoxTree.this.getPathForLocation(event.getX(), event.getY());
				if (tp == null)
				{
					return;
				}
				boolean checkMode = !JCheckBoxTree.this.nodesCheckingState.get(tp).isSelected;
				JCheckBoxTree.this.checkSubTree(tp, checkMode);
				JCheckBoxTree.this.updatePredecessorsWithCheckMode(tp, checkMode);
				// Firing the check change event
				JCheckBoxTree.this.fireCheckChangeEvent(new CheckChangeEvent(new Object()));
				// Repainting tree after the data structures were updated
				JCheckBoxTree.this.repaint();
			}
		});
		this.setSelectionModel(dtsm);
	}
	
	public void addCheckChangeEventListener(CheckChangeEventListener listener)
	{
		this.listenerList.add(CheckChangeEventListener.class, listener);
	}
	
	public void removeCheckChangeEventListener(CheckChangeEventListener listener)
	{
		this.listenerList.remove(CheckChangeEventListener.class, listener);
	}
	
	void fireCheckChangeEvent(CheckChangeEvent evt)
	{
		Object[] listeners = this.listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i++)
		{
			if (listeners[i] == CheckChangeEventListener.class)
			{
				((CheckChangeEventListener) listeners[i + 1]).checkStateChanged(evt);
			}
		}
	}
	
	@Override
	public void setModel(TreeModel newModel)
	{
		super.setModel(newModel);
		this.resetCheckingState();
	}
	
	// New method that returns only the checked paths (totally ignores original
	// "selection" mechanism)
	public TreePath[] getCheckedPaths()
	{
		return this.checkedPaths.toArray(new TreePath[this.checkedPaths.size()]);
	}
	
	// Returns true in case that the node is selected, has children but not all
	// of them are selected
	public boolean isSelectedPartially(TreePath path)
	{
		CheckedNode cn = this.nodesCheckingState.get(path);
		return cn.isSelected && cn.hasChildren && !cn.allChildrenSelected;
	}
	
	private void resetCheckingState()
	{
		this.nodesCheckingState = new HashMap<TreePath, CheckedNode>();
		this.checkedPaths = new HashSet<TreePath>();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.getModel().getRoot();
		if (node == null)
		{
			return;
		}
		this.addSubtreeToCheckingStateTracking(node);
	}
	
	// Creating data structure of the current model for the checking mechanism
	private void addSubtreeToCheckingStateTracking(DefaultMutableTreeNode node)
	{
		TreeNode[] path = node.getPath();
		TreePath tp = new TreePath(path);
		CheckedNode cn = new CheckedNode(false, node.getChildCount() > 0, false);
		this.nodesCheckingState.put(tp, cn);
		for (int i = 0; i < node.getChildCount(); i++)
		{
			this.addSubtreeToCheckingStateTracking((DefaultMutableTreeNode) tp.pathByAddingChild(node.getChildAt(i)).getLastPathComponent());
		}
	}
	
	// When a node is checked/unchecked, updating the states of the predecessors
	protected void updatePredecessorsWithCheckMode(TreePath tp, boolean check)
	{
		TreePath parentPath = tp.getParentPath();
		// If it is the root, stop the recursive calls and return
		if (parentPath == null)
		{
			return;
		}
		CheckedNode parentCheckedNode = this.nodesCheckingState.get(parentPath);
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
		parentCheckedNode.allChildrenSelected = true;
		parentCheckedNode.isSelected = false;
		for (int i = 0; i < parentNode.getChildCount(); i++)
		{
			TreePath childPath = parentPath.pathByAddingChild(parentNode.getChildAt(i));
			CheckedNode childCheckedNode = this.nodesCheckingState.get(childPath);
			// It is enough that even one subtree is not fully selected
			// to determine that the parent is not fully selected
			if (!childCheckedNode.allChildrenSelected)
			{
				parentCheckedNode.allChildrenSelected = false;
			}
			// If at least one child is selected, selecting also the parent
			if (childCheckedNode.isSelected)
			{
				parentCheckedNode.isSelected = true;
			}
		}
		if (parentCheckedNode.isSelected)
		{
			this.checkedPaths.add(parentPath);
		}
		else
		{
			this.checkedPaths.remove(parentPath);
		}
		// Go to upper predecessor
		this.updatePredecessorsWithCheckMode(parentPath, check);
	}
	
	// Recursively checks/unchecks a subtree
	protected void checkSubTree(TreePath tp, boolean check)
	{
		CheckedNode cn = this.nodesCheckingState.get(tp);
		cn.isSelected = check;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();
		for (int i = 0; i < node.getChildCount(); i++)
		{
			this.checkSubTree(tp.pathByAddingChild(node.getChildAt(i)), check);
		}
		cn.allChildrenSelected = check;
		if (check)
		{
			this.checkedPaths.add(tp);
		}
		else
		{
			this.checkedPaths.remove(tp);
		}
	}
}
