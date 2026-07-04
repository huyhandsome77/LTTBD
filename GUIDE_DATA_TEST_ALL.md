# Tài liệu nạp dữ liệu Test toàn diện (Full Data Test)

Tài liệu này tổng hợp tất cả các Request JSON để bạn nạp vào Postman, giúp kiểm tra toàn bộ tính năng của hệ thống AppDatMon.

---

## 1. Đăng nhập (Lấy Token Admin)
*Cần thực hiện trước để lấy Token cho các bước sau.*

- **URL:** `http://54.81.9.236:3000/api/auth/login`
- **Method:** `POST`
- **Body:**
```json
{
    "account": "admin",
    "password": "123"
}
```
> **Action:** Copy `token` -> Tab **Authorization** -> **Bearer Token** -> Dán vào.

---

## 2. Nạp Danh mục (Categories)
- **URL:** `http://54.81.9.236:3000/api/categories`
- **Method:** `POST`

**Mẫu 1:**
```json
{ "name": "Khai Vị", "description": "Súp và Salad" }
```
**Mẫu 2:**
```json
{ "name": "Sushi & Sashimi", "description": "Hải sản tươi sống" }
```

---

## 3. Nạp Sản phẩm (Products)
- **URL:** `http://54.81.9.236:3000/api/products`
- **Method:** `POST`
*Lưu ý: `category_id` phải khớp với ID danh mục vừa tạo.*

**Mẫu 1:**
```json
{
    "name": "Sashimi Cá Hồi",
    "description": "Cá hồi Na Uy tươi sống",
    "price": 150000,
    "category_id": 2,
    "stock": 100,
    "isAvailable": true
}
```

---

## 4. Nạp danh sách Bàn (Tables)
- **URL:** `http://54.81.9.236:3000/api/tables/bulk`
- **Method:** `POST`

```json
[
  { "tableNumber": 1, "capacity": 2, "qrCode": "TABLE_01", "status": "AVAILABLE" },
  { "tableNumber": 2, "capacity": 4, "qrCode": "TABLE_02", "status": "AVAILABLE" },
  { "tableNumber": 3, "capacity": 6, "qrCode": "TABLE_03", "status": "AVAILABLE" }
]
```

---

## 5. Thêm Đơn hàng (Orders) - QUAN TRỌNG
- **URL:** `http://54.81.9.236:3000/api/orders`
- **Method:** `POST`

**Mẫu Đơn hàng tại bàn (Có User):**
```json
{
    "table_id": 1,
    "user_id": 1,
    "note": "Cho nhiều gừng hồng",
    "items": [
        {
            "product_id": 1,
            "quantity": 2,
            "note": "Cắt lát mỏng"
        }
    ]
}
```

**Mẫu Đơn hàng khách vãng lai (Quét QR không login):**
```json
{
    "table_id": 2,
    "user_id": null,
    "note": "Khách vãng lai thanh toán sau",
    "items": [
        {
            "product_id": 1,
            "quantity": 1
        }
    ]
}
```

---

## 6. Gửi Đánh giá (Reviews)
- **URL:** `http://54.81.9.236:3000/api/reviews`
- **Method:** `POST`

```json
{
    "user_id": 1,
    "phone": "0987654321",
    "dish_name": "Sashimi Cá Hồi",
    "content": "Món ăn rất tươi, phục vụ tốt!",
    "rating": 5
}
```

---

## 7. Đặt bàn trước (Reservations)
- **URL:** `http://54.81.9.236:3000/api/reservations`
- **Method:** `POST`

```json
{
    "guestName": "Lê Huy Quân",
    "guestPhone": "0123456789",
    "reservationTime": "2023-12-25T19:00:00.000Z",
    "numberOfGuests": 4,
    "note": "Tiệc sinh nhật",
    "user_id": 1
}
```

---

## 8. Tích điểm thủ công (Points)
- **URL:** `http://54.81.9.236:3000/api/points/add-points`
- **Method:** `POST`
*Yêu cầu: Đơn hàng phải COMPLETED và PAID.*

```json
{
    "phone": "0123456789",
    "orderId": 1
}
```

---

## 9. Xem Thống kê (Stats)
- **URL:** `http://54.81.9.236:3000/api/stats?type=day&date=2023-10-22`
- **Method:** `GET`

---
*Lưu ý: Mọi thay đổi dữ liệu trên server EC2 cần thực hiện `pm2 restart backend` nếu bạn sửa code, nhưng với việc nạp dữ liệu qua API này thì dữ liệu sẽ vào thẳng Database ngay lập tức.*
