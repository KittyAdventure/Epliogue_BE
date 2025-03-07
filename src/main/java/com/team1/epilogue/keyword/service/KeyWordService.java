package com.team1.epilogue.keyword.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeyWordService {

  private final StringRedisTemplate stringRedisTemplate;
  public static final long TTL_SECONDS =  86400; // 24 시간
  private static final String SEARCH_KEY_PREFIX = "search:keyword:";

  /**
   * 검색된 Keyword 를 Redis 에 저장합니다
   * @param keyWord 검색어
   */
  public void saveKeyWord(String keyWord) {
    // Redis 키: search:keyword:{검색어}:{시간}
    String redisKey = "search:keyword:" + keyWord;

    // 위 Key 에 해당하는 Value 를 +1 한다.
    stringRedisTemplate.opsForValue().increment(redisKey, 1);
    // 만료 시간 설정
    stringRedisTemplate.expire(redisKey, TTL_SECONDS, TimeUnit.SECONDS);
  }

  /**
   * 가장 많이 검색된 키워드를 10개까지 List 로 return 합니다
   * @return 10개의 인기 검색어
   */
  public List<String> getPopularKeywords() {
    // redis 에서 검색어들을 가져온다
    Set<String> keys = stringRedisTemplate.keys(SEARCH_KEY_PREFIX + "*");

    if (keys == null || keys.isEmpty()) {
      return Collections.emptyList();
    }

    Map<String, Integer> keywordCounts = new HashMap<>();

    for (String key : keys) {
      // keyWord 만 가져온다
      String keyword = key.replace(SEARCH_KEY_PREFIX, "");
      log.info(keyword);
      // redis 내부 value 를 가져온다
      String countStr = stringRedisTemplate.opsForValue().get(key);
      int count = (countStr != null) ? Integer.parseInt(countStr) : 0;
      keywordCounts.put(keyword, count);
    }

    // 10개의 검색어만 value 의 숫자가 높은 순서대로 return
    return keywordCounts.entrySet().stream()
        .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
        .limit(10)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }
}
