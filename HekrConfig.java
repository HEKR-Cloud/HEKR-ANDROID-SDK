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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * HekrConfig.java created by durianskh at Oct 30, 2015 6:49:19 PM 这里对类或者接口作简要描述
 *
 * @author durianskh
 * @version 1.0
 * 
 */

public class HekrConfig {

	/**
	 * 原子变量，决定是否需要继续发送ssid和password
	 */
	private AtomicBoolean isSsidAndPassOK = new AtomicBoolean(false);

	/**
	 * 原子变量，决定是否需要继续发送ak
	 */
	private AtomicBoolean isAccessKeyOK = new AtomicBoolean(false);

	/**
	 * 原子变量，判断线程是否全部停止
	 */
	private AtomicBoolean done = new AtomicBoolean(false);

	/**
	 * 原子变量，返回的最终结果
	 */
	private AtomicReference<DatagramPacket> result = null;

	/**
	 * dak
	 */
	private final String ak;

	public HekrConfig(String accesskey) {
		this.ak = accesskey;
	}

	/**
	 * 获取发送经历的时间
	 * 
	 * @param beginTime
	 * @return
	 */
	private long getPassTime(long beginTime) {
		return System.currentTimeMillis() - beginTime;
	}

	/**
	 * 获取需要等待的时间，该变量变化不大，有时间可以多测试，写一个更好的公式
	 * 
	 * @param passTime
	 * @param length
	 * @return
	 */
	private long getSleepTime(long passTime, int length) {
		long param = passTime / 1000 - 3 > 0 ? passTime / 1000 - 3 : 0;
		long time = 100 / length * (1 + param / 6);
		return time;
	}

	/**
	 * 具体执行配置的过程，启动两个线程，一个线程用于发送ssid和pass，一个线程用于发送ak
	 * 
	 * @param ssid
	 * @param password
	 * @return
	 */
	public Object config(final String ssid, final String password) {

		// 发送ssid和pass的线程
		new Thread() {
			public void run() {
				byte[] data = (ssid + '\0' + password + '\0').getBytes();
				long beginTime = System.currentTimeMillis();
				long passTime = getPassTime(beginTime);
				while (!isSsidAndPassOK.get()) {
					long sleepTime;
					if (passTime > 1000) {
						sleepTime = getSleepTime(passTime, data.length);
					} else {
						sleepTime = getSleepTime(passTime, data.length + 1);
					}
					try {
						UDPConfig.hekrconfig(ssid + "", password + "", (int) sleepTime);
						Thread.sleep(sleepTime);
					} catch (Exception e) {
					}
                    passTime = getPassTime(beginTime);
				}
			}
		}.start();

		// 发送ak的线程
		new Thread() {
			public void run() {
				DatagramPacket dp;
				while (!isAccessKeyOK.get()) {
					try {
						// 这里使用的是固定的时间
						dp = UDPConfig.waitDevice(200);
						String str = new String(dp.getData());
						if (str.startsWith("(deviceACK ")) {
							result = new AtomicReference<DatagramPacket>(dp);
							isSsidAndPassOK.set(true);
							isAccessKeyOK.set(true);
							done.set(true);
						}
					} catch (IOException e) {
						try {
							// 这个80毫秒没有卵用
							dp = UDPConfig.setAccessKey("255.255.255.255", ak, null, 80);
							String str = new String(dp.getData());
							if (str.startsWith("(deviceACK ")) {
								result = new AtomicReference<DatagramPacket>(dp);
								isSsidAndPassOK.set(true);
								isAccessKeyOK.set(true);
								done.set(true);
							}
						} catch (IOException e1) {
						}
					}
				}
				if (!done.get()) {
					result = null;
					done.set(true);
				}
			}
		}.start();

		int count = 0;
		try {
			// 120*500毫秒是1分钟，这是配置的总体超时时间
			// 在1分钟内，不断去判断配置是否成功
			while ((!isSsidAndPassOK.get() || !isAccessKeyOK.get()) && count < 120) {
				// 每次判断之后，主线程休眠500毫秒
				Thread.sleep(500);
				count++;
			}
		} catch (Exception e) {
		}

		// timeout, stopping the two threads
		isSsidAndPassOK.set(true);
		isAccessKeyOK.set(true);
		// 等待所有的线程停止
		while (!done.get()) {
		}

		return result;
	}

    public void stop() {
        isSsidAndPassOK.set(true);
        isAccessKeyOK.set(true);
    }

	/**
	 * hekrconfig配置
	 * 
	 * @param ssid
	 * @param password
	 * @throws java.io.IOException
	 */
	public static void hekrconfig(String ssid, String password) throws IOException {
		UDPConfig.hekrconfig(ssid, password, 0);
	}

	public static boolean isSuccess(String respstr) {
		try {
			if (respstr != null) {
				JSONObject jo = (JSONObject) JSONObject.parse(respstr);
				int code = jo.getInteger("code");
				if (code == 0) {
					return true;
				}
			}
			return false;
		} catch (Exception ex) {
			return false;
		}
	}

//	public static void main(String[] args) throws Exception {
//		String ak = "azAxMWZzQmo3bHNlRytvUklYWTZsNVFKV09GeGdud29ZN1lKaXZ4bitOUnhIWDJDK0JYRHdrME5qbUQ1SGJ2aitr";
//		HekrConfigTest hc = new HekrConfigTest(ak);
//		long begin = System.currentTimeMillis();
//		hc.config("HEKR-C", "56781234");
//		System.out.println((System.currentTimeMillis() - begin) / 1000);
//	}
}
