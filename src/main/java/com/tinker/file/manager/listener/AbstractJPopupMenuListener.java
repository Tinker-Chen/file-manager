package com.tinker.file.manager.listener;

import com.tinker.file.manager.bean.FileNode;

import javax.swing.*;

/**
 * AbstractJPopupMenuListener
 *
 * @author Tinker Chen
 * @date 2019/3/1
 */
public abstract class AbstractJPopupMenuListener {

    protected JList<FileNode> fileList;
    protected JPopupMenu jPopupMenu;
    protected NavigationListener navigationListener;

    protected static final String PADDING_STRING = "     ";

    /**
     * 复制的文件（文件夹）路径
     */
    protected static String copyPath = "";
    /**
     * 是否是剪切操作
     */
    protected static boolean isCut = false;

    public AbstractJPopupMenuListener(JList<FileNode> fileList, JPopupMenu jPopupMenu, NavigationListener navigationListener) {
        this.fileList = fileList;
        this.jPopupMenu = jPopupMenu;
        this.navigationListener = navigationListener;
    }

    /**
     * 添加右键菜单监听器
     */
    public abstract void addListener();
}
