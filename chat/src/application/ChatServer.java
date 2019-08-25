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
 * 服务器端编码  
 * @author 欧阳平 2009-3-17   
 */  
public class ChatServer {   
    static int port = 5566;//端口号   
    static Vector<Client> clients = new Vector<Client>(10);//存储连接客户信息   
    static ServerSocket server = null; //建立服务器socket   
    static Socket socket = null; //套接字连接   
    /**  
     * Constructs  
     */  
    public ChatServer() {   
        try {   
            System.out.println("Server start...");   
            server  = new ServerSocket(port); //初始化服务器套接字   
            while (true) {   
                socket = server.accept(); //等待连接   
                System.out.println(socket.getInetAddress()+"连接\n");//得到客户机地址   
                Client client = new Client(socket); //实例化一个客户线程(其中线程Client中有Socket，这里的的Socket只是起个过度作用)   
                //   
                clients.add(client);//增加客户线程到向量中   
                client.start();//启动线程   
                notifyChatRoom(); //监视聊天室连接变化   
            }   
        } catch (Exception ex) {   
            ex.printStackTrace();//输出出错信息   
        }   
    }   
       
    public static void notifyChatRoom() { //监视客户端线程   
        StringBuffer newUser = new StringBuffer("newUser");   
        for (int i = 0; i < clients.size(); i++) {   
            Client c = (Client)clients.elementAt(i);   
            newUser.append(":"+c.name); //客户端姓名字符串   
        }   
        sendClients(newUser);//发送信息到客户端   
    }   
       
    public static void sendClients(StringBuffer message) {   
        for (int i= 0 ; i < clients.size(); i++) {   
            Client client = (Client)clients.elementAt(i);//分别得到每个客户端的连接   
            client.send(message);//发送信息   
        }   
    }   
       
    public void closeAll() { //关闭所有连接   
        while (clients.size() > 0 ) { //遍历整个Vector   
            Client client = (Client) clients.firstElement(); //得到一个客户端   
            try {   
                client.socket.close();   
            } catch(IOException ex) {   
                ex.printStackTrace(); // 输出错误信息   
            }   
            clients.removeElement(client); //移出客户端   
        }   
    }   
       
    public static void disconnect(Client c) {// 断开客户端   
        try {   
            System.err.println(c.ip+"断开连接\n");   
        } catch (Exception ex) {   
            ex.printStackTrace();   
        }   
        clients.removeElement(c);   
        c.socket = null;   
    }   
       
       
    /**  
     * main方法  
     * @param args  
     */  
    public static void main(String[] args) {   
        new ChatServer();   
  
    }   
    class Client extends Thread {   
        Socket socket;//连接端口   
        String name ;//用户姓名   
        String ip; //客户端ip地址   
        BufferedReader reader;//输入流   
        PrintStream ps;//输出流   
        public Client(Socket s) {   
            socket = s;   
            try {   
                reader = new BufferedReader(new InputStreamReader(s.getInputStream()));//得到输入流   
                ps = new PrintStream(s.getOutputStream());//得到输出流   
                String info = reader.readLine();//读取接收到的信息   
                StringTokenizer stinfo = new StringTokenizer(info,":"); //分解字符串   
                String head = stinfo.nextToken(); //获取关键字   
                System.out.println(stinfo.toString());   
                System.out.println(head);   
                if (stinfo.hasMoreTokens()){   
                    name = stinfo.nextToken() ;//获取用户名   
                }   
                if (stinfo.hasMoreTokens()) {   
                    ip = stinfo.nextToken(); //获取IP地址   
                }   
            } catch (IOException ex) {   
                ex.printStackTrace();   
            }   
            System.out.println(name);   
            System.out.println(ip);   
        }   
           
        public void send (StringBuffer msg) {   
            ps.println(msg); //输出信息   
            ps.flush();   
        }   
        public void run() {   
            while (true) {   
                String line = null;   
                try {   
                       
                    line = reader.readLine();   
                    System.out.println("line:"+line);   
                       
                } catch (IOException ex) {   
                    ex.printStackTrace(); //输出错误信息   
                    ChatServer.disconnect(this);//断开连接   
                    ChatServer.notifyChatRoom();//更新信息   
                    return ;   
                }   
                if (line == null) { //客户离开   
                    ChatServer.disconnect(this);   
                    ChatServer.notifyChatRoom();   
                    return ;   
                }   
                StringTokenizer st = new StringTokenizer(line,":");//分解字符串   
                String keyword = st.nextToken();   
                if (keyword.equals("MSG")) { //发送来的聊天信息   
                    StringBuffer msg = new StringBuffer("MSG:");   
                    msg.append(name); //在信息上增加用户名   
                    msg.append(st.nextToken("\0\n"));   
                    ChatServer.sendClients(msg);//发送聊天语句到各个客户端   
                    System.out.println(msg);   
                } else if (keyword.equals("quit")) { //退出命令   
                    ChatServer.disconnect(this); //断开连接   
                    ChatServer.notifyChatRoom(); //刷新信息   
                }   
            }   
        }   
    }   
       
} 
