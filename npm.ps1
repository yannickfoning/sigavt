# Alias PowerShell pour simuler npm start
# Ce script permet d'utiliser "npm start" même sans Node.js installé

param(
    [Parameter(Position = 0)]
    [string]$Command,
    [Parameter(Position = 1)]
    [string]$SubCommand
)

if ($Command -eq "start") {
    Write-Host "Lancement de SIGAVT..." -ForegroundColor Green
    
    # Vérifier si le JAR existe
    if (-not (Test-Path "target\sigavt.jar")) {
        Write-Host "Le JAR n'existe pas. Compilation en cours..." -ForegroundColor Yellow
        & ".\apache-maven-3.9.6\bin\mvn.cmd" clean package
        if ($LASTEXITCODE -ne 0) {
            Write-Host "Erreur lors de la compilation." -ForegroundColor Red
            exit 1
        }
    }
    
    Write-Host "Lancement de l'application..." -ForegroundColor Green
    java -jar target\sigavt.jar
} elseif ($Command -eq "build") {
    Write-Host "Compilation de SIGAVT..." -ForegroundColor Green
    & ".\apache-maven-3.9.6\bin\mvn.cmd" clean package
} elseif ($Command -eq "dev") {
    Write-Host "Compilation et lancement de SIGAVT..." -ForegroundColor Green
    & ".\apache-maven-3.9.6\bin\mvn.cmd" clean package
    if ($LASTEXITCODE -eq 0) {
        java -jar target\sigavt.jar
    }
} else {
    Write-Host "Commandes disponibles:" -ForegroundColor Cyan
    Write-Host "  npm start   - Lance l'application" -ForegroundColor White
    Write-Host "  npm build   - Compile l'application" -ForegroundColor White
    Write-Host "  npm dev     - Compile et lance l'application" -ForegroundColor White
}
