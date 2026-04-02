@echo off
setlocal

echo.
echo ========================================================
echo   Nexus - Iniciando Orquestracao Completa (Docker)
echo ========================================================
echo.
echo [1/2] Parando containers anteriores (limpeza)...
docker-compose down

echo [2/2] Iniciando build e execucao (isso pode demorar na primeira vez)...
docker-compose up --build -d

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERRO] Falha ao subir os servicos Docker.
    echo Certifique-se de que o Docker Desktop esta aberto e rodando.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo [SUCESSO] Ecossistema Nexus rodando!
echo --------------------------------------------------------
echo Frontend: http://localhost:3000
echo Backend (API): http://localhost:8089
echo.
echo Monitorando logs de inicializacao (Ctrl+C para parar):
docker logs -f treinamentos-app
