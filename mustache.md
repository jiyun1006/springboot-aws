# 머스테치 (Mustache)   

<br>

>### 템플릿 엔진   
>#### 지정된 템플릿 양식과 데이터가 합쳐져 HTML문서를 출력하는 소프트웨어   


<br>

- 서버 템플릿 엔진   
    - 서버에서 코드를 문자열로 만든 뒤 HTML로 변환하여 브라우저로 전달.   
    

<img src ="https://user-images.githubusercontent.com/52434993/109408212-85330480-79ca-11eb-91e2-0d09f6ff8352.png" width="780px">   


<br>

- 클라이언트 템플릿 엔진   
    - 서버에서 json 혹은 xml형식의 데이터만 넘기고 클라이언트에서 화면을 조립한다.   
    
<img src ="https://user-images.githubusercontent.com/52434993/109408226-ae539500-79ca-11eb-8581-27ce4c4d7a6e.png" width="780px">   

<br>


>### 머스테치    
>#### 스프링 부트 프로젝트에서 머스테치를 build.gradle에 등록 (머스테치 스타터 의존성을 등록하는 것. --> 편하게 사용하기 위함.)

<br>

- ### 머스테치 url 매핑    

<br>

`머스테치의 파일 위치는 src/main/resources/templates이다.`   

`머스테치 스타터 덕분에 앞의 경로와 뒤의 파일확장자는 자동으로 지정된다.`   

`따라서 src/main/resources/templates가 앞에 붙고, 뒤에 .mustache가 붙는다.`   

```java
@Controller
public class IndexController {
    
  // index만 반환해도 된다.  
  @GetMapping("/")
  public String index() {
    return "index";
  }
}
```   

<br>

- ### 게시글 작성 api   

<br>

`js파일을 만들어서 필요한 api를 작성한다.`    

`브라우저의 스코프는 공용공간으로 쓰이기 때문에 나중에 로딩된 js들이 먼저 로딩된 function을 덮어쓴다.`   

`따라서, 각각의 js의 유효범위를 설정해서 사용한다.`

<br>

*js파일 저장소 위치*   

```
|--main
       |--java
       |--resources
                   |--static
                            |--js
                                 |--app
                                       |--index.js
```   

<br>

`layout에서 만든 footer에 넣어준다.`   


```javascript
<script src="/js/app/index.js"></script>
```
*기본적으로 스프링 부트에서는 src/main/resources/static에 위치한 js, css 등의 정적파일들은 url에서 '/'로 설정된다.*     


