package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryExpressionType", namespace = "http:docs.oasis-open.orgwsnt-1", propOrder = {"content"})
public class QueryExpressionTypeT1
{
  @XmlMixed
  @XmlAnyElement(lax = true)
  protected List<Object> content;
  @XmlAttribute(name = "Dialect", required = true)
  @XmlSchemaType(name = "anyURI")
  protected String dialect;
  
  public List<Object> getContent() {
    if (this.content == null) {
      this.content = new ArrayList();
    }
    return this.content;
  }

  
  public String getDialect() {
    return this.dialect;
  }

  
  public void setDialect(String value) {
    this.dialect = value;
  }
}
