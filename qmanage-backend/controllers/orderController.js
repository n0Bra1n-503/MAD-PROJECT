const db = require('../config/db');

// POST /api/orders
const placeOrder = async (req, res) => {
    try {
        const { userId, outletId, totalAmount, items } = req.body;

        if (!userId || !outletId || !items || items.length === 0) {
            return res.status(400).json({ success: false, message: 'Missing order details' });
        }

        // Generate a simple token number (e.g., QM-1234)
        const tokenNumber = 'QM-' + Math.floor(1000 + Math.random() * 9000);

        // Call the stored procedure (demonstrating Stored Procedures, Transactions, and ACID)
        await db.query(
            'CALL sp_place_order(?, ?, ?, ?, @p_order_id)',
            [userId, outletId, totalAmount, tokenNumber]
        );

        // Get the order ID returned by the procedure
        const [[{ orderId }]] = await db.query('SELECT @p_order_id AS orderId');

        // 2. Insert items into order_items table
        for (const item of items) {
            await db.query(
                'INSERT INTO order_items (order_id, menu_item_id, quantity, price) VALUES (?, ?, ?, ?)',
                [orderId, item.menuItemId, item.quantity, item.price]
            );
        }

        res.status(201).json({
            success: true,
            message: 'Order placed successfully',
            orderId,
            tokenNumber
        });

    } catch (error) {
        console.error('Place Order Error:', error.message);
        res.status(500).json({ success: false, message: 'Error placing order' });
    }
};

// GET /api/orders/user/:userId
const getUserOrders = async (req, res) => {
    try {
        const { userId } = req.params;
        // Demonstrating SUBQUERIES: Finding total item count for each order
        const [orders] = await db.query(
            `SELECT o.*, ot.name as outletName, ot.image_url as outletImage,
             (SELECT SUM(quantity) FROM order_items WHERE order_id = o.id) as totalItems
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
        // Demonstrating VIEWS: Using the pre-defined view_order_details
        const [orders] = await db.query(
            'SELECT * FROM view_order_details WHERE outlet_id = ? ORDER BY created_at DESC',
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

// GET /api/orders/:orderId
const getOrderById = async (req, res) => {
    try {
        const { orderId } = req.params;
        const [orders] = await db.query(
            `SELECT o.*, ot.name as outletName, ot.image_url as outletImage 
             FROM orders o 
             JOIN outlets ot ON o.outlet_id = ot.id 
             WHERE o.id = ?`,
            [orderId]
        );

        if (orders.length === 0) {
            return res.status(404).json({ success: false, message: 'Order not found' });
        }

        res.json({ success: true, order: orders[0] });
    } catch (error) {
        console.error('Get Order By Id Error:', error.message);
        res.status(500).json({ success: false, message: 'Error fetching order' });
    }
};

module.exports = { placeOrder, getUserOrders, getOutletOrders, updateOrderStatus, getOrderById };
