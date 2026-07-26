// ============ CENTRALIZED STATE MANAGEMENT ============

const AppState = {
    user: null,
    token: null,
    currentModule: 'dashboard',
    billetData: {},
    colisData: {},
    personnelData: {},
    paieData: {},
    flotteData: {},
    comptabiliteData: {},
    lignesData: {},
    courriersData: {},
    parametresData: {},
    dashboardData: {},
    
    // Initialize state from localStorage
    init() {
        this.token = localStorage.getItem('sigavt_token');
        this.user = JSON.parse(localStorage.getItem('sigavt_user') || 'null');
    },
    
    // Set user data
    setUser(user, token) {
        this.user = user;
        this.token = token;
        localStorage.setItem('sigavt_token', token);
        localStorage.setItem('sigavt_user', JSON.stringify(user));
    },
    
    // Clear user data (logout)
    clearUser() {
        this.user = null;
        this.token = null;
        localStorage.removeItem('sigavt_token');
        localStorage.removeItem('sigavt_user');
    },
    
    // Update module data
    setModuleData(module, data) {
        this[`${module}Data`] = data;
    },
    
    // Get module data
    getModuleData(module) {
        return this[`${module}Data`] || {};
    },
    
    // Reset module data
    resetModuleData(module) {
        this[`${module}Data`] = {};
    }
};

// Initialize state on load
AppState.init();

// ============ API CLIENT ============

async function api(endpoint, options = {}) {
    const token = AppState.token || localStorage.getItem('sigavt_token');
    const headers = {
        'Content-Type': 'application/json',
        ...(token ? { 'Authorization': 'Bearer ' + token } : {})
    };
    
    const res = await fetch('/api' + endpoint, {
        headers,
        ...options
    });
    
    if (res.status === 401) {
        logout();
        return;
    }
    
    const data = await res.json();
    if (!res.ok) {
        throw new Error(data.error || 'Erreur serveur');
    }
    return data;
}

// ============ AUTH ============

function logout() {
    AppState.clearUser();
    window.location.href = '/login';
}

function getCurrentUser() {
    return AppState.user;
}

// ============ UI HELPERS ============

function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container') || createToastContainer();
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
        <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
            ${type === 'success' 
                ? '<path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>'
                : type === 'error'
                ? '<path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>'
                : '<path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"/>'}
        </svg>
        <span>${message}</span>
    `;
    container.appendChild(toast);
    
    setTimeout(() => toast.remove(), 3000);
}

function createToastContainer() {
    const container = document.createElement('div');
    container.id = 'toast-container';
    container.className = 'toast-container';
    document.body.appendChild(container);
    return container;
}

function showModal(html) {
    const overlay = document.createElement('div');
    overlay.className = 'modal-overlay active';
    overlay.innerHTML = `
        <div class="modal">
            <div class="modal-header">
                <h3 class="modal-title">Modal</h3>
                <button class="modal-close" onclick="closeModal()">×</button>
            </div>
            <div class="modal-body">${html}</div>
        </div>
    `;
    document.body.appendChild(overlay);
    return overlay;
}

function closeModal() {
    const overlay = document.querySelector('.modal-overlay');
    if (overlay) {
        overlay.classList.remove('active');
        setTimeout(() => overlay.remove(), 300);
    }
}

function showSpinner() {
    const overlay = document.createElement('div');
    overlay.className = 'spinner-overlay active';
    overlay.id = 'global-spinner';
    overlay.innerHTML = '<div class="spinner"></div>';
    document.body.appendChild(overlay);
}

function hideSpinner() {
    const spinner = document.getElementById('global-spinner');
    if (spinner) {
        spinner.classList.remove('active');
        setTimeout(() => spinner.remove(), 300);
    }
}

function formatFCFA(amount) {
    return new Intl.NumberFormat('fr-FR').format(amount) + ' FCFA';
}

function formatDate(isoDate) {
    if (!isoDate) return '-';
    const date = new Date(isoDate);
    return date.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' });
}

function formatDateTime(isoDate) {
    if (!isoDate) return '-';
    const date = new Date(isoDate);
    return date.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}

function confirm(message) {
    return window.confirm(message);
}

// ============ SVG CHARTS ============

function barChart(container, data, options = {}) {
    const { width = 400, height = 200, color = '#3b82f6' } = options;
    const max = Math.max(...data.map(d => d.value));
    const barWidth = (width / data.length) - 10;
    
    let svg = `<svg width="${width}" height="${height}" viewBox="0 0 ${width} ${height}">`;
    
    data.forEach((d, i) => {
        const barHeight = (d.value / max) * (height - 40);
        const x = i * (width / data.length) + 5;
        const y = height - barHeight - 30;
        
        svg += `
            <rect x="${x}" y="${y}" width="${barWidth}" height="${barHeight}" fill="${color}" rx="4"/>
            <text x="${x + barWidth/2}" y="${y - 5}" text-anchor="middle" font-size="12" fill="#64748b">${d.label}</text>
            <text x="${x + barWidth/2}" y="${height - 10}" text-anchor="middle" font-size="11" fill="#1e293b">${d.value}</text>
        `;
    });
    
    svg += '</svg>';
    container.innerHTML = svg;
}

function donutChart(container, data, options = {}) {
    const { size = 200, colors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444'] } = options;
    const total = data.reduce((sum, d) => sum + d.value, 0);
    let startAngle = 0;
    
    let svg = `<svg width="${size}" height="${size}" viewBox="0 0 ${size} ${size}">`;
    const center = size / 2;
    const radius = (size / 2) - 20;
    const innerRadius = radius - 30;
    
    data.forEach((d, i) => {
        const angle = (d.value / total) * 360;
        const endAngle = startAngle + angle;
        
        const x1 = center + radius * Math.cos((startAngle - 90) * Math.PI / 180);
        const y1 = center + radius * Math.sin((startAngle - 90) * Math.PI / 180);
        const x2 = center + radius * Math.cos((endAngle - 90) * Math.PI / 180);
        const y2 = center + radius * Math.sin((endAngle - 90) * Math.PI / 180);
        
        const largeArc = angle > 180 ? 1 : 0;
        
        svg += `<path d="M ${center} ${center} L ${x1} ${y1} A ${radius} ${radius} 0 ${largeArc} 1 ${x2} ${y2} Z" fill="${colors[i % colors.length]}" opacity="0.8"/>`;
        
        startAngle = endAngle;
    });
    
    // Inner circle for donut effect
    svg += `<circle cx="${center}" cy="${center}" r="${innerRadius}" fill="white"/>`;
    
    // Legend
    let legendY = 10;
    data.forEach((d, i) => {
        svg += `
            <rect x="10" y="${legendY}" width="12" height="12" fill="${colors[i % colors.length]}" rx="2"/>
            <text x="28" y="${legendY + 10}" font-size="11" fill="#64748b">${d.label}: ${d.value}</text>
        `;
        legendY += 20;
    });
    
    svg += '</svg>';
    container.innerHTML = svg;
}

// ============ SPA ROUTER ============

const pages = {
    dashboard: renderDashboard,
    billets: renderBillets,
    colis: renderColis,
    personnel: renderPersonnel,
    paie: renderPaie,
    flotte: renderFlotte,
    comptabilite: renderComptabilite,
    lignes: renderLignes,
    courriers: renderCourriers,
    parametres: renderParametres
};

let currentPage = 'dashboard';

function navigate(page, params = {}) {
    if (!pages[page]) {
        console.error('Page not found:', page);
        return;
    }
    
    currentPage = page;
    AppState.currentModule = page;
    
    // Update sidebar active state
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.remove('active');
        if (item.dataset.page === page) {
            item.classList.add('active');
        }
    });
    
    // Update page title
    const titles = {
        dashboard: 'Tableau de bord',
        billets: 'Vente de billets',
        colis: 'Gestion des colis',
        personnel: 'Personnel',
        paie: 'Paie',
        flotte: 'Flotte',
        comptabilite: 'Comptabilité',
        lignes: 'Lignes',
        courriers: 'Courriers',
        parametres: 'Paramètres'
    };
    
    const titleElement = document.querySelector('.topbar-title');
    if (titleElement) {
        titleElement.textContent = titles[page] || page;
    }
    
    // Render page content
    const content = document.getElementById('main-content');
    if (content) {
        content.innerHTML = '<div class="spinner-overlay active"><div class="spinner"></div></div>';
        
        try {
            pages[page](content, params);
        } catch (error) {
            console.error('Error rendering page:', error);
            content.innerHTML = `<div class="card"><div class="card-body"><p class="text-center" style="color: var(--red);">Erreur de chargement de la page</p></div></div>`;
        }
    }
}

// ============ PAGE RENDERERS ============

async function renderDashboard(container) {
    try {
        const [stats, departs, recettes, topLignes] = await Promise.all([
            api('/dashboard/stats'),
            api('/dashboard/departs'),
            api('/dashboard/recettes-semaine'),
            api('/dashboard/top-lignes')
        ]);
        
        // Store dashboard data in state
        AppState.setModuleData('dashboard', { stats, departs, recettes, topLignes });
        
        container.innerHTML = `
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-label">Recettes du jour</div>
                    <div class="stat-value">${formatFCFA(stats.recettes_jour)}</div>
                    <div class="stat-meta">
                        <span class="badge-change ${stats.variation_recettes >= 0 ? 'badge-up' : 'badge-down'}">
                            ${stats.variation_recettes >= 0 ? '↑' : '↓'} ${Math.abs(stats.variation_recettes)}% vs hier
                        </span>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Billets vendus</div>
                    <div class="stat-value">${stats.billets_jour}</div>
                    <div class="stat-meta">
                        <span class="badge-change ${stats.variation_billets >= 0 ? 'badge-up' : 'badge-down'}">
                            ${stats.variation_billets >= 0 ? '↑' : '↓'} ${Math.abs(stats.variation_billets)}% vs hier
                        </span>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Colis en transit</div>
                    <div class="stat-value">${stats.colis_transit}</div>
                    <div class="stat-meta">Sur toutes les lignes</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Bus en service</div>
                    <div class="stat-value">${stats.bus_service}</div>
                    <div class="stat-meta">Disponibles</div>
                </div>
            </div>
            
            ${stats.alertes && stats.alertes.length > 0 ? `
                <div class="card" style="margin-bottom: 24px;">
                    <div class="card-header">
                        <h3>⚠️ Alertes actives (${stats.alertes.length})</h3>
                    </div>
                    <div class="card-body">
                        <div style="display: flex; flex-wrap: wrap; gap: 12px;">
                            ${stats.alertes.map(a => `
                                <div class="alert alert-${a.severity || 'orange'}" style="flex: 1; min-width: 250px;">
                                    <div class="alert-title">${a.type || 'Alerte'}</div>
                                    <div class="alert-sub">${a.message}</div>
                                </div>
                            `).join('')}
                        </div>
                    </div>
                </div>
            ` : ''}
            
            <div class="card" style="margin-bottom: 24px;">
                <div class="card-header">
                    <h3>Départs du jour</h3>
                </div>
                <div class="card-body">
                    <div class="table-wrap">
                        <table>
                            <thead>
                                <tr>
                                    <th>Ligne</th>
                                    <th>Code</th>
                                    <th>Heure</th>
                                    <th>Places</th>
                                    <th>Bus</th>
                                    <th>Statut</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${departs.map(d => `
                                    <tr>
                                        <td>${d.ligne}</td>
                                        <td><span class="route-plate">${d.code}</span></td>
                                        <td>${d.heure}</td>
                                        <td>${d.places}</td>
                                        <td>${d.bus}</td>
                                        <td><span class="badge ${getStatutBadgeClass(d.statut)}">${d.statut}</span></td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            
            <div class="grid-2">
                <div class="card">
                    <div class="card-header">
                        <h3>Recettes / semaine</h3>
                    </div>
                    <div class="card-body" id="recettes-chart"></div>
                </div>
                <div class="card">
                    <div class="card-header">
                        <h3>Top lignes</h3>
                    </div>
                    <div class="card-body">
                        ${topLignes.map(l => `
                            <div style="margin-bottom: 16px;">
                                <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
                                    <span style="font-weight: 600;">${l.ligne}</span>
                                    <span style="color: var(--text-muted);">${l.code}</span>
                                </div>
                                <div class="progress">
                                    <div class="progress-bar progress-blue" style="width: ${l.taux}%"></div>
                                </div>
                                <div style="text-align: right; font-size: 12px; color: var(--text-muted); margin-top: 4px;">${l.taux}% remplissage</div>
                            </div>
                        `).join('')}
                    </div>
                </div>
            </div>
        `;
        
        // Render chart
        const chartData = recettes.map(r => ({
            label: new Date(r.date).toLocaleDateString('fr-FR', { weekday: 'short' }),
            value: r.montant
        }));
        barChart(document.getElementById('recettes-chart'), chartData, { width: 350, height: 180 });
        
    } catch (error) {
        console.error('Error loading dashboard:', error);
        container.innerHTML = `<div class="card"><div class="card-body"><p class="text-center" style="color: var(--red);">Erreur de chargement du tableau de bord</p></div></div>`;
    }
}

