# Karaoke Booking & POS System — AI-Assisted System Design

Bài nộp cho bài kiểm tra thực hành **AI-Assisted System Design**.

## Cấu trúc thư mục

```
.
├── History_Prompts.txt        # Toàn bộ lịch sử prompt đã dùng để điều hướng AI (iterative prompting)
├── System_Design_SRS.docx     # Tài liệu đặc tả hệ thống (SRS) hoàn chỉnh, sơ đồ đã render thành ảnh
└── miniproject/                # Source code backend RESTful API (Spring Boot)
```

## Tóm tắt hệ thống

- **Chủ đề:** Hệ thống Quản lý Chuỗi Phòng hát Karaoke (Karaoke Booking & POS)
- **3 Module cốt lõi:** Room Booking, Order & POS, Billing/Invoice
- **3 Actors:** Customer, Receptionist, Manager

Chi tiết đầy đủ về scope, functional requirements, user stories, use case diagram, ERD, non-functional requirements và danh sách API endpoints nằm trong `System_Design_SRS.docx`.

## Backend: `miniproject/`

- **Framework:** Spring Boot 3 (Java 17)
- **Build tool:** Gradle
- **Dependencies chính:** Spring Web, Spring Data JPA, Validation, Lombok, Spring Boot DevTools, MySQL Driver
- **Database:** MySQL (cấu hình sẵn trong `application.properties`), test dùng H2 in-memory

### Cách chạy

1. Cài đặt MySQL, đảm bảo user/password khớp với `src/main/resources/application.properties`:
   ```
   spring.datasource.url=jdbc:mysql://localhost:3306/karaoke_db?createDatabaseIfNotExist=true
   spring.datasource.username=root
   spring.datasource.password=123456789
   ```
   (Database `karaoke_db` sẽ tự tạo nếu chưa có, nhờ `createDatabaseIfNotExist=true`.)

2. Chạy project:
   ```bash
   cd miniproject
   ./gradlew bootRun
   ```
   > Nếu chưa có Gradle Wrapper jar trong máy, chạy `gradle wrapper` trước (cần Gradle 8.x đã cài sẵn), hoặc dùng `gradle bootRun` trực tiếp nếu máy đã cài Gradle 8+.

3. Server chạy tại `http://localhost:8080`. Dữ liệu mẫu (1 chi nhánh, 3 phòng, 2 tài khoản nhân viên, 4 món trong menu) sẽ được tự động seed khi database rỗng (xem `DataSeeder`).

4. Chạy test (dùng H2 in-memory, không cần MySQL):
   ```bash
   ./gradlew test
   ```

### Danh sách API chính

Xem đầy đủ trong SRS, mục 7 — RESTful API Endpoints. Tóm tắt nhanh:

| Module | Endpoints |
|---|---|
| Room Booking | `GET/POST /api/rooms`, `PUT /api/rooms/{id}`, `GET/POST /api/bookings`, `PUT /api/bookings/{id}`, `POST /api/bookings/{id}/checkin`, `POST /api/bookings/{id}/checkout` |
| Order & POS | `GET/POST /api/menu-items`, `PUT /api/menu-items/{id}`, `GET/POST /api/bookings/{id}/orders`, `PUT/DELETE /api/orders/{id}` |
| Billing | `POST /api/bookings/{id}/invoice`, `PUT /api/invoices/{id}/pay`, `GET /api/invoices/{id}`, `GET /api/reports/revenue` |
| Khác | `GET/POST /api/branches`, `GET/POST /api/customers`, `GET/POST /api/users` |

### Ghi chú

- Vì không dùng Spring Security theo yêu cầu đề bài, project này **không có JWT/authentication** — đây là bản rút gọn tập trung vào đúng 3 module nghiệp vụ chính theo yêu cầu đề bài ("giữ đơn giản, không cần đầy đủ mọi tính năng").
- Mật khẩu user hiện lưu dạng plain text để đơn giản hoá (ghi rõ trong code) — trong hệ thống thực tế cần hash (BCrypt) và bổ sung Spring Security.
