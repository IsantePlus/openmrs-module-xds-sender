package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"publisherReference", "topic", "demand", "creationTime"})
@XmlRootElement(name = "PublisherRegistrationRP", namespace = "http:docs.oasis-open.orgwsnbr-2")
public class PublisherRegistrationRP
{
  @XmlElement(name = "PublisherReference", namespace = "http:docs.oasis-open.orgwsnbr-2")
  protected EndpointReferenceType publisherReference;
  @XmlElementRef(name = "Topic", namespace = "http:docs.oasis-open.orgwsnbr-2", type = TopicBR2.class, required = false)
  protected List<TopicBR2> topic;
  @XmlElement(name = "Demand", namespace = "http:docs.oasis-open.orgwsnbr-2")
  protected boolean demand;
  @XmlElementRef(name = "CreationTime", namespace = "http:docs.oasis-open.orgwsnbr-2", type = CreationTimeBR2.class, required = false)
  protected CreationTimeBR2 creationTime;
  
  public EndpointReferenceType getPublisherReference() {
    return this.publisherReference;
  }

  
  public void setPublisherReference(EndpointReferenceType value) {
    this.publisherReference = value;
  }

  
  public List<TopicBR2> getTopic() {
    if (this.topic == null) {
      this.topic = new ArrayList<>();
    }
    return this.topic;
  }

  
  public boolean isDemand() {
    return this.demand;
  }

  
  public void setDemand(boolean value) {
    this.demand = value;
  }

  
  public CreationTimeBR2 getCreationTime() {
    return this.creationTime;
  }

  
  public void setCreationTime(CreationTimeBR2 value) {
    this.creationTime = value;
  }
}
