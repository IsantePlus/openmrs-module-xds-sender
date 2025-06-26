package org.openmrs.module.xdssender.notificationspullpoint;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributedUnsignedLongType", namespace = "http://www.w3.org/200508/addressing", propOrder = {"value"})
public class AttributedUnsignedLongType
{
  @XmlValue
  @XmlSchemaType(name = "unsignedLong")
  protected BigInteger value;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap<>();

  
  public BigInteger getValue() {
    return this.value;
  }

  
  public void setValue(BigInteger value) {
    this.value = value;
  }

  
  public Map<QName, String> getOtherAttributes() {
    return this.otherAttributes;
  }
}
