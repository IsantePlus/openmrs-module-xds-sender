package org.openmrs.module.xdssender.notificationspullpoint;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

public class CreationTimeB2
  extends JAXBElement<XMLGregorianCalendar>
{
  protected static final QName NAME = new QName("http://docs.oasis-open.org/wsn/b-2", "CreationTime");
  
  public CreationTimeB2(XMLGregorianCalendar value) {
    super(NAME, XMLGregorianCalendar.class, null, value);
  }
  
  public CreationTimeB2() {
    super(NAME, XMLGregorianCalendar.class, null, null);
  }
}
