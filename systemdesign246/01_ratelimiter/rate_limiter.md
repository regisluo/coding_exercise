# System Design Interview: Tagging System Rate Limiter

**Question:** Designing a tagging system rate limiter involves controlling how frequently users (or systems) can perform tagging operations (e.g., adding tags to a resource) to prevent abuse and ensure fairness.

---

## Mock Interview Dialogue

**Interviewer (I):** Today we're designing a rate limiter for a tagging system. The goal is to control how frequently users or systems can perform tagging operations to prevent abuse and ensure fairness. You have 45 minutes. Please walk me through your approach.

**Candidate (C):** Thank you for the interesting problem. I'll approach this systematically using a four-phase structure: understanding the problem and establishing design scope, high-level design with typical use cases, detailed design focusing on key components, and finally wrap-up with trade-offs and future improvements. Let me start by clarifying the requirements.

---

## **PHASE 1: UNDERSTAND THE PROBLEM AND REQUIREMENTS (3-5 minutes)**

**C:** Let me start by understanding the core requirements and constraints:

**Functional Requirements (Who, What, Whom, Where):**
1. **Who** are the actors using this rate limiting system? (end users, power users, automated systems, moderators, API clients)
2. **What** tagging operations need rate limiting? (add tags, remove tags, bulk operations, tag modifications)
3. **Whom/What** are these operations performed on? (posts, files, products, documents - what resources get tagged?)
4. **Where** does this system operate? (single region, multi-region, edge locations)
5. Are there different user tiers with varying limits? (free vs paid users)
6. What specific business rules define abuse? (spam patterns, bulk operations, coordinated attacks)

**Non-Functional Requirements (Performance, Monitoring, Failures, Security):**
7. **Performance & Tuning**: What are the expected load patterns and latency requirements for tagging operations?
8. **Monitoring & Alerting**: What metrics are critical - rate limit violations, quota usage, false positives?
9. **Failure Scenarios**: What happens when rate limiter fails? How should the system degrade gracefully?
10. **Security**: What are the authentication requirements and how do we prevent rate limit bypassing?

**I:** Good questions. Let me provide context:
- **Who**: Regular users, power users, content moderators, automated content processing systems, and external API clients
- **What**: Add single tags, remove tags, bulk tag operations (up to 10 tags at once), and tag modifications
- **Whom**: Blog posts, images, documents, products in an e-commerce platform - various content types that need categorization
- **Where**: Multi-region deployment (3 regions: US, EU, APAC) with users distributed globally
- We have three tiers: Free (100 operations/hour), Pro (1000/hour), Enterprise (10000/hour)
- Abuse patterns: Spam tagging across unrelated content, coordinated manipulation campaigns, automated scraping for competitive intelligence
- **Performance**: Users expect responsive tagging similar to social media platforms
- **Monitoring**: Need to track quota utilization, violation rates, and false positive rates that block legitimate users
- **Failures**: Rate limiter outage shouldn't completely block legitimate users - need graceful degradation
- **Security**: JWT-based authentication, need to prevent API key sharing and rate limit circumvention

**C:** Perfect. Based on your context, let me establish some performance and operational targets:

**Performance Analysis:**
- For responsive user experience with 1B operations/day, targeting sub-100ms total tagging latency
- Rate limiting check should add minimal overhead (target: <10ms)
- Need to handle 35K QPS peak load across all regions

**Operational Requirements:**
- Monitor quota utilization rates and alert on unusual patterns
- Track false positive rates (legitimate users blocked) - target <0.1%
- Graceful degradation: fail-open with logging when rate limiter unavailable

Let me establish the design scope:

**In Scope:**
- Rate limiting for add/remove/bulk tag operations
- Multi-tier user limits (Free/Pro/Enterprise)
- Per-user, per-tenant, and global rate limiting
- Real-time enforcement with <50ms latency impact
- Audit logging for compliance

**Out of Scope (for this interview):**
- Tag recommendation algorithms
- Content moderation beyond rate limiting
- Complex analytics and reporting features

**Key Assumptions:**
- Peak load is 3x average (targeting 3000 QPS peak)
- 80/20 rule: 20% of users generate 80% of operations
- Global distribution with acceptable eventual consistency
- Focus on preventing abuse while maintaining user experience

### **Simple Back-of-Envelope Calculations:**

**C:** Let me do some quick capacity estimation:

