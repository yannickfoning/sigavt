# Script de test complet de l'API SIGAVT
# Teste tous les endpoints principaux de l'application

$ErrorActionPreference = "Continue"
$baseUrl = "http://localhost:8081"
$results = @()

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TEST COMPLET DE L'API SIGAVT" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Fonction pour tester un endpoint
function Test-Endpoint {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [hashtable]$Headers = @{},
        [string]$Body = $null
    )
    
    Write-Host "Test: $Name" -ForegroundColor Yellow
    Write-Host "  $Method $Url" -ForegroundColor Gray
    
    try {
        $params = @{
            Method = $Method
            Uri = $Url
            Headers = $Headers
            ContentType = "application/json"
        }
        
        if ($Body -ne $null) {
            $params.Body = $Body
        }
        
        $response = Invoke-RestMethod @params
        Write-Host "  ✓ SUCCÈS" -ForegroundColor Green
        Write-Host "  Status: 200 OK" -ForegroundColor Gray
        return @{Success = $true; Response = $response}
    }
    catch {
        Write-Host "  ✗ ÉCHEC" -ForegroundColor Red
        Write-Host "  Error: $($_.Exception.Message)" -ForegroundColor Red
        return @{Success = $false; Error = $_.Exception.Message}
    }
    
    Write-Host ""
}

# 1. Test de santé de l'application
Write-Host "=== TESTS DE SANTÉ ===" -ForegroundColor Cyan
$healthResult = Test-Endpoint -Name "Health Check" -Method "GET" -Url "$baseUrl/actuator/health"
$results += @{Test = "Health Check"; Result = if ($healthResult.Success) { "SUCCÈS" } else { "ÉCHEC" }}

# 2. Test d'authentification
Write-Host "=== TESTS D'AUTHENTIFICATION ===" -ForegroundColor Cyan

# Test login avec identifiants incorrects
$loginBody = @{
    email = "test@example.com"
    password = "wrongpassword"
} | ConvertTo-Json

$loginFailResult = Test-Endpoint -Name "Login (invalid credentials)" -Method "POST" -Url "$baseUrl/api/auth/login" -Body $loginBody
$results += @{Test = "Login (invalid credentials)"; Result = if (-not $loginFailResult.Success) { "SUCCÈS (échec attendu)" } else { "ÉCHEC" }}

# Test login avec identifiants corrects (admin par défaut)
$loginBody = @{
    email = "admin@sigavt.cm"
    password = "admin123"
} | ConvertTo-Json

$loginResult = Test-Endpoint -Name "Login (admin)" -Method "POST" -Url "$baseUrl/api/auth/login" -Body $loginBody
$results += @{Test = "Login (admin)"; Result = if ($loginResult.Success) { "SUCCÈS" } else { "ÉCHEC" }}

$token = $null
if ($loginResult.Success) {
    $token = $loginResult.Response.token
    Write-Host "  Token obtenu: $($token.Substring(0, 20))..." -ForegroundColor Green
    Write-Host ""
}

