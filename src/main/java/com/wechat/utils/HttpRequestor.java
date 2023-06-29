package com.wechat.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Map;

@Slf4j
public class HttpRequestor {


	/**
	 * 默认采用的http协议的HttpClient对象
	 */
	private static CloseableHttpClient httpClient;
	/**
	 * 默认采用的https协议的HttpClient对象
	 */
	private static CloseableHttpClient httpsClient;
	private static RequestConfig requestConfig;
	private static PoolingHttpClientConnectionManager connManager;
	private static final int MAX_TIMEOUT1 = 20000;
	//连接超时
	private static final int MAX_TIMEOUT2 = 20000;
	//请求超时
	private static final int MAX_TIMEOUT3 = 20000;
	public static String CHARSET_DEFAULT = "UTF-8";
	
	 private static String charset = "utf-8";
	 private static Integer connectTimeout = 10;
	 private static Integer socketTimeout = null;
	 private static String proxyHost = null;
	 private static Integer proxyPort = null;
	 
	 private static final int MAX_TOTAL_CONNECTION = 800;
		static {
			//采用绕过验证的方式处理https请求  
		    SSLContext sslcontext = createIgnoreVerifySSL();  
	       // 设置协议http和https对应的处理socket链接工厂的对象  
	       Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()  
	           .register("http", PlainConnectionSocketFactory.INSTANCE)
	           .register("https", new SSLConnectionSocketFactory(sslcontext))
	           .build();  
			// 设置连接池
			connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			
			// 设置连接池大小
			connManager.setMaxTotal(MAX_TOTAL_CONNECTION);
			connManager.setDefaultMaxPerRoute(300);
			// 设置连接超时	// 设置全局的标准cookie策略r.setCookieSpec(CookieSpecs.STANDARD_STRICT);
			/*设置ConnectionPoolTimeout：从连接池中取连接的超时时间;ConnectionPoolTimeoutException
			 *设置ConnectionTimeout：连接超时 ,这定义了通过网络与服务器建立连接的超时时间。ConnectionTimeoutException  ==>HttpHostConnectException
			 *设置 SocketTimeout：请求超时即读取超时 ,这定义了Socket读数据的超时时间，即从服务器获取响应数据需要等待的时间，此处设置为4秒。SocketTimeoutException*/
			requestConfig = RequestConfig.custom().setConnectionRequestTimeout(MAX_TIMEOUT1).setConnectTimeout(MAX_TIMEOUT2).setSocketTimeout(MAX_TIMEOUT3).build();
			//httpClient = HttpClients.custom().setConnectionManager(connManager).setDefaultRequestConfig(requestConfig).build();// 设置可关闭的HttpClient
			httpClient = HttpClients.custom().setConnectionManager(connManager).setConnectionManagerShared(true).setDefaultRequestConfig(requestConfig).build();
			
			httpsClient = HttpClients.createDefault();
		}
		/** 
		 * 绕过验证 
		 *   
		 * @return 
		 */  
		public static SSLContext createIgnoreVerifySSL() {  
		    SSLContext sc = null;
			try {
				sc = SSLContext.getInstance("SSL");
				// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法  
			    X509TrustManager trustManager = new X509TrustManager() {  
			        @Override  
			        public void checkClientTrusted(  
			                java.security.cert.X509Certificate[] paramArrayOfX509Certificate,  
			                String paramString) throws CertificateException {  
			        }  
			  
			        @Override  
			        public void checkServerTrusted(  
			                java.security.cert.X509Certificate[] paramArrayOfX509Certificate,  
			                String paramString) throws CertificateException {  
			        }  
			  
			        @Override  
			        public java.security.cert.X509Certificate[] getAcceptedIssuers() {  
			            return null;  
			        }  
			    };  
			    sc.init(null, new TrustManager[] { trustManager }, new java.security.SecureRandom());
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}catch (KeyManagementException e) {
				e.printStackTrace();
			}  
		    return sc;  
		} 
		/**
		 * 释放httpClient连接
		 */

