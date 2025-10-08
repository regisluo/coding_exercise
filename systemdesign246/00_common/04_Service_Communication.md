# Service Communication Patterns for System Design Interviews

## Overview
In distributed systems, services need to communicate with each other to fulfill business requirements. Understanding different communication patterns and their trade-offs is crucial for system design interviews, especially for senior-level positions.

---

## **1. SYNCHRONOUS COMMUNICATION**

### **REST (Representational State Transfer)**

#### **Characteristics:**
- HTTP-based communication
- Request-response pattern
- Stateless operations
- Human-readable (JSON/XML)

#### **When to Use:**
- Real-time operations requiring immediate response
- Simple CRUD operations
- Public APIs and web services
- When human readability is important

#### **Example Implementation:**
```http
GET /api/v1/users/123
POST /api/v1/orders
{
  "user_id": "123",
  "items": [{"product_id": "456", "quantity": 2}],
  "total": 99.99
}

Response: 201 Created
{
  "order_id": "789",
  "status": "pending",
  "created_at": "2025-10-07T10:30:00Z"
}
```

#### **Pros:**
- Simple to understand and implement
- Wide tooling and library support
- Cacheable responses
- Stateless and scalable

#### **Cons:**
- Higher latency due to HTTP overhead
- Text-based format increases payload size
- Limited real-time capabilities
- Tight coupling between services

#### **Best Practices:**
- Use proper HTTP status codes (200, 201, 400, 404, 500)
- Implement pagination for large datasets
- Version your APIs (/v1/, /v2/)
- Use HTTPS for security
- Implement rate limiting and authentication

---

### **gRPC (Google Remote Procedure Call)**

#### **Characteristics:**
- Binary protocol using Protocol Buffers
- HTTP/2 based with multiplexing
- Strongly typed contracts
- Built-in load balancing and retries

#### **When to Use:**
- High-performance internal service communication
- Real-time operations with low latency requirements
- Polyglot environments (multiple programming languages)
- When type safety is crucial

#### **Example Implementation:**
```protobuf
// user.proto
service UserService {
  rpc GetUser(GetUserRequest) returns (User);
  rpc CreateUser(CreateUserRequest) returns (User);
}

message User {
  string id = 1;
  string name = 2;
  string email = 3;
  int64 created_at = 4;
}
```

#### **Pros:**
- High performance (binary serialization)
- Strong typing with code generation
- Built-in streaming capabilities
- Cross-language support
- HTTP/2 features (multiplexing, compression)

#### **Cons:**
- Less human-readable than REST
- Requires schema management
- Limited browser support
- More complex debugging

#### **Performance Comparison:**
| Aspect | REST | gRPC |
|--------|------|------|
| Payload Size | Larger (JSON) | Smaller (Binary) |
| Serialization | Slower | Faster |
| Latency | Higher | Lower |
| Throughput | Lower | Higher |
| Debugging | Easier | More Complex |

---

### **Synchronous Communication Trade-offs**

#### **Advantages:**
- Immediate consistency
- Simple error handling
- Easy to understand and debug
- Direct request-response flow

#### **Disadvantages:**
- Tight coupling between services
- Cascading failures
- Lower availability (dependent on all services)
- Higher latency for complex workflows

#### **Interview Considerations:**
```
**Interviewer:** When would you choose REST over gRPC?

**Candidate:** I'd choose REST for:
- Public APIs where human readability matters
- Simple CRUD operations
- When teams are more familiar with HTTP/JSON
- Browser-based clients

I'd choose gRPC for:
- High-performance internal service communication
- Real-time systems requiring low latency
- Type-safe contracts between services
- Streaming data requirements
```

---

## **2. ASYNCHRONOUS COMMUNICATION**

### **Message Queues**

#### **Characteristics:**
- Producer-consumer pattern
- Decoupled communication
- Guaranteed delivery options
- Temporary message storage

#### **Common Implementations:**
1. **RabbitMQ**: Feature-rich, supports multiple messaging patterns
2. **Apache Kafka**: High-throughput, distributed streaming platform
3. **Amazon SQS**: Managed queue service
4. **Redis Pub/Sub**: In-memory messaging

#### **When to Use:**
- Event-driven workflows
- Decoupling services
- Handling traffic spikes
- Background processing
- Non-critical operations that can be processed later

