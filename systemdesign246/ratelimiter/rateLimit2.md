# Mock Interview: Tagging System Rate Limiter Design

**Question:** Designing a tagging system rate limiter involves controlling how frequently users (or systems) can perform tagging operations (e.g., adding tags to a resource) to prevent abuse and ensure fairness.

---

## Mock Interview Dialogue

**Interviewer (I):** Today we'll be designing a rate limiter for a tagging system. The goal is to control how frequently users or systems can perform tagging operations to prevent abuse and ensure fairness. You have 45 minutes. Please walk me through your approach.

**Candidate (C):** Thank you. This is an interesting problem that combines content management with fairness and abuse prevention. Let me use a Domain-Driven Design approach to structure my solution, as it will help us align technical decisions with business value.

I'll move through four phases: domain understanding, strategic design with bounded contexts, tactical design, and advanced considerations including scalability and operational concerns. Let me start with some clarifying questions to understand the business domain.

---

## Phase 1: Domain Understanding (5-7 minutes) - concepts and bounded contexts

**C:** Let's start by understanding the business domain. I have several questions:

1. What's the primary business value of tagging? Is it for search discovery, content organization, recommendations, or content moderation?
2. Who are the actors performing tagging operations? End users, content moderators, automated systems, or external API clients?
3. Are there different user tiers with varying quotas - like Free, Pro, Enterprise?
4. What constitutes abuse in this context? Spam tagging, coordinated manipulation, or automated scraping?
5. What scale are we designing for? Number of users, resources, and tagging operations per day?
6. Are there compliance requirements like audit trails or data residency?
7. What's our latency budget for tagging operations?

**I:** Good questions. Let me provide context:
- Tags are primarily used for search relevance and content discovery, driving user engagement
- Actors include regular users, power users, moderation tools, and API clients
- We have three tiers: Free (1000 tags/day), Pro (10,000 tags/day), Enterprise (unlimited with fair use)
- Abuse patterns include burst tagging spam, coordinated campaigns, and automated manipulation
- Scale: 100M users, 1B resources, 10B tagging operations per day
- We need audit trails for compliance and p95 latency under 50ms for tagging operations
- Multi-region deployment across 3 regions

**C:** Perfect. Let me define our ubiquitous language for this domain:

**Core Concepts:**
- **Tag**: A semantic label attached to a Resource for classification
- **Tagging Operation**: Action of adding, removing, or modifying tags on a resource
- **Rate Limit**: Policy defining maximum operations within a time window
- **Quota**: Tier-based allocation over longer periods (daily/monthly)
- **Burst Capacity**: Temporary allowance for legitimate usage spikes
- **Enforcement Scope**: USER, TENANT, RESOURCE_TYPE, GLOBAL levels
- **Trust Score**: Dynamic assessment influencing adaptive limits

**Business Problem Statement:** We need to protect platform quality and ensure fair usage while preserving legitimate user productivity and supporting our monetization model through tiered quotas.

---

## Phase 2: Strategic Design - Bounded Contexts, mapping and events (8-10 minutes)

**C:** Based on the domain analysis, I identify these natural bounded contexts:

### 1. **Content Management Context** (Core Domain)
- **Responsibility**: Managing resources and their tag associations
- **Key Aggregates**: Resource, Tag, TagAssociation  
- **Domain Events**: ResourceTagged, ResourceUntagged, TagCreated
- **Business Value**: Direct user engagement and content discovery

### 2. **Rate Limiting Context** (Supporting Domain)
- **Responsibility**: Enforcing usage policies and preventing abuse
- **Key Aggregates**: RateLimit, Quota, UsageWindow, AdaptiveOverlay
- **Domain Events**: UsageRecorded, RateLimitExceeded, QuotaDepleted
- **Business Value**: Platform protection and fairness

### 3. **User & Tier Management Context** (Supporting Domain)
- **Responsibility**: User identity, permissions, and tier management
- **Key Aggregates**: User, UserTier, Permission
- **Domain Events**: UserTierChanged, PermissionGranted
- **Business Value**: Monetization and access control

