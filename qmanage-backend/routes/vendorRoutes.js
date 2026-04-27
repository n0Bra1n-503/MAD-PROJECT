const express = require('express');
const router = express.Router();
const { login } = require('../controllers/vendorController');

router.post('/login', login);

module.exports = router;
