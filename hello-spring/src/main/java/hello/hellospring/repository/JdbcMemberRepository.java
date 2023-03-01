package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcMemberRepository implements MemberRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcMemberRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Member save(Member member) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("member").usingGeneratedKeyColumns("memberNum");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nickName", member.getNickname());
        parameters.put("password", member.getPassword());
        parameters.put("email", member.getEmail());
        parameters.put("phoneNum", member.getPhoneNumber());
        parameters.put("user_name", member.getName());
        parameters.put("open",member.getOpen());
        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        member.setId(key.longValue());
        return member;
    }

    @Override
    public Optional<Member> findByid(Long id) {
        List<Member> result = jdbcTemplate.query("select * from member where memberNum = ?", memberRowMapper(), id);
        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        return jdbcTemplate.query("select * from member", memberRowMapper());
    }

    @Override
    public Optional<Member> findByNickname(String nickname) {
        List<Member> result = jdbcTemplate.query("select * from member where nickName = ?", memberRowMapper(), nickname);
        return result.stream().findAny();
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        List<Member> result = jdbcTemplate.query("select * from member where email = ?", memberRowMapper(), email);
        return result.stream().findAny();
    }
    @Override
    public Optional<Member> findByPhoneNumber(String phoneNumber) {
        List<Member> result = jdbcTemplate.query("select * from member where phoneNum = ?", memberRowMapper(), phoneNumber);
        return result.stream().findAny();
    }

    @Override
    public Optional<Member> findByLogin(String password, String id, int C) {
        List<Member> result;

        if(C == 1) {
            result = jdbcTemplate.query("select * from member where email = ? AND password = ?", memberRowMapper(), id, password);
        }
        else if(C == 2) {
            result = jdbcTemplate.query("select * from member where phoneNum = ? AND password = ?", memberRowMapper(), id, password);
        }
        else {
            result = jdbcTemplate.query("select * from member where nickName = ? AND password = ?", memberRowMapper(), id, password);
        }
        return result.stream().findAny();
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setId(rs.getLong("memberNum"));
            member.setNickname(rs.getString("nickName"));
            member.setPassword(rs.getString("password"));
            member.setEmail(rs.getString("email"));
            member.setPhoneNumber(rs.getString("phoneNum"));
            member.setName(rs.getString("user_name"));
            return member;
        };
    }
}