### 4. **Abuse Detection Context** (Supporting Domain)
- **Responsibility**: Behavioral analysis and adaptive response
- **Key Aggregates**: BehaviorPattern, AbuseSignal, TrustScore
- **Domain Events**: AbuseDetected, TrustScoreUpdated
- **Business Value**: Advanced threat protection

Let me draw the context map:

```
┌─────────────────┐    sync call    ┌──────────────────┐
│ Content Mgmt    │────────────────▶│ Rate Limiting    │
│ (Core)          │◀────────────────│ (Supporting)     │
└─────────────────┘    allow/deny   └──────────────────┘
                                              │
                                              │ events
                                              ▼
┌─────────────────┐                  ┌──────────────────┐
│ User & Tier     │                  │ Abuse Detection  │
│ (Supporting)    │─────events──────▶│ (Supporting)     │
└─────────────────┘                  └──────────────────┘
```

**I:** Why separate Rate Limiting from Content Management instead of embedding it?

**C:** Excellent question. Separation provides several benefits:
1. **Single Responsibility**: Rate limiting has different scaling characteristics and evolution needs
2. **Reusability**: The rate limiting context can be shared with other mutation operations
3. **Independent Evolution**: We can enhance algorithms and add ML-based adaptive features without impacting core tagging latency
4. **Team Ownership**: Enables dedicated teams per bounded context following Conway's Law

**Architecture Style Selection:**
- **Event-Driven Architecture** for loose coupling between contexts
- **CQRS** within Rate Limiting for optimized read/write paths  
- **Hexagonal Architecture** for algorithm pluggability
- **Multi-region active-active** with eventual consistency for soft limits

---

## Phase 3: Tactical Design: focus on the main Context related to the interview (10-12 minutes)
- Rate Limiting Context Deep Dive 

**C:** Let me zoom into the Rate Limiting bounded context and model the core aggregates:

### Core Aggregates and Invariants:

#### 1. **RateLimit Aggregate**
```
RateLimit {
  - limitId: LimitId
  - scope: LimitScope (USER, TENANT, RESOURCE_TYPE, GLOBAL)
  - window: TimeWindow (SECOND, MINUTE, HOUR, DAY)
  - maxOperations: Integer
  - burstCapacity: Integer
  - replenishRate: Integer
  
  Invariants:
  - maxOperations > 0
  - burstCapacity <= maxOperations * 2
  - replenishRate > 0
  
  + checkLimit(entityId, operation): Boolean
  + recordUsage(entityId, operation): UsageResult
  + adjustLimit(newLimit): void
}
```

#### 2. **Quota Aggregate**
```
Quota {
  - quotaId: QuotaId
  - entityId: EntityId
  - quotaType: QuotaType
  - allocated: Integer
  - consumed: Integer
  - resetTime: Timestamp
  - tier: UserTier
  
  Invariants:
  - consumed <= allocated (unless burst allowed)
  - resetTime aligned with quota window
  
  + consume(amount): ConsumeResult
  + replenish(): void
  + upgrade(newTier): void
}
```

#### 3. **UsageWindow Aggregate**
```
UsageWindow {
  - windowId: WindowId
  - entityId: EntityId
  - windowStart: Timestamp
  - windowEnd: Timestamp
  - operationCounts: Map<OperationType, Integer>
  - isActive: Boolean
  
  Invariants:
  - windowEnd > windowStart
  - operationCounts >= 0
  
  + recordOperation(operation): void
  + isWithinLimit(operation, limit): Boolean
  + expire(): void
}
```

### Domain Events for Inter-Context Communication:
- `TaggingAttempted` → triggers rate limit check
- `RateLimitExceeded` → blocks operation + triggers analytics
- `QuotaRefreshed` → enables continued operations  
- `AbusePatternDetected` → triggers adaptive limit adjustments

