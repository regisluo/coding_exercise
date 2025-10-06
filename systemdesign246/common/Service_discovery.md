# Service Discovery & Resilience Patterns for System Design Interviews

## Overview
In distributed microservices architectures, services need to discover and communicate with each other reliably. Service discovery and resilience patterns are critical for building fault-tolerant, scalable systems. Understanding these patterns is essential for senior-level system design interviews.

---

## **1. SERVICE DISCOVERY**

### **What is Service Discovery?**
Service discovery is the mechanism by which services find and communicate with each other in a distributed system. It solves the problem of dynamic service locations, load balancing, and health checking.

### **Service Discovery Patterns**

#### **Client-Side Discovery**
```
┌─────────────┐    1. Query Registry    ┌─────────────────┐
│   Client    │────────────────────────▶│ Service Registry│
│             │◀────────────────────────│                 │
└─────────────┘    2. Return Locations  └─────────────────┘
        │
        │ 3. Direct Call
        ▼
┌─────────────┐
│ Service A   │
└─────────────┘
```

**Characteristics:**
- Client queries service registry directly
- Client handles load balancing logic
- Examples: Netflix Eureka, Consul

**Pros:**
- Simple and straightforward
- Client has full control over load balancing
- Lower latency (no proxy)

**Cons:**
- Client logic becomes complex
- Tight coupling with discovery mechanism
- Multiple language implementations needed

#### **Server-Side Discovery**
```
┌─────────────┐    1. Request     ┌─────────────┐    2. Query Registry    ┌─────────────────┐
│   Client    │──────────────────▶│Load Balancer│────────────────────────▶│ Service Registry│
│             │                   │             │◀────────────────────────│                 │
└─────────────┘                   └─────────────┘    3. Return Locations  └─────────────────┘
                                          │
                                          │ 4. Route Request
                                          ▼
                                  ┌─────────────┐
                                  │ Service A   │
                                  └─────────────┘
```

**Characteristics:**
- Load balancer/proxy handles discovery
- Client makes requests through intermediary
- Examples: AWS ALB, Kubernetes Services

**Pros:**
- Simpler client implementation
- Centralized load balancing logic
- Language-agnostic

**Cons:**
- Additional network hop
- Load balancer can become bottleneck
- Single point of failure

### **Service Registry Implementations**

#### **1. Consul (HashiCorp)**
```json
// Service Registration
{
  "ID": "user-service-1",
  "Name": "user-service",
  "Tags": ["v1.0", "primary"],
  "Address": "192.168.1.100",
  "Port": 8080,
  "Check": {
    "HTTP": "http://192.168.1.100:8080/health",
    "Interval": "10s"
  }
}

// Service Discovery Query
curl http://consul:8500/v1/catalog/service/user-service
```

**Features:**
- Built-in health checking
- Key-value store
- Multi-datacenter support
- DNS interface

#### **2. Netflix Eureka**
```java
// Service Registration
@SpringBootApplication
@EnableEurekaClient
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}

// Configuration
eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka-server:8761/eureka/
  instance:
    preferIpAddress: true
    healthCheckUrlPath: /actuator/health
```

#### **3. Kubernetes Services**
```yaml
apiVersion: v1
kind: Service
metadata:
  name: user-service
spec:
  selector:
    app: user-service
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
  type: ClusterIP
```

### **Health Checking Strategies**

#### **Active Health Checks**
```java
// Spring Boot Actuator
@RestController
public class HealthController {
    
    @Autowired
    private DatabaseHealthIndicator dbHealth;
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        
        // Check database connectivity
        if (dbHealth.isHealthy()) {
            status.put("status", "UP");
            status.put("database", "UP");
        } else {
            status.put("status", "DOWN");
            status.put("database", "DOWN");
            return ResponseEntity.status(503).body(status);
        }
        
        return ResponseEntity.ok(status);
    }
}
```

#### **Passive Health Checks**
- Monitor actual request success/failure rates
- Remove unhealthy instances based on error rates
- Gradual traffic reduction for degraded services

