package sounds;

/*
 * UDPReceive.java	21/06/07
 * author: Max
 * MSN: zengfc@21cn.com
 * QQ: 22291911
 * Email: zengfc@21cn.com
 *
 */

import java.net.*;
import javax.swing.JOptionPane;

public class UDPReceive extends Thread {
    String msgReceive;
    String msgSend;
    byte[] recbuf = new byte[1024];
    DatagramSocket ds;
    DatagramPacket pack = new DatagramPacket(recbuf, recbuf.length);

    ChatFrame cf;

    public UDPReceive(DatagramSocket ds,ChatFrame cf) {
        this(ds, cf, null);
    }

    public UDPReceive(DatagramSocket ds, ChatFrame cf,
                      String msgSend) {
        this.ds = ds;
        this.cf = cf;
        this.msgSend = msgSend;
    }

    public void run() {
        for (; ; ) {
            try { //0表示新加入，1表示回复，2表示请求连接，3为请求包的回复，6表示离开,7为服务器对客户端的通信，发所有信息给新加入的客户
                ds.receive(pack);
                msgReceive = new String(pack.getData(), pack.getOffset(),
                                        pack.getLength());
                String Num = msgReceive.substring(0, 1);
                msgReceive = msgReceive.substring(2);

                String address[] = msgReceive.split(":");

                if (Num.equals("0") || Num.equals("1")) {
                    cf.defaultModel.addRow(address);

                    if (Num.equals("0") &&
                        !pack.getAddress().getHostAddress().equals(
                            getLocalAddress())) {
                        new UDPTransmit(ds, "1 " + msgSend,
                                        pack.getAddress().getHostAddress(),
                                        pack.getPort(), false).start();
                    }
                } else if (Num.equals("2")) {
                    int result = JOptionPane.showConfirmDialog(null,
                            address[0] + " 请求连接\n是否接受该连接？", "消息",
                            JOptionPane.YES_NO_OPTION);

                    new UDPTransmit(ds, "3 " + result, address[0],
                                    Integer.parseInt(address[1]), false).start(); //0表示连接，1表示拒绝。如“3 0”表示连接，“3 1”表示拒绝
                    if (result == 0) {
                        cf.tar.setDestAddressAndPort(address[0],
                                Integer.parseInt(address[1]),
                                Integer.parseInt(address[2]),
                                Integer.parseInt(address[3]),
                                Integer.parseInt(address[4]),
                                Integer.parseInt(address[5])
                                );
                        cf.rtpConnect(address[0]);
                    }
                } else if (Num.equals("3")) {
                    if (msgReceive.equals("1")) {
                        JOptionPane.showMessageDialog(null,
                                pack.getAddress().getHostAddress() + " 拒绝连接");
                    } else {
                        cf.rtpConnect(pack.getAddress().getHostAddress());
                    }
                } else if (Num.equals("6")) { //收到Bye，删除行
                    //cf.findTableRow(address[0], address[1]);
                    cf.defaultModel.removeRow( cf.findTableRow(address[0], address[1]) );
                            //delTableRow(address[0], address[1]);
                } else {
                    String addressList[] = msgReceive.split(" ");
                    for (int i = 0; i < addressList.length; i++) {
                        address = addressList[i].split(":");
                        cf.defaultModel.addRow(address);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void delTableRow(String name, String address) {

        int rowCount = cf.msgTable.getRowCount();
        int columnCount = cf.msgTable.getColumnCount();

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {

                Object value = cf.msgTable.getValueAt(i, j);
                if (name.equals(value)) {

                    for (int k = 0; k < columnCount; k++) {
                        value = cf.msgTable.getValueAt(i, k);
                        if (address.equals(value)) {
                            cf.defaultModel.removeRow(i);
                            return;
                        }
                    }
                }
            }
        }
    }

    public String getLocalAddress() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
        }
        return addr.getHostAddress();
    }

}

