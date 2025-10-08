# Common System Design Patterns Reference

## **Scalability Patterns**

### **Load Balancing**
**Definition**: Distributes incoming requests across multiple servers to prevent any single server from becoming overwhelmed.

**Types:**
- **Round Robin**: Requests distributed sequentially across servers
- **Least Connections**: Routes to server with fewest active connections
- **Weighted Round Robin**: Assigns weights based on server capacity
- **Consistent Hashing**: Ensures minimal redistribution when servers are added/removed
- **IP Hash**: Routes based on client IP hash
- **Geographic**: Routes based on client location

**Components:**
- Load Balancer (Hardware/Software)
- Health Check mechanisms
- Backend server pool
- Session persistence (if needed)

**Pros:**
- Improved availability and fault tolerance
- Better resource utilization
- Horizontal scaling capability
- No single point of failure for requests

**Cons:**
- Load balancer can become bottleneck
- Session affinity complexity
- Additional infrastructure cost
- Network latency overhead

**Use Cases**: Web applications, API gateways, microservices

---

### **Caching**
**Definition**: Temporarily stores frequently accessed data in fast storage to reduce latency and backend load.

**Cache Patterns:**

#### **Cache-Aside (Lazy Loading)**
```
if (data not in cache):
    data = fetch_from_database()
    cache.set(key, data)
return data
```
**Pros**: Only requested data cached, fault tolerant
**Cons**: Cache miss penalty, potential cache stampede

#### **Write-Through**
```
cache.set(key, data)
database.save(data)
```
**Pros**: Data consistency, cache always current
**Cons**: Write latency, unnecessary cache writes

#### **Write-Behind (Write-Back)**
```
cache.set(key, data)
// Asynchronously write to database
```
**Pros**: Low write latency, batch operations
**Cons**: Risk of data loss, complexity

#### **Read-Through**
```
data = cache.get(key)  // Cache handles DB fetch if miss
```
**Pros**: Transparent to application, automatic loading
**Cons**: Initial implementation complexity

**Cache Levels:**
- Browser Cache (Client-side)
- CDN (Edge caching)
- Reverse Proxy Cache
- Application Cache
- Database Cache

**Eviction Policies:**
- **LRU (Least Recently Used)**: Good for temporal locality
- **LFU (Least Frequently Used)**: Good for frequency patterns
- **FIFO**: Simple but ignores access patterns
- **TTL (Time To Live)**: Time-based expiration

**Trade-offs:**
| Aspect | Benefit | Cost |
|--------|---------|------|
| Performance | Faster response times | Memory usage |
| Scalability | Reduced backend load | Cache consistency complexity |
| Availability | Fault tolerance | Additional failure points |

---

### **Database Scaling**

#### **Read Replicas**
**Definition**: Create read-only copies of the primary database to distribute read load.

**Architecture:**
```
Primary DB (Write) → Replication → Read Replica 1
                                → Read Replica 2
                                → Read Replica 3
```

**Pros:**
- Improved read performance
- High availability for reads
- Geographically distributed reads

**Cons:**
- Read-after-write consistency issues
- Replication lag
- Increased complexity

#### **Sharding (Horizontal Partitioning)**
**Definition**: Split data across multiple databases based on a partition key.

**Sharding Strategies:**
- **Range-based**: Partition by value ranges (e.g., A-M, N-Z)
- **Hash-based**: Use hash function on partition key
- **Directory-based**: Lookup service maps keys to shards
- **Geographic**: Partition by user location

**Pros:**
- Linear scalability
- Fault isolation
- Improved performance

**Cons:**
- Complex queries across shards
- Rebalancing difficulty
- Application complexity

#### **Partitioning (Vertical)**
**Definition**: Split tables into smaller tables with fewer columns.

**Example:**
```
Users Table → Basic Info (frequently accessed)
           → Extended Info (rarely accessed)
```

#### **Federation**
**Definition**: Split databases by function/feature.

**Example:**
```
User Service → User Database
Order Service → Order Database
Product Service → Product Database
```

---

