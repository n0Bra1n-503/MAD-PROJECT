const express = require('express');
const router = express.Router();
const { placeOrder, getUserOrders, getOutletOrders, updateOrderStatus } = require('../controllers/orderController');

router.post('/', placeOrder);
router.get('/user/:userId', getUserOrders);
router.get('/outlet/:outletId', getOutletOrders);
router.patch('/:orderId/status', updateOrderStatus);

module.exports = router;
