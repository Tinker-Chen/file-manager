package com.tinker.file.manager.bean;

import javax.swing.*;
import java.awt.*;

/**
 * FileListRenderer
 *
 * @author Tinker Chen
 * @date 2019/2/17
 */
public class FileListRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        JLabel label= (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        FileNode fileNode = (FileNode) value;
        label.setText(fileNode.name);
        label.setIcon(fileNode.icon);
        label.setOpaque(false);
        return label;
    }
}
