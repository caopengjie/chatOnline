package applet;
import java.awt.BorderLayout;   
import java.awt.Button;   
import java.awt.Color;   
import java.awt.Label;   
import java.awt.TextArea;   
import java.awt.TextField;   
import java.awt.event.ActionEvent;   
import java.awt.event.ActionListener;   
import java.io.BufferedReader;   
import java.io.IOException;   
import java.io.InputStreamReader;   
import java.io.PrintStream;   
import java.net.InetAddress;   
import java.net.Socket;   
import java.util.StringTokenizer;   
  
import javax.swing.JFrame;   
import javax.swing.JOptionPane;   
import javax.swing.JPanel;   
  
/**  
 * ����Socket����������� �ͻ��˱���  
 * @author xx 2010-08-12  
 */  
public class ChatClient extends JFrame  implements ActionListener,Runnable{   
     
    TextField tfName = new TextField(15);//���������ı���   
    Button btConnect = new Button("����");//���Ӱ�ť   
    Button btDisconnect = new Button("�Ͽ�����");//�Ͽ����Ӱ�ť   
    TextArea tfChat = new TextArea(8,27);//��ʾ������Ϣ�ı���   
    Button btSend = new Button("����");   
    TextField tfMessage = new TextField(30);//��������   
    java.awt.List list1  = new java.awt.List(9);//��ʾ�����û���Ϣ    
    Socket socket = null;//���Ӷ˿�   
    PrintStream ps = null;//�����   
    Listen listen = null;   
    //�����߳���   
    class Listen extends Thread {   
        BufferedReader reader;   
        PrintStream ps;   
        String cname;   
        Socket socket;   
        ChatClient chatClient;   
        public Listen(ChatClient client,String name,Socket socket) {   
            try {   
                this.chatClient = client;   
                 this.socket = socket;   
                 this.cname = name;   
                 reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));   
                 ps = new PrintStream(socket.getOutputStream());   
            } catch (IOException e) {   
                e.printStackTrace();   
            }   
        }   
        public void run() {   
            while (true) {   
                String line=null ;   
                try {   
                line = reader.readLine(); //��ȡ������   
                System.out.println("�ͻ��ˣ�"+line);   
                   
            }catch (IOException ex) {   
                ex.printStackTrace();   
                ps.println("quit");; //�Ͽ�����   
                return;   
            }   
            StringTokenizer stinfo = new StringTokenizer(line,":"); //�ֽ��ַ���   
            String keyword = stinfo.nextToken();   
            if (keyword.equals("MSG")) {   
                chatClient.tfChat.append(line+"\n");   
            }   
            else if (keyword.equals("newUser")){   
                chatClient.list1.clear();   
                chatClient.list1.add("users", 0);   
                int i = 1;   
                while (stinfo.hasMoreTokens()) {   
                    chatClient.list1.add(stinfo.nextToken(), i++);   
                }   
            }   
        }   
           
      }   
    }   
    public void actionPerformed(ActionEvent e) {   
        try{   
            if(e.getSource()==btConnect) { //������Ӱ�ť   
                if (socket == null) {   
                    socket = new Socket(InetAddress.getLocalHost(),5566);//ʵ����һ���׽���   
                    ps = new PrintStream(socket.getOutputStream());//��ȡ�������д����Ϣ   
                    StringBuffer info = new StringBuffer("info:");   
                    String userinfo = tfName.getText()+":"+InetAddress.getLocalHost().toString();   
                    ps.println(info.append(userinfo));//�����Ϣ   
                    ps.flush();   
                    listen = new Listen(this,tfName.getText(),socket);   
                    listen.start();   
                }   
            } else if (e.getSource() == btDisconnect) { //����Ͽ����Ӱ�ť   
                disconnect();   
            } else if (e.getSource() == btSend) { //������Ͱ�ť   
                if (socket != null) {   
                    StringBuffer msg = new StringBuffer("MSG:");   
                    String msgtxt = new String(tfMessage.getText());   
                    ps.println(msg.append(msgtxt));//������Ϣ   
                    ps.flush();   
                } else {   
                    JOptionPane.showMessageDialog(this, "�������ӣ�", "��ʾ", 1);   
                }   
            }   
               
        } catch (Exception ex) {   
            ex.printStackTrace();//���������Ϣ   
        }   
    }   
    public void disconnect() { //�Ͽ����ӷ���   
        if (socket != null) {   
            ps.println("quit");//������Ϣ   
            ps.flush();   
               
            socket = null;   
            tfName.setText("");   
        }   
    }   
       
       
       
       
       
       
  
       
    public ChatClient(Socket socket) {   
           
           
        this.setLayout(new BorderLayout());   
           
        JPanel panel1 = new JPanel();   
        Label label = new Label("����");   
        panel1.setBackground(Color.orange);   
        panel1.add(label);   
        panel1.add(tfName);   
        panel1.add(btConnect);   
        panel1.add(btDisconnect);   
        this.add(panel1,BorderLayout.NORTH);   
           
        JPanel panel2 = new JPanel();   
        panel2.add(tfChat);   
        panel2.add(list1);   
        this.add(panel2,BorderLayout.CENTER);   
           
        JPanel panel3 = new JPanel();   
        Label label2 = new Label("������Ϣ");   
        panel3.add(label2);   
        panel3.add(tfMessage);   
        panel3.add(btSend);   
        this.add(panel3,BorderLayout.SOUTH);   
           
        this.setBounds(50,50,400,350);   
        this.setVisible(true);   
           
        btConnect.addActionListener(this);   
        btDisconnect.addActionListener(this);   
        btSend.addActionListener(this);   
           
    }   
       
    /**  
     * @param args  
     */  
    public static void main(String[] args) {   
        ChatClient client = new ChatClient(new Socket());   
        System.out.println(client.socket);   
    }
	
	public void run() {
		// TODO Auto-generated method stub
		
	}  
}