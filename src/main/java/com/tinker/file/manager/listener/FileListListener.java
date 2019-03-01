package com.tinker.file.manager.listener;

import com.tinker.file.manager.bean.FileNode;
import com.tinker.file.manager.util.FileUtil;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
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
    private JPopupMenu blankJPopupMenu;
    private NavigationListener navigationListener;

    private static final String DISK_SUFFIX = ":\\";

    public FileListListener(JList<FileNode> fileList, JPopupMenu jPopupMenu, JPopupMenu diskJPopupMenu,
                            JPopupMenu blankJPopupMenu, NavigationListener navigationListener) {
        this.fileList = fileList;
        this.jPopupMenu = jPopupMenu;
        this.diskJPopupMenu = diskJPopupMenu;
        this.blankJPopupMenu = blankJPopupMenu;
        this.navigationListener = navigationListener;

        Font font = new Font("Dialog", Font.PLAIN, 12);
        this.fileList.setFont(font);
    }

    public void addListener() {
        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //点击空白处不选择最后一条数据
                clickListBlankHandler(e);

                //点击非空白处
                if (fileList.getSelectedIndex() != -1) {
                    //单击
                    if (e.getClickCount() == MouseEvent.BUTTON1) {
                        //do nothing
                    }
                    //双击list时，打开文件或进入该子目录
                    if (e.getClickCount() == MouseEvent.BUTTON2) {
                        FileUtil.fileOpenHandler(fileList, navigationListener);
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
                    //右击空白处
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        String currentPath = navigationListener.getCurrentPath();
                        if (!NavigationListener.getHomeDir().equals(currentPath) && !"".equals(currentPath)) {
                            blankJPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                }
            }
        });

        //注册回车键监听事件
        fileList.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileList.getSelectedIndex() != -1) {
                    FileUtil.fileOpenHandler(fileList, navigationListener);
                }
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    /**
     * 点击空白处不选择最后一条数据
     * @param e
     */
    private void clickListBlankHandler(MouseEvent e) {
        JList<FileNode> list = (JList<FileNode>) e.getSource();

        if (list.locationToIndex(e.getPoint()) == -1
                && !e.isShiftDown()
                && !isMenuShortcutKeyDown(e)) {

            list.clearSelection();
        }
    }

    private boolean isMenuShortcutKeyDown(InputEvent event) {
        return (event.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0;
    }
}
