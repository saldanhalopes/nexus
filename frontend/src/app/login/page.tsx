'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { api, setAuthToken } from '@/lib/api';
import { LogIn } from 'lucide-react';

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await api.post('/auth/login', { email, senha });
      const data = response.data;
      
      if (data.token) {
        setAuthToken(data.token);
        // Save user data to local storage or state
        localStorage.setItem('nexus_user', JSON.stringify(data.usuario));
        router.push('/dashboard');
      }
    } catch (err: unknown) {
      const errorObj = err as {response?: {data?: {message?: string}}};
      setError(errorObj.response?.data?.message || 'Erro ao realizar login. Verifique suas credenciais.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center relative overflow-hidden">
      {/* Decorative Background Gradients suitable for Emerald dark theme */}
      <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-primary/20 blur-[120px] rounded-full pointer-events-none" />
      <div className="absolute bottom-[-10%] right-[-10%] w-[30%] h-[30%] bg-surface-border blur-[100px] rounded-full pointer-events-none" />

      <div className="glass-panel p-8 rounded-2xl w-full max-w-md z-10 shadow-2xl relative">
        <div className="flex justify-center mb-6">
          <div className="h-14 w-14 rounded-full bg-primary/20 flex items-center justify-center border border-primary/30">
            <LogIn className="text-primary w-6 h-6" />
          </div>
        </div>
        
        <h1 className="text-2xl font-bold text-center mb-2 text-foreground tracking-tight">
          Nexus Treinamentos
        </h1>
        <p className="text-muted-foreground text-center mb-8 text-sm">
          Acesse a plataforma corporativa
        </p>

        {error && (
          <div className="bg-danger/10 border border-danger/20 text-danger p-3 rounded-lg mb-6 text-sm text-center">
            {error}
          </div>
        )}

        <form onSubmit={handleLogin} className="space-y-4">
          <div className="space-y-1.5">
            <label className="text-sm font-medium text-foreground/80 ml-1">Email Corporativo</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              className="w-full bg-surface border border-surface-border rounded-xl px-4 py-3 text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 focus:border-primary transition-all duration-300"
              placeholder="seu.nome@farma.com"
            />
          </div>

          <div className="space-y-1.5">
            <label className="text-sm font-medium text-foreground/80 ml-1">Senha</label>
            <input
              type="password"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              required
              className="w-full bg-surface border border-surface-border rounded-xl px-4 py-3 text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 focus:border-primary transition-all duration-300"
              placeholder="••••••••"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-primary hover:bg-primary/90 text-primary-foreground font-semibold py-3 rounded-xl transition-all duration-300 shadow-[0_0_15px_rgba(16,185,129,0.3)] hover:shadow-[0_0_25px_rgba(16,185,129,0.5)] active:scale-[0.98] mt-4"
          >
            {loading ? 'Autenticando...' : 'Entrar na plataforma'}
          </button>
        </form>
      </div>
    </div>
  );
}
