# Script de test complet de l'API SIGAVT
$ErrorActionPreference = "SilentlyContinue"
$baseUrl = "http://localhost:8081"
$results = @()

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TEST COMPLET DE L'API SIGAVT" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Health check
Write-Host "=== HEALTH CHECKS ===" -ForegroundColor Cyan
Write-Host "Test: Health Check" -ForegroundColor Yellow
Write-Host "  GET $baseUrl/actuator/health" -ForegroundColor Gray
$response = Invoke-RestMethod -Method GET -Uri "$baseUrl/actuator/health" -ContentType "application/json" -ErrorVariable myError
if ($myError) {
    Write-Host "  X FAILED: $($myError.Message)" -ForegroundColor Red
    $results += @{Test = "Health Check"; Result = "FAILED"}
}
else {
    Write-Host "  SUCCESS" -ForegroundColor Green
    $results += @{Test = "Health Check"; Result = "SUCCESS"}
}
$myError = $null
Write-Host ""

# 2. Authentication tests
Write-Host "=== AUTHENTICATION TESTS ===" -ForegroundColor Cyan

# Test login with correct credentials
Write-Host "Test: Login (admin)" -ForegroundColor Yellow
Write-Host "  POST $baseUrl/api/auth/login" -ForegroundColor Gray
$loginBody = @{email = "admin@sigavt.cm"; motDePasse = "admin123"} | ConvertTo-Json
$response = Invoke-RestMethod -Method POST -Uri "$baseUrl/api/auth/login" -Body $loginBody -ContentType "application/json" -ErrorVariable myError
if ($myError) {
    Write-Host "  X FAILED: $($myError.Message)" -ForegroundColor Red
    $token = $null
    $results += @{Test = "Login (admin)"; Result = "FAILED"}
}
else {
    Write-Host "  SUCCESS" -ForegroundColor Green
    $token = $response.token
    Write-Host "  Token obtained: $($token.Substring(0, 20))..." -ForegroundColor Green
    $results += @{Test = "Login (admin)"; Result = "SUCCESS"}
}
$myError = $null
Write-Host ""

