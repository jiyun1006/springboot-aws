>## 스프링 시큐리티   
>#### 스프링 시큐리티는 Authentication과 Authorization 기능을 가진 프레임워크이다.   
>#### 스프링 기반의 애플리케이션에서 보안을 위한 표준으로 쓰인다.   

<br>

### 스프링 부트 2.0   

스프링 부트 2.0으로 넘어오면서 OAuth2 연동방법이 변경되었다.   

스프링부트 2 방식인 Spring Security Oauth2 Client 라이브러리를 사용해서 진행한다.   

<br>

- 스프링 부트 1.5방식을 사용하지 않는 이유   

```
스프링 팀에서 1.5버전의 프로젝트는 유지 상태로 결정했고, 
신규 기능은 새 oauth2 라이브러리에만 지원하기로 선언.
```

<br>

### OAuth 클라이언트 ID

**google cloud platform**을 이용해서 **OAuth2.0 클라이언트 ID**를 만든다.     

생성된 클라이언트ID와 클라이언트 보안 비밀 코드를 프로젝트에 설정한다.    

<br>

**application-oauth.properites** 파일에 클라이언트 ID 와 보안 비밀 코드를 설정한다.    

코드 중, `scope=profile,email`의 이유는 openid를 기본값에서 빼주기 위함이다.   

**구글은 Openid provider 서비스이고, 네이버나 카카오는 그렇지 않기 때문에, 
각각 OAuth2Service를 만드는 과정을 거치지 않기 위함이다.**   

```
스프링부트에서의 application-xxx.properties 파일들은 xxx라는 이름의 porfile이 생성된다.   

따라서 이를 통해 관리할 수 있는데, profile=xxx와 같이 호출하여 해당 properties의 설정들을 가져올 수 있다.   
```

이제 구글에서 로그인 인증정보 발급 받는 절차가 다 끝났으니, 프로젝트를 구현 해야함.   

<br>
<br>


>### 구글 로그인 연동   

#### **사용자 정보**를 담당할 **User 클래스**를 생성한다.    

<br>

- `@Enumerated`    
  **JPA로 데이터베이스를 저장**할 때, **Enum값**을 어떤 형태로
  저장할지를 결정한다.    
  기본적으로는 int형이지만, 숫자로 저장된다면 값의 의미를 알기 힘들기 때문에, 문자열로 선언한다.   
  

```java

@Getter
@NoArgsConstructor
@Entity
public class User extends BaseTimeEntity {
    // 생략

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    // 생략
}
```   


<br>

#### 각 사용자의 권한을 관리할 **Enum 클래스 Role**을 생성.   

<br>

`권한 코드 앞에 항상 'ROLE_' 가  있어야 한다. 따라서 밑의 코드에서 키 값을 ROLE_GUEST, ROLE_USER 로 지정했다.`   


```java
@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("ROLE_GUEST", "손님"),
    USER("ROLE_USER", "일반 사용자");

    private final String key;
    private final String title;
}

```

<br>

#### User의 **CRUD**를 담당할 **인터페이스 UserRepository**를 생성.    

<br>

`소셜 로그인으로 반환되는 값 중, email을 통해 이미 생성된 사용자인지 처음 가입한 사용자인지 판단하는 메소드`   


```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```

<br>


### 스프링 시큐리티 설정   

#### 처음에 build.gradle에 스프링 시큐리티 의존성을 추가한다.   

- `spring-boot-starter-oauth2-client`   
소셜 로그인 등 클라이언트 입장에서 소셜 기능 구현시 필요한 의존성.   
**spring-security-oauth2-client**와 **spring-security-oauth2-jose**를 기본으로 관리.   
  
<br>

#### 새로운 패키지에 security config를 설정한다.   

- `@EnableWebSecurity`   
Spring Security 설정들을 활성화시켜 준다.   
  

- `csrf().disable().headers().frameOptions().disable()`   
h2-console화면을 사용하기 위해 해당 옵션들을 disable 한다.   
  
- `authorizeRequests`   
URL별 권한 관리를 설정하는 옵션의 시작점이다.   
authorizeRequests가 선언되야 antMatchers 옵션을 사용할 수 있다.   


