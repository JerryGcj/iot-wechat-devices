package com.wechat.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

/**
 * IP工具类
 * 
 * @author ywy
 *
 */
public class IPUtil {
	/**
	 * 获得真实IP地址
	 * 
	 * @param request 请求数据
	 * @return IP
	 */
	public static String getIpAddress(HttpServletRequest request) {
		// Squid代理请求头，只有通过HTTP代理或者负载均衡服务器时才有该项
		String ip = request.getHeader("X-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			// apache http代理请求头
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			// apache weblogic插件请求头
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		// 没有使用代理服务
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		// 如果是通过多个反向代理会有多个ip值，第一个ip才是真实ip
		if (ip != null && ip.length() > 15) {
			if (ip.indexOf(",") > 0) {
				ip = ip.substring(0, ip.indexOf(","));
			}
		}
		if (ip.equals("0:0:0:0:0:0:0:1")) {
			ip = "127.0.0.1";
		}
		return ip;
	}
}
