## **HTTPS/TLS - Explanation & Usage in Projects**

### **Quick Definition (30 seconds):**

*"HTTPS is HTTP with encryption via TLS (Transport Layer Security). TLS encrypts data in transit between client and
server, ensuring confidentiality, integrity, and authentication. In my projects at MKU and VNet, we use HTTPS/TLS
everywhere—for external APIs, internal microservice communication, database connections, and message queues—to protect
sensitive financial and business data."*

---

## **Part 1: Understanding HTTPS/TLS**

### **What is TLS?**

**TLS (Transport Layer Security):**

- *Cryptographic protocol for secure communication over networks*
- *Successor to SSL (Secure Sockets Layer)*
- *Current versions: TLS 1.2 (minimum), TLS 1.3 (preferred)*

**What TLS Provides:**

**1. Encryption (Confidentiality):**

- *Data encrypted in transit—can't be read if intercepted*
- *Example: Credit card numbers, trading data, passwords*

**2. Integrity:**

- *Data can't be modified without detection*
- *Tampering is detected via message authentication codes (MAC)*

**3. Authentication:**

- *Verify you're talking to the real server, not an impostor*
- *Uses digital certificates (X.509 certificates)*

---

### **How TLS Works (Simplified):**

```
CLIENT                                    SERVER
  │                                          │
  │ ──── ClientHello ────────────────────► │
  │      (TLS versions, cipher suites)      │
  │                                          │
  │ ◄─── ServerHello ──────────────────── │
  │      (Selected TLS version, cipher)     │
  │      Server Certificate (public key)    │
  │                                          │
  │ ──── Verify Certificate ───────────► │
  │      (Check CA signature, validity)     │
  │                                          │
  │ ──── Key Exchange ─────────────────► │
  │      (Generate shared secret)           │
  │                                          │
  │ ◄──── Handshake Complete ──────────── │
  │                                          │
  │ ══ Encrypted Application Data ══════ │
  │      (All HTTP traffic encrypted)       │
```

**Key Steps:**

1. **Handshake:** Client and server agree on encryption method
2. **Certificate Verification:** Client verifies server's identity
3. **Key Exchange:** Generate shared encryption keys
4. **Encrypted Communication:** All data encrypted with shared keys

---

## **Part 2: HTTPS/TLS in Current Project (MKU)**

### **Overview:**

*"At MKU, we handle sensitive financial data—trading information, customer data, transaction details. TLS is
mandatory everywhere. We enforce TLS 1.2+ and use certificates issued by our internal Certificate Authority (CA) for
internal services and public CAs (like DigiCert) for external-facing services."*

---

### **Use Case 1: Kafka Communication (MSK)**

**Why TLS is Critical:**

- *Trading events contain sensitive deal information*
- *Data flows across network between services*
- *Must prevent eavesdropping and tampering*

**Implementation:**

**Kafka Configuration (Producer in Messaging-Gateway):**

```java
Properties props = new Properties();

// Bootstrap servers with TLS port (9094 instead of 9092)
props.

put("bootstrap.servers","kafka-broker-1.aws.internal:9094");

// Enable TLS/SSL
props.

put("security.protocol","SSL");

// Truststore - contains CA certificate to verify broker
props.

put("ssl.truststore.location","/app/config/kafka.truststore.jks");
props.

put("ssl.truststore.password",System.getenv("TRUSTSTORE_PASSWORD"));

// Keystore - our client certificate for mutual TLS (mTLS)
        props.

put("ssl.keystore.location","/app/config/kafka.keystore.jks");
props.

put("ssl.keystore.password",System.getenv("KEYSTORE_PASSWORD"));
        props.

put("ssl.key.password",System.getenv("KEY_PASSWORD"));

// TLS version enforcement
        props.

put("ssl.enabled.protocols","TLSv1.2,TLSv1.3");

// Hostname verification
props.

put("ssl.endpoint.identification.algorithm","https");

KafkaProducer<String, String> producer = new KafkaProducer<>(props);
```

**What This Does:**

- *Encrypts all messages between Messaging-Gateway and Kafka brokers*
- *Mutual TLS (mTLS)—both client and server authenticate each other*
- *Kafka broker verifies our client certificate*
- *We verify Kafka broker's certificate*

**Benefits:**

- *Trading data encrypted in transit*
- *Prevents man-in-the-middle attacks*
- *Ensures only authorized clients can connect to Kafka*

---

### **Use Case 2: REST API Communication (Manager ↔ Extractor)**

**Scenario:**

- *Manager sends job requests to Extractor instances via REST API*
- *Payload contains metadata: S3 paths, database credentials, deal IDs*

**Implementation (Spring Boot):**

**Extractor Service (Server-side):**

