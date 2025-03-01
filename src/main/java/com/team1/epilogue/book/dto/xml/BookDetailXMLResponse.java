package com.team1.epilogue.book.dto.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JacksonXmlRootElement(namespace = "channel")
@Builder
public class BookDetailXMLResponse {

  @JacksonXmlProperty(localName = "title")
  private String title;

  @JacksonXmlProperty(localName = "link")
  private String link;

  @JacksonXmlProperty(localName = "description")
  private String description;

  @JacksonXmlElementWrapper(localName = "item", useWrapping = false)
  private List<Item> items;

}
