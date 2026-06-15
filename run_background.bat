@echo off
echo Starting Java Backend in background...
start /B javaw -jar "d:\Tm\truck_java\target\truck-backend-1.0.0.jar" > "d:\Tm\truck_java\server.log" 2>&1
echo Backend started on http://localhost:8000
echo Log file: d:\Tm\truck_java\server.log
timeout /t 3 /nobreak >nul
