# System Design Interview: Distributed Task Queue System

**Question:** Design a distributed task queue system that can handle high throughput and low latency for processing tasks. Discuss the considerations and components that would be necessary to implement such a system effectively.

---

## Mock Interview Dialogue

**Interviewer (I):** Today we'll be designing a distributed task queue system. The system needs to handle high throughput and low latency for processing tasks. Can you walk me through your approach to solving this problem?

**Candidate (C):** Thank you for the interesting problem. I'll approach this systematically using a four-phase structure: understanding the problem and establishing design scope, high-level design with typical use cases, detailed design focusing on key components, and finally wrap-up with trade-offs and future improvements. Let me start by clarifying the requirements.

---

## Phase 1: Understand the Problem and Requirements (3-5 minutes)

**C:** Let me start by understanding the core requirements and constraints:

**Functional Requirements (Who, What, Whom, Where):**

**I:** Good approach. Let's clarify the requirements.

**C:** 
1. **Who** are the actors using this system? I assume we have task producers (applications, services, users) who submit tasks, and consumers (worker nodes) that process them. Also administrators who monitor and manage the system.

2. **What** core operations does the system need to support? 
   - Task submission and queuing
   - Task scheduling and execution
   - Task status tracking and monitoring
   - Task retry mechanisms
   - Task prioritization

3. **Whom/What** are these operations performed on?
   - Tasks with different types (email sending, image processing, data analysis)
   - Task metadata (priority, retry count, timestamps)

4. **Where** does this system operate? Is this a global system or region-specific?

**I:** Let's assume it's a global system supporting multiple regions. What about specific business requirements?

**C:** Great. Some additional clarification questions:
- Do tasks have different priorities? Should high-priority tasks be processed first?
- What's the expected task failure rate and retry policy?
- Do we need scheduled tasks (delayed execution)?
- Should tasks support dependencies (task A must complete before task B)?
- What's the required task durability guarantee?

**I:** Yes to priorities and retries. Let's keep it simple - no dependencies or complex scheduling for now. Tasks should be durable and not lost.

**Non-Functional Requirements (Performance, Monitoring, Failures, Security):**

**C:** 
5. **Performance & Tuning**: What are the expected load patterns?
   - Expected QPS for task submission?
   - Task processing latency requirements?
   - Peak vs average load ratios?

**I:** Let's target 100K tasks per second submission rate, with P99 processing latency under 100ms for high-priority tasks.

**C:** 
6. **Monitoring & Alerting**: We'll need metrics for queue depths, processing rates, worker health, and latency percentiles.

7. **Failure Scenarios**: Main concerns are worker node failures, queue service failures, and network partitions.

8. **Security**: Authentication for task submission, authorization for different task types, and encryption for sensitive task data.

**Simple Back-of-Envelope Calculations:**

**C:** Let me do some quick capacity estimation:

**Scale Estimates:**
- Task submission: 100K/second = 8.64B tasks/day
- Assuming average task size of 1KB: 8.64TB/day storage
- With 30-day retention: ~260TB total storage
- Peak load (3x average): 300K/second submission rate
- Worker capacity: If average processing time is 10ms, each worker handles 100 tasks/second
- For 100K TPS, we need ~1000 active workers

---

## Phase 2: High-Level Design (10-15 minutes)

**C:** Let me propose a high-level architecture:

**System Architecture Diagram:**

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Producers  │────│Load Balancer│────│ API Gateway │
│(Apps/Users) │    │             │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
                                              │
                                              ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Cache     │────│Task Manager │────│Priority     │
│  (Redis)    │    │  Service    │    │Queues       │
└─────────────┘    └─────────────┘    └─────────────┘
                                              │
                                              ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Database   │────│Worker Pool  │────│Result Store │
│ (Metadata)  │    │Manager      │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
```

**Core Components:**
1. **Load Balancer**: Distributes incoming requests using round-robin with health checks
2. **API Gateway**: Rate limiting, authentication, request routing
3. **Task Manager Service**: Core business logic for task lifecycle management
4. **Priority Queues**: Multiple queues (high, medium, low priority) using message brokers
5. **Worker Pool Manager**: Auto-scaling worker management with health monitoring
6. **Database**: Task metadata, worker status, and system configuration
7. **Cache**: Fast access to frequently accessed task status and worker information

**I:** How do you handle the message queues? What technology would you use?

**C:** For the priority queues, I'd use Apache Kafka or Amazon SQS with multiple topics/queues for different priorities. Kafka provides high throughput and durability, while SQS offers managed simplicity.

**Typical Use Case Walkthrough:**

**C:** Let me walk through a typical task submission and processing workflow:

**Scenario**: A web application submits an image processing task

**Step-by-step flow:**
1. Producer submits task → API Gateway receives POST /tasks request
2. Load balancer routes to available Task Manager instance
3. Task Manager validates task, assigns priority, generates unique task ID
4. Task metadata stored in database, task payload sent to appropriate priority queue
5. Worker Pool Manager assigns available worker from the pool
6. Worker polls queue, receives task, updates status to "processing"
7. Worker executes task (image processing), updates progress periodically
8. On completion, worker updates task status and stores result
9. Optional notification sent to producer about completion
10. Worker becomes available for next task

**Performance Characteristics:**
- Expected latency: <50ms for task submission, <100ms P99 for high-priority processing
- Cache hit ratio: 90% for task status queries
- Database queries: 2-3 per task (create, update status, completion)

**I:** What happens if a worker crashes during task processing?

**C:** Good question. For worker failure, we implement:
- Heartbeat mechanism: Workers send periodic heartbeats to Worker Pool Manager
- Task timeout: If no heartbeat received within threshold, task is marked as failed
- Automatic retry: Task is requeued with incremented retry count
- Dead letter queue: After max retries, tasks go to dead letter queue for manual investigation

---

## Phase 3: Detailed Design (10-20 minutes)

**I:** Let's dive deeper into the queue management and worker scaling. How do you handle different priorities effectively?

**C:** Great question. Let me detail the priority handling and worker scaling mechanisms:

**Priority Queue Management:**
```
High Priority Queue    → Weight: 70% of workers
Medium Priority Queue  → Weight: 25% of workers  
Low Priority Queue     → Weight: 5% of workers
```

**Worker Allocation Strategy:**
- Dynamic worker allocation based on queue depth and priority
- Workers can switch between queues based on demand
- High-priority queue gets guaranteed minimum worker allocation
- Starving prevention for low-priority tasks

**Auto-scaling Logic:**
```python
# Simplified scaling algorithm
if queue_depth > threshold_high:
    scale_out(target_workers = queue_depth / target_tasks_per_worker)
