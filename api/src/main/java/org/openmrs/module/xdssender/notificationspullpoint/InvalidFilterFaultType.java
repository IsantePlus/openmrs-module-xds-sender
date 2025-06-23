package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InvalidFilterFaultType", propOrder = {"unknownFilter"})
public class InvalidFilterFaultType
  extends BaseFaultType
{
  @XmlElement(name = "UnknownFilter", required = true)
  protected List<QName> unknownFilter;
  
  public List<QName> getUnknownFilter() {
    if (this.unknownFilter == null) {
      this.unknownFilter = new ArrayList<>();
    }
    return this.unknownFilter;
  }
}
