package com.tinker.file.manager.bean;

import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.io.File;

/**
 * FileTreeModel
 *
 * @author Tinker Chen
 * @date 2019/2/15
 */
public class FileTreeModel extends DefaultTreeModel {

    public FileTreeModel(TreeNode root) {
        super(root);
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        File[] files = fileSystemView.getFiles(fileSystemView.getHomeDirectory(), true);
        for (int i = 0; i < 1; i++) {
            FileNode fileNode = new FileNode(fileSystemView.getSystemDisplayName(files[i]),
                    fileSystemView.getSystemIcon(files[i]), files[i], false);
            DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(fileNode);
            ((DefaultMutableTreeNode)root).add(childTreeNode);
        }
    }
    @Override
    public boolean isLeaf(Object node) {
        DefaultMutableTreeNode treeNode=(DefaultMutableTreeNode)node;
        FileNode fileNode=(FileNode)treeNode.getUserObject();
        if(fileNode.isDummyRoot) {
            return false;
        }
        return fileNode.file.isFile();
    }
}