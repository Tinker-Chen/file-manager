package com.tinker.file.manager.bean;

import javax.swing.*;
import java.io.File;

/**
 * FileNode
 *
 * @author Tinker Chen
 * @date 2019/2/15
 */
public class FileNode {

    public FileNode(String name, Icon icon, File file, boolean isDummyRoot){
        this.name=name;this.icon=icon;this.file=file;this.isDummyRoot=isDummyRoot;
    }

    public boolean isInit;
    public boolean isDummyRoot;
    public String name;
    public Icon icon;
    public File file;

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

    public boolean isDummyRoot() {
        return isDummyRoot;
    }

    public void setDummyRoot(boolean dummyRoot) {
        isDummyRoot = dummyRoot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
