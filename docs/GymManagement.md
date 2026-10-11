# ĐẶC TẢ NGHIỆP VỤ — GYM MANAGEMENT

## 1. Bối cảnh ứng dụng

Một phòng gym nhỏ đang quản lý thông tin hội viên bằng phương pháp thủ công. Nhân viên thường xuyên phải thêm hội viên mới, cập nhật thông tin cá nhân, theo dõi thời hạn gói tập và xử lý yêu cầu gia hạn. Việc quản lý thủ công dễ dẫn đến sai sót về thông tin, thời hạn tập luyện và trạng thái hội viên.

Phòng gym muốn xây dựng một ứng dụng quản lý hội viên đơn giản để giải quyết các vấn đề trên. Ứng dụng có giao diện web cho nhân viên sử dụng, backend Java Spring Boot để xử lý nghiệp vụ và cơ sở dữ liệu MySQL để lưu trữ thông tin.

Hệ thống hỗ trợ hai loại gói tập là BASIC và PREMIUM. Nhân viên có thể quản lý thông tin hội viên, xem danh sách, kiểm tra thời hạn gói tập, cập nhật thông tin và gia hạn khi hội viên có nhu cầu.

Trong dự án này, yêu cầu quan trọng không chỉ là ứng dụng hoạt động được mà còn phải đảm bảo các quy tắc nghiệp vụ được thực hiện chính xác. Vì vậy, phần xử lý nghiệp vụ cần được kiểm thử độc lập trước khi đưa vào sử dụng.

## 2. Các chức năng chính

### 2.1. Quản lý hội viên

Mỗi hội viên có các thông tin:

- ID: mã định danh duy nhất do hệ thống tạo.
- Họ tên: thông tin bắt buộc.
- Email: thông tin bắt buộc, phải đúng định dạng và không được trùng với email của hội viên khác.
- Số điện thoại: thông tin bắt buộc.
- Loại gói tập: BASIC hoặc PREMIUM.
- Ngày bắt đầu gói tập.
- Ngày hết hạn gói tập.

Hệ thống cho phép nhân viên tạo mới, xem danh sách, xem chi tiết, cập nhật thông tin và xóa hội viên.

Khi tạo hội viên, hệ thống phải tự xác định ngày bắt đầu và ngày hết hạn theo quy tắc của gói tập. Nhân viên không được tự đặt hai ngày này thông qua form tạo hội viên.

Khi cập nhật thông tin cá nhân, các thông tin liên quan đến gói tập không được tự ý thay đổi nếu nghiệp vụ cập nhật không yêu cầu điều đó.

### 2.2. Quản lý giá gói tập

Phòng gym quy định giá một tháng như sau:

- BASIC: 300.000 VND.
- PREMIUM: 500.000 VND.

Giá tháng được xác định theo loại gói tập. Việc thay đổi thông tin cá nhân không được làm thay đổi loại gói hoặc giá gói một cách ngoài ý muốn.

Ứng dụng hiện tại chỉ quản lý thông tin gói tập và thời hạn sử dụng, chưa triển khai thanh toán trực tuyến, hóa đơn hoặc quản lý giao dịch tài chính.

### 2.3. Quản lý thời hạn gói tập

Khi một hội viên đăng ký mới, ngày bắt đầu là ngày hiện tại. Gói tập đầu tiên kéo dài một tháng theo quy tắc tính tháng lịch.

Ngày hết hạn được tính bằng:

**Ngày hết hạn = Ngày bắt đầu cộng một tháng rồi trừ một ngày.**

Ví dụ, nếu ngày bắt đầu là 15/03/2026 thì ngày hết hạn là 14/04/2026.

Một hội viên được xem là còn hạn khi ngày hiện tại nằm trong khoảng từ ngày bắt đầu đến ngày hết hạn, bao gồm cả hai ngày này.

Nếu ngày hiện tại nằm trước ngày bắt đầu hoặc sau ngày hết hạn, hội viên không được xem là có gói tập đang còn hiệu lực.

Hệ thống sử dụng ngày lịch, không tính thời hạn bằng cách mặc định một tháng luôn có 30 ngày. Những tháng có số ngày khác nhau và các trường hợp chuyển tháng cần được xử lý theo quy tắc ngày tháng của Java.

### 2.4. Gia hạn gói tập

Nhân viên có thể gia hạn gói tập cho một hội viên với số tháng nguyên từ 1 đến 12.

Nếu hội viên vẫn còn hạn tại thời điểm gia hạn, thời hạn mới được nối tiếp từ ngày hết hạn hiện tại. Ngày bắt đầu của gói tập không bị thay đổi.

Ví dụ: gói tập hiện tại hết hạn ngày 14/04/2026. Nếu gia hạn một tháng khi gói tập vẫn còn hạn, ngày hết hạn mới là 14/05/2026.

