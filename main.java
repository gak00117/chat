package tw.com.ispan.java1;


import java.awt.BorderLayout;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Sam_Ip_Lab_Receive_final extends JFrame {
	
	private static Connection conn;
	private final static String USER = "root";
	private final static String URL = "jdbc:mysql://10.10.243.81:8889/messenger";
	private final static String PASSWORD = "root";
	private final static String SQL_SCAN = "SELECT * FROM content WHERE kw = ?";
	private final static String SQLINSERT = "INSERT INTO content (kw,ip,port) VALUES (?,?,?)";
	private final static String SQL_DEL = "DELETE FROM content";
	private static final long serialVersionUID = 1L;
	private JTextArea showText;
	private JLabel inputIpLB, inputPortLB;
	private JTextField inputIp, inputPort, inputText;
	private JButton estConn, disConn; 
	
	String tm = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
	
	public Sam_Ip_Lab_Receive_final() throws Exception{
		super("Chat");

	disConn= new JButton("斷線");
	estConn= new JButton("連線");
	
	
	inputIpLB= new JLabel("連線代碼 :");
	inputPortLB= new JLabel("Port :");
	inputIp= new JTextField(14);
	inputPort= new JTextField(6);
	inputText= new JTextField(60);
	
	showText= new JTextArea(20, 60);

	JPanel top = new JPanel(new FlowLayout());
	top.add(inputIpLB);
	top.add(inputIp);
	top.add(inputPortLB);
	top.add(inputPort);

	top.add(estConn);
	top.add(disConn);
	
	new JPanel(new FlowLayout());
	
	
	setLayout(new BorderLayout());
	add(top, BorderLayout.NORTH);
	
	JPanel ctr = new JPanel(new FlowLayout());
	ctr.add(showText);
	ctr.add(inputText);
	add(ctr, BorderLayout.CENTER);
	add(ctr, BorderLayout.CENTER);
	


	
			
	setSize(800,600);
	setVisible(true);
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	init();

	
	}
	

	private void init() throws Exception {
		

		estConn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			
				testThread mt1 = new testThread("A");
				mt1.start();
			}
		});
		
		
	}
	private void initInpText() throws Exception {
		
		inputText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				String mesg = inputText.getText();
				

				byte[] data = mesg.getBytes();
				
			
				String kw = inputIp.getText();
				//String kwR = kw+"_1";
				Properties prop = new Properties();
				prop.put("user", USER);prop.put("password", PASSWORD);
				try {
					conn = DriverManager.getConnection(URL, prop);
					PreparedStatement pstmt = conn.prepareStatement(SQL_SCAN);
					pstmt.setString(1, kw);
					ResultSet rs = pstmt.executeQuery();
					rs.next();
					
					
			
					
					
					String v3 = rs.getString("ip");
					String v4 = rs.getString("port");
					int pp = Integer.parseInt(v4);
					showText.append(mesg+"\n"+tm+" sent");
					
					DatagramSocket socket = new DatagramSocket(pp); //開關之間資料傳遞
					
					DatagramPacket packet = new DatagramPacket(data, data.length,
							InetAddress.getByName(v3),pp);
					socket.send(packet);
					socket.close();
					pstmt.close();
					PreparedStatement pstmtc = conn.prepareStatement(SQL_DEL);
					pstmtc.executeUpdate();
					pstmtc.close();
					
					
				} catch (Exception e1) {
					System.out.println(e1);
				}
				inputText.setText("");

				
			}
	});
	}
	
	private void initInpTextN() throws Exception {
		
		inputText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				String mesg = inputText.getText();
				

				byte[] data = mesg.getBytes();
				
			
				String kw = inputIp.getText();
				String kwR = kw+"_1";
				Properties prop = new Properties();
				prop.put("user", USER);prop.put("password", PASSWORD);
				try {
					conn = DriverManager.getConnection(URL, prop);
					PreparedStatement pstmt = conn.prepareStatement(SQL_SCAN);
					pstmt.setString(1, kwR);
					ResultSet rs = pstmt.executeQuery();
					rs.next();
					
					
			
					
					
					String v3 = rs.getString("ip");
					String v4 = rs.getString("port");
					int pp = Integer.parseInt(v4);
					showText.append("\n"+mesg+"\n"+tm+" sent\f");
					
					DatagramSocket socket = new DatagramSocket(pp); //開關之間資料傳遞
					
					DatagramPacket packet = new DatagramPacket(data, data.length,
							InetAddress.getByName(v3),pp);
					socket.send(packet);
					socket.close();
					pstmt.close();
					
					
					
				} catch (Exception e1) {
					System.out.println(e1);
				}
				inputText.setText("");
				
				
			}
	});
	}

		private  void estConnect() throws Exception{
		
		Properties prop = new Properties();
		String kw = inputIp.getText();
		schIp();
		
		
		prop.put("user", USER);prop.put("password", PASSWORD);
		try  {
			conn = DriverManager.getConnection(URL, prop); 
			
				if(checkIp(kw)) {

					PreparedStatement pstmt = conn.prepareStatement(SQLINSERT);
					initInpTextN();
					String port = inputPort.getText();
					int bbb = inputPort.getText().length();
						if(bbb == 4) {
							pstmt.setString(1, kw);
							pstmt.setString(2, schIp());
							pstmt.setString(3, port);
							pstmt.executeUpdate();
							System.out.println("ok");
							byte[] buf = new byte[1024];
							showText.append(schIp()+" "+port+"\n"+"等待連線...");
							while(true) {
							try {
								DatagramSocket socket = new DatagramSocket(bbb);
								DatagramPacket packet = new DatagramPacket(buf, buf.length);
								
								socket.receive(packet);
								socket.close();
								System.out.println("ok");
							
								
								String urip = packet.getAddress().getHostAddress();
								int len = packet.getLength();
								byte[] data = packet.getData();
								String mesg = new String(data, 0, len);
								showText.append(urip + mesg);

								
								
							} catch (Exception e1) {
								System.out.println(e1);
							}
							}
						}else {
							
							int tt;
							while (true) {
								tt = (int)((Math.random()*9999));
								if(tt>5000) {
									System.out.println(tt);
									break;
								}
							}
							String t1 = String.valueOf(tt);
							pstmt.setString(1, kw);
							pstmt.setString(2, schIp());
							pstmt.setString(3, t1);
							pstmt.executeUpdate();
							System.out.println(" !=4 ok");
							byte[] buf = new byte[1024];
//							String i = inputPort.getText();

							showText.append(schIp()+" "+t1+"\n"+"等待連線...");
							
							
							
							while(true) {
							try {
								DatagramSocket socket = new DatagramSocket(tt);
								DatagramPacket packet = new DatagramPacket(buf, buf.length);
								
								socket.receive(packet);
								socket.close();

								int len = packet.getLength();
								byte[] data = packet.getData();
								String mesg = new String(data, 0, len);
								showText.append("\n"+mesg+"\n"+tm+" received");
								
								
							} catch (Exception e1) {
								System.out.println(e1);
							}
							}
						}
					
					
				
				}else { //connect!!!
					
					
					initInpText();
					byte[] buf = new byte[1024];
					PreparedStatement pstmt = conn.prepareStatement(SQL_SCAN);
					pstmt.setString(1, kw);
					ResultSet rs = pstmt.executeQuery();
					rs.next();
					
					
					
					String v3 = rs.getString("ip");
					String v4 = rs.getString("port");
					int p1 = Integer.parseInt(v4);
					showText.append(v3+ " " +v4+"\n連線成功！");
					String kwR = kw+"_1";
					String mesg = "連線成功！";
					byte[] data = mesg.getBytes();
					int ttt;
					while (true) {
						ttt = (int)((Math.random()*9999));
						if(ttt>5000) {
							System.out.println(ttt);
							break;
						}
					}
					try {
						DatagramSocket socket = new DatagramSocket(p1); 
						
						DatagramPacket packet = new DatagramPacket(data, data.length,
								InetAddress.getByName(v3), p1);
						socket.send(packet);
						socket.close();
						pstmt.close();
						PreparedStatement pstmti = conn.prepareStatement(SQLINSERT);
						
						String tt1 = String.valueOf(ttt);
						pstmti.setString(1, kwR);
						pstmti.setString(2, schIp());
						pstmti.setString(3, tt1);
						pstmti.executeUpdate();
						
						
					} catch (Exception e) {
						System.out.println(e);
					}
					while(true) {
						try {
							DatagramSocket socket = new DatagramSocket(ttt);
							DatagramPacket packet = new DatagramPacket(buf, buf.length);
							
							
							socket.receive(packet);
							socket.close();
							
							int len = packet.getLength();
							byte[] data2 = packet.getData();
							String mesg2 = new String(data2, 0, len);
							showText.append("\n"+mesg2+"\n"+tm+" received");
							
							
						} catch (Exception e1) {
							System.out.println(e1);
						}
						}

						
					
					
					
					
					
					
				}
		
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		}
		
	class testThread extends Thread {
		private String name;
		public testThread(String name) {
			this.name = name;
		}
		public void run() {
			try {
				estConnect();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	

		
	}
	}
	
	private static boolean checkIp(String kw) {
		boolean ret = false;
		
		try{PreparedStatement pstmt = conn.prepareStatement(SQL_SCAN);
		pstmt.setString(1, kw);
		ResultSet rs = pstmt.executeQuery();
		if(rs.next()) {
		ret = rs.getInt("count") == 0;

		pstmt.close();
		}else {
			ret = true;
		}
		}catch(Exception e) {
		
		}
		
		return ret;
		
	}
	private static boolean checkIpR(String kwR) {
		boolean ret = false;
		
		try{PreparedStatement pstmt = conn.prepareStatement(SQL_SCAN);
		pstmt.setString(1, kwR);
		ResultSet rs = pstmt.executeQuery();
		if(rs.next()) {
		ret = rs.getInt("count") == 0;

		pstmt.close();
		}else {
			ret = true;
		}
		}catch(Exception e) {
		
		}
		
		return ret;
		
	}
	public static  String schIp() throws IOException {
		try (Socket socket = new Socket()) {
		    socket.connect(new InetSocketAddress("google.com", 80));
		    String ip =socket.getLocalAddress().getHostAddress();
		    return ip;
		}
	}

		
	
	public static void main(String[] args) throws Exception {
		new Sam_Ip_Lab_Receive_final();
		
		
		
	}
		
}

