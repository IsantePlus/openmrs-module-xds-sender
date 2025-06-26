package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TopicNamespaceType", namespace = "http://docs.oasis-open.org/wsn/t-1", propOrder = {"topic", "any"})
public class TopicNamespaceType
  extends ExtensibleDocumented
{
  @XmlElement(name = "Topic")
  protected List<Topic> topic;
  @XmlAnyElement(lax = true)
  protected List<Object> any;
  @XmlAttribute(name = "name")
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlSchemaType(name = "NCName")
  protected String name;
  @XmlAttribute(name = "targetNamespace", required = true)
  @XmlSchemaType(name = "anyURI")
  protected String targetNamespace;
  @XmlAttribute(name = "final")
  protected Boolean _final;
  
  public List<Topic> getTopic() {
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

  
  public String getTargetNamespace() {
    return this.targetNamespace;
  }

  
  public void setTargetNamespace(String value) {
    this.targetNamespace = value;
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

  
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "")
  public static class Topic
    extends TopicType
  {
    @XmlAttribute(name = "parent")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String parent;

    
    public String getParent() {
      return this.parent;
    }

    
    public void setParent(String value) {
      this.parent = value;
    }
  }
}
