package com.team1.epilogue.auth.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.entity.QMember;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomMemberRepositoryImpl implements CustomMemberRepository{

  private final JPAQueryFactory queryFactory;


  @Override
  public Page<Member> searchMembers(String searchType, String keyword, Pageable pageable) {
    QMember member = QMember.member;
    BooleanBuilder booleanBuilder = new BooleanBuilder();

    if (searchType != null && keyword != null && !keyword.isEmpty()) {
      switch (searchType) {
        case "loginId":
          booleanBuilder.and(member.loginId.likeIgnoreCase("%" + keyword + "%"));
          break;
        case "nickname":
          booleanBuilder.and(member.nickname.likeIgnoreCase("%" + keyword + "%"));
          break;
        case "email":
          booleanBuilder.and(member.email.likeIgnoreCase("%" + keyword + "%"));
          break;
        default:
          throw new IllegalArgumentException("지원되지 않은 검색 유형입니다. " + searchType);
      }
    }


    List<Member> results = queryFactory
        .selectFrom(member)
        .where(booleanBuilder)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long total = Optional.ofNullable(
        queryFactory
            .select(member.count())
            .from(member)
            .where(booleanBuilder)
            .fetchOne()
    ).orElse(0L);

    return new PageImpl<>(results, pageable, total);
  }
}
