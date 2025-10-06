# System Design Interview LLM Prompt Template & Guidelines

## **INSTRUCTIONS FOR LLM: System Design Interview Framework**

This document serves as a structured prompt template for LLMs to conduct realistic system design interviews for **senior engineers and tech leads**. Follow the 4-phase approach below, presenting the solution as a natural dialogue between an interviewer (I) and candidate (C).

---

## **LLM RESPONSE FORMAT TEMPLATE**

When answering system design questions, structure your response exactly as follows:

```
# System Design Interview: [PROBLEM TITLE]

**Question:** [Insert the actual question here]

---

## Mock Interview Dialogue

**Interviewer (I):** [Present the problem statement and context]

**Candidate (C):** Thank you for the interesting problem. I'll approach this systematically using a four-phase structure: understanding the problem and establishing design scope, high-level design with typical use cases, detailed design focusing on key components, and finally wrap-up with trade-offs and future improvements. Let me start by clarifying the requirements.

[Continue with the 4 phases below...]
```

---

## **PHASE 1: UNDERSTAND THE PROBLEM and REQUIREMENTS (3-5 minutes)**

### **LLM Instructions:** Focus on clarifying requirements and constraints through strategic questions

#### **Requirements Clarification Framework:**
```
**C:** Let me start by understanding the core requirements and constraints:
Give a list of sample questions to ask the interviewer to clarify requirements, 
both functional and non-functional, for the given question accordingly.

```

#### **Simple Back-of-Envelope Calculations/Estimation:**
```
**C:** Let me do some quick capacity estimation:

**Scale Estimates:**
- Daily Active Users (DAU): [X]
- Read QPS: [calculation] 
- Write QPS: [calculation]
- Storage per day: [calculation]
- Total storage (5 years): [calculation]
- Bandwidth: [calculation]

**Key Numbers to Remember:**
- Peak load is typically 2-3x average
- 80/20 rule for hot data
- Network latency considerations for global users
```

---

## **PHASE 2: HIGH-LEVEL DESIGN (10-15 minutes)**

### **LLM Instructions:** Create box diagram and walk through typical use case

#### **High-Level Architecture Template:**
```
**C:** Let me propose a high-level architecture:

**System Architecture Diagram (ASCII):**
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Client    │────│Load Balancer│──  │  Web Servers│
│ (Mobile/Web)│    │             │    │  (Stateless)│
└─────────────┘    └─────────────┘    └─────────────┘
                                              │
                                              ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Cache     │────│ Application │────│  Database   │
│  (Redis)    │    │   Services  │    │ (Primary +  │
 ─────────────┘    └─────────────┘    │ Replicas)   │
                                      └─────────────┘
```

**Core Components:**
1. **Load Balancer**: [Purpose and algorithm choice]
2. **Web Servers**: [Technology and scaling approach]
3. **Application Services**: [Core business logic services]
4. **Cache Layer**: [Caching strategy and invalidation]
5. **Database**: [Database choice and rationale]
6. **Additional Components**: [CDN, message queues, etc.]
```

#### **API Design:**
It is not always necessary to define APIs, but if relevant:
```
**C:** Here are the key APIs:

**Core Endpoints:**
```
// Primary operations
POST /api/v1/[resource]           - Create new [resource]
GET  /api/v1/[resource]/{id}      - Get specific [resource]
GET  /api/v1/[resource]?filters   - List/search [resources]
PUT  /api/v1/[resource]/{id}      - Update [resource]
DELETE /api/v1/[resource]/{id}    - Delete [resource]

// Example request/response
POST /api/v1/[resource]
{
  "key_field": "value",
  "metadata": {...}
}

Response: 201 Created
{
  "id": "uuid",
  "key_field": "value", 
  "created_at": "timestamp",
  "status": "success"
}
```

#### **Typical Use Case Walkthrough:**
```
**C:** Let me walk through a typical (user) workflow:

**Scenario**: [Describe a common user journey]

**Step-by-step flow:**
1. User [action] → Client sends [API call]
2. Load balancer routes to available web server
3. Web server [processing logic]
4. Check cache for [data] → Cache hit/miss handling
5. Query database for [required data]
6. [Business logic processing]
7. Update cache with [result]
8. Return response to client
9. Client updates UI

**Performance Characteristics:**
- Expected latency: [Xms] for [operation]
- Cache hit ratio: [X%] for [data type]
- Database queries: [X] per request
```