---

## **2. SERVICE MESH**

### **What is a Service Mesh?**
A service mesh is a dedicated infrastructure layer that handles service-to-service communication, providing features like load balancing, service discovery, encryption, observability, and traffic management.

### **Service Mesh Architecture**
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Service A  │    │  Service B  │    │  Service C  │
│             │    │             │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
        │                  │                  │
        ▼                  ▼                  ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Sidecar     │    │ Sidecar     │    │ Sidecar     │
│ Proxy       │◀──▶│ Proxy       │◀──▶│ Proxy       │
└─────────────┘    └─────────────┘    └─────────────┘
        │                  │                  │
        └──────────────────┼──────────────────┘
                           │
                           ▼
                ┌─────────────────┐
                │ Control Plane   │
                │ (Configuration, │
                │  Policies,      │
                │  Telemetry)     │
                └─────────────────┘
```

### **Popular Service Mesh Solutions**

#### **1. Istio**
```yaml
# Virtual Service for Traffic Routing
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: user-service
spec:
  http:
  - match:
    - headers:
        version:
          exact: v2
    route:
    - destination:
        host: user-service
        subset: v2
      weight: 100
  - route:
    - destination:
        host: user-service
        subset: v1
      weight: 90
    - destination:
        host: user-service
        subset: v2
      weight: 10
```

#### **2. Linkerd**
```yaml
# Traffic Split for Canary Deployment
apiVersion: split.smi-spec.io/v1alpha1
kind: TrafficSplit
metadata:
  name: user-service-split
spec:
  service: user-service
  backends:
  - service: user-service-v1
    weight: 90
  - service: user-service-v2
    weight: 10
```

### **Service Mesh Benefits**

#### **Communication Management:**
- **Load Balancing**: Automatic distribution of traffic
- **Service Discovery**: Dynamic service location
- **Traffic Routing**: Advanced routing rules and policies
- **Protocol Support**: HTTP, gRPC, TCP, WebSocket

#### **Security:**
- **mTLS**: Automatic mutual TLS between services
- **Authentication**: Service-to-service identity verification
- **Authorization**: Fine-grained access control policies

#### **Observability:**
- **Distributed Tracing**: Request flow across services
- **Metrics Collection**: Automatic gathering of traffic metrics
- **Logging**: Centralized access logs

#### **Interview Example:**
```
**Interviewer:** How would you handle service communication in a microservices architecture?

**Candidate:** I'd implement a service mesh like Istio for several reasons:

1. **Centralized Communication Logic**: Instead of implementing load balancing, retries, and circuit breakers in each service, the mesh handles this at the infrastructure layer.

2. **Security**: Automatic mTLS encryption between services without code changes.

3. **Observability**: Built-in metrics, tracing, and logging for all service communications.

4. **Traffic Management**: Easy canary deployments and A/B testing through configuration.

The trade-off is additional operational complexity, but for a large microservices deployment, the benefits outweigh the costs.
```

---

## **3. CIRCUIT BREAKER PATTERN**

### **Problem Statement**
In distributed systems, services can fail or become slow. Without protection, one failing service can cascade failures throughout the system, causing a complete outage.

### **Circuit Breaker States**
```
┌─────────────┐    Failure Rate    ┌─────────────┐
│   CLOSED    │   Exceeds         │    OPEN     │
│             │   Threshold       │             │
│ (Normal     │──────────────────▶│ (Failing    │
│  Operation) │                   │  Fast)      │
└─────────────┘                   └─────────────┘
        ▲                                 │
        │                                 │ Timeout
        │ Success Rate                    │ Period
        │ Above Threshold                 │ Expires
        │                                 ▼
┌─────────────┐                   ┌─────────────┐
│ HALF-OPEN   │◀──────────────────│             │
│             │                   │             │
│ (Testing    │                   │             │
│  Recovery)  │                   │             │
└─────────────┘                   └─────────────┘
```

### **Implementation Examples**

#### **Netflix Hystrix (Java)**
```java
@Component
public class UserServiceClient {
    
