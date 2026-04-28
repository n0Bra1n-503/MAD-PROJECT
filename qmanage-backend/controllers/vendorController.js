const db = require('../config/db');

// POST /api/vendors/login
const login = async (req, res) => {
    try {
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).json({ success: false, message: 'Missing email or password' });
        }

        // Search for outlet with matching email and password
        // Note: In production, use bcrypt to compare hashed passwords
        const [rows] = await db.query(
            'SELECT id, name, email FROM outlets WHERE email = ? AND password = ?',
            [email, password]
        );

        if (rows.length === 0) {
            return res.status(401).json({ success: false, message: 'Invalid credentials' });
        }

        const vendor = rows[0];
        res.json({
            success: true,
            message: 'Login successful',
            vendor: {
                id: vendor.id,
                name: vendor.name,
                email: vendor.email
            }
        });

    } catch (error) {
        console.error('Vendor Login Error:', error.message);
        res.status(500).json({ success: false, message: 'Server error during login' });
    }
};

// GET /api/vendors/performance/:id
const getPerformance = async (req, res) => {
    try {
        const { id } = req.params;
        const [rows] = await db.query(
            'SELECT * FROM view_outlet_performance WHERE name = (SELECT name FROM outlets WHERE id = ?)',
            [id]
        );

        if (rows.length === 0) {
            return res.json({ 
                success: true, 
                performance: { total_orders: 0, avg_rating: 0, revenue: 0 } 
            });
        }

        res.json({ success: true, performance: rows[0] });
    } catch (error) {
        console.error('Get Performance Error:', error.message);
        res.status(500).json({ success: false, message: 'Error fetching performance' });
    }
};

module.exports = { login, getPerformance };

