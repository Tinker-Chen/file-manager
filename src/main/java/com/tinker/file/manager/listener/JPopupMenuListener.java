package com.tinker.file.manager.listener;

import com.tinker.file.manager.bean.FileNode;
import com.tinker.file.manager.ui.DirectoryPropertyForm;
import com.tinker.file.manager.ui.FilePropertyForm;
import com.tinker.file.manager.util.FileUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * JPopupMenuListener
 *
 * @author Tinker Chen
 * @date 2019/2/25
 */
public class JPopupMenuListener {

    private JList<FileNode> fileList;
    private JPopupMenu jPopupMenu;
    private JPopupMenu driveJPopupMenu;

    private static final String PADDING_STRING = "     ";

    public JPopupMenuListener(JList<FileNode> fileList, JPopupMenu jPopupMenu, JPopupMenu driveJPopupMenu) {
        this.fileList = fileList;
        this.jPopupMenu = jPopupMenu;
        this.driveJPopupMenu = driveJPopupMenu;
    }

    public void addListener() {
        //右键文件菜单
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