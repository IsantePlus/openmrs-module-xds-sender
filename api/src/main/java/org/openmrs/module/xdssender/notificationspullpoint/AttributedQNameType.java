package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributedQNameType", namespace = "http://www.w3.org/200508/addressing", propOrder = {"value"})
public class AttributedQNameType
{
  @XmlValue
  protected QName value;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap<>();

  
  public QName getValue() {
    return this.value;
  }

  
  public void setValue(QName value) {
    this.value = value;
  }

  
  public Map<QName, String> getOtherAttributes() {
    return this.otherAttributes;
  }
}