#### **Interviewer Interaction Points:**
```
**I:** What happens if [specific component] fails?
**C:** Good question. For [component] failure, we have [specific mitigation strategy]...

**I:** How do you handle [specific edge case]?
**C:** For [edge case], I would [solution approach] because [reasoning]...

**I:** Can you explain why you chose [technology X] over [technology Y]?
**C:** I chose [X] over [Y] because [specific technical and business rationale]...
```

---

## **PHASE 3: DETAILED DESIGN (10-20 minutes)**

### **LLM Instructions:** Work with interviewer to identify and prioritize components. Adapt based on interviewer's focus areas.

#### **Component Deep Dive Framework:**

##### **Option A: Database Design Focus**
It is not always necessary to go deep into database design, but if relevant:
```
**C:** Let me dive into the database design:

**Schema Design:**
```sql
-- Core entities
CREATE TABLE [primary_entity] (
    id UUID PRIMARY KEY,
    [key_fields],
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    INDEX(created_at),
    INDEX([frequently_queried_field])
);

CREATE TABLE [relationship_table] (
    [foreign_keys],
    [relationship_data],
    PRIMARY KEY ([composite_key])
);
```

**Database Scaling Strategy:**
- **Read Replicas**: [X] replicas for read traffic distribution
- **Sharding Strategy**: Shard by [key] using [algorithm]
- **Partitioning**: Time-based partitioning for [time-series data]
- **Indexing**: Strategic indexes on [frequently queried columns]

**Data Consistency Approach:**
- Strong consistency for [critical operations]
- Eventual consistency for [non-critical data]
- Conflict resolution strategy: [approach]
```

##### **Option B: Performance & Scalability Focus**
```
**C:** Let me focus on performance characteristics and bottlenecks:
Performance analysis and optimization strategies are always per the specific system:

**Performance Analysis:**
- **Bottleneck Identification**: [Component] under [load scenario]
- **Capacity Planning**: Current design supports [X] QPS
- **Scaling Triggers**: Auto-scale when [metric] exceeds [threshold]

**Optimization Strategies:**
1. **Caching**: [Multi-level caching strategy]
   - L1 Cache: [Application cache for hot data]
   - L2 Cache: [Distributed cache for shared data]
   - CDN: [Static content and geographic distribution]

2. **Database Optimization**:
   - Connection pooling with [X] connections
   - Query optimization for [critical queries]
   - Read replicas routing strategy

3. **Application Performance**:
   - Asynchronous processing for [heavy operations]
   - Batch processing for [bulk operations]
   - Resource pooling and reuse

**Load Testing Results** (estimated):
- Current capacity: [X] QPS at [Y]ms p95 latency
- Breaking point: [X] QPS before degradation
- Scaling headroom: [X]x with current architecture
```

##### **Option C: Service Architecture Focus**
```
**C:** Let me detail the service decomposition and communication:

**Microservices Breakdown:**
1. **[Service 1]**: [Responsibility and boundaries]
   - Domain: [Business domain]
   - APIs: [Key endpoints]
   - Data: [Owned data entities]

2. **[Service 2]**: [Responsibility and boundaries]
   - Domain: [Business domain]
   - APIs: [Key endpoints]
   - Data: [Owned data entities]

**Service Communication:**
- **Synchronous**: REST/gRPC for [real-time operations]
- **Asynchronous**: Message queues for [event-driven workflows]
- **Data Consistency**: Saga pattern for [distributed transactions]

**Service Discovery & Resilience:**
- Service mesh for [communication management]
- Circuit breakers for [failure isolation]
- Retry policies with exponential backoff
- Bulkhead pattern for [resource isolation]
```

#### **Advanced Topics (Senior Level):**

