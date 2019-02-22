package com.tinker.file.manager.bean;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * FileTreeRenderer
 *
 * @author Tinker Chen
 * @date 2019/2/15
 */
public class FileTreeRenderer extends DefaultTreeCellRenderer {

    public FileTreeRenderer(){
    }

    @Override
    public Component getTreeCellRendererComponent(javax.swing.JTree tree,
                                                  java.lang.Object value,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus){

//        FileTree fileTree=(FileTree)tree;
        JLabel label= (JLabel) super.getTreeCellRendererComponent(tree,value,sel,expanded,leaf,row,hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        FileNode fileNode = (FileNode) node.getUserObject();
        label.setText(fileNode.name);
        label.setIcon(fileNode.icon);

        label.setOpaque(false);
//        if(fileTree.mouseInPath!=null&&
//                fileTree.mouseInPath.getLastPathComponent().equals(value)){
//            label.setOpaque(true);
//            label.setBackground(new Color(255,0,0,90));
//        }
        return label;
    }
}
