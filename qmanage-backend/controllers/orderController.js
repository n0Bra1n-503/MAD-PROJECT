const db = require('../config/db');

// POST /api/orders
const placeOrder = async (req, res) => {
    let connection;
    try {
        const { userId, outletId, totalAmount, items } = req.body;

        if (!userId || !outletId || !items || items.length === 0) {
            return res.status(400).json({ success: false, message: 'Missing order details' });
        }

        // Generate a simple token number (e.g., QM-1234)
        const tokenNumber = 'QM-' + Math.floor(1000 + Math.random() * 9000);

        // Start transaction
        connection = await db.getConnection();
        await connection.beginTransaction();

        // 1. Insert into orders table
        const [orderResult] = await connection.query(
            'INSERT INTO orders (user_id, outlet_id, total_amount, token_number) VALUES (?, ?, ?, ?)',
            [userId, outletId, totalAmount, tokenNumber]
        );
        const orderId = orderResult.insertId;

        // 2. Insert items into order_items table
        for (const item of items) {
            await connection.query(
                'INSERT INTO order_items (order_id, menu_item_id, quantity, price) VALUES (?, ?, ?, ?)',
                [orderId, item.menuItemId, item.quantity, item.price]
            );
        }

        await connection.commit();
        res.status(201).json({
            success: true,
            message: 'Order placed successfully',
            orderId,
            tokenNumber
        });

    } catch (error) {
        if (connection) await connection.rollback();
        console.error('Place Order Error:', error.message);
        res.status(500).json({ success: false, message: 'Error placing order' });
    } finally {
        if (connection) connection.release();
    }
};

// GET /api/orders/user/:userId
const getUserOrders = async (req, res) => {
    try {
        const { userId } = req.params;
        const [orders] = await db.query(
            `SELECT o.*, ot.name as outletName, ot.image_url as outletImage 
             FROM orders o 
             JOIN outlets ot ON o.outlet_id = ot.id 
             WHERE o.user_id = ? 
             ORDER BY o.created_at DESC`,
            [userId]
        );
        res.json({ success: true, orders });
    } catch (error) {
        console.error('Get User Orders Error:', error.message);
        res.status(500).json({ success: false, message: 'Error fetching orders' });
    }
};

// GET /api/orders/outlet/:outletId
const getOutletOrders = async (req, res) => {
    try {
        const { outletId } = req.params;
        const [orders] = await db.query(
            `SELECT o.*, u.name as userName 
             FROM orders o 
             JOIN users u ON o.user_id = u.id 
             WHERE o.outlet_id = ? 
             ORDER BY o.created_at DESC`,
            [outletId]
        );
        res.json({ success: true, orders });
    } catch (error) {
        console.error('Get Outlet Orders Error:', error.message);
        res.status(500).json({ success: false, message: 'Error fetching orders' });
    }
};

// PATCH /api/orders/:orderId/status
const updateOrderStatus = async (req, res) => {
    try {
        const { orderId } = req.params;
        const { status } = req.body; // 'received', 'preparing', 'ready', 'completed'

        await db.query(
            'UPDATE orders SET status = ? WHERE id = ?',
            [status, orderId]
        );

        res.json({ success: true, message: 'Order status updated' });
    } catch (error) {
        console.error('Update Order Status Error:', error.message);
        res.status(500).json({ success: false, message: 'Error updating status' });
    }
};

module.exports = { placeOrder, getUserOrders, getOutletOrders, updateOrderStatus };
