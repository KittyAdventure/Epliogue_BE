package com.team1.epilogue.keyword.controller;

import com.team1.epilogue.keyword.service.KeyWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KeyWordController {
  private final KeyWordService keyWordService;

  @GetMapping("/api/keywords")
  public ResponseEntity<?> getHotKeywords() {
    keyWordService.getHotKeywords();
  }



}