**Scale Estimates:**
- Daily Active Users: 1M (10% of total users)
- Average operations per active user: 1000 operations/day
- Total daily operations: 1B operations/day
- Average QPS: 1B / (24 * 3600) ≈ 11,600 QPS
- Peak QPS: 11,600 * 3 ≈ 35,000 QPS
- Storage per operation (metadata): ~100 bytes
- Daily storage: 1B * 100 bytes = 100GB/day
- Storage (1 year): 100GB * 365 ≈ 36TB

**Key Numbers:**
- Rate limiter should handle 35K QPS peak load
- Sub-10ms latency budget for rate limit checks
- Need to track ~10M user quotas concurrently

---

## **PHASE 2: HIGH-LEVEL DESIGN (10-15 minutes)**

**C:** Let me propose a high-level architecture:

**System Architecture Diagram:**
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Client    │────│Load Balancer│────│  API Gateway│
│ (Mobile/Web)│    │             │    │  (Auth &    │
└─────────────┘    └─────────────┘    │ Routing)    │
                                      └─────────────┘
                                              │
                                              ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Rate Limiter│────│ Tagging     │────│  Content    │
│  Service    │    │  Service    │    │  Service    │
│  (Redis)    │    │             │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
        │                  │                  │
        ▼                  ▼                  ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Config DB   │    │ Tag         │    │ Resource    │
│ (Policies)  │    │ Database    │    │ Database    │
└─────────────┘    └─────────────┘    └─────────────┘
```

**Core Components:**
1. **API Gateway**: Authentication, request routing, and initial validation
2. **Rate Limiter Service**: Centralized rate limiting with Redis backend
3. **Tagging Service**: Core business logic for tag operations
4. **Content Service**: Manages resources that can be tagged
5. **Redis Cluster**: High-performance storage for rate limiting counters
6. **Config Database**: Stores rate limiting policies and user tier information

### **API Design:**

**C:** Here are the key APIs:

**Core Endpoints:**
```
// Tagging operations
POST /api/v1/resources/{resourceId}/tags
PUT  /api/v1/resources/{resourceId}/tags
DELETE /api/v1/resources/{resourceId}/tags/{tagId}
POST /api/v1/resources/{resourceId}/tags/bulk

// Rate limiting check (internal)
POST /internal/ratelimit/check
{
  "user_id": "user123",
  "operation": "ADD_TAG",
  "resource_type": "POST",
  "cost": 1
}

Response: 200 OK
{
  "allowed": true,
  "remaining": 47,
  "reset_time": 1696680000,
  "retry_after": null
}
```

**Example request/response:**
```
POST /api/v1/resources/post123/tags
Headers:
  Authorization: Bearer <jwt_token>
  X-User-Tier: PRO

{
  "tags": ["urgent", "review", "frontend"],
  "operation": "add"
}

Response: 201 Created
{
  "resource_id": "post123",
  "tags_added": ["urgent", "review", "frontend"],
  "operation_count": 3,
  "remaining_quota": 997
}

Response: 429 Too Many Requests (if rate limited)
{
  "error": "Rate limit exceeded",
  "retry_after": 3600,
  "current_usage": 1000,
  "limit": 1000,
  "reset_time": 1696680000
}
```

### **Typical Use Case Walkthrough:**

**C:** Let me walk through a typical user workflow:

**Scenario**: A Pro user adds tags to a blog post

**Step-by-step flow:**
1. User clicks "Add Tags" → Client sends POST /api/v1/resources/post123/tags
2. Load balancer routes to available API gateway instance
3. API Gateway validates JWT token and extracts user_id and tier
4. API Gateway calls Rate Limiter Service with user context
5. Rate Limiter checks Redis for current usage against Pro tier limits
6. If allowed, decrements quota and returns remaining count
7. Request proceeds to Tagging Service for business logic
8. Tagging Service validates tags and updates Tag Database
9. Success response with updated quota information returned to client
10. Client updates UI showing remaining quota

**Performance Characteristics:**
- Expected latency: 30-40ms for tag operation (including 5ms rate limit check)
- Cache hit ratio: 95% for rate limiting data in Redis
- Database queries: 1-2 per tagging request

**I:** What happens if the Rate Limiter Service fails?

**C:** Good question. For Rate Limiter Service failure, we have several mitigation strategies:
1. **Circuit Breaker**: Detect failures and fail-open temporarily with logging
2. **Fallback Limits**: Use conservative local limits when remote service is unavailable
3. **Graceful Degradation**: Continue processing with warning logs and async reconciliation
4. **Health Checks**: Automatic failover to backup rate limiter instances

**I:** How do you handle different rate limiting scopes simultaneously?

**C:** For multiple scopes, I would implement a hierarchical check system:
1. Check user-level limits first (most restrictive)
2. Check tenant/organization limits (for enterprise customers)
3. Check global system limits (DDoS protection)
4. All checks must pass for the operation to proceed
5. Use atomic Redis operations to ensure consistency across checks

---

## **PHASE 3: DETAILED DESIGN (10-20 minutes)**

**C:** Let me focus on the performance characteristics and rate limiting algorithms:

### **Performance & Scalability Focus**

**Performance Analysis:**
- **Bottleneck Identification**: Redis cluster under 35K QPS peak load
- **Capacity Planning**: Current design supports 50K QPS with 3-node Redis cluster
- **Scaling Triggers**: Auto-scale when Redis CPU > 70% or memory > 80%

**Rate Limiting Algorithms by Use Case:**

1. **Token Bucket Algorithm** (Primary choice for user quotas):
```
Algorithm: Bucket capacity = hourly limit, refill rate = limit/3600 tokens per second
Benefits: Allows bursts while maintaining average rate
Implementation: Redis with TTL-based token buckets

