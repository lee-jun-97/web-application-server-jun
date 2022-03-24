package util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import model.User;
import util.HttpRequestUtils.Pair;

public class HttpRequestUtilsTest {
    @Test
    public void parseQueryString() {
        String queryString = "userId=javajigi";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is(nullValue()));

        queryString = "userId=javajigi&password=password2";
        parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is("password2"));
    }

    @Test
    public void parseQueryString_null() {
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(null);
        assertThat(parameters.isEmpty(), is(true));

        parameters = HttpRequestUtils.parseQueryString("");
        assertThat(parameters.isEmpty(), is(true));

        parameters = HttpRequestUtils.parseQueryString(" ");
        assertThat(parameters.isEmpty(), is(true));
    }

    @Test
    public void parseQueryString_invalid() {
        String queryString = "userId=javajigi&password";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is(nullValue()));
    }

    @Test
    public void parseCookies() {
        String cookies = "logined=true; JSessionId=1234";
        Map<String, String> parameters = HttpRequestUtils.parseCookies(cookies);
        assertThat(parameters.get("logined"), is("true"));
        assertThat(parameters.get("JSessionId"), is("1234"));
        assertThat(parameters.get("session"), is(nullValue()));
    }

    @Test
    public void getKeyValue() throws Exception {
        Pair pair = HttpRequestUtils.getKeyValue("userId=javajigi", "=");
        assertThat(pair, is(new Pair("userId", "javajigi")));
    }

    @Test
    public void getKeyValue_invalid() throws Exception {
        Pair pair = HttpRequestUtils.getKeyValue("userId", "=");
        assertThat(pair, is(nullValue()));
    }

    @Test
    public void parseHeader() throws Exception {
        String header = "Content-Length: 59";
        Pair pair = HttpRequestUtils.parseHeader(header);
        assertThat(pair, is(new Pair("Content-Length", "59")));
    }
    
    @Test
    public void getHeaderData() throws Exception{
    	
    	assertThat("POST", is(HttpRequestUtils.getHeaderData("POST /index.html asdfasfsadf")[0]));
    	assertThat("/index.html", is(HttpRequestUtils.getHeaderData("POST /index.html asdfasfsadf")[1]));
    	assertThat("asdfasfsadf", is(HttpRequestUtils.getHeaderData("POST /index.html asdfasfsadf")[2]));
    }
    
    @Test
    public void parseParam() throws Exception {
    	
    	String header = "GET /user/create?userId=asdfasdfasdf&password=password&name=java&email=java@gmail.com";
    	
    	Map<String, String> param = HttpRequestUtils.parseParam(header);
    	
    	assertThat("asdfasdfasdf", is(param.get("userId")));
    	assertThat("password", is(param.get("password")));
    	assertThat("java", is(param.get("name")));
    	assertThat("java@gmail.com", is(param.get("email")));
    }
    
    @Test
    public void saveUser() {
    	
    	Map<String, String> userMap = new HashMap<>();
    	
    	userMap.put("userId", "ID");
    	userMap.put("password", "password");
    	userMap.put("name", "name");
    	userMap.put("email", "email");
    	
    	User user = HttpRequestUtils.saveUser(userMap);
    	
    	assertThat("ID", is(user.getUserId()));
    	assertThat("password", is(user.getPassword()));
    	assertThat("name", is(user.getName()));
    	assertThat("email", is(user.getEmail()));
    	
    }
}
