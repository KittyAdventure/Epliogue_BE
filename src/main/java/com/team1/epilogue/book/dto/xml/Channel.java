package com.team1.epilogue.book.dto.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Channel {

  @JacksonXmlProperty(localName = "title")
  private String title;

  @JacksonXmlProperty(localName = "link")
  private String link;

  @JacksonXmlProperty(localName = "description")
  private String description;

  @JacksonXmlElementWrapper(localName = "item", useWrapping = false)
  private List<Item> items;

}