Redis Key Pattern: rl:tb:{user_id}:{hour_epoch}
Data: {tokens_remaining: 45, last_refill: timestamp}
```

2. **Fixed Window Counter** (For tier-based daily quotas):
```
Algorithm: Reset counters at fixed intervals (daily/hourly)
Benefits: Simple to implement and understand
Implementation: Redis counters with daily TTL

Redis Key Pattern: rl:daily:{user_id}:{date}
Data: {count: 150, limit: 1000, tier: "PRO"}
```

3. **Sliding Window Log** (For abuse detection):
```
Algorithm: Track individual request timestamps
Benefits: Most accurate for detecting burst patterns
Implementation: Redis sorted sets with timestamp scoring

Redis Key Pattern: rl:sliding:{user_id}
Data: Sorted set of timestamps within sliding window
```

**Multi-Level Caching Strategy:**
1. **L1 Cache (Application Level)**: 
   - In-memory cache for rate limit policies (1-minute TTL)
   - Reduces Redis load for policy lookups
   
2. **L2 Cache (Redis Cluster)**:
   - Hot path for rate limiting counters
   - Optimized data structures for atomic operations
   
3. **L3 Storage (PostgreSQL)**:
   - Persistent storage for policies and audit logs
   - Batch writes to reduce database load

**Database Optimization:**
- Connection pooling: 20 connections per application instance
- Read replicas for policy reads (eventual consistency acceptable)
- Batch processing for audit log writes (every 10 seconds)

**Redis Cluster Design:**
```
Redis Cluster Configuration:
- 3 master nodes with 3 replicas (6 total nodes)
- Consistent hashing for key distribution
- Pipeline operations for multi-scope checks
- Lua scripts for atomic rate limit operations

Example Lua Script for Token Bucket:
local key = KEYS[1]
local capacity = tonumber(ARGV[1])
local tokens = tonumber(ARGV[2])
local interval = tonumber(ARGV[3])
local current_time = tonumber(ARGV[4])

local bucket = redis.call('HMGET', key, 'tokens', 'last_refill')
local available_tokens = tonumber(bucket[1]) or capacity
local last_refill = tonumber(bucket[2]) or current_time

-- Calculate tokens to add based on time elapsed
local elapsed = current_time - last_refill
local tokens_to_add = math.floor(elapsed * capacity / interval)
available_tokens = math.min(capacity, available_tokens + tokens_to_add)

if available_tokens >= tokens then
    available_tokens = available_tokens - tokens
    redis.call('HMSET', key, 'tokens', available_tokens, 'last_refill', current_time)
    redis.call('EXPIRE', key, interval)
    return {1, available_tokens}  -- Allowed
else
    return {0, available_tokens}  -- Denied
