# Hướng dẫn nạp dữ liệu mẫu (Data Test) qua Postman

Tài liệu này cung cấp bộ dữ liệu JSON chuẩn để bạn nạp vào hệ thống sau khi đã Reset Database.

---

## Bước 1: Lấy mã xác thực (Token Admin)

Vì các API quản lý yêu cầu quyền Admin, bạn cần đăng nhập trước.

- **Method:** `POST`
- **URL:** `http://54.81.9.236:3000/api/auth/login`
- **Body (JSON):**
```json
{
    "account": "admin",
    "password": "123"
}
```
> **Lưu ý:** Copy chuỗi `token` trong kết quả trả về. Trong các yêu cầu tiếp theo, vào tab **Authorization** -> chọn **Bearer Token** -> Dán Token vào.

---

## Bước 2: Nạp Danh mục (Categories)

- **Method:** `POST`
- **URL:** `http://54.81.9.236:3000/api/categories`
- **Dữ liệu (Gửi từng cái một):**

**Cái 1: Khai vị**
```json
{ "name": "Món Khai Vị", "description": "Súp, salad và các món nhẹ" }
```
**Cái 2: Món chính**
```json
{ "name": "Sushi & Sashimi", "description": "Hải sản tươi sống chuẩn Nhật" }
```
**Cái 3: Đồ uống**
```json
{ "name": "Đồ Uống", "description": "Trà, bia và nước giải khát" }
```

---

## Bước 3: Nạp Món ăn (Products)

- **Method:** `POST`
- **URL:** `http://54.81.9.236:3000/api/products`
- **Lưu ý:** `category_id` phải khớp với ID sinh ra ở Bước 2 (thường là 1, 2, 3).

```json
{
    "name": "Sashimi Cá Hồi",
    "description": "Cá hồi Na Uy tươi thái lát",
    "price": 150000,
    "image": "/uploads/sashimi.jpg",
    "category_id": 2,
    "stock": 100,
    "isAvailable": true
}
```

```json
{
    "name": "Sushi Lươn Nhật",
    "description": "Lươn nướng sốt đặc biệt",
    "price": 120000,
    "category_id": 2,
    "stock": 50
}
```

---

## Bước 4: Nạp danh sách Bàn (Bulk Tables)

- **Method:** `POST`
- **URL:** `http://54.81.9.236:3000/api/tables/bulk`

```json
[
  { "tableNumber": 1, "capacity": 2, "qrCode": "TABLE_01", "status": "AVAILABLE" },
  { "tableNumber": 2, "capacity": 4, "qrCode": "TABLE_02", "status": "AVAILABLE" },
  { "tableNumber": 3, "capacity": 6, "qrCode": "TABLE_03", "status": "AVAILABLE" },
  { "tableNumber": 4, "capacity": 8, "qrCode": "TABLE_04", "status": "AVAILABLE" },
  { "tableNumber": 5, "capacity": 4, "qrCode": "TABLE_05", "status": "AVAILABLE" }
]
```

---

## Bước 5: Tạo dữ liệu để test Thống kê (Stats)

Để thống kê có dữ liệu, bạn cần tạo đơn hàng và giả lập thanh toán hoàn tất.

1. **Tạo đơn hàng:** `POST /api/orders`
```json
{
  "table_id": 1,
  "user_id": 1,
  "items": [
    { "product_id": 1, "quantity": 2 }
  ]
}
```
> Lấy `id` đơn hàng trả về (ví dụ là `1`).

2. **Thanh toán đơn hàng:** `PUT /api/orders/1/pay` (Không cần body).

3. **Hoàn thành trạng thái:** `PUT /api/orders/1/status`
```json
{ "status": "COMPLETED" }
```

4. **Kiểm tra kết quả:** Gọi `GET /api/stats?type=day` để xem doanh thu đã được cộng vào chưa.

---
*Tài liệu hướng dẫn nạp dữ liệu cho AppDatMon.*
