const app = require('./src/app');
const dotenv = require('dotenv');
const { connectDB, sequelize } = require('./src/models');

dotenv.config();

const PORT = process.env.PORT || 3000;

// Connect to Database and sync models
connectDB();

// Đồng bộ database. Lưu ý: tránh lạm dụng { alter: true } vì MySQL giới hạn 64 indexes (keys)
sequelize.sync().then(() => {
    console.log('Database synced');
    app.listen(PORT, () => {
        console.log(`Server is running on port ${PORT}`);
    });
}).catch(err => {
    console.error('Failed to sync database:', err);
});
