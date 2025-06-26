package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"topicExpression", "fixedTopicSet", "topicExpressionDialect", "topicSet"})
@XmlRootElement(name = "NotificationProducerRP")
public class NotificationProducerRP
{
  @XmlElement(name = "TopicExpression")
  protected List<TopicExpressionType> topicExpression;
  @XmlElement(name = "FixedTopicSet", defaultValue = "true")
  protected Boolean fixedTopicSet;
  @XmlElement(name = "TopicExpressionDialect")
  @XmlSchemaType(name = "anyURI")
  protected List<String> topicExpressionDialect;
  @XmlElement(name = "TopicSet", namespace = "http://docs.oasis-open.org/wsn/t-1")
  protected TopicSetType topicSet;
  
  public List<TopicExpressionType> getTopicExpression() {
    if (this.topicExpression == null) {
      this.topicExpression = new ArrayList<>();
    }
    return this.topicExpression;
  }

  
  public Boolean isFixedTopicSet() {
    return this.fixedTopicSet;
  }

  
  public void setFixedTopicSet(Boolean value) {
    this.fixedTopicSet = value;
  }

  
  public List<String> getTopicExpressionDialect() {
    if (this.topicExpressionDialect == null) {
      this.topicExpressionDialect = new ArrayList<>();
    }
    return this.topicExpressionDialect;
  }

  
  public TopicSetType getTopicSet() {
    return this.topicSet;
  }

  
  public void setTopicSet(TopicSetType value) {
    this.topicSet = value;
  }
}