    @HystrixCommand(
        fallbackMethod = "getDefaultUser",
        commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000")
        }
    )
    public User getUser(String userId) {
        return restTemplate.getForObject("/users/" + userId, User.class);
    }
    
    public User getDefaultUser(String userId) {
        return new User(userId, "Default User", "default@example.com");
    }
}
```

#### **Resilience4j (Modern Alternative)**
```java
@Service
public class UserServiceClient {
    
    private final CircuitBreaker circuitBreaker;
    
    public UserServiceClient() {
        this.circuitBreaker = CircuitBreaker.ofDefaults("userService");
        circuitBreaker.getEventPublisher()
            .onStateTransition(event -> 
                log.info("Circuit breaker state transition: {}", event));
    }
    
    public User getUser(String userId) {
        Supplier<User> decoratedSupplier = CircuitBreaker
            .decorateSupplier(circuitBreaker, () -> {
                return restTemplate.getForObject("/users/" + userId, User.class);
            });
            
        return Try.ofSupplier(decoratedSupplier)
            .recover(throwable -> getDefaultUser(userId))
            .get();
    }
}
```

#### **Configuration Parameters**
```yaml
# Resilience4j Configuration
resilience4j:
  circuitbreaker:
    instances:
      userService:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        permittedNumberOfCallsInHalfOpenState: 3
        waitDurationInOpenState: 5s
        failureRateThreshold: 50
        eventConsumerBufferSize: 10
```

### **Circuit Breaker Benefits**
- **Fail Fast**: Prevents cascading failures
- **Resource Protection**: Reduces load on failing services
- **Automatic Recovery**: Tests service health periodically
- **Fallback Support**: Provides alternative responses

### **Interview Considerations**
```
**Interviewer:** How do you prevent cascading failures in your system?

**Candidate:** I'd implement circuit breakers with these considerations:

1. **Threshold Configuration**: Set failure rate (50%) and minimum requests (10) based on service SLAs
2. **Timeout Settings**: Balance between giving services time to recover (5s) vs. user experience
3. **Fallback Strategies**: 
   - Return cached data
   - Return default values
   - Degrade functionality gracefully
4. **Monitoring**: Alert on circuit breaker state changes
5. **Testing**: Regularly test circuit breaker behavior in staging
```

---

## **4. RETRY POLICIES WITH EXPONENTIAL BACKOFF**

### **Problem Statement**
Network requests can fail due to transient issues (network hiccups, temporary service overload). Simple retry mechanisms can overwhelm struggling services.

### **Exponential Backoff Algorithm**
```
Retry Delay = Base Delay × (2^attempt) + Random Jitter

Example:
Attempt 1: 100ms + jitter
Attempt 2: 200ms + jitter  
Attempt 3: 400ms + jitter
Attempt 4: 800ms + jitter
Attempt 5: 1600ms + jitter (capped at max)
```

### **Implementation Examples**

#### **Spring Retry**
```java
@Service
public class UserServiceClient {
    
    @Retryable(
        value = {ConnectException.class, SocketTimeoutException.class},
        maxAttempts = 3,
        backoff = @Backoff(
            delay = 1000,
            multiplier = 2,
            maxDelay = 10000,
            random = true
        )
    )
    public User getUser(String userId) throws Exception {
        log.info("Attempting to fetch user: {}", userId);
        return restTemplate.getForObject("/users/" + userId, User.class);
    }
    
    @Recover
    public User recover(Exception ex, String userId) {
        log.error("All retry attempts failed for user: {}", userId, ex);
        return getDefaultUser(userId);
    }
}
```

#### **Custom Implementation with Jitter**
```java
public class RetryService {
    
    private static final int MAX_ATTEMPTS = 5;
    private static final long BASE_DELAY_MS = 100;
    private static final long MAX_DELAY_MS = 10000;
    private static final Random random = new Random();
    
