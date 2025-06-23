package org.openmrs.module.xdssender.notificationspullpoint;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class ConsumerReferenceB2
  extends JAXBElement<EndpointReferenceType>
{
  protected static final QName NAME = new QName("http:docs.oasis-open.orgwsnb-2", "ConsumerReference");
  
  public ConsumerReferenceB2(EndpointReferenceType value) {
    super(NAME, EndpointReferenceType.class, null, value);
  }
  
  public ConsumerReferenceB2() {
    super(NAME, EndpointReferenceType.class, null, null);
  }
}
