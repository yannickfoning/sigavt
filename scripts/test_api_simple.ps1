# Script de test complet de l'API SIGAVT
$ErrorActionPreference = "Continue"
$baseUrl = "http://localhost:8081"
$results = @()

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TEST COMPLET DE L'API SIGAVT" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

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
        Write-Host "  ✓ SUCCESS" -ForegroundColor Green
        Write-Host "  Status: 200 OK" -ForegroundColor Gray
        return @{Success = $true; Response = $response}
    }
    catch {
        Write-Host "  ✗ FAILED" -ForegroundColor Red
        Write-Host "  Error: $($_.Exception.Message)" -ForegroundColor Red
        return @{Success = $false; Error = $_.Exception.Message}
    }
    
    Write-Host ""
}

# 1. Health check
Write-Host "=== HEALTH CHECKS ===" -ForegroundColor Cyan
$healthResult = Test-Endpoint -Name "Health Check" -Method "GET" -Url "$baseUrl/actuator/health"
$results += @{Test = "Health Check"; Result = if ($healthResult.Success) { "SUCCESS" } else { "FAILED" }}

# 2. Authentication tests
Write-Host "=== AUTHENTICATION TESTS ===" -ForegroundColor Cyan

# Test login with wrong credentials
$loginBody = @{
    email = "test@example.com"
    password = "wrongpassword"
} | ConvertTo-Json

$loginFailResult = Test-Endpoint -Name "Login (invalid credentials)" -Method "POST" -Url "$baseUrl/api/auth/login" -Body $loginBody
$results += @{Test = "Login (invalid credentials)"; Result = if (-not $loginFailResult.Success) { "SUCCESS (expected failure)" } else { "FAILED" }}

# Test login with correct credentials (admin default)
$loginBody = @{
    email = "admin@sigavt.cm"
    password = "admin123"
} | ConvertTo-Json

$loginResult = Test-Endpoint -Name "Login (admin)" -Method "POST" -Url "$baseUrl/api/auth/login" -Body $loginBody
$results += @{Test = "Login (admin)"; Result = if ($loginResult.Success) { "SUCCESS" } else { "FAILED" }}

$token = $null
if ($loginResult.Success) {
    $token = $loginResult.Response.token
    Write-Host "  Token obtained: $($token.Substring(0, 20))..." -ForegroundColor Green
    Write-Host ""
}