    public <T> T executeWithRetry(Supplier<T> operation, Class<? extends Exception>... retryableExceptions) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                return operation.get();
            } catch (Exception e) {
                lastException = e;
                
                if (attempt == MAX_ATTEMPTS || !isRetryableException(e, retryableExceptions)) {
                    break;
                }
                
                long delay = calculateDelay(attempt);
                log.warn("Attempt {} failed, retrying in {}ms", attempt, delay, e);
                
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during retry", ie);
                }
            }
        }
        
        throw new RuntimeException("All retry attempts failed", lastException);
    }
    
    private long calculateDelay(int attempt) {
        long exponentialDelay = BASE_DELAY_MS * (1L << (attempt - 1));
        long cappedDelay = Math.min(exponentialDelay, MAX_DELAY_MS);
        
        // Add jitter (±25% of the delay)
        long jitter = (long) (cappedDelay * 0.25 * (random.nextDouble() * 2 - 1));
        return cappedDelay + jitter;
    }
}
```

### **Retry Strategy Considerations**

#### **Idempotency**
```java
// Idempotent Operations (Safe to Retry)
GET /users/123          ✅ Safe to retry
PUT /users/123 {...}    ✅ Safe to retry (same result)
DELETE /users/123       ✅ Safe to retry (same result)

// Non-Idempotent Operations (Careful with Retry)
POST /users {...}       ❌ May create duplicates
POST /orders {...}      ❌ May charge customer multiple times
```

#### **Retry-Specific Headers**
```http
# Request Headers
Idempotency-Key: 550e8400-e29b-41d4-a716-446655440000
X-Request-ID: req-123456

# Response Headers (from service)
Retry-After: 300        # Retry after 5 minutes
X-RateLimit-Reset: 1609459200
```

### **Advanced Retry Patterns**

#### **Circuit Breaker + Retry Combination**
```java
@Service
public class ResilientUserClient {
    
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;
    
    public User getUser(String userId) {
        Supplier<User> decoratedSupplier = Decorators
            .ofSupplier(() -> restTemplate.getForObject("/users/" + userId, User.class))
            .withCircuitBreaker(circuitBreaker)
            .withRetry(retry)
            .decorate();
            
        return Try.ofSupplier(decoratedSupplier)
            .recover(throwable -> getDefaultUser(userId))
            .get();
    }
}
```

---

## **5. BULKHEAD PATTERN**

### **Problem Statement**
In distributed systems, failure in one component can consume all available resources, affecting other unrelated components. The bulkhead pattern isolates critical resources.

### **Bulkhead Pattern Concept**
```
Ship Without Bulkheads:
┌─────────────────────────────────────┐
│ ████████████████████████████████████ │ ← Single compartment
│                 💧LEAK              │   (one leak sinks ship)
└─────────────────────────────────────┘

Ship With Bulkheads:
┌────────┬────────┬────────┬────────┐
│ ██████ │ ██████ │ 💧LEAK │ ██████ │ ← Multiple compartments
│        │        │        │        │   (isolated failure)
└────────┴────────┴────────┴────────┘
```

### **Resource Isolation Strategies**

#### **1. Thread Pool Isolation**
```java
@Configuration
public class ThreadPoolConfig {
    
    @Bean("userServiceExecutor")
    public ThreadPoolTaskExecutor userServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("UserService-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
    
    @Bean("orderServiceExecutor")
    public ThreadPoolTaskExecutor orderServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("OrderService-");
        return executor;
    }
}

@Service
public class UserServiceClient {
    
    @Async("userServiceExecutor")
    public CompletableFuture<User> getUserAsync(String userId) {
        // User service calls use dedicated thread pool
        return CompletableFuture.completedFuture(
            restTemplate.getForObject("/users/" + userId, User.class)
        );
    }
}
```

#### **2. Connection Pool Isolation**
```java
@Configuration
public class DataSourceConfig {
    
