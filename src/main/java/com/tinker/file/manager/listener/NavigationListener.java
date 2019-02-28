package com.tinker.file.manager.listener;

import com.tinker.file.manager.bean.FileNode;
import com.tinker.file.manager.enums.NavigationTypeEnum;
import com.tinker.file.manager.util.FileUtil;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.Stack;

/**
 * NavigationListener
 *
 * 导航栏事件监听器
 * 导航栏前进后退实现参考：https://blog.csdn.net/gx17864373822/article/details/83005539
 *
 * @author Tinker Chen
 * @date 2019/2/27
 */
public class NavigationListener {

    private JTextField navigationTextField;
    private JLabel forward;
    private JLabel backward;
    private JLabel refresh;
    private JList<FileNode> fileList;

    /**
     * 主目录
     */
    private static String HOME_DIR = "";
    /**
     * 当前导航栏地址
     */
    private String currentPath = "";
    /**
     * 前进栈，存储前进的记录；点击前进按钮，backwardStack出栈放入forwardStack中
     */
    private Stack<String> forwardStack = new Stack();
    /**
     * 后退栈，存储后退的记录；点击后退按钮，forwardStack出栈放入backwardStack
     */
    private Stack<String> backwardStack = new Stack();

    public NavigationListener(JTextField navigationTextField, JLabel forward, JLabel backward, JLabel refresh,
                              JList<FileNode> fileList) {
        this.navigationTextField = navigationTextField;
        this.forward = forward;
        this.backward = backward;
        this.refresh = refresh;
        this.fileList = fileList;
        HOME_DIR = FileUtil.getHomeDirectory().toString();
    }

    public void addListener() {
        forward.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == MouseEvent.BUTTON1) {
                    //点击前进，backwardStack出栈放入forwardStack
                    navigate(backwardStack, forwardStack, NavigationTypeEnum.forward);
                }
            }
        });

        backward.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == MouseEvent.BUTTON1) {
                    //点击回退，forwardStack出栈放入backwardStack
                    navigate(forwardStack, backwardStack, NavigationTypeEnum.backward);
                }
            }
        });

        refresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                fastNavigate();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                refresh.setIcon(FileUtil.getImageIcon(this, "images/refresh_arrow.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                refresh.setIcon(FileUtil.getImageIcon(this, "images/refresh.png"));
            }
        });

        //导航栏文本框注册回车键监听事件
        navigationTextField.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fastNavigate();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), JComponent.WHEN_FOCUSED);
    }

    /**
     * 点击导航按钮 前进or后退
     * @param popStack
     * @param pushStack
     * @param navigationType
     */
    private void navigate(Stack<String> popStack, Stack<String> pushStack, NavigationTypeEnum navigationType) {
        if (!popStack.empty()) {
            String filePath = popStack.pop();
            pushStack.push(filePath);

            //刷新导航栏图标
            refreshNavigationIcon();

            //当前路径与出栈路径相同做特殊处理显示正确路径、正确的导航栏图标
            if (currentPath.equals(filePath)) {
                if (!popStack.empty()) {
                    //显示popStack的下个路径才是正确路径，注意这里是peek()并未删除栈顶元素
                    currentPath = popStack.peek();
                    navigationTextField.setText(currentPath);

                    //此时如果popStack剩下一个元素，相当于空栈，改变图标
                    if (popStack.size() == 1) {
                        if (NavigationTypeEnum.forward.equals(navigationType)) {
                            forward.setIcon(FileUtil.getImageIcon(this, "images/forward.png"));
                        } else {
                            backward.setIcon(FileUtil.getImageIcon(this, "images/backward.png"));
                        }
                    }
                }
            } else {
                currentPath = filePath;
                navigationTextField.setText(currentPath);
            }

            //刷新右侧文件列表
            if (HOME_DIR.equals(currentPath)) {
                //主目录列表
                FileUtil.showFileList(fileList, FileUtil.getHomeDirectory());
            } else {
                //当前目录
                FileUtil.showFileList(fileList, new File(currentPath));
            }
        }
    }

    /**
     * 点击左侧文件树or右侧文件列表 展示导航栏
     * @param file
     * @param excludeHomeDir 排除主目录
     */
    public void showNavigation(File file, boolean excludeHomeDir) {
        String filePath = file.getAbsolutePath();

        if (excludeHomeDir) {
            //判断是否是主目录
            if (FileUtil.getHomeDirectory().equals(file)) {
                filePath = HOME_DIR;
            }
        }
        //展示导航栏地址
        navigationTextField.setText(filePath);

        //文件浏览记录入栈，多次点击同一个节点不重复入栈
        if (forwardStack.empty()) {
            forwardStack.push(filePath);
        } else if (!filePath.equalsIgnoreCase(forwardStack.peek())) {
            forwardStack.push(filePath);
        }
        //记录当前路径
        currentPath = filePath;
        //打开新目录以后清空后退栈中的数据，并刷新图标
        this.cleanBackwardStack();
    }

    /**
     * 快速导航
     */
    private void fastNavigate() {
        String navigationText = navigationTextField.getText();
        if (navigationText != null && !"".equals(navigationText)) {
            if (HOME_DIR.equals(navigationText)) {
                currentPath = HOME_DIR;
                FileUtil.showFileList(fileList, FileUtil.getHomeDirectory());
                this.cleanBackwardStack();
            } else {
                File file = new File(navigationText);
                if (file.exists()) {
                    if (file.isDirectory()) {
                        currentPath = file.getAbsolutePath();
                        FileUtil.showFileList(fileList, file);
                        this.cleanBackwardStack();
                    } else {
                        FileUtil.openFile(file);
                    }
                } else {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Windows 找不到“");
                    builder.append(navigationText);
                    builder.append("”。请检查拼写并重试。");
                    JOptionPane.showMessageDialog(null, builder.toString(), "文件资源管理器", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * 打开新目录以后清空后退栈中的数据，并刷新图标
     */
    private void cleanBackwardStack() {
        if (!backwardStack.empty()) {
            backwardStack.clear();
        }
        this.refreshNavigationIcon();
    }

    /**
     * 刷新导航栏图标
     */
    private void refreshNavigationIcon() {
        backward.setIcon(forwardStack.empty() ? FileUtil.getImageIcon(this, "images/backward.png") :
                FileUtil.getImageIcon(this, "images/backward_arrow.png"));
        forward.setIcon(backwardStack.empty() ? FileUtil.getImageIcon(this, "images/forward.png") :
                FileUtil.getImageIcon(this, "images/forward_arrow.png"));
    }
}
