package com.team1.epilogue.book.dto.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {

  @JacksonXmlProperty(localName = "title")
  private String title;

  @JacksonXmlProperty(localName = "link")
  private String link;

  @JacksonXmlProperty(localName = "author")
  private String author;

  @JacksonXmlProperty(localName = "price")
  private int price;

  @JacksonXmlProperty(localName = "publisher")
  private String publisher;

  @JacksonXmlProperty(localName = "pubdate")
  private String pubDate;

  @JacksonXmlProperty(localName = "description")
  private String description;

  @JacksonXmlProperty(localName = "image")
  private String image;

  @JacksonXmlProperty(localName = "isbn")
  private String isbn;

}
