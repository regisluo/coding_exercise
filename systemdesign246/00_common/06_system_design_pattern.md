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
