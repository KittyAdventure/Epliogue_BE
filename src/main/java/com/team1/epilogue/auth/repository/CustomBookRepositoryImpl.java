package com.team1.epilogue.auth.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.entity.QMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomBookRepositoryImpl implements CustomMemberRepository{

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Member> findByLoginIdContains(String loginIdPart) {
    QMember member = QMember.member;

    return queryFactory
        .selectFrom(member)
        .where(member.loginId.lower().contains(loginIdPart.toLowerCase())) // 대소문자 구분 없음, 포함검색
        .fetch();
  }
}
