package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnsupportedPolicyRequestFaultType", propOrder = {"unsupportedPolicy"})
public class UnsupportedPolicyRequestFaultType
  extends BaseFaultType
{
  @XmlElement(name = "UnsupportedPolicy")
  protected List<QName> unsupportedPolicy;
  
  public List<QName> getUnsupportedPolicy() {
    if (this.unsupportedPolicy == null) {
      this.unsupportedPolicy = new ArrayList<>();
    }
    return this.unsupportedPolicy;
  }
}
