package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseFaultType", namespace = "http:docs.oasis-open.orgwsrfbf-2", propOrder = {"any", "timestamp", "originator", "errorCode", "description", "faultCause"})
@XmlSeeAlso({SubscribeCreationFailedFaultType.class, InvalidFilterFaultType.class, TopicExpressionDialectUnknownFaultType.class, InvalidTopicExpressionFaultType.class, TopicNotSupportedFaultType.class, MultipleTopicsSpecifiedFaultType.class, InvalidProducerPropertiesExpressionFaultType.class, InvalidMessageContentExpressionFaultType.class, UnrecognizedPolicyRequestFaultType.class, UnsupportedPolicyRequestFaultType.class, NotifyMessageNotSupportedFaultType.class, UnacceptableInitialTerminationTimeFaultType.class, NoCurrentMessageOnTopicFaultType.class, UnableToGetMessagesFaultType.class, UnableToDestroyPullPointFaultType.class, UnableToCreatePullPointFaultType.class, UnacceptableTerminationTimeFaultType.class, UnableToDestroySubscriptionFaultType.class, PauseFailedFaultType.class, ResumeFailedFaultType.class, PublisherRegistrationRejectedFaultType.class, PublisherRegistrationFailedFaultType.class, ResourceNotDestroyedFaultType.class})
public class BaseFaultType
{
  @XmlAnyElement(lax = true)
  protected List<Object> any;
  @XmlElement(name = "Timestamp", required = true)
  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar timestamp;
  @XmlElement(name = "Originator")
  protected EndpointReferenceType originator;
  @XmlElement(name = "ErrorCode")
  protected ErrorCode errorCode;
  @XmlElement(name = "Description")
  protected List<Description> description;
  @XmlElement(name = "FaultCause")
  protected FaultCause faultCause;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap<>();

  
  public List<Object> getAny() {
    if (this.any == null) {
      this.any = new ArrayList();
    }
    return this.any;
  }

  
  public XMLGregorianCalendar getTimestamp() {
    return this.timestamp;
  }

  
  public void setTimestamp(XMLGregorianCalendar value) {
    this.timestamp = value;
  }

  
  public EndpointReferenceType getOriginator() {
    return this.originator;
  }

  
  public void setOriginator(EndpointReferenceType value) {
    this.originator = value;
  }

  
  public ErrorCode getErrorCode() {
    return this.errorCode;
  }

  
  public void setErrorCode(ErrorCode value) {
    this.errorCode = value;
  }

  
  public List<Description> getDescription() {
    if (this.description == null) {
      this.description = new ArrayList<>();
    }
    return this.description;
  }

  
  public FaultCause getFaultCause() {
    return this.faultCause;
  }

  
  public void setFaultCause(FaultCause value) {
    this.faultCause = value;
  }

  
  public Map<QName, String> getOtherAttributes() {
    return this.otherAttributes;
  }

  
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "", propOrder = {"value"})
  public static class Description
  {
    @XmlValue
    protected String value;

    
    @XmlAttribute(name = "lang", namespace = "http:www.w3.orgXML1998namespace")
    protected String lang;

    
    public String getValue() {
      return this.value;
    }

    
    public void setValue(String value) {
      this.value = value;
    }

    
    public String getLang() {
      return this.lang;
    }

    
    public void setLang(String value) {
      this.lang = value;
    }
  }

  
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "", propOrder = {"content"})
  public static class ErrorCode
  {
    @XmlMixed
    @XmlAnyElement
    protected List<Object> content;

    
    @XmlAttribute(name = "dialect", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String dialect;

    
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    
    public List<Object> getContent() {
      if (this.content == null) {
        this.content = new ArrayList();
      }
      return this.content;
    }

    
    public String getDialect() {
      return this.dialect;
    }

    
    public void setDialect(String value) {
      this.dialect = value;
    }

    
    public Map<QName, String> getOtherAttributes() {
      return this.otherAttributes;
    }
  }

  
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "", propOrder = {"any"})
  public static class FaultCause
  {
    @XmlAnyElement(lax = true)
    protected Object any;

    
    public Object getAny() {
      return this.any;
    }

    
    public void setAny(Object value) {
      this.any = value;
    }
  }
}