### **Asynchronous Processing**

#### **Message Queues**
**Definition**: Enable asynchronous communication between services through queues.

**Queue Types:**
- **FIFO Queues**: First-in, first-out processing
- **Priority Queues**: Process by priority levels
- **Delay Queues**: Messages available after delay
- **Dead Letter Queues**: Handle failed messages

**Technologies**: RabbitMQ, Apache Kafka, Amazon SQS, Redis

#### **Event Streams**
**Definition**: Continuous flow of events for real-time processing.

**Characteristics:**
- Append-only log structure
- Ordered event sequence
- Multiple consumers possible
- Event replay capability

#### **Batch Processing**
**Definition**: Process large volumes of data in scheduled batches.

**Use Cases**: ETL operations, report generation, data analytics

---

## **Reliability Patterns**

### **Circuit Breaker**
**Definition**: Prevents cascading failures by monitoring failure rates and stopping requests to failing services.

**States:**
1. **Closed**: Normal operation, requests pass through
2. **Open**: Failure threshold reached, requests fail fast
3. **Half-Open**: Testing if service recovered

**Implementation:**
```python
class CircuitBreaker:
    def __init__(self, failure_threshold=5, timeout=60):
        self.failure_count = 0
        self.failure_threshold = failure_threshold
        self.timeout = timeout
        self.last_failure_time = None
        self.state = 'CLOSED'
    
    def call(self, func):
        if self.state == 'OPEN':
            if time.now() - self.last_failure_time > self.timeout:
                self.state = 'HALF_OPEN'
            else:
                raise CircuitBreakerException()
        
        try:
            result = func()
            self.reset()
            return result
        except Exception:
            self.record_failure()
            raise
```

**Pros:**
- Prevents cascade failures
- Fast failure detection
- Automatic recovery testing

**Cons:**
- Additional complexity
- Potential false positives
- Configuration sensitivity

---

### **Bulkhead Pattern**
**Definition**: Isolates critical resources to prevent failure in one area from affecting others.

**Types:**
- **Thread Pool Isolation**: Separate thread pools for different operations
- **Connection Pool Isolation**: Dedicated database connections
- **Service Isolation**: Separate services for critical functions

**Example:**
```
Critical API → Dedicated Thread Pool (10 threads)
Regular API → Shared Thread Pool (50 threads)
Background Jobs → Separate Thread Pool (20 threads)
```

---

### **Retry with Backoff**
**Definition**: Automatically retry failed operations with increasing delays.

**Strategies:**
- **Fixed Delay**: Same delay between retries
- **Linear Backoff**: Linearly increasing delays
- **Exponential Backoff**: Exponentially increasing delays
- **Jittered Backoff**: Add randomness to prevent thundering herd

**Implementation:**
```python
def exponential_backoff_retry(func, max_retries=3):
    for attempt in range(max_retries):
        try:
            return func()
        except Exception as e:
            if attempt == max_retries - 1:
                raise e
            delay = (2 ** attempt) + random.uniform(0, 1)
            time.sleep(delay)
```

---

### **Health Checks**
**Definition**: Regular monitoring of service health to enable automatic recovery.

**Types:**
- **Liveness Probe**: Is the service running?
- **Readiness Probe**: Is the service ready to handle requests?
- **Startup Probe**: Has the service finished starting up?

**Implementation Levels:**
- Infrastructure (Load balancer health checks)
- Application (Service health endpoints)
- Business Logic (Domain-specific health indicators)

---

## **Data Patterns**

### **CQRS (Command Query Responsibility Segregation)**
**Definition**: Separate read and write models to optimize for different access patterns.

**Architecture:**
```
Commands → Command Model → Write Database
Queries  → Query Model   → Read Database
                ↑
         Event Synchronization
```

**Pros:**
- Optimized read/write performance
- Independent scaling
- Complex query support
- Event sourcing compatibility

**Cons:**
- Eventual consistency
- Increased complexity
- Data synchronization overhead

---

### **Event Sourcing**
**Definition**: Store events that led to current state instead of storing current state directly.

