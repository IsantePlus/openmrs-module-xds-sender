package org.openmrs.module.xdssender.notificationspullpoint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"consumerReference", "filter", "subscriptionPolicy", "creationTime"})
@XmlRootElement(name = "SubscriptionManagerRP")
public class SubscriptionManagerRP
{
  @XmlElementRef(name = "ConsumerReference", namespace = "http://docs.oasis-open.org/wsn/b-2", type = ConsumerReferenceB2.class)
  protected ConsumerReferenceB2 consumerReference;
  @XmlElement(name = "Filter")
  protected FilterType filter;
  @XmlElement(name = "SubscriptionPolicy")
  protected SubscriptionPolicyType subscriptionPolicy;
  @XmlElementRef(name = "CreationTime", namespace = "http://docs.oasis-open.org/wsn/b-2", type = CreationTimeB2.class, required = false)
  protected CreationTimeB2 creationTime;
  
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

  
  public SubscriptionPolicyType getSubscriptionPolicy() {
    return this.subscriptionPolicy;
  }

  
  public void setSubscriptionPolicy(SubscriptionPolicyType value) {
    this.subscriptionPolicy = value;
  }

  
  public CreationTimeB2 getCreationTime() {
    return this.creationTime;
  }

  
  public void setCreationTime(CreationTimeB2 value) {
    this.creationTime = value;
  }
}