    @Bean("userDatabase")
    @Primary
    public DataSource userDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://user-db:3306/users");
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setPoolName("UserDB-Pool");
        return new HikariDataSource(config);
    }
    
    @Bean("analyticsDatabase")
    public DataSource analyticsDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://analytics-db:3306/analytics");
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(10000);
        config.setPoolName("Analytics-Pool");
        return new HikariDataSource(config);
    }
}
```

#### **3. CPU and Memory Isolation (Kubernetes)**
```yaml
# Critical User Service
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
spec:
  template:
    spec:
      containers:
      - name: user-service
        image: user-service:v1.0
        resources:
          requests:
            memory: "256Mi"
            cpu: "500m"
          limits:
            memory: "512Mi"
            cpu: "1000m"
        
---
# Non-Critical Analytics Service
apiVersion: apps/v1
kind: Deployment
metadata:
  name: analytics-service
spec:
  template:
    spec:
      containers:
      - name: analytics-service
        image: analytics-service:v1.0
        resources:
          requests:
            memory: "128Mi"
            cpu: "100m"
          limits:
            memory: "256Mi"
            cpu: "200m"
```

### **Bulkhead Implementation Patterns**

#### **Actor Model (Akka Example)**
```java
// Each actor has isolated mailbox and thread
public class UserActor extends AbstractActor {
    
    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(GetUserMessage.class, this::handleGetUser)
            .match(UpdateUserMessage.class, this::handleUpdateUser)
            .build();
    }
    
    private void handleGetUser(GetUserMessage message) {
        // Isolated processing for user operations
        try {
            User user = userService.getUser(message.getUserId());
            getSender().tell(user, getSelf());
        } catch (Exception e) {
            getSender().tell(new Status.Failure(e), getSelf());
        }
    }
}

// Router with different strategies
ActorRef userRouter = getContext().actorOf(
    new RoundRobinPool(5).props(Props.create(UserActor.class)),
    "user-router"
);
```

#### **Semaphore-Based Resource Limiting**
```java
@Component
public class ResourceLimitedService {
    
    // Limit concurrent external API calls
    private final Semaphore externalApiSemaphore = new Semaphore(5);
    
    // Limit concurrent database operations
    private final Semaphore databaseSemaphore = new Semaphore(10);
    
    public User getUserFromExternalApi(String userId) throws InterruptedException {
        if (!externalApiSemaphore.tryAcquire(2, TimeUnit.SECONDS)) {
            throw new ResourceExhaustedException("External API calls limit exceeded");
        }
        
        try {
            return externalApiClient.getUser(userId);
        } finally {
            externalApiSemaphore.release();
        }
    }
    
    public void updateUserInDatabase(User user) throws InterruptedException {
        if (!databaseSemaphore.tryAcquire(1, TimeUnit.SECONDS)) {
            throw new ResourceExhaustedException("Database operations limit exceeded");
        }
        
        try {
            userRepository.save(user);
        } finally {
            databaseSemaphore.release();
        }
    }
}
```

### **Bulkhead Benefits**
- **Fault Isolation**: Failures in one area don't affect others
- **Resource Protection**: Critical operations get guaranteed resources
- **Performance Stability**: Prevents resource starvation
- **Graceful Degradation**: Non-critical features can be sacrificed

---

## **6. SYSTEM DESIGN INTERVIEW SCENARIOS**

### **Scenario 1: E-commerce Platform**
```
**Interviewer:** How would you ensure resilience in an e-commerce platform?

**Candidate:** I'd implement multiple resilience patterns:

1. **Service Mesh (Istio)**: 
   - Automatic service discovery and load balancing
   - mTLS for service-to-service communication
   - Traffic management for canary deployments

2. **Circuit Breakers**: 
   - Product catalog service with fallback to cached data
   - Payment service with graceful degradation
   - Recommendation service that can fail without affecting checkout

3. **Retry Policies**:
   - Inventory checks with exponential backoff
   - Order processing with idempotency keys
   - Payment processing with strict retry limits

