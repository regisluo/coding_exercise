## **10 Technical Interview Questions for Senior Tech Lead Role**

*Based on your tech stack: Spring Boot, Kafka, AWS (MSK, S3, Redshift, EKS), microservices, data platform, Azure,
full-stack development*

---

### **Question 1: Design a Fault-Tolerant Data Processing Pipeline**

**Interviewer:** *"You mentioned building an event-driven data platform at MKU. Walk me through how you designed the
system to handle failures at each stage—Kafka consumption, processing, and loading to Redshift. How do you ensure zero
data loss?"*

---

**ANSWER:**

*"Great question. Fault tolerance was critical for our financial data platform. Let me walk through our defense-in-depth
approach at each stage:*

**1. Kafka Consumption Layer (Messaging-Gateway):**

**Challenge:** What if the consumer crashes after reading a message but before processing it?

**Solution - At-Least-Once Delivery:**

- Manual offset commit—we only commit offsets AFTER successful S3 persistence
- If consumer crashes before commit, Kafka redelivers the message
- We accept potential duplicates (handled downstream)

**Configuration:**

```
enable.auto.commit = false
isolation.level = read_committed
max.poll.records = tuned for 15-min window
```

**Failure Scenario:** Consumer crashes

- Kafka broker detects heartbeat timeout
- Partition rebalances to healthy consumer
- Uncommitted messages reprocessed
- S3 persistence is idempotent (same message written multiple times = same result)

**2. Persistence Layer (S3):**

**Challenge:** What if S3 write fails?

**Solution - Retry with Exponential Backoff:**

```java
RetryPolicy retryPolicy = RetryPolicy.builder()
        .maxAttempts(3)
        .delay(Duration.ofSeconds(2))
        .backoffMultiplier(2.0)
        .build();

// AWS SDK automatically handles retries for transient failures
// If all retries fail, message stays uncommitted in Kafka
// Will be reprocessed on next poll
```

**Why S3:**

- 99.999999999% durability (11 nines)
- Cross-AZ replication automatic
- Versioning enabled for accidental deletes

**3. Job Creation (Manager):**

**Challenge:** What if Manager crashes after retrieving S3 data but before creating jobs?

**Solution - Stateless Design with Checkpointing:**

- Manager stores last-processed time window in DynamoDB
- On restart, Manager reads checkpoint: "Last processed window: 14:00-14:15"
- Processes next window: 14:15-14:30
- Updates checkpoint after successful job creation
- If crash happens, restart from last checkpoint (may reprocess same window, but idempotent)

**4. Processing Layer (Extractor Cluster):**

**Challenge:** What if an Extractor crashes mid-processing?

**Solution - Job State Tracking + Timeouts:**

```
Job States: PENDING → IN_PROGRESS → COMPLETED / FAILED

Manager tracks:
- Job ID: job-12345
- State: IN_PROGRESS
- Assigned to: extractor-pod-3
- Started at: 14:23:45
- Timeout: 5 minutes

If Extractor crashes:
- Stops sending heartbeats to Manager
- Manager detects missing heartbeats (30 seconds)
- Marks extractor-pod-3 as UNHEALTHY
- Job job-12345 marked as FAILED (timeout)
- Job returns to queue for retry
- Healthy Extractor picks it up
```

**Kubernetes Self-Healing:**

- K8s detects pod failure via liveness probe
- Automatically restarts pod
- New pod registers with Manager
- Starts accepting new jobs

**5. Database Extraction:**

**Challenge:** What if database query times out or connection fails?

**Solution - Connection Pooling + Circuit Breaker:**

```java
HikariConfig config = new HikariConfig();
config.

setMaximumPoolSize(20);
config.

setConnectionTimeout(5000);
config.

setIdleTimeout(300000);
config.

setMaxLifetime(1800000);

// Circuit breaker pattern
@CircuitBreaker(
        failureRateThreshold = 50,
        waitDurationInOpenState = 30s,
        ringBufferSizeInClosedState = 10
)
public DealData extractDealData(String dealId) {
    return tradingDatabase.query(dealId);
}
```

**Failure Handling:**

- Transient failures: Retry 3 times with backoff
- Persistent failures: Circuit opens, fail fast, job marked FAILED
- Failed jobs go to Replay Queue for manual investigation

**6. Redshift Loading:**

**Challenge:** What if Redshift load fails?

**Solution - Transactional Loads with Rollback:**