##### **Monitoring & Observability:**
```
**C:** For a production system, observability is critical:

**Monitoring Stack:**
- **Metrics**: [Key business and technical metrics]
  - Business: [conversion rate, user engagement]
  - Technical: [latency, error rate, throughput]
  - Infrastructure: [CPU, memory, disk, network]

- **Logging**: Structured logging with correlation IDs
  - Critical events: [user actions, system errors]
  - Audit logs: [compliance and security events]
  - Performance logs: [slow queries, timeouts]

- **Distributed Tracing**: Request flow across services
  - Trace sampling strategy
  - Performance bottleneck identification
  - Error root cause analysis

**Alerting Strategy:**
- SLI/SLO framework with [specific targets]
- Multi-level alerts: Warning → Critical → Emergency
- Escalation procedures and on-call rotation
```

##### **Security & Compliance:**
```
**C:** Security considerations for enterprise deployment:

**Authentication & Authorization:**
- OAuth 2.0/JWT for API authentication
- Role-based access control (RBAC)
- Multi-factor authentication for admin operations

**Data Protection:**
- Encryption at rest and in transit
- PII data handling and anonymization
- GDPR compliance for data deletion

**Infrastructure Security:**
- Network segmentation and firewalls
- Regular security audits and penetration testing
- Secrets management for API keys and credentials
```

---

## **PHASE 4: WRAP-UP AND SUMMARY (3-5 minutes)**

### **LLM Instructions:** Summarize key decisions, trade-offs, and future improvements

#### **Summary Template:**
```
**C:** Let me summarize the key aspects of this design:

**Architecture Summary:**
We've designed a [system type] that supports [scale] with [key quality attributes]. The architecture uses [key pattern/approach] to achieve [business objectives].

**Key Design Decisions:**
1. **[Decision 1]**: Chose [option] over [alternative] because [reasoning]
2. **[Decision 2]**: Implemented [pattern] to handle [requirement]
3. **[Decision 3]**: Selected [technology] for [specific use case]

**Trade-offs Made:**
1. **Consistency vs Performance**: Chose [consistency model] accepting [trade-off]
2. **Cost vs Reliability**: Opted for [approach] balancing [concerns]
3. **Complexity vs Maintainability**: Selected [solution] considering [factors]

**System Characteristics:**
- **Scalability**: Supports [X] users with [scaling strategy]
- **Reliability**: Achieves [availability target] through [mechanisms]
- **Performance**: Delivers [latency target] under [load conditions]
- **Maintainability**: [Deployment and operational considerations]
```

#### **Future Improvements:**
```
**C:** Areas for future enhancement:

**Short-term (3-6 months):**
- [Immediate optimization opportunity]
- [Performance improvement with clear ROI]
- [Security enhancement]

**Medium-term (6-12 months):**
- [Advanced feature addition]
- [Architecture evolution opportunity]
- [Scale preparation improvement]

**Long-term (1+ years):**
- [Next-generation technology adoption]
- [Major architectural refactoring]
- [Advanced analytics/ML integration]

**Risk Mitigation Priorities:**
1. [Highest risk area] → [Mitigation strategy]
2. [Second priority risk] → [Mitigation approach]
3. [Monitoring and alerting gaps] → [Observability improvements]
```

#### **Professional Closing:**
```
**C:** This design balances [key constraints] while providing [business value]. The modular architecture allows for [evolution capabilities] as requirements change.

I'm confident this solution addresses the core requirements while maintaining [quality attributes]. I'm happy to dive deeper into any specific area or discuss alternative approaches.

**I:** [Optional follow-up questions or feedback]
- "What if we had [different constraint]?"
- "How would you modify this for [different scale]?"
- "What's the biggest risk in this design?"

**C:** [Thoughtful responses demonstrating deep understanding]
```

---

## **SENIOR-LEVEL INTERVIEW GUIDELINES**

### **Key Differentiators for Senior/Tech Lead Level:**

#### **Technical Leadership Qualities to Demonstrate:**
- **Systems Thinking**: Consider entire ecosystem, not just individual components
- **Trade-off Analysis**: Explicitly discuss alternatives and decision rationale
- **Operational Excellence**: Include monitoring, deployment, and maintenance considerations
- **Risk Assessment**: Identify potential failure modes and mitigation strategies
- **Business Alignment**: Connect technical decisions to business objectives

