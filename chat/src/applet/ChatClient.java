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
 * 基于Socket网络聊天程序 客户端编码  
 * @author xx 2010-08-12  
 */  
public class ChatClient extends JFrame  implements ActionListener,Runnable{   
     
    TextField tfName = new TextField(15);//姓名输入文本域   
    Button btConnect = new Button("连接");//连接按钮   
    Button btDisconnect = new Button("断开连接");//断开连接按钮   
    TextArea tfChat = new TextArea(8,27);//显示聊天信息文本域   
    Button btSend = new Button("发送");   
    TextField tfMessage = new TextField(30);//聊天输入   
    java.awt.List list1  = new java.awt.List(9);//显示在线用户信息    
    Socket socket = null;//连接端口   
    PrintStream ps = null;//输出流   
    Listen listen = null;   
    //监听线程类   
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
                line = reader.readLine(); //读取数据流   
                System.out.println("客户端："+line);   
                   
            }catch (IOException ex) {   
                ex.printStackTrace();   
                ps.println("quit");; //断开连接   
                return;   
            }   
            StringTokenizer stinfo = new StringTokenizer(line,":"); //分解字符串   
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
            if(e.getSource()==btConnect) { //点击连接按钮   
                if (socket == null) {   
                    socket = new Socket(InetAddress.getLocalHost(),5566);//实例化一个套接字   
                    ps = new PrintStream(socket.getOutputStream());//获取输出流，写入信息   
                    StringBuffer info = new StringBuffer("info:");   
                    String userinfo = tfName.getText()+":"+InetAddress.getLocalHost().toString();   
                    ps.println(info.append(userinfo));//输出信息   
                    ps.flush();   
                    listen = new Listen(this,tfName.getText(),socket);   
                    listen.start();   
                }   
            } else if (e.getSource() == btDisconnect) { //点击断开连接按钮   
                disconnect();   
            } else if (e.getSource() == btSend) { //点击发送按钮   
                if (socket != null) {   
                    StringBuffer msg = new StringBuffer("MSG:");   
                    String msgtxt = new String(tfMessage.getText());   
                    ps.println(msg.append(msgtxt));//发送信息   
                    ps.flush();   
                } else {   
                    JOptionPane.showMessageDialog(this, "请先连接！", "提示", 1);   
                }   
            }   
               
        } catch (Exception ex) {   
            ex.printStackTrace();//输出错误信息   
        }   
    }   
    public void disconnect() { //断开连接方法   
        if (socket != null) {   
            ps.println("quit");//发送信息   
            ps.flush();   
               
            socket = null;   
            tfName.setText("");   
        }   
    }   
       
       
       
       
       
       
  
       
    public ChatClient(Socket socket) {   
           
           
        this.setLayout(new BorderLayout());   
           
        JPanel panel1 = new JPanel();   
        Label label = new Label("姓名");   
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
        Label label2 = new Label("聊天信息");   
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