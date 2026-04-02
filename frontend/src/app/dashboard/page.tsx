'use client';

import { useEffect, useState } from 'react';
import { api } from '@/lib/api';
import { BookOpen, AlertCircle, Clock, CheckCircle } from 'lucide-react';

interface Treinamento {
  id: string;
  assunto: string;
  categoria: string;
  tipoTreinamento: string;
  status: string;
}

export default function Dashboard() {
  const [obrigatoios, setObrigatorios] = useState<Treinamento[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadData = async () => {
      try {
        // Tentativa de puxar do backend via proxy Next.js:
        const res = await api.get('/treinamentos/obrigatorios');
        setObrigatorios(res.data);
      } catch (err) {
        console.error('Falha ao carregar treinamentos. Exibindo MOCK:', err);
        // Fallback temporário Mockado para demonstração UI (se o back estiver offline)
        setObrigatorios([
          { id: '1', assunto: 'Integração Institucional e LGPD', categoria: 'Onboarding', tipoTreinamento: 'EAD', status: 'PENDENTE' },
          { id: '2', assunto: 'Segurança da Informação e Compliance', categoria: 'Regulatório', tipoTreinamento: 'Misto', status: 'PENDENTE' },
        ]);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, []);

  return (
    <div className="space-y-8 animate-in slide-in-from-bottom-4 duration-700 fade-in">
      <div className="flex flex-col gap-1.5">
        <h1 className="text-3xl font-extrabold tracking-tight">Meus Treinamentos</h1>
        <p className="text-muted-foreground text-sm">Bem-vindo(a) ao seu portal de desenvolvimento capacitacional.</p>
      </div>

      <div>
        <h2 className="text-lg font-bold mb-5 flex items-center gap-2 tracking-tight">
          <AlertCircle className="text-danger w-5 h-5" /> 
          Obrigatórios Pendentes
        </h2>
        
        {loading ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {[1, 2, 3].map(i => (
              <div key={i} className="animate-pulse bg-surface/50 h-44 w-full rounded-2xl border border-surface-border"></div>
            ))}
          </div>
        ) : obrigatoios.length === 0 ? (
          <div className="p-12 border border-dashed border-surface-border rounded-2xl flex flex-col items-center justify-center text-muted-foreground bg-surface/30">
            <CheckCircle className="w-12 h-12 text-primary/50 mb-3" />
            <p className="font-medium text-foreground">Você não possui treinamentos pendentes.</p>
            <p className="text-sm">Parabéns, você está em dia com suas obrigações!</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {obrigatoios.map(treinamento => (
              <div key={treinamento.id} className="glass-panel p-6 rounded-2xl relative group hover:-translate-y-1 hover:shadow-[0_10px_30px_rgba(16,185,129,0.08)] transition-all duration-300">
                <div className="absolute top-0 right-0 p-4">
                  <span className="bg-danger/10 text-danger text-[10px] px-2.5 py-1 rounded-full font-bold uppercase tracking-wider border border-danger/20">
                    Obrigatório
                  </span>
                </div>
                
                <div className="w-12 h-12 rounded-xl bg-primary/10 border border-primary/20 flex items-center justify-center mb-5 group-hover:scale-110 group-hover:bg-primary/20 transition-all duration-300">
                   <BookOpen className="text-primary w-6 h-6" />
                </div>
                
                <h3 className="font-semibold text-lg leading-tight mb-2 group-hover:text-primary transition-colors line-clamp-2">
                  {treinamento.assunto}
                </h3>
                
                <div className="flex items-center gap-3 text-xs text-muted-foreground mb-6">
                  <span className="bg-surface border border-surface-border px-2 py-1 rounded text-foreground/80">{treinamento.categoria}</span>
                  <span className="flex items-center gap-1"><Clock className="w-3 h-3" /> {treinamento.tipoTreinamento}</span>
                </div>
                
                <button className="w-full text-sm font-semibold bg-surface hover:bg-primary/20 hover:text-primary border border-surface-border py-2.5 rounded-xl transition-all duration-300 text-foreground">
                  Iniciar Módulo
                </button>
              </div>
            ))}
          </div>
        )}
      </div>

    </div>
  );
}
