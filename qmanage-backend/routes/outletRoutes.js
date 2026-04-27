const express = require('express');
const router = express.Router();
const { getAllOutlets, getOutletMenu, addMenuItem, updateMenuItem, toggleOutletStatus } = require('../controllers/outletController');

router.get('/', getAllOutlets);
router.get('/:id/menu', getOutletMenu);
router.post('/:id/menu', addMenuItem);
router.patch('/menu/:itemId', updateMenuItem);
router.patch('/:id/status', toggleOutletStatus);

module.exports = router;
