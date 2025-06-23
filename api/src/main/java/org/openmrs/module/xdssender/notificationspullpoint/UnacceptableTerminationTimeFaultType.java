package org.openmrs.module.xdssender.notificationspullpoint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnacceptableTerminationTimeFaultType", propOrder = {"minimumTime", "maximumTime"})
public class UnacceptableTerminationTimeFaultType
  extends BaseFaultType
{
  @XmlElement(name = "MinimumTime", required = true)
  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar minimumTime;
  @XmlElement(name = "MaximumTime")
  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar maximumTime;
  
  public XMLGregorianCalendar getMinimumTime() {
    return this.minimumTime;
  }

  
  public void setMinimumTime(XMLGregorianCalendar value) {
    this.minimumTime = value;
  }

  
  public XMLGregorianCalendar getMaximumTime() {
    return this.maximumTime;
  }

  
  public void setMaximumTime(XMLGregorianCalendar value) {
    this.maximumTime = value;
  }
}
