package com.team1.epilogue.auth.repository;

import com.team1.epilogue.auth.entity.Member;
import java.util.List;

public interface CustomMemberRepository {
  List<Member> findByLoginIdContains(String loginIdPart);
}
