const express = require('express');
const router = express.Router();
const orderController = require('../controllers/orderController');

router.get('/table/:tableId', orderController.getCurrentOrderByTable);
router.put('/:id/pay', orderController.payOrder);

module.exports = router;
