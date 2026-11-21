/**
 * SmartWatt – Frontend Application Logic & Telemetry Dashboard
 * Handles live REST polling, appliance toggles, Chart.js visualizations,
 * and rule-based optimization alerts.
 */

// Global State
const state = {
    pollingIntervalMs: 3000,
    pollTimer: null,
    isSimulationActive: true,
    livePowerHistory: [], // Holds last 15 data points for line chart
    liveTimeLabels: [],
    charts: {
        power: null,
        roomShare: null,
        applianceLoad: null,
        dailyTrend: null
    }
};

// DOM Ready Init
document.addEventListener('DOMContentLoaded', () => {
    initCharts();
    setupEventListeners();
    fetchDashboardData();
    fetchAnalyticsData();
    startLivePolling();
});

/* ===================================================================
   Polling & Data Fetching
   =================================================================== */

function startLivePolling() {
    if (state.pollTimer) clearInterval(state.pollTimer);
    state.pollTimer = setInterval(() => {
        fetchDashboardData();
    }, state.pollingIntervalMs);
}

async function fetchDashboardData() {
    try {
        const response = await fetch('/api/dashboard');
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const result = await response.json();
        
        if (result.success && result.data) {
            updateDashboardUI(result.data);
            updateLivePowerHistory(result.data.totalPowerWatts);
        }
    } catch (err) {
        console.warn('Live telemetry sync paused or backend reconnecting...', err.message);
        document.getElementById('lastSyncedTime').innerHTML = `<i class="fa-solid fa-triangle-exclamation text-warning"></i> Reconnecting...`;
    }
}

async function fetchAnalyticsData() {
    try {
        const response = await fetch('/api/analytics');
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const result = await response.json();
        
        if (result.success && result.data) {
            updateAnalyticsCharts(result.data);
            updateHighConsumersWidget(result.data.highConsumingAppliances);
        }
    } catch (err) {
        console.error('Failed to load analytics data:', err);
    }
}

/* ===================================================================
   UI Updates: Summary, Rooms & Appliances
   =================================================================== */

function updateDashboardUI(data) {
    // 1. Header & System Status
    const statusPill = document.getElementById('systemStatusPill');
    const statusText = document.getElementById('systemStatusText');
    statusText.textContent = data.systemStatus.replace('_', ' ');
    statusPill.className = 'system-status-pill';
    
    if (data.systemStatus === 'CRITICAL') {
        statusPill.classList.add('status-critical');
    } else if (data.systemStatus === 'HIGH_CONSUMPTION') {
        statusPill.classList.add('status-warning');
    } else if (data.systemStatus === 'MODERATE') {
        statusPill.classList.add('status-moderate');
    }

    // Simulation status
    state.isSimulationActive = data.isSimulationActive;
    const simBadge = document.getElementById('simBadge');
    const simText = document.getElementById('simStatusText');
    const simBtnLabel = document.getElementById('simBtnLabel');
    if (data.isSimulationActive) {
        simBadge.style.opacity = '1';
        simText.textContent = 'SIMULATED TELEMETRY ACTIVE';
        simBtnLabel.textContent = 'Pause Sim';
    } else {
        simBadge.style.opacity = '0.5';
        simText.textContent = 'SIMULATION PAUSED';
        simBtnLabel.textContent = 'Resume Sim';
    }

    // Grid Voltage & Current
    document.getElementById('gridVoltageVal').textContent = `${data.simulatedGridVoltage.toFixed(1)} V`;
    document.getElementById('totalCurrentVal').textContent = `${data.totalCurrentAmp.toFixed(2)} A`;

    // 2. Global Metric Cards
    document.getElementById('totalPowerWatts').textContent = Math.round(data.totalPowerWatts);
    document.getElementById('thresholdNotice').textContent = `Max Safety Limit: ${data.powerThresholdWatts} W`;
    document.getElementById('totalEnergyKwh').textContent = data.totalEnergyKwh.toFixed(3);
    document.getElementById('estimatedBillInr').textContent = data.estimatedBillInr.toFixed(2);
    document.getElementById('currentTariffRate').textContent = data.tariffRatePerKwh.toFixed(2);
    document.getElementById('activeAppliancesCount').textContent = data.activeAppliancesCount;
    document.getElementById('totalAppliancesCount').textContent = data.totalAppliancesCount;

    // 3. Render Rooms & Appliances
    renderRooms(data.rooms);

    // 4. Alerts & Recommendations
    renderAlerts(data.recentAlerts);
    renderRecommendations(data.activeRecommendations);

    // 5. Last sync timestamp
    const now = new Date();
    document.getElementById('lastSyncedTime').innerHTML = `<i class="fa-regular fa-clock"></i> Synced: ${now.toLocaleTimeString()}`;
}

