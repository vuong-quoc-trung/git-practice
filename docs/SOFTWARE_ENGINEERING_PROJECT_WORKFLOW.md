# Software Engineering Project Workflow với AI

## Mục tiêu

Workflow này dùng cho quá trình xây dựng một dự án phần mềm với sự hỗ trợ của AI.

Mục tiêu không phải để AI làm thay toàn bộ công việc, mà là sử dụng AI như một **engineering assistant** trong khi người phát triển vẫn chịu trách nhiệm về:

- Hiểu bài toán
- Thiết kế hệ thống
- Ra quyết định kỹ thuật
- Kiểm tra chất lượng
- Bảo mật
- Hiệu năng
- Vận hành và cải tiến

---

# 1. IDEA

Xác định dự án đang muốn xây dựng và vấn đề mà dự án giải quyết.

### Cần xác định

- Sản phẩm là gì?
- Giải quyết vấn đề nào?
- Ai là người sử dụng?
- Giá trị chính của sản phẩm là gì?
- Phạm vi ban đầu là gì?

### Ví dụ

> Xây dựng một ứng dụng chat realtime giống WhatsApp ở quy mô nhỏ.

### Output

```text
Project Name:
Problem:
Target Users:
Main Value:
Initial Scope:
```

### Nguyên tắc

Chưa cần quyết định framework hay database ở bước này.

---

# 2. REQUIREMENT

Biến ý tưởng thành các yêu cầu cụ thể.

## Functional Requirements

Hệ thống phải làm được gì?

Ví dụ:

```text
FR1: User có thể đăng ký.
FR2: User có thể đăng nhập.
FR3: User có thể tìm kiếm người dùng.
FR4: User có thể tạo conversation.
FR5: User có thể gửi message.
FR6: User có thể nhận message realtime.
FR7: User có thể xem lịch sử message.
```

## Non-functional Requirements

Hệ thống phải hoạt động như thế nào?

Ví dụ:

```text
NFR1: API response nhanh trong điều kiện bình thường.
NFR2: Hỗ trợ nhiều kết nối đồng thời.
NFR3: Message không được mất.
NFR4: Password phải được bảo vệ.
NFR5: Hệ thống có logging và monitoring.
```

### Output

Một danh sách requirements có thể kiểm tra được.

---

# 3. ACCEPTANCE CRITERIA

Định nghĩa chính xác khi nào một chức năng được xem là hoàn thành.

### Ví dụ

Requirement:

> User có thể gửi message.

Acceptance Criteria:

```text
Given:
User A đang mở conversation với User B.

When:
User A gửi "Hello".

Then:
- Server nhận message.
- Message được lưu.
- User B nhận message realtime.
- User A thấy kết quả gửi.
- Sau khi refresh, message vẫn tồn tại.
```

### Mục đích

Acceptance Criteria giúp:

- Tránh requirement mơ hồ
- Kiểm tra implementation
- Viết test
- Đánh giá code do AI tạo
- Xác định Definition of Done

---

# 4. RESEARCH

Tìm hiểu các kiến thức và công nghệ cần thiết trước khi thiết kế.

### Ví dụ với ứng dụng chat

Có thể cần nghiên cứu:

```text
HTTP
WebSocket
SSE
PostgreSQL
Redis
Message Queue
Load Balancer
Authentication
Authorization
```

### Cần trả lời

- Công nghệ này giải quyết vấn đề gì?
- Có thực sự cần không?
- Có giải pháp đơn giản hơn không?
- Trade-off là gì?
- Quy mô hiện tại có cần giải pháp phức tạp không?

### Nguyên tắc

Không sử dụng công nghệ chỉ vì nó phổ biến.

> Problem → Requirement → Solution → Technology

Không nên làm ngược:

> Technology → tìm một vấn đề để dùng Technology

---

# 5. SYSTEM DESIGN

Thiết kế kiến trúc trước khi implementation.

## Cần thiết kế

