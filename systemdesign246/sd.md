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

**Functional Requirements (Who, What, Whom, Where):**
1. **Who** are the actors using this system? (end users, admins, automated systems, API clients)
2. **What** core operations does the system need to support? (main features and capabilities)
3. **Whom/What** are these operations performed on? (data entities, resources, targets)
4. **Where** does this system operate? (geographic scope, deployment context)
5. Are there specific business rules or workflow constraints?
6. What integrations with external systems are required?

**Non-Functional Requirements (Performance, Monitoring, Failures, Security):**
7. **Performance & Tuning**: What are the expected load patterns and performance requirements?
8. **Monitoring & Alerting**: What metrics and alerts are critical for this system?
9. **Failure Scenarios**: What are the main failure modes and how should the system respond?
10. **Security**: What are the authentication, authorization, and data protection requirements?

**Follow-up based on answers:**
- "Given [X] load, what would be acceptable performance?"
- "What would be the business impact if [component] fails?"
- "What security threats are we most concerned about?"
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
```
For detailed design, we can focus on one or more of the following areas based on interviewer interest or question itself:
api design, database design, OO design;
caching strategy, security concerns and solutions, performance optimization, failover, monitoring and alerting, logging, etc.

```

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
