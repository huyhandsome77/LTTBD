const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');

router.post('/register', authController.register);
router.post('/login', authController.login);
router.git remote add origin https://github.com/huyhandsome77/AppDatMon.gitget('/test', (req, res) => res.send('Auth route is working!'));

module.exports = router;
