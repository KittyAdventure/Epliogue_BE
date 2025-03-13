package com.team1.epilogue.auth.repository;

import com.team1.epilogue.auth.entity.Member;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * [클래스 레벨]
 * MemberRepository 인터페이스는 Member 엔티티에 대한 CRUD 작업을 수행하는 JPA Repository
 * 스프링 데이터 JPA가 자동으로 구현체를 생성
 */
@Repository
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

    /**
     * 사용자 정보 Lock 을 걸기위한 메서드. 이 메서드로 호출한 Member 정보는 해당 작업이 끝날때까지
     * <쓰기> 에 대한 접근이 제한된다.
     *
     * @param loginId 사용자 ID
     * @return 사용자 정보를 Optional 에 담아 return
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM Member m WHERE m.loginId = :loginId")
    Optional<Member> findByLoginIdWithLock(String loginId);

    boolean existsByNickname(String nickname);

    Optional<Member> findByNickname(String nickname);


}
