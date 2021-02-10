package springboot.web;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// 컨트롤러를 JSON을 반환한느 컨트롤러로 만들어 준다.
// @ResponseBody를 각 메소드마다 선언한 것을 한번에 사용하는 것.
@RestController
public class HelloController {

    // HTTP Method Get의 요청을 받을 수 있는 API를 만든다.
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }
}