**Command Flow for Add Tag Operation:**
1. Content Management receives tag request
2. Calls Rate Limiting: `CheckAndConsume(userId, tenantId, ADD_TAG, 1)`
3. Rate Limiting evaluates composite policies: user → tenant → resource_type → global
4. On success: decrements counters, emits `UsageRecorded` asynchronously
5. On failure: returns deny with retry-after, emits `RateLimitExceeded`

**I:** How do you handle race conditions with high QPS operations?

**C:** Great question. I use atomic operations:
- **Redis Lua scripts** for atomic counter operations
- **Pipeline atomic operations** for multi-scope checks (user + tenant + global)
- **Hot key sharding** with consistent hashing for celebrity users
- **Optimistic concurrency control** with retry logic for extreme contention

For multi-scope enforcement, if any scope exceeds its limit, the entire operation is denied atomically.

---

## Phase 4: Technical Implementation & Algorithms (10-12 minutes)

**C:** Now let me detail the algorithms and data structures:

### Rate Limiting Algorithms by Use Case:

| Scope | Algorithm | Configuration | Business Rationale |
|-------|-----------|---------------|-------------------|
| User short-term | Token Bucket | 100 tokens/hour, burst 20 | Smooth bursts for legitimate users |
| Tenant daily quota | Fixed Window Counter | Free: 1000/day, Pro: 10000/day | Simple billing alignment |
| Resource-type spikes | Sliding Window Log | Track last 100 operations | Precise abuse detection |
| Global protection | Leaky Bucket | 1000 ops/second globally | Protect downstream systems |
| Adaptive overlay | Dynamic Multiplier | 0.5x - 2.0x base limit | Risk-based adjustment |

### Data Storage Strategy:

#### 1. **Redis Cluster** (Hot Path - Sub-10ms)
```
Key Patterns:
rate_limit:{region}:{userId}:{policyId} → {tokens, lastRefill, timestamp}
quota:{tenantId}:{date}:{type} → {consumed, allocated} (TTL: 25 hours)
window:{userId}:{opType}:sliding → [timestamp1, timestamp2, ...] (TTL: window + grace)
overlay:{entityId} → {multiplier, expiresAt, reason}
global:{region}:sec:{epochSec} → counter (TTL: 3600)
```

#### 2. **Event Store** (Durability & Analytics)
```
Events: usage_recorded, limit_exceeded, quota_depleted
Retention: 30 days detailed, 1 year aggregated
Schema: {timestamp, entityId, operation, result, metadata}
```

#### 3. **Configuration Store** (PostgreSQL)
```
Tables: rate_limit_policies, user_quotas, violation_audit
Consistency: ACID for policy changes and compliance logs
```

### API Design:

**Public Tagging API:**
```http
POST /api/v1/resources/{resourceId}/tags
Headers:
  X-User-Id: {userId}
  X-Tenant-Id: {tenantId}
  Authorization: Bearer {token}
Body:
  {"add": ["security", "urgent"], "remove": ["draft"]}

Response (Success):
  200 OK
  X-RateLimit-Remaining-User: 47
  X-RateLimit-Remaining-Tenant: 8952
  X-RateLimit-Reset: 1609459200

Response (Rate Limited):
  429 Too Many Requests
  Retry-After: 3600
  X-RateLimit-Policy: user_hourly_limit
```

**Internal Rate Limit Check API:**
```http
POST /internal/ratelimit/check-and-consume
Request:
{
  "scopes": [
    {"type": "USER", "id": "user123"},
    {"type": "TENANT", "id": "tenant456"}
  ],
  "operation": "ADD_TAG",
  "cost": 1
}

Response:
{
  "decision": "ALLOW",
  "remaining": {"user": 47, "tenant": 8952},
  "retryAfter": null
}
```

**I:** How do you ensure multi-region consistency without high latency?

**C:** I use a **region-sliced budget approach**:

1. **Per-region enforcement** with bounded overshoot
2. **Global budget distribution**: Split global limits (e.g., 1M/sec → 3×350K per region)
3. **Async reconciliation**: Hourly rebalancing via control plane events
4. **Acceptable overshoot**: <5% due to eventual consistency
5. **Critical abuse triggers**: Use strongly consistent path for extreme cases

