package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"subscriptionReference", "currentTime", "terminationTime", "any"})
@XmlRootElement(name = "SubscribeResponse")
public class SubscribeResponse
{
  @XmlElement(name = "SubscriptionReference", required = true)
  protected EndpointReferenceType subscriptionReference;
  @XmlElement(name = "CurrentTime")
  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar currentTime;
  @XmlElement(name = "TerminationTime", nillable = true)
  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar terminationTime;
  @XmlAnyElement(lax = true)
  protected List<Object> any;
  
  public EndpointReferenceType getSubscriptionReference() {
    return this.subscriptionReference;
  }

  
  public void setSubscriptionReference(EndpointReferenceType value) {
    this.subscriptionReference = value;
  }

  
  public XMLGregorianCalendar getCurrentTime() {
    return this.currentTime;
  }

  
  public void setCurrentTime(XMLGregorianCalendar value) {
    this.currentTime = value;
  }

  
  public XMLGregorianCalendar getTerminationTime() {
    return this.terminationTime;
  }

  
  public void setTerminationTime(XMLGregorianCalendar value) {
    this.terminationTime = value;
  }

  
  public List<Object> getAny() {
    if (this.any == null) {
      this.any = new ArrayList();
    }
    return this.any;
  }
}
