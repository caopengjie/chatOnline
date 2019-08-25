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
 * 用于视频拍照
 * 
 * @version 1.00
 * @author 钮
 * @date 2010-8-12
 */
public class FirstCapture extends JPanel implements ActionListener{     
    
    private CaptureDeviceInfo captureDeviceInfo=null;   //捕获硬件设备信息对象  
    private MediaLocator mediaLocator=null;     //媒体定位器
    private static Player player=null;     //播放对象
    private ImagePanel imagePanel=null;     //图片面板
    private JButton capture;     			//按钮
    private Buffer buffer=null;     		//缓冲区
    private VideoFormat videoFormat=null;     //视频数据格式
    private BufferToImage bufferToImage=null;   //缓存影像  
    private Image image=null;     //图片  
    //static FirstCapture cf = null;
    public FirstCapture()     //构造方法
    {     
           
           
         setLayout(new BorderLayout());     // 设置布局
         setSize(320,550);     //大小
        String str="vfw:Microsoft WDM Image Capture (Win32):0";     //驱动
        captureDeviceInfo=CaptureDeviceManager.getDevice(str);     //获得改驱动的视频硬件
       // MediaLocator mediaLocator=captureDeviceInfo.getLocator();   
        mediaLocator=new MediaLocator("vfw://0");     
        imagePanel=new ImagePanel();     //设置面板
         capture=new JButton("拍照");     //设置按钮名称
         capture.addActionListener(this);     //增加按钮事件
        try {     
            player=Manager.createRealizedPlayer(mediaLocator);  //将本地视频绑定到播放对象中，数据源绑定.   
            player.start();     //启动，一般分为6个状态
            Component comp;     //具有图形表示能力的对象
            Component comp1;     //具有图形表示能力的对象
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
        add(imagePanel,BorderLayout.SOUTH);     //添加到面板的北方
        add(capture,BorderLayout.CENTER);     //添加到面板的中间
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
        JFrame f = new JFrame("FirstCapture");     //创建jframe
        //FirstCapture cf = new FirstCapture();     //实例化对象执行构造方法
             
        f.addWindowListener(new WindowAdapter() {     
          public void windowClosing(WindowEvent e) {     //关闭窗口事件
          player.close();     //播放关闭
          //ChatFrame s = new ChatFrame();
          ChatFrame.videoButton.setEnabled(true);
//          ChatFrame.disconnectButton.setEnabled(false);
          //System.exit(0);
          }});     //正常结束退出
             
        f.add("Center",cf);     //添加对象到jframe
        f.pack();     //画面显示大小和画面的实际大小适应
        f.setSize(new Dimension(320,550));     //大小
        f.setVisible(true);     //是否显示
    }     
      
    public void actionPerformed(ActionEvent e) {   //拍照事件  
        // TODO Auto-generated method stub     
        FrameGrabbingControl fgc=(FrameGrabbingControl)player.getControl("javax.media.control.FrameGrabbingControl");     
        buffer=fgc.grabFrame();     
        bufferToImage=new BufferToImage((VideoFormat)buffer.getFormat());     
        image=bufferToImage.createImage(buffer);     
        imagePanel.setImage(image);     
        saveImage(image,getTimeStr());//设置照片名称     
    }   
    
    public static String getTimeStr() {
        Date currentTime = new Date();//时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HHmmss");//格式化时间
        String dateString = formatter.format(currentTime);//转换成字符串
        String[] str=dateString.split(" ");      
        return "D:\\rec\\"+str[0]+str[1]+".jpg";
       }  
    
    public static void saveImage(Image image,String path)     
    {     
        BufferedImage bi=new BufferedImage(image.getWidth(null),image.getHeight(null),BufferedImage.TYPE_INT_RGB);     
        Graphics2D g2 = bi.createGraphics();     
        g2.drawImage(image, null, null);     
        FileOutputStream fos=null;     //文件输出流对象
        try {     
                fos=new FileOutputStream(path);     //获得路径
                 
        } catch (FileNotFoundException e) {     
            // TODO Auto-generated catch block     
            e.printStackTrace();     
        }     
        JPEGImageEncoder je=JPEGCodec.createJPEGEncoder(fos);   //创建一个指向  fos的JPEGImageEncoder对象
        JPEGEncodeParam jp=je.getDefaultJPEGEncodeParam(bi);     //@@@
        jp.setQuality(0.5f, false);      //创建替代当前已建量化表的新量化表
        je.setJPEGEncodeParam(jp);      //设置JPEGImageEncoder对象编码操作
        try {     
            je.encode(bi);      // 將 BufferedImage 作为 JPEG 数据流编码。
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