const express = require('express');
const cors = require('cors');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 8080;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Routes
const userRoutes = require('./routes/userRoutes');
const outletRoutes = require('./routes/outletRoutes');
const orderRoutes = require('./routes/orderRoutes');
const vendorRoutes = require('./routes/vendorRoutes');

app.use('/api/users', userRoutes);
app.use('/api/outlets', outletRoutes);
app.use('/api/orders', orderRoutes);
app.use('/api/vendors', vendorRoutes);

// Health check endpoint
app.get('/', (req, res) => {
    res.json({
        message: 'Qmanage API is running',
        version: '1.0.0',
        endpoints: {
            users: '/api/users/register | /api/users/login'
        }
    });
});

// 404 handler
app.use((req, res) => {
    res.status(404).json({
        success: false,
        message: `Route ${req.originalUrl} not found`
    });
});

// Start server
app.listen(PORT, '0.0.0.0', () => {
    console.log(`🚀 Server running on port ${PORT}`);
    console.log(`   Local: http://localhost:${PORT}`);
    console.log(`   Network: http://0.0.0.0:${PORT}`);
});
