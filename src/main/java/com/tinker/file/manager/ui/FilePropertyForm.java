package com.tinker.file.manager.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.tinker.file.manager.bean.FileNode;
import com.tinker.file.manager.util.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * FilePropertyForm
 *
 * @author Tinker Chen
 * @date 2019/2/18
 */
public class FilePropertyForm {
    private JFrame frame;
    private JPanel filePropertyJPanel;
    private JPanel fileNameJPanel;
    private JPanel propertyJpanel;
    private JPanel timeJPanel;
    private JButton propertyButton;
    private JTextField fileName;
    private JLabel fileIcon;
    private JLabel filePath;
    private JLabel fileSize;
    private JLabel createTime;
    private JLabel modifiedTime;

    private JList<FileNode> fileList;
    private FileNode fileNode;

    public FilePropertyForm(JList<FileNode> fileList) {
        this.fileList = fileList;
        if (fileList != null) {
            this.fileNode = fileList.getSelectedValue();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame = new JFrame("文件属性");
                frame.setContentPane(filePropertyJPanel);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
                frame.setResizable(false);
                frame.setSize(400, 600);
                frame.setLocationRelativeTo(null);

                initComponent();
            }
        });
    }

    private void initComponent() {
        if (fileNode != null) {
            File file = fileNode.getFile();
            if (file != null) {
                fileIcon.setIcon(fileNode.getIcon());
                fileName.setText(file.getName());

                filePath.setText(file.getAbsolutePath());
                fileSize.setText(FileUtil.sizeOfFile(file));

                createTime.setText(FileUtil.getTimeString(FileUtil.getFileCreateTime(file)));
                modifiedTime.setText(FileUtil.getTimeString(file.lastModified()));

                propertyButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        FileUtil.renameAndRefreshFileList(fileList, file, fileName.getText());
                        //关闭窗口
                        frame.dispose();
                    }
                });
            }
        }
    }

}
