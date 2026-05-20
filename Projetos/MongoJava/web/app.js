// ========== GLOBAL STATE ==========
let currentUser = null;
let aboutDialog = null;
let deliveryFreights = [];
let deliveryProducts = [];
const today = new Date().toISOString().slice(0, 10);

// Helpers para pegar o output correto por página
function getOutputEl() {
    const active = document.querySelector('.page-content.active');
    if (!active) return document.getElementById('output');
    const id = active.id;
    if (id === 'page-users') return document.getElementById('output-users');
    return document.getElementById('output');
}

function setOutput(value) {
    const el = getOutputEl();
    if (!el) return;
    el.textContent = typeof value === 'string' ? value : JSON.stringify(value, null, 2);
}

window.addEventListener('load', () => {
    aboutDialog = document.getElementById('aboutDialog');

    const dateInput = document.getElementById('deliveryDate');
    if (dateInput) dateInput.value = today;

    loadDeliveryCatalog();
});

// ========== LOGIN ==========
function handleLogin(event) {
    event.preventDefault();

    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value.trim();

    if ((username === 'brayan' || username === 'brayan@email.com') && password === 'senha123') {
        currentUser = { username: 'brayan', email: 'brayan@email.com' };

        document.getElementById('page-login').classList.remove('active');
        document.getElementById('app-wrapper').style.display = 'flex';
        document.getElementById('userDisplay').textContent = username;

        showPage('dashboard');
        loadDashboardStats();
    } else {
        alert('Usuário ou senha inválidos. Tente: brayan / senha123');
    }
}

function handleLogout() {
    currentUser = null;
    document.getElementById('app-wrapper').style.display = 'none';
    document.getElementById('page-login').classList.add('active');
    document.getElementById('loginForm').reset();
}

// ========== NAVIGATION ==========
function showPage(pageId) {
    document.querySelectorAll('.page-content').forEach(p => p.classList.remove('active'));
    document.querySelectorAll('.drawer-item').forEach(b => b.classList.remove('active'));

    const page = document.getElementById('page-' + pageId);
    if (page) page.classList.add('active');

    // Marca o botão do drawer correto como ativo
    document.querySelectorAll('.drawer-item').forEach(btn => {
        const onclick = btn.getAttribute('onclick') || '';
        if (onclick.includes(`'${pageId}'`)) btn.classList.add('active');
    });

    // Fecha drawer em mobile
    if (window.innerWidth < 1024) {
        document.getElementById('drawer').classList.remove('open');
    }

    if (pageId === 'dashboard') loadDashboardStats();
    if (pageId === 'deliveries') renderDeliveryCatalog();
}

function toggleDrawer() {
    document.getElementById('drawer').classList.toggle('open');
}

// ========== DIALOG ==========
function openAboutDialog() {
    aboutDialog?.showModal?.();
}
function closeAboutDialog() {
    aboutDialog?.close?.();
}

// ========== HTTP HELPER ==========
async function request(path, options = {}) {
    const response = await fetch(path, {
        headers: { 'Content-Type': 'application/json' },
        ...options,
    });
    const text = await response.text();
    try { return JSON.parse(text); } catch { return text; }
}

async function copyOutput() {
    const text = getOutputEl()?.textContent || '';
    try {
        await navigator.clipboard.writeText(text);
        setOutput('Copiado para a área de transferência');
    } catch {
        setOutput('Não foi possível copiar neste navegador');
    }
}

// ========== DASHBOARD ==========
async function loadDashboardStats() {
    try {
        const [deliveries, users] = await Promise.all([
            request('/api/deliveries'),
            request('/api/users'),
        ]);

        let totalFreight = 0;

        if (Array.isArray(deliveries)) {
            document.getElementById('totalDeliveries').textContent = deliveries.length;
            deliveries.forEach(d => { totalFreight += Number(d.totalValue || d.freightValue || 0); });

            const recentDiv = document.getElementById('recentDeliveries');
            const items = deliveries.slice(-5).reverse();
            recentDiv.innerHTML = items.length
                ? items.map(d => `
                    <div class="delivery-item">
                        <strong>${d.cargo}</strong>
                        <p>${d.freightDescription || 'Frete ' + (d.freightCode || '')} &bull;
                           ${Array.isArray(d.products) ? d.products.length : 0} produtos &bull;
                           R$ ${Number(d.totalValue || 0).toFixed(2)}</p>
                    </div>`).join('')
                : '<p class="empty-state">Nenhuma entrega registrada</p>';
        }

        if (Array.isArray(users)) {
            document.getElementById('totalUsers').textContent = users.length;
        }

        const count = Array.isArray(deliveries) ? deliveries.length : 0;
        document.getElementById('totalRevenue').textContent = `R$ ${totalFreight.toFixed(2)}`;
        document.getElementById('avgFreight').textContent = `R$ ${count > 0 ? (totalFreight / count).toFixed(2) : '0,00'}`;

    } catch (err) {
        setOutput(`Erro ao carregar dashboard: ${err.message}`);
    }
}

// ========== USERS CRUD ==========
async function addUser() {
    const username = document.getElementById('userUsername').value.trim();
    const email = document.getElementById('userEmail').value.trim();
    if (!username || !email) { setOutput('Preencha todos os campos'); return; }
    const result = await request('/api/users', { method: 'POST', body: JSON.stringify({ username, email }) });
    setOutput(result);
    clearForms();
}

async function listUsers() {
    setOutput(await request('/api/users'));
}

async function getUser() {
    const username = document.getElementById('userUsernameSearch').value.trim();
    if (!username) { setOutput('Digite um username'); return; }
    setOutput(await request(`/api/users/${encodeURIComponent(username)}`));
}