```yaml
# application.yml
server:
  port: 8443
  ssl:
    enabled: true
    key-store: classpath:extractor-keystore.p12
    key-store-password: ${KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: extractor-service
    # Enforce TLS 1.2+
    enabled-protocols: TLSv1.2,TLSv1.3
    # Strong cipher suites only
    ciphers: TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
```

**Manager Service (Client-side):**

```java

@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate secureRestTemplate() throws Exception {
        // Load truststore to verify Extractor certificates
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(
                        new ClassPathResource("truststore.jks").getFile(),
                        truststorePassword.toCharArray()
                )
                .build();

        // Hostname verification
        SSLConnectionSocketFactory socketFactory =
                new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.2", "TLSv1.3"},
                        null,
                        SSLConnectionSocketFactory.getDefaultHostnameVerifier()
                );

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .build();

        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory);
    }
}

@Service
public class ExtractorClient {

    @Autowired
    private RestTemplate secureRestTemplate;

    public void sendJobToExtractor(String extractorUrl, JobMetadata job) {
        // All communication over HTTPS
        String url = "https://" + extractorUrl + "/api/jobs";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<JobMetadata> request = new HttpEntity<>(job, headers);

        ResponseEntity<JobResponse> response =
                secureRestTemplate.postForEntity(url, request, JobResponse.class);

        // Job data encrypted in transit
    }
}
```

**What This Achieves:**

- *Job metadata (including sensitive database connection details) encrypted*
- *Manager verifies Extractor's identity via certificate*
- *Prevents unauthorized services from impersonating Extractor*

---

### **Use Case 3: Database Connections (Redshift, RDS)**

**Why TLS for Databases:**

- *Database queries may contain sensitive deal data*
- *Connection strings contain credentials*
- *Network traffic could be intercepted*

**Implementation (Spring Boot + Redshift):**

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:redshift://redshift-cluster.aws.internal:5439/trading_db?ssl=true&sslmode=verify-full&sslrootcert=/app/config/rds-ca-bundle.pem
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      connection-test-query: SELECT 1
```

**What This Does:**

- `ssl=true`: Enable TLS encryption
- `sslmode=verify-full`: Verify server certificate AND hostname
- `sslrootcert`: CA certificate to verify Redshift's identity
- *All queries encrypted in transit*

**Code Example:**

```java

@Repository
public class DealRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Deal findDealById(Long dealId) {
        // This query travels over TLS-encrypted connection
        String sql = "SELECT * FROM validated.deals WHERE deal_id = ?";
        return jdbcTemplate.queryForObject(sql, new DealRowMapper(), dealId);
    }
}
```

---

### **Use Case 4: S3 Communication (AWS SDK)**

**Scenario:**

- *Messaging-Gateway uploads trading messages to S3*
- *Extractor downloads messages from S3*

**Implementation:**

```java

@Service
public class S3Service {

    private final S3Client s3Client;

    public S3Service() {
        // AWS SDK uses HTTPS by default
        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create("https://s3.us-east-1.amazonaws.com"))
                .build();
    }

    public void uploadMessage(String bucket, String key, String content) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .serverSideEncryption(ServerSideEncryption.AES256) // Encryption at rest
                .build();

        // Upload over HTTPS (TLS in transit)
        s3Client.putObject(request, RequestBody.fromString(content));

        // Data protected both in transit (TLS) and at rest (SSE)
    }
}
```

**What Happens:**

- *All S3 API calls use HTTPS automatically*
- *Trading messages encrypted during upload/download*
- *Combined with S3 server-side encryption for defense-in-depth*

---

### **Use Case 5: Ingress/Load Balancer (External Access)**

**Scenario:**

- *Support Dashboard (Python Flask) accessed by support team*
- *External users accessing web applications*

**Implementation (Kubernetes Ingress with TLS):**

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: support-dashboard-ingress
  annotations:
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/force-ssl-redirect: "true"
spec:
  tls:
    - hosts:
        - support-dashboard.MKU.internal
      secretName: support-dashboard-tls
  rules:
    - host: support-dashboard.MKU.internal
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: support-dashboard
                port:
                  number: 5000
```

**Certificate Management:**

```yaml
apiVersion: cert-manager.io/v1
kind: Certificate
metadata:
  name: support-dashboard-cert
spec:
  secretName: support-dashboard-tls
  issuerRef:
    name: internal-ca-issuer
    kind: ClusterIssuer
  dnsNames:
    - support-dashboard.MKU.internal
  duration: 2160h # 90 days
  renewBefore: 360h # Renew 15 days before expiry
```

**What This Achieves:**

- *All access to Support Dashboard over HTTPS*
- *Certificate automatically issued and renewed*
- *HTTP requests redirected to HTTPS*
- *Support team's browser verifies certificate*

