const { User } = require('../models');

// Lấy thông tin cá nhân
const getUserProfile = async (req, res) => {
    try {
        // userId sẽ được lấy từ middleware xác thực (authMiddleware) sau này
        const userId = req.user.id;
        const user = await User.findByPk(userId, {
            attributes: { exclude: ['password'] }
        });

        if (!user) {
            return res.status(404).json({ message: "Không tìm thấy người dùng" });
        }

        res.json(user);
    } catch (error) {
        res.status(500).json({ message: "Lỗi server", error: error.message });
    }
};

module.exports = {
    getUserProfile
};
