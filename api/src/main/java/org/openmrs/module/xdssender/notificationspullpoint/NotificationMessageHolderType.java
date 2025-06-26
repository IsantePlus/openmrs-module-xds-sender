package org.openmrs.module.xdssender.notificationspullpoint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NotificationMessageHolderType", propOrder = {"subscriptionReference", "topic", "producerReference", "message"})
public class NotificationMessageHolderType
{
  @XmlElement(name = "SubscriptionReference")
  protected EndpointReferenceType subscriptionReference;
  @XmlElementRef(name = "Topic", namespace = "http://docs.oasis-open.org/wsn/b-2", type = TopicB2.class, required = false)
  protected TopicB2 topic;
  @XmlElement(name = "ProducerReference")
  protected EndpointReferenceType producerReference;
  @XmlElement(name = "Message", required = true)
  protected Message message;
  
  public EndpointReferenceType getSubscriptionReference() {
    return this.subscriptionReference;
  }

  
  public void setSubscriptionReference(EndpointReferenceType value) {
    this.subscriptionReference = value;
  }

  
  public TopicB2 getTopic() {
    return this.topic;
  }

  
  public void setTopic(TopicB2 value) {
    this.topic = value;
  }

  
  public EndpointReferenceType getProducerReference() {
    return this.producerReference;
  }

  
  public void setProducerReference(EndpointReferenceType value) {
    this.producerReference = value;
  }

  
  public Message getMessage() {
    return this.message;
  }

  
  public void setMessage(Message value) {
    this.message = value;
  }

  
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "", propOrder = {"any"})
  public static class Message
  {
    @XmlAnyElement(lax = true)
    protected Object any;

    
    public Object getAny() {
      return this.any;
    }

    
    public void setAny(Object value) {
      this.any = value;
    }
  }
}