4. **Bulkhead Isolation**:
   - Separate thread pools for critical (checkout) vs non-critical (recommendations)
   - Dedicated database connections for user data vs analytics
   - Resource limits preventing analytics from affecting core transactions
```

### **Scenario 2: Real-time Chat System**
```
**Interviewer:** How do you handle service communication in a chat system?

**Candidate:** I'd focus on these resilience aspects:

1. **Service Discovery**:
   - Dynamic discovery of chat servers for user connections
   - Health checks to ensure message delivery reliability
   - Automatic failover for connection routing

2. **Circuit Breaker for External Services**:
   - Push notification services with fallback to in-app notifications
   - File upload services with retry and alternative storage
   - User presence service with cached status fallback

3. **Bulkhead Patterns**:
   - Separate message queues for different channels/teams
   - Isolated WebSocket connection pools
   - Priority queues for critical vs casual messages

4. **Retry Strategies**:
   - Message delivery retries with exponential backoff
   - File upload retries with resume capability
   - Database write retries for message persistence
```

### **Performance and Monitoring Considerations**

#### **Metrics to Monitor**
```yaml
# Circuit Breaker Metrics
circuit_breaker_state{service="user-service", state="open"}
circuit_breaker_calls_total{service="user-service", outcome="success"}
circuit_breaker_calls_total{service="user-service", outcome="failure"}

# Retry Metrics
retry_attempts_total{service="payment-service", outcome="success"}
retry_attempts_total{service="payment-service", outcome="exhausted"}

# Bulkhead Metrics
thread_pool_active_threads{pool="user-service"}
thread_pool_queue_size{pool="order-service"}
semaphore_available_permits{resource="external-api"}
```

#### **Alerting Rules**
```yaml
# Circuit breaker opened
- alert: CircuitBreakerOpen
  expr: circuit_breaker_state{state="open"} == 1
  for: 1m
  annotations:
    summary: "Circuit breaker is open for {{ $labels.service }}"

# High retry rate
- alert: HighRetryRate
  expr: rate(retry_attempts_total{outcome="exhausted"}[5m]) > 0.1
  for: 2m
  annotations:
    summary: "High retry exhaustion rate for {{ $labels.service }}"

# Resource exhaustion
- alert: ThreadPoolExhaustion
  expr: thread_pool_active_threads / thread_pool_max_threads > 0.9
  for: 30s
  annotations:
    summary: "Thread pool nearly exhausted for {{ $labels.pool }}"
```

---

## **7. BEST PRACTICES FOR INTERVIEWS**

### **Common Interview Questions**
1. "How do you handle service failures in a microservices architecture?"
2. "Explain the difference between circuit breaker and retry patterns"
3. "How do you prevent one slow service from affecting the entire system?"
4. "What's the trade-off between resilience and performance?"
5. "How do you test these resilience patterns?"

### **Key Points to Emphasize**
- **Layered Defense**: Use multiple patterns together, not in isolation
- **Observability**: Monitor and alert on resilience pattern behavior
- **Testing**: Chaos engineering to validate resilience
- **Configuration**: Tune parameters based on actual system behavior
- **Trade-offs**: Balance between resilience and complexity

### **Red Flags to Avoid**
- Implementing all patterns without understanding the specific requirements
- Ignoring the performance overhead of resilience patterns
- Not considering the operational complexity
- Forgetting about testing and monitoring
- Using default configurations without tuning

---

## **Summary**

**Service discovery and resilience patterns work together to build robust distributed systems:**

- **Service Discovery**: Enables dynamic service location and health management
- **Service Mesh**: Provides infrastructure-level communication management
- **Circuit Breakers**: Prevent cascading failures with fail-fast behavior
- **Retry Policies**: Handle transient failures with intelligent backoff
- **Bulkhead Pattern**: Isolate resources to prevent total system failure

**Interview Success Factors:**
- Understand when and why to apply each pattern
- Discuss implementation trade-offs and monitoring needs
- Connect patterns to specific business requirements
- Demonstrate knowledge of real-world tools and frameworks
- Show awareness of operational complexity and testing strategies
