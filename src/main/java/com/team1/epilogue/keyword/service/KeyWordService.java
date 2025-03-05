package com.team1.epilogue.keyword.service;

import com.team1.epilogue.keyword.repository.KeyWordRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeyWordService {

  private final KeyWordRepository keyWordRepository;

  public void getHotKeywords() {
    keyWordRepository.find

  }

  /**
   * 어제 00:00 ~ 23:59 동안 수집된 검색어들을 삭제해준다.
   * 이 작업을 다음날 12:00 에 진행.
   * 즉 12:00 부터는 당일 00:00 ~ 의 데이터만 수집함
   */
  @Scheduled(cron = "0 0 12 * * ?") // 매일 낮 12시 실행
  public void removeOldData() {
    LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
    keyWordRepository.deleteByCreatedAtBefore(today);

    log.info("인기검색어 DB 내부 어제날짜 데이터들이 삭제 되었습니다.");
  }
}