```sql
BEGIN TRANSACTION;

COPY raw.trading_events
FROM 's3://bucket/path/'
IAM_ROLE 'arn:aws:iam::xxx:role/RedshiftRole'
FORMAT AS JSON
TIMEFORMAT 'auto';

-- Validation
SELECT COUNT(*) FROM raw.trading_events WHERE load_timestamp = CURRENT_TIMESTAMP;

-- If count matches expected, commit
COMMIT;

-- If mismatch, rollback
-- ROLLBACK;
```

**Idempotency:**

- Each load tagged with unique load_id
- Before loading, check: `SELECT * FROM load_log WHERE load_id = 'xxx'`
- If already loaded, skip (safe retry)

**7. External Completeness Validation:**

**Challenge:** How do we know for sure no data was lost?

**Solution - Independent Reconciliation:**

**Support Dashboard runs every 15 minutes:**

```python
def reconcile_data_completeness(time_window):
    # Count at each stage
    kafka_count = count_kafka_messages(time_window)
    s3_count = count_s3_messages(time_window)
    redshift_count = count_redshift_records(time_window)
    
    # Compare
    if not (kafka_count == s3_count == redshift_count):
        alert("Data loss detected in window {}", time_window)
        trigger_replay(time_window)
    
    return reconciliation_report
```

**8. Dead Letter Queue (DLQ) Pattern:**

**For Unrecoverable Failures:**

```
Job Retry Flow:
1. First attempt fails → Retry (exponential backoff)
2. Second attempt fails → Retry
3. Third attempt fails → Move to DLQ

DLQ jobs:
- Logged with full context (error, stack trace, data)
- Alert sent to support team
- Manual investigation and replay
- Pattern analysis to prevent future occurrences
```

---

**End-to-End Fault Tolerance Summary:**

```
┌─────────────────────────────────────────────────────────┐
│ Stage          │ Failure Handling                       │
├────────────────┼────────────────────────────────────────┤
│ Kafka          │ Manual offset commit, redelivery       │
│ S3             │ Retry, 11-9s durability                │
│ Manager        │ Checkpointing, idempotent processing   │
│ Extractor      │ Heartbeat monitoring, job timeout      │
│ Database       │ Connection pooling, circuit breaker    │
│ Redshift       │ Transactional loads, idempotency       │
│ Validation     │ Reconciliation, automated alerts       │
│ Unrecoverable  │ DLQ, manual replay                     │
└─────────────────────────────────────────────────────────┘

Result: Zero data loss in 18 months of operation
```

---

**Trade-offs Discussed:**

*"We chose at-least-once delivery over exactly-once because:*

- Simpler implementation (no distributed transactions)
- Better performance (lower latency)
- Acceptable for our use case (idempotent downstream processing)
- Financial data integrity validated through reconciliation, not delivery guarantees alone

*For critical financial transactions, we would use exactly-once semantics with Kafka transactions, but for reporting
data, at-least-once with reconciliation is sufficient and more performant."*

---

### **Question 2: How Do You Handle Database Schema Changes in a Microservices Environment?**

**Interviewer:** *"Your microservices at MKU share a Redshift database. How do you manage schema migrations without
causing downtime or breaking services?"*

---

**ANSWER:**

*"Database schema changes in microservices are tricky—you have multiple services potentially accessing the same tables,
and you can't coordinate deployments perfectly. Here's our strategy:*

**Core Principle: Backward-Compatible Changes Only**

*"We enforce a rule: Every schema change must be backward-compatible so old and new code versions can coexist during
deployment."*

---

**Pattern 1: Expand-Contract (Parallel Change)**

**Scenario:** Rename column `customer_name` to `customer_full_name`

**Traditional (Breaking) Approach:**

```sql
-- DON'T DO THIS - breaks old code immediately
ALTER TABLE customers RENAME COLUMN customer_name TO customer_full_name;
```

**Our Approach - 3 Phases:**

**Phase 1: Expand (Add new column, keep old)**

```sql
-- Migration v1
ALTER TABLE validated.customers 
ADD COLUMN customer_full_name VARCHAR(255);

-- Populate new column from old
UPDATE validated.customers 
SET customer_full_name = customer_name 
WHERE customer_full_name IS NULL;

-- Trigger to keep them in sync
CREATE TRIGGER sync_customer_name
AFTER INSERT OR UPDATE ON validated.customers
FOR EACH ROW
BEGIN
    NEW.customer_full_name = NEW.customer_name;
END;
```

