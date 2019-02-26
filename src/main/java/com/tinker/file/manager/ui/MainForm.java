package com.tinker.file.manager.ui;

import com.tinker.file.manager.bean.FileNode;
import com.tinker.file.manager.bean.FileTreeModel;
import com.tinker.file.manager.bean.FileTreeRenderer;
import com.tinker.file.manager.listener.FileListListener;
import com.tinker.file.manager.listener.FileTreeListener;
import com.tinker.file.manager.listener.JPopupMenuListener;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * MainForm
 *
 * @author Tinker Chen
 * @date 2019/2/15
 */
public class MainForm {
    private JPanel mainJPanel;
    private JScrollPane leftScrollPane;
    private JScrollPane rightScrollPane;
    private JTree fileTree;
    private JList<FileNode> fileList;
    private JTextField navigationTextField;
    private JLabel navigationLabel;
    private JPopupMenu jPopupMenu = new JPopupMenu();
    private JPopupMenu diskJPopupMenu = new JPopupMenu();

    private MainForm() {

        this.init();

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("资源管理器");
                frame.setContentPane(new MainForm().mainJPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                //是否可见
                frame.setVisible(true);
                //设置JFrame的长和宽
                frame.setSize(800, 600);
                //设置JFrame是否可以改变大小
                frame.setResizable(false);
                //窗口居中显示
                frame.setLocationRelativeTo(null);
            }
        });
    }

    private void init() {
        this.generateFileTree();
        this.generateFileList();
        this.generateRightKeyMenu();
    }

    private void generateFileTree() {
        //构造左边文件树
        FileTreeModel model = new FileTreeModel(new DefaultMutableTreeNode(new FileNode("root", null,
                null, true)));
        fileTree.setModel(model);
        fileTree.setCellRenderer(new FileTreeRenderer());

        //添加文件目录树的监听事件
        FileTreeListener listener = new FileTreeListener(fileTree, fileList, navigationTextField);
        listener.addListener();
    }

    private void generateFileList() {
        FileListListener listener = new FileListListener(fileList, navigationTextField, jPopupMenu, diskJPopupMenu);
        listener.addListener();
    }

    private void generateRightKeyMenu() {
        JPopupMenuListener listener = new JPopupMenuListener(fileList, jPopupMenu, diskJPopupMenu);
        listener.addListener();
    }

}