**Components:**
- **Event Store**: Immutable event log
- **Event Handlers**: Process events to update read models
- **Snapshots**: Periodic state snapshots for performance

**Benefits:**
- Complete audit trail
- Event replay capability
- Temporal queries
- Natural integration with CQRS

**Challenges:**
- Query complexity
- Event schema evolution
- Storage overhead

---

### **Saga Pattern**
**Definition**: Manage distributed transactions through a series of local transactions with compensating actions.

**Types:**

#### **Choreography-based Saga**
Each service publishes events and listens to other services' events.

#### **Orchestration-based Saga**
Central orchestrator coordinates the transaction.

**Example - Order Processing Saga:**
```
1. Reserve Inventory → Success
2. Process Payment → Success  
3. Ship Order → Failure
4. Compensate: Refund Payment
5. Compensate: Release Inventory
```

---

### **Database per Service**
**Definition**: Each microservice owns its data and database schema.

**Benefits:**
- Service independence
- Technology diversity
- Fault isolation
- Team autonomy

**Challenges:**
- Data consistency across services
- Complex queries spanning services
- Data synchronization
- Transaction management

---

## **Communication Patterns**

### **API Gateway**
**Definition**: Single entry point that handles cross-cutting concerns and routes requests to appropriate services.

**Responsibilities:**
- Request routing and composition
- Authentication and authorization
- Rate limiting and throttling
- Request/response transformation
- Monitoring and analytics
- Protocol translation

**Benefits:**
- Simplified client interface
- Centralized cross-cutting concerns
- Service evolution flexibility
- Security enforcement

**Drawbacks:**
- Potential bottleneck
- Single point of failure
- Additional complexity

---

### **Service Mesh**
**Definition**: Infrastructure layer that handles service-to-service communication with features like discovery, load balancing, encryption, and observability.

**Components:**
- **Data Plane**: Sidecar proxies (Envoy)
- **Control Plane**: Configuration management (Istio, Linkerd)

**Features:**
- Traffic management
- Security (mTLS)
- Observability
- Policy enforcement

---

### **Event-Driven Architecture**
**Definition**: Services communicate through events, promoting loose coupling.

**Patterns:**
- **Event Notification**: Simple event announcements
- **Event-Carried State Transfer**: Events contain state data
- **Event Sourcing**: Events as source of truth

**Benefits:**
- Loose coupling
- Scalability
- Resilience
- Flexibility

**Challenges:**
- Eventual consistency
- Complex debugging
- Event ordering issues

---

### **Request-Response vs Fire-and-Forget**

#### **Request-Response (Synchronous)**
- Immediate response required
- Strong consistency
- Simple error handling
- Higher latency and coupling

#### **Fire-and-Forget (Asynchronous)**
- No immediate response needed
- Eventual consistency
- Better performance and scalability
- Complex error handling

**Decision Factors:**
- Consistency requirements
- Performance needs
- Error handling complexity
- System coupling tolerance

---

## **Pattern Selection Guidelines**

### **Scalability Needs:**
- **High Read Load**: Read replicas, caching, CDN
- **High Write Load**: Sharding, write-behind caching
- **Global Scale**: Geographic distribution, edge caching

### **Reliability Requirements:**
- **High Availability**: Circuit breakers, bulkheads, health checks
- **Fault Tolerance**: Retry mechanisms, graceful degradation
- **Data Durability**: Replication, backup strategies

### **Consistency Requirements:**
- **Strong Consistency**: ACID transactions, synchronous patterns
- **Eventual Consistency**: Event-driven, async patterns, CQRS
- **Session Consistency**: Sticky sessions, read-your-writes

### **Performance Priorities:**
- **Low Latency**: Caching, async processing, geographic distribution
- **High Throughput**: Load balancing, horizontal scaling, batching
- **Resource Efficiency**: Connection pooling, caching, compression

This reference provides the foundational patterns for building scalable, reliable, and maintainable distributed systems. Choose patterns based on specific requirements, constraints, and trade-offs of your system design context.