async function renderBillets(container) {
    container.innerHTML = `
        <div class="dashboard-card">
            <div class="dashboard-card-header">
                <h3 class="dashboard-card-title">Vente de billet</h3>
            </div>
            <div class="dashboard-card-body">
                <div class="wizard-steps">
                    <div class="wizard-step active" data-step="1">
                        <div class="wizard-step-number">1</div>
                        <div class="wizard-step-label">Trajet</div>
                    </div>
                    <div class="wizard-step" data-step="2">
                        <div class="wizard-step-number">2</div>
                        <div class="wizard-step-label">Passager</div>
                    </div>
                    <div class="wizard-step" data-step="3">
                        <div class="wizard-step-number">3</div>
                        <div class="wizard-step-label">Siège</div>
                    </div>
                    <div class="wizard-step" data-step="4">
                        <div class="wizard-step-number">4</div>
                        <div class="wizard-step-label">Paiement</div>
                    </div>
                </div>
                
                <div id="billet-step-content">
                    <!-- Step content will be loaded here -->
                </div>
            </div>
        </div>
        
        <div class="dashboard-card">
            <div class="dashboard-card-header">
                <h3 class="dashboard-card-title">Historique des billets</h3>
            </div>
            <div class="dashboard-card-body">
                <div style="margin-bottom: 16px; display: flex; gap: 12px;">
                    <input type="text" id="billet-search" placeholder="Rechercher..." class="form-input" style="flex: 1;">
                    <select id="billet-statut-filter" class="form-select" style="width: 150px;">
                        <option value="">Tous statuts</option>
                        <option value="actif">Actif</option>
                        <option value="annule">Annulé</option>
                        <option value="utilise">Utilisé</option>
                    </select>
                </div>
                <table class="dashboard-table">
                    <thead>
                        <tr>
                            <th>Numéro</th>
                            <th>Passager</th>
                            <th>Voyage</th>
                            <th>Siège</th>
                            <th>Montant</th>
                            <th>Statut</th>
                            <th>Date</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody id="billets-table-body">
                        <tr><td colspan="8" class="text-center">Chargement...</td></tr>
                    </tbody>
                </table>
            </div>
        </div>
    `;
    
    loadBilletsList();
    loadBilletStep1();
}

