# Script de test de persistance MySQL pour SIGAVT
# Ce script teste que les données survivent aux redémarrages et circulent entre les modules

# Configuration
$BaseUrl = "http://localhost:8080"
$AdminEmail = "admin@sigavt.cm"
$AdminPassword = "admin123"

Write-Host "=== Test de persistance MySQL ===" -ForegroundColor Cyan
Write-Host ""

# Étape 1: Connexion et obtention du token JWT
Write-Host "1. Connexion admin..." -ForegroundColor Yellow
$LoginBody = @{
    email = $AdminEmail
    motDePasse = $AdminPassword
} | ConvertTo-Json

try {
    $LoginResponse = Invoke-RestMethod -Uri "$BaseUrl/api/auth/login" -Method Post -Body $LoginBody -ContentType "application/json"
    $Token = $LoginResponse.token
    Write-Host "✅ Connexion réussie, token obtenu" -ForegroundColor Green
} catch {
    Write-Host "❌ Erreur de connexion: $_" -ForegroundColor Red
    exit 1
}

$Headers = @{
    Authorization = "Bearer $Token"
}

# Étape 2: Création d'un bus de test
Write-Host ""
Write-Host "2. Création d'un bus de test..." -ForegroundColor Yellow
$BusTestBody = @{
    immatriculation = "TEST-9999"
    modele = "Bus Test Persistance"
    nombrePlaces = 50
    ligneAssigneeId = 1
    prochainEntretien = "2025-12-31"
    assuranceExpiration = "2026-12-31"
    statut = "MAINTENANCE"
} | ConvertTo-Json

try {
    $BusResponse = Invoke-RestMethod -Uri "$BaseUrl/api/bus" -Method Post -Body $BusTestBody -ContentType "application/json" -Headers $Headers
    $BusTestId = $BusResponse.id
    Write-Host "✅ Bus créé avec ID: $BusTestId" -ForegroundColor Green
} catch {
    Write-Host "❌ Erreur création bus: $_" -ForegroundColor Red
    exit 1
}

# Étape 3: Vérification que le bus est dans la liste
Write-Host ""
Write-Host "3. Vérification que le bus apparaît dans la liste..." -ForegroundColor Yellow
try {
    $BusListResponse = Invoke-RestMethod -Uri "$BaseUrl/api/bus" -Method Get -Headers $Headers
    $BusTestExists = $BusListResponse.content | Where-Object { $_.immatriculation -eq "TEST-9999" }
    if ($BusTestExists) {
        Write-Host "✅ Bus TEST-9999 trouvé dans la liste" -ForegroundColor Green
    } else {
        Write-Host "❌ Bus TEST-9999 non trouvé dans la liste" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Erreur récupération liste bus: $_" -ForegroundColor Red
    exit 1
}

# Étape 4: Comptage des bus avant redémarrage
Write-Host ""
Write-Host "4. Comptage des bus avant redémarrage..." -ForegroundColor Yellow
$BusCountBefore = $BusListResponse.totalElements
Write-Host "Nombre de bus avant redémarrage: $BusCountBefore" -ForegroundColor Cyan

# Étape 5: Instructions pour le redémarrage
Write-Host ""
Write-Host "=== INSTRUCTIONS POUR LE TEST DE PERSISTANCE ===" -ForegroundColor Yellow
Write-Host "1. Arrêtez l'application Spring Boot (Ctrl+C dans le terminal)" -ForegroundColor White
Write-Host "2. Relancez l'application: mvn spring-boot:run" -ForegroundColor White
Write-Host "3. Exécutez ce script à nouveau" -ForegroundColor White
Write-Host ""
Write-Host "4. Le script vérifiera:" -ForegroundColor White
Write-Host "   - Que le bus TEST-9999 existe toujours après redémarrage" -ForegroundColor White
Write-Host "   - Que le compteur de bus est identique" -ForegroundColor White
Write-Host "   - Que le bus peut être récupéré par son ID" -ForegroundColor White
Write-Host ""
Write-Host "Après redémarrage, relancez: .\test_persistance.ps1 -Phase2" -ForegroundColor Cyan
Write-Host ""
Write-Host "Bus de test créé avec ID: $BusTestId" -ForegroundColor Green
Write-Host "Compteur avant redémarrage: $BusCountBefore" -ForegroundColor Green

# Si on est en phase 2 (après redémarrage)
if ($Phase2) {
    Write-Host ""
    Write-Host "=== PHASE 2: Vérification après redémarrage ===" -ForegroundColor Yellow
    
    # Reconnexion
    Write-Host "1. Reconnexion admin..." -ForegroundColor Yellow
    try {
        $LoginResponse = Invoke-RestMethod -Uri "$BaseUrl/api/auth/login" -Method Post -Body $LoginBody -ContentType "application/json"
        $Token = $LoginResponse.token
        $Headers = @{ Authorization = "Bearer $Token" }
        Write-Host "✅ Reconnexion réussie" -ForegroundColor Green
    } catch {
        Write-Host "❌ Erreur reconnexion: $_" -ForegroundColor Red
        exit 1
    }
    
    # Vérification que le bus existe toujours
    Write-Host ""
    Write-Host "2. Vérification que le bus existe toujours..." -ForegroundColor Yellow
    try {
        $BusResponse = Invoke-RestMethod -Uri "$BaseUrl/api/bus/$BusTestId" -Method Get -Headers $Headers
        if ($BusResponse.immatriculation -eq "TEST-9999") {
            Write-Host "✅ Bus TEST-9999 retrouvé par ID" -ForegroundColor Green
        } else {
            Write-Host "❌ Bus retrouvé mais immatriculation incorrecte" -ForegroundColor Red
            exit 1
        }
    } catch {
        Write-Host "❌ Bus non retrouvé par ID: $_" -ForegroundColor Red
        exit 1
    }
    
    # Vérification compteur
    Write-Host ""
    Write-Host "3. Vérification du compteur de bus..." -ForegroundColor Yellow
    try {
        $BusListResponse = Invoke-RestMethod -Uri "$BaseUrl/api/bus" -Method Get -Headers $Headers
        $BusCountAfter = $BusListResponse.totalElements
        Write-Host "Nombre de bus après redémarrage: $BusCountAfter" -ForegroundColor Cyan
        
        if ($BusCountAfter -eq $BusCountBefore) {
            Write-Host "✅ Compteur identique: persistance confirmée" -ForegroundColor Green
        } else {
            Write-Host "❌ Compteur différent: avant=$BusCountBefore, après=$BusCountAfter" -ForegroundColor Red
            exit 1
        }
    } catch {
        Write-Host "❌ Erreur comptage: $_" -ForegroundColor Red
        exit 1
    }
    
    # Nettoyage
    Write-Host ""
    Write-Host "4. Nettoyage du bus de test..." -ForegroundColor Yellow
    try {
        Invoke-RestMethod -Uri "$BaseUrl/api/bus/$BusTestId" -Method Delete -Headers $Headers
        Write-Host "✅ Bus de test supprimé" -ForegroundColor Green
    } catch {
        Write-Host "⚠️ Erreur suppression bus: $_" -ForegroundColor Yellow
    }
    
    Write-Host ""
    Write-Host "=== TEST DE PERSISTANCE RÉUSSI ===" -ForegroundColor Green
}
