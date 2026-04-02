# Nexus Project

Sistema modernizado de Treinamentos e Gestão para Farmácias.

## 🚀 Tecnologias

- **Backend**: Spring Boot 3+ (Java 21)
- **Frontend**: Next.js (TypeScript)
- **Banco de Dados**: PostgreSQL
- **Orquestração**: Docker & Docker Compose

## 📦 Como Rodar o Projeto

A maneira recomendada é usar os scripts de automação incluídos:

### Build e Deploy Local
Execute o arquivo na raiz do projeto:
```bash
build-nexus.bat
```
Este script irá:
1. Compilar o backend em um ambiente Docker isolado (Maven).
2. Construir as imagens Docker.
3. Subir todos os serviços (Postgres, App, UI).

### Verificação de Ambiente
Para validar se os serviços estão rodando:
```bash
verify-docker.bat
```

## 🔐 Acesso Inicial

Após o build, o sistema estará disponível em:
- **Frontend**: `http://localhost:3000`
- **Backend API**: `http://localhost:8089` (Internamente `8080`)

**Credenciais de Administrador**:
- **Email**: `admin@farma.com`
- **Senha**: `admin123`

## 📂 Estrutura do Projeto

- `/src`: Código-fonte do Backend (Spring Boot).
- `/frontend`: Aplicação Next.js.
- `Dockerfile`: Configuração de build multi-stage do backend.
- `docker-compose.yml`: Definição de toda a infraestrutura de containers.
