# System Design Interview: Distributed Task Queue System

**Question:** Design a distributed task queue system that can handle millions of tasks per day, ensuring reliable task execution, proper retry mechanisms, and scalability across multiple workers and regions.

---

## Mock Interview Dialogue

**Interviewer (I):** Today we're designing a distributed task queue system. Think of something like Celery, Amazon SQS, or Google Cloud Tasks. The system should handle millions of tasks per day with reliable execution and proper scaling. You have 45 minutes. Please walk me through your approach.

**Candidate (C):** Thank you for the interesting problem. I'll approach this systematically using a four-phase structure: understanding the problem and establishing design scope, high-level design with typical use cases, detailed design focusing on key components, and finally wrap-up with trade-offs and future improvements. Let me start by clarifying the requirements.

---

## **PHASE 1: UNDERSTAND THE PROBLEM AND REQUIREMENTS (3-5 minutes)**

**C:** Let me start by understanding the core requirements and constraints:

**Functional Requirements (Who, What, Whom, Where):**
1. **Who** are the actors using this task queue system? (application services, background workers, admin dashboards, monitoring systems)
2. **What** core operations does the system need to support? (task submission, task execution, retry logic, task scheduling, priority handling)
3. **Whom/What** are these operations performed on? (background jobs, batch processing tasks, async operations, scheduled jobs)
4. **Where** does this system operate? (single region, multi-region, edge locations)
5. Are there different types of tasks with varying priorities and execution requirements?
6. What integrations with external systems are required for task execution?

**Non-Functional Requirements (Performance, Monitoring, Failures, Security):**
7. **Performance & Tuning**: What are the expected task throughput and latency requirements for task processing?
8. **Monitoring & Alerting**: What metrics are critical - task success rates, queue depths, worker health, processing latencies?
9. **Failure Scenarios**: How should the system handle worker failures, task timeouts, and poison messages?
10. **Security**: What are the authentication requirements and how do we secure task payloads and worker access?

**I:** Good questions. Let me provide context:
- **Who**: Microservices submitting async tasks, background workers processing tasks, DevOps teams monitoring the system, and automated schedulers
- **What**: Task submission with priority levels, reliable task execution, automatic retries with exponential backoff, delayed/scheduled tasks, dead letter queue handling
- **Whom**: Various background jobs like image processing, email sending, data ETL pipelines, report generation, cleanup tasks
- **Where**: Multi-region deployment (3 regions) with cross-region task distribution capability
- Task types: High priority (real-time notifications), Medium priority (batch processing), Low priority (cleanup jobs)
- **Performance**: Handle 10M tasks/day with peak of 5K tasks/second, task processing latency <1 second for high priority
- **Monitoring**: Task throughput, queue depth, worker utilization, failed task rates, processing times
- **Failures**: Workers can crash, tasks can timeout, need poison message detection and dead letter queues
- **Security**: JWT-based task submission authentication, encrypted task payloads, worker authorization

**C:** Perfect. Based on your context, let me establish some performance and operational targets:

**Performance Analysis:**
- For 10M tasks/day with 5K TPS peak, need horizontally scalable worker pools
- Task processing latency target: <1s for high priority, <10s for medium, <60s for low priority
- Queue depth monitoring to prevent memory overflow and ensure timely processing

**Operational Requirements:**
- Monitor queue depths per priority level and alert on backlog buildup
- Track task success/failure rates and identify poison message patterns
- Implement graceful worker scaling based on queue depth and processing times

Let me establish the design scope:

**In Scope:**
- Distributed task queue with priority levels
- Reliable task execution with retry mechanisms
- Worker auto-scaling and load balancing
- Dead letter queue for failed tasks
- Cross-region task distribution
- Real-time monitoring and alerting

**Out of Scope (for this interview):**
- Complex workflow orchestration (DAGs)
- Task scheduling with cron-like functionality
- Real-time streaming task processing

**Key Assumptions:**
- Tasks are mostly independent (minimal dependencies)
- Average task execution time: 2-5 seconds
- 80/20 rule: 20% of task types generate 80% of volume
- Acceptable task loss rate: <0.01% with proper retry mechanisms

### **Simple Back-of-Envelope Calculations:**

**C:** Let me do some quick capacity estimation:

