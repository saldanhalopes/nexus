@echo off
setlocal

echo.
echo ========================================================
echo   Nexus - Verificacao de Ambiente Docker
echo ========================================================
echo.

echo [1/2] Testando resolucao de DNS no seu Host (Windows)...
nslookup docker.io
if %ERRORLEVEL% NEQ 0 (
    echo [ALERTA] DNS falhando no Host! Verifique sua conexao ou VPN.
)

echo.
echo [2/2] Testando pull de imagem leve (Alpine)...
docker pull alpine:latest
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [FALHA] O Docker nao conseguiu baixar a imagem!
    echo Provavelmente o DNS interno do Docker Desktop esta bloqueado.
    echo.
    echo SOLUCAO sugerida:
    echo 1. Docker Settings -> Docker Engine
    echo 2. Adicionar '"dns": ["8.8.8.8"]'
    echo 3. Apply e Restart.
    echo.
) else (
    echo.
    echo [SUCESSO] Docker conectado e pronto!
    echo Voce ja pode rodar o 'build-nexus.bat' para atualizar o sistema.
)

echo.
pause