---

## **Part 3: HTTPS/TLS in Previous Project (VNet Solutions)**

### **Azure App Service with TLS:**

**Custom Domain with TLS:**

```bash
# Bind custom domain
az webapp config hostname add \
  --resource-group inventory-rg \
  --webapp-name inventory-app \
  --hostname inventory.vnetsolutions.com.au

# Upload TLS certificate
az webapp config ssl upload \
  --resource-group inventory-rg \
  --name inventory-app \
  --certificate-file inventory-cert.pfx \
  --certificate-password ${CERT_PASSWORD}

# Bind certificate to hostname
az webapp config ssl bind \
  --resource-group inventory-rg \
  --name inventory-app \
  --certificate-thumbprint ${CERT_THUMBPRINT} \
  --ssl-type SNI
```

**Enforce HTTPS:**

```yaml
# Azure App Service Configuration
properties:
  httpsOnly: true
  minTlsVersion: '1.2'
  ftpsState: 'Disabled'
```

**Angular Frontend → Spring Boot Backend:**

```typescript
// Angular HTTP Interceptor
@Injectable()
export class SecureInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Ensure all API calls use HTTPS
    if (!req.url.startsWith('https://')) {
      const secureUrl = req.url.replace('http://', 'https://');
      req = req.clone({ url: secureUrl });
    }
    return next.handle(req);
  }
}
```

---

### **Azure Redis Cache with TLS:**

```java

@Configuration
public class RedisConfig {

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("inventory-cache.redis.cache.windows.net");
        config.setPort(6380); // TLS port (non-TLS is 6379)
        config.setPassword(redisPassword);

        JedisClientConfiguration clientConfig = JedisClientConfiguration.builder()
                .useSsl() // Enable TLS
                .build();

        return new JedisConnectionFactory(config, clientConfig);
    }
}
```

---

### **Azure SQL Database with TLS:**

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://inventory-db.database.windows.net:1433;database=inventory;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;
    username: ${DB_USER}
    password: ${DB_PASSWORD}
```

**Parameters:**

- `encrypt=true`: Enable TLS
- `trustServerCertificate=false`: Verify server certificate
- `hostNameInCertificate`: Expected hostname in cert

---

## **Part 4: Certificate Management**

### **Certificate Types:**

**1. Self-Signed Certificates (Dev/Test Only)**

```bash
# Generate self-signed cert (NOT for production)
openssl req -x509 -newkey rsa:4096 \
  -keyout key.pem -out cert.pem \
  -days 365 -nodes \
  -subj "/CN=localhost"
```

**2. Internal CA Certificates (Internal Services)**

- *MKU has internal Certificate Authority*
- *Issues certificates for internal microservices*
- *Trusted within corporate network*

**3. Public CA Certificates (External Services)**

- *DigiCert, Let's Encrypt, etc.*
- *For customer-facing applications*
- *Trusted by browsers globally*

---

### **Certificate Lifecycle:**

**At MKU (Kubernetes + cert-manager):**

```yaml
# Automated certificate management
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: internal-ca-issuer
spec:
  ca:
    secretName: ca-key-pair

---
apiVersion: cert-manager.io/v1
kind: Certificate
metadata:
  name: kafka-client-cert
spec:
  secretName: kafka-client-tls
  issuerRef:
    name: internal-ca-issuer
  dnsNames:
    - messaging-gateway.default.svc.cluster.local
  duration: 2160h # 90 days
  renewBefore: 360h # Auto-renew 15 days before expiry
