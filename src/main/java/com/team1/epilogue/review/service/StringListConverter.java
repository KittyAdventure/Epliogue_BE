package com.team1.epilogue.review.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(List<String> attribute) {
    try {
      return (attribute == null || attribute.isEmpty()) ? null
          : objectMapper.writeValueAsString(attribute);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("List<String> 변환 실패", e);
    }
  }

  @Override
  public List<String> convertToEntityAttribute(String dbData) {
    try {
      return (dbData == null || dbData.isEmpty()) ? List.of()
          : Arrays.asList(objectMapper.readValue(dbData, String[].class));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("DB 데이터 -> List<String> 변환 실패", e);
    }
  }
}