#### **Example Implementation:**
```json
// Order Created Event
{
  "event_type": "order.created",
  "event_id": "uuid-123",
  "timestamp": "2025-10-07T10:30:00Z",
  "data": {
    "order_id": "789",
    "user_id": "123",
    "total": 99.99,
    "items": [...]
  }
}

// Event Flow:
// 1. Order Service publishes "order.created" event
// 2. Inventory Service consumes event → Updates stock
// 3. Payment Service consumes event → Processes payment
// 4. Notification Service consumes event → Sends confirmation email
```

#### **Message Queue Patterns:**

##### **1. Point-to-Point (Queue)**
```
Producer → [Queue] → Consumer
- One message consumed by one consumer
- Use for task distribution
```

##### **2. Publish-Subscribe (Topic)**
```
Producer → [Topic] → Consumer 1
                  → Consumer 2
                  → Consumer 3
- One message consumed by multiple consumers
- Use for event broadcasting
```

##### **3. Request-Reply**
```
Requestor → [Request Queue] → Service
         ← [Reply Queue]   ←
- Asynchronous request-response
- Use for non-blocking operations
```

#### **Pros:**
- Loose coupling between services
- Better fault tolerance
- Scalability and load balancing
- Reliability with message persistence

#### **Cons:**
- Eventual consistency
- Complex error handling
- Message ordering challenges
- Potential message duplication

---

### **Event Streaming (Apache Kafka)**

#### **Characteristics:**
- Distributed commit log
- High throughput and low latency
- Fault-tolerant and scalable
- Event sourcing capabilities

#### **When to Use:**
- Real-time data processing
- Event sourcing architectures
- Log aggregation
- Stream processing

#### **Example Implementation:**
```java
// Producer
Producer<String, String> producer = new KafkaProducer<>(props);
producer.send(new ProducerRecord<>("user-events", userId, eventData));

// Consumer
KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
consumer.subscribe(Arrays.asList("user-events"));
while (true) {
    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
    for (ConsumerRecord<String, String> record : records) {
        processEvent(record.key(), record.value());
    }
}
```

#### **Kafka vs Traditional Message Queues:**
| Aspect | Kafka | Traditional MQ |
|--------|-------|----------------|
| Throughput | Very High | Moderate |
| Latency | Low | Variable |
| Persistence | Long-term | Short-term |
| Ordering | Partition-level | Queue-level |
| Scalability | Horizontal | Vertical/Limited |

---

## **3. DATA CONSISTENCY PATTERNS**

### **Saga Pattern for Distributed Transactions**

#### **Problem:**
- ACID transactions don't work across multiple services
- Two-phase commit (2PC) is complex and has performance issues
- Need to maintain data consistency in distributed systems

#### **Saga Solution:**
A saga is a sequence of local transactions where each transaction updates data within a single service. If a transaction fails, the saga executes compensating transactions to undo the changes.

#### **Types of Saga Patterns:**

##### **1. Choreography-based Saga**
```
Order Service → (order.created) → Inventory Service
                                ↓ (inventory.reserved)
Payment Service ← (payment.requested) ← Email Service
     ↓ (payment.completed)
Shipping Service
```

**Characteristics:**
- Services coordinate through events
- No central coordinator
- Each service knows what events to listen for

**Example Implementation:**
```java
// Order Service
public void createOrder(Order order) {
    // 1. Save order locally
    orderRepository.save(order);
    
    // 2. Publish event
    eventPublisher.publish(new OrderCreatedEvent(order));
}

// Inventory Service
@EventHandler
public void handle(OrderCreatedEvent event) {
    try {
        inventoryService.reserveItems(event.getItems());
        eventPublisher.publish(new InventoryReservedEvent(event.getOrderId()));
    } catch (InsufficientStockException e) {
        eventPublisher.publish(new InventoryReservationFailedEvent(event.getOrderId()));
    }
}

// Payment Service - Compensation
@EventHandler
public void handle(InventoryReservationFailedEvent event) {
    // Compensate: Cancel the order
    orderService.cancelOrder(event.getOrderId());
}
```

##### **2. Orchestration-based Saga**
```
Saga Orchestrator
    ↓ (reserve inventory)
Inventory Service
    ↓ (process payment)
Payment Service
    ↓ (ship order)
Shipping Service
```

**Characteristics:**
- Central orchestrator manages the saga
- Orchestrator tells services what to do
- Better visibility and control