function renderRooms(rooms) {
    const container = document.getElementById('roomsGrid');
    if (!rooms || rooms.length === 0) {
        container.innerHTML = `<div class="empty-state">No monitored rooms configured.</div>`;
        return;
    }

    // Render 3 room cards
    container.innerHTML = rooms.map(room => `
        <div class="room-card" data-room-id="${room.id}">
            <div class="room-header">
                <div class="room-title-group">
                    <h3>${escapeHtml(room.name)}</h3>
                    <p>${escapeHtml(room.description || '')}</p>
                </div>
                <div class="room-load-badge">
                    <i class="fa-solid fa-bolt"></i> ${room.currentPowerWatts.toFixed(1)} W
                </div>
            </div>

            <div class="room-metrics-bar">
                <div class="room-metric-item">
                    <span class="metric-label">Energy</span>
                    <span class="metric-val">${room.totalEnergyKwh.toFixed(3)} kWh</span>
                </div>
                <div class="room-metric-item">
                    <span class="metric-label">Cost</span>
                    <span class="metric-val">₹ ${room.estimatedCostInr.toFixed(2)}</span>
                </div>
                <div class="room-metric-item">
                    <span class="metric-label">Active</span>
                    <span class="metric-val">${room.activeAppliancesCount} / ${room.totalAppliancesCount}</span>
                </div>
            </div>

            <div class="appliances-list">
                ${room.appliances.map(app => renderApplianceItem(app)).join('')}
            </div>
        </div>
    `).join('');

    // Attach toggle event listeners to all appliance switch inputs
    document.querySelectorAll('.appliance-toggle-input').forEach(input => {
        input.addEventListener('change', (e) => {
            const appId = e.target.getAttribute('data-appliance-id');
            const isChecked = e.target.checked;
            handleApplianceToggle(appId, isChecked);
        });
    });
}

function renderApplianceItem(app) {
    const isActive = app.isOn;
    const iconClass = getApplianceIcon(app.type);
    const spinClass = (app.type === 'FAN' && isActive) ? 'icon-spin' : '';

    return `
        <div class="appliance-item ${isActive ? 'is-active' : ''}" id="app-card-${app.id}">
            <div class="appliance-left">
                <div class="appliance-icon ${spinClass}">
                    <i class="fa-solid ${iconClass}"></i>
                </div>
                <div class="appliance-details">
                    <h4>${escapeHtml(app.name)}</h4>
                    <div class="appliance-telemetry">
                        <span><strong>${app.baseWattage}W</strong> base</span>
                        <span>&bull;</span>
                        <span><strong>${app.currentVoltage.toFixed(1)}V</strong></span>
                        <span>&bull;</span>
                        <span><strong>${app.currentAmp.toFixed(2)}A</strong></span>
                        <span>&bull;</span>
                        <span><strong>${app.activeDurationHours.toFixed(1)}h</strong></span>
                    </div>
                </div>
            </div>

            <div class="appliance-right">
                <span class="appliance-wattage-badge">
                    ${isActive ? `${app.currentWattage.toFixed(1)} W` : '0 W'}
                </span>
                <label class="switch">
                    <input type="checkbox" class="appliance-toggle-input" 
                           data-appliance-id="${app.id}" 
                           ${isActive ? 'checked' : ''}>
                    <span class="slider"></span>
                </label>
            </div>
        </div>
    `;
}

function getApplianceIcon(type) {
    switch (type) {
        case 'FAN': return 'fa-fan';
        case 'BULB': return 'fa-lightbulb';
        case 'AC': return 'fa-snowflake';
        case 'TV': return 'fa-tv';
        case 'REFRIGERATOR': return 'fa-kitchen-set';
        default: return 'fa-plug';
    }
}

/* ===================================================================
   Appliance Toggle Handler
   =================================================================== */

