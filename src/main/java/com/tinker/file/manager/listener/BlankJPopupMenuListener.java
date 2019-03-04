package com.tinker.file.manager.listener;

import com.tinker.file.manager.bean.FileNode;
import com.tinker.file.manager.ui.DiskPropertyForm;
import com.tinker.file.manager.util.FileUtil;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * BlankJPopupMenuListener
 *
 * @author Tinker Chen
 * @date 2019/3/1
 */
public class BlankJPopupMenuListener extends AbstractJPopupMenuListener {

    public BlankJPopupMenuListener(JList<FileNode> fileList, JPopupMenu jPopupMenu, NavigationListener navigationListener) {
        super(fileList, jPopupMenu, navigationListener);
    }

    /**
     * 磁盘路径长度 eg: D:\
     */
    private static final int DISK_PATH_LENGTH = 3;

    @Override
    public void addListener() {
        JMenu newFileMenu = new JMenu(PADDING_STRING + "新建");
        JMenuItem fileItem = new JMenuItem(PADDING_STRING + "文件");
        JMenuItem dirItem = new JMenuItem(PADDING_STRING + "文件夹");
        newFileMenu.add(fileItem);
        newFileMenu.add(dirItem);

        JMenuItem pasteItem = new JMenuItem( PADDING_STRING + "粘贴");
        JMenuItem diskPropertyItem = new JMenuItem(PADDING_STRING + "属性");

        jPopupMenu.add(newFileMenu);
        jPopupMenu.add(pasteItem);
        jPopupMenu.addSeparator();
        jPopupMenu.add(diskPropertyItem);
        jPopupMenu.setPopupSize(70,75);
        fileList.add(jPopupMenu);

        this.addNewFileListener(fileItem, dirItem);
        this.addPasteListener(pasteItem);
        this.addPropertyListener(diskPropertyItem);
    }

    private void addNewFileListener(JMenuItem fileItem, JMenuItem dirItem) {
        //新建文件
        fileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = (String) JOptionPane.showInputDialog(null, "请输入文件名:\n", "新建文件",
                        JOptionPane.PLAIN_MESSAGE, null, null, "");
                if (fileName != null && !"".equals(fileName)) {
                    if (FileUtil.isFileNameValid(fileName)) {
                        try {
                            File file = FileUtil.generateCopyFile(navigationListener.getCurrentPath(), fileName);
                            file.createNewFile();
                            FileUtil.showFileList(fileList, new File(navigationListener.getCurrentPath()));
                        } catch (IOException e1) {
                            JOptionPane.showMessageDialog(null, e1.getMessage(), "", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "文件名不能包含下列各种字符:\n \\:*?\"<>|/", "", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        //新建文件夹
        dirItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = (String) JOptionPane.showInputDialog(null, "请输入文件夹名:\n", "新建文件夹",
                        JOptionPane.PLAIN_MESSAGE, null, null, "");
                if (fileName != null && !"".equals(fileName)) {
                    if (FileUtil.isFileNameValid(fileName)) {
                        File file = FileUtil.generateCopyDir(navigationListener.getCurrentPath(), fileName);
                        file.mkdir();
                        FileUtil.showFileList(fileList, new File(navigationListener.getCurrentPath()));
                    } else {
                        JOptionPane.showMessageDialog(null, "文件名不能包含下列各种字符:\n \\:*?\"<>|/", "", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void addPasteListener(JMenuItem pasteItem) {
        //粘贴
        pasteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (copyPath != null && !"".equals(copyPath)) {
                    File file = new File(copyPath);
                    if (file.exists()) {
                        String currentPath = navigationListener.getCurrentPath();
                        File currentDir = new File(currentPath);
                        if (currentDir.exists()) {
                            try {
                                if (file.isFile()) {
                                    File copyFile = FileUtil.generateCopyFile(currentPath, file.getName());
                                    FileUtils.copyFile(file, copyFile);
                                }
                                if (file.isDirectory()) {
                                    File copyFile = FileUtil.generateCopyDir(currentPath, file.getName());
                                    FileUtil.copyDirectoryToDirectory(file, new File(currentPath), copyFile.getName());
                                }
                            } catch (IOException e1) {
                                JOptionPane.showMessageDialog(null, e1.getMessage(), "", JOptionPane.ERROR_MESSAGE);
                            }
                            FileUtil.showFileList(fileList, new File(currentPath));
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "不存在 " + copyPath, "", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void addPropertyListener(JMenuItem diskPropertyItem) {
        diskPropertyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //所在磁盘的属性
                String path = navigationListener.getCurrentPath();
                if (path != null && path.length() >= DISK_PATH_LENGTH) {
                    File file = new File(path.substring(0, DISK_PATH_LENGTH));
                    FileSystemView fileSystemView = FileSystemView.getFileSystemView();

                    FileNode fileNode = new FileNode("", fileSystemView.getSystemIcon(file), file, false);
                    new DiskPropertyForm(fileNode);
                }
            }
        });
    }
}
