package org.openmrs.module.xdssender.notificationspullpoint;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory
{
  private static final QName _TopicExpression_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "TopicExpression");
  private static final QName _FixedTopicSet_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "FixedTopicSet");
  private static final QName _TopicExpressionDialect_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "TopicExpressionDialect");
  private static final QName _TopicSet_QNAME = new QName("http:docs.oasis-open.orgwsnt-1", "TopicSet");
  private static final QName _Filter_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "Filter");
  private static final QName _SubscriptionPolicy_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "SubscriptionPolicy");
  private static final QName _SubscriptionReference_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "SubscriptionReference");
  private static final QName _ProducerReference_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "ProducerReference");
  private static final QName _NotificationMessage_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "NotificationMessage");
  private static final QName _CurrentTime_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "CurrentTime");
  private static final QName _TerminationTime_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "TerminationTime");
  private static final QName _ProducerProperties_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "ProducerProperties");
  private static final QName _MessageContent_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "MessageContent");
  private static final QName _SubscribeCreationFailedFault_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "SubscribeCreationFailedFault");
  private static final QName _InvalidFilterFault_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "InvalidFilterFault");
  private static final QName _TopicExpressionDialectUnknownFault_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "TopicExpressionDialectUnknownFault");
  private static final QName _InvalidTopicExpressionFault_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "InvalidTopicExpressionFault");
  private static final QName _TopicNotSupportedFault_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "TopicNotSupportedFault");
  private static final QName _MultipleTopicsSpecifiedFault_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "MultipleTopicsSpecifiedFault");
  private static final QName _InvalidProducerPropertiesExpressionFault_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "InvalidProducerPropertiesExpressionFault");
  private static final QName _InvalidMessageContentExpressionFault_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "InvalidMessageContentExpressionFault");
  private static final QName _UnrecognizedPolicyRequestFault_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "UnrecognizedPolicyRequestFault");
  private static final QName _UnsupportedPolicyRequestFault_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "UnsupportedPolicyRequestFault");
  private static final QName _NotifyMessageNotSupportedFault_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "NotifyMessageNotSupportedFault");
  private static final QName _UnacceptableInitialTerminationTimeFault_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "UnacceptableInitialTerminationTimeFault");
  private static final QName _NoCurrentMessageOnTopicFault_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "NoCurrentMessageOnTopicFault");
  private static final QName _UnableToGetMessagesFault_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "UnableToGetMessagesFault");
  private static final QName _UnableToDestroyPullPointFault_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "UnableToDestroyPullPointFault");
  private static final QName _UnableToCreatePullPointFault_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "UnableToCreatePullPointFault");
  private static final QName _UnacceptableTerminationTimeFault_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "UnacceptableTerminationTimeFault");
  private static final QName _UnableToDestroySubscriptionFault_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "UnableToDestroySubscriptionFault");
  private static final QName _PauseFailedFault_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "PauseFailedFault");
  private static final QName _ResumeFailedFault_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "ResumeFailedFault");
  private static final QName _EndpointReference_QNAME = new QName("http:www.w3.org200508addressing", "EndpointReference");
  private static final QName _Metadata_QNAME = new QName("http:www.w3.org200508addressing", "Metadata");
  private static final QName _MessageID_QNAME = new QName("http:www.w3.org200508addressing", "MessageID");
  private static final QName _RelatesTo_QNAME = new QName("http:www.w3.org200508addressing", "RelatesTo");
  private static final QName _ReplyTo_QNAME = new QName("http:www.w3.org200508addressing", "ReplyTo");
  private static final QName _From_QNAME = new QName("http:www.w3.org200508addressing", "From");
  private static final QName _FaultTo_QNAME = new QName("http:www.w3.org200508addressing", "FaultTo");
  private static final QName _To_QNAME = new QName("http:www.w3.org200508addressing", "To");
  private static final QName _Action_QNAME = new QName("http:www.w3.org200508addressing", "Action");
  private static final QName _RetryAfter_QNAME = new QName("http:www.w3.org200508addressing", "RetryAfter");
  private static final QName _ProblemHeaderQName_QNAME = new QName("http:www.w3.org200508addressing", "ProblemHeaderQName");
  private static final QName _ProblemHeader_QNAME = new QName("http:www.w3.org200508addressing", "ProblemHeader");
  private static final QName _ProblemIRI_QNAME = new QName("http:www.w3.org200508addressing", "ProblemIRI");
  private static final QName _ProblemAction_QNAME = new QName("http:www.w3.org200508addressing", "ProblemAction");
  private static final QName _BaseFault_QNAME = new QName("http:docs.oasis-open.orgwsrfbf-2", "BaseFault");
  private static final QName _TopicNamespace_QNAME = new QName("http:docs.oasis-open.orgwsnt-1", "TopicNamespace");
  private static final QName _RequiresRegistration_QNAME = new QName("http:docs.oasis-open.orgwsnbr-2", "RequiresRegistration");
  private static final QName _PublisherReference_QNAME = new QName("http:docs.oasis-open.orgwsnbr-2", "PublisherReference");
  private static final QName _Demand_QNAME = new QName("http:docs.oasis-open.orgwsnbr-2", "Demand");
  private static final QName _PublisherRegistrationRejectedFault_QNAME = new QName("http:docs.oasis-open.orgwsnbr-2", "PublisherRegistrationRejectedFault");
  private static final QName _PublisherRegistrationFailedFault_QNAME = new QName("http:docs.oasis-open.orgwsnbr-2", "PublisherRegistrationFailedFault");
  private static final QName _ResourceNotDestroyedFault_QNAME = new QName("http:docs.oasis-open.orgwsnbr-2", "ResourceNotDestroyedFault");
  private static final QName _SubscribeInitialTerminationTime_QNAME = new QName("http:docs.oasis-open.orgwsnb-2", "InitialTerminationTime");

  
  public Subscribe createSubscribe() {
    return new Subscribe();
  }

  
  public BaseFaultType createBaseFaultType() {
    return new BaseFaultType();
  }

  
  public TopicNamespaceType createTopicNamespaceType() {
    return new TopicNamespaceType();
  }

  
  public NotificationMessageHolderType createNotificationMessageHolderType() {
    return new NotificationMessageHolderType();
  }

  
  public TopicExpressionType createTopicExpressionType() {
    return new TopicExpressionType();
  }

  
  public NotificationProducerRP createNotificationProducerRP() {
    return new NotificationProducerRP();
  }

  
  public TopicSetType createTopicSetType() {
    return new TopicSetType();
  }

  
  public EndpointReferenceType createEndpointReferenceType() {
    return new EndpointReferenceType();
  }

  
  public FilterType createFilterType() {
    return new FilterType();
  }

  
  public SubscriptionPolicyType createSubscriptionPolicyType() {
    return new SubscriptionPolicyType();
  }

  
  public SubscriptionManagerRP createSubscriptionManagerRP() {
    return new SubscriptionManagerRP();
  }

  
  public Notify createNotify() {
    return new Notify();
  }

  
  public QueryExpressionTypeB2 createQueryExpressionTypeB2() {
    return new QueryExpressionTypeB2();
  }

  
  public UseRaw createUseRaw() {
    return new UseRaw();
  }

  
  public Subscribe.SubscriptionPolicy createSubscribeSubscriptionPolicy() {
    return new Subscribe.SubscriptionPolicy();
  }

  
  public SubscribeResponse createSubscribeResponse() {
    return new SubscribeResponse();
  }

  
  public GetCurrentMessage createGetCurrentMessage() {
    return new GetCurrentMessage();
  }

  
  public GetCurrentMessageResponse createGetCurrentMessageResponse() {
    return new GetCurrentMessageResponse();
  }

  
  public SubscribeCreationFailedFaultType createSubscribeCreationFailedFaultType() {
    return new SubscribeCreationFailedFaultType();
  }

  
  public InvalidFilterFaultType createInvalidFilterFaultType() {
    return new InvalidFilterFaultType();
  }

  
  public TopicExpressionDialectUnknownFaultType createTopicExpressionDialectUnknownFaultType() {
    return new TopicExpressionDialectUnknownFaultType();
  }

  
  public InvalidTopicExpressionFaultType createInvalidTopicExpressionFaultType() {
    return new InvalidTopicExpressionFaultType();
  }

  
  public TopicNotSupportedFaultType createTopicNotSupportedFaultType() {
    return new TopicNotSupportedFaultType();
  }

  
  public MultipleTopicsSpecifiedFaultType createMultipleTopicsSpecifiedFaultType() {
    return new MultipleTopicsSpecifiedFaultType();
  }

  
  public InvalidProducerPropertiesExpressionFaultType createInvalidProducerPropertiesExpressionFaultType() {
    return new InvalidProducerPropertiesExpressionFaultType();
  }

  
  public InvalidMessageContentExpressionFaultType createInvalidMessageContentExpressionFaultType() {
    return new InvalidMessageContentExpressionFaultType();
  }

  
  public UnrecognizedPolicyRequestFaultType createUnrecognizedPolicyRequestFaultType() {
    return new UnrecognizedPolicyRequestFaultType();
  }

  
  public UnsupportedPolicyRequestFaultType createUnsupportedPolicyRequestFaultType() {
    return new UnsupportedPolicyRequestFaultType();
  }

  
  public NotifyMessageNotSupportedFaultType createNotifyMessageNotSupportedFaultType() {
    return new NotifyMessageNotSupportedFaultType();
  }

  
  public UnacceptableInitialTerminationTimeFaultType createUnacceptableInitialTerminationTimeFaultType() {
    return new UnacceptableInitialTerminationTimeFaultType();
  }

  
  public NoCurrentMessageOnTopicFaultType createNoCurrentMessageOnTopicFaultType() {
    return new NoCurrentMessageOnTopicFaultType();
  }

  
  public GetMessages createGetMessages() {
    return new GetMessages();
  }

  
  public GetMessagesResponse createGetMessagesResponse() {
    return new GetMessagesResponse();
  }

  
  public DestroyPullPoint createDestroyPullPoint() {
    return new DestroyPullPoint();
  }

  
  public DestroyPullPointResponse createDestroyPullPointResponse() {
    return new DestroyPullPointResponse();
  }

  
  public UnableToGetMessagesFaultType createUnableToGetMessagesFaultType() {
    return new UnableToGetMessagesFaultType();
  }

  
  public UnableToDestroyPullPointFaultType createUnableToDestroyPullPointFaultType() {
    return new UnableToDestroyPullPointFaultType();
  }

  
  public CreatePullPoint createCreatePullPoint() {
    return new CreatePullPoint();
  }

  
  public CreatePullPointResponse createCreatePullPointResponse() {
    return new CreatePullPointResponse();
  }

  
  public UnableToCreatePullPointFaultType createUnableToCreatePullPointFaultType() {
    return new UnableToCreatePullPointFaultType();
  }

  
  public Renew createRenew() {
    return new Renew();
  }

  
  public RenewResponse createRenewResponse() {
    return new RenewResponse();
  }

  
  public UnacceptableTerminationTimeFaultType createUnacceptableTerminationTimeFaultType() {
    return new UnacceptableTerminationTimeFaultType();
  }

  
  public Unsubscribe createUnsubscribe() {
    return new Unsubscribe();
  }

  
  public UnsubscribeResponse createUnsubscribeResponse() {
    return new UnsubscribeResponse();
  }

  
  public UnableToDestroySubscriptionFaultType createUnableToDestroySubscriptionFaultType() {
    return new UnableToDestroySubscriptionFaultType();
  }

  
  public PauseSubscription createPauseSubscription() {
    return new PauseSubscription();
  }

  
  public PauseSubscriptionResponse createPauseSubscriptionResponse() {
    return new PauseSubscriptionResponse();
  }

  
  public ResumeSubscription createResumeSubscription() {
    return new ResumeSubscription();
  }

  
  public ResumeSubscriptionResponse createResumeSubscriptionResponse() {
    return new ResumeSubscriptionResponse();
  }

  
  public PauseFailedFaultType createPauseFailedFaultType() {
    return new PauseFailedFaultType();
  }

  
  public ResumeFailedFaultType createResumeFailedFaultType() {
    return new ResumeFailedFaultType();
  }

  
  public MetadataType createMetadataType() {
    return new MetadataType();
  }

  
  public AttributedURIType createAttributedURIType() {
    return new AttributedURIType();
  }

  
  public RelatesToType createRelatesToType() {
    return new RelatesToType();
  }

  
  public AttributedUnsignedLongType createAttributedUnsignedLongType() {
    return new AttributedUnsignedLongType();
  }

  
  public AttributedQNameType createAttributedQNameType() {
    return new AttributedQNameType();
  }

  
  public AttributedAnyType createAttributedAnyType() {
    return new AttributedAnyType();
  }

  
  public ProblemActionType createProblemActionType() {
    return new ProblemActionType();
  }

  
  public ReferenceParametersType createReferenceParametersType() {
    return new ReferenceParametersType();
  }

  
  public Documentation createDocumentation() {
    return new Documentation();
  }

  
  public QueryExpressionTypeT1 createQueryExpressionTypeT1() {
    return new QueryExpressionTypeT1();
  }

  
  public TopicType createTopicType() {
    return new TopicType();
  }

  
  public NotificationBrokerRP createNotificationBrokerRP() {
    return new NotificationBrokerRP();
  }

  
  public PublisherRegistrationRP createPublisherRegistrationRP() {
    return new PublisherRegistrationRP();
  }

  
  public RegisterPublisher createRegisterPublisher() {
    return new RegisterPublisher();
  }

  
  public RegisterPublisherResponse createRegisterPublisherResponse() {
    return new RegisterPublisherResponse();
  }

  
  public PublisherRegistrationRejectedFaultType createPublisherRegistrationRejectedFaultType() {
    return new PublisherRegistrationRejectedFaultType();
  }

  
  public PublisherRegistrationFailedFaultType createPublisherRegistrationFailedFaultType() {
    return new PublisherRegistrationFailedFaultType();
  }

  
  public DestroyRegistration createDestroyRegistration() {
    return new DestroyRegistration();
  }

  
  public DestroyRegistrationResponse createDestroyRegistrationResponse() {
    return new DestroyRegistrationResponse();
  }

  
  public ResourceNotDestroyedFaultType createResourceNotDestroyedFaultType() {
    return new ResourceNotDestroyedFaultType();
  }

  
  public BaseFaultType.ErrorCode createBaseFaultTypeErrorCode() {
    return new BaseFaultType.ErrorCode();
  }

  
  public BaseFaultType.Description createBaseFaultTypeDescription() {
    return new BaseFaultType.Description();
  }

  
  public BaseFaultType.FaultCause createBaseFaultTypeFaultCause() {
    return new BaseFaultType.FaultCause();
  }

  
  public TopicNamespaceType.Topic createTopicNamespaceTypeTopic() {
    return new TopicNamespaceType.Topic();
  }

  
  public NotificationMessageHolderType.Message createNotificationMessageHolderTypeMessage() {
    return new NotificationMessageHolderType.Message();
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "TopicExpression")
  public JAXBElement<TopicExpressionType> createTopicExpression(TopicExpressionType value) {
    return new JAXBElement(_TopicExpression_QNAME, TopicExpressionType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "FixedTopicSet", defaultValue = "true")
  public JAXBElement<Boolean> createFixedTopicSet(Boolean value) {
    return new JAXBElement(_FixedTopicSet_QNAME, Boolean.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "TopicExpressionDialect")
  public JAXBElement<String> createTopicExpressionDialect(String value) {
    return new JAXBElement(_TopicExpressionDialect_QNAME, String.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnt-1", name = "TopicSet")
  public JAXBElement<TopicSetType> createTopicSet(TopicSetType value) {
    return new JAXBElement(_TopicSet_QNAME, TopicSetType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "ConsumerReference")
  public ConsumerReferenceB2 createConsumerReferenceB2(EndpointReferenceType value) {
    return new ConsumerReferenceB2(value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "Filter")
  public JAXBElement<FilterType> createFilter(FilterType value) {
    return new JAXBElement(_Filter_QNAME, FilterType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "SubscriptionPolicy")
  public JAXBElement<SubscriptionPolicyType> createSubscriptionPolicy(SubscriptionPolicyType value) {
    return new JAXBElement(_SubscriptionPolicy_QNAME, SubscriptionPolicyType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "CreationTime")
  public CreationTimeB2 createCreationTimeB2(XMLGregorianCalendar value) {
    return new CreationTimeB2(value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "SubscriptionReference")
  public JAXBElement<EndpointReferenceType> createSubscriptionReference(EndpointReferenceType value) {
    return new JAXBElement(_SubscriptionReference_QNAME, EndpointReferenceType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "Topic")
  public TopicB2 createTopicB2(TopicExpressionType value) {
    return new TopicB2(value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "ProducerReference")
  public JAXBElement<EndpointReferenceType> createProducerReference(EndpointReferenceType value) {
    return new JAXBElement(_ProducerReference_QNAME, EndpointReferenceType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "NotificationMessage")
  public JAXBElement<NotificationMessageHolderType> createNotificationMessage(NotificationMessageHolderType value) {
    return new JAXBElement(_NotificationMessage_QNAME, NotificationMessageHolderType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "CurrentTime")
  public JAXBElement<XMLGregorianCalendar> createCurrentTime(XMLGregorianCalendar value) {
    return new JAXBElement(_CurrentTime_QNAME, XMLGregorianCalendar.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "TerminationTime")
  public JAXBElement<XMLGregorianCalendar> createTerminationTime(XMLGregorianCalendar value) {
    return new JAXBElement(_TerminationTime_QNAME, XMLGregorianCalendar.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "ProducerProperties")
  public JAXBElement<QueryExpressionTypeB2> createProducerProperties(QueryExpressionTypeB2 value) {
    return new JAXBElement(_ProducerProperties_QNAME, QueryExpressionTypeB2.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "MessageContent")
  public JAXBElement<QueryExpressionTypeB2> createMessageContent(QueryExpressionTypeB2 value) {
    return new JAXBElement(_MessageContent_QNAME, QueryExpressionTypeB2.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "SubscribeCreationFailedFault")
  public JAXBElement<SubscribeCreationFailedFaultType> createSubscribeCreationFailedFault(SubscribeCreationFailedFaultType value) {
    return new JAXBElement(_SubscribeCreationFailedFault_QNAME, SubscribeCreationFailedFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "InvalidFilterFault")
  public JAXBElement<InvalidFilterFaultType> createInvalidFilterFault(InvalidFilterFaultType value) {
    return new JAXBElement(_InvalidFilterFault_QNAME, InvalidFilterFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "TopicExpressionDialectUnknownFault")
  public JAXBElement<TopicExpressionDialectUnknownFaultType> createTopicExpressionDialectUnknownFault(TopicExpressionDialectUnknownFaultType value) {
    return new JAXBElement(_TopicExpressionDialectUnknownFault_QNAME, TopicExpressionDialectUnknownFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "InvalidTopicExpressionFault")
  public JAXBElement<InvalidTopicExpressionFaultType> createInvalidTopicExpressionFault(InvalidTopicExpressionFaultType value) {
    return new JAXBElement(_InvalidTopicExpressionFault_QNAME, InvalidTopicExpressionFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "TopicNotSupportedFault")
  public JAXBElement<TopicNotSupportedFaultType> createTopicNotSupportedFault(TopicNotSupportedFaultType value) {
    return new JAXBElement(_TopicNotSupportedFault_QNAME, TopicNotSupportedFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "MultipleTopicsSpecifiedFault")
  public JAXBElement<MultipleTopicsSpecifiedFaultType> createMultipleTopicsSpecifiedFault(MultipleTopicsSpecifiedFaultType value) {
    return new JAXBElement(_MultipleTopicsSpecifiedFault_QNAME, MultipleTopicsSpecifiedFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "InvalidProducerPropertiesExpressionFault")
  public JAXBElement<InvalidProducerPropertiesExpressionFaultType> createInvalidProducerPropertiesExpressionFault(InvalidProducerPropertiesExpressionFaultType value) {
    return new JAXBElement(_InvalidProducerPropertiesExpressionFault_QNAME, InvalidProducerPropertiesExpressionFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "InvalidMessageContentExpressionFault")
  public JAXBElement<InvalidMessageContentExpressionFaultType> createInvalidMessageContentExpressionFault(InvalidMessageContentExpressionFaultType value) {
    return new JAXBElement(_InvalidMessageContentExpressionFault_QNAME, InvalidMessageContentExpressionFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "UnrecognizedPolicyRequestFault")
  public JAXBElement<UnrecognizedPolicyRequestFaultType> createUnrecognizedPolicyRequestFault(UnrecognizedPolicyRequestFaultType value) {
    return new JAXBElement(_UnrecognizedPolicyRequestFault_QNAME, UnrecognizedPolicyRequestFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "UnsupportedPolicyRequestFault")
  public JAXBElement<UnsupportedPolicyRequestFaultType> createUnsupportedPolicyRequestFault(UnsupportedPolicyRequestFaultType value) {
    return new JAXBElement(_UnsupportedPolicyRequestFault_QNAME, UnsupportedPolicyRequestFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "NotifyMessageNotSupportedFault")
  public JAXBElement<NotifyMessageNotSupportedFaultType> createNotifyMessageNotSupportedFault(NotifyMessageNotSupportedFaultType value) {
    return new JAXBElement(_NotifyMessageNotSupportedFault_QNAME, NotifyMessageNotSupportedFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "UnacceptableInitialTerminationTimeFault")
  public JAXBElement<UnacceptableInitialTerminationTimeFaultType> createUnacceptableInitialTerminationTimeFault(UnacceptableInitialTerminationTimeFaultType value) {
    return new JAXBElement(_UnacceptableInitialTerminationTimeFault_QNAME, UnacceptableInitialTerminationTimeFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "NoCurrentMessageOnTopicFault")
  public JAXBElement<NoCurrentMessageOnTopicFaultType> createNoCurrentMessageOnTopicFault(NoCurrentMessageOnTopicFaultType value) {
    return new JAXBElement(_NoCurrentMessageOnTopicFault_QNAME, NoCurrentMessageOnTopicFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "UnableToGetMessagesFault")
  public JAXBElement<UnableToGetMessagesFaultType> createUnableToGetMessagesFault(UnableToGetMessagesFaultType value) {
    return new JAXBElement(_UnableToGetMessagesFault_QNAME, UnableToGetMessagesFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "UnableToDestroyPullPointFault")
  public JAXBElement<UnableToDestroyPullPointFaultType> createUnableToDestroyPullPointFault(UnableToDestroyPullPointFaultType value) {
    return new JAXBElement(_UnableToDestroyPullPointFault_QNAME, UnableToDestroyPullPointFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "UnableToCreatePullPointFault")
  public JAXBElement<UnableToCreatePullPointFaultType> createUnableToCreatePullPointFault(UnableToCreatePullPointFaultType value) {
    return new JAXBElement(_UnableToCreatePullPointFault_QNAME, UnableToCreatePullPointFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "UnacceptableTerminationTimeFault")
  public JAXBElement<UnacceptableTerminationTimeFaultType> createUnacceptableTerminationTimeFault(UnacceptableTerminationTimeFaultType value) {
    return new JAXBElement(_UnacceptableTerminationTimeFault_QNAME, UnacceptableTerminationTimeFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "UnableToDestroySubscriptionFault")
  public JAXBElement<UnableToDestroySubscriptionFaultType> createUnableToDestroySubscriptionFault(UnableToDestroySubscriptionFaultType value) {
    return new JAXBElement(_UnableToDestroySubscriptionFault_QNAME, UnableToDestroySubscriptionFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "PauseFailedFault")
  public JAXBElement<PauseFailedFaultType> createPauseFailedFault(PauseFailedFaultType value) {
    return new JAXBElement(_PauseFailedFault_QNAME, PauseFailedFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "ResumeFailedFault")
  public JAXBElement<ResumeFailedFaultType> createResumeFailedFault(ResumeFailedFaultType value) {
    return new JAXBElement(_ResumeFailedFault_QNAME, ResumeFailedFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:www.w3.org200508addressing", name = "EndpointReference")
  public JAXBElement<EndpointReferenceType> createEndpointReference(EndpointReferenceType value) {
    return new JAXBElement(_EndpointReference_QNAME, EndpointReferenceType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:www.w3.org200508addressing", name = "Metadata")
  public JAXBElement<MetadataType> createMetadata(MetadataType value) {
    return new JAXBElement(_Metadata_QNAME, MetadataType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:www.w3.org200508addressing", name = "MessageID")
  public JAXBElement<AttributedURIType> createMessageID(AttributedURIType value) {
    return new JAXBElement(_MessageID_QNAME, AttributedURIType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:www.w3.org200508addressing", name = "RelatesTo")
  public JAXBElement<RelatesToType> createRelatesTo(RelatesToType value) {
    return new JAXBElement(_RelatesTo_QNAME, RelatesToType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:www.w3.org200508addressing", name = "ReplyTo")
  public JAXBElement<EndpointReferenceType> createReplyTo(EndpointReferenceType value) {
    return new JAXBElement(_ReplyTo_QNAME, EndpointReferenceType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:www.w3.org200508addressing", name = "From")
  public JAXBElement<EndpointReferenceType> createFrom(EndpointReferenceType value) {
    return new JAXBElement(_From_QNAME, EndpointReferenceType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:www.w3.org200508addressing", name = "FaultTo")
  public JAXBElement<EndpointReferenceType> createFaultTo(EndpointReferenceType value) {
    return new JAXBElement(_FaultTo_QNAME, EndpointReferenceType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:www.w3.org200508addressing", name = "To")
  public JAXBElement<AttributedURIType> createTo(AttributedURIType value) {
    return new JAXBElement(_To_QNAME, AttributedURIType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:www.w3.org200508addressing", name = "Action")
  public JAXBElement<AttributedURIType> createAction(AttributedURIType value) {
    return new JAXBElement(_Action_QNAME, AttributedURIType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:www.w3.org200508addressing", name = "RetryAfter")
  public JAXBElement<AttributedUnsignedLongType> createRetryAfter(AttributedUnsignedLongType value) {
    return new JAXBElement(_RetryAfter_QNAME, AttributedUnsignedLongType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:www.w3.org200508addressing", name = "ProblemHeaderQName")
  public JAXBElement<AttributedQNameType> createProblemHeaderQName(AttributedQNameType value) {
    return new JAXBElement(_ProblemHeaderQName_QNAME, AttributedQNameType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:www.w3.org200508addressing", name = "ProblemHeader")
  public JAXBElement<AttributedAnyType> createProblemHeader(AttributedAnyType value) {
    return new JAXBElement(_ProblemHeader_QNAME, AttributedAnyType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:www.w3.org200508addressing", name = "ProblemIRI")
  public JAXBElement<AttributedURIType> createProblemIRI(AttributedURIType value) {
    return new JAXBElement(_ProblemIRI_QNAME, AttributedURIType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:www.w3.org200508addressing", name = "ProblemAction")
  public JAXBElement<ProblemActionType> createProblemAction(ProblemActionType value) {
    return new JAXBElement(_ProblemAction_QNAME, ProblemActionType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsrfbf-2", name = "BaseFault")
  public JAXBElement<BaseFaultType> createBaseFault(BaseFaultType value) {
    return new JAXBElement(_BaseFault_QNAME, BaseFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnt-1", name = "TopicNamespace")
  public JAXBElement<TopicNamespaceType> createTopicNamespace(TopicNamespaceType value) {
    return new JAXBElement(_TopicNamespace_QNAME, TopicNamespaceType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnbr-2", name = "RequiresRegistration")
  public JAXBElement<Boolean> createRequiresRegistration(Boolean value) {
    return new JAXBElement(_RequiresRegistration_QNAME, Boolean.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnbr-2", name = "PublisherReference")
  public JAXBElement<EndpointReferenceType> createPublisherReference(EndpointReferenceType value) {
    return new JAXBElement(_PublisherReference_QNAME, EndpointReferenceType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnbr-2", name = "ConsumerReference")
  public ConsumerReferenceBR2 createConsumerReferenceBR2(EndpointReferenceType value) {
    return new ConsumerReferenceBR2(value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnbr-2", name = "Topic")
  public TopicBR2 createTopicBR2(TopicExpressionType value) {
    return new TopicBR2(value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnbr-2", name = "Demand")
  public JAXBElement<Boolean> createDemand(Boolean value) {
    return new JAXBElement(_Demand_QNAME, Boolean.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnbr-2", name = "CreationTime")
  public CreationTimeBR2 createCreationTimeBR2(XMLGregorianCalendar value) {
    return new CreationTimeBR2(value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnbr-2", name = "PublisherRegistrationRejectedFault")
  public JAXBElement<PublisherRegistrationRejectedFaultType> createPublisherRegistrationRejectedFault(PublisherRegistrationRejectedFaultType value) {
    return new JAXBElement(_PublisherRegistrationRejectedFault_QNAME, PublisherRegistrationRejectedFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnbr-2", name = "PublisherRegistrationFailedFault")
  public JAXBElement<PublisherRegistrationFailedFaultType> createPublisherRegistrationFailedFault(PublisherRegistrationFailedFaultType value) {
    return new JAXBElement(_PublisherRegistrationFailedFault_QNAME, PublisherRegistrationFailedFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnbr-2", name = "ResourceNotDestroyedFault")
  public JAXBElement<ResourceNotDestroyedFaultType> createResourceNotDestroyedFault(ResourceNotDestroyedFaultType value) {
    return new JAXBElement(_ResourceNotDestroyedFault_QNAME, ResourceNotDestroyedFaultType.class, null, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "ConsumerReference", scope = Subscribe.class)
  public Subscribe.ConsumerReferenceB2 createSubscribeConsumerReferenceB2(EndpointReferenceType value) {
    return new Subscribe.ConsumerReferenceB2(value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "InitialTerminationTime", scope = Subscribe.class)
  public JAXBElement<String> createSubscribeInitialTerminationTime(String value) {
    return new JAXBElement(_SubscribeInitialTerminationTime_QNAME, String.class, Subscribe.class, value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnb-2", name = "Topic", scope = GetCurrentMessage.class)
  public GetCurrentMessage.TopicB2 createGetCurrentMessageTopicB2(TopicExpressionType value) {
    return new GetCurrentMessage.TopicB2(value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnbr-2", name = "Topic", scope = RegisterPublisher.class)
  public RegisterPublisher.TopicBR2 createRegisterPublisherTopicBR2(TopicExpressionType value) {
    return new RegisterPublisher.TopicBR2(value);
  }

  
  @XmlElementDecl(namespace = "http:docs.oasis-open.orgwsnbr-2", name = "ConsumerReference", scope = RegisterPublisherResponse.class)
  public RegisterPublisherResponse.ConsumerReferenceBR2 createRegisterPublisherResponseConsumerReferenceBR2(EndpointReferenceType value) {
    return new RegisterPublisherResponse.ConsumerReferenceBR2(value);
  }
}