**Phase 2: Migrate (Update code to use new column)**

```java
// Deploy v1: Write to both, read from new
@Entity
public class Customer {
    @Column(name = "customer_name")  // Still here
    private String customerName;

    @Column(name = "customer_full_name")  // New column
    private String customerFullName;

    public void setName(String name) {
        this.customerName = name;  // Write to both
        this.customerFullName = name;
    }

    public String getName() {
        return this.customerFullName;  // Read from new
    }
}

// Wait 2-4 weeks for all services to upgrade
```

**Phase 3: Contract (Remove old column)**

```sql
-- Migration v2 (after all services upgraded)
ALTER TABLE validated.customers DROP COLUMN customer_name;
DROP TRIGGER sync_customer_name;
```

---

**Pattern 2: Additive Changes (Safest)**

**Adding a Column:**

```sql
-- Always safe if nullable or has default
ALTER TABLE validated.deals 
ADD COLUMN settlement_status VARCHAR(20) DEFAULT 'PENDING';

-- Old code: Ignores new column (works fine)
-- New code: Uses new column
```

**Our Rule:**

- New columns must be nullable OR have default values
- Old services won't populate it, new services will
- No breaking changes

---

**Pattern 3: Data Type Changes**

**Scenario:** Change `amount DECIMAL(10,2)` to `DECIMAL(18,4)` (more precision)

**Approach:**

```sql
-- Safe: Widening precision is backward-compatible
ALTER TABLE validated.deals 
ALTER COLUMN amount TYPE DECIMAL(18,4);

-- Old code: Writes DECIMAL(10,2) → fits in DECIMAL(18,4) ✓
-- New code: Writes DECIMAL(18,4) → uses full precision ✓
```

**Rule:**

- Widening types: Safe (VARCHAR(50) → VARCHAR(100))
- Narrowing types: Requires expand-contract pattern

---

**Pattern 4: Adding NOT NULL Constraint**

**Scenario:** Make `email` column NOT NULL

**Wrong Approach:**

```sql
-- DON'T - breaks if any service writes without email
ALTER TABLE customers ALTER COLUMN email SET NOT NULL;
```

**Right Approach - 3 Phases:**

**Phase 1: Make email required in application**

```java
// Deploy validation first
@Column(nullable = false)  // Application validates
@NotNull
private String email;
```

**Phase 2: Backfill missing data**

```sql
-- Fix existing data
UPDATE customers 
SET email = 'noreply@company.com' 
WHERE email IS NULL;
```

**Phase 3: Add database constraint**

```sql
-- After all services enforce it and data cleaned
ALTER TABLE customers ALTER COLUMN email SET NOT NULL;
```

---

**Our Migration Process:**

**1. Migration Script Versioning (Flyway/Liquibase):**

```
db/migrations/
├── V1.0__initial_schema.sql
├── V1.1__add_customer_full_name.sql
├── V1.2__add_settlement_status.sql
└── V1.3__drop_customer_name.sql

Each migration:
- Versioned
- Applied once
- Tracked in schema_version table
- Idempotent (can run multiple times safely)
```

**2. Deployment Coordination:**

```
Week 1: Deploy schema change (backward-compatible)
        Old services: Continue working
        New services: Can start using new schema
        
Week 2-3: Rolling deployment of new service versions
          Both old and new code running simultaneously
          
Week 4: All services upgraded
        
Week 5: Deploy cleanup migration (remove old columns/triggers)
```

**3. Testing Strategy:**

```
Pre-deployment:
├── Test migration on staging database
├── Test old service version with new schema
├── Test new service version with new schema
├── Validate data integrity
└── Test rollback scenario

Post-deployment:
└── Monitor for errors in production
```

---

**Real Example from MKU:**

**Adding `data_quality_score` to deals table:**

```sql
-- Week 1: Schema change
ALTER TABLE validated.deals 
ADD COLUMN data_quality_score DECIMAL(3,2) DEFAULT 0.00;

-- Old Extractor services: Don't populate it (default 0.00 used)
-- New Extractor services: Calculate and populate score
-- Works for both!

-- Week 5: All services upgraded, schema fully utilized
```

---

**Shared Database Anti-Pattern Discussion:**

*"Ideally, microservices shouldn't share databases—each service owns its data. But in reality, especially with data
warehouses like Redshift:*

**Why We Share Redshift:**

