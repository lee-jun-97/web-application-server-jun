package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import db.DataBase;
import model.User;

public class HttpRequestUtils {
	
	private static final Logger log = LoggerFactory.getLogger(HttpRequestUtils.class);
    /**
     * @param queryString은
     *            URL에서 ? 이후에 전달되는 field1=value1&field2=value2 형식임
     * @return
     */
    public static Map<String, String> parseQueryString(String queryString) {
        return parseValues(queryString, "&");
    }

    /**
     * @param 쿠키
     *            값은 name1=value1; name2=value2 형식임
     * @return
     */
    public static Map<String, String> parseCookies(String cookies) {
        return parseValues(cookies, ";");
    }

    private static Map<String, String> parseValues(String values, String separator) {
        if (Strings.isNullOrEmpty(values)) {
            return Maps.newHashMap();
        }

        String[] tokens = values.split(separator);
        return Arrays.stream(tokens).map(t -> getKeyValue(t, "=")).filter(p -> p != null)
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }

    static Pair getKeyValue(String keyValue, String regex) {
        if (Strings.isNullOrEmpty(keyValue)) {
            return null;
        }

        String[] tokens = keyValue.split(regex);
        if (tokens.length != 2) {
            return null;
        }

        return new Pair(tokens[0], tokens[1]);
    }

    public static Pair parseHeader(String header) {
        return getKeyValue(header, ": ");
    }

    public static class Pair {
        String key;
        String value;

        Pair(String key, String value) {
            this.key = key.trim();
            this.value = value.trim();
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Pair other = (Pair) obj;
            if (key == null) {
                if (other.key != null)
                    return false;
            } else if (!key.equals(other.key))
                return false;
            if (value == null) {
                if (other.value != null)
                    return false;
            } else if (!value.equals(other.value))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "Pair [key=" + key + ", value=" + value + "]";
        }
    }
    
    public static String[] getHeaderData(String httpHeader) {
    	
    	return httpHeader.split(" ");
    }
    
    public static Map<String, String> parseParam(String str) {
    	
    	int index = str.indexOf("?");
    	
    	String param = str.substring(index+1);
    	
    	return parseQueryString(param);
    }
    
    
    public static User saveUser(Map<String, String> param) {
    	return new User(param.get("userId"), param.get("password"), param.get("name"), param.get("email"));
    }
    
    public static boolean loginCheck(User user) {
    	
    	String id = user.getUserId();
    	String pw = user.getPassword();
    	
    	return pw.equals(DataBase.findUserById(id).getPassword());
    }
    
    public static Map<String, String> parseHeader(String line, BufferedReader bufReader) {
    	
    	Map<String, String> map = new HashMap<>();
    	
    	try {
    		
	    	while(!" ".equals(line)) {
	    		
				line = bufReader.readLine();
	    		
	    		String[] param = getHeaderData(line);
	    		
	    		if ( param[0].equals("Content-Length:") ) {
	    			map.put("content_length", param[1]);
	    		}
	    		
	    		if ( param[0].equals("Cookie:") ) {
	    			map.put("cookie", param[1]);
	    		}
	    	}
    	} catch (IOException e) {
    		e.getMessage();
    	}
    	
    	return map;
    }
    
    public static StringBuilder makeTable() {
    	StringBuilder strbuilder = new StringBuilder();
		
		strbuilder.append("<table border='1'>");
        for (User i : DataBase.findAll()) {
        	strbuilder.append("<tr>");
        	strbuilder.append("<td>" + i.getUserId() + "</td>");
        	strbuilder.append("<td>" + i.getName() + "</td>");
        	strbuilder.append("<td>" + i.getEmail() + "</td>");
        	strbuilder.append("</tr>");
        }
        strbuilder.append("</table>");
        
        return strbuilder;
    }
    
}
