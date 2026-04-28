const db = require('../config/db');

// POST /api/reviews
const addReview = async (req, res) => {
    try {
        const { userId, outletId, rating, comment } = req.body;
        console.log('📝 Received Review:', { userId, outletId, rating, comment });

        if (!userId || !outletId || !rating) {
            console.error('❌ Missing review fields:', { userId, outletId, rating });
            return res.status(400).json({ success: false, message: 'Missing review details' });
        }


        await db.query(
            'INSERT INTO reviews (user_id, outlet_id, rating, comment) VALUES (?, ?, ?, ?)',
            [userId, outletId, rating, comment]
        );

        // Update outlet rating (Average of all reviews)
        const [avgRows] = await db.query(
            'SELECT AVG(rating) as avgRating FROM reviews WHERE outlet_id = ?',
            [outletId]
        );
        
        const newRating = avgRows[0].avgRating || 0;
        await db.query('UPDATE outlets SET rating = ? WHERE id = ?', [newRating, outletId]);

        res.status(201).json({ success: true, message: 'Review added successfully' });
    } catch (error) {
        console.error('Add Review Error:', error.message);
        res.status(500).json({ success: false, message: 'Error adding review' });
    }
};

// GET /api/reviews/outlet/:outletId
const getOutletReviews = async (req, res) => {
    try {
        const { outletId } = req.params;
        const [reviews] = await db.query(
            `SELECT r.*, u.name as userName 
             FROM reviews r 
             JOIN users u ON r.user_id = u.id 
             WHERE r.outlet_id = ? 
             ORDER BY r.created_at DESC`,
            [outletId]
        );
        res.json({ success: true, reviews });
    } catch (error) {
        console.error('Get Reviews Error:', error.message);
        res.status(500).json({ success: false, message: 'Error fetching reviews' });
    }
};

module.exports = { addReview, getOutletReviews };