```

**What This Does:**

- *cert-manager automatically issues certificates*
- *Stores in Kubernetes Secrets*
- *Auto-renews before expiry*
- *No manual certificate management*

---

## **Part 5: Security Best Practices We Follow**

### **1. TLS Version Enforcement**

- ❌ TLS 1.0, TLS 1.1 (deprecated, vulnerable)
- ✅ TLS 1.2 (minimum)
- ✅ TLS 1.3 (preferred, faster handshake)

### **2. Strong Cipher Suites**

- *Only allow secure ciphers*
- *Disable weak ciphers (RC4, DES, MD5)*
- *Prefer forward secrecy (ECDHE)*

### **3. Certificate Validation**

- *Always verify server certificates*
- *Never use `trustAllCertificates()` in production*
- *Validate hostname matches certificate*

### **4. Mutual TLS (mTLS) Where Appropriate**

- *Kafka: Both client and broker authenticate*
- *Internal microservices: mTLS for service-to-service*
- *Zero-trust security model*

### **5. Certificate Rotation**

- *Short certificate lifetimes (90 days)*
- *Automated renewal (cert-manager)*
- *Monitoring for expiring certificates*

### **6. No Hardcoded Secrets**

- *Certificates/passwords in environment variables*
- *AWS Secrets Manager / Azure Key Vault*
- *Kubernetes Secrets for sensitive data*

---

## **Part 6: Common Issues & Solutions**

### **Issue 1: Certificate Expiry**

**Problem:**
*"Certificate expired, service can't connect to Kafka—production outage!"*

**Prevention at MKU:**

- *Automated renewal with cert-manager*
- *Grafana alerts 30 days before expiry*
- *Weekly checks in Support Dashboard*

---

### **Issue 2: Hostname Mismatch**

**Problem:**

```
SSLHandshakeException: Certificate hostname mismatch
Expected: kafka-broker-1.aws.internal
Found: *.kafka.aws.internal
```

**Solution:**

- *Update certificate with correct Subject Alternative Names (SANs)*
- *Or configure client to accept wildcard certificates*

---

### **Issue 3: Truststore Issues**

**Problem:**
*"Unable to find valid certification path to requested target"*

**Cause:**

- *Client doesn't have CA certificate in truststore*

**Solution:**

```bash
# Import CA certificate into Java truststore
keytool -import \
  -alias kafka-ca \
  -file ca-cert.pem \
  -keystore truststore.jks \
  -storepass changeit
```

---

### **Issue 4: Performance Impact**

**Problem:**
*"TLS handshake adds latency"*

**Mitigation:**

- *TLS session resumption (reuse handshake)*
- *Connection pooling (HTTP keep-alive)*
- *TLS 1.3 (faster handshake than 1.2)*
- *At MKU, negligible impact due to long-lived Kafka connections*

---

## **Interview Q&A:**

**Q: "What's the difference between TLS and SSL?"**

*"SSL (Secure Sockets Layer) is the predecessor to TLS. SSL 3.0 was deprecated in 2015 due to vulnerabilities. TLS is
the modern, secure version. When people say 'SSL certificate,' they usually mean TLS certificate—it's just legacy
terminology. In practice:*

- *SSL 2.0, 3.0: Deprecated, vulnerable*
- *TLS 1.0, 1.1: Deprecated*
- *TLS 1.2: Current minimum standard*
- *TLS 1.3: Latest, most secure*

*At both MKU and VNet, we enforce TLS 1.2+ and have disabled all SSL versions."*

---

**Q: "What is mutual TLS (mTLS)?"**

*"Standard TLS: Only server provides certificate, client verifies server.*

*Mutual TLS (mTLS): Both server AND client provide certificates, both verify each other.*

*Example at MKU:*

- *Kafka broker has a certificate*
- *Messaging-Gateway (client) also has a certificate*
- *Broker verifies Gateway is authorized before accepting connection*
- *Gateway verifies broker is legitimate*

*This prevents unauthorized clients from connecting, even if they know the broker address. It's a zero-trust security
model—don't trust based on network location, verify identity cryptographically."*

---

**Q: "How do you handle certificate renewal in production without downtime?"**

*"At MKU, we use cert-manager in Kubernetes:*

1. *cert-manager renews certificate 15 days before expiry*
2. *New certificate stored in Kubernetes Secret*
3. *Pod restart triggered automatically (rolling restart)*
4. *Pods restart one-by-one, no downtime*
5. *New pods use new certificate*

*For Kafka specifically:*

- *Kafka supports dynamic certificate reload (no broker restart)*
- *Update certificate file, trigger config refresh*

*At VNet with Azure:*

- *Azure App Service allows certificate update without restart*
- *Blue-green deployment for zero downtime if restart needed"*

---

## **Summary: HTTPS/TLS in My Projects**

**Current Role (MKU):**

- ✅ Kafka messaging (MSK) with TLS + mTLS
- ✅ REST APIs (Manager ↔ Extractor) over HTTPS
- ✅ Database connections (Redshift, RDS) with TLS
- ✅ S3 API calls over HTTPS
- ✅ Support Dashboard with TLS ingress
- ✅ Automated certificate management (cert-manager)

**Previous Role (VNet):**

- ✅ Azure App Service with custom domain TLS
- ✅ Angular frontend → Spring Boot backend over HTTPS
- ✅ Azure Redis Cache with TLS
- ✅ Azure SQL Database with TLS encryption
- ✅ Enforced HTTPS-only, TLS 1.2+ minimum

*"In financial services and enterprise systems, TLS isn't optional—it's mandatory. Every byte of sensitive data must be
encrypted in transit. I've implemented TLS across the entire stack, from frontend to database, ensuring comprehensive
protection."*

---

**Does this cover HTTPS/TLS comprehensively with your project examples? Ready for the next expertise area?**