- Redshift is for analytics, not transactional
- Cross-service queries needed (JOIN deals with customers)
- Cost: Separate Redshift clusters expensive
- Data governance: Centralized makes compliance easier

**How We Mitigate:**

- Clear table ownership (Extractor owns `raw.*`, validation layer owns `validated.*`)
- Schema changes go through change approval
- Backward compatibility enforced
- Services communicate via events (Kafka), not direct DB reads

**Long-term:**

- Moving toward event-driven architecture
- Services publish events to Kafka
- Downstream consumers rebuild their own views
- Reduces tight coupling"

---

**Key Principles:**

✅ **Backward compatibility first**
✅ **Expand-contract for breaking changes**
✅ **Additive changes preferred**
✅ **Test both old and new code with new schema**
✅ **Gradual rollout over weeks, not hours**
✅ **Automate with Flyway/Liquibase**
✅ **Document ownership and approval process**

---

### **Question 3: Scaling Kafka Consumers - How Do You Optimize Throughput?**

**Interviewer:** *"Your Messaging-Gateway consumes from Kafka at MKU. How do you optimize throughput when dealing with
high-volume trading data? What if you need to process 10x more messages?"*

---

**ANSWER:**

*"Excellent question. Kafka consumer optimization has multiple dimensions. Let me walk through our approach and how we'd
scale 10x:*

---

**Current Architecture Baseline:**

```
Upstream Kafka Topic: trading-events
├── Partitions: 6
├── Current throughput: 10,000 msg/sec
├── Message size: ~2KB average
├── Target: Scale to 100,000 msg/sec
```

---

**Strategy 1: Increase Consumer Parallelism**

**Understanding Kafka Consumer Groups:**

- Maximum consumers in group = number of partitions
- Currently: 6 partitions = max 6 consumers

**Problem with Current 6 Partitions:**

- Each consumer handles ~1,667 msg/sec (10K / 6)
- To hit 100K msg/sec: Each consumer would need 16,667 msg/sec
- That's too much for single consumer

**Solution: Increase Partitions**

```
# Kafka topic configuration
kafka-topics --alter \
  --topic trading-events \
  --partitions 60 \
  --bootstrap-server kafka-broker:9092

Result:
├── 60 partitions
├── 60 parallel consumers possible
├── Each consumer: 100K / 60 = 1,667 msg/sec (same as before)
├── Horizontal scaling achieved
```

**Messaging-Gateway Scaling:**

```yaml
# Kubernetes Deployment
apiVersion: apps/v1
kind: Deployment
metadata:
  name: messaging-gateway
spec:
  replicas: 60  # Match partition count
  template:
    spec:
      containers:
        - name: gateway
          image: messaging-gateway:v2
          resources:
            requests:
              memory: "512Mi"
              cpu: "500m"
            limits:
              memory: "1Gi"
              cpu: "1000m"
```

**Consumer Group Behavior:**

- Kafka auto-assigns 1 partition per consumer
- If 1 consumer dies, partition rebalances to others
- Can have fewer consumers than partitions (some consumers get multiple)

---

**Strategy 2: Optimize Consumer Configuration**

**Batch Processing (Fetch More Per Poll):**

```java
Properties props = new Properties();

// Increase batch size
props.

put("max.poll.records",500);  // Up from 100
// Fetch more data per request
props.

put("fetch.min.bytes",1024*1024);  // 1MB
// Wait up to 500ms for batch to fill
props.

put("fetch.max.wait.ms",500);

// Larger receive buffer
props.

put("receive.buffer.bytes",64*1024);  // 64KB

KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
```

**Why This Helps:**

- Fewer network round-trips
- Process messages in batches (more efficient)
- Better throughput, slightly higher latency (acceptable for our use case)

**Trade-off:**

- Batch processing: Higher throughput, higher latency
- Our case: 15-minute window tolerance, batch processing perfect fit

---

**Strategy 3: Optimize Message Processing**

**Current Bottleneck Analysis:**

```
Per-Message Processing Time:
├── Kafka poll: 1ms
├── Deserialization: 2ms
├── Validation: 5ms
├── S3 write: 50ms ← BOTTLENECK
└── Total: 58ms per message

At 58ms/msg: Max throughput = 17 msg/sec per consumer
Need: 1,667 msg/sec per consumer
Problem: S3 writes are slow!
```

**Solution: Batch S3 Writes**

