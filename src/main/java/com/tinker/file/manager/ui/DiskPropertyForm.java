package com.tinker.file.manager.ui;

import com.tinker.file.manager.bean.FileNode;
import com.tinker.file.manager.chart.DiskChart;
import com.tinker.file.manager.util.FileUtil;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * DiskPropertyForm
 *
 * @author Tinker Chen
 * @date 2019/2/25
 */
public class DiskPropertyForm {
    private JFrame frame;
    private JPanel diskPanel;
    private JButton okButton;
    private JPanel diskNamePanel;
    private JPanel diskSizePanel;
    private JPanel diskChartPanel;
    private JLabel diskIcon;
    private JLabel diskName;
    private JLabel freeSpace;
    private JLabel totalSpace;
    private JLabel usedSpace;
    private JLabel freeSpaceByte;
    private JLabel usedSpaceByte;
    private JLabel totalSpaceByte;
    private FileNode fileNode;

    private static final String BYTE_STRING = " 字节";

    public DiskPropertyForm(FileNode fileNode) {
        this.fileNode = fileNode;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame = new JFrame("磁盘属性");
                frame.setContentPane(diskPanel);
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
        File file = fileNode.getFile();
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        String diskDisplayName = fileSystemView.getSystemDisplayName(file);
        diskName.setText(diskDisplayName);
        diskIcon.setIcon(fileNode.getIcon());

        long freeSpaceValue = file.getFreeSpace();
        long totalSpaceValue = file.getTotalSpace();
        long usedSpaceValue = totalSpaceValue - freeSpaceValue;
        freeSpaceByte.setText(freeSpaceValue + BYTE_STRING);
        usedSpaceByte.setText(usedSpaceValue + BYTE_STRING);
        totalSpaceByte.setText(totalSpaceValue + BYTE_STRING);

        freeSpace.setText(FileUtil.sizeOfFile(freeSpaceValue));
        usedSpace.setText(FileUtil.sizeOfFile(usedSpaceValue));
        totalSpace.setText(FileUtil.sizeOfFile(totalSpaceValue));

        DiskChart diskChart = new DiskChart(diskDisplayName, totalSpaceValue, freeSpaceValue, usedSpaceValue);
        diskChartPanel.add(diskChart.generateChartPanel());

        okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //关闭窗口
                frame.dispose();
            }
        });
    }
}
