package com.team1.epilogue.keyword.controller;

import com.team1.epilogue.keyword.service.KeyWordService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KeyWordController {
  private final KeyWordService keyWordService;

  /**
   * 인기 검색어를 받아오는 기능입니다. 10개까지 return 합니다
   */
  @GetMapping("/api/keywords")
  public ResponseEntity<?> getHotKeywords() {
    List<String> hoyKeyWords = keyWordService.getPopularKeywords();
    return ResponseEntity.ok(hoyKeyWords);
  }
}