**Scale Estimates:**
- Daily tasks: 10M tasks/day
- Average TPS: 10M / (24 * 3600) ≈ 116 TPS
- Peak TPS: 5K TPS (roughly 43x average)
- Task payload size: ~1KB average
- Daily storage: 10M * 1KB = 10GB/day
- Storage (30 days retention): 300GB
- Worker memory per task: ~10MB during processing
- Concurrent tasks at peak: 5K * 3s avg execution = 15K concurrent tasks

**Key Numbers:**
- Need worker pools capable of handling 15K concurrent tasks
- Queue storage should handle 10GB daily throughput
- Network bandwidth: 5K TPS * 1KB = 5MB/s peak

---

## **PHASE 2: HIGH-LEVEL DESIGN (10-15 minutes)**

**C:** Let me propose a high-level architecture:

**System Architecture Diagram:**
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Producer   │────│Load Balancer│────│  Queue API  │
│  Services   │    │             │    │  Gateway    │
└─────────────┘    └─────────────┘    └─────────────┘
                                              │
                                              ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Message     │────│ Task Queue  │────│  Worker     │
│ Broker      │    │ Manager     │    │  Pools      │
│ (Kafka)     │    │             │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
        │                  │                  │
        ▼                  ▼                  ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Dead Letter │    │ Task        │    │ Worker      │
│ Queue       │    │ Metadata DB │    │ Registry    │
└─────────────┘    └─────────────┘    └─────────────┘
```

**Core Components:**
1. **Queue API Gateway**: Task submission endpoint with authentication and validation
2. **Message Broker**: Kafka for durable task storage and distribution with partitioning
3. **Task Queue Manager**: Orchestrates task routing, retry logic, and worker assignment
4. **Worker Pools**: Auto-scaling groups of workers organized by task type and priority
5. **Worker Registry**: Service discovery and health monitoring for active workers
6. **Task Metadata DB**: PostgreSQL for task state, retry counts, and execution history

### **API Design:**

**C:** Here are the key APIs:

**Core Endpoints:**
```
// Task submission
POST /api/v1/tasks
{
  "task_type": "image_processing",
  "priority": "high",
  "payload": {...},
  "retry_policy": {
    "max_retries": 3,
    "backoff_multiplier": 2
  },
  "delay_seconds": 0
}

Response: 201 Created
{
  "task_id": "uuid-123",
  "status": "queued",
  "estimated_execution_time": "2023-10-08T10:35:00Z"
}

// Task status check
GET /api/v1/tasks/{task_id}
Response: 200 OK
{
  "task_id": "uuid-123",
  "status": "completed",
  "result": {...},
  "execution_time_ms": 2500,
  "retry_count": 0
}

// Worker registration (internal)
POST /internal/workers/register
{
  "worker_id": "worker-456",
  "task_types": ["image_processing", "email_sending"],
  "capacity": 10,
  "region": "us-west-2"
}
```

### **Typical Use Case Walkthrough:**

**C:** Let me walk through a typical task processing workflow:

**Scenario**: A user uploads an image that needs to be processed for thumbnail generation

**Step-by-step flow:**
1. Image service uploads file → submits task via POST /api/v1/tasks
2. Queue API Gateway validates request and assigns task_id
3. Task metadata stored in PostgreSQL with status "queued"
4. Task published to appropriate Kafka partition (based on priority and type)
5. Task Queue Manager routes task to available worker in the correct pool
6. Worker pulls task, updates status to "processing", and begins execution
7. Worker processes image, generates thumbnails, uploads to storage
8. Worker updates task status to "completed" with execution results
9. Task completion event published for any downstream consumers
10. Success metrics updated and monitoring alerts cleared

**Performance Characteristics:**
- Expected latency: 1-3 seconds for high priority image processing tasks
- Queue depth: <1000 tasks per partition under normal load
- Worker utilization: 70-80% average with auto-scaling headroom

**I:** What happens if a worker crashes during task execution?

**C:** Great question. For worker failure scenarios, we have several safety mechanisms:

1. **Heartbeat Monitoring**: Workers send heartbeats every 30 seconds; missed heartbeats trigger task reassignment
2. **Task Timeouts**: Each task has a maximum execution time; timeouts trigger automatic retry
3. **Graceful Shutdown**: Workers finish current tasks before shutting down during deployments
4. **Task Leasing**: Tasks are "leased" to workers with TTL; expired leases allow task reassignment
5. **Dead Letter Queue**: After max retries, failed tasks move to DLQ for manual investigation

**I:** How do you handle different task priorities efficiently?

**C:** For priority handling, I implement a multi-level approach:

1. **Separate Kafka Topics**: High, medium, low priority topics with different consumer configurations
2. **Dedicated Worker Pools**: High priority pools with more resources and faster scaling
3. **Weighted Fair Queuing**: Workers poll high priority queues more frequently (80% high, 15% medium, 5% low)
4. **Resource Reservation**: Reserve 20% of worker capacity exclusively for high priority tasks
5. **Priority Preemption**: Critical tasks can interrupt low priority work with proper cleanup

---

## **PHASE 3: DETAILED DESIGN (10-20 minutes)**

**C:** Let me focus on the core task processing engine and scaling mechanisms:

### **Performance & Scalability Focus**

**Task Processing Engine Design:**

1. **Message Broker Architecture (Kafka)**:
```
Topic Structure:
tasks.high.image_processing    (3 partitions, 3 replicas)
tasks.high.email_sending      (3 partitions, 3 replicas)
tasks.medium.batch_processing (6 partitions, 3 replicas)
tasks.low.cleanup             (3 partitions, 2 replicas)

