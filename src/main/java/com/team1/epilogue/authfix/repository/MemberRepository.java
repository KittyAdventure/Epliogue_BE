package com.team1.epilogue.authfix.repository;

import com.team1.epilogue.authfix.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * [클래스 레벨]
 * MemberRepository 인터페이스는 Member 엔티티에 대한 CRUD 작업을 수행하는 JPA Repository
 * 스프링 데이터 JPA가 자동으로 구현체를 생성
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * [메서드 레벨]
     * findByLoginId 메서드는 주어진 loginId에 해당하는 Member 엔티티를 조회
     *
     * @param loginId 회원의 로그인 ID
     * @return Optional<Member> 객체로, 해당 회원이 존재하면 값을 포함하고, 없으면 비어있음
     */
    Optional<Member> findByLoginId(String loginId);

    /**
     * [메서드 레벨]
     * findByEmail 메서드는 주어진 이메일에 해당하는 Member 엔티티를 조회
     *
     * @param email 회원의 이메일
     * @return Optional<Member> 객체로, 해당 회원이 존재하면 값을 포함하고, 없으면 비어있음
     */
    Optional<Member> findByEmail(String email);

    /**
     * [메서드 레벨]
     * existsByLoginId 메서드는 주어진 loginId를 가진 회원이 존재하는지 확인
     *
     * @param loginId 회원의 로그인 ID
     * @return 존재하면 true, 없으면 false를 반환
     */
    boolean existsByLoginId(String loginId);

    /**
     * [메서드 레벨]
     * existsByEmail 메서드는 주어진 이메일을 가진 회원이 존재하는지 확인
     *
     * @param email 회원의 이메일
     * @return 존재하면 true, 없으면 false를 반환
     */
    boolean existsByEmail(String email);
}
