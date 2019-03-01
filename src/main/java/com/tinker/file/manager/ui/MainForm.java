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
import javax.swing.plaf.FontUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.Enumeration;

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
    private JPopupMenu blankJPopupMenu = new JPopupMenu();

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

                initGlobalFont();
            }
        });
    }

    /**
     * 设置控件统一字体
     */
    private static void initGlobalFont() {
        Font font = new Font("Dialog", Font.PLAIN, 12);
        FontUIResource fontRes = new FontUIResource(font);
        for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontRes);
            }
        }
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
        FileListListener fileListListener = new FileListListener(fileList, jPopupMenu, diskJPopupMenu, blankJPopupMenu,
                navigationListener);
        fileListListener.addListener();

        //右侧文件类别右键菜单监听事件
        JPopupMenuListener rightKeyMenuListener = new JPopupMenuListener(fileList, jPopupMenu, diskJPopupMenu, blankJPopupMenu,
                navigationListener);
        rightKeyMenuListener.addListener();
    }

    /**
     * IDEA 提供的自定义控件声明，不让IDEA自动创建控件对象，可以在此方法中由用户自己创建控件对象
     * 默认方法名不可修改
     */
    private void createUIComponents() {
        fileList = new JList() {

            @Override
            public int locationToIndex(Point location) {
                //点击空白处不选择最后一条数据
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