Key Benefits:
- Partition by task_type for parallel processing
- Replication for durability
- Consumer groups for load balancing
```

2. **Worker Pool Management**:
```
High Priority Pool:
- Min workers: 50
- Max workers: 500
- Scale up trigger: Queue depth > 100 OR avg latency > 2s
- Scale down trigger: Queue depth < 10 AND avg latency < 1s

Medium Priority Pool:
- Min workers: 20
- Max workers: 200
- Scale triggers based on queue depth and processing time

Worker Health Monitoring:
- Heartbeat interval: 30s
- Task timeout: 5 minutes
- Max concurrent tasks per worker: 5
```

3. **Retry and Dead Letter Queue Logic**:
```
Retry Policy:
- Initial delay: 1s
- Exponential backoff: delay = base_delay * (2^retry_count)
- Max delay: 300s
- Jitter: ±20% to prevent thundering herd

Dead Letter Queue Criteria:
- Max retries exceeded (default: 5)
- Poison message detection (consistent parse failures)
- Task timeout exceeded multiple times
- Worker reporting task as non-retryable
```

**Database Schema for Task Metadata:**
```sql
CREATE TABLE tasks (
    task_id UUID PRIMARY KEY,
    task_type VARCHAR(100) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(50) NOT NULL,
    payload JSONB NOT NULL,
    retry_count INTEGER DEFAULT 0,
    max_retries INTEGER DEFAULT 3,
    created_at TIMESTAMP DEFAULT NOW(),
    scheduled_at TIMESTAMP DEFAULT NOW(),
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    worker_id VARCHAR(100),
    error_message TEXT,
    execution_time_ms INTEGER,
    
    INDEX(status, priority, created_at),
    INDEX(task_type, status),
    INDEX(scheduled_at) WHERE status = 'scheduled'
);

