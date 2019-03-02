package ccb.com.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import ccb.com.service.SocketWebHttp;

public class SocketServlet extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = 4606560043722069303L;
	private SocketWebHttp socketServices = null;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		this.socketServices = null;
		super.destroy();
	}


	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		socketServices = new SocketWebHttp();
		socketServices.setDaemon(true);
		socketServices.start();
	}

}
