package hello.hellospring.controller;

import hello.hellospring.domain.Member;
import hello.hellospring.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@CrossOrigin(originPatterns = "http://localhost:8080")
@RestController
@RequestMapping("/api")
@Controller
public class AccountController {
    private final MemberService memberService;

    public AccountController(MemberService memberService) {
        this.memberService = memberService;
    }

    /*@PostMapping("/account")
    public Member create(@RequestBody String email,
                         @RequestBody String name,
                         @RequestBody String user_name,
                         @RequestBody String password) {
        System.out.println("받은 ID : " + email + " 받은 PW : " + password);
        Member member = new Member();
        member.setEmail(email);
        member.setName(user_name);
        member.setPassword(password);
        member.setPhoneNumber(name);
        memberService.join(member);
        return member;
    }*/

    @PostMapping("/account")
    public Member create(@RequestBody HashMap<String, String> requestJsonHashMap) {
        // System.out.println("받은 ID : " + email + " 받은 PW : " + password);

        Member member = new Member();
        member.setEmailOrPhone(requestJsonHashMap.get("emailOrPhone"));
        member.setName(requestJsonHashMap.get("user_name"));
        member.setNickname(requestJsonHashMap.get("nickname"));
        member.setPassword(requestJsonHashMap.get("password"));
        member.setOpen(1L);

        String str = member.getEmailOrPhone();

        if(member.getEmailOrPhone().contains("@")) {
            member.setEmail(str);
            member.setPhoneNumber("");
            System.out.println(str);
        }
        else {
            member.setPhoneNumber(str);
            member.setEmail("");
            System.out.println(str);
        }

        memberService.join(member);
        return member;
    }
}