end
```

**Load Testing Results** (estimated):
- Current capacity: 50K QPS at 8ms p95 latency for rate limit checks
- Breaking point: 75K QPS before Redis cluster saturation
- Scaling headroom: 2x with current architecture before major redesign

**I:** How do you handle the distributed nature across 3 regions?

**C:** For multi-region deployment, I'd implement a region-aware strategy:

1. **Regional Quotas**: Distribute daily quotas across regions (e.g., 40% US, 35% EU, 25% APAC)
2. **Local Enforcement**: Each region enforces its allocated portion independently
3. **Async Reconciliation**: Hourly sync to rebalance unused quotas between regions
4. **Circuit Breaker**: Fall back to local-only limits if cross-region communication fails
5. **Eventual Consistency**: Accept <5% quota overage due to regional delays

### **Service Architecture Focus**

**C:** Let me detail the service decomposition:

**Microservices Breakdown:**
1. **Rate Limiter Service**: 
   - Domain: Usage enforcement and quota management
   - APIs: /check, /consume, /status
   - Data: Rate limiting counters, user quotas

2. **Policy Service**:
   - Domain: Rate limiting policies and user tier management
   - APIs: /policies, /tiers, /limits
   - Data: Tier definitions, custom policies

3. **Audit Service**:
   - Domain: Compliance logging and violation tracking
   - APIs: /audit-logs, /violations, /reports
   - Data: Operation logs, violation records

**Service Communication:**
- **Synchronous**: gRPC for rate limit checks (low latency requirement)
- **Asynchronous**: Kafka for audit logging and policy updates
- **Data Consistency**: Eventually consistent across regions, strongly consistent within region

**Service Discovery & Resilience:**
- Kubernetes service discovery for internal communication
- Circuit breakers for external dependency failures
- Exponential backoff retry (max 3 attempts, 100ms base delay)
- Bulkhead pattern: Separate thread pools for different user tiers

---

## **PHASE 4: WRAP-UP AND SUMMARY (3-5 minutes)**

**C:** Let me summarize the key aspects of this design:

**Architecture Summary:**
We've designed a distributed rate limiting system that supports 1B daily operations with multi-tier user quotas. The architecture uses a combination of token bucket and fixed window algorithms to achieve both burst handling and fair usage enforcement.

**Key Design Decisions:**
1. **Token Bucket + Fixed Window**: Chose hybrid approach for burst allowance with daily quotas
2. **Redis Cluster**: Implemented for sub-10ms latency requirements with high availability
3. **Regional Distribution**: Selected regional quota allocation over global synchronization for better performance

**Trade-offs Made:**
1. **Consistency vs Performance**: Chose eventual consistency across regions accepting <5% overage for better latency
2. **Cost vs Reliability**: Opted for Redis cluster (higher cost) over single instance for availability requirements
3. **Complexity vs Accuracy**: Selected multiple algorithms over single approach for better user experience

**System Characteristics:**
- **Scalability**: Supports 10M users with 35K QPS peak load
- **Reliability**: Achieves 99.9% availability through redundancy and failover
- **Performance**: Delivers <50ms total latency including <10ms rate limiting overhead
- **Maintainability**: Microservices architecture enables independent scaling and deployment

### **Future Improvements:**

**C:** Areas for future enhancement:

**Short-term (3-6 months):**
- Machine learning for dynamic abuse detection and adaptive limits
- Real-time analytics dashboard for quota utilization monitoring
- API rate limiting SDK for easier client integration

**Medium-term (6-12 months):**
- Advanced quota sharing within organizations/teams
- Predictive scaling based on usage patterns
- Integration with content moderation for context-aware limiting

**Long-term (1+ years):**
- Blockchain-based quota marketplace for enterprise customers
- Edge-computing rate limiting for ultra-low latency
- Advanced behavioral analysis for sophisticated abuse prevention

**Risk Mitigation Priorities:**
1. **Redis Cluster Failure** → Implement cross-region backup clusters with automatic failover
2. **Policy Configuration Errors** → Add policy validation and gradual rollout mechanisms
3. **DDoS Attacks** → Deploy additional global rate limiting at CDN/infrastructure level

**C:** This design balances performance requirements with fairness enforcement while providing scalability for future growth. The modular architecture allows for evolution as abuse patterns change and new requirements emerge.

I'm confident this solution addresses the core requirements while maintaining sub-50ms latency and 99.9% availability. I'm happy to dive deeper into any specific area such as the Redis Lua scripts, multi-region synchronization, or abuse detection algorithms.

**I:** What's the biggest risk in this design?

**C:** The biggest risk is Redis cluster becoming a single point of failure despite replication. If we lose the entire Redis cluster, we lose all rate limiting state. I'd mitigate this by:

1. **Cross-region Redis replication** for disaster recovery
2. **Graceful degradation mode** with conservative local limits when Redis is unavailable
3. **State reconstruction** from audit logs for rapid recovery
4. **Regular backup strategies** for Redis data with point-in-time recovery
5. **Chaos engineering** to regularly test failure scenarios and validate our failover mechanisms

The key is ensuring that a rate limiter failure doesn't completely block legitimate users while still preventing abuse through fallback mechanisms.
