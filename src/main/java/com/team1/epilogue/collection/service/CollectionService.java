package com.team1.epilogue.collection.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.collection.dto.CollectionDetail;
import com.team1.epilogue.collection.dto.CollectionResponse;
import com.team1.epilogue.collection.entity.CollectionEntity;
import com.team1.epilogue.collection.exception.AlreadyAddedCollectionException;
import com.team1.epilogue.collection.repository.CollectionRepository;
import com.team1.epilogue.review.exception.BookNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CollectionService {

  private final CollectionRepository collectionRepository;
  private final BookRepository bookRepository;
  private final MemberRepository memberRepository;

  /**
   * 내가 컬렉션에 담은 책들 조회하는 메서드
   */
  public CollectionResponse getCollection(CustomMemberDetails details, int page) {
    Long memberId = details.getId();
    Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException("ID가 " + memberId + "인 회원을 찾을 수 없습니다."));


    PageRequest pageRequest = PageRequest.of(page-1, 9);

    Page<CollectionEntity> result = collectionRepository.findAllByMember(pageRequest, member);

    List<CollectionDetail> list = new ArrayList<>();

    result.getContent().stream().forEach(
        data -> {
          list.add(
              CollectionDetail.builder()
                  .bookId(data.getBook().getId())
                  .bookTitle(data.getBook().getTitle())
                  .thumbnail(data.getBook().getCoverUrl())
                  .build()
          );
        }
    );

    return CollectionResponse.builder()
        .page(page)
        .totalPages(result.getTotalPages())
        .books(list)
        .build();
  }

  /**
   * 컬렉션 내부에 존재하는 책 삭제하는 메서드
   */
  @Transactional
  public void deleteCollection(String bookId, CustomMemberDetails details) {
    Long memberId = details.getId();

    collectionRepository.deleteByMemberAndBookId(memberId, bookId);
  }

  /**
   * 컬렉션에 책 추가하는 메서드
   */
  public void addCollection(String bookId, CustomMemberDetails details) {
    Long memberId = details.getId();
    Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException("ID가 " + memberId + "인 회원을 찾을 수 없습니다."));

    Book book = bookRepository.findById(bookId).orElseThrow(
        () -> new BookNotFoundException("존재하지 않는 책입니다"));

    if (collectionRepository.existsByMemberAndBook(member, book)) {
      throw new AlreadyAddedCollectionException("이미 컬렉션에 추가된 책입니다.");
    }

    collectionRepository.save(CollectionEntity.builder()
        .book(book)
        .member(member)
        .build());
  }
}
