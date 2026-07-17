# Test simple des endpoints critiques SIGAVT
$BASE = "http://localhost:8080"

Write-Host "=== TEST SIMPLE SIGAVT ===" -ForegroundColor Cyan

# Test 1: Login
Write-Host "`n1. Authentification" -ForegroundColor Yellow
try {
    $loginBody = @{email="admin@sigavt.cm"; motDePasse="admin123"} | ConvertTo-Json
    $loginResponse = Invoke-RestMethod -Uri "$BASE/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    $token = $loginResponse.token
    if ($token) {
        Write-Host "  [OK] Login admin réussi" -ForegroundColor Green
    } else {
        Write-Host "  [FAIL] Login admin - pas de token" -ForegroundColor Red
    }
} catch {
    Write-Host "  [FAIL] Login admin - $($_.Exception.Message)" -ForegroundColor Red
    $token = $null
}

$headers = @{}
if ($token) {
    $headers["Authorization"] = "Bearer $token"
}

# Test 2: Dashboard
Write-Host "`n2. Dashboard" -ForegroundColor Yellow
$endpoints = @{
    "/api/dashboard" = "Dashboard principal"
    "/api/dashboard/stats" = "Stats dashboard"
    "/api/dashboard/departs" = "Départs du jour"
}

foreach ($url in $endpoints.Keys) {
    try {
        $response = Invoke-RestMethod -Uri "$BASE$url" -Method GET -Headers $headers
        Write-Host "  [OK] $($endpoints[$url])" -ForegroundColor Green
    } catch {
        Write-Host "  [FAIL] $($endpoints[$url]) - $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Test 3: Billets
Write-Host "`n3. Billets" -ForegroundColor Yellow
$endpoints = @{
    "/api/billets?page=0&size=5" = "Liste billets"
    "/api/voyages?page=0&size=5" = "Liste voyages"
}

foreach ($url in $endpoints.Keys) {
    try {
        $response = Invoke-RestMethod -Uri "$BASE$url" -Method GET -Headers $headers
        Write-Host "  [OK] $($endpoints[$url])" -ForegroundColor Green
    } catch {
        Write-Host "  [FAIL] $($endpoints[$url]) - $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Test 4: Colis
Write-Host "`n4. Colis" -ForegroundColor Yellow
$endpoints = @{
    "/api/colis?page=0&size=5" = "Liste colis"
}

foreach ($url in $endpoints.Keys) {
    try {
        $response = Invoke-RestMethod -Uri "$BASE$url" -Method GET -Headers $headers
        Write-Host "  [OK] $($endpoints[$url])" -ForegroundColor Green
    } catch {
        Write-Host "  [FAIL] $($endpoints[$url]) - $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Test 5: Autres modules
Write-Host "`n5. Autres modules" -ForegroundColor Yellow
$endpoints = @{
    "/api/bus?page=0&size=5" = "Liste bus"
    "/api/lignes" = "Liste lignes"
    "/api/personnel?page=0&size=5" = "Liste personnel"
    "/api/paie/stats?mois=5&annee=2025" = "Stats paie"
    "/api/comptabilite/ecritures?page=0&size=5" = "Écritures comptabilité"
}

foreach ($url in $endpoints.Keys) {
    try {
        $response = Invoke-RestMethod -Uri "$BASE$url" -Method GET -Headers $headers
        Write-Host "  [OK] $($endpoints[$url])" -ForegroundColor Green
    } catch {
        Write-Host "  [FAIL] $($endpoints[$url]) - $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n=== FIN DES TESTS ===" -ForegroundColor Cyan
