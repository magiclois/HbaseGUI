package com.lm.hbase.tab;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import com.lm.hbase.adapter.HbaseUtil;
import com.lm.hbase.common.Env;
import com.lm.hbase.swing.HbaseGui;
import com.lm.hbase.util.StringUtil;

public class CreateTab extends TabAbstract {

    private JTextField textField_tab3_tableName;
    private JButton    tab3_create_table_button;
    private JTextArea  tab3_textArea;

    public CreateTab(HbaseGui window){
        super(window);
    }

    @Override
    public String getTitle() {
        return "创建表";
    }

    @Override
    public JPanel initializePanel() {
        JPanel table = new JPanel();
        table.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        table.setLayout(new BorderLayout(0, 0));

        JPanel tableNorthPanel = new JPanel();
        tableNorthPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        table.add(tableNorthPanel, BorderLayout.NORTH);
        tableNorthPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JLabel label_2 = new JLabel(new ImageIcon(Env.IMG_DIR + "table.png"));
        tableNorthPanel.add(label_2);

        textField_tab3_tableName = new JTextField();
        tableNorthPanel.add(textField_tab3_tableName);
        textField_tab3_tableName.setColumns(10);

        JButton button_2 = new JButton("添加列族", new ImageIcon(Env.IMG_DIR + "add.png"));
        tableNorthPanel.add(button_2);

        JPanel tableCenterPanel = new JPanel();
        tableCenterPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        table.add(tableCenterPanel, BorderLayout.CENTER);
        tableCenterPanel.setLayout(new BoxLayout(tableCenterPanel, BoxLayout.Y_AXIS));

        tab3_textArea = new JTextArea();
        tab3_textArea.setColumns(20);
        tab3_textArea.setRows(10);
        tableCenterPanel.add(tab3_textArea);

        button_2.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                JDialog dialog = new JDialog(getFrame(), "添加列族", true);
                dialog.setSize(200, 70);

                int windowWidth = dialog.getWidth(); // 获得窗口宽
                int windowHeight = dialog.getHeight(); // 获得窗口高
                Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
                Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
                int screenWidth = screenSize.width; // 获取屏幕的宽
                int screenHeight = screenSize.height; // 获取屏幕的高
                dialog.setLocation(screenWidth / 2 - windowWidth, screenHeight / 2 - windowHeight * 2);

                JPanel dialogPanel = new JPanel(new BorderLayout());
                dialogPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
                dialog.getContentPane().add(dialogPanel);

                JLabel jLabel_dialog = new JLabel("列族名：");
                dialogPanel.add(jLabel_dialog, BorderLayout.WEST);

                JTextField jTextField_dialog = new JTextField();
                dialogPanel.add(jTextField_dialog, BorderLayout.CENTER);

                jTextField_dialog.addKeyListener(new KeyAdapter() {

                    @Override
                    public void keyReleased(KeyEvent e) {
                        if (e.getKeyCode() == 10) {
                            tab3_textArea.setText(tab3_textArea.getText() + "\n{"
                                                  + com.lm.hbase.swing.SwingConstants.COLUMN_FAMILY_DES
                                                  + jTextField_dialog.getText() + "}");
                            jTextField_dialog.setText("");
                            dialog.setVisible(false);
                        }
                    }

                });

                dialog.setVisible(true);

            }
        });

        textField_tab3_tableName.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                int index = tab3_textArea.getText().indexOf("]");
                if (index > 0) {
                    tab3_textArea.setText("[" + com.lm.hbase.swing.SwingConstants.TABLE_NAME_DES
                                          + textField_tab3_tableName.getText() + "]"
                                          + tab3_textArea.getText().substring(index + 1));
                } else {
                    tab3_textArea.setText("[" + com.lm.hbase.swing.SwingConstants.TABLE_NAME_DES
                                          + textField_tab3_tableName.getText() + "]" + tab3_textArea.getText());
                }
            }

        });

        JPanel tableSouthPanel = new JPanel();
        tableSouthPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        table.add(tableSouthPanel, BorderLayout.SOUTH);

        tab3_create_table_button = new JButton("创建", new ImageIcon(Env.IMG_DIR + "new.png"));
        tableSouthPanel.add(tab3_create_table_button);
        tab3_create_table_button.addMouseListener(new CreateTable());

        return table;
    }

    class CreateTable extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            tab3_create_table_button.setEnabled(false);
        }

        @Override
        public void mouseReleased(MouseEvent e) {

            String createStatement = tab3_textArea.getText().trim().replaceAll("\r|\n", "");
            createStatement = createStatement.replaceAll(com.lm.hbase.swing.SwingConstants.TABLE_NAME_DES,
                                                         "").replaceAll(com.lm.hbase.swing.SwingConstants.COLUMN_FAMILY_DES,
                                                                        "");
            int index = createStatement.indexOf("]");
            if (index == -1) {
                JOptionPane.showMessageDialog(getFrame(), "请输入表名", "警告", JOptionPane.WARNING_MESSAGE);
                tab3_create_table_button.setEnabled(true);
                return;
            }
            String tableName = createStatement.substring(1, index);
            if (StringUtil.isEmpty(tableName)) {
                JOptionPane.showMessageDialog(getFrame(), "请输入表名", "警告", JOptionPane.WARNING_MESSAGE);
                tab3_create_table_button.setEnabled(true);
                return;
            }

            String temp = createStatement.substring(index + 1);
            if (StringUtil.isEmpty(temp)) {
                JOptionPane.showMessageDialog(getFrame(), "请输入至少一个列族", "警告", JOptionPane.WARNING_MESSAGE);
                tab3_create_table_button.setEnabled(true);
                return;
            }
            String[] familys = temp.replaceAll("\\{", "").split("}");
            if (familys == null || familys.length == 0) {
                JOptionPane.showMessageDialog(getFrame(), "请输入至少一个列族", "警告", JOptionPane.WARNING_MESSAGE);
                tab3_create_table_button.setEnabled(true);
                return;
            }

            try {
                HbaseUtil.createTable(tableName, familys);
                JOptionPane.showMessageDialog(getFrame(), "成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e2) {
                JOptionPane.showMessageDialog(getFrame(), e2.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }

            tab3_create_table_button.setEnabled(true);
        }

    }

    @Override
    public void enableAll() {
        // TODO Auto-generated method stub

    }

    @Override
    public void disableAll() {
        // TODO Auto-generated method stub

    }

}