		public static void shutdown() {
			//connManager.shutdown();
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 组装头部
		 * 
		 * @param map
		 *            头部map信息
		 * @return
		 */
		public static Header[] builderHeader(Map<String, String> map) {
			Header[] headers = new BasicHeader[map.size()];
			int i = 0;
			for (String str : map.keySet()) {
				headers[i] = new BasicHeader(str, map.get(str));
				i++;
			}
			return headers;
		}

	/**
     * Do GET request
     * @param url
     * @return
     * @throws Exception
     * @throws IOException
     */
    public static String doGet(String url){       
        URL localURL;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null; 
		try {
			localURL = new URL(url);
			URLConnection connection = openConnection(localURL);
		    HttpURLConnection httpURLConnection = (HttpURLConnection)connection;       
		    httpURLConnection.setRequestProperty("Accept-Charset", charset);
		    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");       
		               
		    if (httpURLConnection.getResponseCode() >= 300) {
		    	log.error("HTTP GET Request is not success, Response code is " + httpURLConnection.getResponseCode());
		    }        
		        	
		    inputStream = httpURLConnection.getInputStream();
		    inputStreamReader = new InputStreamReader(inputStream);
		    reader = new BufferedReader(inputStreamReader);            
		    while ((tempLine = reader.readLine()) != null) {
		    	resultBuffer.append(tempLine);
		    }
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {                       
                try {
                	if (reader != null){
                		reader.close();
					}
                	if (inputStreamReader != null){
                		inputStreamReader.close();
					}
                	if (inputStream != null){
                		inputStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}                                                  
        }
        return resultBuffer.toString();
    }
    
    /**
     * Do POST request
     * @param url
     * @param param
     * @return
     * @throws Exception 
     */
    public static String doPost(String url, String param){      
        URL localURL;
        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;  
		try {
			localURL = new URL(url);
			URLConnection connection = openConnection(localURL);
	        HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
	        
	        httpURLConnection.setConnectTimeout(30000);
	        httpURLConnection.setDoOutput(true);
	        httpURLConnection.setRequestMethod("POST");
	        httpURLConnection.setRequestProperty("Accept-Charset", charset);
	        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        httpURLConnection.setRequestProperty("Content-Length", String.valueOf(param.length()));
	        	             
	        outputStream = httpURLConnection.getOutputStream();
	        outputStreamWriter = new OutputStreamWriter(outputStream);           
	        outputStreamWriter.write(param);
	        outputStreamWriter.flush();           
	        if (httpURLConnection.getResponseCode() >= 300) {
	        	log.error("HTTP POST Request is not success, Response code is " + httpURLConnection.getResponseCode());
	        }
	            
	        inputStream = httpURLConnection.getInputStream();
	        inputStreamReader = new InputStreamReader(inputStream);
	        reader = new BufferedReader(inputStreamReader);
	            
	        while ((tempLine = reader.readLine()) != null) {
	        	resultBuffer.append(tempLine);
	        }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {                    
			try {
				if (outputStreamWriter != null){
					outputStreamWriter.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
				if (reader != null) {
					reader.close();
				}
				if (inputStreamReader != null) {
					inputStreamReader.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}           
        }

        return resultBuffer.toString();
    }
    
    private static URLConnection openConnection(URL localURL) throws IOException {
        URLConnection connection;
        if (proxyHost != null && proxyPort != null) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            connection = localURL.openConnection(proxy);
        } else {
            connection = localURL.openConnection();
        }
        return connection;
    }
   
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null; 
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int iresult = bis.read();
            while(iresult != -1) {
                buf.write((byte) iresult);
                iresult = bis.read();
            }
            result= buf.toString();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line = null;
            while ((line = in.readLine()) != null) {
                result += line;
            }

        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * 向指定 URL 发送POST方法的请求
	 * @param url	发送请求的 URL
     * @param param	请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPostGBK(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"GBK"));
            String line = null;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }
    
    /**
	 * 发送 POST 请求（HTTP），【查询参数使用json格式】，并返回字符串
	 * 
	 * @param url
	 *            请求地址
	 * @param json
	 *            请求json参数
	 * @return
	 * @throws IOException
	 */
	public static String post(String url, String json, String charSet) {
		Header[] headers = new Header[] {new BasicHeader("Content-Type", "application/json")};
		StringEntity stringEntity = new StringEntity(json, charSet);// 解决中文乱码问题
		stringEntity.setContentEncoding(charSet);
		String httpStr = null;
		CloseableHttpResponse response = null;
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(stringEntity);
		httpPost.setHeaders(headers);
		try {
			try {
				response = httpClient.execute(httpPost);
			}catch (ConnectionPoolTimeoutException pe) {//请求异常
				log.warn("==>ConnectionPoolTimeoutException异常");
			}catch (HttpHostConnectException ce) {//请求异常
				log.warn("==>HttpHostConnectException异常");
			}catch (SocketTimeoutException se) {//响应异常
				log.warn("==>SocketTimeoutException异常");
			} catch (Exception e) {//其他异常
				log.warn("==>Exception异常"+e.toString());
				e.printStackTrace();
			}
			HttpEntity entity = response.getEntity();
			try {
				httpStr = EntityUtils.toString(entity, charSet);
				log.info("响应结果"+httpStr);
			}catch (Exception e) {//其他异常
				log.warn("==>Exception异常"+e.toString());
				e.printStackTrace();
			}
		}finally {
			if (response != null) {
				try {
					//释放资源
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					//关闭CloseableHttpResponse
					try {
						response.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			httpPost.releaseConnection();
			//关闭HttpClient
			shutdown();
		}
		return httpStr;
	}
	
	public static String post(String url, String requestContext,Map<String, String> headers, String charSet){
		StringEntity stringEntity = new StringEntity(requestContext, charSet);// 解决中文乱码问题
		stringEntity.setContentEncoding(charSet);
		String httpStr = null;
		CloseableHttpResponse response = null;
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(stringEntity);
		httpPost.setHeaders(builderHeader(headers));
		try {
			try {
				if(url.toLowerCase().startsWith("https://")){
					response = httpsClient.execute(httpPost);
				}else{
					response = httpClient.execute(httpPost);
				}
			}catch (ConnectionPoolTimeoutException pe) {//请求异常
				log.warn("==>ConnectionPoolTimeoutException异常");
			}catch (HttpHostConnectException ce) {//请求异常
				log.warn("==>HttpHostConnectException异常");
			}catch (SocketTimeoutException se) {//响应异常
				log.warn("==>SocketTimeoutException异常");
			}  catch (Exception e) {//其他异常
				log.error("推送异常：", e);
			}
			HttpEntity entity = response.getEntity();
			try {
				httpStr = EntityUtils.toString(entity, charSet);
			}catch (Exception e) {//其他异常
				log.error("推送异常：", e);
			}
		}finally {
			if (response != null) {
				try {
					//释放资源
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					//关闭CloseableHttpResponse
					try {
						response.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			httpPost.releaseConnection();
			//关闭HttpClient
			shutdown();
		}
		return httpStr;
	}
}
