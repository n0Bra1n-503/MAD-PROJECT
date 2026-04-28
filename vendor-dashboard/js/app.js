// Configuration
const API_BASE_URL = 'http://localhost:8080/api';

// Authentication Check
const vendorData = JSON.parse(localStorage.getItem('qmanage_vendor'));
if (!vendorData) {
    window.location.href = 'login.html';
}

const OUTLET_ID = vendorData.id;
const OUTLET_NAME = vendorData.name;

// State
let orders = [];
let menuItems = [];

// DOM Elements
const activeQueueBody = document.querySelector('#activeQueueTable tbody');
const totalRevenueEl = document.getElementById('totalRevenue');
const totalOrdersEl = document.getElementById('totalOrders');
const pendingOrdersEl = document.getElementById('pendingOrders');
const menuGridEl = document.getElementById('menuItemsGrid');
const outletStatusToggle = document.getElementById('outletStatusToggle');
const statusText = document.getElementById('statusText');
const outletNameDisplay = document.getElementById('outletName');
const logoutBtn = document.getElementById('logoutBtn');

// Set UI from Login Info
outletNameDisplay.textContent = OUTLET_NAME;
document.querySelector('.avatar').textContent = OUTLET_NAME.charAt(0);

// Logout
logoutBtn.addEventListener('click', () => {
    localStorage.removeItem('qmanage_vendor');
    window.location.href = 'login.html';
});

// Navigation
document.querySelectorAll('.sidebar li').forEach(li => {
    li.addEventListener('click', () => {
        document.querySelectorAll('.sidebar li').forEach(el => el.classList.remove('active'));
        li.classList.add('active');
        
        const viewId = li.getAttribute('data-view');
        document.querySelectorAll('.view').forEach(view => view.style.display = 'none');
        document.getElementById(`${viewId}View`).style.display = 'block';
        document.getElementById('viewTitle').textContent = li.textContent.trim();
        
        if (viewId === 'menu') fetchMenu();
        if (viewId === 'dashboard') fetchOrders();
    });
});

// Fetch Orders
async function fetchOrders() {
    try {
        const response = await fetch(`${API_BASE_URL}/orders/outlet/${OUTLET_ID}`);
        const data = await response.json();
        
        if (data.success) {
            orders = data.orders;
            renderDashboard();
        }
    } catch (error) {
        console.error('Fetch Orders Error:', error);
    }
}

// Render Dashboard
function renderDashboard() {
    // Stats
    const totalRevenue = orders.reduce((sum, o) => sum + parseFloat(o.total_amount), 0);
    const pendingCount = orders.filter(o => o.status !== 'completed' && o.status !== 'Cancelled').length;
    
    totalRevenueEl.textContent = `Rs. ${totalRevenue.toFixed(0)}`;
    totalOrdersEl.textContent = orders.length;
    pendingOrdersEl.textContent = pendingCount;

    // Active Queue Table
    activeQueueBody.innerHTML = '';
    const activeOrders = orders.filter(o => o.status !== 'Completed' && o.status !== 'Cancelled');
    
    if (activeOrders.length === 0) {
        activeQueueBody.innerHTML = '<tr><td colspan="5" style="text-align: center; padding: 40px; color: #64748B;">No active orders in the queue</td></tr>';
        return;
    }

    activeOrders.forEach(order => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td style="font-weight: 700; color: #F59E0B;">${order.token_number}</td>
            <td>${order.userName || 'Student'}</td>
            <td>Rs. ${parseFloat(order.total_amount).toFixed(0)}</td>
            <td><span class="badge badge-${order.status.toLowerCase()}">${order.status}</span></td>
            <td>
                ${getNextStatusButton(order)}
            </td>
        `;
        activeQueueBody.appendChild(tr);
    });
}

function getNextStatusButton(order) {
    const status = order.status.toLowerCase();
    if (status === 'received') {
        return `<button class="btn btn-primary btn-sm" onclick="updateStatus(${order.id}, 'Preparing')">Start Preparing</button>`;
    } else if (status === 'preparing') {
        return `<button class="btn btn-primary btn-sm" style="background-color: #10B981;" onclick="updateStatus(${order.id}, 'Ready')">Mark Ready</button>`;
    } else if (status === 'ready') {
        return `<button class="btn btn-secondary btn-sm" onclick="updateStatus(${order.id}, 'Completed')">Complete</button>`;
    }
    return '';
}

// Update Order Status
async function updateStatus(orderId, newStatus) {
    try {
        const response = await fetch(`${API_BASE_URL}/orders/${orderId}/status`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: newStatus })
        });
        
        if (response.ok) {
            fetchOrders();
        }
    } catch (error) {
        console.error('Update Status Error:', error);
    }
}

// Fetch Menu
async function fetchMenu() {
    try {
        const response = await fetch(`${API_BASE_URL}/outlets/${OUTLET_ID}/menu`);
        const data = await response.json();
        
        if (data.success) {
            menuItems = data.menuItems;
            renderMenu();
        }
    } catch (error) {
        console.error('Fetch Menu Error:', error);
    }
}

// Render Menu
function renderMenu() {
    menuGridEl.innerHTML = '';
    if (menuItems.length === 0) {
        menuGridEl.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 40px; color: #64748B;">No menu items found. Add your first item!</div>';
        return;
    }

    menuItems.forEach(item => {
        const div = document.createElement('div');
        div.className = 'menu-item-card';
        // Get full URL if it's a relative path
        const imgUrl = item.image_url ? (item.image_url.startsWith('http') ? item.image_url : `${API_BASE_URL.replace('/api', '')}${item.image_url}`) : 'https://via.placeholder.com/150';
        
        div.innerHTML = `
            <img src="${imgUrl}" alt="${item.name}" class="item-img" style="width: 100%; height: 120px; object-fit: cover; border-radius: 8px; margin-bottom: 12px;">
            <div class="item-info">
                <h4>${item.name} ${item.is_veg ? '🟢' : '🔴'}</h4>
                <p>${item.description}</p>
                <div class="item-price">Rs. ${parseFloat(item.price).toFixed(0)}</div>
            </div>
            <div class="item-actions">
                <button class="btn btn-secondary btn-sm" onclick="toggleAvailability(${item.id}, ${!item.is_available})">
                    ${item.is_available ? 'Hide' : 'Show'}
                </button>
            </div>
        `;
        menuGridEl.appendChild(div);
    });
}

// Toggle Outlet Status
outletStatusToggle.addEventListener('change', async (e) => {
    const isOpen = e.target.checked;
    statusText.textContent = isOpen ? 'Open' : 'Closed';
    
    try {
        await fetch(`${API_BASE_URL}/outlets/${OUTLET_ID}/status`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ isOpen })
        });
    } catch (error) {
        console.error('Toggle Status Error:', error);
    }
});

// Modal Logic
function showAddItemModal() { document.getElementById('addItemModal').style.display = 'flex'; }
function hideAddItemModal() { document.getElementById('addItemModal').style.display = 'none'; }

document.getElementById('addItemForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);
    const newItem = {
        name: formData.get('name'),
        description: formData.get('description'),
        price: formData.get('price'),
        isVeg: formData.get('isVeg') === '1',
        category: 'Main Course' // Changed from categories to category
    };

    try {
        const response = await fetch(`${API_BASE_URL}/outlets/${OUTLET_ID}/menu`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newItem)
        });
        
        if (response.ok) {
            hideAddItemModal();
            fetchMenu();
        }
    } catch (error) {
        console.error('Add Item Error:', error);
    }
});

// Initial Load
fetchOrders();
// Poll for new orders every 10 seconds
setInterval(fetchOrders, 10000);
