const express = require('express');
const router = express.Router();
const kitchenController = require('../controllers/kitchenController');
const { verifyToken, isKitchenOrAdmin } = require('../middlewares/authMiddleware');

// Get all waiting/cooking items
router.get('/items', verifyToken, isKitchenOrAdmin, kitchenController.getKitchenItems);

// Update status of an item (COOKING, DONE)
router.put('/items/:id/status', verifyToken, isKitchenOrAdmin, kitchenController.updateKitchenItemStatus);

// Create test item (from testing console in Admin)
router.post('/test-item', verifyToken, isKitchenOrAdmin, kitchenController.createTestKitchenItem);

module.exports = router;
