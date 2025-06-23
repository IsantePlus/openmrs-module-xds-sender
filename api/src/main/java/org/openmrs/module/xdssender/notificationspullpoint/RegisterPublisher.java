package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"publisherReference", "topic", "demand", "initialTerminationTime", "any"})
@XmlRootElement(name = "RegisterPublisher", namespace = "http:docs.oasis-open.orgwsnbr-2")
public class RegisterPublisher
{
  @XmlElement(name = "PublisherReference", namespace = "http:docs.oasis-open.orgwsnbr-2")
  protected EndpointReferenceType publisherReference;
  @XmlElementRef(name = "Topic", namespace = "http:docs.oasis-open.orgwsnbr-2", type = TopicBR2.class, required = false)
  protected List<TopicBR2> topic;
  @XmlElement(name = "Demand", namespace = "http:docs.oasis-open.orgwsnbr-2", defaultValue = "false")
  protected Boolean demand;
  @XmlElement(name = "InitialTerminationTime", namespace = "http:docs.oasis-open.orgwsnbr-2")
  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar initialTerminationTime;
  @XmlAnyElement(lax = true)
  protected List<Object> any;
  
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

  
  public Boolean isDemand() {
    return this.demand;
  }

  
  public void setDemand(Boolean value) {
    this.demand = value;
  }

  
  public XMLGregorianCalendar getInitialTerminationTime() {
    return this.initialTerminationTime;
  }

  
  public void setInitialTerminationTime(XMLGregorianCalendar value) {
    this.initialTerminationTime = value;
  }

  
  public List<Object> getAny() {
    if (this.any == null) {
      this.any = new ArrayList();
    }
    return this.any;
  }

  
  public static class TopicBR2
    extends JAXBElement<TopicExpressionType>
  {
    protected static final QName NAME = new QName("http:docs.oasis-open.orgwsnbr-2", "Topic");
    
    public TopicBR2(TopicExpressionType value) {
      super(NAME, TopicExpressionType.class, RegisterPublisher.class, value);
    }
    
    public TopicBR2() {
      super(NAME, TopicExpressionType.class, RegisterPublisher.class, null);
    }
  }
}
