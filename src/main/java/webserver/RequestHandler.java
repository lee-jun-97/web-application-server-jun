package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

	public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (
            InputStream in = connection.getInputStream();
            OutputStream out = connection.getOutputStream() ) {

            BufferedReader bufReader = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);
            
            String line = bufReader.readLine();
            String[] param = HttpRequestUtils.getHeaderData(line);
            
            byte[] body = "Hello World".getBytes();
            
            String httpBody = "";
            
            int content_length = 0;
            
            // GET 방식
            if ( line.contains("?") ) {
            	Map<String, String> paramMap = HttpRequestUtils.parseParam(param[1]);
            	User user = HttpRequestUtils.saveUser(paramMap);
            	DataBase.addUser(user);
            	response200Header(dos, body.length);
            }
            
            // POST 방식
            if ( !param[1].equals("/") ) {
            	if ( param[1].contains(".html") ) {
            		body = Files.readAllBytes(new File("./webapp" + param[1]).toPath());
            		response200Header(dos, body.length);
            		responseBody(dos, body);
            	}
            	
            	// 로그인 요청 시
            	if ( param[1].equals("/user/login") ) {
            		while (!"".equals(line)) { 
            			System.out.println(line);
	                	line = bufReader.readLine();
	                	param = HttpRequestUtils.getHeaderData(line);
	                	if ( param[0].equals("Content-Length:") ) {
	                		content_length = Integer.parseInt(param[1]);
	                	}
	                	if ( line == null ) {
	                		break ;
	                	}
	                }
	        		httpBody = IOUtils.readData(bufReader, content_length);
	        		Map<String, String> bodyMap = HttpRequestUtils.parseParam(httpBody);
	            	User user = HttpRequestUtils.saveUser(bodyMap);
	            	
	            	boolean login = HttpRequestUtils.loginCheck(user);
	            	
	            	if ( login == true ) {
	            		responseLoginTrueHeader(dos, content_length);
	            	}
	            	
	            	if ( login == false ) {
	            		responseLoginFalseHeader(dos, content_length);
	            	}
	            	responseBody(dos, body);
        		
            	}
            	
            	// 회원가입 요청 시
            	if ( param[1].equals("/user/create")) {
	            	while (!"".equals(line)) { 
	            		System.out.println(line);
	                	line = bufReader.readLine();
	                	param = HttpRequestUtils.getHeaderData(line);
	                	if ( param[0].equals("Content-Length:") ) {
	                		content_length = Integer.parseInt(param[1]);
	                	}
	                	if ( line == null ) {
	                		break ;
	                	}
	                }
	            	httpBody = IOUtils.readData(bufReader, content_length);
	            	Map<String, String> bodyMap = HttpRequestUtils.parseParam(httpBody);
	            	User user = HttpRequestUtils.saveUser(bodyMap);
	            	DataBase.addUser(user);
	            	response302Header(dos, httpBody.length());
	            	responseBody(dos, body);
            	}
            	
            	// List 조회 시 
            	if ( param[1].equals("/user/list") ) {
            		
            		while (!"".equals(line)) { 
            			System.out.println(line);
	                	line = bufReader.readLine();
	                	param = HttpRequestUtils.getHeaderData(line);
	                	if ( param[0].equals("Content-Length:") ) {
	                		content_length = Integer.parseInt(param[1]);
	                	}
	                	
	                	if ( param[0].equals("Cookie:") ) {
	                		Map<String, String> cookie = HttpRequestUtils.parseCookies(param[1]);
	                		
	                		if ( Boolean.parseBoolean(cookie.get("logined")) ) {
	                			responseListTrueHeader(dos, content_length);
	                			
	                			StringBuilder strbuilder = new StringBuilder();
	                			
	                			for ( User i : DataBase.findAll() ) {
	                				strbuilder.append(i);
	                			}
	                			
	                			responseListBody(dos, strbuilder);
	                		} else if ( !Boolean.parseBoolean(cookie.get("logined"))) {
	                			responseListFalseHeader(dos, content_length);
	                			responseBody(dos, body);
	                		}
	                	}
	                	if ( line == null ) {
	                		break ;
	                	}
	                }
            		
            	}
            }
            
            
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
	private void response302Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: ../index.html\r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("Set-Cookie: logined=false\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
	
	private void responseLoginTrueHeader(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: ../index.html\r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Set-Cookie: logined=true\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
	}
	
	private void responseLoginFalseHeader(DataOutputStream dos, int lengthOfBodyContent) {
		try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: ../user/login_failed.html\r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Set-Cookie: logined=false\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
	}
	
	
	private void responseListTrueHeader(DataOutputStream dos, int lengthOfBodyContent) {
		try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: ../user/list.html\r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
	}
	
	private void responseListFalseHeader(DataOutputStream dos, int lengthOfBodyContent) {
		try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: ../user/login.html\r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
	}
	
	private void responseListBody(DataOutputStream dos, StringBuilder strbuilder) {
		
	}
    
}
