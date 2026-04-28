const db = require('./config/db');

async function checkTable() {
    try {
        console.log('Columns in menu_items:');
        const [menuColumns] = await db.query('DESCRIBE menu_items');
        console.table(menuColumns);

        console.log('\nColumns in outlets:');
        const [outletColumns] = await db.query('DESCRIBE outlets');
        console.table(outletColumns);

        process.exit(0);
    } catch (error) {
        console.error('Error:', error.message);
        process.exit(1);
    }
}

checkTable();
