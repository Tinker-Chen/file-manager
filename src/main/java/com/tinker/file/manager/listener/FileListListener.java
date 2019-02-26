package com.tinker.file.manager.listener;

import com.tinker.file.manager.bean.FileNode;
import com.tinker.file.manager.util.FileUtil;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * FileListListener
 *
 * @author Tinker Chen
 * @date 2019/2/25
 */
public class FileListListener {

    private JList<FileNode> fileList;
    private JTextField navigationTextField;
    private JPopupMenu jPopupMenu;
    private JPopupMenu diskJPopupMenu;

    private static final String DISK_SUFFIX = ":\\";

    public FileListListener(JList<FileNode> fileList, JTextField navigationTextField, JPopupMenu jPopupMenu,
                            JPopupMenu diskJPopupMenu) {
        this.fileList = fileList;
        this.navigationTextField = navigationTextField;
        this.jPopupMenu = jPopupMenu;
        this.diskJPopupMenu = diskJPopupMenu;
    }

    public void addListener() {
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
    }
}
