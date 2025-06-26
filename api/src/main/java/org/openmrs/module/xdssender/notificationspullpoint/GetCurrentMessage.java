package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"topic", "any"})
@XmlRootElement(name = "GetCurrentMessage")
public class GetCurrentMessage
{
  @XmlElementRef(name = "Topic", namespace = "http://docs.oasis-open.org/wsn/b-2", type = TopicB2.class)
  protected TopicB2 topic;
  @XmlAnyElement(lax = true)
  protected List<Object> any;
  
  public TopicB2 getTopic() {
    return this.topic;
  }

  
  public void setTopic(TopicB2 value) {
    this.topic = value;
  }

  
  public List<Object> getAny() {
    if (this.any == null) {
      this.any = new ArrayList();
    }
    return this.any;
  }

  
  public static class TopicB2
    extends JAXBElement<TopicExpressionType>
  {
    protected static final QName NAME = new QName("http://docs.oasis-open.org/wsn/b-2", "Topic");
    
    public TopicB2(TopicExpressionType value) {
      super(NAME, TopicExpressionType.class, GetCurrentMessage.class, value);
    }
    
    public TopicB2() {
      super(NAME, TopicExpressionType.class, GetCurrentMessage.class, null);
    }
  }
}
