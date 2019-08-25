package sounds;

/*
 * ChatLoginFrame.java	21/06/07
 * author: Max
 * MSN: zengfc@21cn.com
 * QQ: 22291911
 * Email: zengfc@21cn.com
 *
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Rectangle;
import java.awt.Dimension;

public class ChatLoginFrame extends JFrame {
    String serverAdd;
    int serverPort;
    boolean hasServer = false;

//    JCheckBox jCheckBox1 = new JCheckBox();
    JButton jButton1 = new JButton();
    JButton jButton2 = new JButton();
    JLabel jLabel1 = new JLabel();
//    JLabel jLabel2 = new JLabel();
//    JLabel jLabel3 = new JLabel();
    JTextField nameTField = new JTextField();
    JTextField addrTField = new JTextField();
    JTextField portTField = new JTextField();

    public ChatLoginFrame() {
        try {
            jbInit();	

            addWindowListener(
                    new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            }
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void jbInit() throws Exception {
        setSize(new Dimension(249, 182));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        setLocation((screenSize.width - frameSize.width) / 2,
                    (screenSize.height - frameSize.height) / 2);
        getContentPane().setLayout(null);
        setTitle("登录");
        jButton1.setBounds(new Rectangle(28, 118, 73, 23));
        jButton1.setFont(new java.awt.Font("宋体", Font.PLAIN, 13));
        jButton1.setText("确定");
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton1_actionPerformed(e);
            }
        });
//        jCheckBox1.setFont(new java.awt.Font("宋体", Font.PLAIN, 13));
//        jCheckBox1.setText("需要服务器");
//        jCheckBox1.setBounds(new Rectangle(28, 34, 92, 23));
//        jCheckBox1.addItemListener(new ChatLoginFrame_jCheckBox1_itemAdapter(this));
        jLabel1.setFont(new java.awt.Font("宋体", Font.PLAIN, 13));
        jLabel1.setText("用户名:");
        jLabel1.setBounds(new Rectangle(28, 11, 46, 14));
        nameTField.setFont(new java.awt.Font("宋体", Font.PLAIN, 13));
        nameTField.setBounds(new Rectangle(97, 6, 118, 19));
//        jLabel2.setFont(new java.awt.Font("宋体", Font.PLAIN, 13));
//        jLabel3.setFont(new java.awt.Font("宋体", Font.PLAIN, 13));
//        jLabel3.setText("端口：");
//        jLabel3.setBounds(new Rectangle(28, 94, 41, 14));
//        jLabel2.setText("IP地址：");
//        jLabel2.setBounds(new Rectangle(28, 65, 59, 14));
        portTField.setFont(new java.awt.Font("宋体", Font.PLAIN, 13));
        portTField.setBounds(new Rectangle(97, 89, 118, 19));
        portTField.setVisible(false);
        portTField.setBorder(BorderFactory.createLineBorder(Color.black));
        addrTField.setFont(new java.awt.Font("宋体", Font.PLAIN, 13));
        addrTField.setBounds(new Rectangle(97, 60, 118, 19));
        addrTField.setVisible(false);
        addrTField.setBorder(BorderFactory.createLineBorder(Color.black));
        jButton2.setBounds(new Rectangle(142, 118, 73, 23));
        jButton2.setFont(new java.awt.Font("宋体", Font.PLAIN, 13));
        jButton2.setText("取消");
        jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton2_actionPerformed(e);
            }
        });
        this.getContentPane().add(addrTField);
        this.getContentPane().add(jLabel1);
//        this.getContentPane().add(jLabel2);
        this.getContentPane().add(nameTField);
        this.getContentPane().add(portTField);
//        this.getContentPane().add(jLabel3);
        this.getContentPane().add(jButton1);
        this.getContentPane().add(jButton2);
//        this.getContentPane().add(jCheckBox1);
        this.setResizable(false);
        this.setVisible(true);

    }

    public static void main(String args[]) {
        ChatLoginFrame fr = new ChatLoginFrame();
        //fr.validate();
    }

    public void jCheckBox1_itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == 1) {
            hasServer = true;
            portTField.setVisible(true);
            addrTField.setVisible(true);
        } else {
            hasServer = false;
            portTField.setVisible(false);
            addrTField.setVisible(false);
        }
    }

    public void jButton1_actionPerformed(ActionEvent e) { //对输入作判断
        dispose();//关闭当前窗口
        if(hasServer == false){
            serverPort = 2008;
        } else {
            serverPort = Integer.parseInt(portTField.getText());
        }
        new ChatFrame(nameTField.getText(), hasServer, addrTField.getText(), serverPort);
    }

    public void jButton2_actionPerformed(ActionEvent e) {
        System.exit(0);//正常退出
    }
}

class ChatLoginFrame_jCheckBox1_itemAdapter implements ItemListener {
    private ChatLoginFrame adaptee;
    ChatLoginFrame_jCheckBox1_itemAdapter(ChatLoginFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void itemStateChanged(ItemEvent e) {
        adaptee.jCheckBox1_itemStateChanged(e);
    }
}