async function handleApplianceToggle(applianceId, newState) {
    try {
        // Instant visual feedback
        const card = document.getElementById(`app-card-${applianceId}`);
        if (card) {
            if (newState) card.classList.add('is-active');
            else card.classList.remove('is-active');
        }

        const response = await fetch(`/api/appliances/${applianceId}/toggle?state=${newState}`, {
            method: 'POST'
        });

        if (!response.ok) throw new Error('Toggle request failed');
        const resData = await response.json();

        // Refresh dashboard and charts to reflect immediate recalculation in DB
        await fetchDashboardData();
        await fetchAnalyticsData();
    } catch (err) {
        console.error('Error toggling appliance:', err);
        // Revert toggle on error
        await fetchDashboardData();
    }
}

/* ===================================================================
   Alerts & Recommendations Rendering
   =================================================================== */

function renderAlerts(alerts) {
    const container = document.getElementById('alertsList');
    if (!alerts || alerts.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fa-solid fa-circle-check text-success"></i>
                <p>All monitored sections within normal power limits.</p>
            </div>
        `;
        return;
    }

    container.innerHTML = alerts.map(alert => `
        <div class="alert-item severity-${alert.severity}">
            <div class="alert-header-row">
                <span class="alert-title">${escapeHtml(alert.title)}</span>
                <span class="alert-time">${formatTime(alert.timestamp)}</span>
            </div>
            <p class="alert-message">${escapeHtml(alert.message)}</p>
            <div class="alert-action-row">
                <button class="btn btn-xs btn-outline" onclick="resolveSingleAlert(${alert.id})">
                    <i class="fa-solid fa-check"></i> Dismiss
                </button>
            </div>
        </div>
    `).join('');
}

async function resolveSingleAlert(id) {
    try {
        await fetch(`/api/alerts/${id}/resolve`, { method: 'POST' });
        await fetchDashboardData();
    } catch (err) {
        console.error('Failed to resolve alert:', err);
    }
}

async function resolveAllAlerts() {
    try {
        await fetch('/api/alerts/resolve-all', { method: 'POST' });
        await fetchDashboardData();
    } catch (err) {
        console.error('Failed to resolve all alerts:', err);
    }
}

function renderRecommendations(recs) {
    const container = document.getElementById('recommendationsList');
    if (!recs || recs.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fa-solid fa-lightbulb text-accent"></i>
                <p>No active recommendations right now. System is running efficiently.</p>
            </div>
        `;
        return;
    }

    container.innerHTML = recs.map(rec => `
        <div class="rec-item">
            <div class="rec-title-row">
                <span class="rec-title">${escapeHtml(rec.title)}</span>
                ${rec.potentialMonthlySavingsInr > 0 ? `
                    <span class="rec-savings-pill">Save ₹${rec.potentialMonthlySavingsInr.toFixed(0)}/mo</span>
                ` : ''}
            </div>
            <p class="rec-desc">${escapeHtml(rec.description)}</p>
            <div class="rec-action-row">
                <span class="rec-action-badge"><i class="fa-solid fa-arrow-right"></i> ${escapeHtml(rec.actionSuggested || 'Review Usage')}</span>
                <button class="btn btn-xs btn-outline" onclick="dismissRec(${rec.id})">
                    Dismiss
                </button>
            </div>
        </div>
    `).join('');
}

async function dismissRec(id) {
    try {
        await fetch(`/api/recommendations/${id}/dismiss`, { method: 'POST' });
        await fetchDashboardData();
    } catch (err) {
        console.error('Failed to dismiss recommendation:', err);
    }
}

async function regenerateRecommendations() {
    try {
        await fetch('/api/recommendations/generate', { method: 'POST' });
        await fetchDashboardData();
    } catch (err) {
        console.error('Failed to regenerate recommendations:', err);
    }
}

function updateHighConsumersWidget(highConsumers) {
    const container = document.getElementById('highConsumersList');
    if (!highConsumers || highConsumers.length === 0) {
        container.innerHTML = `<div class="empty-state"><p>No high-wattage loads currently active.</p></div>`;
        return;
    }

    container.innerHTML = highConsumers.map(app => `
        <div class="consumer-item">
            <div>
                <div class="consumer-name">${escapeHtml(app.applianceName)}</div>
                <div class="consumer-room">${escapeHtml(app.roomName)} &bull; ${app.type}</div>
            </div>
            <div class="consumer-wattage">${app.currentWattage} W</div>
        </div>
    `).join('');
}

