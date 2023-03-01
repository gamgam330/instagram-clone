package hello.hellospring.controller;

import hello.hellospring.Dto.TokenInfo;
import hello.hellospring.domain.Member;
import hello.hellospring.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

@CrossOrigin(originPatterns = "http://localhost:8080")
@RestController
@RequestMapping("/api")
@Controller
public class LoginController {
    private final MemberService memberService;

    public LoginController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/insert")
    public void login(@RequestBody HashMap<String, String> requestJsonHashMap) {
        String Id = requestJsonHashMap.get("id");
        String Pw = requestJsonHashMap.get("password");

        //Optional<Member> result = memberService.login(Id, Pw);

        TokenInfo result = memberService.login2(Id, Pw);

        System.out.println("ID : " + Id + " PW : " + Pw);
    }
}
