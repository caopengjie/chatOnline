package handlens;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
	   import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.text.MaskFormatter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**  
 * 放大镜程序  
 * @author xx 2010-08-12  
 */ 
public class Magnifier
 {
//	 JButton b1 = null;
//	 JTextField jtext1 = new JTextField();
//	 JTextField jtext2 = new JTextField();
//	  public   Magnifier()  {
//     Box form = Box.createVerticalBox();
//     b1 = new JButton("放大镜");          
//     b1.addActionListener(this);
//     Panel toolBar = new Panel();
//     toolBar.add(b1);
//     form.add(toolBar);
//     JFrame frame = new JFrame("作者小钮");
//     frame.getContentPane().add(form);
//     frame.setSize(100,110);
//     frame.setLocationRelativeTo(null);
//     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//     frame.setVisible(true);
// }
//
//	public void actionPerformed(ActionEvent e) {
//		MagnifierWindow magnifierWindow = new MagnifierWindow("Magnifier"); 
//
//	}
	
	public static void main(String[] args)
	{
//		new Magnifier();
		MagnifierWindow magnifierWindow = new MagnifierWindow("Magnifier"); 
	}
	}