### Architecture

Ví dụ:

```text
Client
   ↓
Load Balancer
   ↓
Backend API
   ↓
PostgreSQL

Backend
   ↓
Redis

Backend
   ↓
WebSocket
   ↓
Client
```

### API Design

Ví dụ:

```text
POST /auth/login
GET  /users/search
POST /conversations
GET  /conversations/{id}/messages
POST /messages
```

### Database Design

Ví dụ:

```text
users
conversations
conversation_members
messages
```

### Cần tự hỏi

- Thành phần nào chịu trách nhiệm gì?
- Dữ liệu nằm ở đâu?
- Request đi qua những thành phần nào?
- Database schema như thế nào?
- Có cần cache không?
- Có cần queue không?
- Scale như thế nào?
- Nếu một service chết thì sao?
- Nếu database chậm thì sao?
- Nếu một request được gửi hai lần thì sao?

### Output

- Architecture Diagram
- ERD
- API Design
- Data Flow
- Các quyết định kỹ thuật chính
- Trade-offs

---

# 6. SECURITY DESIGN

Security phải được thiết kế từ đầu, không phải chỉ thêm sau khi code xong.

### Kiểm tra

```text
Authentication
Authorization
Input Validation
Rate Limiting
Password Security
Session / Token Security
SQL Injection
XSS
CSRF
SSRF
File Upload
Secrets
Logging
Auditing
```

### Ví dụ

Khi đọc conversation:

```text
User A
  ↓
GET /conversations/123
  ↓
Check authentication
  ↓
Check membership / authorization
  ↓
Allow / Deny
```

### Cần tự hỏi

> Ai được truy cập dữ liệu?

> Nếu người dùng sửa ID trong URL thì sao?

> Nếu request được gửi hàng nghìn lần thì sao?

> Nếu attacker gửi input độc hại thì sao?

### Output

Một security checklist hoặc threat model cơ bản.

---

# 7. BREAK TASKS

Chia dự án lớn thành các task nhỏ và có thể kiểm soát.

Ví dụ:

```text
Authentication
├── Register
├── Login
├── Password hashing
├── JWT
├── Refresh token
└── Logout

Messaging
├── Create conversation
├── Send message
├── Store message
├── Load history
└── Realtime delivery
```

### Nguyên tắc

Không yêu cầu AI:

> "Build toàn bộ ứng dụng."

Nên yêu cầu AI làm từng task rõ ràng.

---

# 8. TỰ SUY NGHĨ

Trước khi hỏi AI, hãy tự đưa ra phương án.

### Ví dụ

```text
Tôi nghĩ:
- PostgreSQL phù hợp để lưu message.
- WebSocket phù hợp cho realtime.
- Redis có thể dùng cho cache hoặc trạng thái cần truy cập nhanh.
- Tôi chưa chắc có cần Kafka ở quy mô hiện tại.
```

### Tại sao phải làm bước này?

Vì mục tiêu là phát triển khả năng:

- Problem solving
- Architecture thinking
- Technical judgment
- Trade-off analysis

AI có thể sai. Người phát triển phải đủ hiểu để phát hiện sai.

---

# 9. AI HỖ TRỢ

AI nên được sử dụng như một người hỗ trợ kỹ thuật.

## Trước tiên: dùng AI để review suy nghĩ

Ví dụ prompt:

```text
Project:
Real-time chat application.

Stack:
- Spring Boot
- PostgreSQL
- Redis
- React + TypeScript

Requirements:
...

Acceptance Criteria:
...

Current Architecture:
...

My proposed design:
...

Please:
1. Review my design.
2. Identify weaknesses.
3. Explain technical trade-offs.
4. Suggest improvements.
5. Do not write code yet.
```

## Sau khi design ổn: yêu cầu implementation

```text
Now implement the following task:

Task:
...

Constraints:
...

Existing architecture:
...

Existing code:
...

Expected behavior:
...

Tests required:
...

Please:
- Explain the approach first.
- Then implement.
- Do not change unrelated parts.
```