# 3. Protected endpoints tests
if ($token -ne $null) {
    $authHeaders = @{"Authorization" = "Bearer $token"}
    
    Write-Host "=== PROTECTED ENDPOINTS TESTS ===" -ForegroundColor Cyan
    
    # Dashboard
    Write-Host "Test: Dashboard" -ForegroundColor Yellow
    $response = Invoke-RestMethod -Method GET -Uri "$baseUrl/api/dashboard" -Headers $authHeaders -ContentType "application/json" -ErrorVariable myError
    if ($myError) {
        Write-Host "  X FAILED: $($myError.Message)" -ForegroundColor Red
        $results += @{Test = "Dashboard"; Result = "FAILED"}
    }
    else {
        Write-Host "  SUCCESS" -ForegroundColor Green
        $results += @{Test = "Dashboard"; Result = "SUCCESS"}
    }
    $myError = $null
    Write-Host ""
    
    # Agences
    Write-Host "Test: Liste des agences" -ForegroundColor Yellow
    $response = Invoke-RestMethod -Method GET -Uri "$baseUrl/api/agences" -Headers $authHeaders -ContentType "application/json" -ErrorVariable myError
    if ($myError) {
        Write-Host "  X FAILED: $($myError.Message)" -ForegroundColor Red
        $results += @{Test = "Liste des agences"; Result = "FAILED"}
    }
    else {
        Write-Host "  SUCCESS" -ForegroundColor Green
        $results += @{Test = "Liste des agences"; Result = "SUCCESS"}
    }
    $myError = $null
    Write-Host ""
    
    # Bus
    Write-Host "Test: Liste des bus" -ForegroundColor Yellow
    $response = Invoke-RestMethod -Method GET -Uri "$baseUrl/api/bus" -Headers $authHeaders -ContentType "application/json" -ErrorVariable myError
    if ($myError) {
        Write-Host "  X FAILED: $($myError.Message)" -ForegroundColor Red
        $results += @{Test = "Liste des bus"; Result = "FAILED"}
    }
    else {
        Write-Host "  SUCCESS" -ForegroundColor Green
        $results += @{Test = "Liste des bus"; Result = "SUCCESS"}
    }
    $myError = $null
    Write-Host ""
    
    # Lignes
    Write-Host "Test: Liste des lignes" -ForegroundColor Yellow
    $response = Invoke-RestMethod -Method GET -Uri "$baseUrl/api/lignes" -Headers $authHeaders -ContentType "application/json" -ErrorVariable myError
    if ($myError) {
        Write-Host "  X FAILED: $($myError.Message)" -ForegroundColor Red
        $results += @{Test = "Liste des lignes"; Result = "FAILED"}
    }
    else {
        Write-Host "  SUCCESS" -ForegroundColor Green
        $results += @{Test = "Liste des lignes"; Result = "SUCCESS"}
    }
    $myError = $null
    Write-Host ""
    
    # Personnel
    Write-Host "Test: Liste du personnel" -ForegroundColor Yellow
    $response = Invoke-RestMethod -Method GET -Uri "$baseUrl/api/personnel" -Headers $authHeaders -ContentType "application/json" -ErrorVariable myError
    if ($myError) {
        Write-Host "  X FAILED: $($myError.Message)" -ForegroundColor Red
        $results += @{Test = "Liste du personnel"; Result = "FAILED"}
    }
    else {
        Write-Host "  SUCCESS" -ForegroundColor Green
        $results += @{Test = "Liste du personnel"; Result = "SUCCESS"}
    }
    $myError = $null
    Write-Host ""
    
    # Voyages
    Write-Host "Test: Liste des voyages" -ForegroundColor Yellow
    $response = Invoke-RestMethod -Method GET -Uri "$baseUrl/api/voyages" -Headers $authHeaders -ContentType "application/json" -ErrorVariable myError
    if ($myError) {
        Write-Host "  X FAILED: $($myError.Message)" -ForegroundColor Red
        $results += @{Test = "Liste des voyages"; Result = "FAILED"}
    }
    else {
        Write-Host "  SUCCESS" -ForegroundColor Green
        $results += @{Test = "Liste des voyages"; Result = "SUCCESS"}
    }
    $myError = $null
    Write-Host ""
    
    # Billets
    Write-Host "Test: Liste des billets" -ForegroundColor Yellow
    $response = Invoke-RestMethod -Method GET -Uri "$baseUrl/api/billets" -Headers $authHeaders -ContentType "application/json" -ErrorVariable myError
    if ($myError) {
        Write-Host "  X FAILED: $($myError.Message)" -ForegroundColor Red
        $results += @{Test = "Liste des billets"; Result = "FAILED"}
    }
    else {
        Write-Host "  SUCCESS" -ForegroundColor Green
        $results += @{Test = "Liste des billets"; Result = "SUCCESS"}
    }
    $myError = $null
    Write-Host ""
    
    # Colis
    Write-Host "Test: Liste des colis" -ForegroundColor Yellow
    $response = Invoke-RestMethod -Method GET -Uri "$baseUrl/api/colis" -Headers $authHeaders -ContentType "application/json" -ErrorVariable myError
    if ($myError) {
        Write-Host "  X FAILED: $($myError.Message)" -ForegroundColor Red
        $results += @{Test = "Liste des colis"; Result = "FAILED"}
    }
    else {
        Write-Host "  SUCCESS" -ForegroundColor Green
        $results += @{Test = "Liste des colis"; Result = "SUCCESS"}
    }
    $myError = $null
    Write-Host ""
}
else {
    Write-Host "Cannot test protected endpoints: no token obtained" -ForegroundColor Red
    $results += @{Test = "Protected endpoints"; Result = "FAILED (no token)"}
}

# 4. HTML pages tests
Write-Host "=== HTML PAGES TESTS ===" -ForegroundColor Cyan

$pages = @("/", "/login", "/dashboard")
foreach ($page in $pages) {
    Write-Host "Test: Page $page" -ForegroundColor Yellow
    $response = Invoke-RestMethod -Method GET -Uri "$baseUrl$page" -ContentType "application/json" -ErrorVariable myError
    if ($myError) {
        Write-Host "  X FAILED: $($myError.Message)" -ForegroundColor Red
        $results += @{Test = "Page $page"; Result = "FAILED"}
    }
    else {
        Write-Host "  SUCCESS" -ForegroundColor Green
        $results += @{Test = "Page $page"; Result = "SUCCESS"}
    }
    $myError = $null
    Write-Host ""
}

# 5. Results summary
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TEST SUMMARY" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$successCount = 0
$failCount = 0

foreach ($result in $results) {
    if ($result.Result -eq "SUCCESS") {
        Write-Host "OK $($result.Test): $($result.Result)" -ForegroundColor Green
        $successCount++
    }
    else {
        Write-Host "X $($result.Test): $($result.Result)" -ForegroundColor Red
        $failCount++
    }
}

Write-Host ""
Write-Host "Total: $($results.Count) tests" -ForegroundColor Cyan
Write-Host "Success: $successCount" -ForegroundColor Green
Write-Host "Failed: $failCount" -ForegroundColor Red
Write-Host ""

if ($failCount -eq 0) {
    Write-Host "ALL TESTS PASSED!" -ForegroundColor Green
}
else {
    Write-Host "SOME TESTS FAILED!" -ForegroundColor Red
}

Write-Host ""
