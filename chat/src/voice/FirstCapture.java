package voice;
import java.awt.BorderLayout;     
import java.awt.Color;     
import java.awt.Component;     
import java.awt.Dimension;     
import java.awt.Graphics;     
import java.awt.Graphics2D;     
import java.awt.Image;     
import java.awt.Panel;     
import java.awt.Rectangle;     
import java.awt.event.ActionEvent;     
import java.awt.event.ActionListener;     
import java.awt.event.WindowAdapter;     
import java.awt.event.WindowEvent;     
import java.awt.image.BufferedImage;     
import java.io.FileNotFoundException;     
import java.io.FileOutputStream;     
import java.io.IOException;     
import java.text.SimpleDateFormat;
import java.util.Date;
    
import javax.media.Buffer;     
import javax.media.CannotRealizeException;     
import javax.media.CaptureDeviceInfo;     
import javax.media.CaptureDeviceManager;     
import javax.media.Manager;     
import javax.media.MediaLocator;     
import javax.media.NoPlayerException;     
import javax.media.Player;     
import javax.media.control.FrameGrabbingControl;     
import javax.media.format.VideoFormat;     
import javax.media.util.BufferToImage;     
import javax.swing.JButton;     
import javax.swing.JFrame;     
import javax.swing.JPanel;     
    
import sounds.ChatFrame;

import com.sun.image.codec.jpeg.ImageFormatException;     
import com.sun.image.codec.jpeg.JPEGCodec;     
import com.sun.image.codec.jpeg.JPEGEncodeParam;     
import com.sun.image.codec.jpeg.JPEGImageEncoder;     
    
/**
 * ������Ƶ����
 * 
 * @version 1.00
 * @author ť
 * @date 2010-8-12
 */
public class FirstCapture extends JPanel implements ActionListener{     
    
    private CaptureDeviceInfo captureDeviceInfo=null;   //����Ӳ���豸��Ϣ����  
    private MediaLocator mediaLocator=null;     //ý�嶨λ��
    private static Player player=null;     //���Ŷ���
    private ImagePanel imagePanel=null;     //ͼƬ���
    private JButton capture;     			//��ť
    private Buffer buffer=null;     		//������
    private VideoFormat videoFormat=null;     //��Ƶ���ݸ�ʽ
    private BufferToImage bufferToImage=null;   //����Ӱ��  
    private Image image=null;     //ͼƬ  
    //static FirstCapture cf = null;
    public FirstCapture()     //���췽��
    {     
           
           
         setLayout(new BorderLayout());     // ���ò���
         setSize(320,550);     //��С
        String str="vfw:Microsoft WDM Image Capture (Win32):0";     //����
        captureDeviceInfo=CaptureDeviceManager.getDevice(str);     //��ø���������ƵӲ��
       // MediaLocator mediaLocator=captureDeviceInfo.getLocator();   
        mediaLocator=new MediaLocator("vfw://0");     
        imagePanel=new ImagePanel();     //�������
         capture=new JButton("����");     //���ð�ť����
         capture.addActionListener(this);     //���Ӱ�ť�¼�
        try {     
            player=Manager.createRealizedPlayer(mediaLocator);  //��������Ƶ�󶨵����Ŷ����У�����Դ��.   
            player.start();     //������һ���Ϊ6��״̬
            Component comp;     //����ͼ�α�ʾ�����Ķ���
            Component comp1;     //����ͼ�α�ʾ�����Ķ���
            if((comp=player.getVisualComponent())!=null)     
                add(comp,BorderLayout.NORTH);     
//          if((comp1=player.getControlPanelComponent())!=null)     
//              add(comp1,BorderLayout.CENTER);     
        } catch (NoPlayerException e) {     
            // TODO Auto-generated catch block     
            e.printStackTrace();     
        } catch (CannotRealizeException e) {     
            // TODO Auto-generated catch block     
            e.printStackTrace();     
        } catch (IOException e) {     
            // TODO Auto-generated catch block     
            e.printStackTrace();     
        }     
        add(imagePanel,BorderLayout.SOUTH);     //��ӵ����ı���
        add(capture,BorderLayout.CENTER);     //��ӵ������м�
    }     
         