**Example Implementation:**
```java
// Saga Orchestrator
public class OrderSagaOrchestrator {
    
    public void processOrder(Order order) {
        SagaTransaction saga = new SagaTransaction()
            .addStep(
                () -> inventoryService.reserveItems(order.getItems()),
                () -> inventoryService.releaseItems(order.getItems())
            )
            .addStep(
                () -> paymentService.processPayment(order.getPayment()),
                () -> paymentService.refundPayment(order.getPayment())
            )
            .addStep(
                () -> shippingService.createShipment(order),
                () -> shippingService.cancelShipment(order.getId())
            );
            
        saga.execute();
    }
}
```

#### **Saga Pattern Comparison:**
| Aspect | Choreography | Orchestration |
|--------|--------------|---------------|
| Coupling | Loose | Tight |
| Complexity | Distributed | Centralized |
| Visibility | Limited | High |
| Testing | Harder | Easier |
| Scalability | Better | Limited |

#### **Compensation Strategies:**
1. **Semantic Rollback**: Undo business operations (cancel order, refund payment)
2. **Backward Recovery**: Execute compensating transactions in reverse order
3. **Forward Recovery**: Continue saga execution with alternative paths

---

## **4. INTERVIEW SCENARIOS AND TRADE-OFFS**

### **Choosing Communication Patterns**

#### **Scenario 1: E-commerce Order Processing**
```
**Interviewer:** How would you design communication for an order processing system?

**Candidate:** I'd use a hybrid approach:

1. **Synchronous (REST)** for:
   - User authentication and authorization
   - Real-time inventory checks before order placement
   - Payment gateway integration (immediate response needed)

2. **Asynchronous (Message Queues)** for:
   - Order fulfillment workflow
   - Inventory updates
   - Email notifications
   - Analytics and reporting

3. **Saga Pattern** for:
   - Distributed transaction across Order, Inventory, Payment, and Shipping services
   - Ensuring data consistency without distributed transactions
```

#### **Scenario 2: Real-time Chat System**
```
**Interviewer:** What communication patterns for a chat system like Slack?

**Candidate:** 

1. **WebSocket/Server-Sent Events** for:
   - Real-time message delivery
   - Presence indicators
   - Typing notifications

2. **REST APIs** for:
   - User management
   - Channel creation and management
   - Message history retrieval

3. **Message Queues** for:
   - Message persistence
   - Push notifications for offline users
   - Message indexing for search

4. **Event Streaming** for:
   - Real-time analytics
   - Message ordering guarantees
   - Audit logging
```

### **Performance Considerations**

#### **Latency Requirements:**
- **< 50ms**: Use synchronous communication (gRPC)
- **< 200ms**: REST APIs acceptable
- **> 200ms**: Consider asynchronous patterns

#### **Throughput Requirements:**
- **High throughput**: Message queues, event streaming
- **Moderate throughput**: REST APIs with caching
- **Low throughput**: Any pattern works

#### **Consistency Requirements:**
- **Strong consistency**: Synchronous communication
- **Eventual consistency**: Asynchronous patterns with saga
- **Mixed**: Hybrid approach based on operation criticality

---

## **5. BEST PRACTICES FOR INTERVIEWS**

### **Communication Pattern Selection Framework:**
```
1. **Identify the requirement:**
   - Real-time vs eventual consistency
   - Latency requirements
   - Coupling tolerance
   - Failure handling needs

2. **Consider the trade-offs:**
   - Performance vs complexity
   - Consistency vs availability
   - Coupling vs independence

3. **Design for evolution:**
   - Start simple (REST)
   - Add complexity as needed (async patterns)
   - Plan migration paths
```

### **Common Interview Questions:**
1. "How do you handle service communication in a microservices architecture?"
2. "What's the difference between REST and gRPC? When would you use each?"
3. "How do you maintain data consistency across multiple services?"
4. "Explain the Saga pattern and its alternatives"
5. "How do you handle failures in distributed systems?"

### **Red Flags to Avoid:**
- Using only synchronous communication for all operations
- Not considering failure scenarios
- Ignoring eventual consistency implications
- Over-engineering with complex patterns for simple use cases
- Not discussing monitoring and observability

---

## **Summary**

**Service communication is about choosing the right tool for the job:**

- **Synchronous (REST/gRPC)**: When you need immediate responses and can tolerate tight coupling
- **Asynchronous (Message Queues)**: When you need loose coupling and can accept eventual consistency
- **Saga Pattern**: When you need distributed transactions without the complexity of 2PC

**Key Interview Points:**
- Always discuss trade-offs explicitly
- Consider failure scenarios and recovery mechanisms
- Think about monitoring and observability
- Design for evolution and changing requirements
- Connect technical decisions to business requirements
