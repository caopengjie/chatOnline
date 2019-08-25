package sounds;

/*
 * UDPTransmit.java	21/06/07
 * author: Max
 * MSN: zengfc@21cn.com
 * QQ: 22291911
 * Email: zengfc@21cn.com
 *
 */

import java.io.IOException;
import java.net.*;

public class UDPTransmit extends Thread{
    DatagramSocket ds;
    DatagramPacket pack;
    String address;
    String data;
    int port;
    boolean cycle;

    public UDPTransmit(DatagramSocket ds, String data, String address, int port, boolean cycle) {
        this.ds = ds;
        this.data = data;
        this.address = address;
        this.port = port;
        this.cycle = cycle;
        setPack();
    }

    public void setPack(){
        byte[] bData = data.getBytes();
        InetSocketAddress addr = new InetSocketAddress(address, port);//获取地址和端口号
        try {
            pack = new DatagramPacket(bData, bData.length, addr);//创建数据报对象
        } catch (SocketException ex) {
        }
    }

    public void run(){
        if(cycle){
            for (; ; ) {
                try {
                    ds.send(pack);//发送数据
                    sleep(20000); //20秒
                } catch (IOException ex) {
                } catch (InterruptedException ex1) {
                }
            }
        } else {
            try {
                ds.send(pack);
            } catch (IOException ex2) {
            }
        }
    }
}