async function loadBilletsList() {
    try {
        const search = document.getElementById('billet-search')?.value || '';
        const statut = document.getElementById('billet-statut-filter')?.value || '';
        const data = await api(`/billets?search=${search}&statut=${statut}`);
        
        const tbody = document.getElementById('billets-table-body');
        tbody.innerHTML = data.data.map(b => `
            <tr>
                <td><span class="badge badge-navy">${b.numero}</span></td>
                <td>${b.passager_nom}</td>
                <td>${b.ligne_code} - ${formatDate(b.date_voyage)} ${b.heure_depart}</td>
                <td><span class="badge badge-info">${b.siege}</span></td>
                <td>${formatFCFA(b.montant)}</td>
                <td><span class="badge ${getStatutBadgeClass(b.statut)}">${b.statut}</span></td>
                <td>${formatDate(b.created_at)}</td>
                <td>
                    ${b.statut === 'actif' ? `<button class="btn btn-sm btn-outline" onclick="annulerBillet(${b.id})">Annuler</button>` : '-'}
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error loading billets:', error);
    }
}

async function annulerBillet(id) {
    if (!confirm('Êtes-vous sûr de vouloir annuler ce billet ?')) return;
    
    try {
        await api(`/billets/${id}/annuler`, { method: 'PUT' });
        showToast('Billet annulé avec succès');
        loadBilletsList();
    } catch (error) {
        showToast(error.message, 'error');
    }
}

// Use centralized state for billet data
function getBilletData() {
    return AppState.billetData;
}

function setBilletData(data) {
    AppState.billetData = { ...AppState.billetData, ...data };
}

function resetBilletData() {
    AppState.resetModuleData('billet');
}

async function loadBilletStep1() {
    const today = new Date().toISOString().split('T')[0];
    
    document.getElementById('billet-step-content').innerHTML = `
        <div class="form-row">
            <div class="form-group">
                <label class="form-label">Ville de départ</label>
                <select id="billet-ville-depart" class="form-select" onchange="loadVoyages()">
                    <option value="">Sélectionner...</option>
                    <option value="Yaoundé">Yaoundé</option>
                    <option value="Douala">Douala</option>
                    <option value="Bafoussam">Bafoussam</option>
                    <option value="Bamenda">Bamenda</option>
                    <option value="Ngaoundéré">Ngaoundéré</option>
                    <option value="Ebolowa">Ebolowa</option>
                    <option value="Kribi">Kribi</option>
                    <option value="Garoua">Garoua</option>
                </select>
            </div>
            <div class="form-group">
                <label class="form-label">Ville d'arrivée</label>
                <select id="billet-ville-arrivee" class="form-select" onchange="loadVoyages()">
                    <option value="">Sélectionner...</option>
                    <option value="Yaoundé">Yaoundé</option>
                    <option value="Douala">Douala</option>
                    <option value="Bafoussam">Bafoussam</option>
                    <option value="Bamenda">Bamenda</option>
                    <option value="Ngaoundéré">Ngaoundéré</option>
                    <option value="Ebolowa">Ebolowa</option>
                    <option value="Kribi">Kribi</option>
                    <option value="Garoua">Garoua</option>
                </select>
            </div>
        </div>
        <div class="form-row">
            <div class="form-group">
                <label class="form-label">Date du voyage</label>
                <input type="date" id="billet-date" class="form-input" value="${today}" min="${today}" onchange="loadVoyages()">
            </div>
            <div class="form-group">
                <label class="form-label">Nombre de places</label>
                <div style="display: flex; align-items: center; gap: 8px;">
                    <button class="btn btn-outline btn-sm" onclick="adjustPlaces(-1)">-</button>
                    <span id="billet-places-count" style="font-weight: 700; font-size: 18px;">1</span>
                    <button class="btn btn-outline btn-sm" onclick="adjustPlaces(1)">+</button>
                </div>
            </div>
        </div>
        <div class="form-group">
            <label class="form-label">Horaires disponibles</label>
            <div id="billet-voyages-list" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 12px;">
                <p class="text-center" style="color: var(--text-muted);">Sélectionnez les villes et la date</p>
            </div>
        </div>
        <div id="billet-recap" class="hidden" style="margin-top: 20px; padding: 16px; background: var(--bg); border-radius: 8px;">
            <!-- Recap will be shown here -->
        </div>
    `;
    
    setBilletData({ places: 1 });
}

function adjustPlaces(delta) {
    const count = document.getElementById('billet-places-count');
    const newValue = Math.max(1, Math.min(10, parseInt(count.textContent) + delta));
    count.textContent = newValue;
    setBilletData({ places: newValue });
}

async function loadVoyages() {
    const depart = document.getElementById('billet-ville-depart').value;
    const arrivee = document.getElementById('billet-ville-arrivee').value;
    const date = document.getElementById('billet-date').value;
    
    if (!depart || !arrivee || !date) return;
    
    try {
        const voyages = await api(`/voyages?date=${date}`);
        const filtered = voyages.filter(v => 
            v.ville_depart === depart && v.ville_arrivee === arrivee
        );
        
        const container = document.getElementById('billet-voyages-list');
        
        if (filtered.length === 0) {
            container.innerHTML = '<p class="text-center" style="color: var(--text-muted);">Aucun voyage disponible pour cette destination</p>';
            return;
        }
        
        container.innerHTML = filtered.map(v => `
            <div class="dashboard-stat-card" style="cursor: pointer; padding: 16px;" onclick="selectVoyage(${v.id}, '${v.heure_depart}', ${v.places_total - v.places_vendues}, ${v.tarif_base || 4500}, '${v.immatriculation}', '${v.chauffeur_nom}')">
                <div style="font-weight: 700; color: var(--navy);">${v.heure_depart}</div>
                <div style="font-size: 13px; color: var(--text-muted);">${v.places_total - v.places_vendues} places dispo</div>
                <div style="font-weight: 600; color: var(--gold); margin-top: 4px;">${formatFCFA(v.tarif_base || 4500)}</div>
            </div>
        `).join('');
    } catch (error) {
        console.error('Error loading voyages:', error);
    }
}

function selectVoyage(id, heure, placesDispo, tarif, bus, chauffeur) {
    const billetData = getBilletData();
    if (placesDispo < billetData.places) {
        showToast(`Pas assez de places disponibles (${placesDispo} places)`, 'error');
        return;
    }
    
    setBilletData({
        voyageId: id,
        heure: heure,
        tarif: tarif,
        bus: bus,
        chauffeur: chauffeur,
        total: tarif * billetData.places
    });
    
    document.getElementById('billet-recap').classList.remove('hidden');
    document.getElementById('billet-recap').innerHTML = `
        <div style="font-weight: 700; margin-bottom: 8px;">Récapitulatif</div>
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 8px; font-size: 14px;">
            <div>Heure : <strong>${heure}</strong></div>
            <div>Bus : <strong>${bus}</strong></div>
            <div>Chauffeur : <strong>${chauffeur}</strong></div>
            <div>Total : <strong style="color: var(--gold);">${formatFCFA(tarif * billetData.places)}</strong></div>
        </div>
        <button class="btn btn-primary" style="margin-top: 16px; width: 100%;" onclick="goToBilletStep(2)">Continuer →</button>
    `;
}

function goToBilletStep(step) {
    document.querySelectorAll('.wizard-step').forEach(s => {
        s.classList.remove('active');
        if (parseInt(s.dataset.step) < step) s.classList.add('completed');
        if (parseInt(s.dataset.step) === step) s.classList.add('active');
    });
    
    if (step === 2) loadBilletStep2();
    if (step === 3) loadBilletStep3();
    if (step === 4) loadBilletStep4();
}

function loadBilletStep2() {
    const billetData = getBilletData();
    let passengerFields = '';
    for (let i = 0; i < billetData.places; i++) {
        passengerFields += `
            <div style="background: var(--bg); padding: 16px; border-radius: 8px; margin-bottom: 12px;">
                <h4 style="margin-bottom: 12px; color: var(--navy);">Passager ${i + 1}</h4>
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Nom complet</label>
                        <input type="text" class="form-input" id="passager-nom-${i}" placeholder="Nom Prénom">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Téléphone</label>
                        <input type="tel" class="form-input" id="passager-tel-${i}" placeholder="+237 6XX XXX XXX">
                    </div>
                </div>
                <div class="form-group">
                    <label class="form-label">CNI (optionnel)</label>
                    <input type="text" class="form-input" id="passager-cni-${i}" placeholder="Numéro CNI">
                </div>
            </div>
        `;
    }
    
    document.getElementById('billet-step-content').innerHTML = `
        ${passengerFields}
        <div style="display: flex; gap: 12px; margin-top: 16px;">
            <button class="btn btn-outline" onclick="goToBilletStep(1)">← Retour</button>
            <button class="btn btn-primary" onclick="validatePassagersAndContinue()">Continuer →</button>
        </div>
    `;
}

function validatePassagersAndContinue() {
    const billetData = getBilletData();
    const passagers = [];
    
    for (let i = 0; i < billetData.places; i++) {
        const nom = document.getElementById(`passager-nom-${i}`).value;
        const tel = document.getElementById(`passager-tel-${i}`).value;
        const cni = document.getElementById(`passager-cni-${i}`).value;
        
        if (!nom || !tel) {
            showToast(`Veuillez remplir le nom et téléphone du passager ${i + 1}`, 'error');
            return;
        }
        
        passagers.push({ nom, tel, cni });
    }
    
    setBilletData({ passagers });
    goToBilletStep(3);
}

async function loadBilletStep3() {
    const billetData = getBilletData();
    try {
        const sieges = await api(`/voyages/${billetData.voyageId}/sieges`);
        
        let seatMap = '<div class="seat-map">';
        sieges.forEach(s => {
            const statusClass = s.occupe ? 'occupied' : 'available';
            seatMap += `<div class="seat ${statusClass}" data-siege="${s.siege}" onclick="selectSiege('${s.siege}', this)">${s.siege}</div>`;
        });
        seatMap += '</div>';
        
        document.getElementById('billet-step-content').innerHTML = `
            <div style="text-align: center; margin-bottom: 16px;">
                <h4 style="color: var(--navy);">Sélectionnez vos sièges</h4>
                <p style="color: var(--text-muted); font-size: 14px;">${billetData.places} siège(s) à sélectionner</p>
            </div>
            ${seatMap}
            <div class="seat-legend">
                <div class="seat-legend-item">
                    <div class="seat-legend-color" style="background: #dbeafe;"></div>
                    <span>Libre</span>
                </div>
                <div class="seat-legend-item">
                    <div class="seat-legend-color" style="background: #fee2e2;"></div>
                    <span>Occupé</span>
                </div>
                <div class="seat-legend-item">
                    <div class="seat-legend-color" style="background: var(--green);"></div>
                    <span>Sélectionné</span>
                </div>
            </div>
            <div id="selected-sieges" style="text-align: center; margin-top: 16px; font-weight: 700; color: var(--gold);">
                Sièges sélectionnés : Aucun
            </div>
            <div style="display: flex; gap: 12px; margin-top: 16px;">
                <button class="btn btn-outline" onclick="goToBilletStep(2)">← Retour</button>
                <button class="btn btn-primary" onclick="validateSiegesAndContinue()" id="continue-sieges" disabled>Continuer →</button>
            </div>
        `;
        
        setBilletData({ selectedSieges: [] });
    } catch (error) {
        console.error('Error loading seats:', error);
        showToast('Erreur de chargement du plan de siège', 'error');
    }
}

function selectSiege(siege, element) {
    const billetData = getBilletData();
    if (element.classList.contains('occupied')) return;
    
    let selectedSieges = [...billetData.selectedSieges];
    
    if (selectedSieges.includes(siege)) {
        selectedSieges = selectedSieges.filter(s => s !== siege);
        element.classList.remove('selected');
        element.classList.add('available');
    } else {
        if (selectedSieges.length >= billetData.places) {
            showToast(`Vous avez déjà sélectionné ${billetData.places} siège(s)`, 'error');
            return;
        }
        selectedSieges.push(siege);
        element.classList.remove('available');
        element.classList.add('selected');
    }
    
    setBilletData({ selectedSieges });
    
    document.getElementById('selected-sieges').textContent = 
        `Sièges sélectionnés : ${selectedSieges.length > 0 ? selectedSieges.join(', ') : 'Aucun'}`;
    
    document.getElementById('continue-sieges').disabled = selectedSieges.length !== billetData.places;
}

function validateSiegesAndContinue() {
    goToBilletStep(4);
}

function loadBilletStep4() {
    const billetData = getBilletData();
    document.getElementById('billet-step-content').innerHTML = `
        <div style="background: var(--bg); padding: 24px; border-radius: 8px; margin-bottom: 20px;">
            <h4 style="color: var(--navy); margin-bottom: 16px;">Récapitulatif final</h4>
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
                <div>
                    <div style="font-size: 12px; color: var(--text-muted); text-transform: uppercase;">Trajet</div>
                    <div style="font-weight: 600;">${document.getElementById('billet-ville-depart').value} → ${document.getElementById('billet-ville-arrivee').value}</div>
                </div>
                <div>
                    <div style="font-size: 12px; color: var(--text-muted); text-transform: uppercase;">Date et heure</div>
                    <div style="font-weight: 600;">${formatDate(document.getElementById('billet-date').value)} à ${billetData.heure}</div>
                </div>
                <div>
                    <div style="font-size: 12px; color: var(--text-muted); text-transform: uppercase;">Passagers</div>
                    <div style="font-weight: 600;">${billetData.passagers.map(p => p.nom).join(', ')}</div>
                </div>
                <div>
                    <div style="font-size: 12px; color: var(--text-muted); text-transform: uppercase;">Sièges</div>
                    <div style="font-weight: 600;">${billetData.selectedSieges.join(', ')}</div>
                </div>
                <div>
                    <div style="font-size: 12px; color: var(--text-muted); text-transform: uppercase;">Bus</div>
                    <div style="font-weight: 600;">${billetData.bus}</div>
                </div>
                <div>
                    <div style="font-size: 12px; color: var(--text-muted); text-transform: uppercase;">Total à payer</div>
                    <div style="font-weight: 800; font-size: 20px; color: var(--gold);">${formatFCFA(billetData.total)}</div>
                </div>
            </div>
        </div>
        
        <div class="form-group">
            <label class="form-label">Mode de paiement</label>
            <div style="display: flex; gap: 12px;">
                <button class="btn btn-outline" onclick="selectPaiement('especes', this)" id="paiement-especes">
                    💵 Espèces
                </button>
                <button class="btn btn-outline" onclick="selectPaiement('orange_money', this)" id="paiement-orange_money">
                    🟠 Orange Money
                </button>
                <button class="btn btn-outline" onclick="selectPaiement('mtn_momo', this)" id="paiement-mtn_momo">
                    🟡 MTN MoMo
                </button>
            </div>
        </div>
        
        <div style="display: flex; gap: 12px; margin-top: 20px;">
            <button class="btn btn-outline" onclick="goToBilletStep(3)">← Retour</button>
            <button class="btn btn-primary" onclick="confirmerBillet()" id="confirmer-billet" disabled>
                Confirmer et émettre le billet
            </button>
        </div>
    `;
    
    setBilletData({ modePaiement: null });
}

function selectPaiement(mode, button) {
    document.querySelectorAll('[id^="paiement-"]').forEach(b => {
        b.classList.remove('btn-primary');
        b.classList.add('btn-outline');
    });
    button.classList.remove('btn-outline');
    button.classList.add('btn-primary');
    setBilletData({ modePaiement: mode });
    document.getElementById('confirmer-billet').disabled = false;
}

async function confirmerBillet() {
    const billetData = getBilletData();
    showSpinner();
    
    try {
        for (let i = 0; i < billetData.places; i++) {
            await api('/billets', {
                method: 'POST',
                body: JSON.stringify({
                    voyage_id: billetData.voyageId,
                    passager_nom: billetData.passagers[i].nom,
                    passager_tel: billetData.passagers[i].tel,
                    passager_cni: billetData.passagers[i].cni,
                    siege: billetData.selectedSieges[i],
                    montant: billetData.tarif,
                    mode_paiement: billetData.modePaiement
                })
            });
        }
        
        hideSpinner();
        showToast('Billet(s) émis avec succès !');
        loadBilletsList();
        resetBilletData();
        loadBilletStep1();
    } catch (error) {
        hideSpinner();
        showToast(error.message, 'error');
    }
}

async function renderColis(container) {
    // Initialize colis data in state
    AppState.setModuleData('colis', { currentTab: 'enregistrer' });
    
    container.innerHTML = `
        <div class="card">
            <div class="card-header">
                <h3>Gestion des colis</h3>
            </div>
            <div class="card-body">
                <div style="display: flex; gap: 12px; margin-bottom: 20px;">
                    <button class="btn btn-primary" onclick="showColisTab('enregistrer')" id="tab-enregistrer">Enregistrer</button>
                    <button class="btn btn-outline" onclick="showColisTab('suivi')" id="tab-suivi">Suivi</button>
                    <button class="btn btn-outline" onclick="showColisTab('liste')" id="tab-liste">Liste</button>
                </div>
                
                <div id="colis-tab-content">
                    <!-- Tab content will be loaded here -->
                </div>
            </div>
        </div>
    `;
    
    showColisTab('enregistrer');
}

function showColisTab(tab) {
    // Update tab buttons
    document.querySelectorAll('[id^="tab-"]').forEach(btn => {
        btn.classList.remove('btn-primary');
        btn.classList.add('btn-outline');
    });
    const activeBtn = document.getElementById(`tab-${tab}`);
    if (activeBtn) {
        activeBtn.classList.remove('btn-outline');
        activeBtn.classList.add('btn-primary');
    }
    
    // Update state
    AppState.setModuleData('colis', { currentTab: tab });
    
    if (tab === 'enregistrer') loadColisEnregistrer();
    if (tab === 'suivi') loadColisSuivi();
    if (tab === 'liste') loadColisListe();
}

function loadColisEnregistrer() {
    document.getElementById('colis-tab-content').innerHTML = `
        <form id="colis-form" onsubmit="enregistrerColis(event)">
            <div class="form-grid">
                <div class="form-group">
                    <label>Expéditeur - Nom <span class="req">*</span></label>
                    <input type="text" id="colis-expediteur-nom" required>
                </div>
                <div class="form-group">
                    <label>Expéditeur - Téléphone <span class="req">*</span></label>
                    <input type="tel" id="colis-expediteur-tel" required>
                </div>
                <div class="form-group">
                    <label>Destinataire - Nom <span class="req">*</span></label>
                    <input type="text" id="colis-destinataire-nom" required>
                </div>
                <div class="form-group">
                    <label>Destinataire - Téléphone <span class="req">*</span></label>
                    <input type="tel" id="colis-destinataire-tel" required>
                </div>
                <div class="form-group">
                    <label>Poids (kg) <span class="req">*</span></label>
                    <input type="number" id="colis-poids" step="0.1" required onchange="calculerTarifColis()">
                </div>
                <div class="form-group">
                    <label>Description</label>
                    <input type="text" id="colis-description">
                </div>
            </div>
            <div class="form-group">
                <label>Options</label>
                <div style="display: flex; gap: 16px; flex-wrap: wrap;">
                    <label style="display: flex; align-items: center; gap: 8px;">
                        <input type="checkbox" id="colis-fragile" onchange="calculerTarifColis()">
                        Fragile (+300F)
                    </label>
                    <label style="display: flex; align-items: center; gap: 8px;">
                        <input type="checkbox" id="colis-urgent" onchange="calculerTarifColis()">
                        Urgent (+800F)
                    </label>
                    <label style="display: flex; align-items: center; gap: 8px;">
                        <input type="checkbox" id="colis-assure" onchange="calculerTarifColis()">
                        Assuré (+500F)
                    </label>
                </div>
            </div>
            <div class="form-group">
                <label>Tarif calculé</label>
                <div id="colis-tarif" style="font-size: 24px; font-weight: 800; color: var(--gold);">0 FCFA</div>
            </div>
            <div class="form-group">
                <label>Mode de paiement</label>
                <select id="colis-paiement">
                    <option value="especes">Espèces</option>
                    <option value="orange_money">Orange Money</option>
                    <option value="mtn_momo">MTN MoMo</option>
                </select>
            </div>
            <button type="submit" class="btn btn-primary" style="width: 100%;">Enregistrer le colis</button>
        </form>
    `;
}

function calculerTarifColis() {
    const poids = parseFloat(document.getElementById('colis-poids').value) || 0;
    const fragile = document.getElementById('colis-fragile').checked;
    const urgent = document.getElementById('colis-urgent').checked;
    const assure = document.getElementById('colis-assure').checked;
    
    let tarif = 0;
    if (poids <= 1) tarif = 500;
    else if (poids <= 5) tarif = 1200;
    else if (poids <= 15) tarif = 2500;
    else tarif = 4000;
    
    if (fragile) tarif += 300;
    if (urgent) tarif += 800;
    if (assure) tarif += 500;
    
    document.getElementById('colis-tarif').textContent = formatFCFA(tarif);
    return tarif;
}

async function enregistrerColis(event) {
    event.preventDefault();
    
    const tarif = calculerTarifColis();
    
    try {
        const result = await api('/colis', {
            method: 'POST',
            body: JSON.stringify({
                expediteur_nom: document.getElementById('colis-expediteur-nom').value,
                expediteur_tel: document.getElementById('colis-expediteur-tel').value,
                destinataire_nom: document.getElementById('colis-destinataire-nom').value,
                destinataire_tel: document.getElementById('colis-destinataire-tel').value,
                poids_kg: parseFloat(document.getElementById('colis-poids').value),
                description: document.getElementById('colis-description').value,
                fragile: document.getElementById('colis-fragile').checked ? 1 : 0,
                urgent: document.getElementById('colis-urgent').checked ? 1 : 0,
                assure: document.getElementById('colis-assure').checked ? 1 : 0,
                montant: tarif,
                mode_paiement: document.getElementById('colis-paiement').value,
                lieu: 'Agence'
            })
        });
        
        showModal(`
            <div style="text-align: center;">
                <h3 style="color: var(--green); margin-bottom: 16px;">✓ Colis enregistré !</h3>
                <div style="background: var(--bg); padding: 24px; border-radius: 8px; margin-bottom: 16px;">
                    <div style="font-size: 12px; color: var(--text-muted); text-transform: uppercase;">Numéro de tracking</div>
                    <div style="font-size: 28px; font-weight: 800; color: var(--navy);">${result.numero_tracking}</div>
                </div>
                <button class="btn btn-primary" onclick="closeModal(); showColisTab('enregistrer');">Nouveau colis</button>
            </div>
        `);
        
        showToast('Colis enregistré avec succès');
    } catch (error) {
        showToast(error.message, 'error');
    }
}

function loadColisSuivi() {
    document.getElementById('colis-tab-content').innerHTML = `
        <div class="form-group">
            <label class="form-label">Numéro de tracking</label>
            <div style="display: flex; gap: 12px;">
                <input type="text" class="form-input" id="colis-tracking-search" placeholder="COL-2025-XXXXX" style="flex: 1;">
                <button class="btn btn-primary" onclick="rechercherColis()">Rechercher</button>
            </div>
        </div>
        <div id="colis-tracking-result"></div>
    `;
}

async function rechercherColis() {
    const numero = document.getElementById('colis-tracking-search').value;
    if (!numero) {
        showToast('Veuillez entrer un numéro de tracking', 'error');
        return;
    }
    
    try {
        const result = await api(`/colis/tracking/${numero}`);
        
        const statutColors = {
            'enregistre': '#3b82f6',
            'pris_en_charge': '#f59e0b',
            'en_transit': '#8b5cf6',
            'arrive': '#10b981',
            'en_livraison': '#f97316',
            'livre': '#10b981',
            'non_reclame': '#ef4444'
        };
        
        document.getElementById('colis-tracking-result').innerHTML = `
            <div style="background: var(--bg); padding: 24px; border-radius: 8px; margin-top: 20px;">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                    <div>
                        <div style="font-size: 12px; color: var(--text-muted); text-transform: uppercase;">Statut actuel</div>
                        <div style="font-size: 20px; font-weight: 700; color: ${statutColors[result.colis.statut] || '#64748b'};">
                            ${result.colis.statut.replace(/_/g, ' ').toUpperCase()}
                        </div>
                    </div>
                    <div style="text-align: right;">
                        <div style="font-size: 12px; color: var(--text-muted); text-transform: uppercase;">Tracking</div>
                        <div style="font-size: 18px; font-weight: 700; color: var(--navy);">${result.colis.numero_tracking}</div>
                    </div>
                </div>
                
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 20px;">
                    <div>
                        <div style="font-size: 12px; color: var(--text-muted);">Expéditeur</div>
                        <div style="font-weight: 600;">${result.colis.expediteur_nom}</div>
                        <div style="font-size: 13px; color: var(--text-muted);">${result.colis.expediteur_tel}</div>
                    </div>
                    <div>
                        <div style="font-size: 12px; color: var(--text-muted);">Destinataire</div>
                        <div style="font-weight: 600;">${result.colis.destinataire_nom}</div>
                        <div style="font-size: 13px; color: var(--text-muted);">${result.colis.destinataire_tel}</div>
                    </div>
                </div>
                
                <h4 style="color: var(--navy); margin-bottom: 12px;">Historique des événements</h4>
                <div style="border-left: 2px solid var(--border); padding-left: 20px;">
                    ${result.events.map((e, i) => `
                        <div style="margin-bottom: 16px; position: relative;">
                            <div style="position: absolute; left: -26px; top: 0; width: 10px; height: 10px; border-radius: 50%; background: ${i === result.events.length - 1 ? 'var(--gold)' : 'var(--border)'};"></div>
                            <div style="font-weight: 600; color: var(--navy);">${e.statut.replace(/_/g, ' ').toUpperCase()}</div>
                            <div style="font-size: 14px; color: var(--text-muted);">${e.description}</div>
                            <div style="font-size: 12px; color: var(--text-muted);">${e.lieu} • ${formatDateTime(e.created_at)}</div>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;
    } catch (error) {
        document.getElementById('colis-tracking-result').innerHTML = `
            <div style="text-align: center; padding: 40px; color: var(--red);">
                Colis non trouvé
            </div>
        `;
    }
}

async function loadColisListe() {
    try {
        const data = await api('/colis');
        
        document.getElementById('colis-tab-content').innerHTML = `
            <div style="margin-bottom: 16px;">
                <select class="form-select" id="colis-statut-filter" style="width: 200px;" onchange="filterColis()">
                    <option value="">Tous statuts</option>
                    <option value="enregistre">Enregistré</option>
                    <option value="en_transit">En transit</option>
                    <option value="arrive">Arrivé</option>
                    <option value="livre">Livré</option>
                </select>
            </div>
            <table class="dashboard-table">
                <thead>
                    <tr>
                        <th>Tracking</th>
                        <th>Expéditeur</th>
                        <th>Destinataire</th>
                        <th>Poids</th>
                        <th>Statut</th>
                        <th>Date</th>
                    </tr>
                </thead>
                <tbody id="colis-table-body">
                    ${data.data.map(c => `
                        <tr>
                            <td><span class="badge badge-navy">${c.numero_tracking}</span></td>
                            <td>${c.expediteur_nom}</td>
                            <td>${c.destinataire_nom}</td>
                            <td>${c.poids_kg} kg</td>
                            <td><span class="badge ${getStatutBadgeClass(c.statut)}">${c.statut}</span></td>
                            <td>${formatDate(c.created_at)}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    } catch (error) {
        console.error('Error loading colis:', error);
    }
}

async function filterColis() {
    const statut = document.getElementById('colis-statut-filter').value;
    const data = await api(`/colis?statut=${statut}`);
    
    document.getElementById('colis-table-body').innerHTML = data.data.map(c => `
        <tr>
            <td><span class="badge badge-navy">${c.numero_tracking}</span></td>
            <td>${c.expediteur_nom}</td>
            <td>${c.destinataire_nom}</td>
            <td>${c.poids_kg} kg</td>
            <td><span class="badge ${getStatutBadgeClass(c.statut)}">${c.statut}</span></td>
            <td>${formatDate(c.created_at)}</td>
        </tr>
    `).join('');
}

async function renderPersonnel(container) {
    try {
        const employes = await api('/employes');
        
        // Store personnel data in state
        AppState.setModuleData('personnel', { employes });
        
        container.innerHTML = `
            <div class="card">
                <div class="card-header">
                    <h3>Personnel</h3>
                    <button class="btn btn-primary btn-sm" onclick="showAddEmployeModal()">+ Employé</button>
                </div>
                <div class="card-body">
                    <div style="margin-bottom: 16px; display: flex; gap: 12px;">
                        <input type="text" placeholder="Rechercher..." class="form-input" style="flex: 1;" id="personnel-search" onkeyup="filterPersonnel()">
                        <select class="form-select" style="width: 150px;" id="personnel-poste-filter" onchange="filterPersonnel()">
                            <option value="">Tous postes</option>
                            <option value="chauffeur">Chauffeur</option>
                            <option value="billetterie">Billetterie</option>
                            <option value="convoyeur">Convoyeur</option>
                            <option value="comptable">Comptable</option>
                            <option value="responsable_flotte">Resp. Flotte</option>
                            <option value="directeur">Directeur</option>
                        </select>
                        <select class="form-select" style="width: 150px;" id="personnel-statut-filter" onchange="filterPersonnel()">
                            <option value="">Tous statuts</option>
                            <option value="actif">Actif</option>
                            <option value="inactif">Inactif</option>
                        </select>
                    </div>
                    <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 16px;" id="personnel-grid">
                        ${employes.map(e => `
                            <div class="card" style="padding: 16px; display: flex; align-items: center; gap: 12px;">
                                <div class="avatar avatar-${getAvatarColor(e.poste)}" style="width: 48px; height: 48px; font-size: 18px;">
                                    ${e.prenom[0]}${e.nom[0]}
                                </div>
                                <div style="flex: 1;">
                                    <div style="font-weight: 700; color: var(--navy);">${e.nom} ${e.prenom}</div>
                                    <div style="font-size: 13px; color: var(--text-muted);">${e.poste}</div>
                                    <div style="margin-top: 4px;">
                                        <span class="badge ${e.statut === 'actif' ? 'badge-green' : 'badge-red'}">${e.statut}</span>
                                        <span class="badge badge-blue">${e.type_contrat}</span>
                                    </div>
                                </div>
                                <button class="btn btn-sm btn-outline" onclick="showEditEmployeModal(${e.id})">Modifier</button>
                            </div>
                        `).join('')}
                    </div>
                </div>
            </div>
        `;
    } catch (error) {
        console.error('Error loading personnel:', error);
        container.innerHTML = `<div class="card"><div class="card-body"><p class="text-center" style="color: var(--red);">Erreur de chargement du personnel</p></div></div>`;
    }
}

function getAvatarColor(poste) {
    const colors = {
        'chauffeur': 'orange',
        'billetterie': 'blue',
        'convoyeur': 'green',
        'comptable': 'purple',
        'responsable_flotte': 'teal',
        'directeur': 'red'
    };
    return colors[poste] || 'blue';
}

function filterPersonnel() {
    const search = document.getElementById('personnel-search').value.toLowerCase();
    const poste = document.getElementById('personnel-poste-filter').value;
    const statut = document.getElementById('personnel-statut-filter').value;
    
    const personnelData = AppState.getModuleData('personnel');
    const allEmployes = personnelData.employes || [];
    
    const filtered = allEmployes.filter(e => {
        const matchSearch = !search || 
            e.nom.toLowerCase().includes(search) || 
            e.prenom.toLowerCase().includes(search) ||
            e.poste.toLowerCase().includes(search);
        const matchPoste = !poste || e.poste === poste;
        const matchStatut = !statut || e.statut === statut;
        return matchSearch && matchPoste && matchStatut;
    });
    
    const grid = document.getElementById('personnel-grid');
    grid.innerHTML = filtered.map(e => `
        <div class="card" style="padding: 16px; display: flex; align-items: center; gap: 12px;">
            <div class="avatar avatar-${getAvatarColor(e.poste)}" style="width: 48px; height: 48px; font-size: 18px;">
                ${e.prenom[0]}${e.nom[0]}
            </div>
            <div style="flex: 1;">
                <div style="font-weight: 700; color: var(--navy);">${e.nom} ${e.prenom}</div>
                <div style="font-size: 13px; color: var(--text-muted);">${e.poste}</div>
                <div style="margin-top: 4px;">
                    <span class="badge ${e.statut === 'actif' ? 'badge-green' : 'badge-red'}">${e.statut}</span>
                    <span class="badge badge-blue">${e.type_contrat}</span>
                </div>
            </div>
            <button class="btn btn-sm btn-outline" onclick="showEditEmployeModal(${e.id})">Modifier</button>
        </div>
    `).join('');
}

function showAddEmployeModal() {
    showModal(`
        <form onsubmit="saveEmploye(event)">
            <div class="form-grid">
                <div class="form-group">
                    <label>Nom <span class="req">*</span></label>
                    <input type="text" name="nom" required>
                </div>
                <div class="form-group">
                    <label>Prénom <span class="req">*</span></label>
                    <input type="text" name="prenom" required>
                </div>
                <div class="form-group">
                    <label>Poste <span class="req">*</span></label>
                    <select name="poste" required>
                        <option value="chauffeur">Chauffeur</option>
                        <option value="billetterie">Billetterie</option>
                        <option value="convoyeur">Convoyeur</option>
                        <option value="comptable">Comptable</option>
                        <option value="responsable_flotte">Responsable flotte</option>
                        <option value="directeur">Directeur</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Type contrat <span class="req">*</span></label>
                    <select name="type_contrat" required>
                        <option value="CDI">CDI</option>
                        <option value="CDD">CDD</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Date embauche <span class="req">*</span></label>
                    <input type="date" name="date_embauche" required>
                </div>
                <div class="form-group">
                    <label>Salaire base (FCFA) <span class="req">*</span></label>
                    <input type="number" name="salaire_base" required>
                </div>
                <div class="form-group">
                    <label>Téléphone</label>
                    <input type="tel" name="telephone">
                </div>
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="email">
                </div>
                <div class="form-group">
                    <label>CNI</label>
                    <input type="text" name="cni">
                </div>
                <div class="form-group">
                    <label>CNPS</label>
                    <input type="text" name="cnps">
                </div>
            </div>
            <button type="submit" class="btn btn-primary" style="width: 100%;">Enregistrer</button>
        </form>
    `);
}

async function saveEmploye(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());
    
    try {
        await api('/employes', {
            method: 'POST',
            body: JSON.stringify(data)
        });
        closeModal();
        showToast('Employé créé avec succès');
        navigate('personnel');
    } catch (error) {
        showToast(error.message, 'error');
    }
}

async function renderPaie(container) {
    const today = new Date();
    const mois = today.getMonth() + 1;
    const annee = today.getFullYear();
    
    try {
        const [fiches, stats] = await Promise.all([
            api(`/paie?mois=${mois}&annee=${annee}`),
            api(`/paie/stats/${mois}/${annee}`)
        ]);
        
        // Store paie data in state
        AppState.setModuleData('paie', { fiches, stats, mois, annee });
        
        container.innerHTML = `
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-label">Masse salariale brute</div>
                    <div class="stat-value">${formatFCFA(stats.masse_salariale)}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Net à payer total</div>
                    <div class="stat-value" style="color: var(--gold);">${formatFCFA(stats.net_total)}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Cotisations CNPS (employeur)</div>
                    <div class="stat-value">${formatFCFA(stats.cnps_total)}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Bulletins générés</div>
                    <div class="stat-value">${stats.bulletins}</div>
                </div>
            </div>
            
            <div class="card" style="margin-bottom: 24px;">
                <div class="card-header">
                    <h3>Fiches de paie - ${mois}/${annee}</h3>
                    <div style="display: flex; gap: 12px;">
                        <select class="form-select" style="width: 120px;" id="paie-mois-filter" onchange="filterPaie()">
                            ${[1,2,3,4,5,6,7,8,9,10,11,12].map(m => 
                                `<option value="${m}" ${m === mois ? 'selected' : ''}>${m}</option>`
                            ).join('')}
                        </select>
                        <select class="form-select" style="width: 100px;" id="paie-annee-filter" onchange="filterPaie()">
                            <option value="${annee}" selected>${annee}</option>
                            <option value="${annee-1}">${annee-1}</option>
                        </select>
                        <button class="btn btn-primary btn-sm" onclick="genererFichesPaie()">Générer fiches</button>
                    </div>
                </div>
                <div class="card-body">
                    <div class="table-wrap">
                        <table>
                            <thead>
                                <tr>
                                    <th>Employé</th>
                                    <th>Poste</th>
                                    <th>Salaire brut</th>
                                    <th>Net à payer</th>
                                    <th>Statut</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="paie-table-body">
                                ${fiches.map(f => `
                                    <tr>
                                        <td>
                                            <div style="display: flex; align-items: center; gap: 8px;">
                                                <div class="avatar avatar-blue" style="width: 32px; height: 32px; font-size: 12px;">
                                                    ${f.prenom[0]}${f.nom[0]}
                                                </div>
                                                <span>${f.nom} ${f.prenom}</span>
                                            </div>
                                        </td>
                                        <td>${f.poste}</td>
                                        <td>${formatFCFA(f.salaire_brut)}</td>
                                        <td style="font-weight: 700; color: var(--gold);">${formatFCFA(f.net_a_payer)}</td>
                                        <td><span class="badge ${f.statut === 'paye' ? 'badge-green' : 'badge-orange'}">${f.statut}</span></td>
                                        <td>
                                            <button class="btn btn-sm btn-outline" onclick="showFichePaieDetail(${f.id})">Voir</button>
                                            ${f.statut === 'en_attente' ? `<button class="btn btn-sm btn-green" onclick="marquerPaye(${f.id})">Payer</button>` : ''}
                                            <button class="btn btn-sm btn-outline" onclick="imprimerFiche(${f.id})">Imprimer</button>
                                        </td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            
            <div id="fiche-detail" class="card" style="display: none;">
                <!-- Fiche detail will be shown here -->
            </div>
        `;
    } catch (error) {
        console.error('Error loading paie:', error);
        container.innerHTML = `<div class="card"><div class="card-body"><p class="text-center" style="color: var(--red);">Erreur de chargement de la paie</p></div></div>`;
    }
}

async function filterPaie() {
    const mois = document.getElementById('paie-mois-filter').value;
    const annee = document.getElementById('paie-annee-filter').value;
    
    try {
        const [fiches, stats] = await Promise.all([
            api(`/paie?mois=${mois}&annee=${annee}`),
            api(`/paie/stats/${mois}/${annee}`)
        ]);
        
        AppState.setModuleData('paie', { fiches, stats, mois, annee });
        
        const tbody = document.getElementById('paie-table-body');
        tbody.innerHTML = fiches.map(f => `
            <tr>
                <td>
                    <div style="display: flex; align-items: center; gap: 8px;">
                        <div class="avatar avatar-blue" style="width: 32px; height: 32px; font-size: 12px;">
                            ${f.prenom[0]}${f.nom[0]}
                        </div>
                        <span>${f.nom} ${f.prenom}</span>
                    </div>
                </td>
                <td>${f.poste}</td>
                <td>${formatFCFA(f.salaire_brut)}</td>
                <td style="font-weight: 700; color: var(--gold);">${formatFCFA(f.net_a_payer)}</td>
                <td><span class="badge ${f.statut === 'paye' ? 'badge-green' : 'badge-orange'}">${f.statut}</span></td>
                <td>
                    <button class="btn btn-sm btn-outline" onclick="showFichePaieDetail(${f.id})">Voir</button>
                    ${f.statut === 'en_attente' ? `<button class="btn btn-sm btn-green" onclick="marquerPaye(${f.id})">Payer</button>` : ''}
                    <button class="btn btn-sm btn-outline" onclick="imprimerFiche(${f.id})">Imprimer</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error filtering paie:', error);
    }
}

function imprimerFiche(id) {
    showToast('Fonction d\'impression en cours de développement', 'info');
}

async function genererFichesPaie() {
    if (!confirm('Générer les fiches de paie pour ce mois ?')) return;
    
    const today = new Date();
    try {
        await api('/paie/generer', {
            method: 'POST',
            body: JSON.stringify({
                mois: today.getMonth() + 1,
                annee: today.getFullYear()
            })
        });
        showToast('Fiches de paie générées avec succès');
        navigate('paie');
    } catch (error) {
        showToast(error.message, 'error');
    }
}

async function showFichePaieDetail(id) {
    try {
        const fiche = await api(`/paie/${id}`);
        
        document.getElementById('fiche-detail').classList.remove('hidden');
        document.getElementById('fiche-detail').innerHTML = `
            <div class="dashboard-card-header">
                <h3 class="dashboard-card-title">Bulletin de paie - ${fiche.nom} ${fiche.prenom}</h3>
                <button class="btn btn-sm btn-outline" onclick="document.getElementById('fiche-detail').classList.add('hidden');">Fermer</button>
            </div>
            <div class="dashboard-card-body">
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 24px;">
                    <div>
                        <div style="font-size: 12px; color: var(--text-muted);">Poste</div>
                        <div style="font-weight: 600;">${fiche.poste}</div>
                    </div>
                    <div>
                        <div style="font-size: 12px; color: var(--text-muted);">CNPS</div>
                        <div style="font-weight: 600;">${fiche.cnps}</div>
                    </div>
                    <div>
                        <div style="font-size: 12px; color: var(--text-muted);">Agence</div>
                        <div style="font-weight: 600;">${fiche.agence_nom || 'Non assigné'}</div>
                    </div>
                    <div>
                        <div style="font-size: 12px; color: var(--text-muted);">Période</div>
                        <div style="font-weight: 600;">${fiche.periode_mois}/${fiche.periode_annee}</div>
                    </div>
                </div>
                
                <div style="background: #f0fdf4; padding: 16px; border-radius: 8px; margin-bottom: 16px;">
                    <h4 style="color: var(--green); margin-bottom: 12px;">GAINS</h4>
                    <table style="width: 100%;">
                        <tr>
                            <td>Salaire base</td>
                            <td class="text-right">${formatFCFA(fiche.salaire_base)}</td>
                        </tr>
                        <tr>
                            <td>Prime rendement</td>
                            <td class="text-right">${formatFCFA(fiche.prime_rendement)}</td>
                        </tr>
                        <tr>
                            <td>Indemnité transport</td>
                            <td class="text-right">${formatFCFA(fiche.prime_transport)}</td>
                        </tr>
                        <tr>
                            <td>Heures supplémentaires (${fiche.heures_sup_nb}h)</td>
                            <td class="text-right">${formatFCFA(fiche.heures_sup_montant)}</td>
                        </tr>
                        <tr style="font-weight: 700; border-top: 1px solid var(--border);">
                            <td>SALAIRE BRUT</td>
                            <td class="text-right">${formatFCFA(fiche.salaire_brut)}</td>
                        </tr>
                    </table>
                </div>
                
                <div style="background: #fef2f2; padding: 16px; border-radius: 8px; margin-bottom: 16px;">
                    <h4 style="color: var(--red); margin-bottom: 12px;">RETENUES</h4>
                    <table style="width: 100%;">
                        <tr>
                            <td>CNPS salarié (4,2%)</td>
                            <td class="text-right">-${formatFCFA(fiche.cnps_salarie)}</td>
                        </tr>
                        <tr>
                            <td>IRPP</td>
                            <td class="text-right">-${formatFCFA(fiche.irpp)}</td>
                        </tr>
                        <tr>
                            <td>Avance sur salaire</td>
                            <td class="text-right">-${formatFCFA(fiche.avance)}</td>
                        </tr>
                    </table>
                </div>
                
                <div style="background: var(--navy); color: white; padding: 20px; border-radius: 8px; text-align: center; margin-bottom: 16px;">
                    <div style="font-size: 12px; opacity: 0.8; text-transform: uppercase;">NET À PAYER</div>
                    <div style="font-size: 32px; font-weight: 800; color: var(--gold);">${formatFCFA(fiche.net_a_payer)}</div>
                </div>
                
                <div style="background: var(--bg); padding: 16px; border-radius: 8px; margin-bottom: 16px;">
                    <h4 style="color: var(--navy); margin-bottom: 12px;">CHARGES PATRONALES</h4>
                    <div style="display: flex; justify-content: space-between;">
                        <span>CNPS patronal (11,2%)</span>
                        <span style="font-weight: 600;">${formatFCFA(fiche.cnps_patronal)}</span>
                    </div>
                    <div style="display: flex; justify-content: space-between; margin-top: 8px; font-weight: 700;">
                        <span>Coût employeur total</span>
                        <span>${formatFCFA(fiche.cout_employeur)}</span>
                    </div>
                </div>
                
                ${fiche.statut === 'en_attente' ? `
                    <div style="display: flex; gap: 12px; align-items: center;">
                        <select class="form-select" style="width: 200px;" id="paiement-mode">
                            <option value="especes">Espèces</option>
                            <option value="orange_money">Orange Money</option>
                            <option value="mtn_momo">MTN MoMo</option>
                        </select>
                        <button class="btn btn-primary" onclick="marquerPaye(${fiche.id})">Marquer comme payé</button>
                    </div>
                ` : `
                    <div class="badge badge-success">Payé le ${formatDate(fiche.date_paiement)}</div>
                `}
            </div>
        `;
    } catch (error) {
        console.error('Error loading fiche detail:', error);
    }
}

async function marquerPaye(id) {
    const mode = document.getElementById('paiement-mode').value;
    
    try {
        await api(`/paie/${id}/payer`, {
            method: 'PUT',
            body: JSON.stringify({ mode_paiement: mode })
        });
        showToast('Fiche marquée comme payée');
        navigate('paie');
    } catch (error) {
        showToast(error.message, 'error');
    }
}

async function renderFlotte(container) {
    try {
        const bus = await api('/bus');
        
        // Store flotte data in state
        AppState.setModuleData('flotte', { bus });
        
        const operationnel = bus.filter(b => b.statut === 'operationnel').length;
        const maintenance = bus.filter(b => b.statut === 'maintenance').length;
        const horsService = bus.filter(b => b.statut === 'hors_service').length;
        
        container.innerHTML = `
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-label">Total bus</div>
                    <div class="stat-value">${bus.length}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">En service</div>
                    <div class="stat-value" style="color: var(--green);">${operationnel}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">En maintenance</div>
                    <div class="stat-value" style="color: var(--yellow);">${maintenance}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Hors service</div>
                    <div class="stat-value" style="color: var(--red);">${horsService}</div>
                </div>
            </div>
            
            <div class="card">
                <div class="card-header">
                    <h3>Flotte de bus</h3>
                    <button class="btn btn-primary btn-sm" onclick="showAddBusModal()">+ Bus</button>
                </div>
                <div class="card-body">
                    <div class="table-wrap">
                        <table>
                            <thead>
                                <tr>
                                    <th>Immatriculation</th>
                                    <th>Modèle</th>
                                    <th>Places</th>
                                    <th>Agence</th>
                                    <th>Km total</th>
                                    <th>Visite technique</th>
                                    <th>Assurance</th>
                                    <th>Statut</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${bus.map(b => `
                                    <tr>
                                        <td><strong>${b.immatriculation}</strong></td>
                                        <td>${b.modele}</td>
                                        <td>${b.places}</td>
                                        <td>${b.agence_nom || '-'}</td>
                                        <td>${b.km_total.toLocaleString()} km</td>
                                        <td>${formatDate(b.date_visite_tech)}</td>
                                        <td>${formatDate(b.date_assurance)}</td>
                                        <td><span class="badge ${getStatutBadgeClass(b.statut)}">${b.statut}</span></td>
                                        <td>
                                            <button class="btn btn-sm btn-outline" onclick="showEditBusModal(${b.id})">Modifier</button>
                                            <button class="btn btn-sm btn-outline" onclick="showChangeStatutModal(${b.id}, '${b.statut}', '${b.immatriculation}')">Statut</button>
                                        </td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        `;
    } catch (error) {
        console.error('Error loading flotte:', error);
        container.innerHTML = `<div class="card"><div class="card-body"><p class="text-center" style="color: var(--red);">Erreur de chargement de la flotte</p></div></div>`;
    }
}

function showAddBusModal() {
    showModal(`
        <form onsubmit="saveBus(event)">
            <div class="form-grid">
                <div class="form-group">
                    <label>Immatriculation <span class="req">*</span></label>
                    <input type="text" name="immatriculation" required>
                </div>
                <div class="form-group">
                    <label>Modèle <span class="req">*</span></label>
                    <input type="text" name="modele" required>
                </div>
                <div class="form-group">
                    <label>Places <span class="req">*</span></label>
                    <input type="number" name="places" required>
                </div>
                <div class="form-group">
                    <label>Statut <span class="req">*</span></label>
                    <select name="statut" required>
                        <option value="operationnel">Opérationnel</option>
                        <option value="maintenance">Maintenance</option>
                        <option value="hors_service">Hors service</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Km total</label>
                    <input type="number" name="km_total" value="0">
                </div>
                <div class="form-group">
                    <label>Date visite technique</label>
                    <input type="date" name="date_visite_tech">
                </div>
                <div class="form-group full">
                    <label>Date assurance</label>
                    <input type="date" name="date_assurance">
                </div>
            </div>
            <button type="submit" class="btn btn-primary" style="width: 100%;">Enregistrer</button>
        </form>
    `);
}

function showChangeStatutModal(id, currentStatut, immatriculation) {
    showModal(`
        <form onsubmit="changeBusStatut(event, ${id})">
            <div class="form-group">
                <label>Bus : ${immatriculation}</label>
            </div>
            <div class="form-group">
                <label>Nouveau statut <span class="req">*</span></label>
                <select name="statut" required>
                    <option value="operationnel" ${currentStatut === 'operationnel' ? 'selected' : ''}>Opérationnel</option>
                    <option value="maintenance" ${currentStatut === 'maintenance' ? 'selected' : ''}>Maintenance</option>
                    <option value="hors_service" ${currentStatut === 'hors_service' ? 'selected' : ''}>Hors service</option>
                </select>
            </div>
            <button type="submit" class="btn btn-primary" style="width: 100%;">Changer le statut</button>
        </form>
    `);
}

async function changeBusStatut(event, id) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());
    
    try {
        await api(`/bus/${id}/statut`, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
        closeModal();
        showToast('Statut du bus modifié avec succès');
        navigate('flotte');
    } catch (error) {
        showToast(error.message, 'error');
    }
}

async function saveBus(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());
    
    try {
        await api('/bus', {
            method: 'POST',
            body: JSON.stringify(data)
        });
        closeModal();
        showToast('Bus ajouté avec succès');
        navigate('flotte');
    } catch (error) {
        showToast(error.message, 'error');
    }
}

async function renderComptabilite(container) {
    const today = new Date();
    const mois = today.getMonth() + 1;
    const annee = today.getFullYear();
    
    try {
        const [bilan, ecritures] = await Promise.all([
            api(`/compta/bilan?mois=${mois}&annee=${annee}`),
            api('/compta/ecritures')
        ]);
        
        // Store comptabilite data in state
        AppState.setModuleData('comptabilite', { bilan, ecritures, mois, annee });
        
        container.innerHTML = `
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-label">Recettes du mois</div>
                    <div class="stat-value" style="color: var(--green);">${formatFCFA(bilan.recettes)}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Dépenses du mois</div>
                    <div class="stat-value" style="color: var(--red);">${formatFCFA(bilan.depenses)}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Bénéfice net</div>
                    <div class="stat-value" style="color: ${bilan.benefice >= 0 ? 'var(--green)' : 'var(--red)'};">${formatFCFA(bilan.benefice)}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Marge</div>
                    <div class="stat-value">${bilan.marge}%</div>
                </div>
            </div>
            
            <div class="card" style="margin-bottom: 24px;">
                <div class="card-header">
                    <h3>Journal des écritures</h3>
                    <div style="display: flex; gap: 12px;">
                        <select class="form-select" style="width: 120px;" id="compta-mois-filter" onchange="filterComptabilite()">
                            ${[1,2,3,4,5,6,7,8,9,10,11,12].map(m => 
                                `<option value="${m}" ${m === mois ? 'selected' : ''}>${m}</option>`
                            ).join('')}
                        </select>
                        <select class="form-select" style="width: 100px;" id="compta-annee-filter" onchange="filterComptabilite()">
                            <option value="${annee}" selected>${annee}</option>
                            <option value="${annee-1}">${annee-1}</option>
                        </select>
                        <select class="form-select" style="width: 150px;" id="compta-type-filter" onchange="filterComptabilite()">
                            <option value="">Tous types</option>
                            <option value="recette">Recettes</option>
                            <option value="depense">Dépenses</option>
                        </select>
                        <button class="btn btn-primary btn-sm" onclick="showAddEcritureModal()">+ Écriture</button>
                    </div>
                </div>
                <div class="card-body">
                    <div class="table-wrap">
                        <table>
                            <thead>
                                <tr>
                                    <th>Date</th>
                                    <th>Description</th>
                                    <th>Catégorie</th>
                                    <th>Type</th>
                                    <th>Montant</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="compta-table-body">
                                ${ecritures.map(e => `
                                    <tr>
                                        <td>${formatDate(e.date_ecriture)}</td>
                                        <td>${e.description}</td>
                                        <td><span class="badge badge-blue">${e.categorie}</span></td>
                                        <td><span class="badge ${e.type === 'recette' ? 'badge-green' : 'badge-red'}">${e.type}</span></td>
                                        <td style="font-weight: 700; color: ${e.type === 'recette' ? 'var(--green)' : 'var(--red)'};">
                                            ${e.type === 'recette' ? '+' : '-'}${formatFCFA(e.montant)}
                                        </td>
                                        <td>
                                            <button class="btn btn-sm btn-outline" onclick="showEditEcritureModal(${e.id})">Modifier</button>
                                            <button class="btn btn-sm btn-red" onclick="deleteEcriture(${e.id})">Supprimer</button>
                                        </td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            
            <div class="card">
                <div class="card-header">
                    <h3>Balance des comptes</h3>
                </div>
                <div class="card-body">
                    <div class="table-wrap">
                        <table>
                            <thead>
                                <tr>
                                    <th>Catégorie</th>
                                    <th>Total recettes</th>
                                    <th>Total dépenses</th>
                                    <th>Solde</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${getBalanceByCategorie(ecritures).map(b => `
                                    <tr>
                                        <td><span class="badge badge-blue">${b.categorie}</span></td>
                                        <td style="color: var(--green);">${formatFCFA(b.recettes)}</td>
                                        <td style="color: var(--red);">${formatFCFA(b.depenses)}</td>
                                        <td style="font-weight: 700; color: ${b.solde >= 0 ? 'var(--green)' : 'var(--red)'};">${formatFCFA(b.solde)}</td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        `;
    } catch (error) {
        console.error('Error loading comptabilite:', error);
        container.innerHTML = `<div class="card"><div class="card-body"><p class="text-center" style="color: var(--red);">Erreur de chargement de la comptabilité</p></div></div>`;
    }
}

function getBalanceByCategorie(ecritures) {
    const categories = {};
    ecritures.forEach(e => {
        if (!categories[e.categorie]) {
            categories[e.categorie] = { categorie: e.categorie, recettes: 0, depenses: 0 };
        }
        if (e.type === 'recette') {
            categories[e.categorie].recettes += e.montant;
        } else {
            categories[e.categorie].depenses += e.montant;
        }
    });
    
    return Object.values(categories).map(c => ({
        ...c,
        solde: c.recettes - c.depenses
    }));
}

async function filterComptabilite() {
    const mois = document.getElementById('compta-mois-filter').value;
    const annee = document.getElementById('compta-annee-filter').value;
    const type = document.getElementById('compta-type-filter').value;
    
    try {
        const [bilan, ecritures] = await Promise.all([
            api(`/compta/bilan?mois=${mois}&annee=${annee}`),
            api('/compta/ecritures')
        ]);
        
        AppState.setModuleData('comptabilite', { bilan, ecritures, mois, annee });
        
        const filtered = type ? ecritures.filter(e => e.type === type) : ecritures;
        
        const tbody = document.getElementById('compta-table-body');
        tbody.innerHTML = filtered.map(e => `
            <tr>
                <td>${formatDate(e.date_ecriture)}</td>
                <td>${e.description}</td>
                <td><span class="badge badge-blue">${e.categorie}</span></td>
                <td><span class="badge ${e.type === 'recette' ? 'badge-green' : 'badge-red'}">${e.type}</span></td>
                <td style="font-weight: 700; color: ${e.type === 'recette' ? 'var(--green)' : 'var(--red)'};">
                    ${e.type === 'recette' ? '+' : '-'}${formatFCFA(e.montant)}
                </td>
                <td>
                    <button class="btn btn-sm btn-outline" onclick="showEditEcritureModal(${e.id})">Modifier</button>
                    <button class="btn btn-sm btn-red" onclick="deleteEcriture(${e.id})">Supprimer</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error filtering comptabilite:', error);
    }
}

async function deleteEcriture(id) {
    if (!confirm('Supprimer cette écriture ?')) return;
    
    try {
        await api(`/compta/ecritures/${id}`, { method: 'DELETE' });
        showToast('Écriture supprimée avec succès');
        navigate('comptabilite');
    } catch (error) {
        showToast(error.message, 'error');
    }
}

function showAddEcritureModal() {
    showModal(`
        <form onsubmit="saveEcriture(event)">
            <div class="form-group">
                <label class="form-label">Date</label>
                <input type="date" class="form-input" name="date_ecriture" required value="${new Date().toISOString().split('T')[0]}">
            </div>
            <div class="form-group">
                <label class="form-label">Description</label>
                <input type="text" class="form-input" name="description" required>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Type</label>
                    <select class="form-select" name="type" required>
                        <option value="recette">Recette</option>
                        <option value="depense">Dépense</option>
                    </select>
                </div>
                <div class="form-group">
                    <label class="form-label">Catégorie</label>
                    <select class="form-select" name="categorie" required>
                        <option value="billets">Billets</option>
                        <option value="colis">Colis</option>
                        <option value="salaires">Salaires</option>
                        <option value="carburant">Carburant</option>
                        <option value="maintenance">Maintenance</option>
                        <option value="assurance">Assurance</option>
                        <option value="autres">Autres</option>
                    </select>
                </div>
            </div>
            <div class="form-group">
                <label class="form-label">Montant (FCFA)</label>
                <input type="number" class="form-input" name="montant" required>
            </div>
            <button type="submit" class="btn btn-primary" style="width: 100%;">Enregistrer</button>
        </form>
    `);
}

async function saveEcriture(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());
    
    try {
        await api('/compta/ecritures', {
            method: 'POST',
            body: JSON.stringify(data)
        });
        closeModal();
        showToast('Écriture enregistrée avec succès');
        navigate('comptabilite');
    } catch (error) {
        showToast(error.message, 'error');
    }
}

async function renderLignes(container) {
    try {
        const lignes = await api('/lignes');
        
        // Store lignes data in state
        AppState.setModuleData('lignes', { lignes });
        
        container.innerHTML = `
            <div class="card">
                <div class="card-header">
                    <h3>Lignes</h3>
                    <button class="btn btn-primary btn-sm" onclick="showAddLigneModal()">+ Ligne</button>
                </div>
                <div class="card-body">
                    <div class="table-wrap">
                        <table>
                            <thead>
                                <tr>
                                    <th>Code</th>
                                    <th>Trajet</th>
                                    <th>Distance</th>
                                    <th>Durée</th>
                                    <th>Tarif</th>
                                    <th>Taux remplissage</th>
                                    <th>Statut</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${lignes.map(l => `
                                    <tr>
                                        <td><span class="badge badge-blue">${l.code}</span></td>
                                        <td>${l.ville_depart} → ${l.ville_arrivee}</td>
                                        <td>${l.distance_km} km</td>
                                        <td>${l.duree_h}h</td>
                                        <td>${formatFCFA(l.tarif_base)}</td>
                                        <td>
                                            <div style="display: flex; align-items: center; gap: 8px;">
                                                <div class="progress" style="width: 80px;">
                                                    <div class="progress-bar progress-blue" style="width: ${l.taux_remplissage}%"></div>
                                                </div>
                                                <span style="font-size: 12px;">${l.taux_remplissage}%</span>
                                            </div>
                                        </td>
                                        <td><span class="badge ${l.statut === 'actif' ? 'badge-green' : 'badge-red'}">${l.statut || 'actif'}</span></td>
                                        <td>
                                            <button class="btn btn-sm btn-outline" onclick="showEditLigneModal(${l.id})">Modifier</button>
                                            <button class="btn btn-sm btn-outline" onclick="toggleLigneStatut(${l.id}, '${l.statut || 'actif'}')">
                                                ${l.statut === 'actif' ? 'Désactiver' : 'Activer'}
                                            </button>
                                        </td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            
            <div class="card" style="margin-top: 24px;">
                <div class="card-header">
                    <h3>Performance des lignes</h3>
                </div>
                <div class="card-body">
                    <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 16px;">
                        ${lignes.map(l => `
                            <div style="background: var(--bg); padding: 16px; border-radius: 8px;">
                                <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
                                    <span style="font-weight: 700; color: var(--navy);">${l.code}</span>
                                    <span class="badge badge-blue">${l.taux_remplissage}%</span>
                                </div>
                                <div style="font-size: 13px; color: var(--text-muted); margin-bottom: 12px;">${l.ville_depart} → ${l.ville_arrivee}</div>
                                <div class="progress">
                                    <div class="progress-bar ${l.taux_remplissage >= 80 ? 'progress-green' : l.taux_remplissage >= 50 ? 'progress-blue' : 'progress-orange'}" style="width: ${l.taux_remplissage}%"></div>
                                </div>
                                <div style="display: flex; justify-content: space-between; margin-top: 8px; font-size: 12px;">
                                    <span style="color: var(--text-muted);">Tarif: ${formatFCFA(l.tarif_base)}</span>
                                    <span style="color: var(--text-muted);">${l.distance_km} km</span>
                                </div>
                            </div>
                        `).join('')}
                    </div>
                </div>
            </div>
        `;
    } catch (error) {
        console.error('Error loading lignes:', error);
        container.innerHTML = `<div class="card"><div class="card-body"><p class="text-center" style="color: var(--red);">Erreur de chargement des lignes</p></div></div>`;
    }
}

async function toggleLigneStatut(id, currentStatut) {
    const newStatut = currentStatut === 'actif' ? 'inactif' : 'actif';
    
    try {
        await api(`/lignes/${id}/statut`, {
            method: 'PUT',
            body: JSON.stringify({ statut: newStatut })
        });
        showToast(`Ligne ${newStatut === 'actif' ? 'activée' : 'désactivée'} avec succès`);
        navigate('lignes');
    } catch (error) {
        showToast(error.message, 'error');
    }
}

function showAddLigneModal() {
    showModal(`
        <form onsubmit="saveLigne(event)">
            <div class="form-grid">
                <div class="form-group">
                    <label>Code <span class="req">*</span></label>
                    <input type="text" name="code" placeholder="YDE-DBA" required>
                </div>
                <div class="form-group">
                    <label>Tarif base (FCFA) <span class="req">*</span></label>
                    <input type="number" name="tarif_base" required>
                </div>
                <div class="form-group">
                    <label>Ville départ <span class="req">*</span></label>
                    <input type="text" name="ville_depart" required>
                </div>
                <div class="form-group">
                    <label>Ville arrivée <span class="req">*</span></label>
                    <input type="text" name="ville_arrivee" required>
                </div>
                <div class="form-group">
                    <label>Distance (km) <span class="req">*</span></label>
                    <input type="number" name="distance_km" required>
                </div>
                <div class="form-group">
                    <label>Durée (heures) <span class="req">*</span></label>
                    <input type="number" name="duree_h" step="0.5" required>
                </div>
            </div>
            <button type="submit" class="btn btn-primary" style="width: 100%;">Enregistrer</button>
        </form>
    `);
}

async function saveLigne(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());
    
    try {
        await api('/lignes', {
            method: 'POST',
            body: JSON.stringify(data)
        });
        closeModal();
        showToast('Ligne créée avec succès');
        navigate('lignes');
    } catch (error) {
        showToast(error.message, 'error');
    }
}

async function renderCourriers(container) {
    try {
        const result = await api('/courriers');
        
        // Store courriers data in state
        AppState.setModuleData('courriers', { 
            courriers: result.data, 
            nonLus: result.non_lus 
        });
        
        container.innerHTML = `
            <div class="card">
                <div class="card-header">
                    <h3>
                        Courriers
                        ${result.non_lus > 0 ? `<span class="badge badge-orange" style="margin-left: 8px;">${result.non_lus} non lu(s)</span>` : ''}
                    </h3>
                    <button class="btn btn-primary btn-sm" onclick="showAddCourrierModal()">+ Courrier</button>
                </div>
                <div class="card-body">
                    <div style="margin-bottom: 16px; display: flex; gap: 12px;">
                        <input type="text" placeholder="Rechercher..." class="form-input" style="flex: 1;" id="courrier-search" onkeyup="filterCourriers()">
                        <select class="form-select" style="width: 150px;" id="courrier-statut-filter" onchange="filterCourriers()">
                            <option value="">Tous statuts</option>
                            <option value="non_lu">Non lu</option>
                            <option value="lu">Lu</option>
                            <option value="archive">Archivé</option>
                        </select>
                        <select class="form-select" style="width: 150px;" id="courrier-priorite-filter" onchange="filterCourriers()">
                            <option value="">Toutes priorités</option>
                            <option value="normale">Normale</option>
                            <option value="urgente">Urgente</option>
                        </select>
                    </div>
                    <div class="table-wrap">
                        <table>
                            <thead>
                                <tr>
                                    <th>Objet</th>
                                    <th>Expéditeur</th>
                                    <th>Destinataire</th>
                                    <th>Type</th>
                                    <th>Priorité</th>
                                    <th>Statut</th>
                                    <th>Date</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="courriers-table-body">
                                ${result.data.map(c => `
                                    <tr style="${c.statut === 'non_lu' ? 'font-weight: 700;' : ''}" data-courrier-id="${c.id}">
                                        <td>${c.objet}</td>
                                        <td>${c.expediteur}</td>
                                        <td>${c.destinataire}</td>
                                        <td><span class="badge badge-blue">${c.type}</span></td>
                                        <td>${c.priorite === 'urgente' ? '<span class="badge badge-red">URGENTE</span>' : '<span class="badge badge-gray">Normale</span>'}</td>
                                        <td><span class="badge ${c.statut === 'non_lu' ? 'badge-red' : c.statut === 'lu' ? 'badge-green' : 'badge-gray'}">${c.statut}</span></td>
                                        <td>${formatDate(c.created_at)}</td>
                                        <td>
                                            <button class="btn btn-sm btn-outline" onclick="showCourrierDetail(${c.id})">Voir</button>
                                            ${c.statut === 'non_lu' ? `<button class="btn btn-sm btn-green" onclick="marquerCourrierLu(${c.id})">Marquer lu</button>` : ''}
                                        </td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        `;
    } catch (error) {
        console.error('Error loading courriers:', error);
        container.innerHTML = `<div class="card"><div class="card-body"><p class="text-center" style="color: var(--red);">Erreur de chargement des courriers</p></div></div>`;
    }
}

async function showCourrierDetail(id) {
    try {
        const courrier = await api(`/courriers/${id}`);
        showModal(`
            <div>
                <h3 style="color: var(--navy); margin-bottom: 16px;">${courrier.objet}</h3>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 16px;">
                    <div>
                        <div style="font-size: 12px; color: var(--text-muted);">De</div>
                        <div style="font-weight: 600;">${courrier.expediteur}</div>
                    </div>
                    <div>
                        <div style="font-size: 12px; color: var(--text-muted);">À</div>
                        <div style="font-weight: 600;">${courrier.destinataire}</div>
                    </div>
                </div>
                <div style="background: var(--bg); padding: 16px; border-radius: 8px; margin-bottom: 16px; white-space: pre-wrap;">${courrier.contenu}</div>
                ${courrier.statut === 'non_lu' ? `<button class="btn btn-primary" onclick="marquerCourrierLu(${id})">Marquer comme lu</button>` : ''}
            </div>
        `);
    } catch (error) {
        showToast(error.message, 'error');
    }
}

function filterCourriers() {
    const search = document.getElementById('courrier-search').value.toLowerCase();
    const statut = document.getElementById('courrier-statut-filter').value;
    const priorite = document.getElementById('courrier-priorite-filter').value;
    
    const courriersData = AppState.getModuleData('courriers');
    const allCourriers = courriersData.courriers || [];
    
    const filtered = allCourriers.filter(c => {
        const matchSearch = !search || 
            c.objet.toLowerCase().includes(search) || 
            c.expediteur.toLowerCase().includes(search) ||
            c.destinataire.toLowerCase().includes(search);
        const matchStatut = !statut || c.statut === statut;
        const matchPriorite = !priorite || c.priorite === priorite;
        return matchSearch && matchStatut && matchPriorite;
    });
    
    const tbody = document.getElementById('courriers-table-body');
    tbody.innerHTML = filtered.map(c => `
        <tr style="${c.statut === 'non_lu' ? 'font-weight: 700;' : ''}" data-courrier-id="${c.id}">
            <td>${c.objet}</td>
            <td>${c.expediteur}</td>
            <td>${c.destinataire}</td>
            <td><span class="badge badge-blue">${c.type}</span></td>
            <td>${c.priorite === 'urgente' ? '<span class="badge badge-red">URGENTE</span>' : '<span class="badge badge-gray">Normale</span>'}</td>
            <td><span class="badge ${c.statut === 'non_lu' ? 'badge-red' : c.statut === 'lu' ? 'badge-green' : 'badge-gray'}">${c.statut}</span></td>
            <td>${formatDate(c.created_at)}</td>
            <td>
                <button class="btn btn-sm btn-outline" onclick="showCourrierDetail(${c.id})">Voir</button>
                ${c.statut === 'non_lu' ? `<button class="btn btn-sm btn-green" onclick="marquerCourrierLu(${c.id})">Marquer lu</button>` : ''}
            </td>
        </tr>
    `).join('');
}

async function marquerCourrierLu(id) {
    try {
        await api(`/courriers/${id}/lire`, { method: 'PUT' });
        closeModal();
        showToast('Courrier marqué comme lu');
        navigate('courriers');
    } catch (error) {
        showToast(error.message, 'error');
    }
}

function showAddCourrierModal() {
    showModal(`
        <form onsubmit="saveCourrier(event)">
            <div class="form-group">
                <label class="form-label">Objet</label>
                <input type="text" class="form-input" name="objet" required>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Expéditeur</label>
                    <input type="text" class="form-input" name="expediteur" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Destinataire</label>
                    <input type="text" class="form-input" name="destinataire" required>
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Type</label>
                    <select class="form-select" name="type" required>
                        <option value="interne">Interne</option>
                        <option value="entrant">Entrant</option>
                        <option value="sortant">Sortant</option>
                    </select>
                </div>
                <div class="form-group">
                    <label class="form-label">Priorité</label>
                    <select class="form-select" name="priorite">
                        <option value="normale">Normale</option>
                        <option value="urgente">Urgente</option>
                    </select>
                </div>
            </div>
            <div class="form-group">
                <label class="form-label">Contenu</label>
                <textarea class="form-input" name="contenu" rows="5" required></textarea>
            </div>
            <button type="submit" class="btn btn-primary" style="width: 100%;">Enregistrer</button>
        </form>
    `);
}

async function saveCourrier(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());
    
    try {
        await api('/courriers', {
            method: 'POST',
            body: JSON.stringify(data)
        });
        closeModal();
        showToast('Courrier créé avec succès');
        navigate('courriers');
    } catch (error) {
        showToast(error.message, 'error');
    }
}

async function renderParametres(container) {
    const user = getCurrentUser();
    
    // Store parametres data in state
    AppState.setModuleData('parametres', { user });
    
    container.innerHTML = `
        <div class="card" style="margin-bottom: 24px;">
            <div class="card-header">
                <h3>Mon compte</h3>
            </div>
            <div class="card-body">
                <div style="max-width: 600px;">
                    <div style="background: var(--bg); padding: 24px; border-radius: 8px; margin-bottom: 24px;">
                        <div style="display: flex; align-items: center; gap: 16px; margin-bottom: 20px;">
                            <div class="avatar avatar-blue" style="width: 64px; height: 64px; font-size: 24px;">
                                ${user.nom ? user.nom[0] : 'U'}${user.prenom ? user.prenom[0] : ''}
                            </div>
                            <div>
                                <div style="font-size: 20px; font-weight: 700; color: var(--navy);">${user.nom} ${user.prenom || ''}</div>
                                <div style="font-size: 14px; color: var(--text-muted);">${user.email}</div>
                            </div>
                        </div>
                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
                            <div>
                                <div style="font-size: 12px; color: var(--text-muted); text-transform: uppercase;">Rôle</div>
                                <div><span class="badge badge-blue">${user.role}</span></div>
                            </div>
                            <div>
                                <div style="font-size: 12px; color: var(--text-muted); text-transform: uppercase;">Agence</div>
                                <div style="font-weight: 600;">${user.agence || 'Non assigné'}</div>
                            </div>
                        </div>
                    </div>
                    
                    <button class="btn btn-outline" onclick="showEditProfileModal()">Modifier mon profil</button>
                </div>
            </div>
        </div>
        
        <div class="card">
            <div class="card-header">
                <h3>Sécurité</h3>
            </div>
            <div class="card-body">
                <div style="max-width: 600px;">
                    <form onsubmit="changePassword(event)">
                        <div class="form-group">
                            <label>Mot de passe actuel <span class="req">*</span></label>
                            <input type="password" name="current_password" required>
                        </div>
                        <div class="form-group">
                            <label>Nouveau mot de passe <span class="req">*</span></label>
                            <input type="password" name="new_password" required minlength="8">
                        </div>
                        <div class="form-group">
                            <label>Confirmer le mot de passe <span class="req">*</span></label>
                            <input type="password" name="confirm_password" required minlength="8">
                        </div>
                        <button type="submit" class="btn btn-primary">Changer le mot de passe</button>
                    </form>
                </div>
            </div>
        </div>
    `;
}

function showEditProfileModal() {
    const user = getCurrentUser();
    showModal(`
        <form onsubmit="updateProfile(event)">
            <div class="form-grid">
                <div class="form-group">
                    <label>Nom <span class="req">*</span></label>
                    <input type="text" name="nom" value="${user.nom}" required>
                </div>
                <div class="form-group">
                    <label>Prénom</label>
                    <input type="text" name="prenom" value="${user.prenom || ''}">
                </div>
                <div class="form-group full">
                    <label>Email <span class="req">*</span></label>
                    <input type="email" name="email" value="${user.email}" required>
                </div>
                <div class="form-group">
                    <label>Téléphone</label>
                    <input type="tel" name="telephone" value="${user.telephone || ''}">
                </div>
                <div class="form-group">
                    <label>Agence</label>
                    <input type="text" name="agence" value="${user.agence || ''}">
                </div>
            </div>
            <div style="display: flex; gap: 12px; margin-top: 16px;">
                <button type="button" class="btn btn-outline" onclick="closeModal()">Annuler</button>
                <button type="submit" class="btn btn-primary">Sauvegarder</button>
            </div>
        </form>
    `);
}

async function updateProfile(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());
    
    try {
        await api('/utilisateur/profile', {
            method: 'PUT',
            body: JSON.stringify(data)
        });
        
        // Update user in state
        AppState.setUser({ ...AppState.user, ...data }, AppState.token);
        
        closeModal();
        showToast('Profil mis à jour avec succès');
        navigate('parametres');
    } catch (error) {
        showToast(error.message, 'error');
    }
}

async function changePassword(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());
    
    if (data.new_password !== data.confirm_password) {
        showToast('Les mots de passe ne correspondent pas', 'error');
        return;
    }
    
    if (data.new_password.length < 8) {
        showToast('Le mot de passe doit contenir au moins 8 caractères', 'error');
        return;
    }
    
    try {
        await api('/utilisateur/change-password', {
            method: 'POST',
            body: JSON.stringify({
                current_password: data.current_password,
                new_password: data.new_password
            })
        });
        
        form.reset();
        showToast('Mot de passe changé avec succès');
    } catch (error) {
        showToast(error.message, 'error');
    }
}

// ============ HELPERS ============

function getStatutBadgeClass(statut) {
    const classes = {
        'actif': 'badge-success',
        'annule': 'badge-danger',
        'utilise': 'badge-info',
        'planifie': 'badge-info',
        'ouvert': 'badge-success',
        'embarquement': 'badge-warning',
        'en_route': 'badge-info',
        'arrive': 'badge-success',
        'operationnel': 'badge-success',
        'maintenance': 'badge-warning',
        'hors_service': 'badge-danger',
        'enregistre': 'badge-info',
        'pris_en_charge': 'badge-warning',
        'en_transit': 'badge-info',
        'livre': 'badge-success',
        'non_reclame': 'badge-danger',
        'en_livraison': 'badge-warning',
        'paye': 'badge-success',
        'en_attente': 'badge-warning',
        'lu': 'badge-success',
        'non_lu': 'badge-danger'
    };
    return classes[statut] || 'badge-info';
}