Nếu hội viên đã hết hạn, thời hạn mới bắt đầu từ ngày hiện tại. Hệ thống không được tiếp tục cộng tháng vào ngày hết hạn đã nằm trong quá khứ.

Nếu ngày bắt đầu của một gói tập nằm trong tương lai, gói tập đó chưa được xem là đang có hiệu lực. Khi gia hạn theo quy tắc của hệ thống, thời hạn mới bắt đầu từ ngày hiện tại.

Ngày hết hạn mới phải được tính theo số tháng gia hạn và quy tắc lịch tháng. Quá trình gia hạn không được tạo ra một hội viên mới hoặc làm thay đổi email, họ tên và số điện thoại.

### 2.5. Xử lý dữ liệu không hợp lệ

Hệ thống phải từ chối những dữ liệu không đáp ứng yêu cầu nghiệp vụ.

Các trường hợp cần được xử lý bao gồm:

- Họ tên hoặc số điện thoại bị bỏ trống.
- Email sai định dạng hoặc bị trùng với email của hội viên khác.
- Loại gói tập không thuộc BASIC hoặc PREMIUM.
- ID hội viên không tồn tại.
- Số tháng gia hạn nhỏ hơn 1, lớn hơn 12 hoặc không phải số nguyên hợp lệ.
- Yêu cầu gia hạn hoặc cập nhật chứa dữ liệu không hợp lệ.

Khi không tìm thấy hội viên theo ID, backend phải trả về HTTP 404. Dữ liệu đầu vào không hợp lệ phải nhận phản hồi lỗi thích hợp và có thông báo dễ hiểu.

Hệ thống không được trả về stack trace hoặc thông tin nội bộ của server cho người dùng.

## 3. Các yêu cầu đối với REST API

Backend cung cấp các API sau:

- POST /api/members: tạo hội viên.
- GET /api/members: lấy danh sách hội viên.
- GET /api/members/{id}: xem thông tin một hội viên.
- PUT /api/members/{id}: cập nhật thông tin hội viên.
- DELETE /api/members/{id}: xóa hội viên.
- POST /api/members/{id}/renew?months=1: gia hạn gói tập.

API cần sử dụng phương thức HTTP, mã trạng thái và JSON response phù hợp với kết quả xử lý. Controller tiếp nhận yêu cầu, Service chịu trách nhiệm xử lý nghiệp vụ và Repository thực hiện các thao tác dữ liệu thông qua Spring Data JPA.

Các quy tắc về giá, thời hạn, gia hạn và tính hợp lệ phải được xử lý tại backend; frontend không phải nguồn quyết định cuối cùng về tính đúng đắn của nghiệp vụ.

## 4. Yêu cầu đối với giao diện web

Nhân viên sử dụng giao diện HTML, CSS và JavaScript thuần để:

- Xem danh sách hội viên.
- Xem tổng số hội viên, số hội viên còn hạn và số hội viên hết hạn.
- Thêm hội viên mới.
- Cập nhật thông tin hội viên.
- Xóa hội viên sau khi xác nhận.
- Gia hạn gói tập từ 1 đến 12 tháng.
- Xem thông báo thành công hoặc lỗi khi thao tác.

Giao diện phải hiển thị dữ liệu lấy từ backend, không sử dụng dữ liệu giả thay thế cho database. Sau khi thêm, cập nhật, xóa hoặc gia hạn thành công, danh sách cần được đồng bộ lại với dữ liệu thực tế.

Việc kiểm tra dữ liệu ở giao diện nhằm giúp người dùng nhập liệu thuận tiện hơn. Backend vẫn phải tự kiểm tra và thực thi các quy tắc nghiệp vụ.

## 5. Yêu cầu về chất lượng

Ứng dụng cần dễ bảo trì và dễ kiểm thử. Logic nghiệp vụ được tổ chức trong lớp Service, tách biệt khỏi phần giao diện và truy cập database.

Các chức năng nghiệp vụ phải có thể được kiểm thử độc lập mà không cần khởi động trình duyệt hoặc kết nối MySQL. Hệ thống cần có khả năng xác định thời điểm hiện tại theo cách kiểm soát được để có thể kiểm thử ngày bắt đầu, ngày hết hạn và gia hạn bằng ngày cố định.

Các bài kiểm thử phải dựa trên yêu cầu nghiệp vụ, xác định kết quả mong đợi một cách độc lập và phát hiện được trường hợp chương trình xử lý sai. Khi phát hiện lỗi, việc sửa code phải giữ nguyên các yêu cầu chính thức của hệ thống.

Phạm vi hiện tại chỉ bao gồm quản lý hội viên và gói tập. Các tính năng như đăng nhập, phân quyền, thanh toán, quản lý huấn luyện viên, quản lý thiết bị và thống kê doanh thu nằm ngoài phạm vi dự án này.