package com.tinker.file.manager.ui;

import com.tinker.file.manager.bean.FileNode;
import com.tinker.file.manager.bean.FileTreeModel;
import com.tinker.file.manager.bean.FileTreeRenderer;
import com.tinker.file.manager.util.FileUtil;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private JPopupMenu jPopupMenu;
    private JPopupMenu driveJPopupMenu;

    private String PADDING_STRING = "     ";

    private List<String> computerNames = new ArrayList(Arrays.asList(new String[]{"我的电脑", "此电脑"}));

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

        //文件树监听事件
        final FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        fileTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                super.mouseClicked(event);
                if (event.getClickCount() == MouseEvent.BUTTON1) {
                    JTree jt = (JTree) event.getComponent();
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) jt.getLastSelectedPathComponent();
                    if (treeNode != null) {
                        FileNode fileNode = (FileNode) treeNode.getUserObject();
                        File file = fileNode.getFile();

                        //展示导航栏地址
                        if (!computerNames.contains(file.toString())) {
                            navigationTextField.setText(file.getAbsolutePath());
                        }

                        //展示右侧文件list
                        FileUtil.showFileList(fileList, file);
                    }
                }
            }
        });
        fileTree.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
            }
        });
        fileTree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                DefaultMutableTreeNode lastTreeNode = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
                FileNode fileNode = (FileNode) lastTreeNode.getUserObject();
                if (!fileNode.isInit) {
                    File[] files;
                    if (fileNode.isDummyRoot) {
                        files = fileSystemView.getRoots();
                    } else {
                        files = fileSystemView.getFiles(
                                ((FileNode) lastTreeNode.getUserObject()).file,
                                false);
                    }
                    for (int i = 0; i < files.length; i++) {
                        FileNode childFileNode = new FileNode(
                                fileSystemView.getSystemDisplayName(files[i]),
                                fileSystemView.getSystemIcon(files[i]), files[i],
                                false);
                        DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(childFileNode);
                        lastTreeNode.add(childTreeNode);
                    }
                    //通知模型节点发生变化
                    DefaultTreeModel treeModel1 = (DefaultTreeModel) fileTree.getModel();
                    treeModel1.nodeStructureChanged(lastTreeNode);
                }
                //更改标识，避免重复加载
                fileNode.isInit = true;
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

            }
        });
    }

    private void generateFileList() {

        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //点击非空白处
                if (fileList.getSelectedIndex() != -1) {
                    FileNode fileNode = fileList.getSelectedValue();
                    File file = fileNode != null ? fileNode.getFile() : null;

                    //单击list
                    if (e.getClickCount() == MouseEvent.BUTTON1) {
                        if (file != null) {
                            //导航栏
                            navigationTextField.setText(file.getAbsolutePath());
                        }
                    } else if (e.getClickCount() == MouseEvent.BUTTON2) {
                        //双击list时，打开文件或进入该子目录
                        if (file != null) {
                            if (file.isDirectory()) {
                                FileUtil.showFileList(fileList, file);
                            } else if (file.isFile()) {
                                FileUtil.openFile(file);
                            }
                        }
                    }
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        //右击list时，打开菜单栏
                        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
                        File f = FileUtil.getSelectedFile(fileList);
                        if (f != null) {
                            if (fileSystemView.isDrive(f)) {
                                driveJPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                            } else {
                                jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                            }
                        }
                    }
                } else {
                    //TODO 点击空白处
                }
            }
        });
    }

    private void generateRightKeyMenu() {
        //右键文件菜单
        jPopupMenu = new JPopupMenu();
        JMenuItem openItem = new JMenuItem(PADDING_STRING + "打开");
        JMenuItem renameItem = new JMenuItem(PADDING_STRING + "重命名");
        JMenuItem deleteItem = new JMenuItem(PADDING_STRING + "删除");
        JMenuItem propertyItem = new JMenuItem(PADDING_STRING + "属性");
        jPopupMenu.add(openItem);
        jPopupMenu.addSeparator();
        jPopupMenu.add(renameItem);
        jPopupMenu.add(deleteItem);
        jPopupMenu.addSeparator();
        jPopupMenu.add(propertyItem);
        //右键磁盘菜单
        driveJPopupMenu = new JPopupMenu();
        JMenuItem openItem1 = new JMenuItem(PADDING_STRING + "打开");
        driveJPopupMenu.add(openItem1);

        fileList.add(jPopupMenu);
        fileList.add(driveJPopupMenu);

        //添加右键菜单“打开”监听器
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileUtil.fileOpenHandler(fileList);
            }
        });
        openItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileUtil.fileOpenHandler(fileList);
            }
        });
        //添加右键菜单“重命名”监听器
        renameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = FileUtil.getSelectedFile(fileList);
                if (file != null) {
                    String fileName = (String) JOptionPane.showInputDialog(null, "请输入新文件夹名:\n", "重命名",
                            JOptionPane.PLAIN_MESSAGE, null, null, file.getName());
                    if (fileName != null && !fileName.equals(file.getName())) {
                        //重命名
                        file.renameTo(new File(file.getParent() + File.separator + fileName));
                        //刷新文件名
                        FileUtil.showFileList(fileList, file.getParentFile());
                    }
                }
            }
        });
        //添加右键菜单“删除”监听器
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = FileUtil.getSelectedFile(fileList);
                if (file != null) {
                    file.delete();
                    //刷新
                    FileUtil.showFileList(fileList, file.getParentFile());
                }
            }
        });
        //添加右键菜单“属性”监听器
        propertyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = FileUtil.getSelectedFile(fileList);
                if (file != null) {
                    if (file.isFile()) {
                        new FilePropertyForm(fileList);
                    } else {
                        new DirectoryPropertyForm(fileList);
                    }
                }
            }
        });
    }

}
