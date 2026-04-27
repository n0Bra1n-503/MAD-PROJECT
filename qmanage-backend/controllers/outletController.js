const db = require('../config/db');

// GET /api/outlets
const getAllOutlets = async (req, res) => {
    try {
        const [outlets] = await db.query('SELECT * FROM outlets ORDER BY is_open DESC, rating DESC');
        res.json({ success: true, outlets });
    } catch (error) {
        console.error('Get Outlets Error:', error.message);
        res.status(500).json({ success: false, message: 'Error fetching outlets' });
    }
};

// GET /api/outlets/:id/menu
const getOutletMenu = async (req, res) => {
    try {
        const { id } = req.params;
        const [menuItems] = await db.query(
            'SELECT * FROM menu_items WHERE outlet_id = ? AND is_available = true',
            [id]
        );
        res.json({ success: true, menuItems });
    } catch (error) {
        console.error('Get Menu Error:', error.message);
        res.status(500).json({ success: false, message: 'Error fetching menu items' });
    }
};

// POST /api/outlets/:id/menu
const addMenuItem = async (req, res) => {
    try {
        const { id } = req.params;
        const { name, description, price, isVeg, categories } = req.body;

        await db.query(
            'INSERT INTO menu_items (outlet_id, name, description, price, is_veg, categories) VALUES (?, ?, ?, ?, ?, ?)',
            [id, name, description, price, isVeg, categories]
        );

        res.status(201).json({ success: true, message: 'Menu item added' });
    } catch (error) {
        console.error('Add Menu Item Error:', error.message);
        res.status(500).json({ success: false, message: 'Error adding menu item' });
    }
};

// PATCH /api/outlets/menu/:itemId
const updateMenuItem = async (req, res) => {
    try {
        const { itemId } = req.params;
        const { name, description, price, isVeg, isAvailable } = req.body;

        await db.query(
            'UPDATE menu_items SET name = ?, description = ?, price = ?, is_veg = ?, is_available = ? WHERE id = ?',
            [name, description, price, isVeg, isAvailable, itemId]
        );

        res.json({ success: true, message: 'Menu item updated' });
    } catch (error) {
        console.error('Update Menu Item Error:', error.message);
        res.status(500).json({ success: false, message: 'Error updating menu item' });
    }
};

// PATCH /api/outlets/:id/status
const toggleOutletStatus = async (req, res) => {
    try {
        const { id } = req.params;
        const { isOpen } = req.body;

        await db.query('UPDATE outlets SET is_open = ? WHERE id = ?', [isOpen, id]);
        res.json({ success: true, message: `Outlet is now ${isOpen ? 'OPEN' : 'CLOSED'}` });
    } catch (error) {
        console.error('Toggle Outlet Status Error:', error.message);
        res.status(500).json({ success: false, message: 'Error toggling status' });
    }
};

module.exports = { 
    getAllOutlets, 
    getOutletMenu, 
    addMenuItem, 
    updateMenuItem, 
    toggleOutletStatus 
};
