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

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"terminationTime", "any"})
@XmlRootElement(name = "Renew")
public class Renew
{
  @XmlElement(name = "TerminationTime", required = true, nillable = true)
  @XmlSchemaType(name = "anySimpleType")
  protected String terminationTime;
  @XmlAnyElement(lax = true)
  protected List<Object> any;
  
  public String getTerminationTime() {
    return this.terminationTime;
  }

  
  public void setTerminationTime(String value) {
    this.terminationTime = value;
  }

  
  public List<Object> getAny() {
    if (this.any == null) {
      this.any = new ArrayList();
    }
    return this.any;
  }
}
