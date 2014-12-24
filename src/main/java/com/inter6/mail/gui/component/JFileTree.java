package com.inter6.mail.gui.component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class JFileTree extends JTree {
	private static final long serialVersionUID = -8962517684111301582L;

	public JFileTree(File rootDir) throws FileNotFoundException {
		super(new FileSystemModel(rootDir));
	}
}

class FileSystemModel implements TreeModel {
	private final File rootDir;
	private final Vector<TreeModelListener> listeners = new Vector<TreeModelListener>();

	public FileSystemModel(File rootDir) throws FileNotFoundException {
		if (rootDir == null || !rootDir.exists() || !rootDir.isDirectory()) {
			throw new FileNotFoundException("");
		}
		this.rootDir = rootDir;
	}

	@Override
	public Object getRoot() {
		return this.rootDir;
	}

	@Override
	public Object getChild(Object parent, int index) {
		File directory = (File) parent;
		String[] children = directory.list();
		return new TreeFile(directory, children[index]);
	}

	@Override
	public int getChildCount(Object parent) {
		File file = (File) parent;
		if (file.isDirectory()) {
			String[] fileList = file.list();
			if (fileList != null) {
				return file.list().length;
			}
		}
		return 0;
	}

	@Override
	public boolean isLeaf(Object node) {
		File file = (File) node;
		return file.isFile();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		File directory = (File) parent;
		File file = (File) child;
		String[] children = directory.list();
		for (int i = 0; i < children.length; i++) {
			if (file.getName().equals(children[i])) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object value) {
		File oldFile = (File) path.getLastPathComponent();
		String fileParentPath = oldFile.getParent();
		String newFileName = (String) value;
		File targetFile = new File(fileParentPath, newFileName);
		oldFile.renameTo(targetFile);
		File parent = new File(fileParentPath);
		int[] changedChildrenIndices = { this.getIndexOfChild(parent, targetFile) };
		Object[] changedChildren = { targetFile };
		this.fireTreeNodesChanged(path.getParentPath(), changedChildrenIndices, changedChildren);

	}

	private void fireTreeNodesChanged(TreePath parentPath, int[] indices, Object[] children) {
		TreeModelEvent event = new TreeModelEvent(this, parentPath, indices, children);
		Iterator<TreeModelListener> iterator = this.listeners.iterator();
		TreeModelListener listener = null;
		while (iterator.hasNext()) {
			listener = iterator.next();
			listener.treeNodesChanged(event);
		}
	}

	@Override
	public void addTreeModelListener(TreeModelListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener listener) {
		this.listeners.remove(listener);
	}

	private class TreeFile extends File {
		private static final long serialVersionUID = 6507284871083417340L;

		public TreeFile(File parent, String child) {
			super(parent, child);
		}

		@Override
		public String toString() {
			return this.getName();
		}
	}
}
