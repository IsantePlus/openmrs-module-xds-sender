package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TopicType", namespace = "http://docs.oasis-open.org/wsn/t-1", propOrder = {"messagePattern", "topic", "any"})
@XmlSeeAlso({TopicNamespaceType.Topic.class})
public class TopicType
  extends ExtensibleDocumented
{
  @XmlElement(name = "MessagePattern")
  protected QueryExpressionTypeT1 messagePattern;
  @XmlElement(name = "Topic")
  protected List<TopicType> topic;
  @XmlAnyElement(lax = true)
  protected List<Object> any;
  @XmlAttribute(name = "name", required = true)
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlSchemaType(name = "NCName")
  protected String name;
  @XmlAttribute(name = "messageTypes")
  @XmlJavaTypeAdapter(QNameAdapter.class)
  protected List<QName> messageTypes;
  @XmlAttribute(name = "final")
  protected Boolean _final;
  
  public QueryExpressionTypeT1 getMessagePattern() {
    return this.messagePattern;
  }

  
  public void setMessagePattern(QueryExpressionTypeT1 value) {
    this.messagePattern = value;
  }

  
  public List<TopicType> getTopic() {
    if (this.topic == null) {
      this.topic = new ArrayList<>();
    }
    return this.topic;
  }

  
  public List<Object> getAny() {
    if (this.any == null) {
      this.any = new ArrayList();
    }
    return this.any;
  }

  
  public String getName() {
    return this.name;
  }

  
  public void setName(String value) {
    this.name = value;
  }

  
  public List<QName> getMessageTypes() {
    if (this.messageTypes == null) {
      this.messageTypes = new ArrayList<>();
    }
    return this.messageTypes;
  }

  
  public boolean isFinal() {
    if (this._final == null) {
      return false;
    }
    return this._final.booleanValue();
  }

  
  public void setFinal(Boolean value) {
    this._final = value;
  }
}
