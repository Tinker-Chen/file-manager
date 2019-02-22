package com.tinker.file.manager.bean;

/**
 * FileQuantity
 *
 * @author Tinker Chen
 * @date 2019/2/20
 */
public class FileQuantity {

    /**
     * 文件夹数量
     */
    private int dirNum = 0;

    /**
     * 文件数量
     */
    private int fileNum = 0;

    public int getDirNum() {
        return dirNum;
    }

    public void setDirNum(int dirNum) {
        this.dirNum = dirNum;
    }

    public int getFileNum() {
        return fileNum;
    }

    public void setFileNum(int fileNum) {
        this.fileNum = fileNum;
    }
}
