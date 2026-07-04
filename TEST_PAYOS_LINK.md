# Hướng dẫn kiểm tra tính năng Thanh toán PayOS

Nếu App không hiển thị mã QR, hãy thực hiện các bước sau để kiểm tra xem Backend đã hoạt động đúng chưa.

## 0. Kiểm tra tình trạng khởi tạo (Debug Link)

Mở trình duyệt và truy cập:
`http://54.81.9.236:3000/api/payos/debug`

**Kết quả mong đợi:**
- `env`: Tất cả phải là `PRESENT`.
- `payosObject.methods`: Phải chứa `createPaymentLink`, `verifyPaymentWebhookData`, ...
- `libType`: Phải là `function` hoặc `object`.

## 1. Kiểm tra Backend qua Postman (Hoặc trình duyệt)

Hãy gửi một yêu cầu để lấy link thanh toán thực tế từ PayOS.

- **Method:** `POST`
- **URL:** `http://54.81.9.236:3000/api/payos/create-payment-link`
- **Body (JSON):**
```json
{
    "orderId": 1
}
```
*(Lưu ý: Thay `orderId` bằng một mã ID đơn hàng có thật trong database của bạn và chưa thanh toán).*

### Kết quả mong đợi:
Bạn phải nhận được một JSON có dạng:
```json
{
    "bin": "...",
    "accountNumber": "...",
    "accountName": "...",
    "amount": 150000,
    "description": "...",
    "orderCode": 1001,
    "currency": "VND",
    "paymentLinkId": "...",
    "status": "PENDING",
    "checkoutUrl": "https://pay.payos.vn/gate/...",
    "qrCode": "00020101021238580010A000000727..."
}
```

## 2. Kiểm tra Link Thanh toán
- Copy giá trị trong trường `checkoutUrl`.
- Dán vào trình duyệt web (Chrome/Safari).
- **Kết quả:** Nếu trình duyệt mở ra trang thanh toán có mã VietQR và đúng tên tài khoản của bạn -> **Backend ĐÃ CHUẨN**.

## 3. Nếu Bước 2 thành công nhưng App vẫn không hiện QR:
Lỗi nằm ở phần hiển thị của App Android. Nguyên nhân có thể là:
1. **Internet:** Điện thoại không truy cập được server `api.qrserver.com`.
2. **Quyền truy cập:** Server EC2 chưa mở cổng 3000 cho các request từ bên ngoài (Check Security Group trên AWS).
3. **Log lỗi:** Bạn hãy mở **Logcat** trong Android Studio, lọc từ khóa `Glide` hoặc `Retrofit` để xem thông báo lỗi đỏ.

## 4. Cách khắc phục nhanh trong App
Nếu API tạo ảnh QR gặp lỗi, bạn có thể nhấn trực tiếp vào vùng hiển thị QR (tôi đã gán sự kiện click). App sẽ mở trình duyệt điện thoại để khách thanh toán trực tiếp trên trang chủ của PayOS.

---
*Tài liệu hỗ trợ gỡ lỗi thanh toán AppDatMon.*
