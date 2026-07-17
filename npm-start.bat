@echo off
echo ========================================
echo SIGAVT - Lancement de l'application
echo ========================================
echo.

REM Vérifier si le JAR existe
if not exist "target\sigavt.jar" (
    echo Le JAR n'existe pas. Compilation en cours...
    echo.
    .\apache-maven-3.9.6\bin\mvn.cmd clean package
    if %ERRORLEVEL% NEQ 0 (
        echo Erreur lors de la compilation.
        pause
        exit /b 1
    )
)

echo Lancement de l'application...
echo.
java -jar target\sigavt.jar

pause