# 3. Tests des endpoints protégés (avec token)
if ($token -ne $null) {
    $authHeaders = @{
        "Authorization" = "Bearer $token"
    }
    
    Write-Host "=== TESTS DES ENDPOINTS PROTÉGÉS ===" -ForegroundColor Cyan
    
    # Dashboard
    $dashboardResult = Test-Endpoint -Name "Dashboard" -Method "GET" -Url "$baseUrl/api/dashboard" -Headers $authHeaders
    $results += @{Test = "Dashboard"; Result = if ($dashboardResult.Success) { "SUCCÈS" } else { "ÉCHEC" }}
    
    # Stats
    $statsResult = Test-Endpoint -Name "Dashboard Stats" -Method "GET" -Url "$baseUrl/api/dashboard/stats" -Headers $authHeaders
    $results += @{Test = "Dashboard Stats"; Result = if ($statsResult.Success) { "SUCCÈS" } else { "ÉCHEC" }}
    
    # Départs
    $departsResult = Test-Endpoint -Name "Départs du jour" -Method "GET" -Url "$baseUrl/api/dashboard/departs" -Headers $authHeaders
    $results += @{Test = "Départs du jour"; Result = if ($departsResult.Success) { "SUCCÈS" } else { "ÉCHEC" }}
    
    # Recettes semaine
    $recettesResult = Test-Endpoint -Name "Recettes semaine" -Method "GET" -Url "$baseUrl/api/dashboard/recettes-semaine" -Headers $authHeaders
    $results += @{Test = "Recettes semaine"; Result = if ($recettesResult.Success) { "SUCCÈS" } else { "ÉCHEC" }}
    
    # Top lignes
    $topLignesResult = Test-Endpoint -Name "Top lignes" -Method "GET" -Url "$baseUrl/api/dashboard/top-lignes" -Headers $authHeaders
    $results += @{Test = "Top lignes"; Result = if ($topLignesResult.Success) { "SUCCÈS" } else { "ÉCHEC" }}
    
    # Alertes
    $alertesResult = Test-Endpoint -Name "Alertes" -Method "GET" -Url "$baseUrl/api/dashboard/alertes" -Headers $authHeaders
    $results += @{Test = "Alertes"; Result = if ($alertesResult.Success) { "SUCCÈS" } else { "ÉCHEC" }}
    
    Write-Host "=== TESTS DES ENTITÉS ===" -ForegroundColor Cyan
    
    # Agences
    $agencesResult = Test-Endpoint -Name "Liste des agences" -Method "GET" -Url "$baseUrl/api/agences" -Headers $authHeaders
    $results += @{Test = "Liste des agences"; Result = if ($agencesResult.Success) { "SUCCÈS" } else { "ÉCHEC" }}
    
    # Bus
    $busResult = Test-Endpoint -Name "Liste des bus" -Method "GET" -Url "$baseUrl/api/bus" -Headers $authHeaders
    $results += @{Test = "Liste des bus"; Result = if ($busResult.Success) { "SUCCÈS" } else { "ÉCHEC" }}
    
    # Lignes
    $lignesResult = Test-Endpoint -Name "Liste des lignes" -Method "GET" -Url "$baseUrl/api/lignes" -Headers $authHeaders
    $results += @{Test = "Liste des lignes"; Result = if ($lignesResult.Success) { "SUCCÈS" } else { "ÉCHEC" }}
    
    # Personnel
    $personnelResult = Test-Endpoint -Name "Liste du personnel" -Method "GET" -Url "$baseUrl/api/personnel" -Headers $authHeaders
    $results += @{Test = "Liste du personnel"; Result = if ($personnelResult.Success) { "SUCCÈS" } else { "ÉCHEC" }}
    
    # Voyages
    $voyagesResult = Test-Endpoint -Name "Liste des voyages" -Method "GET" -Url "$baseUrl/api/voyages" -Headers $authHeaders
    $results += @{Test = "Liste des voyages"; Result = if ($voyagesResult.Success) { "SUCCÈS" } else { "ÉCHEC" }}
    
    # Billets
    $billetsResult = Test-Endpoint -Name "Liste des billets" -Method "GET" -Url "$baseUrl/api/billets" -Headers $authHeaders
    $results += @{Test = "Liste des billets"; Result = if ($billetsResult.Success) { "SUCCÈS" } else { "ÉCHEC" }}
    
    # Colis
    $colisResult = Test-Endpoint -Name "Liste des colis" -Method "GET" -Url "$baseUrl/api/colis" -Headers $authHeaders
    $results += @{Test = "Liste des colis"; Result = if ($colisResult.Success) { "SUCCÈS" } else { "ÉCHEC" }}
    
    # Comptabilité
    $comptabiliteResult = Test-Endpoint -Name "Ecritures comptables" -Method "GET" -Url "$baseUrl/api/comptabilite/ecritures" -Headers $authHeaders
    $results += @{Test = "Ecritures comptables"; Result = if ($comptabiliteResult.Success) { "SUCCÈS" } else { "ÉCHEC" }}
    
    Write-Host "=== TESTS DE SUIVI COLIS (PUBLIC) ===" -ForegroundColor Cyan
    
    # Tracking colis public
    $trackingResult = Test-Endpoint -Name "Tracking colis public" -Method "GET" -Url "$baseUrl/api/colis/tracking/TEST001"
    $results += @{Test = "Tracking colis public"; Result = if ($trackingResult.Success) { "SUCCÈS" } else { "ÉCHEC" }}
}
else {
    Write-Host "Impossible de tester les endpoints protégés: aucun token obtenu" -ForegroundColor Red
    $results += @{Test = "Endpoints protégés"; Result = "ÉCHEC (pas de token)" }
}

# 4. Tests des pages HTML
Write-Host "=== TESTS DES PAGES HTML ===" -ForegroundColor Cyan

$pages = @("/", "/login", "/dashboard")
foreach ($page in $pages) {
    $pageResult = Test-Endpoint -Name "Page $page" -Method "GET" -Url "$baseUrl$page"
    $results += @{Test = "Page $page"; Result = if ($pageResult.Success) { "SUCCÈS" } else { "ÉCHEC" }}
}

# 5. Résumé des résultats
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "RÉSUMÉ DES TESTS" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$successCount = 0
$failCount = 0

foreach ($result in $results) {
    if ($result.Result -like "*SUCCÈS*") {
        Write-Host "✓ $($result.Test): $($result.Result)" -ForegroundColor Green
        $successCount++
    }
    else {
        Write-Host "✗ $($result.Test): $($result.Result)" -ForegroundColor Red
        $failCount++
    }
}

Write-Host ""
Write-Host "Total: $($results.Count) tests" -ForegroundColor Cyan
Write-Host "Succès: $successCount" -ForegroundColor Green
Write-Host "Échecs: $failCount" -ForegroundColor Red
Write-Host ""

if ($failCount -eq 0) {
    Write-Host "TOUS LES TESTS SONT PASSÉS! ✓" -ForegroundColor Green
}
else {
    Write-Host "CERTAINS TESTS ONT ÉCHOUÉ!" -ForegroundColor Red
}

Write-Host ""
