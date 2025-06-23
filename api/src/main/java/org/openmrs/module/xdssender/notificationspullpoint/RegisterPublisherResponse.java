package org.openmrs.module.xdssender.notificationspullpoint;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"publisherRegistrationReference", "consumerReference"})
@XmlRootElement(name = "RegisterPublisherResponse", namespace = "http:docs.oasis-open.orgwsnbr-2")
public class RegisterPublisherResponse
{
  @XmlElement(name = "PublisherRegistrationReference", namespace = "http:docs.oasis-open.orgwsnbr-2", required = true)
  protected EndpointReferenceType publisherRegistrationReference;
  @XmlElementRef(name = "ConsumerReference", namespace = "http:docs.oasis-open.orgwsnbr-2", type = ConsumerReferenceBR2.class, required = false)
  protected ConsumerReferenceBR2 consumerReference;
  
  public EndpointReferenceType getPublisherRegistrationReference() {
    return this.publisherRegistrationReference;
  }

  
  public void setPublisherRegistrationReference(EndpointReferenceType value) {
    this.publisherRegistrationReference = value;
  }

  
  public ConsumerReferenceBR2 getConsumerReference() {
    return this.consumerReference;
  }

  
  public void setConsumerReference(ConsumerReferenceBR2 value) {
    this.consumerReference = value;
  }

  
  public static class ConsumerReferenceBR2
    extends JAXBElement<EndpointReferenceType>
  {
    protected static final QName NAME = new QName("http:docs.oasis-open.orgwsnbr-2", "ConsumerReference");
    
    public ConsumerReferenceBR2(EndpointReferenceType value) {
      super(NAME, EndpointReferenceType.class, RegisterPublisherResponse.class, value);
    }
    
    public ConsumerReferenceBR2() {
      super(NAME, EndpointReferenceType.class, RegisterPublisherResponse.class, null);
    }
  }
}
