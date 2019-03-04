package com.tinker.file.manager.util;

import com.tinker.file.manager.bean.FileListModel;
import com.tinker.file.manager.bean.FileListRenderer;
import com.tinker.file.manager.bean.FileNode;
import com.tinker.file.manager.bean.FileQuantity;
import com.tinker.file.manager.listener.NavigationListener;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * 同一个目录下的复制需要改名
     */
    private static final String COPY_NAME = " - 副本";

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
     * 针对目录：1、进入子目录 2、展示导航栏地址
     * @param fileList
     * @param navigationListener
     */
    public static void fileOpenHandler(JList<FileNode> fileList, NavigationListener navigationListener) {
        File file = FileUtil.getSelectedFile(fileList);
        if (file != null) {
            if (file.isDirectory()) {
                //进入子目录
                FileUtil.showFileList(fileList, file);
                //展示导航栏地址
                navigationListener.showNavigation(file, false);
            }
            if (file.isFile()) {
                //打开文件
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

    /**
     * 检查文件名合法性
     * @param fileName
     * @return
     */
    public static boolean isFileNameValid(String fileName) {
        Pattern pattern = Pattern.compile("[\\\\/:*?\"<>|]+");
        Matcher matcher = pattern.matcher(fileName);
        return !matcher.find();
    }

    /**
     * 生成需要复制的文件
     * 处理文件同名需要改名的问题
     * @param currentPath
     * @param fileName
     * @return
     */
    public static File generateCopyFile(String currentPath, String fileName) {
        String parentFilePath = currentPath + File.separator;
        File file = new File(parentFilePath + fileName);
        if (file.exists() && file.isFile()) {
            //存在同名文件，修改文件名
            int index = fileName.lastIndexOf(".");

            String prefixName = index > 0 ? fileName.substring(0, index) : fileName;
            String suffixName = index > 0 ? fileName.substring(index) : "";
            File copyFile = new File(parentFilePath + prefixName + COPY_NAME + suffixName);

            int i = 1;
            StringBuilder builder = new StringBuilder();
            while (copyFile.exists() && copyFile.isFile()) {
                builder.append(parentFilePath);
                builder.append(prefixName);
                builder.append(COPY_NAME);
                builder.append(" (");
                builder.append(i++);
                builder.append(")");
                builder.append(suffixName);

                copyFile = new File(builder.toString());
                //清空StringBuilder
                builder.delete(0, builder.length());
            }
            return copyFile;
        }
        return file;
    }

    /**
     * 生成复制文件夹
     * @param currentPath
     * @param fileName
     * @return
     */
    public static File generateCopyDir(String currentPath, String fileName) {
        String filePath = currentPath + File.separator + fileName;
        File file = new File(filePath);

        if (file.exists() && file.isDirectory()) {
            File copyFile = new File(filePath + COPY_NAME);

            int i = 1;
            StringBuilder builder = new StringBuilder();
            while (copyFile.exists() && copyFile.isDirectory()) {
                builder.append(filePath);
                builder.append(COPY_NAME);
                builder.append(" (");
                builder.append(i++);
                builder.append(")");

                copyFile = new File(builder.toString());
                //清空StringBuilder
                builder.delete(0, builder.length());
            }
            return copyFile;
        }
        return file;
    }

    /**
     * 复制文件夹到另一目录下
     * @param srcDir
     * @param destDir
     * @throws IOException
     */
    public static void copyDirectoryToDirectory(File srcDir, File destDir, String copyFileName) throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        } else if (srcDir.exists() && !srcDir.isDirectory()) {
            throw new IllegalArgumentException("Source '" + destDir + "' is not a directory");
        } else if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        } else if (destDir.exists() && !destDir.isDirectory()) {
            throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
        } else {
            FileUtils.copyDirectory(srcDir, new File(destDir, copyFileName), true);
        }
    }

    /**
     * 新建文件增加目录树节点
     * @param fileTree
     * @param childFile
     */
    public static void addTreeNode(JTree fileTree, File childFile) {
        DefaultTreeModel treeModel = (DefaultTreeModel) fileTree.getModel();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        FileNode fileNode = new FileNode(childFile.getName(), fileSystemView.getSystemIcon(childFile), childFile, false);
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(fileNode);
        treeModel.insertNodeInto(childNode, treeNode, treeNode.getChildCount());
    }
}
