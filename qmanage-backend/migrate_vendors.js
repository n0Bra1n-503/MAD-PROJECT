const db = require('./config/db');

async function migrate() {
    try {
        console.log('Starting migration...');
        
        // 1. Add email and password columns to outlets table
        await db.query(`
            ALTER TABLE outlets 
            ADD COLUMN email VARCHAR(255) UNIQUE AFTER name,
            ADD COLUMN password VARCHAR(255) AFTER email
        `);
        console.log('✅ Added email and password columns to outlets table');

        // 2. Add credentials for existing outlets
        const credentials = [
            { name: 'Campus Cafe', email: 'cafe@qmanage.com', password: 'cafe123' },
            { name: 'Food Court', email: 'foodcourt@qmanage.com', password: 'food123' },
            { name: 'Juice Bar', email: 'juice@qmanage.com', password: 'juice123' },
            { name: 'Quick Bites', email: 'quick@qmanage.com', password: 'quick123' }
        ];

        for (const cred of credentials) {
            await db.query(
                'UPDATE outlets SET email = ?, password = ? WHERE name = ?',
                [cred.email, cred.password, cred.name]
            );
        }
        console.log('✅ Updated existing outlets with credentials');
        
        console.log('Migration completed successfully!');
        process.exit(0);
    } catch (error) {
        console.error('❌ Migration failed:', error.message);
        process.exit(1);
    }
}

migrate();