/* ===================================================================
   Chart.js Visualizations
   =================================================================== */

function initCharts() {
    // 1. Real-Time Power Trend Line Chart
    const powerCtx = document.getElementById('realtimePowerChart').getContext('2d');
    state.charts.power = new Chart(powerCtx, {
        type: 'line',
        data: {
            labels: [],
            datasets: [{
                label: 'Active Power (Watts)',
                data: [],
                borderColor: '#10b981',
                backgroundColor: 'rgba(16, 185, 129, 0.12)',
                borderWidth: 2.5,
                fill: true,
                tension: 0.35,
                pointRadius: 2,
                pointHoverRadius: 5
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false }
            },
            scales: {
                x: {
                    grid: { color: 'rgba(255, 255, 255, 0.05)' },
                    ticks: { color: '#64748b', font: { size: 10 } }
                },
                y: {
                    grid: { color: 'rgba(255, 255, 255, 0.05)' },
                    ticks: { color: '#64748b', font: { size: 10 } },
                    beginAtZero: true
                }
            }
        }
    });

    // 2. Room-Wise Share Doughnut Chart
    const roomCtx = document.getElementById('roomShareChart').getContext('2d');
    state.charts.roomShare = new Chart(roomCtx, {
        type: 'doughnut',
        data: {
            labels: ['Room 1', 'Room 2', 'Room 3'],
            datasets: [{
                data: [33, 33, 34],
                backgroundColor: ['#10b981', '#06b6d4', '#3b82f6'],
                borderColor: '#131b2e',
                borderWidth: 3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: { color: '#94a3b8', font: { size: 11 } }
                }
            },
            cutout: '68%'
        }
    });

    // 3. Appliance Wattage Comparison Horizontal Bar Chart
    const appCtx = document.getElementById('applianceLoadChart').getContext('2d');
    state.charts.applianceLoad = new Chart(appCtx, {
        type: 'bar',
        data: {
            labels: [],
            datasets: [
                {
                    label: 'Current Wattage',
                    data: [],
                    backgroundColor: '#10b981',
                    borderRadius: 4
                },
                {
                    label: 'Base Rated Wattage',
                    data: [],
                    backgroundColor: 'rgba(148, 163, 184, 0.25)',
                    borderRadius: 4
                }
            ]
        },
        options: {
            indexAxis: 'y',
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'top',
                    labels: { color: '#94a3b8', font: { size: 11 } }
                }
            },
            scales: {
                x: {
                    grid: { color: 'rgba(255, 255, 255, 0.05)' },
                    ticks: { color: '#64748b', font: { size: 10 } }
                },
                y: {
                    grid: { display: false },
                    ticks: { color: '#94a3b8', font: { size: 11 } }
                }
            }
        }
    });

    // 4. 7-Day Daily Consumption Bar Chart
    const dailyCtx = document.getElementById('dailyTrendChart').getContext('2d');
    state.charts.dailyTrend = new Chart(dailyCtx, {
        type: 'bar',
        data: {
            labels: [],
            datasets: [{
                label: 'Energy (kWh)',
                data: [],
                backgroundColor: 'rgba(6, 182, 212, 0.75)',
                borderRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false }
            },
            scales: {
                x: {
                    grid: { display: false },
                    ticks: { color: '#64748b', font: { size: 10 } }
                },
                y: {
                    grid: { color: 'rgba(255, 255, 255, 0.05)' },
                    ticks: { color: '#64748b', font: { size: 10 } },
                    beginAtZero: true
                }
            }
        }
    });
}

function updateLivePowerHistory(currentWatts) {
    const now = new Date();
    const timeLabel = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });

    state.livePowerHistory.push(currentWatts);
    state.liveTimeLabels.push(timeLabel);

    if (state.livePowerHistory.length > 15) {
        state.livePowerHistory.shift();
        state.liveTimeLabels.shift();
    }

    if (state.charts.power) {
        state.charts.power.data.labels = [...state.liveTimeLabels];
        state.charts.power.data.datasets[0].data = [...state.livePowerHistory];
        state.charts.power.update('none'); // Update without full animation for smooth real-time stream
    }
}