This provides sub-10ms local decisions while keeping global drift bounded.

---

## Phase 5: Advanced Considerations (8-10 minutes)

### Evolution and Migration Strategy

**Strangler Fig Pattern Implementation:**
```
Phase 1: Mirror Mode - Log decisions, don't enforce (2 weeks)
Phase 2: Shadow Mode - Compute denies, measure impact (1 week)  
Phase 3: Canary Enforcement - Enable for 5% of Free tier users (1 week)
Phase 4: Gradual Rollout - 25%, 50%, 100% of each tier (3 weeks)
Phase 5: Advanced Features - Adaptive overlays, ML scoring (ongoing)
```

**Schema Evolution:**
- Rate limit policies stored as versioned JSON configurations
- Feature flags for algorithm switching
- Backward-compatible event schema with version fields

### Operational Excellence

**Multi-Level Monitoring:**
```
Business Metrics:
- Tag creation success rate by user tier (target: >99.5%)
- False positive rate - legitimate users blocked (target: <0.1%)
- Revenue impact from quota upgrades
- Abuse detection accuracy (precision/recall)

Technical Metrics:
- Rate limiter latency p95/p99 (target: <10ms/<20ms)
- Cache hit ratio (target: >98%)
- Multi-region drift percentage (target: <2%)
- Algorithm effectiveness per scope

Operational Metrics:
- Policy deployment success rate
- Alert noise ratio
- Time to detection for new abuse patterns
```

**Chaos Engineering Scenarios:**
- Redis cluster partial failure during peak tagging
- Network partitions between regions
- Quota service degradation
- Kafka event bus lag
- Clock skew between services

### Security and Compliance

**Zero-Trust Architecture:**
- mTLS for all internal service communication
- JWT tokens with short expiration for API access
- Rate limits applied even to internal services
- Audit trails for all policy changes

**GDPR Compliance:**
- User data deletion pipeline with tombstone events
- Data residency - rate limiting data stored in appropriate regions
- Audit logs retained per regulatory requirements
- Privacy-preserving analytics with data aggregation

### Adaptive Abuse Prevention

**Behavioral Analysis Patterns:**
```
Suspicious Behaviors:
- Rapid sequential tagging across unrelated resources
- Coordinated tagging campaigns (multiple users, same tags)
- Off-hours burst activity inconsistent with user timezone
- Tag content analysis for spam/inappropriate terms

Response Strategy:
- Temporary stricter limits (adaptive overlay: 0.5x normal)
- Human moderator escalation for severe cases
- Account flagging for manual review
- Real-time trust score updates
```

**Machine Learning Integration (Future):**
```
Features: Time patterns, tag diversity, content relevance, user history
Model: Legitimate user probability score (0.0 - 1.0)
Action: Dynamic limit adjustment based on trust score
Feedback: Appeal outcomes used for model training
```

---

## Phase 6: Trade-offs and Design Decisions

**C:** Let me explicitly state the key trade-offs and justifications:

### 1. **Distributed vs. Centralized Rate Limiting**
**Decision:** Distributed with eventual consistency
- **Business Driver:** Lower latency improves user experience and engagement
- **Technical Driver:** Horizontal scalability for global deployment  
- **Trade-off:** Slightly less accurate limits (≤5% overshoot) but better performance
- **Evolution Path:** Can add centralized layer for critical abuse prevention

### 2. **Multiple Algorithm Strategy**
**Decision:** Different algorithms optimized for different scopes
- **Business Driver:** Reduces false positives/negatives per use case
- **Technical Driver:** Each algorithm has specific strengths
- **Trade-off:** Increased operational complexity but better effectiveness
- **Evolution Path:** ML-driven algorithm selection based on usage patterns

