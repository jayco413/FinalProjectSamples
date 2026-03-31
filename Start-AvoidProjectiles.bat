@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT_DIR%Avoid Projectiles\Start-AvoidProjectiles.ps1" %*
