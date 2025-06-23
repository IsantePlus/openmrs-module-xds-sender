package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TopicExpressionType", propOrder = {"content"})
public class TopicExpressionType
{
  @XmlMixed
  @XmlAnyElement(lax = true)
  protected List<Object> content;
  @XmlAttribute(name = "Dialect", required = true)
  @XmlSchemaType(name = "anyURI")
  protected String dialect;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap<>();

  
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

  
  public Map<QName, String> getOtherAttributes() {
    return this.otherAttributes;
  }
}