async function updateUser() {
    const username = document.getElementById('userUsernameSearch').value.trim();
    const email = document.getElementById('userEmail').value.trim();
    if (!username || !email) { setOutput('Preencha username e email'); return; }
    setOutput(await request(`/api/users/${encodeURIComponent(username)}`, {
        method: 'PUT', body: JSON.stringify({ email }),
    }));
}

async function deleteUser() {
    const username = document.getElementById('userUsernameSearch').value.trim();
    if (!username) { setOutput('Digite um username'); return; }
    if (!confirm(`Remover usuário ${username}?`)) return;
    setOutput(await request(`/api/users/${encodeURIComponent(username)}`, { method: 'DELETE' }));
}

// ========== DELIVERIES CRUD ==========
async function addDelivery() {
    const freightCode = document.getElementById('freightCode').value;
    const cargo = document.getElementById('deliveryCargo').value.trim();
    const date = document.getElementById('deliveryDate').value;
    const productIds = getSelectedProductIds();

    if (!freightCode || !cargo || !date || productIds.length === 0) {
        setOutput('Preencha todos os campos obrigatórios e selecione ao menos um produto');
        return;
    }

    const result = await request('/api/deliveries', {
        method: 'POST',
        body: JSON.stringify({ cargo, date, freightCode, productIds }),
    });
    setOutput(result);
    clearForms();
}

async function listDeliveries() {
    const deliveries = await request('/api/deliveries');

    if (Array.isArray(deliveries)) {
        const listDiv = document.getElementById('deliveriesList');
        listDiv.innerHTML = deliveries.length
            ? deliveries.map(d => `
                <div class="delivery-item">
                    <strong>${d.cargo}</strong>
                    <p>${d.freightDescription || 'Frete ' + (d.freightCode || '')} &bull;
                       ${Array.isArray(d.products) ? d.products.map(p => p.name).join(', ') : 'Sem produtos'} &bull;
                       R$ ${Number(d.totalValue || 0).toFixed(2)}</p>
                </div>`).join('')
            : '<p class="empty-state">Nenhuma entrega encontrada</p>';
    }

    setOutput(deliveries);
}

// ========== FORM HELPERS ==========
function clearForms() {
    const fields = ['userUsername', 'userEmail', 'userUsernameSearch', 'deliveryCargo'];
    fields.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });

    const freightSel = document.getElementById('freightCode');
    if (freightSel) freightSel.value = deliveryFreights[0]?.code || '';

    const dateEl = document.getElementById('deliveryDate');
    if (dateEl) dateEl.value = today;

    document.querySelectorAll('.product-check').forEach(cb => { cb.checked = false; });
    updateDeliverySummary();
}

async function loadDeliveryCatalog() {
    try {
        const [freights, products] = await Promise.all([
            request('/api/freights'),
            request('/api/products'),
        ]);
        deliveryFreights = Array.isArray(freights) ? freights : [];
        deliveryProducts = Array.isArray(products) ? products : [];
        renderDeliveryCatalog();
    } catch (err) {
        setOutput(`Erro ao carregar catálogo: ${err.message}`);
    }
}

function renderDeliveryCatalog() {
    const freightSelect = document.getElementById('freightCode');
    const productCatalog = document.getElementById('productCatalog');

    if (freightSelect && deliveryFreights.length > 0) {
        freightSelect.innerHTML = deliveryFreights.map(f => `
            <option value="${f.code}">${f.code} — ${f.description} (R$ ${Number(f.value).toFixed(2)})</option>
        `).join('');
        if (!freightSelect.value) freightSelect.value = deliveryFreights[0].code;
    }

    if (productCatalog && deliveryProducts.length > 0) {
        productCatalog.innerHTML = deliveryProducts.map(p => `
            <label class="product-item">
                <input type="checkbox" class="product-check" value="${p.id}" onchange="updateDeliverySummary()">
                <span>
                    <strong>${p.name}</strong>
                    <small>${p.description} &bull; R$ ${Number(p.price).toFixed(2)}</small>
                </span>
            </label>
        `).join('');
    }

    updateDeliverySummary();
}

function getSelectedFreight() {
    const code = document.getElementById('freightCode')?.value;
    return deliveryFreights.find(f => f.code === code) || null;
}

function getSelectedProductIds() {
    return Array.from(document.querySelectorAll('.product-check:checked')).map(cb => cb.value);
}

function updateDeliverySummary() {
    const summary = document.getElementById('freightSummary');
    if (!summary) return;

    const freight = getSelectedFreight();
    const selected = deliveryProducts.filter(p => getSelectedProductIds().includes(p.id));
    const productTotal = selected.reduce((acc, p) => acc + Number(p.price || 0), 0);
    const freightValue = Number(freight?.value || 0);
    const total = freightValue + productTotal;

    summary.innerHTML = freight
        ? `<strong>${freight.description}</strong>
           <p>Frete: R$ ${freightValue.toFixed(2)}</p>
           <p>Capacidade: ${freight.maxProducts} produtos &bull; Selecionados: ${selected.length}</p>
           <p><strong>Total: R$ ${total.toFixed(2)}</strong></p>`
        : '<p>Selecione um frete para ver o resumo.</p>';
}

// ========== EVENT LISTENERS ==========
document.addEventListener('DOMContentLoaded', () => {
    aboutDialog = document.getElementById('aboutDialog');

    aboutDialog?.addEventListener('click', e => {
        if (e.target === aboutDialog) closeAboutDialog();
    });

    document.addEventListener('keydown', e => {
        if (e.key === 'Escape' && aboutDialog?.open) closeAboutDialog();
    });
});