- `antMatchers`   
권한 관리 대상을 지정하는 옵션   
  URL, HTTP 메소드별로 관리가 가능하다.   
  "/"등 지정된 URL들을 permitAll() 옵션으로 전체 열람 권한을 주었다.   
  "/api/v1/**" 주소를 가진 API는 USER권한을 가진 사람만 가능하게 함.   
  

- `anyRequest`   
설정된 값들 이외 나머지 URL들 설정한다.   
  authenticated()을 추가하여 나머지 URL들은 모두 인증된 사용자들에게만 허용하게 한다.    
  

- `logout().logoutSuccessUrl("/)`   
로그아웃 기능에 대한 여러 설정의 진입점.   
  로그아웃 성공 시 설정한 /  주소로 이동한다.   
  

- `oauth2Login`   
OAuth2 로그인 기능에 대한 여려 설정의 진입점.   
  

- `userInfoEndpoint`   
OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정을 담당.   
  

- `userService`   
소셜 로그인 성공 시 후속 조치를 진행할 UserService 인터페이스의 구현체를 등록.   
  리소스 서버에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능을 명시한다.   
  
  
  
```java
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfiguration {
    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**").permitAll()
                .antMatchers("/api/v1/**").hasRole(Role.USER.name())
                .anyRequest().authenticated()
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .userService(customOAuth2UserService);
    }
}

```

config를 and()로 계속해서 이어나가며, 지정하는 모습인 것 같다.   

기본적으로 spring security에 필요한 것을 활성화 시킨다음, `antMatchers`로   
권한 관리 대상을 지정하고, url에 대한 접근 권한을 설정한다.   

이후 logout에 대한 설정을 마치고, 로그인을 성공했을 때 지정할 인터페이스를 지정해준다.   

<br>

#### 설정을 마친 후에, 로그인 성공 후 지정한 **custom인터페이스**를 생성한다.   

<br>

- `registrationId`
현재 로그인 진행 중인 서비스를 구분하는 코드.   
  각각의 소셜 로그인을 구분하기 위해 사용한다. (네이버,구글 ,....)   
  

- `userNameAttributeName`   
OAuth2 로그인 진행 시 키가 되는 필드값을 말한다. (Primary Key)   
  구글의 기본 지원 코드는 "sub"이다. (네이버, 카카오 기본지원 x)   
  

- `OAuthAttributes`   
OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담을 클래스이다.   
  


- `SessionUser`   
세션에 사용자 정보를 저장하기 위한 Dto 클래스이다.   
  User클래스를 사용하지 않는 이유는, 다른 엔티티와의 관계 형성을 쉽게 하기 위함이다.   
  

  
  
```java
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
  private final UserRepository userRepository;
  private final HttpSession httpSession;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2UserService<OAuth2UserRequest, OAuth2User>
            delegate = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = delegate.loadUser(userRequest);

    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
            .getUserNameAttributeName();

    OAuthAttributes attributes = OAuthAttributes.
            of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

    User user = saveOrUpdate(attributes);

    httpSession.setAttribute("user", new SessionUser(user));

    return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
            attributes.getAttributes(),
            attributes.getNameAttributeKey());
  }
  // 생략
}

```   


<br>

위에서 사용된 OAuthAttributes 클래스를 생성한다.   

<br>

- `of()`   
OAuth2User에서 반환하는 사용자 정보는 Map이기 때문에 값 하나하나를 변환한다.   
  

- `toEntity()`   
User 엔티티를 생성한다.   
  OAuthAttributes에서 엔티티를 생성하는 시점은 처음 가입할 때이다.   
  가입할 때의 기본 권한을 GUEST로 주기 위해서 role 빌더값에는 Role.GUEST를 사용한다.   
  

```java
@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes,
                           String nameAttributeKey, String name,
                           String email, String picture){
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName,
                                     Map<String, Object> attributes){
        return ofGoogle(userNameAttributeName, attributes);
    }
    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes){
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public User toEntity(){
        return User.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .role(Role.GUEST)
                .build();
    }
}
```   

<br>

#### 인증된 사용자 정보만을 필요로하는 SessionUser 클래스를 생성한다.   

```java
@Getter
public class SessionUser {
    private String name;
    private String email;
    private String picture;

    public SessionUser(User user){
        this.name = user.getName();
        this.email = user.getEmail();
        this.picture = user.getPicture();
    }
}
```