#### **Advanced Topics to Cover:**
- **Capacity Planning**: Resource estimation and scaling math
- **Performance Engineering**: Bottleneck analysis and optimization strategies
- **Distributed Systems**: CAP theorem, consistency models, fault tolerance
- **Organizational Impact**: Team structure, Conway's Law, operational responsibilities
- **Technology Evolution**: Migration strategies, backward compatibility, technical debt

### **Common Interview Focus Areas:**

#### **For Staff/Principal Engineers:**
- System architecture and technology strategy
- Cross-team collaboration and technical influence
- Long-term technical vision and roadmap planning
- Complex trade-off analysis and risk assessment

#### **For Engineering Managers/Tech Leads:**
- Team scalability and development processes
- System reliability and operational excellence
- Technology choices impact on team productivity
- Balancing technical debt vs feature delivery

---

## **COMMON SYSTEM DESIGN PATTERNS REFERENCE**

### **Scalability Patterns:**
- **Load Balancing**: Round robin, least connections, consistent hashing
- **Caching**: Write-through, write-back, cache-aside, read-through
- **Database Scaling**: Read replicas, sharding, partitioning, federation
- **Asynchronous Processing**: Message queues, event streams, batch processing

### **Reliability Patterns:**
- **Circuit Breaker**: Prevent cascading failures
- **Bulkhead**: Isolate critical resources
- **Retry with Backoff**: Handle transient failures
- **Health Checks**: Enable auto-recovery

### **Data Patterns:**
- **CQRS**: Separate read/write models
- **Event Sourcing**: Audit trail and replay capability
- **Saga Pattern**: Distributed transaction management
- **Database per Service**: Service data isolation

### **Communication Patterns:**
- **API Gateway**: Single entry point and cross-cutting concerns
- **Service Mesh**: Service-to-service communication management
- **Event-Driven Architecture**: Loose coupling through events
- **Request-Response vs Fire-and-Forget**: Synchronous vs asynchronous patterns

---

## **SAMPLE PROBLEMS BY DIFFICULTY**

### **Senior Engineer Level:**
- **Distributed Cache System** (Redis/Memcached alternative)
- **Real-time Chat System** (Slack/Discord-like)
- **Content Delivery Network** (CDN design)
- **Search Engine** (Elasticsearch alternative)
- **Notification System** (Push notifications at scale)

### **Staff/Principal Level:**
- **Global Database System** (Distributed SQL database)
- **Service Mesh Architecture** (Istio/Linkerd alternative)
- **Real-time Analytics Platform** (Real-time data processing)
- **Multi-tenant SaaS Platform** (Complex isolation and scaling)
- **Payment Processing System** (Financial transaction handling)

---

## **LLM SUCCESS CRITERIA**

### **Must Demonstrate:**
- ✅ **Structured Approach**: Clear progression through all 4 phases
- ✅ **Technical Depth**: Appropriate detail for senior-level discussions
- ✅ **Business Acumen**: Connect technical decisions to business impact
- ✅ **Trade-off Awareness**: Explicit discussion of alternatives and decisions
- ✅ **Realistic Dialogue**: Natural conversation with interviewer interactions
- ✅ **Operational Mindset**: Include deployment, monitoring, and maintenance
- ✅ **Risk Assessment**: Identify potential issues and mitigation strategies

### **Avoid:**
- ❌ **Over-engineering**: Adding complexity without clear justification
- ❌ **Generic Solutions**: One-size-fits-all answers without context
- ❌ **Missing Trade-offs**: Not discussing alternatives or downsides
- ❌ **Unrealistic Assumptions**: Perfect world scenarios without constraints
- ❌ **Technology Bias**: Choosing familiar tech without proper evaluation
- ❌ **Ignoring Operations**: Focusing only on development without production concerns

---

*This template ensures LLM responses demonstrate senior-level system design thinking with proper interview flow, realistic dialogue, and comprehensive technical coverage appropriate for staff+ engineering roles.*
