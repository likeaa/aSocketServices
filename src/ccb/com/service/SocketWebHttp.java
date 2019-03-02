package ccb.com.service;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

public class SocketWebHttp extends Thread{
	boolean started = false;
	ServerSocket ss = null;
	Logger log=Logger.getLogger(getClass());

	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.startServerSocket();
	}

	public void startServerSocket() {

		try {
			ss = new ServerSocket(7777);
			started = true;
		} catch (BindException e) {
			log.error(e.getMessage(),e);
			log.info("端口使用中....");
			log.info("请关掉相关程序并重新运行服务器！");
			System.exit(0);
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
		try {
			while(started) {
				Socket s = ss.accept();
				ClientSocket c = new ClientSocket(s);
				log.info("ip:"+s.getInetAddress()+"   port:"+s.getPort());
				new Thread(c).start();
			}
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
		}
	}


	/**
	 * 内部类：连上来的socket
	 *
	 */
	class ClientSocket implements Runnable {
		private Socket s;
		private BufferedReader br = null;
		private PrintWriter pw = null;
		private boolean bConnected = false;

		public ClientSocket(Socket s) {
			this.s = s;
			try {
				br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				pw = new PrintWriter(s.getOutputStream(),true);
				bConnected = true;
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
		}

		/**
		 * 向客户端socket发送消息
		 * @param str
		 */
		public void send(String str) {
			try {
				String result = WebHttpManager.getWebService().callServiceMethod(str);
				log.info(s.getInetAddress()+"  "+s.getPort()+"  "+"send:"+result);
				pw.println(result);
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				log.info(s.getInetAddress()+"  "+s.getPort()+"  "+":"+"exit！");
			}
		}


		/**
		 * 读取请求报文
		 */
		public void run() {
			try {
				while(bConnected) {
					String str = br.readLine();
					if(null==str){
						log.info(s.getInetAddress()+"  "+s.getPort()+"  "+":"+"exit！");
						break;
					}
					log.info(s.getInetAddress()+"  "+s.getPort()+"  "+":"+str);
					this.send(str);
					bConnected = false;
				}
			} catch (EOFException e) {
				log.error(e.getMessage(),e);
				log.info(s.getInetAddress()+"  "+s.getPort()+"  "+":"+"exit！");
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			} finally {
				try {
					log.info("a socket had finished");
					if(br != null) br.close();
					if(pw != null) pw.close();
					if(s != null)  {
						s.close();
					}
				} catch (IOException e1) {
					log.error(e1.getMessage(),e1);
				}
			}
		}
	}


}