elif queue_depth < threshold_low and current_workers > min_workers:
    scale_in(target_workers = max(min_workers, queue_depth / target_tasks_per_worker))
```

**I:** How do you ensure exactly-once processing?

**C:** Excellent question. For exactly-once processing:

**Idempotency Design:**
1. **Task Deduplication**: Use content-based hash as task ID to detect duplicates
2. **Worker State Management**: 
   - Task status: PENDING → PROCESSING → COMPLETED/FAILED
   - Use distributed locks (Redis/Zookeeper) during processing
3. **Result Idempotency**: Store processing results with task ID to handle duplicate completions

**Database Schema Design:**
```sql
-- Tasks table
CREATE TABLE tasks (
    task_id VARCHAR(64) PRIMARY KEY,
    content_hash VARCHAR(64) UNIQUE,
    priority ENUM('HIGH', 'MEDIUM', 'LOW'),
    status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED'),
    retry_count INT DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    worker_id VARCHAR(64),
    result_location VARCHAR(256)
);

-- Workers table  
CREATE TABLE workers (
    worker_id VARCHAR(64) PRIMARY KEY,
    status ENUM('ACTIVE', 'BUSY', 'INACTIVE'),
    last_heartbeat TIMESTAMP,
    current_task_id VARCHAR(64),
    capabilities JSON
);
```

**I:** What about monitoring and alerting?

**C:** Comprehensive monitoring is crucial for a distributed task queue:

**Key Metrics:**
1. **Throughput Metrics**: Tasks submitted/processed per second
2. **Latency Metrics**: P50/P90/P99 processing time, queue wait time
3. **Queue Health**: Queue depth by priority, age of oldest task
4. **Worker Metrics**: Worker utilization, failure rates, heartbeat status
5. **System Health**: Database connections, message broker lag, memory usage

**Alerting Strategy:**
- Queue depth > threshold for >5 minutes
- Worker failure rate > 5%
- P99 latency > SLA threshold
- Dead letter queue accumulation
- Database/cache connection failures

**I:** How would you handle a message broker failure?

**C:** For message broker failure resilience:

**Multi-AZ Deployment:**
- Deploy Kafka/SQS across multiple availability zones
- Automatic failover to healthy brokers
- Replication factor of 3 for durability

**Graceful Degradation:**
- Task Manager buffers tasks temporarily during broker outage
- Workers implement exponential backoff for queue polling
- Circuit breaker pattern to prevent cascade failures
- Alternative queue backends for critical tasks

---

## Phase 4: Wrap-up and Trade-offs (5 minutes)

**C:** Let me summarize the key trade-offs and potential improvements:

**Current Design Trade-offs:**

| Aspect | Choice | Trade-off |
|--------|--------|-----------|
| **Consistency** | Eventual consistency | Better performance vs immediate consistency |
| **Queue Technology** | Kafka | High throughput vs operational complexity |
| **Worker Management** | Auto-scaling | Cost efficiency vs cold start latency |
| **Storage** | Separate metadata/payload | Query performance vs storage complexity |

**Potential Issues and Mitigations:**
1. **Hot Partitions**: Use consistent hashing for task distribution
2. **Memory Leaks**: Implement worker restart policies and memory monitoring
3. **Poison Messages**: Dead letter queues with manual investigation workflows
4. **Network Partitions**: Implement jitter in retry mechanisms

**Future Enhancements:**
1. **Task Dependencies**: DAG-based task orchestration for complex workflows
2. **Streaming Processing**: Real-time task processing for ultra-low latency requirements
3. **Multi-tenant Support**: Isolated queues and worker pools per tenant
4. **Advanced Scheduling**: Cron-like scheduling and delayed task execution
5. **Machine Learning**: Predictive scaling based on historical patterns

**I:** This is a solid design. How would you approach the initial rollout?

**C:** For initial rollout, I'd recommend:

1. **Phase 1**: Deploy with basic priority queues and manual scaling
2. **Phase 2**: Add auto-scaling and advanced monitoring
3. **Phase 3**: Implement cross-region replication and disaster recovery
4. **Phase 4**: Add advanced features like dependencies and ML-based scaling

**Deployment Strategy:**
- Blue-green deployment for zero-downtime updates
- Feature flags for gradual rollout of new capabilities
- Comprehensive load testing before production deployment
- Canary deployment for high-risk changes

This design provides a robust foundation for a distributed task queue system that can handle high throughput and low latency while maintaining reliability and scalability.

**I:** Excellent work. Thank you for the comprehensive design.

**C:** Thank you! I enjoyed working through this distributed systems problem and considering the various trade-offs involved in building a production-ready task queue system.
