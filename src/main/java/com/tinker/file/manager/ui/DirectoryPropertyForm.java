package com.tinker.file.manager.ui;

import com.tinker.file.manager.bean.FileNode;
import com.tinker.file.manager.bean.FileQuantity;
import com.tinker.file.manager.util.FileUtil;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * DirectoryPropertyForm
 *
 * @author Tinker Chen
 * @date 2019/2/19
 */
public class DirectoryPropertyForm {
    private JFrame frame;
    private JTextField dirName;
    private JLabel dirIcon;
    private JButton okButton;
    private JPanel dirNamePanel;
    private JPanel dirPropertyPanel;
    private JPanel dirTimePanel;
    private JLabel dirPath;
    private JLabel dirSize;
    private JLabel dirInfo;
    private JLabel createTime;
    private JPanel dirPanel;

    private JList<FileNode> fileList;
    private FileNode fileNode;

    public DirectoryPropertyForm(JList<FileNode> fileList) {
        this.fileList = fileList;
        if (fileList != null) {
            this.fileNode = fileList.getSelectedValue();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame = new JFrame("文件夹属性");
                frame.setContentPane(dirPanel);
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
                dirIcon.setIcon(fileNode.getIcon());
                dirName.setText(file.getName());

                dirPath.setText(file.getAbsolutePath());
                dirSize.setText(FileUtil.sizeOfFile(file));
                dirInfo.setText(this.getFileNumString(file));

                createTime.setText(FileUtil.getTimeString(FileUtil.getFileCreateTime(file)));
            }

            okButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    FileUtil.renameAndRefreshFileList(fileList, file, dirName.getText());
                    //关闭窗口
                    frame.dispose();
                }
            });
        }
    }

    /**
     * 获取文件数量信息
     *
     * @param file
     * @return
     */
    private String getFileNumString(File file) {
        FileQuantity fileQuantity = FileUtil.getFileQuantity(file);
        StringBuilder builder = new StringBuilder();
        builder.append(fileQuantity.getFileNum());
        builder.append(" 个文件，");
        builder.append(fileQuantity.getDirNum());
        builder.append(" 个文件夹");
        return builder.toString();
    }

}