```java

@Service
public class MessageGateway {
    private List<Message> batch = new ArrayList<>();
    private static final int BATCH_SIZE = 100;

    public void processMessages() {
        while (running) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {
                Message msg = deserialize(record.value());
                validate(msg);
                batch.add(msg);

                // Batch writes to S3
                if (batch.size() >= BATCH_SIZE) {
                    flushToS3(batch);
                    batch.clear();
                    consumer.commitSync();
                }
            }
        }
    }

    private void flushToS3(List<Message> messages) {
        // Single S3 write for 100 messages
        String json = JsonSerializer.serialize(messages);
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket("trading-messages")
                        .key(generateKey())
                        .build(),
                RequestBody.fromString(json));

        // 1 S3 call for 100 messages instead of 100 calls
    }
}
```

**Performance Improvement:**

```
Before: 100 messages × 50ms = 5,000ms
After: 1 S3 call for 100 messages = 50ms

Speed-up: 100x faster
New throughput: 17 msg/sec → 1,700 msg/sec per consumer ✓
```

---

**Strategy 4: Asynchronous Processing**

**Problem:** Blocking I/O (S3 writes) wastes CPU time

**Solution: Async I/O with CompletableFuture**

```java

@Service
public class AsyncMessageGateway {
    private final S3AsyncClient s3AsyncClient;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public void processMessages() {
        while (running) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (ConsumerRecord<String, String> record : records) {
                // Process asynchronously
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    Message msg = deserialize(record.value());
                    validate(msg);
                    writeToS3Async(msg);
                }, executor);

                futures.add(future);
            }

            // Wait for all async operations to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // Commit offsets only after all messages processed
            consumer.commitSync();
        }
    }
}
```

**Benefit:**

- CPU doesn't block waiting for S3
- Process multiple messages concurrently
- Better resource utilization

---

**Strategy 5: Multi-Threading Within Consumer**

**Pattern: Single Consumer, Multiple Worker Threads**

```java
public class MultiThreadedConsumer {
    private final ExecutorService workers = Executors.newFixedThreadPool(20);
    private final BlockingQueue<ConsumerRecord> workQueue = new LinkedBlockingQueue<>(1000);

    // Consumer thread (single)
    public void consume() {
        while (running) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {
                // Add to work queue
                workQueue.offer(record);
            }
        }
    }

    // Worker threads (20 parallel)
    public void processWorker() {
        while (running) {
            ConsumerRecord record = workQueue.poll(100, TimeUnit.MILLISECONDS);
            if (record != null) {
                processMessage(record);
            }
        }
    }
}
```

**Caution:**

