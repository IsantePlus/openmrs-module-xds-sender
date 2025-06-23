package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnrecognizedPolicyRequestFaultType", propOrder = {"unrecognizedPolicy"})
public class UnrecognizedPolicyRequestFaultType
  extends BaseFaultType
{
  @XmlElement(name = "UnrecognizedPolicy")
  protected List<QName> unrecognizedPolicy;
  
  public List<QName> getUnrecognizedPolicy() {
    if (this.unrecognizedPolicy == null) {
      this.unrecognizedPolicy = new ArrayList<>();
    }
    return this.unrecognizedPolicy;
  }
}