### Nguyên tắc

Không để AI quyết định toàn bộ kiến trúc thay bạn.

---

# 10. IMPLEMENT

Bắt đầu coding theo từng task nhỏ.

### Quy trình

```text
Task
 ↓
Implement
 ↓
Compile
 ↓
Run
 ↓
Test
```

Không nên viết quá nhiều code trước khi chạy thử.

### Nguyên tắc

Code do AI tạo ra phải được đọc và hiểu.

> Không commit code mà bạn không hiểu.

---

# 11. CODE REVIEW

Review code sau khi implementation hoàn thành.

## Kiểm tra

```text
Correctness
Readability
Maintainability
Security
Performance
Error Handling
Concurrency
Database
Architecture
```

### Có thể dùng AI

```text
Review this code as a senior software engineer.

Check for:
- bugs
- security vulnerabilities
- N+1 queries
- race conditions
- unnecessary complexity
- poor error handling
- scalability issues
- maintainability problems

Do not rewrite everything.
Explain every issue and its impact.
```

### Mục tiêu

Không chỉ tìm bug mà còn học cách viết code tốt hơn.

---

# 12. TESTING

Không chỉ kiểm tra "chạy được".

## Các lớp testing

```text
Unit Test
Integration Test
API Test
End-to-End Test
```

### Ví dụ Login

Kiểm tra:

```text
Correct credentials
Wrong password
Unknown user
Invalid request
Expired token
Malformed token
```

### Acceptance Criteria

Mỗi requirement nên có test tương ứng.

---

# 13. SECURITY TEST

Sau functional testing, kiểm tra các vấn đề bảo mật.

### Ví dụ

```text
SQL Injection
XSS
CSRF
Broken Access Control
Brute Force
Rate Limiting
Privilege Escalation
Malicious File Upload
SSRF
```

### Đối với project có security/IDPS

Có thể mô phỏng:

```text
Attacker
   ↓
Malicious Request
   ↓
Application
   ↓
Logs
   ↓
Detection
   ↓
Alert
```

---

# 14. PERFORMANCE TEST

Kiểm tra hệ thống dưới tải.

### Có thể đo

```text
Latency
Throughput
Error Rate
CPU
RAM
Database Load
Connection Count
Cache Hit/Miss
```

### Ví dụ

```text
100 requests
1,000 requests
10,000 requests
```

Sau đó so sánh:

> Hệ thống chậm ở đâu?

> Database?

> Application?

> Network?

> Cache?

> Lock / Concurrency?

---

# 15. DEBUG

Khi có lỗi, không nên ngay lập tức đưa lỗi cho AI.

## Quy trình

```text
Observe
   ↓
Reproduce
   ↓
Form Hypothesis
   ↓
Check Logs
   ↓
Inspect Code
   ↓
Test Hypothesis
   ↓
Fix
   ↓
Verify
```

### Ví dụ

Thay vì hỏi:

> "Fix this 500 error."

Nên cung cấp:

```text
Expected behavior:
...

Actual behavior:
...

Steps to reproduce:
...

Relevant logs:
...

What I already checked:
...

My hypothesis:
...
```

AI lúc đó có context tốt hơn và bạn cũng hiểu hệ thống hơn.

---

# 16. DEPLOY

Đưa hệ thống ra môi trường thực tế.

### Ví dụ

```text
GitHub
   ↓
CI/CD
   ↓
Build
   ↓
Docker Image
   ↓
Server / Cloud
   ↓
Nginx / Reverse Proxy
   ↓
HTTPS
   ↓
Application
```

### Cần hiểu

- Environment variables
- Secrets
- Database deployment
- HTTPS
- Reverse proxy
- Container
- CI/CD
- Rollback

---

# 17. MONITORING

Sau khi deploy, cần biết hệ thống đang hoạt động như thế nào.

### Theo dõi

