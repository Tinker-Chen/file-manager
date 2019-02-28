package com.tinker.file.manager.listener;

import com.tinker.file.manager.bean.FileNode;
import com.tinker.file.manager.util.FileUtil;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.*;
import java.io.File;

/**
 * FileListListener
 *
 * @author Tinker Chen
 * @date 2019/2/25
 */
public class FileListListener {

    private JList<FileNode> fileList;
    private JPopupMenu jPopupMenu;
    private JPopupMenu diskJPopupMenu;
    private NavigationListener navigationListener;

    private static final String DISK_SUFFIX = ":\\";

    public FileListListener(JList<FileNode> fileList, JPopupMenu jPopupMenu, JPopupMenu diskJPopupMenu,
                            NavigationListener navigationListener) {
        this.fileList = fileList;
        this.jPopupMenu = jPopupMenu;
        this.diskJPopupMenu = diskJPopupMenu;
        this.navigationListener = navigationListener;
    }

    public void addListener() {
        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //点击非空白处
                if (fileList.getSelectedIndex() != -1) {
                    //单击
                    if (e.getClickCount() == MouseEvent.BUTTON1) {
                        //do nothing
                    }
                    //双击list时，打开文件或进入该子目录
                    if (e.getClickCount() == MouseEvent.BUTTON2) {
                        openFolderOrFile();
                    }
                    //右击list时，打开菜单栏
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
                        File f = FileUtil.getSelectedFile(fileList);
                        if (f != null) {
                            if (fileSystemView.isDrive(f)) {
                                if (f.toString().endsWith(DISK_SUFFIX)) {
                                    diskJPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                                } else {
                                    jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                                }
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

        //注册回车键监听事件
        fileList.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileList.getSelectedIndex() != -1) {
                    openFolderOrFile();
                }
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    /**
     * 打开当前文件夹or文件
     *
     */
    private void openFolderOrFile() {
        FileNode fileNode = fileList.getSelectedValue();
        File file = fileNode != null ? fileNode.getFile() : null;
        if (file != null) {
            if (file.isDirectory()) {
                //进入子目录
                FileUtil.showFileList(fileList, file);

                //展示导航栏地址
                navigationListener.showNavigation(file, false);
            } else if (file.isFile()) {
                //打开文件
                FileUtil.openFile(file);
            }
        }
    }
}
