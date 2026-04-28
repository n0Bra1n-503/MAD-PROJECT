const express = require('express');
const router = express.Router();
const { addReview, getOutletReviews } = require('../controllers/reviewController');

router.post('/', addReview);
router.get('/outlet/:outletId', getOutletReviews);

module.exports = router;
