// ============ API CLIENT ============

async function api(endpoint, options = {}) {
    const token = localStorage.getItem('sigavt_token');
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
    localStorage.removeItem('sigavt_token');
    localStorage.removeItem('sigavt_user');
    window.location.href = '/login';
}

function getCurrentUser() {
    const user = localStorage.getItem('sigavt_user');
    return user ? JSON.parse(user) : null;
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
    
    // Update sidebar active state
    document.querySelectorAll('.dashboard-nav-item').forEach(item => {
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
    
    document.querySelector('.dashboard-topbar-title').textContent = titles[page] || page;
    
    // Render page content
    const content = document.getElementById('main-content');
    content.innerHTML = '<div class="spinner-overlay active"><div class="spinner"></div></div>';
    
    try {
        pages[page](content, params);
    } catch (error) {
        console.error('Error rendering page:', error);
        content.innerHTML = `<div class="dashboard-card"><div class="dashboard-card-body"><p class="text-center" style="color: var(--red);">Erreur de chargement de la page</p></div></div>`;
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
        
        container.innerHTML = `
            <div class="dashboard-stats-grid">
                <div class="dashboard-stat-card">
                    <div class="dashboard-stat-label">Recettes du jour</div>
                    <div class="dashboard-stat-value">${formatFCFA(stats.recettes_jour)}</div>
                    <div class="dashboard-stat-variation ${stats.variation_recettes >= 0 ? 'positive' : 'negative'}">
                        ${stats.variation_recettes >= 0 ? '↑' : '↓'} ${Math.abs(stats.variation_recettes)}% vs hier
                    </div>
                </div>
                <div class="dashboard-stat-card">
                    <div class="dashboard-stat-label">Billets vendus</div>
                    <div class="dashboard-stat-value">${stats.billets_jour}</div>
                    <div class="dashboard-stat-variation ${stats.variation_billets >= 0 ? 'positive' : 'negative'}">
                        ${stats.variation_billets >= 0 ? '↑' : '↓'} ${Math.abs(stats.variation_billets)}% vs hier
                    </div>
                </div>
                <div class="dashboard-stat-card">
                    <div class="dashboard-stat-label">Colis en transit</div>
                    <div class="dashboard-stat-value">${stats.colis_transit}</div>
                    <div class="dashboard-stat-variation" style="color: var(--text-muted);">
                        Sur toutes les lignes
                    </div>
                </div>
                <div class="dashboard-stat-card">
                    <div class="dashboard-stat-label">Bus en service</div>
                    <div class="dashboard-stat-value">${stats.bus_service}</div>
                    <div class="dashboard-stat-variation" style="color: var(--text-muted);">
                        Disponibles
                    </div>
                </div>
            </div>
            
            <div class="dashboard-card">
                <div class="dashboard-card-header">
                    <h3 class="dashboard-card-title">Départs du jour</h3>
                </div>
                <div class="dashboard-card-body">
                    <table class="dashboard-table">
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
                                    <td><span class="badge badge-navy">${d.code}</span></td>
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
            
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
                <div class="dashboard-card">
                    <div class="dashboard-card-header">
                        <h3 class="dashboard-card-title">Recettes / semaine</h3>
                    </div>
                    <div class="dashboard-card-body" id="recettes-chart"></div>
                </div>
                <div class="dashboard-card">
                    <div class="dashboard-card-header">
                        <h3 class="dashboard-card-title">Top lignes</h3>
                    </div>
                    <div class="dashboard-card-body">
                        ${topLignes.map(l => `
                            <div style="margin-bottom: 16px;">
                                <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
                                    <span style="font-weight: 600;">${l.ligne}</span>
                                    <span style="color: var(--text-muted);">${l.code}</span>
                                </div>
                                <div class="progress-bar">
                                    <div class="progress-bar-fill" style="width: ${l.taux}%"></div>
                                </div>
                                <div style="text-align: right; font-size: 12px; color: var(--text-muted); margin-top: 4px;">${l.taux}% remplissage</div>
                            </div>
                        `).join('')}
                    </div>
                </div>
            </div>
            
            ${stats.alertes.length > 0 ? `
                <div class="dashboard-card">
                    <div class="dashboard-card-header">
                        <h3 class="dashboard-card-title">Alertes</h3>
                    </div>
                    <div class="dashboard-card-body">
                        ${stats.alertes.map(a => `
                            <div class="badge badge-warning" style="display: inline-block; margin-right: 8px; margin-bottom: 8px;">
                                ${a.message}
                            </div>
                        `).join('')}
                    </div>
                </div>
            ` : ''}
        `;
        
        // Render chart
        const chartData = recettes.map(r => ({
            label: new Date(r.date).toLocaleDateString('fr-FR', { weekday: 'short' }),
            value: r.montant
        }));
        barChart(document.getElementById('recettes-chart'), chartData, { width: 350, height: 180 });
        
    } catch (error) {
        console.error('Error loading dashboard:', error);
        container.innerHTML = `<div class="dashboard-card"><div class="dashboard-card-body"><p class="text-center" style="color: var(--red);">Erreur de chargement du tableau de bord</p></div></div>`;
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

let billetData = {};

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
    
    billetData.places = 1;
}

function adjustPlaces(delta) {
    const count = document.getElementById('billet-places-count');
    const newValue = Math.max(1, Math.min(10, parseInt(count.textContent) + delta));
    count.textContent = newValue;
    billetData.places = newValue;
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
    if (placesDispo < billetData.places) {
        showToast(`Pas assez de places disponibles (${placesDispo} places)`, 'error');
        return;
    }
    
    billetData.voyageId = id;
    billetData.heure = heure;
    billetData.tarif = tarif;
    billetData.bus = bus;
    billetData.chauffeur = chauffeur;
    billetData.total = tarif * billetData.places;
    
    document.getElementById('billet-recap').classList.remove('hidden');
    document.getElementById('billet-recap').innerHTML = `
        <div style="font-weight: 700; margin-bottom: 8px;">Récapitulatif</div>
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 8px; font-size: 14px;">
            <div>Heure : <strong>${heure}</strong></div>
            <div>Bus : <strong>${bus}</strong></div>
            <div>Chauffeur : <strong>${chauffeur}</strong></div>
            <div>Total : <strong style="color: var(--gold);">${formatFCFA(billetData.total)}</strong></div>
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
    billetData.passagers = [];
    
    for (let i = 0; i < billetData.places; i++) {
        const nom = document.getElementById(`passager-nom-${i}`).value;
        const tel = document.getElementById(`passager-tel-${i}`).value;
        const cni = document.getElementById(`passager-cni-${i}`).value;
        
        if (!nom || !tel) {
            showToast(`Veuillez remplir le nom et téléphone du passager ${i + 1}`, 'error');
            return;
        }
        
        billetData.passagers.push({ nom, tel, cni });
    }
    
    goToBilletStep(3);
}

async function loadBilletStep3() {
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
        
        billetData.selectedSieges = [];
    } catch (error) {
        console.error('Error loading seats:', error);
        showToast('Erreur de chargement du plan de siège', 'error');
    }
}

function selectSiege(siege, element) {
    if (element.classList.contains('occupied')) return;
    
    if (billetData.selectedSieges.includes(siege)) {
        billetData.selectedSieges = billetData.selectedSieges.filter(s => s !== siege);
        element.classList.remove('selected');
        element.classList.add('available');
    } else {
        if (billetData.selectedSieges.length >= billetData.places) {
            showToast(`Vous avez déjà sélectionné ${billetData.places} siège(s)`, 'error');
            return;
        }
        billetData.selectedSieges.push(siege);
        element.classList.remove('available');
        element.classList.add('selected');
    }
    
    document.getElementById('selected-sieges').textContent = 
        `Sièges sélectionnés : ${billetData.selectedSieges.length > 0 ? billetData.selectedSieges.join(', ') : 'Aucun'}`;
    
    document.getElementById('continue-sieges').disabled = billetData.selectedSieges.length !== billetData.places;
}

function validateSiegesAndContinue() {
    goToBilletStep(4);
}

function loadBilletStep4() {
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
    
    billetData.modePaiement = null;
}

function selectPaiement(mode, button) {
    document.querySelectorAll('[id^="paiement-"]').forEach(b => {
        b.classList.remove('btn-primary');
        b.classList.add('btn-outline');
    });
    button.classList.remove('btn-outline');
    button.classList.add('btn-primary');
    billetData.modePaiement = mode;
    document.getElementById('confirmer-billet').disabled = false;
}

async function confirmerBillet() {
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
        loadBilletStep1();
    } catch (error) {
        hideSpinner();
        showToast(error.message, 'error');
    }
}

async function renderColis(container) {
    container.innerHTML = `
        <div class="dashboard-card">
            <div class="dashboard-card-header">
                <h3 class="dashboard-card-title">Gestion des colis</h3>
            </div>
            <div class="dashboard-card-body">
                <div style="display: flex; gap: 12px; margin-bottom: 20px;">
                    <button class="btn btn-primary" onclick="showColisTab('enregistrer')">Enregistrer</button>
                    <button class="btn btn-outline" onclick="showColisTab('suivi')">Suivi</button>
                    <button class="btn btn-outline" onclick="showColisTab('liste')">Liste</button>
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
    if (tab === 'enregistrer') loadColisEnregistrer();
    if (tab === 'suivi') loadColisSuivi();
    if (tab === 'liste') loadColisListe();
}

function loadColisEnregistrer() {
    document.getElementById('colis-tab-content').innerHTML = `
        <form id="colis-form" onsubmit="enregistrerColis(event)">
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Expéditeur - Nom</label>
                    <input type="text" class="form-input" id="colis-expediteur-nom" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Expéditeur - Téléphone</label>
                    <input type="tel" class="form-input" id="colis-expediteur-tel" required>
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Destinataire - Nom</label>
                    <input type="text" class="form-input" id="colis-destinataire-nom" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Destinataire - Téléphone</label>
                    <input type="tel" class="form-input" id="colis-destinataire-tel" required>
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Poids (kg)</label>
                    <input type="number" class="form-input" id="colis-poids" step="0.1" required onchange="calculerTarifColis()">
                </div>
                <div class="form-group">
                    <label class="form-label">Description</label>
                    <input type="text" class="form-input" id="colis-description">
                </div>
            </div>
            <div class="form-group">
                <label class="form-label">Options</label>
                <div style="display: flex; gap: 16px;">
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
                <label class="form-label">Tarif calculé</label>
                <div id="colis-tarif" style="font-size: 24px; font-weight: 800; color: var(--gold);">0 FCFA</div>
            </div>
            <div class="form-group">
                <label class="form-label">Mode de paiement</label>
                <select class="form-select" id="colis-paiement">
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
                    <div style="_font-size: 12px; color: var(--text-muted); text-transform: uppercase;">Numéro de tracking</div>
                    <div style="font-size: 28px; font-weight: 800; color: var(--navy);">${result.numero_tracking}</div>
                </div>
                <button class="btn btn-primary" onclick="closeModal(); showColisTab('enregistrer();">Nouveau colis</button>
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
        
        container.innerHTML = `
            <div class="dashboard-card">
                <div class="dashboard-card-header">
                    <h3 class="dashboard-card-title">Personnel</h3>
                    <button class="btn btn-primary btn-sm" onclick="showAddEmployeModal()">+ Employé</button>
                </div>
                <div class="dashboard-card-body">
                    <div style="margin-bottom: 16px; display: flex; gap: 12px;">
                        <input type="text" placeholder="Rechercher..." class="form-input" style="flex: 1;">
                        <select class="form-select" style="width: 150px;">
                            <option value="">Tous postes</option>
                            <option value="chauffeur">Chauffeur</option>
                            <option value="billetterie">Billetterie</option>
                            <option value="convoyeur">Convoyeur</option>
                            <option value="comptable">Comptable</option>
                        </select>
                    </div>
                    <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 16px;">
                        ${employes.map(e => `
                            <div style="background: var(--bg); padding: 16px; border-radius: 8px; display: flex; align-items: center; gap: 12px;">
                                <div class="dashboard-user-avatar" style="width: 48px; height: 48px; font-size: 18px;">
                                    ${e.prenom[0]}${e.nom[0]}
                                </div>
                                <div style="flex: 1;">
                                    <div style="font-weight: 700; color: var(--navy);">${e.nom} ${e.prenom}</div>
                                    <div style="font-size: 13px; color: var(--text-muted);">${e.poste}</div>
                                    <div style="margin-top: 4px;">
                                        <span class="badge ${e.statut === 'actif' ? 'badge-success' : 'badge-warning'}">${e.statut}</span>
                                        <span class="badge badge-info">${e.type_contrat}</span>
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
    }
}

function showAddEmployeModal() {
    showModal(`
        <form onsubmit="saveEmploye(event)">
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Nom</label>
                    <input type="text" class="form-input" name="nom" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Prénom</label>
                    <input type="text" class="form-input" name="prenom" required>
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Poste</label>
                    <select class="form-select" name="poste" required>
                        <option value="chauffeur">Chauffeur</option>
                        <option value="billetterie">Billetterie</option>
                        <option value="convoyeur">Convoyeur</option>
                        <option value="comptable">Comptable</option>
                        <option value="responsable_flotte">Responsable flotte</option>
                        <option value="directeur">Directeur</option>
                    </select>
                </div>
                <div class="form-group">
                    <label class="form-label">Type contrat</label>
                    <select class="form-select" name="type_contrat" required>
                        <option value="CDI">CDI</option>
                        <option value="CDD">CDD</option>
                    </select>
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Date embauche</label>
                    <input type="date" class="form-input" name="date_embauche" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Salaire base (FCFA)</label>
                    <input type="number" class="form-input" name="salaire_base" required>
                </div>
            </div>
            <div class="form-group">
                <label class="form-label">Téléphone</label>
                <input type="tel" class="form-input" name="telephone">
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">CNI</label>
                    <input type="text" class="form-input" name="cni">
                </div>
                <div class="form-group">
                    <label class="form-label">CNPS</label>
                    <input type="text" class="form-input" name="cnps">
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
        
        container.innerHTML = `
            <div class="dashboard-stats-grid">
                <div class="dashboard-stat-card">
                    <div class="dashboard-stat-label">Masse salariale brute</div>
                    <div class="dashboard-stat-value">${formatFCFA(stats.masse_salariale)}</div>
                </div>
                <div class="dashboard-stat-card">
                    <div class="dashboard-stat-label">Net à payer total</div>
                    <div class="dashboard-stat-value" style="color: var(--gold);">${formatFCFA(stats.net_total)}</div>
                </div>
                <div class="dashboard-stat-card">
                    <div class="dashboard-stat-label">Cotisations CNPS (employeur)</div>
                    <div class="dashboard-stat-value">${formatFCFA(stats.cnps_total)}</div>
                </div>
                <div class="dashboard-stat-card">
                    <div class="dashboard-stat-label">Bulletins générés</div>
                    <div class="dashboard-stat-value">${stats.bulletins}</div>
                </div>
            </div>
            
            <div class="dashboard-card">
                <div class="dashboard-card-header">
                    <h3 class="dashboard-card-title">Fiches de paie - ${mois}/${annee}</h3>
                    <button class="btn btn-primary btn-sm" onclick="genererFichesPaie()">Générer fiches</button>
                </div>
                <div class="dashboard-card-body">
                    <table class="dashboard-table">
                        <thead>
                            <tr>
                                <th>Employé</th>
                                <th>Poste</th>
                                <th>Salaire brut</th>
                                <th>Net à payer</th>
                                <th>Statut</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${fiches.map(f => `
                                <tr onclick="showFichePaieDetail(${f.id})" style="cursor: pointer;">
                                    <td>
                                        <div style="display: flex; align-items: center; gap: 8px;">
                                            <div class="dashboard-user-avatar" style="width: 32px; height: 32px; font-size: 12px;">
                                                ${f.prenom[0]}${f.nom[0]}
                                            </div>
                                            <span>${f.nom} ${f.prenom}</span>
                                        </div>
                                    </td>
                                    <td>${f.poste}</td>
                                    <td>${formatFCFA(f.salaire_brut)}</td>
                                    <td style="font-weight: 700; color: var(--gold);">${formatFCFA(f.net_a_payer)}</td>
                                    <td><span class="badge ${f.statut === 'paye' ? 'badge-success' : 'badge-warning'}">${f.statut}</span></td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            </div>
            
            <div id="fiche-detail" class="dashboard-card hidden">
                <!-- Fiche detail will be shown here -->
            </div>
        `;
    } catch (error) {
        console.error('Error loading paie:', error);
    }
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
        
        const operationnel = bus.filter(b => b.statut === 'operationnel').length;
        const maintenance = bus.filter(b => b.statut === 'maintenance').length;
        const horsService = bus.filter(b => b.statut === 'hors_service').length;
        
        container.innerHTML = `
            <div class="dashboard-stats-grid">
                <div class="dashboard-stat-card">
                    <div class="dashboard-stat-label">Total bus</div>
                    <div class="dashboard-stat-value">${bus.length}</div>
                </div>
                <div class="dashboard-stat-card">
                    <div class="dashboard-stat-label">En service</div>
                    <div class="dashboard-stat-value" style="color: var(--green);">${operationnel}</div>
                </div>
                <div class="dashboard-stat-card">
                    <div class="dashboard-stat-label">En maintenance</div>
                    <div class="dashboard-stat-value" style="color: var(--yellow);">${maintenance}</div>
                </div>
                <div class="dashboard-stat-card">
                    <div class="dashboard-stat-label">Hors service</div>
                    <div class="dashboard-stat-value" style="color: var(--red);">${horsService}</div>
                </div>
            </div>
            
            <div class="dashboard-card">
                <div class="dashboard-card-header">
                    <h3 class="dashboard-card-title">Flotte de bus</h3>
                    <button class="btn btn-primary btn-sm" onclick="showAddBusModal()">+ Bus</button>
                </div>
                <div class="dashboard-card-body">
                    <table class="dashboard-table">
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
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            </div>
        `;
    } catch (error) {
        console.error('Error loading flotte:', error);
    }
}

function showAddBusModal() {
    showModal(`
        <form onsubmit="saveBus(event)">
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Immatriculation</label>
                    <input type="text" class="form-input" name="immatriculation" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Modèle</label>
                    <input type="text" class="form-input" name="modele" required>
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Places</label>
                    <input type="number" class="form-input" name="places" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Statut</label>
                    <select class="form-select" name="statut" required>
                        <option value="operationnel">Opérationnel</option>
                        <option value="maintenance">Maintenance</option>
                        <option value="hors_service">Hors service</option>
                    </select>
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Km total</label>
                    <input type="number" class="form-input" name="km_total" value="0">
                </div>
                <div class="form-group">
                    <label class="form-label">Date visite technique</label>
                    <input type="date" class="form-input" name="date_visite_tech">
                </div>
            </div>
            <div class="form-group">
                <label class="form-label">Date assurance</label>
                <input type="date" class="form-input" name="date_assurance">
            </div>
            <button type="submit" class="btn btn-primary" style="width: 100%;">Enregistrer</button>
        </form>
    `);
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
        
        container.innerHTML = `
            <div class="dashboard-stats-grid">
                <div class="dashboard-stat-card">
                    <div class="dashboard-stat-label">Recettes du mois</div>
                    <div class="dashboard-stat-value" style="color: var(--green);">${formatFCFA(bilan.recettes)}</div>
                </div>
                <div class="dashboard-stat-card">
                    <div class="dashboard-stat-label">Dépenses du mois</div>
                    <div class="dashboard-stat-value" style="color: var(--red);">${formatFCFA(bilan.depenses)}</div>
                </div>
                <div class="dashboard-stat-card">
                    <div class="dashboard-stat-label">Bénéfice net</div>
                    <div class="dashboard-stat-value" style="color: ${bilan.benefice >= 0 ? 'var(--green)' : 'var(--red)'};">${formatFCFA(bilan.benefice)}</div>
                </div>
                <div class="dashboard-stat-card">
                    <div class="dashboard-stat-label">Marge</div>
                    <div class="dashboard-stat-value">${bilan.marge}%</div>
                </div>
            </div>
            
            <div class="dashboard-card">
                <div class="dashboard-card-header">
                    <h3 class="dashboard-card-title">Journal des écritures</h3>
                    <button class="btn btn-primary btn-sm" onclick="showAddEcritureModal()">+ Écriture</button>
                </div>
                <div class="dashboard-card-body">
                    <table class="dashboard-table">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Description</th>
                                <th>Catégorie</th>
                                <th>Type</th>
                                <th>Montant</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${ecritures.map(e => `
                                <tr>
                                    <td>${formatDate(e.date_ecriture)}</td>
                                    <td>${e.description}</td>
                                    <td><span class="badge badge-info">${e.categorie}</span></td>
                                    <td><span class="badge ${e.type === 'recette' ? 'badge-success' : 'badge-danger'}">${e.type}</span></td>
                                    <td style="font-weight: 700; color: ${e.type === 'recette' ? 'var(--green)' : 'var(--red)'};">
                                        ${e.type === 'recette' ? '+' : '-'}${formatFCFA(e.montant)}
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            </div>
        `;
    } catch (error) {
        console.error('Error loading comptabilite:', error);
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
        
        container.innerHTML = `
            <div class="dashboard-card">
                <div class="dashboard-card-header">
                    <h3 class="dashboard-card-title">Lignes</h3>
                    <button class="btn btn-primary btn-sm" onclick="showAddLigneModal()">+ Ligne</button>
                </div>
                <div class="dashboard-card-body">
                    <table class="dashboard-table">
                        <thead>
                            <tr>
                                <th>Code</th>
                                <th>Trajet</th>
                                <th>Distance</th>
                                <th>Durée</th>
                                <th>Tarif</th>
                                <th>Taux remplissage</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${lignes.map(l => `
                                <tr>
                                    <td><span class="badge badge-navy">${l.code}</span></td>
                                    <td>${l.ville_depart} → ${l.ville_arrivee}</td>
                                    <td>${l.distance_km} km</td>
                                    <td>${l.duree_h}h</td>
                                    <td>${formatFCFA(l.tarif_base)}</td>
                                    <td>
                                        <div style="display: flex; align-items: center; gap: 8px;">
                                            <div class="progress-bar" style="width: 80px;">
                                                <div class="progress-bar-fill" style="width: ${l.taux_remplissage}%"></div>
                                            </div>
                                            <span style="font-size: 12px;">${l.taux_remplissage}%</span>
                                        </div>
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            </div>
        `;
    } catch (error) {
        console.error('Error loading lignes:', error);
    }
}

function showAddLigneModal() {
    showModal(`
        <form onsubmit="saveLigne(event)">
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Code</label>
                    <input type="text" class="form-input" name="code" placeholder="YDE-DBA" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Tarif base (FCFA)</label>
                    <input type="number" class="form-input" name="tarif_base" required>
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Ville départ</label>
                    <input type="text" class="form-input" name="ville_depart" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Ville arrivée</label>
                    <input type="text" class="form-input" name="ville_arrivee" required>
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Distance (km)</label>
                    <input type="number" class="form-input" name="distance_km" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Durée (heures)</label>
                    <input type="number" class="form-input" name="duree_h" step="0.5" required>
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
        
        container.innerHTML = `
            <div class="dashboard-card">
                <div class="dashboard-card-header">
                    <h3 class="dashboard-card-title">
                        Courriers
                        ${result.non_lus > 0 ? `<span class="badge badge-warning" style="margin-left: 8px;">${result.non_lus} non lu(s)</span>` : ''}
                    </h3>
                    <button class="btn btn-primary btn-sm" onclick="showAddCourrierModal()">+ Courrier</button>
                </div>
                <div class="dashboard-card-body">
                    <table class="dashboard-table">
                        <thead>
                            <tr>
                                <th>Objet</th>
                                <th>Expéditeur</th>
                                <th>Destinataire</th>
                                <th>Type</th>
                                <th>Priorité</th>
                                <th>Statut</th>
                                <th>Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${result.data.map(c => `
                                <tr onclick="showCourrierDetail(${c.id}, '${c.objet}', '${c.contenu}', '${c.expediteur}', '${c.destinataire}')" style="cursor: pointer; ${c.statut === 'non_lu' ? 'font-weight: 700;' : ''}">
                                    <td>${c.objet}</td>
                                    <td>${c.expediteur}</td>
                                    <td>${c.destinataire}</td>
                                    <td><span class="badge badge-info">${c.type}</span></td>
                                    <td>${c.priorite === 'urgente' ? '<span class="badge badge-danger">URGENTE</span>' : '<span class="badge badge-navy">Normale</span>'}</td>
                                    <td><span class="badge ${c.statut === 'non_lu' ? 'badge-danger' : 'badge-success'}">${c.statut}</span></td>
                                    <td>${formatDate(c.created_at)}</td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            </div>
        `;
    } catch (error) {
        console.error('Error loading courriers:', error);
    }
}

function showCourrierDetail(id, objet, contenu, expediteur, destinataire) {
    showModal(`
        <div>
            <h3 style="color: var(--navy); margin-bottom: 16px;">${objet}</h3>
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 16px;">
                <div>
                    <div style="font-size: 12px; color: var(--text-muted);">De</div>
                    <div style="font-weight: 600;">${expediteur}</div>
                </div>
                <div>
                    <div style="font-size: 12px; color: var(--text-muted);">À</div>
                    <div style="font-weight: 600;">${destinataire}</div>
                </div>
            </div>
            <div style="background: var(--bg); padding: 16px; border-radius: 8px; margin-bottom: 16px; white-space: pre-wrap;">${contenu}</div>
            <button class="btn btn-primary" onclick="marquerCourrierLu(${id})">Marquer comme lu</button>
        </div>
    `);
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
    
    container.innerHTML = `
        <div class="dashboard-card">
            <div class="dashboard-card-header">
                <h3 class="dashboard-card-title">Paramètres</h3>
            </div>
            <div class="dashboard-card-body">
                <div style="max-width: 600px;">
                    <h4 style="color: var(--navy); margin-bottom: 16px;">Mon compte</h4>
                    <div style="background: var(--bg); padding: 16px; border-radius: 8px; margin-bottom: 24px;">
                        <div style="margin-bottom: 12px;">
                            <div style="font-size: 12px; color: var(--text-muted);">Nom</div>
                            <div style="font-weight: 600;">${user.nom}</div>
                        </div>
                        <div style="margin-bottom: 12px;">
                            <div style="font-size: 12px; color: var(--text-muted);">Email</div>
                            <div style="font-weight: 600;">${user.email}</div>
                        </div>
                        <div style="margin-bottom: 12px;">
                            <div style="font-size: 12px; color: var(--text-muted);">Rôle</div>
                            <div><span class="badge badge-navy">${user.role}</span></div>
                        </div>
                        <div>
                            <div style="font-size: 12px; color: var(--text-muted);">Agence</div>
                            <div style="font-weight: 600;">${user.agence || 'Non assigné'}</div>
                        </div>
                    </div>
                    
                    <h4 style="color: var(--navy); margin-bottom: 16px;">Sécurité</h4>
                    <form onsubmit="changePassword(event)">
                        <div class="form-group">
                            <label class="form-label">Mot de passe actuel</label>
                            <input type="password" class="form-input" name="current_password" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Nouveau mot de passe</label>
                            <input type="password" class="form-input" name="new_password" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Confirmer le mot de passe</label>
                            <input type="password" class="form-input" name="confirm_password" required>
                        </div>
                        <button type="submit" class="btn btn-primary">Changer le mot de passe</button>
                    </form>
                </div>
            </div>
        </div>
    `;
}

async function changePassword(event) {
    event.preventDefault();
    showToast('Fonctionnalité de changement de mot de passe non implémentée en démonstration', 'info');
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