# 3. Protected endpoints tests (with token)
if ($token -ne $null) {
    $authHeaders = @{
        "Authorization" = "Bearer $token"
    }
    
    Write-Host "=== PROTECTED ENDPOINTS TESTS ===" -ForegroundColor Cyan
    
    # Dashboard
    $dashboardResult = Test-Endpoint -Name "Dashboard" -Method "GET" -Url "$baseUrl/api/dashboard" -Headers $authHeaders
    $results += @{Test = "Dashboard"; Result = if ($dashboardResult.Success) { "SUCCESS" } else { "FAILED" }}
    
    # Stats
    $statsResult = Test-Endpoint -Name "Dashboard Stats" -Method "GET" -Url "$baseUrl/api/dashboard/stats" -Headers $authHeaders
    $results += @{Test = "Dashboard Stats"; Result = if ($statsResult.Success) { "SUCCESS" } else { "FAILED" }}
    
    # Departs
    $departsResult = Test-Endpoint -Name "Departs du jour" -Method "GET" -Url "$baseUrl/api/dashboard/departs" -Headers $authHeaders
    $results += @{Test = "Departs du jour"; Result = if ($departsResult.Success) { "SUCCESS" } else { "FAILED" }}
    
    # Recettes semaine
    $recettesResult = Test-Endpoint -Name "Recettes semaine" -Method "GET" -Url "$baseUrl/api/dashboard/recettes-semaine" -Headers $authHeaders
    $results += @{Test = "Recettes semaine"; Result = if ($recettesResult.Success) { "SUCCESS" } else { "FAILED" }}
    
    # Top lignes
    $topLignesResult = Test-Endpoint -Name "Top lignes" -Method "GET" -Url "$baseUrl/api/dashboard/top-lignes" -Headers $authHeaders
    $results += @{Test = "Top lignes"; Result = if ($topLignesResult.Success) { "SUCCESS" } else { "FAILED" }}
    
    # Alertes
    $alertesResult = Test-Endpoint -Name "Alertes" -Method "GET" -Url "$baseUrl/api/dashboard/alertes" -Headers $authHeaders
    $results += @{Test = "Alertes"; Result = if ($alertesResult.Success) { "SUCCESS" } else { "FAILED" }}
    
    Write-Host "=== ENTITY TESTS ===" -ForegroundColor Cyan
    
    # Agences
    $agencesResult = Test-Endpoint -Name "Liste des agences" -Method "GET" -Url "$baseUrl/api/agences" -Headers $authHeaders
    $results += @{Test = "Liste des agences"; Result = if ($agencesResult.Success) { "SUCCESS" } else { "FAILED" }}
    
    # Bus
    $busResult = Test-Endpoint -Name "Liste des bus" -Method "GET" -Url "$baseUrl/api/bus" -Headers $authHeaders
    $results += @{Test = "Liste des bus"; Result = if ($busResult.Success) { "SUCCESS" } else { "FAILED" }}
    
    # Lignes
    $lignesResult = Test-Endpoint -Name "Liste des lignes" -Method "GET" -Url "$baseUrl/api/lignes" -Headers $authHeaders
    $results += @{Test = "Liste des lignes"; Result = if ($lignesResult.Success) { "SUCCESS" } else { "FAILED" }}
    
    # Personnel
    $personnelResult = Test-Endpoint -Name "Liste du personnel" -Method "GET" -Url "$baseUrl/api/personnel" -Headers $authHeaders
    $results += @{Test = "Liste du personnel"; Result = if ($personnelResult.Success) { "SUCCESS" } else { "FAILED" }}
    
    # Voyages
    $voyagesResult = Test-Endpoint -Name "Liste des voyages" -Method "GET" -Url "$baseUrl/api/voyages" -Headers $authHeaders
    $results += @{Test = "Liste des voyages"; Result = if ($voyagesResult.Success) { "SUCCESS" } else { "FAILED" }}
    
    # Billets
    $billetsResult = Test-Endpoint -Name "Liste des billets" -Method "GET" -Url "$baseUrl/api/billets" -Headers $authHeaders
    $results += @{Test = "Liste des billets"; Result = if ($billetsResult.Success) { "SUCCESS" } else { "FAILED" }}
    
    # Colis
    $colisResult = Test-Endpoint -Name "Liste des colis" -Method "GET" -Url "$baseUrl/api/colis" -Headers $authHeaders
    $results += @{Test = "Liste des colis"; Result = if ($colisResult.Success) { "SUCCESS" } else { "FAILED" }}
    
    # Comptabilite
    $comptabiliteResult = Test-Endpoint -Name "Ecritures comptables" -Method "GET" -Url "$baseUrl/api/comptabilite/ecritures" -Headers $authHeaders
    $results += @{Test = "Ecritures comptables"; Result = if ($comptabiliteResult.Success) { "SUCCESS" } else { "FAILED" }}
    
    Write-Host "=== PUBLIC COLIS TRACKING ===" -ForegroundColor Cyan
    
    # Tracking colis public
    $trackingResult = Test-Endpoint -Name "Tracking colis public" -Method "GET" -Url "$baseUrl/api/colis/tracking/TEST001"
    $results += @{Test = "Tracking colis public"; Result = if ($trackingResult.Success) { "SUCCESS" } else { "FAILED" }}
}
else {
    Write-Host "Cannot test protected endpoints: no token obtained" -ForegroundColor Red
    $results += @{Test = "Protected endpoints"; Result = "FAILED (no token)" }
}

# 4. HTML pages tests
Write-Host "=== HTML PAGES TESTS ===" -ForegroundColor Cyan

$pages = @("/", "/login", "/dashboard")
foreach ($page in $pages) {
    $pageResult = Test-Endpoint -Name "Page $page" -Method "GET" -Url "$baseUrl$page"
    $results += @{Test = "Page $page"; Result = if ($pageResult.Success) { "SUCCESS" } else { "FAILED" }}
}

# 5. Results summary
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TEST SUMMARY" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$successCount = 0
$failCount = 0

foreach ($result in $results) {
    if ($result.Result -like "*SUCCESS*") {
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
Write-Host "Success: $successCount" -ForegroundColor Green
Write-Host "Failed: $failCount" -ForegroundColor Red
Write-Host ""

if ($failCount -eq 0) {
    Write-Host "ALL TESTS PASSED! ✓" -ForegroundColor Green
}
else {
    Write-Host "SOME TESTS FAILED!" -ForegroundColor Red
}

Write-Host ""
