package com.tinker.file.manager.listener;

import com.tinker.file.manager.bean.FileNode;
import com.tinker.file.manager.util.FileUtil;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;

/**
 * FileTreeListener
 *
 * @author Tinker Chen
 * @date 2019/2/25
 */
public class FileTreeListener {

//    private static List<String> computerNames = new ArrayList(Arrays.asList(new String[]{"我的电脑", "此电脑"}));

    private JTree fileTree;
    private JList<FileNode> fileList;
    private NavigationListener navigationListener;

    public FileTreeListener(JTree fileTree, JList<FileNode> fileList, NavigationListener navigationListener) {
        this.fileTree = fileTree;
        this.fileList = fileList;
        this.navigationListener = navigationListener;
    }

    public void addListener() {
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

                        if (file.isDirectory()) {
                            //展示导航栏地址
                            navigationListener.showNavigation(file, true);

                            //展示右侧文件list
                            FileUtil.showFileList(fileList, file);
                        }
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
                //移除子节点，重新加载
                lastTreeNode.removeAllChildren();
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
                    for (File file : files) {
                        //目录树只展示文件夹
                        if (file.isDirectory()) {
                            FileNode childFileNode = new FileNode(
                                    fileSystemView.getSystemDisplayName(file),
                                    fileSystemView.getSystemIcon(file), file,
                                    false);
                            DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(childFileNode);
                            lastTreeNode.add(childTreeNode);
                        }
                    }
                    //通知模型节点发生变化
                    DefaultTreeModel treeModel1 = (DefaultTreeModel) fileTree.getModel();
                    treeModel1.nodeStructureChanged(lastTreeNode);
                }
                //更改标识，避免重复加载(新增节点后需要重新加载)
                fileNode.isInit = false;
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

            }
        });
    }
}
