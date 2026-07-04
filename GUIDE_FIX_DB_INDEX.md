# Hướng dẫn xử lý triệt để lỗi "Too many keys" trên MySQL EC2

Tài liệu này hướng dẫn cách khắc phục lỗi `ER_TOO_MANY_KEYS` (Vượt quá giới hạn 64 index trên một bảng) do quá trình `sequelize.sync({ alter: true })` gây ra rác thải index.

---

## PHƯƠNG ÁN 1: Reset Database (Khuyên dùng - Nhanh & Sạch)
*Lưu ý: Cách này sẽ xóa toàn bộ dữ liệu hiện có để tạo lại cấu trúc mới hoàn toàn sạch sẽ.*

### Bước 1: Cấu hình code để Reset
Trong file `backend/server.js`, tìm đến đoạn `sequelize.sync()` và sửa thành:
```javascript
sequelize.sync({ force: true }).then(async () => {
    // ... code phía sau
});
```

### Bước 2: Thực thi trên EC2
Mở Terminal của EC2 và chạy lệnh:
```bash
pm2 restart backend
# Chờ vài giây để server xóa và tạo lại bảng
pm2 logs backend
```
Khi thấy dòng `Database synced` trong log, nghĩa là cấu trúc đã được làm sạch.

### Bước 3: Đưa code về trạng thái an toàn
Sửa lại `backend/server.js` về mặc định để tránh mất dữ liệu trong tương lai:
```javascript
sequelize.sync().then(async () => {
    // ...
});
```
Sau đó chạy lại `pm2 restart backend`.

---

## PHƯƠNG ÁN 2: Dọn dẹp thủ công (Giữ lại dữ liệu)
*Sử dụng nếu bạn có dữ liệu quan trọng không muốn xóa.*

### Bước 1: Truy cập MySQL CLI trên EC2
```bash
# Thay đổi thông tin theo file .env của bạn
mysql -u root -p
USE database_name_cua_ban;
```

### Bước 2: Kiểm tra các Index dư thừa
```sql
SHOW INDEX FROM users;
```
Bạn sẽ thấy rất nhiều index trùng lặp như `email`, `email_2`, `email_3`, `phone_2`,...

### Bước 3: Xóa các Index rác
Chạy lệnh `DROP INDEX` cho đến khi số lượng index còn dưới 64 (tốt nhất là chỉ để lại các index cần thiết):
```sql
ALTER TABLE users DROP INDEX email_2;
ALTER TABLE users DROP INDEX email_3;
ALTER TABLE users DROP INDEX phone_2;
-- Tiếp tục cho các bảng khác nếu bị lỗi tương tự
```

---

## PHÒNG NGỪA TÁI PHÁT (Quan trọng)

1. **KHÔNG sử dụng `{ alter: true }` trên môi trường Production (EC2):** Chỉ nên dùng ở máy cá nhân (Local). Trên EC2 nên dùng Migrations hoặc `sequelize.sync()` mặc định.
2. **Loại bỏ `unique: true` không cần thiết:** Trong các Model (VD: `User.js`), nếu một cột đã có index, hạn chế bật đi bật lại thuộc tính `unique` khiến Sequelize tạo thêm index mới mỗi lần restart.
3. **Sử dụng `updated_at` chuẩn:** Đảm bảo các model đều có `timestamps: true` để Sequelize quản lý đồng bộ.

---
*Tài liệu được khởi tạo để hỗ trợ dự án AppDatMon.*
