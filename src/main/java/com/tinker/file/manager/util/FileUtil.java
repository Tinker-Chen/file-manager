package com.tinker.file.manager.util;

import com.tinker.file.manager.bean.FileListModel;
import com.tinker.file.manager.bean.FileListRenderer;
import com.tinker.file.manager.bean.FileNode;
import com.tinker.file.manager.bean.FileQuantity;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * FileUtil
 *
 * @author Tinker Chen
 * @date 2019/2/18
 */
public class FileUtil {

    private static final long[] SIZE_VALUE = {1024 * 1024 *1024, 1024 * 1024, 1024, 1};
    private static final String[] SIZE_UNIT = new String[] {" G", " M", " KB", " B"};

    /**
     * 计算文件大小
     * @param file
     * @return
     */
    public static String sizeOfFile(File file) {
        long size = 0;

        if (file != null) {
            if (file.isDirectory()) {
                size = FileUtils.sizeOfDirectory(file);
            } else if (file.isFile()) {
                size = FileUtils.sizeOf(file);
            }
        }
        return sizeOfFile(size);
    }

    /**
     * 计算文件大小
     * @param size
     * @return
     */
    public static String sizeOfFile(long size) {
        String fileSize = "0 字节";
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");

        for (int i = 0; i < SIZE_VALUE.length; i++) {
            if (size / SIZE_VALUE[i] > 0) {
                fileSize = decimalFormat.format(size * 1.0 / SIZE_VALUE[i]);
                fileSize += SIZE_UNIT[i];
                break;
            }
        }
        return fileSize;
    }

    /**
     * 打开文件
     * @param file
     */
    public static void openFile(File file) {
        try {
            if (file != null) {
                Desktop.getDesktop().open(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取右侧选中的文件
     * @return
     */
    public static File getSelectedFile(JList<FileNode> fileList) {
        FileNode fileNode = fileList.getSelectedValue();
        return fileNode != null ? fileNode.getFile() : null;
    }

    /**
     * 展示(刷新)右侧文件列表
     * @param fileList
     * @param file
     */
    public static void showFileList(JList<FileNode> fileList ,File file) {
        FileListModel fileListModel = new FileListModel(file);
        fileList.setModel(fileListModel);
        fileList.setCellRenderer(new FileListRenderer());
    }

    /**
     * 文件处理器：文件夹-展开，文件-直接打开
     *
     */
    public static void fileOpenHandler(JList<FileNode> fileList) {
        File file = FileUtil.getSelectedFile(fileList);
        if (file != null) {
            if (file.isDirectory()) {
                FileUtil.showFileList(fileList, file);
            }
            if (file.isFile()) {
                FileUtil.openFile(file);
            }
        }
    }

    /**
     * 文件重命名，刷新右侧文件列表
     * @param fileList
     * @param file
     * @param newFileName
     */
    public static void renameAndRefreshFileList(JList<FileNode> fileList, File file, String newFileName) {
        if (!file.getName().equals(newFileName)) {
            //重命名
            file.renameTo(new File(file.getParent() + File.separator + newFileName));
            //刷新文件名
            FileUtil.showFileList(fileList, file.getParentFile());
        }
    }

    /**
     * 获取文件的创建时间
     * @param file
     * @return
     */
    public static Long getFileCreateTime(File file){
        try {
            Path path= Paths.get(file.getAbsolutePath());
            BasicFileAttributeView basicView= Files.getFileAttributeView(path, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS );
            BasicFileAttributes attr = basicView.readAttributes();
            return attr.creationTime().toMillis();
        } catch (Exception e) {
            e.printStackTrace();
            return file.lastModified();
        }
    }

    /**
     * 获取文件的创建时间
     * @param time
     * @return
     */
    public static String getTimeString(long time) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        return dateFormat.format(new Date(time));
    }

    /**
     * 统计文件数量
     * @param file
     * @return
     */
    public static FileQuantity getFileQuantity(File file) {
        FileQuantity fileQuantity = new FileQuantity();
        calFileQuantity(file, fileQuantity);
        return fileQuantity;
    }

    /**
     * 递归计算文件数量
     * @param file
     * @param fileQuantity
     * @return
     */
    public static void calFileQuantity(File file, FileQuantity fileQuantity) {
        if (file.exists()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    fileQuantity.setDirNum(fileQuantity.getDirNum() + 1);
                    calFileQuantity(f, fileQuantity);
                } else {
                    fileQuantity.setFileNum(fileQuantity.getFileNum() + 1);
                }
            }
        }
    }

    /**
     * 获取jar包中的图片
     * @param object
     * @param path
     * @return
     */
    public static Image getImage(Object object, String path) {
        Image image = null;
        InputStream in = object.getClass().getClassLoader().getResourceAsStream(path);
        if (in != null) {
            try {
                image = ImageIO.read(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return image;
    }

    /**
     * 获取jar包中的ImageIcon
     * @param object
     * @param path
     * @return
     */
    public static ImageIcon getImageIcon(Object object, String path) {
        Image image = FileUtil.getImage(object, path);
        return image != null ? new ImageIcon(image) : null;
    }

    /**
     * 放入回收站或者直接删除文件
     * @param file
     */
    public static void moveToTrashOrDelete(File file) {
        if (file != null && file.exists()) {
            com.sun.jna.platform.FileUtils fileUtils = com.sun.jna.platform.FileUtils.getInstance();
            if (fileUtils.hasTrash()) {
                try {
                    //放入回收站
                    fileUtils.moveToTrash(new File[]{file});
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else {
                //没有回收站直接删除
                file.delete();
            }
        }
    }

    /**
     * 获取主目录
     * @return
     */
    public static File getHomeDirectory() {
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        return fileSystemView.getFiles(fileSystemView.getHomeDirectory(), true)[0];
    }
}
