package com.tinker.file.manager.bean;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

/**
 * FileListModel
 *
 * @author Tinker Chen
 * @date 2019/2/18
 */
public class FileListModel extends DefaultListModel<FileNode> {

    public FileListModel(File file) {
        super();
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        File[] files = fileSystemView.getFiles(file, false);

        for (int i = 0; i < files.length; i++) {
            FileNode fileNode = new FileNode(fileSystemView.getSystemDisplayName(files[i]),
                    fileSystemView.getSystemIcon(files[i]), files[i], false);
            this.addElement(fileNode);
        }
    }
}
