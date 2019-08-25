package applet;

import java.awt.*;
import javax.swing.*;

import sounds.ChatFrame;

import java.awt.event.*;
import java.net.*;
import java.io.*;
public class MyChatter extends JFrame {
    private JLabel jLName = new JLabel();//设置label 
    private JTextField jTName = new JTextField();//设置文本框
    private JLabel jLSendMss = new JLabel();//设置label
    private JLabel jLReceiveMss = new JLabel();//设置label
    private JButton jBSend = new JButton();//设置普通按钮
    private JButton jBClear = new JButton();//设置普通按钮
    private JScrollPane jScrollPane1 = new JScrollPane();//设置面板
    private JTextPane jTSendMss = new JTextPane();//设置面板
    private JScrollPane jScrollPane2 = new JScrollPane();//设置面板
    private JTextPane jTReceiveMss = new JTextPane();//设置面板

    //通讯用成员变量
    private byte[] receiveBuf=new byte[1000];
    private byte[] sendBuf=null;
    private DatagramSocket datagramServer=null;//数据报文包服务端
    private DatagramSocket datagramClient=null;//数据报文包客户端
    private DatagramPacket receivePacket=null;//数据报文包
    private DatagramPacket sendPacket=null;//发送数据报文包
    private static final int PORT=7425;//端口
    private InetAddress inetAddr=null;//主机名IP地址
    private Server server=null;//服务
    private String machineName=null;//机器名
    private String sendMss=null;//扫描发送
    static int k = 0;
    class Server extends Thread{

        public Server(){
            start();//启动
        }

        public void run(){
            while(true){
                try{               	
                    //System.out.println(datagramServer);
                    datagramServer.receive(receivePacket);//接收数据包返回数据填充到缓冲区
                    displayMss();//调用
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    class MyKeyAdapter extends KeyAdapter{
        public void keyPressed(KeyEvent e){
            //System.out.println(e.getKeyCode());
            if(e.isShiftDown()&&e.getKeyCode()==10){
                sendReady();
            }
        }
    }

    private void sendReady(){
        if(!checkMachineName()){
            return;
        }
        if(!checkSendMss()){
            return;
        }
        if(sendMss()){
            JOptionPane.showMessageDialog(null,"发送消息成功！","提示",JOptionPane.ERROR_MESSAGE);
        }
        else{
            JOptionPane.showMessageDialog(null,"发送消息失败！","提示",JOptionPane.ERROR_MESSAGE);
        }
    }



	private boolean sendMss(){
        try{
            sendPacket=toDatagram(sendMss,inetAddr,PORT);
            datagramClient.send(sendPacket);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    private DatagramPacket toDatagram(String s,InetAddress destIA,int destPort){
        //sendBuf=new byte[s.length()+1];
        //s.getBytes(0,s.length(),sendBuf,0);
        try{
            sendBuf=s.getBytes("GB2312");
        }
        catch(Exception e){

        }
        return new DatagramPacket(sendBuf,sendBuf.length,destIA,destPort);
    }

    private boolean checkMachineName(){
        machineName=jTName.getText();
        if(machineName.length()==0){
            JOptionPane.showMessageDialog(null,"请输入机器名！","警告",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try{
            inetAddr=InetAddress.getByName(machineName);
        }
        catch(UnknownHostException e){
            JOptionPane.showMessageDialog(null,"不能识别的机器名或者机器不存在！","警告",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean checkSendMss(){
        sendMss=jTSendMss.getText();
        if(sendMss.length()==0){
            JOptionPane.showMessageDialog(null,"要发送的消息不能为空！","警告",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void displayMss(){
        String mss=null;
        String tmp=jTReceiveMss.getText();
        try{
            mss=new String(receivePacket.getData(),0,receivePacket.getLength(),"GB2312");
        }
        catch(Exception e){

        }
        jTReceiveMss.setText(tmp+mss+"\n");
    }

    private void init() throws IOException{
    	
        datagramServer=new DatagramSocket(PORT);
        datagramClient=new DatagramSocket();
        receivePacket=new DatagramPacket(receiveBuf,receiveBuf.length);
        jTSendMss.addKeyListener(new MyKeyAdapter());
        server=new Server();
    }

    public MyChatter() throws HeadlessException {
        try {
            jbInit();
            init();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void main2(MyChatter myChatter1) throws HeadlessException, IOException {
      //  myChatter1.addWindowListener(myChatter1.mwa);
        myChatter1.setSize(400,410);
        myChatter1.setVisible(true);      
        myChatter1.addWindowListener(new WindowAdapter() {     
            public void windowClosing(WindowEvent e) {     //关闭窗口事件
            //	JOptionPane.showMessageDialog(null,"发送消息成功！","提示",JOptionPane.ERROR_MESSAGE);
            //close();     //关闭
            //	System.exit(0);
            	server.stop();
            	datagramServer.close();
            	ChatFrame.multiButton.setEnabled(true);
            //System.exit(0);
            }});
           
    }
    private void jbInit() throws Exception {
        jLName.setText("机器名或IP地址：");
        jLName.setBounds(new Rectangle(15, 7, 108, 18));
        this.getContentPane().setLayout(null);
        jTName.setBounds(new Rectangle(13, 27, 368, 22));
        jLSendMss.setText("发送消息：");
        jLSendMss.setBounds(new Rectangle(12, 54, 92, 20));
        jLReceiveMss.setText("接收消息：");
        jLReceiveMss.setBounds(new Rectangle(12, 208, 86, 19));
        jBSend.setBounds(new Rectangle(97, 178, 68, 19));
        jBSend.setText("发送");
        jBSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jBSend_actionPerformed(e);
            }
        });
        jBClear.setText("清空");
        jBClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jBClear_actionPerformed(e);
            }
        });
        jBClear.setBounds(new Rectangle(224, 179, 68, 19));
        this.setResizable(false);
        this.setTitle("XX的聊天程序");
        jScrollPane1.setBounds(new Rectangle(13, 77, 368, 90));
        jScrollPane2.setBounds(new Rectangle(13, 228, 368, 145));
        jTReceiveMss.setEditable(false);
        this.getContentPane().add(jLName, null);
        this.getContentPane().add(jTName, null);
        this.getContentPane().add(jLSendMss, null);
        this.getContentPane().add(jLReceiveMss, null);
        this.getContentPane().add(jBSend, null);
        this.getContentPane().add(jBClear, null);
        this.getContentPane().add(jScrollPane1, null);
        this.getContentPane().add(jScrollPane2, null);
        jScrollPane2.getViewport().add(jTReceiveMss, null);
        jScrollPane1.getViewport().add(jTSendMss, null);
    }

    void jBClear_actionPerformed(ActionEvent e) {
        jTSendMss.setText("");
    }

    void jBSend_actionPerformed(ActionEvent e) {
        sendReady();
    }
}

