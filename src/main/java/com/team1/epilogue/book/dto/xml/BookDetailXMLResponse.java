package com.team1.epilogue.book.dto.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JacksonXmlRootElement(namespace = "rss")
public class BookDetailXMLResponse {

  @JacksonXmlProperty(localName = "channel")
  private Channel channel;

}
