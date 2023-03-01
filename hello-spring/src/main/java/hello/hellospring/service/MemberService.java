package hello.hellospring.service;

import hello.hellospring.Dto.TokenInfo;
import hello.hellospring.Token.JwtProvider;
import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.support.NullValue;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;

    public Long join(Member member) {
        Optional<Member> result;
        if(!member.getEmail().isEmpty()) {
            result = memberRepository.findByEmail(member.getEmail());
        }
        else {
            result = memberRepository.findByPhoneNumber(member.getPhoneNumber());
        }

        Optional<Member> result1 = memberRepository.findByNickname(member.getNickname());

        result.ifPresent(m -> {
            throw new IllegalStateException("이미 존재하는 이메일 또는 전화번호입니다.");
        });

        result1.ifPresent(m -> {
            throw new IllegalStateException("이미 존재하는 사용자 이름 입니다.");
        });

        memberRepository.save(member);
        return member.getId();
    }

    public Optional<Member> login(String Id, String pw) {
        Optional<Member> result;

        int C = 0;

        if(Id.contains("@")) {
            C = 1;
        }
        else if(validPhoneNum(Id)){
            C = 2;
        }
        else {
            C = 3;
        }

        result = memberRepository.findByLogin(pw, Id, C);

        if(result.isEmpty()) {
            throw new IllegalStateException("잘못된 회원정보입니다.");
        }

        System.out.println("로그인 성공입니다.");
        return result;
    }

    public boolean validPhoneNum(String num) {
        Pattern pattern = Pattern.compile("\\d{11}");
        Matcher matcher = pattern.matcher(num);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public TokenInfo login2(String memberId, String password) {
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberId, password);

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenInfo tokenInfo = jwtProvider.generateToken(authentication);

        return tokenInfo;
    }


    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Optional<Member> findOne(Long memberId){
        return memberRepository.findByid(memberId);
    }
}
