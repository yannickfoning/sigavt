@echo off
echo ========================================
echo SIGAVT - Lancement de l'application
echo ========================================
echo.

REM Vérifier si Maven est installé
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo Maven est installe. Compilation et lancement...
    echo.
    mvn clean install spring-boot:run
    goto end
)

REM Maven n'est pas trouve
echo Maven n'est pas installe sur votre systeme.
echo.
echo ========================================
echo SOLUTIONS POSSIBLES :
echo ========================================
echo.
echo 1. Installer Maven depuis : https://maven.apache.org/download.cgi
echo    Puis relancer ce script.
echo.
echo 2. Lancer l'application depuis votre IDE :
echo    - Ouvrez votre IDE (IntelliJ, Eclipse, VS Code)
echo    - Importez le projet Maven dans d:\sigavt
echo    - Lancez src/main/java/com/sigavt/SigavtApplication.java
echo.
echo 3. Utiliser le wrapper Maven (mvnw) si disponible
echo.
echo ========================================
echo.
pause

:end
