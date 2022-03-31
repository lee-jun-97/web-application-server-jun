* Forked from slipp/web-application-server

# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* HTTP Header는 여러 줄로 이루어져 있으며 첫 줄에서 index.html 을 parsing 가능.
* Application을 기동시키고 HTTP 요청이 Thread-0, Thread-1 총 2 번이 들어옴.

### 요구사항 2 - get 방식으로 회원가입
* GET 방식은 HTTP Header에 QueryString 으로 Parameter가 주소창에 노출되어 입력된다.

### 요구사항 3 - post 방식으로 회원가입
* POST 방식은 GET 방식과 다르게 HTTP Body 부분에 Parameter가 입력된다.
* HTTP Body 부분을 readData Method를 활용해 읽기 위해서는 먼저 HTTP Header 부분을 while문을 통해 모두 읽은 후에 읽을 수 있다.

### 요구사항 4 - redirect 방식으로 이동
* HTTP Header에 Location: {URL} 작성 시 대소문자 구분함. URL 작성 유의
* localhost:8080/index.html로 Location 작성 시 파일 찾지 못하여 ../index.html로 전달해주었음.


### 요구사항 5 - cookie
* ID, PW 비교하여 cookie를 HTTP Header 부분에 Set-Cookie: 를 추가하여 cookie 정보 전달함.
* Set-Cookie Header 추가하여 응답 주니 Cookie: logined=~~~; logined=~~~~; 총 2개가 나옴
* Cookie 값에 따라 list.html/login.html로 이동하는 것은 성공하였음
* StringBuilder 이용하여 Body 부에 내용 전달해 주었는데 웹 페이지에서 보이지 않음.

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 
