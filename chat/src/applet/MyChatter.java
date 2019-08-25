package applet;

import java.awt.*;
import javax.swing.*;

import sounds.ChatFrame;

import java.awt.event.*;
import java.net.*;
import java.io.*;
public class MyChatter extends JFrame {
    private JLabel jLName = new JLabel();//����label 
    private JTextField jTName = new JTextField();//�����ı���
    private JLabel jLSendMss = new JLabel();//����label
    private JLabel jLReceiveMss = new JLabel();//����label
    private JButton jBSend = new JButton();//������ͨ��ť
    private JButton jBClear = new JButton();//������ͨ��ť
    private JScrollPane jScrollPane1 = new JScrollPane();//�������
    private JTextPane jTSendMss = new JTextPane();//�������
    private JScrollPane jScrollPane2 = new JScrollPane();//�������
    private JTextPane jTReceiveMss = new JTextPane();//�������

    //ͨѶ�ó�Ա����
    private byte[] receiveBuf=new byte[1000];
    private byte[] sendBuf=null;
    private DatagramSocket datagramServer=null;//���ݱ��İ������
    private DatagramSocket datagramClient=null;//���ݱ��İ��ͻ���
    private DatagramPacket receivePacket=null;//���ݱ��İ�
    private DatagramPacket sendPacket=null;//�������ݱ��İ�
    private static final int PORT=7425;//�˿�
    private InetAddress inetAddr=null;//������IP��ַ
    private Server server=null;//����
    private String machineName=null;//������
    private String sendMss=null;//ɨ�跢��
    static int k = 0;
    class Server extends Thread{

        public Server(){
            start();//����
        }

        public void run(){
            while(true){
                try{               	
                    //System.out.println(datagramServer);
                    datagramServer.receive(receivePacket);//�������ݰ�����������䵽������
                    displayMss();//����
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
            JOptionPane.showMessageDialog(null,"������Ϣ�ɹ���","��ʾ",JOptionPane.ERROR_MESSAGE);
        }
        else{
            JOptionPane.showMessageDialog(null,"������Ϣʧ�ܣ�","��ʾ",JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(null,"�������������","����",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try{
            inetAddr=InetAddress.getByName(machineName);
        }
        catch(UnknownHostException e){
            JOptionPane.showMessageDialog(null,"����ʶ��Ļ��������߻��������ڣ�","����",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean checkSendMss(){
        sendMss=jTSendMss.getText();
        if(sendMss.length()==0){
            JOptionPane.showMessageDialog(null,"Ҫ���͵���Ϣ����Ϊ�գ�","����",JOptionPane.ERROR_MESSAGE);
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
            public void windowClosing(WindowEvent e) {     //�رմ����¼�
            //	JOptionPane.showMessageDialog(null,"������Ϣ�ɹ���","��ʾ",JOptionPane.ERROR_MESSAGE);
            //close();     //�ر�
            //	System.exit(0);
            	server.stop();
            	datagramServer.close();
            	ChatFrame.multiButton.setEnabled(true);
            //System.exit(0);
            }});
           
    }
    private void jbInit() throws Exception {
        jLName.setText("��������IP��ַ��");
        jLName.setBounds(new Rectangle(15, 7, 108, 18));
        this.getContentPane().setLayout(null);
        jTName.setBounds(new Rectangle(13, 27, 368, 22));
        jLSendMss.setText("������Ϣ��");
        jLSendMss.setBounds(new Rectangle(12, 54, 92, 20));
        jLReceiveMss.setText("������Ϣ��");
        jLReceiveMss.setBounds(new Rectangle(12, 208, 86, 19));
        jBSend.setBounds(new Rectangle(97, 178, 68, 19));
        jBSend.setText("����");
        jBSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jBSend_actionPerformed(e);
            }
        });
        jBClear.setText("���");
        jBClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jBClear_actionPerformed(e);
            }
        });
        jBClear.setBounds(new Rectangle(224, 179, 68, 19));
        this.setResizable(false);
        this.setTitle("XX���������");
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

