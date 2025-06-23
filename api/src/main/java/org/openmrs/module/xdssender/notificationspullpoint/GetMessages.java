package org.openmrs.module.xdssender.notificationspullpoint;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"maximumNumber", "any"})
@XmlRootElement(name = "GetMessages")
public class GetMessages
{
  @XmlElement(name = "MaximumNumber")
  @XmlSchemaType(name = "nonNegativeInteger")
  protected BigInteger maximumNumber;
  @XmlAnyElement(lax = true)
  protected List<Object> any;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap<>();

  
  public BigInteger getMaximumNumber() {
    return this.maximumNumber;
  }

  
  public void setMaximumNumber(BigInteger value) {
    this.maximumNumber = value;
  }

  
  public List<Object> getAny() {
    if (this.any == null) {
      this.any = new ArrayList();
    }
    return this.any;
  }

  
  public Map<QName, String> getOtherAttributes() {
    return this.otherAttributes;
  }
}