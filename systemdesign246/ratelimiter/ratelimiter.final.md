# problem statement
A rate limiter is a system that controls the rate of incoming requests to a service, 
ensuring that it does not exceed a predefined limit. This is crucial for preventing abuse, 
ensuring fair usage, and maintaining the stability and performance of the service. For instance, 
the system say can access 1000 requests per minute. If the number of requests exceeds this limit,
the rate limiter will block or delay the excess requests.
# requirements
Loader-balancer can help to some extent on rate limiting, but it does not have knowledge of the cost of each operation. 
It just forwards the request to the service. Thus, the rate limiter should be implemented at the service level.

noisy neighbor problem: one user making a lot of requests can affect the performance of other users.