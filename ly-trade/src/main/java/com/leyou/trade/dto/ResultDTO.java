package com.leyou.trade.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;

@JacksonXmlRootElement(localName = "xml")
@Getter
public class ResultDTO {
    @JacksonXmlProperty(localName = "return_code")
    private String returnCode = "SUCCESS";
    @JacksonXmlProperty(localName = "return_msg")
    private String returnMsg = "OK";
}
