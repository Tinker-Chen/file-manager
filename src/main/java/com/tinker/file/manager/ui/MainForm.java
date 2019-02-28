package com.tinker.file.manager.ui;

import com.tinker.file.manager.bean.FileNode;
import com.tinker.file.manager.bean.FileTreeModel;
import com.tinker.file.manager.bean.FileTreeRenderer;
import com.tinker.file.manager.listener.FileListListener;
import com.tinker.file.manager.listener.FileTreeListener;
import com.tinker.file.manager.listener.JPopupMenuListener;
import com.tinker.file.manager.listener.NavigationListener;
import com.tinker.file.manager.util.FileUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

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
    private JPanel navigationPanel;
    private JLabel backward;
    private JLabel forward;
    private JLabel refresh;
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
                //修改GUI左上角图标
                frame.setIconImage(FileUtil.getImage(this, "images/folder.png"));
            }
        });
    }

    private void init() {
        //构造左边文件树
        FileTreeModel model = new FileTreeModel(new DefaultMutableTreeNode(new FileNode("root", null,
                null, true)));
        fileTree.setModel(model);
        fileTree.setCellRenderer(new FileTreeRenderer());

        //导航栏监听事件
        NavigationListener navigationListener = new NavigationListener(navigationTextField, forward, backward, refresh, fileList);
        navigationListener.addListener();

        //添加文件目录树的监听事件
        FileTreeListener fileTreeListener = new FileTreeListener(fileTree, fileList, navigationListener);
        fileTreeListener.addListener();

        //右侧文件列表监听事件
        FileListListener fileListListener = new FileListListener(fileList, jPopupMenu, diskJPopupMenu, navigationListener);
        fileListListener.addListener();

        //右侧文件类别右键菜单监听事件
        JPopupMenuListener rightKeyMenuListener = new JPopupMenuListener(fileList, jPopupMenu, diskJPopupMenu, navigationListener);
        rightKeyMenuListener.addListener();
    }

    /**
     * IDEA 提供的自定义控件声明，不让IDEA自动创建控件对象，由用户自己创建
     * 默认方法名不可修改
     */
    private void createUIComponents() {
        // TODO: place custom component creation code here
        fileList = new JList() {

            @Override
            public int locationToIndex(Point location) {
                int index = super.locationToIndex(location);
                if (index != -1 && !getCellBounds(index, index).contains(location)) {
                    return -1;
                }
                else {
                    return index;
                }
            }
        };
    }
}
