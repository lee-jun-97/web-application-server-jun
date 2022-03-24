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
            
            String line = bufReader.readLine();
            String[] param = HttpRequestUtils.getHeaderData(line);
            byte[] body = "Hello World".getBytes();
            
            int content_length = 0;
            
            User user = new User("","","","");
            
            if ( line.contains("?") ) {
            	Map<String, String> paramMap = HttpRequestUtils.parseParam(param[1]);
            	user = HttpRequestUtils.saveUser(paramMap);
            }
            
            if ( !param[1].equals("/") ) {
            	
            	while (!"".equals(line)) { 
                	line = bufReader.readLine();
                	param = HttpRequestUtils.getHeaderData(line);
                	if ( param[0].equals("Content-Length:") ) {
                		content_length = Integer.parseInt(param[1]);
                	}
                	if ( line == null ) {
                		return ;
                	}
                }
            	
            	body = Files.readAllBytes(new File("./webapp" + param).toPath());
            }
            
            if ( param[0].equals("POST") ) {
            	line = IOUtils.readData(bufReader, content_length);
            	Map<String, String> userMap = HttpRequestUtils.parseQueryString(line);
            	user = HttpRequestUtils.saveUser(userMap);
            }
            
            System.out.println(user.toString());
            
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
           
            response200Header(dos, body.length);
            responseBody(dos, body);
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
}
