package org.openmrs.module.xdssender.notificationspullpoint;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class ConsumerReferenceBR2
  extends JAXBElement<EndpointReferenceType>
{
  protected static final QName NAME = new QName("http://docs.oasis-open.org/wsn/br-2", "ConsumerReference");
  
  public ConsumerReferenceBR2(EndpointReferenceType value) {
    super(NAME, EndpointReferenceType.class, null, value);
  }
  
  public ConsumerReferenceBR2() {
    super(NAME, EndpointReferenceType.class, null, null);
  }
}