```text
CPU
RAM
Latency
Request Count
Error Rate
Database
Redis
Logs
Security Events
```

### Công cụ có thể học

```text
Prometheus
Grafana
Loki
ELK / OpenSearch
```

### Mục tiêu

Không chỉ biết:

> "App đang chạy."

Mà phải biết:

> "App đang chạy tốt hay có vấn đề gì?"

---

# 18. FEEDBACK

Thu thập thông tin sau khi project chạy.

### Nguồn feedback

- Người dùng
- Logs
- Metrics
- Error reports
- Security events
- Performance tests
- Code review
- Technical debt

### Ví dụ

Phát hiện:

> Query lịch sử chat chậm khi conversation có hàng triệu messages.

Đây trở thành input cho vòng cải tiến tiếp theo.

---

# 19. IMPROVE

Cải tiến hệ thống dựa trên dữ liệu thực tế.

Ví dụ:

```text
Slow query
   ↓
Analyze query
   ↓
Add index / redesign query
   ↓
Benchmark
   ↓
Deploy
   ↓
Monitor
```

Hoặc:

```text
Too many login attempts
   ↓
Analyze logs
   ↓
Rate limiting
   ↓
Security test
   ↓
Deploy
```

---

# VÒNG LẶP CỦA TOÀN BỘ WORKFLOW

Workflow không phải một đường thẳng kết thúc ở Deploy.

```text
Requirement
     ↓
Design
     ↓
Build
     ↓
Test
     ↓
Deploy
     ↓
Monitor
     ↓
Feedback
     ↓
Improve
     │
     └────────────→ Design
```

Sau mỗi vòng lặp, hệ thống tốt hơn và kiến thức của developer cũng tăng lên.

---

# NGUYÊN TẮC SỬ DỤNG AI

## 1. Không để AI thay thế tư duy

Tự suy nghĩ trước, sau đó mới hỏi AI.

## 2. Không commit code mà mình không hiểu

Code AI viết vẫn phải được review.

## 3. Không để AI tự ý thay đổi kiến trúc

Developer quyết định architecture và trade-offs.

## 4. AI nên được sử dụng ở nhiều vai trò

```text
Research Assistant
Architecture Reviewer
Coding Assistant
Code Reviewer
Testing Assistant
Debugging Assistant
Documentation Assistant
```

---

# PROJECT TEMPLATE

Khi bắt đầu project mới, có thể tạo cấu trúc:

```text
project/
├── README.md
├── docs/
│   ├── requirements.md
│   ├── acceptance-criteria.md
│   ├── architecture.md
│   ├── security.md
│   ├── api.md
│   └── adr/
├── backend/
├── frontend/
├── tests/
└── docker/
```

---

# DEFINITION OF DONE

Một feature không nên được coi là hoàn thành chỉ vì code chạy được.

Có thể coi là hoàn thành khi:

```text
[ ] Requirement rõ ràng
[ ] Acceptance Criteria đầy đủ
[ ] Design được xem xét
[ ] Security được xem xét
[ ] Implementation hoàn thành
[ ] Code review
[ ] Tests pass
[ ] Security tests pass
[ ] Performance phù hợp
[ ] Documentation cập nhật
[ ] Deploy thành công
[ ] Monitoring hoạt động
```

---

# TRIẾT LÝ CỐT LÕI

> **Requirement → Design → Think → AI → Implement → Review → Test → Secure → Measure → Deploy → Monitor → Improve**

AI không phải người xây hệ thống thay bạn.

AI là công cụ giúp bạn xây hệ thống **nhanh hơn**, còn bạn vẫn phải hiểu:

- Vì sao hệ thống được thiết kế như vậy.
- Vì sao chọn công nghệ đó.
- Trade-off là gì.
- Hệ thống có an toàn không.
- Hệ thống có đủ nhanh không.
- Hệ thống hoạt động thế nào khi có lỗi.
- Làm thế nào để cải tiến nó.