function updateAnalyticsCharts(analytics) {
    // 1. Room share chart
    if (state.charts.roomShare && analytics.roomConsumptionList) {
        const labels = analytics.roomConsumptionList.map(r => r.roomName);
        const data = analytics.roomConsumptionList.map(r => r.totalKwh);
        state.charts.roomShare.data.labels = labels;
        state.charts.roomShare.data.datasets[0].data = data;
        state.charts.roomShare.update();
    }

    // 2. Appliance comparison chart
    if (state.charts.applianceLoad && analytics.applianceConsumptionList) {
        // Sort by current wattage descending
        const sorted = [...analytics.applianceConsumptionList].sort((a, b) => b.baseWattage - a.baseWattage);
        state.charts.applianceLoad.data.labels = sorted.map(a => a.applianceName);
        state.charts.applianceLoad.data.datasets[0].data = sorted.map(a => a.currentWattage);
        state.charts.applianceLoad.data.datasets[1].data = sorted.map(a => a.baseWattage);
        state.charts.applianceLoad.update();
    }

    // 3. Daily trends chart
    if (state.charts.dailyTrend && analytics.dailyTrends) {
        state.charts.dailyTrend.data.labels = analytics.dailyTrends.map(d => d.dateLabel);
        state.charts.dailyTrend.data.datasets[0].data = analytics.dailyTrends.map(d => d.kwh);
        state.charts.dailyTrend.update();
    }
}

/* ===================================================================
   Settings Modal & Event Handlers
   =================================================================== */

function setupEventListeners() {
    // Simulation Toggle
    document.getElementById('btnToggleSim').addEventListener('click', async () => {
        try {
            const response = await fetch('/api/simulation/toggle', { method: 'POST' });
            const result = await response.json();
            if (result.success) {
                await fetchDashboardData();
            }
        } catch (err) {
            console.error('Error toggling simulation:', err);
        }
    });

    // Refresh Button
    document.getElementById('btnRefresh').addEventListener('click', async () => {
        await fetchDashboardData();
        await fetchAnalyticsData();
    });

    // Resolve all alerts
    document.getElementById('btnResolveAllAlerts').addEventListener('click', resolveAllAlerts);

    // Regenerate recommendations
    document.getElementById('btnRegenerateRecs').addEventListener('click', regenerateRecommendations);

    // Settings Modal Open/Close
    const modal = document.getElementById('settingsModal');
    document.getElementById('btnOpenSettings').addEventListener('click', async () => {
        try {
            const res = await fetch('/api/settings');
            const result = await res.json();
            if (result.success && result.data) {
                document.getElementById('inputTariffRate').value = result.data.tariffRatePerKwh;
                document.getElementById('inputMaxPower').value = result.data.maxPowerThresholdWatts;
                document.getElementById('inputRoomPower').value = result.data.roomPowerThresholdWatts;
                document.getElementById('inputLongRunning').value = result.data.longRunningHoursThreshold;
            }
            modal.classList.add('active');
        } catch (err) {
            console.error('Failed to load settings:', err);
        }
    });

    document.getElementById('btnCloseSettings').addEventListener('click', () => {
        modal.classList.remove('active');
    });

    document.getElementById('btnCancelSettings').addEventListener('click', () => {
        modal.classList.remove('active');
    });

    // Save Settings
    document.getElementById('btnSaveSettings').addEventListener('click', async () => {
        const payload = {
            tariffRatePerKwh: parseFloat(document.getElementById('inputTariffRate').value),
            maxPowerThresholdWatts: parseFloat(document.getElementById('inputMaxPower').value),
            roomPowerThresholdWatts: parseFloat(document.getElementById('inputRoomPower').value),
            longRunningHoursThreshold: parseFloat(document.getElementById('inputLongRunning').value)
        };

        try {
            const response = await fetch('/api/settings', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                modal.classList.remove('active');
                await fetchDashboardData();
                await fetchAnalyticsData();
            }
        } catch (err) {
            console.error('Failed to update settings:', err);
        }
    });
}

// Utility: Format ISO timestamp to HH:mm
function formatTime(isoString) {
    if (!isoString) return '';
    const date = new Date(isoString);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

// Utility: Prevent XSS
function escapeHtml(text) {
    if (!text) return '';
    return text.replace(/[&<>"']/g, function(m) {
        return {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        }[m];
    });
}
