import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: 'standalone',
  async rewrites() {
    // No Docker, o frontend acessa o backend via nome do serviço 'app'
    // Mas preservamos a opção de rodar localmente caso o usuário prefira
    return [
      {
        source: '/api/:path*',
        destination: process.env.NODE_ENV === 'production' 
          ? 'http://app:8080/api/:path*' 
          : 'http://localhost:8080/api/:path*',
      },
    ];
  },
};

export default nextConfig;
