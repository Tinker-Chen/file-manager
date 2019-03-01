package com.tinker.file.manager.listener;

import com.tinker.file.manager.bean.FileNode;
import com.tinker.file.manager.ui.DirectoryPropertyForm;
import com.tinker.file.manager.ui.DiskPropertyForm;
import com.tinker.file.manager.ui.FilePropertyForm;
import com.tinker.file.manager.util.FileUtil;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * JPopupMenuListener
 *
 * @author Tinker Chen
 * @date 2019/2/25
 */
public class JPopupMenuListener {

    private JList<FileNode> fileList;
    private JPopupMenu jPopupMenu;
    private JPopupMenu diskJPopupMenu;
    private JPopupMenu blankJPopupMenu;
    private NavigationListener navigationListener;

    private static final String PADDING_STRING = "     ";

    /**
     * 复制的文件（文件夹）路径
     */
    private static String copyPath = "";

    public JPopupMenuListener(JList<FileNode> fileList, JPopupMenu jPopupMenu, JPopupMenu diskJPopupMenu,
                              JPopupMenu blankJPopupMenu, NavigationListener navigationListener) {
        this.fileList = fileList;
        this.jPopupMenu = jPopupMenu;
        this.diskJPopupMenu = diskJPopupMenu;
        this.blankJPopupMenu = blankJPopupMenu;
        this.navigationListener = navigationListener;
    }

    public void addListener() {
        this.addDiskJPopupMenuListener();
        this.addFileJPopupMenuListener();
        this.addBlankJPopupMenuListener();
    }

    private void addDiskJPopupMenuListener() {
        //右键磁盘菜单
        JMenuItem diskOpenItem = new JMenuItem(PADDING_STRING + "打开");
        JMenuItem diskPropertyItem = new JMenuItem(PADDING_STRING + "属性");
        diskJPopupMenu.add(diskOpenItem);
        diskJPopupMenu.add(diskPropertyItem);
        diskJPopupMenu.setPopupSize(70, 50);

        diskOpenItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileUtil.fileOpenHandler(fileList, navigationListener);
            }
        });
        diskPropertyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileNode fileNode = fileList.getSelectedValue();
                if (fileNode != null) {
                    new DiskPropertyForm(fileNode);
                }
            }
        });
        fileList.add(diskJPopupMenu);
    }

    private void addFileJPopupMenuListener() {
        //右键文件菜单
        JMenuItem openItem = new JMenuItem(PADDING_STRING + "打开");
        JMenuItem renameItem = new JMenuItem(PADDING_STRING + "重命名");
        JMenuItem copyItem = new JMenuItem(PADDING_STRING + "复制");
        JMenuItem deleteItem = new JMenuItem(PADDING_STRING + "删除");
        JMenuItem propertyItem = new JMenuItem(PADDING_STRING + "属性");
        jPopupMenu.add(openItem);
        jPopupMenu.addSeparator();
        jPopupMenu.add(renameItem);
        jPopupMenu.add(copyItem);
        jPopupMenu.add(deleteItem);
        jPopupMenu.addSeparator();
        jPopupMenu.add(propertyItem);
        jPopupMenu.setPopupSize(80,125);
        fileList.add(jPopupMenu);

        //添加右键菜单“打开”监听器
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileUtil.fileOpenHandler(fileList, navigationListener);
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
                //放入回收站或者直接删除文件
                FileUtil.moveToTrashOrDelete(file);
                //刷新
                FileUtil.showFileList(fileList, file.getParentFile());
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
        //复制
        copyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = FileUtil.getSelectedFile(fileList);
                if (file != null && file.exists()) {
                    copyPath = file.getAbsolutePath();
                }
            }
        });
    }

    private void addBlankJPopupMenuListener() {
        JMenu newFileMenu = new JMenu(PADDING_STRING + "新建");
        JMenuItem fileItem = new JMenuItem(PADDING_STRING + "文件");
        JMenuItem dirItem = new JMenuItem(PADDING_STRING + "文件夹");
        newFileMenu.add(fileItem);
        newFileMenu.add(dirItem);

        JMenuItem pasteItem = new JMenuItem( PADDING_STRING + "粘贴");
        JMenuItem diskPropertyItem = new JMenuItem(PADDING_STRING + "属性");

        blankJPopupMenu.add(newFileMenu);
        blankJPopupMenu.add(pasteItem);
        blankJPopupMenu.addSeparator();
        blankJPopupMenu.add(diskPropertyItem);
        blankJPopupMenu.setPopupSize(70,75);
        fileList.add(blankJPopupMenu);

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
}
