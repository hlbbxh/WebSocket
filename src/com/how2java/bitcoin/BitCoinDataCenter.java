package com.how2java.bitcoin;

import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

/**
 * @author egrant13
 * 创建BitCoinDataCenter，使其继承HttpServlet.
 * 标记为Servlet不是为了其被访问，而是为了便于伴随Tomcat一起启动，因为可以通过loadOnStartup一起就启动了
 * 这个类实现了Runnable，可以在初始化方法里创建一个线程并调用之。
 * run 方法： 每个1-3秒就创建一个新价格，然后根据当前有多少人链接过来，进行调整价格，接着通过ServerManager广播出去。 这样浏览器就看到如如图所示的效果了
 */
//标记为Servlet不是为了其被访问，而是为了便于伴随Tomcat一起启动
@WebServlet(name="BitCoinDataCenter",urlPatterns = "/BitCoinDataCenter",loadOnStartup=1)
public class BitCoinDataCenter extends HttpServlet implements Runnable{
	
	public void init(ServletConfig config){
		//startup();
	}
	
	public void startup(){
		new Thread(this).start();
	}
	@Override
	public void run() {
		int bitPrice = 100000;
		while(true){
			//每隔1-3秒就产生一个新价格
			//int duration = 1000+new Random().nextInt(2000);
			try {
				//线程睡觉随机数
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//新价格围绕100000左右50%波动
			float random = 1+(float) (Math.random()-0.5);
			int newPrice = (int) (bitPrice*random);
			
			//查看的人越多，价格越高
			int total = ServerManager.getTotal();
			System.out.println("===========================>>>在线人数：" + total);
			newPrice = newPrice*total;
			String messageFormat = "{\"price\":\"%d\",\"total\":%d}";
			String message = String.format(messageFormat, newPrice,total);
			System.out.println("message:" + message);
			//广播出去
			ServerManager.broadCast(message);
			if(newPrice==0){
				System.out.println("当前已经没有人数连接...");
				break;
			}
		}
	}
}
