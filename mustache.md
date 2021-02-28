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

<br>

- ### 전체 조회 화면   

<br>

`mustache화면의 ui를 변경한다.`    

`{{#posts}}문법은 posts라는 list를 순회하는 것과 같다. (for문과 동일하다.)`   

```html

// 생략   

<tobody id="tbody">
{{#posts}}
    <tr>
        <td>{{id}}</td>
        <td>{{title}}</td>
        <td>{{author}}</td>
        <td>{{modifiedDate}}</td>
    </tr>
{{/posts}}
</tobody>
```     

<br>

`interface에 쿼리 추가.`   

`SpringDataJpa에서 제공하지 않는 메소드이기 때문에 @Query를 사용.`   

```java
public interface PostsRepository extends JpaRepository<Posts, Long> {
    @Query("select p from Posts p order by p.id desc")
    List<Posts> findAllDesc();
}
```   

<br>

`PostsService 코드 추가.`   

`interface에 받아온 데이터를 list로 변환`   

* Transactional(readOnly = true)의 역할은 트랜잭션의 범위는 유지하고, 조회 기능만 남겨두어 조회 속도의 개선을 위함.   

* .map(PostsListResponseDto::new) 는 postsRepository결과로 넘어온 Posts의 Stream을 map을 통해서 list로 변환하는 것.   

```java
@RequiredArgsConstructor // 생성자로 Bean을 주입받는 역할
@Service
public class PostsService {
    // 생략
  
  @Transactional(readOnly = true)
  public List<PostsListResponseDto> findAllDesc() {
    return postsRepository.findAllDesc().stream()
            .map(PostsListResponseDto::new)
            .collect(Collectors.toList());
  }
}
```   

<br>

`이후 Controller을 변경한다.`   


* Model 은 서버 템플릿 엔진에서 사용할 수 있는 객체를 저장한다.   
  postsService.findAllDesc()로 가져온 결과를 posts로 index.mustache에 전달한다.   
  

  
```java
@RequiredArgsConstructor
@Controller
public class IndexController {

  private final PostsService postsService;

  @GetMapping("/")
  public String index(Model model) {
    model.addAttribute("posts", postsService.findAllDesc());
    return "index";
  }
}
```

<br>

*이후의 수정, 삭제도 비슷한 과정을 거쳐서 controller를 구성한다.*   

<br><br>


>## 정리   
>#### 머스테치를 이용해서 view를 구성했고, 이와 controller, service의 구분을 명확히 하게 되었다.   
>#### 아직 헷갈리는 부분이 있으므로, 이 구성을 보고 계속해서 생각할 것.