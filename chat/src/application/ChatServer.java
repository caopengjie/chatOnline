package application;
import java.io.BufferedReader;   
import java.io.IOException;   
import java.io.InputStreamReader;   
import java.io.PrintStream;   
import java.net.ServerSocket;   
import java.net.Socket;   
import java.util.StringTokenizer;   
import java.util.Vector;   
  
/**  
 * �������˱���  
 * @author ŷ��ƽ 2009-3-17   
 */  
public class ChatServer {   
    static int port = 5566;//�˿ں�   
    static Vector<Client> clients = new Vector<Client>(10);//�洢���ӿͻ���Ϣ   
    static ServerSocket server = null; //����������socket   
    static Socket socket = null; //�׽�������   
    /**  
     * Constructs  
     */  
    public ChatServer() {   
        try {   
            System.out.println("Server start...");   
            server  = new ServerSocket(port); //��ʼ���������׽���   
            while (true) {   
                socket = server.accept(); //�ȴ�����   
                System.out.println(socket.getInetAddress()+"����\n");//�õ��ͻ�����ַ   
                Client client = new Client(socket); //ʵ����һ���ͻ��߳�(�����߳�Client����Socket������ĵ�Socketֻ�������������)   
                //   
                clients.add(client);//���ӿͻ��̵߳�������   
                client.start();//�����߳�   
                notifyChatRoom(); //�������������ӱ仯   
            }   
        } catch (Exception ex) {   
            ex.printStackTrace();//���������Ϣ   
        }   
    }   
       
    public static void notifyChatRoom() { //���ӿͻ����߳�   
        StringBuffer newUser = new StringBuffer("newUser");   
        for (int i = 0; i < clients.size(); i++) {   
            Client c = (Client)clients.elementAt(i);   
            newUser.append(":"+c.name); //�ͻ��������ַ���   
        }   
        sendClients(newUser);//������Ϣ���ͻ���   
    }   
       
    public static void sendClients(StringBuffer message) {   
        for (int i= 0 ; i < clients.size(); i++) {   
            Client client = (Client)clients.elementAt(i);//�ֱ�õ�ÿ���ͻ��˵�����   
            client.send(message);//������Ϣ   
        }   
    }   
       
    public void closeAll() { //�ر���������   
        while (clients.size() > 0 ) { //��������Vector   
            Client client = (Client) clients.firstElement(); //�õ�һ���ͻ���   
            try {   
                client.socket.close();   
            } catch(IOException ex) {   
                ex.printStackTrace(); // ���������Ϣ   
            }   
            clients.removeElement(client); //�Ƴ��ͻ���   
        }   
    }   
       
    public static void disconnect(Client c) {// �Ͽ��ͻ���   
        try {   
            System.err.println(c.ip+"�Ͽ�����\n");   
        } catch (Exception ex) {   
            ex.printStackTrace();   
        }   
        clients.removeElement(c);   
        c.socket = null;   
    }   
       
       
    /**  
     * main����  
     * @param args  
     */  
    public static void main(String[] args) {   
        new ChatServer();   
  
    }   
    class Client extends Thread {   
        Socket socket;//���Ӷ˿�   
        String name ;//�û�����   
        String ip; //�ͻ���ip��ַ   
        BufferedReader reader;//������   
        PrintStream ps;//�����   
        public Client(Socket s) {   
            socket = s;   
            try {   
                reader = new BufferedReader(new InputStreamReader(s.getInputStream()));//�õ�������   
                ps = new PrintStream(s.getOutputStream());//�õ������   
                String info = reader.readLine();//��ȡ���յ�����Ϣ   
                StringTokenizer stinfo = new StringTokenizer(info,":"); //�ֽ��ַ���   
                String head = stinfo.nextToken(); //��ȡ�ؼ���   
                System.out.println(stinfo.toString());   
                System.out.println(head);   
                if (stinfo.hasMoreTokens()){   
                    name = stinfo.nextToken() ;//��ȡ�û���   
                }   
                if (stinfo.hasMoreTokens()) {   
                    ip = stinfo.nextToken(); //��ȡIP��ַ   
                }   
            } catch (IOException ex) {   
                ex.printStackTrace();   
            }   
            System.out.println(name);   
            System.out.println(ip);   
        }   
           
        public void send (StringBuffer msg) {   
            ps.println(msg); //�����Ϣ   
            ps.flush();   
        }   
        public void run() {   
            while (true) {   
                String line = null;   
                try {   
                       
                    line = reader.readLine();   
                    System.out.println("line:"+line);   
                       
                } catch (IOException ex) {   
                    ex.printStackTrace(); //���������Ϣ   
                    ChatServer.disconnect(this);//�Ͽ�����   
                    ChatServer.notifyChatRoom();//������Ϣ   
                    return ;   
                }   
                if (line == null) { //�ͻ��뿪   
                    ChatServer.disconnect(this);   
                    ChatServer.notifyChatRoom();   
                    return ;   
                }   
                StringTokenizer st = new StringTokenizer(line,":");//�ֽ��ַ���   
                String keyword = st.nextToken();   
                if (keyword.equals("MSG")) { //��������������Ϣ   
                    StringBuffer msg = new StringBuffer("MSG:");   
                    msg.append(name); //����Ϣ�������û���   
                    msg.append(st.nextToken("\0\n"));   
                    ChatServer.sendClients(msg);//����������䵽�����ͻ���   
                    System.out.println(msg);   
                } else if (keyword.equals("quit")) { //�˳�����   
                    ChatServer.disconnect(this); //�Ͽ�����   
                    ChatServer.notifyChatRoom(); //ˢ����Ϣ   
                }   
            }   
        }   
    }   
       
} 