    @Override    
    public void print(Graphics g) {     
        // TODO Auto-generated method stub     
        super.print(g);     
        g.setColor(new Color(255,0,0));     
          g.drawLine(0, 0, 100, 100);     
    }     
//    public static void main(String args[]){
//    	cf = new FirstCapture();
//    	cf.main1();
//    }
    public  void main1(FirstCapture cf) {     
        // TODO Auto-generated method stub     
        JFrame f = new JFrame("FirstCapture");     //����jframe
        //FirstCapture cf = new FirstCapture();     //ʵ��������ִ�й��췽��
             
        f.addWindowListener(new WindowAdapter() {     
          public void windowClosing(WindowEvent e) {     //�رմ����¼�
          player.close();     //���Źر�
          //ChatFrame s = new ChatFrame();
          ChatFrame.videoButton.setEnabled(true);
//          ChatFrame.disconnectButton.setEnabled(false);
          //System.exit(0);
          }});     //���������˳�
             
        f.add("Center",cf);     //��Ӷ���jframe
        f.pack();     //������ʾ��С�ͻ����ʵ�ʴ�С��Ӧ
        f.setSize(new Dimension(320,550));     //��С
        f.setVisible(true);     //�Ƿ���ʾ
    }     
      
    public void actionPerformed(ActionEvent e) {   //�����¼�  
        // TODO Auto-generated method stub     
        FrameGrabbingControl fgc=(FrameGrabbingControl)player.getControl("javax.media.control.FrameGrabbingControl");     
        buffer=fgc.grabFrame();     
        bufferToImage=new BufferToImage((VideoFormat)buffer.getFormat());     
        image=bufferToImage.createImage(buffer);     
        imagePanel.setImage(image);     
        saveImage(image,getTimeStr());//������Ƭ����     
    }   
    
    public static String getTimeStr() {
        Date currentTime = new Date();//ʱ��
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HHmmss");//��ʽ��ʱ��
        String dateString = formatter.format(currentTime);//ת�����ַ���
        String[] str=dateString.split(" ");      
        return "D:\\rec\\"+str[0]+str[1]+".jpg";
       }  
    
    public static void saveImage(Image image,String path)     
    {     
        BufferedImage bi=new BufferedImage(image.getWidth(null),image.getHeight(null),BufferedImage.TYPE_INT_RGB);     
        Graphics2D g2 = bi.createGraphics();     
        g2.drawImage(image, null, null);     
        FileOutputStream fos=null;     //�ļ����������
        try {     
                fos=new FileOutputStream(path);     //���·��
                 
        } catch (FileNotFoundException e) {     
            // TODO Auto-generated catch block     
            e.printStackTrace();     
        }     
        JPEGImageEncoder je=JPEGCodec.createJPEGEncoder(fos);   //����һ��ָ��  fos��JPEGImageEncoder����
        JPEGEncodeParam jp=je.getDefaultJPEGEncodeParam(bi);     //@@@
        jp.setQuality(0.5f, false);      //���������ǰ�ѽ����������������
        je.setJPEGEncodeParam(jp);      //����JPEGImageEncoder����������
        try {     
            je.encode(bi);      // �� BufferedImage ��Ϊ JPEG ���������롣
            fos.close();     
        } catch (ImageFormatException e) {     
            // TODO Auto-generated catch block     
            e.printStackTrace();     
        } catch (IOException e) {     
            // TODO Auto-generated catch block     
            e.printStackTrace();     
        }     
             
    }     
    class ImagePanel extends Panel      
      {     
        public Image myimg = null;     
             
        public ImagePanel()      
        {     
          setLayout(null);     
          setSize(320,240);     
        }     
             
        public void setImage(Image img)      
        {     
          this.myimg = img;     
          repaint();     
        }     
             
        public void paint(Graphics g)      
        {     
          if (myimg != null)      
          {     
            g.drawImage(myimg, 0, 0, this);     
          }     
               
        }     
      }     
         
} 