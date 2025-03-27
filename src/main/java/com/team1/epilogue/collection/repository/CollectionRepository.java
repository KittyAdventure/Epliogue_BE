package com.team1.epilogue.collection.repository;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.collection.entity.CollectionEntity;
import jakarta.persistence.Table;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Table(name = "collection")
@Repository
public interface CollectionRepository extends JpaRepository<CollectionEntity,Long> {

  @Query("SELECT c FROM CollectionEntity c JOIN FETCH c.book JOIN FETCH  c.member "
      + "WHERE c.member = :member")
  Page<CollectionEntity> findAllByMember(PageRequest pageRequest, Member member);

  @Modifying
  @Query("DELETE FROM CollectionEntity c WHERE c.member.id = :memberId AND c.book.id = :bookId")
  void deleteByMemberAndBookId(@Param("memberId") Long memberId, @Param("bookId") String bookId);

  boolean existsByMemberAndBook(Member member, Book book);

  boolean existsByMember_IdAndBook_Id(Long memberId, String bookId);

  int countAllByMember(Member member);
}