CREATE TABLE worker_registry (
    worker_id VARCHAR(100) PRIMARY KEY,
    task_types TEXT[] NOT NULL,
    capacity INTEGER NOT NULL,
    current_load INTEGER DEFAULT 0,
    region VARCHAR(50) NOT NULL,
    last_heartbeat TIMESTAMP DEFAULT NOW(),
    status VARCHAR(20) DEFAULT 'active',
    
    INDEX(status, last_heartbeat),
    INDEX(region, task_types)
);
```

**Load Testing Results** (estimated):
- Current capacity: 8K TPS sustained with 3-region deployment
- Breaking point: 12K TPS before worker pool saturation
- Scaling headroom: 2.5x with current auto-scaling configuration

**I:** How do you ensure tasks aren't lost during system failures?

**C:** For durability and reliability, I implement multiple layers of protection:

1. **Kafka Persistence**: Messages persisted to disk with configurable retention (7 days default)
2. **At-Least-Once Delivery**: Tasks can be processed multiple times; workers must be idempotent
3. **Transaction Log**: All task state changes logged in PostgreSQL with ACID guarantees
4. **Cross-Region Replication**: Critical tasks replicated across regions for disaster recovery
5. **Backup Processing**: Failed region tasks automatically redistributed to healthy regions

### **Service Architecture Focus**

**Microservices Breakdown:**
1. **Queue API Service**:
   - Domain: Task submission and status queries
   - APIs: /tasks (POST, GET), /tasks/{id} (GET, DELETE)
   - Data: Request validation, task metadata

2. **Task Manager Service**:
   - Domain: Task orchestration and lifecycle management
   - APIs: Internal task routing and retry logic
   - Data: Task state transitions, retry policies

3. **Worker Management Service**:
   - Domain: Worker registration, health monitoring, and load balancing
   - APIs: /workers (register, heartbeat, status)
   - Data: Worker registry, capacity tracking

4. **Dead Letter Queue Service**:
   - Domain: Failed task analysis and recovery
   - APIs: /dlq (list, retry, delete)
   - Data: Failed task storage and investigation tools

**Service Communication:**
- **Synchronous**: gRPC for worker registration and health checks
- **Asynchronous**: Kafka for task distribution and status updates
- **Data Consistency**: Eventually consistent across services, strongly consistent within service boundaries

---

## **PHASE 4: WRAP-UP AND SUMMARY (3-5 minutes)**

**C:** Let me summarize the key aspects of this design:

**Architecture Summary:**
We've designed a distributed task queue system that supports 10M daily tasks with 5K TPS peak capacity. The architecture uses Kafka for durable message storage, auto-scaling worker pools for processing, and PostgreSQL for task metadata and state management.

**Key Design Decisions:**
1. **Kafka for Message Broker**: Chose Kafka over Redis/RabbitMQ for higher throughput, durability, and partition-based scaling
2. **Separate Worker Pools by Priority**: Implemented dedicated pools to ensure high priority tasks get guaranteed resources
3. **Database for Task Metadata**: Selected PostgreSQL for ACID compliance and complex query support for monitoring

**Trade-offs Made:**
1. **Consistency vs Performance**: Chose at-least-once delivery accepting potential duplicate processing for higher throughput
2. **Cost vs Reliability**: Opted for cross-region replication (higher cost) for disaster recovery requirements
3. **Complexity vs Flexibility**: Selected microservices architecture over monolith for independent scaling and evolution

**System Characteristics:**
- **Scalability**: Supports 10M daily tasks with auto-scaling worker pools handling 8K sustained TPS
- **Reliability**: Achieves 99.99% task completion rate through retries, DLQ, and cross-region replication
- **Performance**: Delivers <1s processing latency for high priority tasks under normal load
- **Maintainability**: Microservices enable independent deployment and technology evolution

### **Future Improvements:**

**C:** Areas for future enhancement:

**Short-term (3-6 months):**
- Machine learning for optimal worker pool sizing based on historical patterns
- Task dependency management for simple workflow support
- Enhanced monitoring with custom business metrics and alerting

**Medium-term (6-12 months):**
- Multi-tenancy support with resource isolation and quotas
- Task batching for improved efficiency in bulk operations
- Geographic task routing based on data locality

**Long-term (1+ years):**
- Serverless worker execution for cost optimization
- Advanced workflow orchestration with DAG support
- Real-time task analytics and predictive scaling

**Risk Mitigation Priorities:**
1. **Kafka Cluster Failure** → Implement cross-region Kafka replication and automated failover
2. **Poison Message Handling** → Enhanced detection algorithms and automated quarantine
3. **Worker Pool Exhaustion** → Predictive scaling and circuit breakers for overload protection

**C:** This design balances reliability requirements with performance needs while providing clear scaling paths for future growth. The event-driven architecture ensures loose coupling between components and enables independent evolution of the system.

I'm confident this solution addresses the core requirements for handling 10M daily tasks with proper reliability, monitoring, and scaling capabilities. I'm happy to dive deeper into specific areas like the retry algorithms, cross-region failover strategies, or worker auto-scaling policies.

**I:** What's the biggest operational challenge with this design?

**C:** The biggest operational challenge is **managing task backlog during traffic spikes while maintaining SLAs**. During peak periods, high priority tasks might get delayed behind medium/low priority backlogs.

I'd address this through:

1. **Predictive Auto-scaling**: Use historical patterns and leading indicators (like user activity) to pre-scale worker pools
2. **Admission Control**: Temporarily reject low priority tasks during extreme load to protect high priority SLAs
3. **Circuit Breaker Integration**: Implement backpressure mechanisms to prevent cascade failures from overwhelming the queue
4. **Multi-Region Load Shedding**: Automatically distribute excess load across regions based on capacity
5. **Real-time SLA Monitoring**: Alert and take corrective action when priority task latencies approach SLA thresholds

The key is building operational runbooks and automated responses for common scenarios rather than relying on manual intervention during incidents.
