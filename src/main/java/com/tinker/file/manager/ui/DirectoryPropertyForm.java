package com.tinker.file.manager.ui;

import com.tinker.file.manager.bean.FileNode;
import com.tinker.file.manager.bean.FileQuantity;
import com.tinker.file.manager.util.ExecutorUtil;
import com.tinker.file.manager.util.FileUtil;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.concurrent.ExecutorService;

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

    private static final String LOADING = "loading...";

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
                dirInfo.setText(LOADING);
                dirSize.setText(LOADING);

                //计算文件大小数量太耗时，采用线程异步处理
                ExecutorService executorService = ExecutorUtil.getInstance();
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        dirInfo.setText(getFileNumString(file));
                    }
                });
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        dirSize.setText(FileUtil.sizeOfFile(file));
                    }
                });
                //关闭线程池
                executorService.shutdown();
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
