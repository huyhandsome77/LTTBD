const express = require('express');
const router = express.Router();
const userController = require('../controllers/userController');
const { verifyToken } = require('../middlewares/authMiddleware');

// API Lấy profile: GET http://localhost:3000/api/users/profile
// Cần gửi Header: Authorization: Bearer <token>
router.get('/profile', verifyToken, userController.getUserProfile);

module.exports = router;
