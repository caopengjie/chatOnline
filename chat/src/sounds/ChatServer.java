package sounds;

/*
 * ChatServer.java	21/06/07
 * author: Max
 * MSN: zengfc@21cn.com
 * QQ: 22291911
 * Email: zengfc@21cn.com
 *
 */

import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatServer {

    DatagramSocket ds;

    byte[] recbuf = new byte[1024];

    DatagramPacket rec = new DatagramPacket(recbuf, recbuf.length);

    int port;

    String newAddress = "";
    String addressList = "";
    String sendAddressList = "";

    public ChatServer(int port) {
        this.port = port;
    }

    public void init() throws Exception {//初始化DatagramSocket
        if (port < 1024 || port > 65535) {
            System.out.println("自定义的服务端口号错误！系统自动指定端口为“2008”。");
            ds = new DatagramSocket(2008);
        } else {
            ds = new DatagramSocket(port);
        }
    }

    public void start() throws Exception {
        println("服务器开始运行");
        println("服务端口:" + port);
        init();
        receive();
    }

    public void receive() {
        for ( ; ; ) {
            try {
                ds.receive(rec);

                String msg = new String(rec.getData(), rec.getOffset(), rec.getLength());
                String natAddress = rec.getAddress().toString().substring(1);
                int natPort = rec.getPort();

                String Num = msg.substring(0,1);
                msg = msg.substring(2);
                String line;

                switch(Integer.parseInt(Num)){//1表示管理UDP，2表示音频UDP，3表示音频RTCP UDP，4表示视频UDP，5表示视频RTCP UDP，6表示离开，7表示保持连接
                    case 1 : {
                        line = "一个新的用户登录: " + msg + "  NAT地址: " + natAddress + " 管理端口: " + natPort;
                        sendAddressList = sendAddressList + natAddress + " " + natPort + " ";
                        newAddress = msg + ":" + natAddress + ":" + natPort;
                        println(line);
                        break;
                    }

                    case 2 : {
                        line = "音频端口: " + natPort;
                        newAddress = newAddress + ":" + natPort;
                        println(line);
                        break;
                    }

                    case 3 : {
                        line = "音频RTCP端口: " + natPort;
                        newAddress = newAddress + ":" + natPort;

                        println(line);
                        break;

                    }

                    case 4 : {
                        line = "视频端口: " + natPort;
                        newAddress = newAddress + ":" + natPort;

                        println(line);
                        break;
                    }

                    case 5 : {
                        line = "视频RTCP端口: " + natPort;
                        newAddress = newAddress + ":" + natPort;
                        println(line);
                        if (!sendAddressList.equals(""))
                            doSend("1 " + newAddress, sendAddressList);
                        if(!addressList.equals("")){
                            String address[] = newAddress.split(":");
                            doSend("7 " + addressList,
                                   address[1] + " " + address[2]);
                        }
                        addressList = addressList + newAddress + " ";
                        break;
                    }

                    case 6 : {
                        line = "一个客户离开: " + msg + "  NAT地址: " + natAddress + " 管理端口: " + natPort;
                        println(line);
                        delAddress(msg, natAddress, natPort);
                        if(!sendAddressList.equals(""))
                            doSend("6 " + msg + ":" + natAddress, sendAddressList);
                        break;
                    }

                    case 7 : break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void doSend(String msgSend, String sendAddressList) throws Exception {
        String[] s = sendAddressList.split(" ");
        byte[] data = msgSend.getBytes();
        for(int i =0; i < s.length; i++){
            DatagramPacket pack = new DatagramPacket(data, data.length, InetAddress.getByName(s[i]), Integer.parseInt(s[++i]));
            ds.send(pack);
        }
    }

    public void delAddress(String msg, String natAddress, int ctrlPort){

        String regEx = msg + ":" + natAddress + ":" + ctrlPort + ":.{1,5}:.{1,5}:.{1,5}:.{1,5} ";
        Pattern p = Pattern.compile(regEx);
        Matcher m=p.matcher(addressList);

        String s = m.replaceAll("");
        addressList = s;

        regEx = natAddress + " " + ctrlPort + " ";
        p = Pattern.compile(regEx);
        m = p.matcher(sendAddressList);
        s = m.replaceAll("");
        sendAddressList = s;
    }

    public void println(String s) {
        Date nowTime = new Date();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa", Locale.US);
        System.out.println(fmt.format(nowTime) + "----" + s);
    }

    public static void main(String[] args) throws Exception {
        new ChatServer(2001).start();
        ChatLoginFrame f = new ChatLoginFrame();
        
    }
}

