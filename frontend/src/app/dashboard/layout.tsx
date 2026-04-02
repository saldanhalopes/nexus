import { ReactNode } from 'react';
import { Home, Users, Briefcase, Settings, LogOut } from 'lucide-react';
import Link from 'next/link';

export default function DashboardLayout({ children }: { children: ReactNode }) {
  return (
    <div className="min-h-screen bg-background text-foreground flex">
      {/* Sidebar - Glassmorphism style */}
      <aside className="w-64 border-r border-surface-border glass-panel p-6 flex-col justify-between hidden lg:flex relative z-20">
        <div>
          <h2 className="text-2xl font-bold tracking-tight mb-8 text-transparent bg-clip-text bg-gradient-to-r from-primary to-emerald-300">
            Nexus
          </h2>
          <nav className="space-y-2">
            <Link href="/dashboard" className="flex items-center gap-3 px-3 py-2.5 rounded-xl bg-primary/10 text-primary font-medium border border-primary/20">
              <Home className="w-5 h-5" /> Treinamentos
            </Link>
            <Link href="#" className="flex items-center gap-3 px-3 py-2.5 rounded-xl hover:bg-surface text-muted-foreground hover:text-foreground transition-colors duration-200">
              <Users className="w-5 h-5" /> Equipe
            </Link>
            <Link href="#" className="flex items-center gap-3 px-3 py-2.5 rounded-xl hover:bg-surface text-muted-foreground hover:text-foreground transition-colors duration-200">
              <Briefcase className="w-5 h-5" /> Competências
            </Link>
          </nav>
        </div>
        <div>
          <nav className="space-y-2 border-t border-surface-border pt-4">
             <Link href="#" className="flex items-center gap-3 px-3 py-2.5 rounded-xl hover:bg-surface text-muted-foreground hover:text-foreground transition-colors">
               <Settings className="w-5 h-5" /> Ajustes
             </Link>
             <button className="w-full flex items-center gap-3 px-3 py-2.5 rounded-xl hover:bg-danger/10 hover:border-danger/20 border border-transparent text-muted-foreground hover:text-danger transition-all">
               <LogOut className="w-5 h-5" /> Sair
             </button>
          </nav>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 h-screen overflow-y-auto relative">
        {/* Subtle decorative background blob for the main content area */}
        <div className="absolute top-[-20%] right-[-10%] w-[50%] h-[50%] bg-primary/5 blur-[150px] rounded-full pointer-events-none" />
        
        <header className="sticky top-0 z-10 w-full h-16 backdrop-blur-xl bg-background/60 border-b border-surface-border flex items-center px-8">
           <h3 className="font-medium text-sm text-muted-foreground">Portal Corporativo / <span className="text-foreground">Treinamentos</span></h3>
        </header>

        <div className="p-8 relative z-10 max-w-7xl mx-auto">
          {children}
        </div>
      </main>
    </div>
  );
}
