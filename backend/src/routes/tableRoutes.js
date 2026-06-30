const express = require('express');
const router = express.Router();
const tableController = require('../controllers/tableController');

router.get('/', tableController.getAllTables);
router.put('/:id/status', tableController.updateTableStatus);

module.exports = router;
