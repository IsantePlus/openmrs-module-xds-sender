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
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"consumerReference", "filter", "initialTerminationTime", "subscriptionPolicy", "any"})
@XmlRootElement(name = "Subscribe")
public class Subscribe
{
  @XmlElementRef(name = "ConsumerReference", namespace = "http:docs.oasis-open.orgwsnb-2", type = ConsumerReferenceB2.class)
  protected ConsumerReferenceB2 consumerReference;
  @XmlElement(name = "Filter")
  protected FilterType filter;
  @XmlElementRef(name = "InitialTerminationTime", namespace = "http:docs.oasis-open.orgwsnb-2", type = JAXBElement.class, required = false)
  protected JAXBElement<String> initialTerminationTime;
  @XmlElement(name = "SubscriptionPolicy")
  protected SubscriptionPolicy subscriptionPolicy;
  @XmlAnyElement(lax = true)
  protected List<Object> any;
  
  public ConsumerReferenceB2 getConsumerReference() {
    return this.consumerReference;
  }

  
  public void setConsumerReference(ConsumerReferenceB2 value) {
    this.consumerReference = value;
  }

  
  public FilterType getFilter() {
    return this.filter;
  }

  
  public void setFilter(FilterType value) {
    this.filter = value;
  }

  
  public JAXBElement<String> getInitialTerminationTime() {
    return this.initialTerminationTime;
  }

  
  public void setInitialTerminationTime(JAXBElement<String> value) {
    this.initialTerminationTime = value;
  }

  
  public SubscriptionPolicy getSubscriptionPolicy() {
    return this.subscriptionPolicy;
  }

  
  public void setSubscriptionPolicy(SubscriptionPolicy value) {
    this.subscriptionPolicy = value;
  }

  
  public List<Object> getAny() {
    if (this.any == null) {
      this.any = new ArrayList();
    }
    return this.any;
  }

  
  public static class ConsumerReferenceB2
    extends JAXBElement<EndpointReferenceType>
  {
    protected static final QName NAME = new QName("http:docs.oasis-open.orgwsnb-2", "ConsumerReference");
    
    public ConsumerReferenceB2(EndpointReferenceType value) {
      super(NAME, EndpointReferenceType.class, Subscribe.class, value);
    }
    
    public ConsumerReferenceB2() {
      super(NAME, EndpointReferenceType.class, Subscribe.class, null);
    }
  }

  
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "", propOrder = {"any"})
  public static class SubscriptionPolicy
  {
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    
    public List<Object> getAny() {
      if (this.any == null) {
        this.any = new ArrayList();
      }
      return this.any;
    }
  }
}