### 3. **Event-Driven vs. Synchronous Architecture**
**Decision:** Hybrid - synchronous for enforcement, asynchronous for analytics
- **Business Driver:** Real-time protection with scalable insights
- **Technical Driver:** Decouples hot path from analytics processing
- **Trade-off:** Eventually consistent analytics but immediate protection
- **Evolution Path:** Streaming analytics for real-time adaptive responses

### 4. **Redis vs. Database for Hot Path**
**Decision:** Redis with database backup
- **Business Driver:** Meet latency SLAs for user experience
- **Technical Driver:** In-memory performance for high QPS
- **Trade-off:** Additional operational complexity but required performance
- **Evolution Path:** Distributed cache with automatic failover

**I:** What are the biggest risks you'd monitor after deployment?

**C:** Top risks and mitigations:

1. **Hot Key Contention** (Celebrity users hitting single Redis keys)
   - Mitigation: Consistent hashing with key sharding
   - Monitoring: Key access pattern distribution

2. **Policy Misconfiguration** (Accidentally blocking legitimate users)
   - Mitigation: Shadow mode + canary deployment + automatic rollback
   - Monitoring: False positive rate spikes

3. **Regional Drift** (Fairness complaints due to inconsistent limits)
   - Mitigation: Bounded overshoot + reconciliation SLAs
   - Monitoring: Cross-region quota consumption variance

4. **Abuse Model Bias** (Disproportionate impact on certain user groups)
   - Mitigation: Regular fairness audits + diverse training data
   - Monitoring: Denial rate by user demographic segments

5. **Cascading Failures** (Rate limiter outage affecting core tagging)
   - Mitigation: Circuit breakers + degraded mode operation
   - Monitoring: Dependency health + fallback activation rates

---

## Phase 7: Team Topology and Ownership

**C:** From a team topology standpoint, applying Conway's Law:

- **Stream-Aligned Team**: Rate Limiting Context (5-7 engineers)
  - Owns policies, algorithms, and adaptive features
  - On-call for rate limiting performance and accuracy

- **Platform Team**: Shared Redis/Kafka infrastructure
  - Provides reliable, scalable data layer
  - Manages cross-region synchronization

- **Enabling Team**: ML/Data Science for adaptive scoring
  - Develops behavioral models and trust scoring
  - Provides tools and guidance for abuse detection

**Governance:**
- Architecture Decision Records for major policy changes
- Cross-team design reviews for integration points
- Quarterly fairness audits with business stakeholders

---

## Interview Closing Summary

**C:** To summarize, I've designed this rate limiting system using DDD principles with four main bounded contexts: Content Management as the core domain, and Rate Limiting, User Management, and Abuse Detection as supporting domains.

**Key Design Decisions:**
1. **Multi-scope composite evaluation** (user, tenant, resource-type, global) with different algorithms per scope
2. **Event-driven architecture** with CQRS for performance and scalability
3. **Region-sliced budgets** for global limits to balance consistency and latency
4. **Adaptive overlays** for dynamic abuse response
5. **Strangler Fig migration** for safe deployment

The architecture supports our business requirements for fairness, abuse prevention, and monetization while maintaining technical requirements for low latency, high availability, and horizontal scalability.

**Next Steps:**
1. **Stakeholder validation** with product and business teams on policies
2. **Architecture Decision Records** documenting key trade-offs
3. **Event Storming sessions** to refine domain boundaries
4. **Proof of concept** for token bucket performance validation
5. **Security review** for compliance requirements

**I:** Excellent work. Your DDD approach really helped structure the solution around business value. Any final thoughts?

**C:** Thank you. The key insight is that rate limiting isn't just a technical problem - it's fundamentally about balancing business objectives: user experience, platform protection, and monetization. The DDD approach ensured we designed a system that serves these business needs while maintaining the technical qualities required for scale.

The bounded context separation also positions us well for future evolution - whether that's adding ML-based adaptive features, supporting new business models, or integrating with other platform services.

---

*This interview demonstrated the structured DDD-first approach from systemdesign.md, emphasizing business alignment, explicit trade-offs, and evolutionary architecture thinking that distinguishes senior-level candidates.*
