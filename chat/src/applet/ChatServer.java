		package applet;
		
		import java.io.*; 
		import java.net.*; 
		import java.util.*; 
		
		public class ChatServer { 
		boolean started = false; //连接标示用于判断
		ServerSocket ss = null; //设定socket对象
		
		List<Client> clients = new ArrayList<Client>(); 
		
		public static void main(String[] args) { 
		new ChatServer().start(); //启动
		} 
		
		public void start() { 
		try { 
		ss = new ServerSocket(5566); //套接字
		started = true; //如果套接字端口没有被占用started等于true
		} catch (BindException e) { 
		System.out.println("端口使用中...."); //套接字端口被占用提示
		System.out.println("请关掉相关程序并重新运行服务器！"); 
		System.exit(0); //正常结束程序
		} catch (IOException e) { 
		e.printStackTrace(); 
		} 
		
		try { 
		
		while(started) { //循环started为true
		Socket s = ss.accept(); //设置连接
		Client c = new Client(s); //实例化对象客户端
		System.out.println("a client connected!"); //提示已经连接上
		new Thread(c).start(); //启动线程
		clients.add(c); 
		//dis.close(); 
		} 
		} catch (IOException e) { 
		e.printStackTrace(); 
		} finally { 
		try { 
		ss.close(); 
		} catch (IOException e) { 
		// TODO Auto-generated catch block 
		e.printStackTrace(); 
		} 
		} 
		} 
		
		class Client implements Runnable { 
		private Socket s; //socket对象
		private DataInputStream dis = null; //数据输入流
		private DataOutputStream dos = null; //数据输出流
		private boolean bConnected = false; //连接状态
		
		public Client(Socket s) { 
		this.s = s; 
		try { 
		dis = new DataInputStream(s.getInputStream());//获取客户端的输入流
		dos = new DataOutputStream(s.getOutputStream()); //获取客户端的输出流
		bConnected = true; 
		} catch (IOException e) { 
		e.printStackTrace(); 
		} 
		} 
		
		public void send(String str) { 
		try { 
		dos.writeUTF(str); //写String 
		} catch (IOException e) { 
		clients.remove(this); //退出服务器
		System.out.println("对方退出了！我从List里面去掉了！"); 
		//e.printStackTrace(); 
		} 
		} 
		
		public void run() { 
		try { 
		while(bConnected) { //循环读String 
		String str = dis.readUTF(); 
		System.out.println(str); 
		for(int i=0; i<clients.size(); i++) { 
		Client c = clients.get(i); 
		c.send(str); 
		//System.out.println(" a string send !"); 
		} 
		/* 
		for(Iterator<Client> it = clients.iterator(); it.hasNext(); ) { 
		Client c = it.next(); 
		c.send(str); 
		} 
		*/ 
		/* 
		Iterator<Client> it = clients.iterator(); 
		while(it.hasNext()) { 
		Client c = it.next(); 
		c.send(str); 
		} 
		*/ 
		} 
		} catch (EOFException e) { 
		System.out.println("Client closed!"); 
		} catch (IOException e) { 
		e.printStackTrace(); 
		} finally { 
		try { 
		if(dis != null) dis.close(); //关闭
		if(dos != null) dos.close(); //关闭
		if(s != null) { 
		s.close(); //关闭
		//s = null; 
		} 
		
		} catch (IOException e1) { 
		e1.printStackTrace(); 
		} 
		
		
		} 
		} 
		
		} 
		} 

