package org.openmrs.module.xdssender.notificationspullpoint;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

public class CreationTimeBR2
  extends JAXBElement<XMLGregorianCalendar>
{
  protected static final QName NAME = new QName("http://docs.oasis-open.org/wsn/br-2", "CreationTime");
  
  public CreationTimeBR2(XMLGregorianCalendar value) {
    super(NAME, XMLGregorianCalendar.class, null, value);
  }
  
  public CreationTimeBR2() {
    super(NAME, XMLGregorianCalendar.class, null, null);
  }
}
