package com.team1.epilogue.auth.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
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
public class CustomBookRepositoryImpl implements CustomMemberRepository{

  private final JPAQueryFactory queryFactory;


  @Override
  public Page<Member> searchMembers(String searchType, String keyword, Pageable pageable, Boolean hasProfileUrl, String sortType) {
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

    if(hasProfileUrl != null){
      if(hasProfileUrl) {
        booleanBuilder.and(member.profileUrl.isNotNull());
      } else {
        booleanBuilder.and(member.profileUrl.isNull());
      }
    }

    OrderSpecifier<?> orderSpecifier;
    switch(sortType) {
      case "oldest":
        orderSpecifier = member.createdAt.asc(); // 오래된 가입순
        break;
      case "newest":
        orderSpecifier = member.createdAt.desc(); // 최신 가입순
        break;
      default:
        orderSpecifier = member.id.desc(); // 기본 정렬 (id 역순)
    }


    List<Member> results = queryFactory
        .selectFrom(member)
        .where(booleanBuilder)
        .orderBy(orderSpecifier) // 정렬 적용
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
