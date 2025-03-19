package com.team1.epilogue.auth.repository;

import com.team1.epilogue.auth.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomMemberRepository {
  Page<Member> searchMembers(String searchType, String keyword, Pageable pageable, Boolean hasProfileUrl, String sortType);

}