- Offset management tricky (can't commit until all workers done)
- Potential out-of-order processing (acceptable for our use case)
- We chose multiple consumers instead (simpler, Kafka handles coordination)

---

**Strategy 6: Optimize Serialization**

**Current: JSON Serialization**

```
Message size: 2KB JSON
Serialization time: 2ms
```

**Alternative: Avro or Protocol Buffers**

```
Message size: 600 bytes (70% smaller)
Serialization time: 0.5ms (4x faster)

Benefits:
├── Less network bandwidth
├── Faster serialization/deserialization
├── Schema validation built-in
└── Backward-compatible schema evolution
```

**Trade-off:**

- JSON: Human-readable, easier debugging
- Avro: Compact, faster, requires schema registry

**Our Choice:**

- Stuck with JSON for now (operations team prefers readability)
- Future: Consider Avro if throughput becomes issue

---

**Strategy 7: Monitoring & Auto-Scaling**

**Consumer Lag Monitoring:**

```java
// Export Kafka consumer lag as Prometheus metric
@Component
public class ConsumerLagMonitor {

    @Scheduled(fixedRate = 30000)  // Every 30 seconds
    public void monitorLag() {
        Map<TopicPartition, Long> endOffsets = consumer.endOffsets(assignedPartitions);
        Map<TopicPartition, Long> currentOffsets = consumer.position(assignedPartitions);

        for (TopicPartition partition : assignedPartitions) {
            long lag = endOffsets.get(partition) - currentOffsets.get(partition);

            // Expose to Prometheus
            lagGauge.labels(partition.topic(), String.valueOf(partition.partition()))
                    .set(lag);
        }
    }
}
```

**Kubernetes HPA (Horizontal Pod Autoscaler):**

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: messaging-gateway-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: messaging-gateway
  minReplicas: 10
  maxReplicas: 60
  metrics:
    - type: External
      external:
        metric:
          name: kafka_consumer_lag
        target:
          type: AverageValue
          averageValue: "10000"  # Scale up if lag > 10K per consumer

  behavior:
    scaleUp:
      stabilizationWindowSeconds: 60
      policies:
        - type: Percent
          value: 50  # Scale up 50% at a time
          periodSeconds: 60
    scaleDown:
      stabilizationWindowSeconds: 300  # Wait 5 min before scaling down
      policies:
        - type: Pods
          value: 2  # Scale down slowly (2 pods at a time)
          periodSeconds: 60
```

**Auto-Scaling Behavior:**

- Normal load: 10 consumers
- High load (month-end): Scales to 40 consumers automatically
- Lag detected: Scales up within 2 minutes
- Lag cleared: Scales down gradually (cost optimization)

---

**10x Scaling Plan Summary:**

```
┌────────────────────────┬─────────────┬─────────────┐
│ Optimization           │ Current     │ 10x Target  │
├────────────────────────┼─────────────┼─────────────┤
│ Partitions             │ 6           │ 60          │
│ Consumers              │ 6           │ 10-60 (HPA) │
│ Throughput             │ 10K msg/sec │ 100K msg/sec│
│ Batch size             │ 100 msg     │ 500 msg     │
│ S3 write pattern       │ Per-message │ Batched     │
│ Processing time        │ 58ms/msg    │ 1ms/msg     │
│ Consumer lag           │ < 1K        │ < 10K       │
└────────────────────────┴─────────────┴─────────────┘

Result: System can handle 100K msg/sec with auto-scaling
Cost: Scales with load (efficient)
Reliability: Self-healing, auto-rebalancing
```

---

**Key Lessons:**

✅ **Partitions = Parallelism ceiling** (increase early)
✅ **Batch processing** dramatically improves throughput
✅ **I/O is the bottleneck**, not CPU (optimize I/O first)
✅ **Monitor consumer lag** (early warning system)
✅ **Auto-scale based on lag** (elastic capacity)
✅ **Test at scale** (load testing before production)

*"The beautiful thing about Kafka's design is that horizontal scaling is straightforward—add partitions, add consumers.
The hard part is optimizing per-consumer throughput, which we addressed through batching and async I/O."*

---

### **Question 4: Explain Your Approach to Microservices Observability**

**Interviewer:** *"With multiple microservices (Messaging-Gateway, Manager, Extractor cluster) running in Kubernetes,
how do you debug issues? Walk me through investigating 'Why did deal #12345 fail to process?'"*

---

**ANSWER:**

*"Great question—observability in distributed systems is challenging but critical. We use the three pillars: logs,
metrics, and traces. Let me walk through debugging this specific scenario:*

---

**Scenario: Deal #12345 Failed to Process**

**Step 1: Check the Support Dashboard (Business-Level View)**

```
User reports: "Deal #12345 missing from Redshift"

Support Dashboard shows:
├── Time window: 2026-05-12 14:00-14:15
├── Completeness: 99.8% (4,985 / 5,000 deals)
├── Missing: 15 deals ← Deal #12345 is one of them
└── Status: INCOMPLETE (Red indicator)
```

**Initial Insight:**

- Not a systemic failure (99.8% success)
- Specific deals failed
- Happened in 14:00-14:15 window

---

**Step 2: Check Grafana Metrics (System-Level View)**

**Dashboard: Data Platform Overview**

```
Time Range: 14:00-14:15

Queue Metrics:
├── Scheduled Queue depth: Normal (fluctuating 100-500)
├── Replay Queue depth: 15 ← Spike at 14:12!
├── Failed jobs: 15 ← Matches missing deals
└── Extractor pod restarts: 1 at 14:11 ← Suspicious!

Extractor Metrics:
├── extractor-pod-7: Restarted at 14:11:23
├── Processing time: Spike from 2s → 45s around 14:10
├── Database connection errors: 3 errors at 14:10-14:11
└── Memory usage: 95% before restart (OOM?)
```

**Hypothesis Forming:**

- extractor-pod-7 experienced issues around 14:10
- High memory usage → OOM kill?
- Database connection errors
- Jobs assigned to this pod likely failed

---

**Step 3: Correlation ID Tracing (Distributed Tracing)**

**Our Correlation ID Pattern:**

```
Every message gets unique correlation_id at ingestion:
correlation_id: "trade-2026-05-12-140523-12345"
                 └─ Flows through entire pipeline
```

**Search in Splunk:**

```
index=data-platform correlation_id="trade-2026-05-12-140523-12345"
| sort _time
| table _time, service, level, message
```

**Log Trace Results:**

```
14:05:23.145 [messaging-gateway] INFO  Received message from Kafka, dealId=12345, correlationId=trade-2026-05-12-140523-12345
14:05:23.167 [messaging-gateway] INFO  Persisted to S3: s3://bucket/2026/05/12/14/05/messages.json
14:05:23.201 [messaging-gateway] INFO  Kafka offset committed

14:15:01.432 [manager] INFO  Retrieved 5,000 messages from S3 for window 14:00-14:15
14:15:01.567 [manager] INFO  Created 5,000 processing jobs
14:15:01.623 [manager] INFO  Job job-12345 assigned to extractor-pod-7, correlationId=trade-2026-05-12-140523-12345

14:15:15.234 [extractor-pod-7] INFO  Processing job job-12345, dealId=12345, correlationId=trade-2026-05-12-140523-12345
14:15:16.891 [extractor-pod-7] INFO  Querying trading database for deal 12345
14:15:21.456 [extractor-pod-7] ERROR Database query timeout after 5 seconds, dealId=12345
14:15:21.478 [extractor-pod-7] WARN  Retry attempt 1/3 for job-12345
14:15:22.123 [extractor-pod-7] ERROR Database connection pool exhausted
14:15:22.145 [extractor-pod-7] ERROR OOMKiller: Process killed, pod extractor-pod-7

14:15:35.678 [extractor-pod-7] INFO  Pod restarted by Kubernetes
14:16:01.234 [manager] WARN  Job job-12345 timed out (no heartbeat from extractor-pod-7)
14:16:01.245 [manager] INFO  Marked job-12345 as FAILED, moved to Replay Queue
```

**Root Cause Identified:**

1. Job assigned to extractor-pod-7
2. Database query timeout (5 seconds)
3. Retry attempted
4. Connection pool exhausted (all connections busy)
5. Memory leak → OOM → Pod killed by Kubernetes
6. Job never completed
7. Manager detected timeout, moved to Replay Queue

---

**Step 4: Deep Dive into Extractor Pod Logs**

**Filter for extractor-pod-7 before crash:**

```
index=data-platform service=extractor pod=extractor-pod-7 _time>=14:10:00 _time<=14:11:30
| stats count by level
```

**Results:**

```
ERROR: 47 occurrences
WARN: 123 occurrences
INFO: 2,345 occurrences
```

**Examine ERROR logs:**

```
14:10:12 ERROR HikariPool - Connection is not available, request timed out after 5000ms
14:10:15 ERROR HikariPool - Connection is not available, request timed out after 5000ms
14:10:18 ERROR Database query failed: PSQLException: connection timeout
14:10:45 ERROR OutOfMemoryError: Java heap space
...
(47 similar errors in 2 minutes)
```

**Problem Clear:**

- Database connection pool exhausted
- Queries timing out
- Memory leak (heap exhaustion)
- Pod crashed

---

**Step 5: Check Database Performance**

**Redshift Query Monitoring:**

```sql
SELECT 
    query, 
    starttime, 
    endtime, 
    DATEDIFF(second, starttime, endtime) AS duration,
    querytxt
FROM stl_query
WHERE starttime >= '2026-05-12 14:10:00'
  AND duration > 5
ORDER BY starttime;
```

**Results:**

```
14:10:05 - Query duration: 67 seconds ← SLOW!
Query: SELECT * FROM trading_data WHERE deal_id = 12345

Cause: Missing index on deal_id column
      Full table scan (millions of rows)
```

**Root Cause Confirmed:**

- Slow database query (67 seconds vs expected 1 second)
- Missing index on frequently queried column
- All Extractor connection pool threads blocked waiting
- New queries can't get connections
- Memory accumulates (pending requests)
- OOM kill

---

**Step 6: AI-Enhanced Log Analysis (Our Custom Tool)**

**Alternative Approach (What We Built):**

```
Grafana Dashboard → "Analyze Failure" button

AI analyzes:
├── Last 1,000 log entries from extractor-pod-7
├── Correlation: Errors spike before OOM
├── Pattern: Database timeout → Connection exhaustion → OOM
├── Historical: Similar incident on 2026-03-15 (INCIDENT-234)

AI Output:
┌────────────────────────────────────────────────────┐
│ Root Cause: Database performance degradation      │
│                                                     │
│ Evidence:                                          │
│ - 47 connection timeout errors in 2 minutes       │
│ - Query execution time: 67s (expected <1s)        │
│ - Memory grew from 40% → 95% in 90 seconds        │
│                                                     │
│ Likely Cause:                                      │
│ Missing database index on deal_id column          │
│                                                     │
│ Suggested Fix:                                     │
│ CREATE INDEX idx_deal_id ON trading_data(deal_id);│
│                                                     │
│ Prevention:                                        │
│ - Add query performance monitoring                │
│ - Set connection timeout to 10s (not infinite)    │
│ - Increase connection pool size 20 → 50           │
│                                                     │
│ Related Incidents:                                 │
│ - INCIDENT-234 (2026-03-15): Same pattern         │
│   Resolution: Added index, fixed immediately      │
└────────────────────────────────────────────────────┘
```

**AI saved us:**

- Manual analysis: 30-60 minutes
- AI analysis: 2 minutes
- Pointed to exact fix
- Linked to historical incident

---

**Step 7: Fix and Validate**

**Immediate Fix:**

```sql
-- Add missing index
CREATE INDEX idx_deal_id ON trading_data(deal_id);

-- Verify
EXPLAIN SELECT * FROM trading_data WHERE deal_id = 12345;
-- Result: Index Scan (was: Seq Scan) ✓
```

**Replay Failed Jobs:**

```
Support Dashboard → "Replay" button for window 14:00-14:15

Manager:
├── Retrieves 15 failed jobs from Replay Queue
├── Assigns to healthy Extractors
├── Jobs complete successfully in <2 seconds each
└── Data loaded to Redshift
```

**Validation:**

```
Support Dashboard now shows:
├── Completeness: 100% (5,000 / 5,000 deals) ✓
├── Deal #12345: Present in Redshift ✓
└── Status: COMPLETE (Green)
```

---

**Step 8: Post-Mortem and Prevention**

**Post-Incident Review:**

```
Root Cause: Missing database index
Impact: 15 deals delayed by 30 minutes
Detection Time: 5 minutes (Support Dashboard alert)
Resolution Time: 25 minutes (investigation + fix + replay)

Action Items:
1. Add query performance monitoring (alert if query >5s)
2. Automated index recommendations (analyze slow queries weekly)
3. Increase Extractor connection pool size: 20 → 50
4. Add connection timeout: 10 seconds (fail fast)
5. Memory alerts at 80% (not 95%)
6. Load testing with slow database queries (chaos engineering)

Owner: Me
Deadline: Next sprint
```

---

**Observability Stack Summary:**

```
┌─────────────────────────────────────────────────────┐
│ Layer           │ Tool          │ Purpose           │
├─────────────────┼───────────────┼───────────────────┤
│ Business View   │ Support       │ Data completeness │
│                 │ Dashboard     │ High-level status │
├─────────────────┼───────────────┼───────────────────┤
│ Metrics         │ Grafana +     │ System health     │
│                 │ Prometheus    │ Performance       │
├─────────────────┼───────────────┼───────────────────┤
│ Logs            │ Splunk        │ Detailed debugging│
│                 │ (Centralized) │ Error investigation│
├─────────────────┼───────────────┼───────────────────┤
│ Traces          │ Correlation   │ Request flow      │
│                 │ IDs           │ Distributed trace │
├─────────────────┼───────────────┼───────────────────┤
│ AI Analysis     │ Custom Tool + │ Root cause        │
│                 │ Claude/GPT    │ Recommendations   │
├─────────────────┼───────────────┼───────────────────┤
│ Alerts          │ Grafana       │ Proactive         │
│                 │ Alertmanager  │ notifications     │
└─────────────────┴───────────────┴───────────────────┘
```

**Key Practices:**

✅ **Correlation IDs everywhere** (end-to-end tracing)
✅ **Structured logging** (JSON format, consistent fields)
✅ **Centralized logs** (all services → Splunk)
✅ **Business + technical metrics** (completeness + latency)
✅ **Multi-layer dashboards** (business, system, service-level)
✅ **AI-enhanced debugging** (pattern recognition, historical context)
✅ **Automated alerts** (proactive, not reactive)
✅ **Post-mortems** (learn and prevent recurrence)

*"The goal isn't just to debug faster—it's to prevent issues through observability, detect them early through
monitoring, and resolve them quickly through good tooling and automation."*

---

**I'll continue with Questions 5-10. Would you like me to proceed, or would you like to discuss any of these first four
questions further?**