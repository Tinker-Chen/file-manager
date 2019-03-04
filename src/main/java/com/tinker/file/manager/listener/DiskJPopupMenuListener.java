package com.tinker.file.manager.listener;

import com.tinker.file.manager.bean.FileNode;
import com.tinker.file.manager.ui.DiskPropertyForm;
import com.tinker.file.manager.util.FileUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * DiskJPopupMenuListener
 *
 * @author Tinker Chen
 * @date 2019/3/1
 */
public class DiskJPopupMenuListener extends AbstractJPopupMenuListener {

    public DiskJPopupMenuListener(JList<FileNode> fileList, JPopupMenu jPopupMenu, NavigationListener navigationListener) {
        super(fileList, jPopupMenu, navigationListener);
    }

    @Override
    public void addListener() {
        //右键磁盘菜单
        JMenuItem diskOpenItem = new JMenuItem(PADDING_STRING + "打开");
        JMenuItem diskPropertyItem = new JMenuItem(PADDING_STRING + "属性");
        jPopupMenu.add(diskOpenItem);
        jPopupMenu.add(diskPropertyItem);
        jPopupMenu.setPopupSize(70, 50);

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
        fileList.add(jPopupMenu);
    }
}
