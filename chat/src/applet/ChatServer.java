		package applet;
		
		import java.io.*; 
		import java.net.*; 
		import java.util.*; 
		
		public class ChatServer { 
		boolean started = false; //���ӱ�ʾ�����ж�
		ServerSocket ss = null; //�趨socket����
		
		List<Client> clients = new ArrayList<Client>(); 
		
		public static void main(String[] args) { 
		new ChatServer().start(); //����
		} 
		
		public void start() { 
		try { 
		ss = new ServerSocket(5566); //�׽���
		started = true; //����׽��ֶ˿�û�б�ռ��started����true
		} catch (BindException e) { 
		System.out.println("�˿�ʹ����...."); //�׽��ֶ˿ڱ�ռ����ʾ
		System.out.println("��ص���س����������з�������"); 
		System.exit(0); //������������
		} catch (IOException e) { 
		e.printStackTrace(); 
		} 
		
		try { 
		
		while(started) { //ѭ��startedΪtrue
		Socket s = ss.accept(); //��������
		Client c = new Client(s); //ʵ��������ͻ���
		System.out.println("a client connected!"); //��ʾ�Ѿ�������
		new Thread(c).start(); //�����߳�
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
		private Socket s; //socket����
		private DataInputStream dis = null; //����������
		private DataOutputStream dos = null; //���������
		private boolean bConnected = false; //����״̬
		
		public Client(Socket s) { 
		this.s = s; 
		try { 
		dis = new DataInputStream(s.getInputStream());//��ȡ�ͻ��˵�������
		dos = new DataOutputStream(s.getOutputStream()); //��ȡ�ͻ��˵������
		bConnected = true; 
		} catch (IOException e) { 
		e.printStackTrace(); 
		} 
		} 
		
		public void send(String str) { 
		try { 
		dos.writeUTF(str); //дString 
		} catch (IOException e) { 
		clients.remove(this); //�˳�������
		System.out.println("�Է��˳��ˣ��Ҵ�List����ȥ���ˣ�"); 
		//e.printStackTrace(); 
		} 
		} 
		
		public void run() { 
		try { 
		while(bConnected) { //ѭ����String 
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
		if(dis != null) dis.close(); //�ر�
		if(dos != null) dos.close(); //�ر�
		if(s != null) { 
		s.close(); //�ر�
		//s = null; 
		} 
		
		} catch (IOException e1) { 
		e1.printStackTrace(); 
		} 
		
		
		} 
		} 
		
		} 
		} 

