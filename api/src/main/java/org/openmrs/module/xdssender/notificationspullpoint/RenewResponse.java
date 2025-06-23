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
@XmlType(name = "", propOrder = {"terminationTime", "currentTime", "any"})
@XmlRootElement(name = "RenewResponse")
public class RenewResponse
{
  @XmlElement(name = "TerminationTime", required = true, nillable = true)
  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar terminationTime;
  @XmlElement(name = "CurrentTime")
  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar currentTime;
  @XmlAnyElement(lax = true)
  protected List<Object> any;
  
  public XMLGregorianCalendar getTerminationTime() {
    return this.terminationTime;
  }

  
  public void setTerminationTime(XMLGregorianCalendar value) {
    this.terminationTime = value;
  }

  
  public XMLGregorianCalendar getCurrentTime() {
    return this.currentTime;
  }

  
  public void setCurrentTime(XMLGregorianCalendar value) {
    this.currentTime = value;
  }

  
  public List<Object> getAny() {
    if (this.any == null) {
      this.any = new ArrayList();
    }
    return this.any;
  }
}
