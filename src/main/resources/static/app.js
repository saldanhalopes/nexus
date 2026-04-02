// Nexus Application Module
const API_BASE = '/api';

const App = {
    user: null,
    token: localStorage.getItem('nexus_token'),
    currentPage: 'dashboard',

    init() {
        this.cacheDOM();
        this.createToastContainer();
        this.bindEvents();
        this.checkAuth();
        this.renderCurrentPage();
        
        // Dynamic elements
        document.getElementById('current-date').innerText = new Date().toLocaleDateString('pt-BR');
    },

    createToastContainer() {
        const container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
        this.toastContainer = container;
    },

    notify(message, type = 'success') {
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        const icon = type === 'success' ? 'check-circle' : (type === 'error' ? 'exclamation-circle' : 'exclamation-triangle');
        toast.innerHTML = `<i class="fas fa-${icon}"></i> <span>${message}</span>`;
        this.toastContainer.appendChild(toast);
        
        setTimeout(() => toast.classList.add('show'), 10);
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => toast.remove(), 300);
        }, 4000);
    },

    cacheDOM() {
        this.loginScreen = document.getElementById('login-screen');
        this.mainScreen = document.getElementById('main-screen');
        this.loginForm = document.getElementById('login-form');
        this.loginError = document.getElementById('login-error');
        this.logoutBtn = document.getElementById('logout-btn');
        this.navItems = document.querySelectorAll('.nav-item');
        this.pageContent = document.getElementById('page-content');
        this.pageTitle = document.getElementById('page-title');
        this.loadingOverlay = document.getElementById('loading-overlay');
        
        // Chat
        this.chatToggle = document.getElementById('chat-toggle');
        this.chatWindow = document.getElementById('chat-window');
        this.chatClose = document.getElementById('chat-close');
        this.chatForm = document.getElementById('chat-form');
        this.chatInput = document.getElementById('chat-input');
        this.chatMessages = document.getElementById('chat-messages');
    },

    bindEvents() {
        this.loginForm.addEventListener('submit', (e) => this.handleLogin(e));
        this.logoutBtn.addEventListener('click', () => this.handleLogout());
        
        this.navItems.forEach(item => {
            item.addEventListener('click', (e) => {
                e.preventDefault();
                const page = item.getAttribute('data-page');
                this.navigate(page);
            });
        });

        // Chat events
        this.chatToggle.addEventListener('click', () => this.chatWindow.classList.toggle('hidden'));
        this.chatClose.addEventListener('click', () => this.chatWindow.classList.add('hidden'));
        this.chatForm.addEventListener('submit', (e) => this.handleChatMessage(e));

        // Window hash change
        window.addEventListener('hashchange', () => {
            const hash = window.location.hash.replace('#', '') || 'dashboard';
            this.navigate(hash, false);
        });
    },

    checkAuth() {
        if (!this.token) {
            this.showLogin();
        } else {
            this.showMain();
            // Try to load user profile from token or API
            this.fetchUserProfile();
        }
    },

    showLogin() {
        this.loginScreen.classList.add('active');
        this.mainScreen.classList.remove('active');
    },

    showMain() {
        this.loginScreen.classList.remove('active');
        this.mainScreen.classList.add('active');
    },

    async handleLogin(e) {
        e.preventDefault();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        
        this.showLoading();
        this.loginError.innerText = '';

        try {
            const response = await fetch(`${API_BASE}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: username, senha: password })
            });

            const data = await response.json();

            if (response.ok) {
                this.token = data.token;
                localStorage.setItem('nexus_token', this.token);
                this.user = data.usuario; // Corrigido de data.user para data.usuario
                this.updateUserInfo();
                this.showMain();
                this.navigate('dashboard');
            } else {
                this.loginError.innerText = data.message || 'Erro ao realizar login';
            }
        } catch (err) {
            this.loginError.innerText = 'Erro de conexão com o servidor';
        } finally {
            this.hideLoading();
        }
    },

    handleLogout() {
        localStorage.removeItem('nexus_token');
        this.token = null;
        this.user = null;
        this.showLogin();
    },

    updateUserInfo() {
        if (this.user) {
            document.getElementById('user-name').innerText = this.user.nomeCompleto || 'Usuário';
            document.getElementById('user-role').innerText = this.user.nivelAcesso || 'Colaborador';
            const initials = this.user.nomeCompleto ? this.user.nomeCompleto.split(' ').map(n => n[0]).join('').substring(0,2).toUpperCase() : 'US';
            document.getElementById('user-initials').innerText = initials;
        }
    },

    async fetchUserProfile() {
        // Mocking user profile for now since we don't have a /me endpoint
        // In real app, we would fetch from API
        try {
            // Decodificando o token JWT simples (apenas para exibição)
            const payload = JSON.parse(atob(this.token.split('.')[1]));
            this.user = {
                nomeCompleto: payload.nome || 'Usuário Conectado',
                nivelAcesso: payload.role || 'ADMIN'
            };
            this.updateUserInfo();
        } catch (e) {
            this.handleLogout();
        }
    },

    navigate(page, updateHash = true) {
        this.currentPage = page;
        if (updateHash) window.location.hash = page;

        this.navItems.forEach(item => {
            if (item.getAttribute('data-page') === page) {
                item.classList.add('active');
            } else {
                item.classList.remove('active');
            }
        });

        const titles = {
            'dashboard': 'Dashboard',
            'utilizadores': 'Gestão de Utilizadores',
            'treinamentos': 'Gestão de Treinamentos',
            'matriz': 'Matriz de Competências',
            'auditoria': 'Logs de Auditoria',
            'configuracoes': 'Configurações do Sistema'
        };

        this.pageTitle.innerText = titles[page] || 'Página';
        this.renderCurrentPage();
    },

    renderCurrentPage() {
        this.pageContent.innerHTML = '';
        this.pageContent.classList.remove('page-fade-in');
        void this.pageContent.offsetWidth; // Trigger reflow
        this.pageContent.classList.add('page-fade-in');
        
        switch(this.currentPage) {
            case 'dashboard': this.renderDashboard(); break;
            case 'utilizadores': this.renderUtilizadores(); break;
            case 'matriz': this.renderMatriz(); break;
            case 'treinamentos': this.renderTreinamentos(); break;
            case 'auditoria': this.renderAuditoria(); break;
            case 'configuracoes': this.renderConfiguracoes(); break;
            default: 
                this.pageContent.innerHTML = `<div class="card"><h2>Em breve: ${this.currentPage}</h2></div>`;
        }
    },

    renderSkeleton(type) {
        if (type === 'table') {
            return `
                <div class="card">
                    <div class="skeleton" style="height: 30px; width: 200px; margin-bottom: 24px;"></div>
                    <div style="display: flex; flex-direction: column; gap: 12px;">
                        ${Array(5).fill(`<div class="skeleton" style="height: 50px; width: 100%;"></div>`).join('')}
                    </div>
                </div>
            `;
        }
        if (type === 'cards') {
            return `
                <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px;">
                    ${Array(6).fill(`<div class="card skeleton" style="height: 180px;"></div>`).join('')}
                </div>
            `;
        }
    },

    async renderConfiguracoes() {
        this.pageContent.innerHTML = `<div class="card"><h3>Carregando configurações...</h3></div>`;
        
        try {
            const [depRes, cargoRes, compRes] = await Promise.all([
                this.apiFetch('/departamentos'),
                this.apiFetch('/cargos'),
                this.apiFetch('/competencias')
            ]);
            
            const deps = await depRes.json();
            const cargos = await cargoRes.json();
            const comps = await compRes.json();

            let html = `
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 24px;">
                    <div class="card">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
                            <h3>Departamentos</h3>
                            <button class="btn-icon"><i class="fas fa-plus"></i></button>
                        </div>
                        <ul style="list-style: none; display: flex; flex-direction: column; gap: 8px;">
                            ${deps.map(d => `<li style="padding: 10px; background: rgba(255,255,255,0.03); border-radius: 8px; display: flex; justify-content: space-between;">
                                <span>${d.nome}</span>
                                <span style="font-size: 0.75rem; color: var(--text-muted);">${d.id.substring(0,8)}</span>
                            </li>`).join('')}
                        </ul>
                    </div>
                    <div class="card">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
                            <h3>Cargos / Funções</h3>
                            <button class="btn-icon"><i class="fas fa-plus"></i></button>
                        </div>
                        <ul style="list-style: none; display: flex; flex-direction: column; gap: 8px;">
                            ${cargos.map(c => `<li style="padding: 10px; background: rgba(255,255,255,0.03); border-radius: 8px; display: flex; justify-content: space-between;">
                                <span>${c.nome}</span>
                                <span style="font-size: 0.75rem; color: var(--text-muted);">${c.departamento?.nome || 'N/A'}</span>
                            </li>`).join('')}
                        </ul>
                    </div>
                    <div class="card">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
                            <h3>Competências</h3>
                            <button class="btn-icon"><i class="fas fa-plus"></i></button>
                        </div>
                        <ul style="list-style: none; display: flex; flex-direction: column; gap: 8px;">
                            ${comps.map(c => `<li style="padding: 10px; background: rgba(255,255,255,0.03); border-radius: 8px; display: flex; justify-content: space-between;">
                                <span>${c.nome}</span>
                                <span style="font-size: 0.75rem; color: var(--text-muted);">${c.tipo || 'Geral'}</span>
                            </li>`).join('')}
                        </ul>
                    </div>
                </div>
                
                <div class="card" style="margin-top: 24px;">
                    <h3>Importação de Dados</h3>
                    <p style="color: var(--text-muted); margin-bottom: 16px;">Importe utilizadores em massa via arquivo CSV.</p>
                    <div style="border: 2px dashed var(--border); border-radius: 12px; padding: 40px; text-align: center; cursor: pointer;" onclick="document.getElementById('file-upload').click()">
                        <i class="fas fa-cloud-upload-alt" style="font-size: 2rem; color: var(--primary); margin-bottom: 12px;"></i>
                        <p>Clique ou arraste o arquivo CSV aqui</p>
                        <input type="file" id="file-upload" style="display: none;" onchange="App.handleImport(event)">
                    </div>
                </div>
            `;
            this.pageContent.innerHTML = html;
        } catch (err) {
            this.pageContent.innerHTML = `<div class="card"><h3 style="color: var(--danger)">Erro ao carregar configurações</h3></div>`;
        }
    },

    async handleImport(e) {
        const file = e.target.files[0];
        if (!file) return;

        this.showLoading();
        const formData = new FormData();
        formData.append('file', file);

        try {
            const response = await fetch(`${API_BASE}/import/csv/utilizadores`, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${this.token}` },
                body: formData
            });

            const result = await response.json();
            if (response.ok) {
                alert(`Importação concluída! Sucesso: ${result.sucesso}, Falhas: ${result.falhas}`);
                this.navigate('utilizadores');
            } else {
                alert('Erro na importação: ' + (result.message || 'Erro desconhecido'));
            }
        } catch (err) {
            alert('Erro de conexão ao importar dados.');
        } finally {
            this.hideLoading();
        }
    },

    // Mock/Shell modules - Will be expanded with real API calls
    renderDashboard() {
        this.pageContent.innerHTML = `
            <div class="stats-grid" style="display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 24px; margin-bottom: 32px;">
                <div class="card stat-card">
                    <div style="color: var(--text-muted); font-size: 0.875rem;">Total de Usuários</div>
                    <div style="font-size: 2rem; font-weight: 700; margin: 8px 0;">1,248</div>
                    <div style="color: var(--success); font-size: 0.75rem;"><i class="fas fa-arrow-up"></i> 12% desde o mês passado</div>
                </div>
                <div class="card stat-card">
                    <div style="color: var(--text-muted); font-size: 0.875rem;">Treinamentos Concluídos</div>
                    <div style="font-size: 2rem; font-weight: 700; margin: 8px 0;">85%</div>
                    <div style="color: var(--success); font-size: 0.75rem;"><i class="fas fa-arrow-up"></i> 5% este mês</div>
                </div>
                <div class="card stat-card">
                    <div style="color: var(--text-muted); font-size: 0.875rem;">Treinamentos Pendentes</div>
                    <div style="font-size: 2rem; font-weight: 700; margin: 8px 0;">156</div>
                    <div style="color: var(--danger); font-size: 0.75rem;"><i class="fas fa-exclamation-triangle"></i> Atenção necessária</div>
                </div>
                <div class="card stat-card">
                    <div style="color: var(--text-muted); font-size: 0.875rem;">Conformidade GAMP 5</div>
                    <div style="font-size: 2rem; font-weight: 700; margin: 8px 0;">Aprovado</div>
                    <div style="color: var(--primary); font-size: 0.75rem;"><i class="fas fa-shield-alt"></i> Auditoria em dia</div>
                </div>
            </div>
            
            <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 24px;">
                <div class="card">
                    <h3>Atividade Recente</h3>
                    <div style="height: 300px; display: flex; align-items: center; justify-content: center; color: var(--text-muted);">
                        <canvas id="mainChart"></canvas>
                    </div>
                </div>
                <div class="card">
                    <h3>Ações Rápidas</h3>
                    <div style="display: flex; flex-direction: column; gap: 12px; margin-top: 16px;">
                        <button class="btn-primary" onclick="App.navigate('treinamentos')">Novo Treinamento</button>
                        <button class="btn-primary" style="background: var(--glass-bg); color: white;" onclick="App.navigate('utilizadores')">Adicionar Usuário</button>
                    </div>
                </div>
            </div>
        `;
        
        this.initChart();
    },

    initChart() {
        const ctx = document.getElementById('mainChart');
        if (!ctx) return;
        
        new Chart(ctx, {
            type: 'line',
            data: {
                labels: ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun'],
                datasets: [{
                    label: 'Conclusão de Treinamentos',
                    data: [65, 59, 80, 81, 56, 95],
                    fill: true,
                    borderColor: '#6366f1',
                    backgroundColor: 'rgba(99, 102, 241, 0.1)',
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                plugins: { legend: { display: false } },
                scales: { 
                    y: { grid: { color: 'rgba(255,255,255,0.05)' }, ticks: { color: '#94a3b8' } },
                    x: { grid: { display: false }, ticks: { color: '#94a3b8' } }
                }
            }
        });
    },

    async renderUtilizadores() {
        this.pageContent.innerHTML = `<div class="card"><h3>Carregando usuários...</h3></div>`;
        
        try {
            const response = await this.apiFetch('/utilizadores');
            const data = await response.json();
            
            const users = data.content || [];
            
            let html = `
                <div class="card">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
                        <h3>Lista de Utilizadores</h3>
                        <button class="btn-primary" style="width: auto; padding: 10px 20px;">+ Novo Usuário</button>
                    </div>
                    <table style="width: 100%; border-collapse: collapse;">
                        <thead>
                            <tr style="text-align: left; border-bottom: 1px solid var(--border); color: var(--text-muted); font-size: 0.875rem;">
                                <th style="padding: 12px;">Nome</th>
                                <th style="padding: 12px;">Email</th>
                                <th style="padding: 12px;">Departamento</th>
                                <th style="padding: 12px;">Status</th>
                                <th style="padding: 12px;">Ações</th>
                            </tr>
                        </thead>
                        <tbody>
            `;
            
            users.forEach(user => {
                html += `
                    <tr style="border-bottom: 1px solid var(--border);">
                        <td style="padding: 12px;">${user.nomeCompleto}</td>
                        <td style="padding: 12px;">${user.email}</td>
                        <td style="padding: 12px;">${user.departamentoNome || '-'}</td>
                        <td style="padding: 12px;">
                            <span style="padding: 4px 8px; border-radius: 4px; font-size: 0.75rem; background: ${user.status === 'ATIVO' ? 'rgba(16, 185, 129, 0.1)' : 'rgba(239, 68, 68, 0.1)'}; color: ${user.status === 'ATIVO' ? 'var(--success)' : 'var(--danger)'};">
                                ${user.status}
                            </span>
                        </td>
                        <td style="padding: 12px;">
                            <button class="btn-icon" style="font-size: 0.9rem;"><i class="fas fa-edit"></i></button>
                            <button class="btn-icon" style="font-size: 0.9rem; color: var(--danger);"><i class="fas fa-trash"></i></button>
                        </td>
                    </tr>
                `;
            });
            
            html += `</tbody></table></div>`;
            this.pageContent.innerHTML = html;
        } catch (err) {
            this.pageContent.innerHTML = `<div class="card"><h3 style="color: var(--danger)">Erro ao carregar usuários</h3></div>`;
        }
    },

    async renderTreinamentos() {
        this.pageContent.innerHTML = `<div class="card"><h3>Carregando treinamentos...</h3></div>`;
        
        try {
            const response = await this.apiFetch('/treinamentos/all');
            const trainings = await response.json();
            
            let html = `
                <div class="card">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
                        <h3>Lista de Treinamentos</h3>
                        <button class="btn-primary" style="width: auto; padding: 10px 20px;">+ Novo Treinamento</button>
                    </div>
                    <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px;">
            `;
            
            trainings.forEach(t => {
                html += `
                    <div class="card" style="margin-bottom: 0; background: rgba(15, 23, 42, 0.3);">
                        <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px;">
                            <span style="background: var(--primary); font-size: 0.7rem; padding: 2px 8px; border-radius: 4px;">Treinamento</span>
                            <span style="color: var(--text-muted); font-size: 0.75rem;">${new Date(t.createdAt).toLocaleDateString()}</span>
                        </div>
                        <h4 style="margin-bottom: 8px;">${t.titulo}</h4>
                        <p style="color: var(--text-muted); font-size: 0.8rem; margin-bottom: 16px;">${t.descricao || 'Sem descrição.'}</p>
                        <div style="display: flex; align-items: center; gap: 8px; font-size: 0.75rem;">
                            <i class="fas fa-clock"></i> <span>2h duração</span>
                            <i class="fas fa-tag" style="margin-left: 12px;"></i> <span>${t.categoria || 'Geral'}</span>
                        </div>
                    </div>
                `;
            });
            
            html += `</div></div>`;
            this.pageContent.innerHTML = html;
        } catch (err) {
            this.pageContent.innerHTML = `<div class="card"><h3 style="color: var(--danger)">Erro ao carregar treinamentos</h3></div>`;
        }
    },

    async renderAuditoria() {
        this.pageContent.innerHTML = `<div class="card"><h3>Carregando auditoria...</h3></div>`;
        
        try {
            const response = await this.apiFetch('/audit');
            const data = await response.json();
            const logs = data.content || [];
            
            let html = `
                <div class="card">
                    <h3>Trilha de Auditoria</h3>
                    <div style="overflow-x: auto; margin-top: 20px;">
                        <table style="width: 100%; border-collapse: collapse; font-size: 0.85rem;">
                            <thead>
                                <tr style="text-align: left; border-bottom: 1px solid var(--border); color: var(--text-muted);">
                                    <th style="padding: 12px;">Data/Hora</th>
                                    <th style="padding: 12px;">Usuário</th>
                                    <th style="padding: 12px;">Ação</th>
                                    <th style="padding: 12px;">Tabela</th>
                                    <th style="padding: 12px;">ID Registro</th>
                                </tr>
                            </thead>
                            <tbody>
                `;
            
            logs.forEach(log => {
                html += `
                    <tr style="border-bottom: 1px solid var(--border);">
                        <td style="padding: 12px;">${new Date(log.dataHora).toLocaleString()}</td>
                        <td style="padding: 12px;">${log.utilizadorNome || 'Sistema'}</td>
                        <td style="padding: 12px;">
                            <span style="color: ${this.getActionColor(log.acao)}">${log.acao}</span>
                        </td>
                        <td style="padding: 12px;">${log.tabelaAfetada}</td>
                        <td style="padding: 12px; font-family: monospace;">${log.registroId.substring(0,8)}...</td>
                    </tr>
                `;
            });
            
            html += `</tbody></table></div></div>`;
            this.pageContent.innerHTML = html;
        } catch (err) {
            this.pageContent.innerHTML = `<div class="card"><h3 style="color: var(--danger)">Erro ao carregar auditoria</h3></div>`;
        }
    },

    getActionColor(acao) {
        if (acao.includes('INSERT')) return '#10b981';
        if (acao.includes('UPDATE')) return '#f59e0b';
        if (acao.includes('DELETE')) return '#ef4444';
        return 'inherit';
    },

    async handleChatMessage(e) {
        e.preventDefault();
        const msg = this.chatInput.value.trim();
        if (!msg) return;

        this.appendMessage('user', msg);
        this.chatInput.value = '';
        
        try {
            const response = await this.apiFetch('/chatbot/send', {
                method: 'POST',
                body: JSON.stringify({ message: msg })
            });
            const data = await response.json();
            this.appendMessage('system', data.message || data.response || 'Desculpe, tive um problema ao processar sua dúvida.');
        } catch (err) {
            this.appendMessage('system', 'Erro de conexão com a IA.');
        }
    },

    appendMessage(role, text) {
        const div = document.createElement('div');
        div.className = `message ${role}`;
        div.innerText = text;
        this.chatMessages.appendChild(div);
        this.chatMessages.scrollTop = this.chatMessages.scrollHeight;
    },

    // Utilities
    async apiFetch(url, options = {}) {
        const headers = {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${this.token}`,
            ...options.headers
        };
        
        return fetch(`${API_BASE}${url}`, { ...options, headers });
    },

    async renderMatriz() {
        this.pageContent.innerHTML = this.renderSkeleton('table');
        
        try {
            const [depRes, matRes] = await Promise.all([
                this.apiFetch('/departamentos'),
                this.apiFetch('/matrizes')
            ]);
            
            const deps = await depRes.json();
            const matrices = await matRes.json();

            // Transform data for Heatmap
            const heatmapData = this.transformMatrixToHeatmap(deps, matrices);

            let html = `
                <div class="card">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
                        <div>
                            <h3>Matriz de Competências (Heatmap)</h3>
                            <p style="color: var(--text-muted); font-size: 0.8rem;">Visualização de conformidade por departamento e nível de criticidade.</p>
                        </div>
                        <div class="compliance-legend" style="display: flex; gap: 12px; font-size: 0.75rem;">
                            <span class="legend-item"><span class="box" style="background: var(--danger)"></span> CRÍTICO (< 25%)</span>
                            <span class="legend-item"><span class="box" style="background: var(--warning)"></span> EM ALERTA (25-75%)</span>
                            <span class="legend-item"><span class="box" style="background: var(--success)"></span> EM CONFORMIDADE (> 75%)</span>
                        </div>
                    </div>

                    <div class="heatmap-container" style="overflow-x: auto;">
                        <table class="heatmap-table" style="width: 100%; border-collapse: separate; border-spacing: 4px;">
                            <thead>
                                <tr>
                                    <th style="padding: 12px; text-align: left; position: sticky; left: 0; z-index: 2; background: var(--glass-bg);">Departamento</th>
                                    ${heatmapData.headers.map(h => `<th class="heatmap-header">${h}</th>`).join('')}
                                </tr>
                            </thead>
                            <tbody>
                                ${heatmapData.rows.map(row => `
                                    <tr>
                                        <td style="padding: 12px; font-weight: 600; position: sticky; left: 0; z-index: 2; background: var(--glass-bg); border-radius: 8px;">${row.label}</td>
                                        ${row.cells.map(cell => `
                                            <td class="heatmap-cell" 
                                                style="background: ${this.getComplianceColor(cell.value)};"
                                                onclick="App.showComplianceDetail('${row.id}', '${cell.id}', ${cell.value})"
                                                title="${cell.value}% em conformidade">
                                                <span class="cell-value">${cell.value}%</span>
                                            </td>
                                        `).join('')}
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                </div>

                <div id="compliance-detail" class="card hidden" style="margin-top: 24px;">
                    <!-- Detail content will be injected here -->
                </div>
            `;
            this.pageContent.innerHTML = html;
        } catch (err) {
            this.pageContent.innerHTML = `<div class="card"><h3 style="color: var(--danger)">Erro ao carregar matriz de conformidade</h3></div>`;
        }
    },

    transformMatrixToHeatmap(deps, matrices) {
        // Mocking structure for now
        // In real app, roles would be fetch from API too
        const headers = ['Produção', 'Qualidade', 'Manutenção', 'P&D', 'QA', 'Logística'];
        return {
            headers,
            rows: deps.map(d => ({
                id: d.id,
                label: d.nome,
                cells: headers.map((h, i) => ({
                    id: `role_${i}`,
                    value: Math.floor(Math.random() * 100) // Mock compliance score
                }))
            }))
        };
    },

    getComplianceColor(value) {
        if (value < 25) return 'rgba(239, 68, 68, 0.4)'; // Red
        if (value < 75) return 'rgba(245, 158, 11, 0.4)'; // Amber
        return 'rgba(16, 185, 129, 0.4)'; // Green
    },

    showComplianceDetail(depId, roleId, value) {
        const detail = document.getElementById('compliance-detail');
        detail.classList.remove('hidden');
        detail.innerHTML = `
            <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                <div>
                    <h3>Análise de Gatilho: ${depId} / ${roleId}</h3>
                    <p style="color: var(--text-muted);">Conformidade atual de ${value}% detectada.</p>
                </div>
                <button class="btn-primary" onclick="App.showSignatureModal('UPDATE', 'Ajuste de matriz para conformidade')">
                    <i class="fas fa-file-signature"></i> Assinar Melhoria
                </button>
            </div>
            <div style="margin-top: 16px; display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px;">
                <div class="card" style="background: rgba(255,255,255,0.03); margin: 0;">
                    <div style="font-size: 0.75rem; color: var(--text-muted);">Gaps de Treinamento</div>
                    <div style="font-size: 1.25rem; font-weight: 600; color: var(--danger);">4 críticos</div>
                </div>
                <div class="card" style="background: rgba(255,255,255,0.03); margin: 0;">
                    <div style="font-size: 0.75rem; color: var(--text-muted);">Impacto Estimado</div>
                    <div style="font-size: 1.25rem; font-weight: 600; color: var(--warning);">Risco Alto</div>
                </div>
                <div class="card" style="background: rgba(255,255,255,0.03); margin: 0;">
                    <div style="font-size: 0.75rem; color: var(--text-muted);">Horas Requeridas (HH)</div>
                    <div style="font-size: 1.25rem; font-weight: 600; color: var(--primary);">48h</div>
                </div>
            </div>
        `;
        detail.scrollIntoView({ behavior: 'smooth' });
    },

    showSignatureModal(acao, descricao) {
        const modal = document.getElementById('signature-modal');
        if (!modal) return;
        
        modal.classList.remove('hidden');
        document.getElementById('sig-acao').innerText = acao;
        document.getElementById('sig-descricao').innerText = descricao;
        
        const form = document.getElementById('signature-form');
        form.onsubmit = async (e) => {
            e.preventDefault();
            const password = document.getElementById('sig-password').value;
            const reason = document.getElementById('sig-reason').value;
            
            this.showLoading();
            try {
                // Here we would call the actual API that handles signature
                // For demonstration, we'll simulate a success
                this.notify('Ação assinada eletronicamente com sucesso!', 'success');
                modal.classList.add('hidden');
                form.reset();
            } catch (err) {
                this.notify('Falha na assinatura eletrônica.', 'error');
            } finally {
                this.hideLoading();
            }
        };
    },

    closeSignatureModal() {
        document.getElementById('signature-modal').classList.add('hidden');
    },

    showLoading() { this.loadingOverlay.classList.remove('hidden'); },
    hideLoading() { this.loadingOverlay.classList.add('hidden'); }
};

// Start App
document.addEventListener('DOMContentLoaded', () => App.init());
