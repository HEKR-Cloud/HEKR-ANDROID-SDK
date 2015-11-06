/**
 *
 * Copyright (c) Hekr.me Co.ltd Hangzhou, 2013-2015
 * 
 * 杭州第九区科技（氦氪科技）有限公司拥有该文件的使用、复制、修改和分发的许可权
 * 如果你想得到更多信息，请访问 <http://www.hekr.me>
 *
 * Hekr Co.ltd Hangzhou owns permission to use, copy, modify and
 * distribute this documentation.
 * For more information, please see <http://www.hekr.me>
 * 
 */

package com.hekr.android.app.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 
 * UDPConfig.java created by durianskh at Oct 29, 2015 3:14:16 PM 这里对类或者接口作简要描述
 *
 * @author durianskh
 * @version 1.0
 * 
 */
public class UDPConfig {

	/**
	 * 等待并获取设备发回的数据
	 * 
	 * @param timeout
	 * @return
	 * @throws java.io.IOException
	 */
	public static DatagramPacket waitDevice(int timeout) throws IOException {
		// 创建用来发送数据报包的套接字
		DatagramSocket ds = new DatagramSocket(10000);
		ds.setSoTimeout(timeout);
		byte[] bs = new byte[1024];
		DatagramPacket dp = new DatagramPacket(bs, bs.length);
		try {
			ds.receive(dp);
		} catch (IOException ex) {
			throw ex;
		} finally {
			ds.close();
		}
		return dp;
	}

	/**
	 * 发现设备
	 * 
	 * @param tid
	 * @param option
	 * @param timeout
	 * @return
	 * @throws java.io.IOException
	 */
	public static DatagramPacket discover(String tid, String option, int timeout)
			throws IOException {
		// 创建用来发送数据报包的套接字
		DatagramSocket ds = new DatagramSocket();
		ds.setSoTimeout(timeout);
		String data = "(discover \"" + tid + "\" \"" + option + "\" 10000 )";
		byte[] bs = data.getBytes();
		DatagramPacket dp = new DatagramPacket(bs, bs.length,
				InetAddress.getByName("255.255.255.255"), 10000);
		ds.send(dp);
		dp.setData(new byte[1024]);
		ds.close();
		return dp;
	}

	/**
	 * 
	 * @param ssid
	 * @param password
	 * @param time
	 *            每次发送之后休眠的时间，以免发送速度过快，路由器承受不住
	 * @throws java.io.IOException
	 */
	public static void hekrconfig(String ssid, String password, int time) throws IOException {
		// 创建用来发送数据报包的套接字
		DatagramSocket ds = new DatagramSocket();
		byte[] ssidbs = ssid.getBytes("utf-8");
		byte[] passbs = password.getBytes("utf-8");
		int len = ssidbs.length + passbs.length + 2;
		byte[] d = "hekrconfig".getBytes("utf-8");
		DatagramPacket dp;
		dp = new DatagramPacket(d, d.length, InetAddress.getByName("224.127." + len + ".255"),
				7001);
		ds.send(dp);
		ds.close();

		byte[] data = (ssid + '\0' + password + '\0').getBytes();

		for (int i = 0; i < data.length; i++) {
			ds = new DatagramSocket();
			dp = new DatagramPacket(d, d.length,
					InetAddress.getByName("224." + i + "." + unsignedByteToInt(data[i]) + ".255"),
					7001);
			ds.send(dp);
			ds.close();
			if (time > 0) {
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public static int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}

	/**
	 * 设置accesskey，这里只是发送ak，并不等待获取，发送之后立马返回
	 * 
	 * @param accesskey
	 * @param tid
	 *            可以为空，则设置所有设备
	 * @throws java.io.IOException
	 */
	public static DatagramPacket setAccessKey(String ip, String accesskey, String tid, int timeout)
			throws IOException {
		// 创建用来发送数据报包的套接字
		DatagramSocket ds = new DatagramSocket(10000);
		String data = "";
		// ds.setSoTimeout(timeout);
		if (tid == null) {
			data = "(ak \"" + accesskey + "\" )";
		} else {
			data = "(ak \"" + accesskey + "\" \"" + tid + "\" )";
		}
		byte[] bs = data.getBytes();
		DatagramPacket dp = null;
		try {

			dp = new DatagramPacket(bs, bs.length, InetAddress.getByName(ip), 10000);
			ds.send(dp);
		} catch (IOException ex) {
			throw ex;
		} finally {
			ds.close();
		}
		return dp;
	}

}