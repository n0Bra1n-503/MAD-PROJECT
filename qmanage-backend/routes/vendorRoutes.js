const express = require('express');
const router = express.Router();
const { login, getPerformance } = require('../controllers/vendorController');

router.post('/login', login);
router.get('/performance/:id', getPerformance);


module.exports = router;
