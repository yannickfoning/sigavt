# Script d'audit complet SIGAVT - Test end-to-end
# Adapté pour Spring Boot 2.7.18 + Java 17 + MySQL XAMPP

$BaseUrl = "http://localhost:8080"
$Results = New-Object System.Collections.Generic.List[object]

function Test-Endpoint {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [hashtable]$Headers = @{},
        [string]$Body = $null,
        [int]$ExpectedStatus = 200,
        [string]$Description = ""
    )
    
    try {
        $Params = @{
            Uri = $BaseUrl + $Url
            Method = $Method
            UseBasicParsing = $true
        }
        
        if ($Headers.Count -gt 0) {
            $Params.Headers = $Headers
        }
        
        if ($Body) {
            $Params.ContentType = "application/json"
            $Params.Body = $Body
        }
        
        $Response = Invoke-WebRequest @Params
        $Status = $Response.StatusCode
        
        if ($Status -eq $ExpectedStatus) {
            $Results.Add([PSCustomObject]@{
                Test = $Name
                Status = "SUCCESS"
                Expected = $ExpectedStatus
                Actual = $Status
                Description = $Description
            })
            Write-Host "✅ $Name" -ForegroundColor Green
        } else {
            $Results.Add([PSCustomObject]@{
                Test = $Name
                Status = "FAIL"
                Expected = $ExpectedStatus
                Actual = $Status
                Description = $Description
            })
            Write-Host "❌ $Name - Expected $ExpectedStatus, got $Status" -ForegroundColor Red
        }
    } catch {
        $Status = $_.Exception.Response.StatusCode.value__
        if ($Status -eq $ExpectedStatus) {
            $Results.Add([PSCustomObject]@{
                Test = $Name
                Status = "SUCCESS"
                Expected = $ExpectedStatus
                Actual = $Status
                Description = "$Description (security test passed)"
            })
            Write-Host "✅ $Name (security test passed)" -ForegroundColor Green
        } else {
            $Results.Add([PSCustomObject]@{
                Test = $Name
                Status = "ERROR"
                Expected = $ExpectedStatus
                Actual = $Status
                Description = "$Description - Error: $($_.Exception.Message)"
            })
            Write-Host "❌ $Name - Error: $($_.Exception.Message)" -ForegroundColor Red
        }
    }
}

Write-Host "=== AUDIT COMPLET SIGAVT ===" -ForegroundColor Cyan
Write-Host ""

# Étape 1: Authentification
Write-Host "ÉTAPE 1: AUTHENTIFICATION" -ForegroundColor Yellow
Write-Host ""

# Login valide
$LoginBody = '{"email":"admin@sigavt.cm","motDePasse":"admin123"}'
$LoginResponse = Invoke-WebRequest -Uri "$BaseUrl/api/auth/login" -Method POST -ContentType "application/json" -Body $LoginBody -UseBasicParsing
$LoginData = $LoginResponse.Content | ConvertFrom-Json
$Token = $LoginData.token
$AuthHeaders = @{"Authorization" = "Bearer $Token"}

Test-Endpoint -Name "Login valide" -Method "POST" -Url "/api/auth/login" -Body $LoginBody -ExpectedStatus 200 -Description "Connexion admin réussie"

# Login invalide
Test-Endpoint -Name "Login invalide" -Method "POST" -Url "/api/auth/login" -Body '{"email":"admin@sigavt.cm","motDePasse":"wrong"}' -ExpectedStatus 401 -Description "Mot de passe incorrect"

# Accès sans token
Test-Endpoint -Name "Accès sans token" -Method "GET" -Url "/api/bus" -ExpectedStatus 401 -Description "Endpoint protégé sans authentification"

# Accès avec token valide
Test-Endpoint -Name "Accès avec token valide" -Method "GET" -Url "/api/bus" -Headers $AuthHeaders -ExpectedStatus 200 -Description "Endpoint protégé avec authentification"

Write-Host ""
Write-Host "ÉTAPE 2: TESTS CRUD PAR MODULE" -ForegroundColor Yellow
Write-Host ""

# Module Agences
Write-Host "Module Agences" -ForegroundColor Cyan
Test-Endpoint -Name "GET agences" -Method "GET" -Url "/api/agences" -Headers $AuthHeaders -ExpectedStatus 200
Test-Endpoint -Name "POST agence" -Method "POST" -Url "/api/agences" -Headers $AuthHeaders -Body '{"nom":"Agence Test","ville":"Douala","adresse":"Test Adresse","telephone":"699000000","email":"test@test.com"}' -ExpectedStatus 201

# Module Lignes
Write-Host "Module Lignes" -ForegroundColor Cyan
Test-Endpoint -Name "GET lignes" -Method "GET" -Url "/api/lignes" -Headers $AuthHeaders -ExpectedStatus 200
Test-Endpoint -Name "POST ligne" -Method "POST" -Url "/api/lignes" -Headers $AuthHeaders -Body '{"villeDepart":"Douala","villeArrivee":"Yaoundé","distanceKm":250,"dureeMinutes":210,"tarifBase":5000,"frequenceJour":2,"statut":"ACTIVE"}' -ExpectedStatus 201

# Module Bus
Write-Host "Module Bus" -ForegroundColor Cyan
Test-Endpoint -Name "GET bus" -Method "GET" -Url "/api/bus" -Headers $AuthHeaders -ExpectedStatus 200
Test-Endpoint -Name "POST bus" -Method "POST" -Url "/api/bus" -Headers $AuthHeaders -Body '{"immatriculation":"AUDIT-NEW","modele":"Bus Test","nombrePlaces":50,"statut":"maintenance"}' -ExpectedStatus 201

# Module Personnel
Write-Host "Module Personnel" -ForegroundColor Cyan
Test-Endpoint -Name "GET personnel" -Method "GET" -Url "/api/personnel" -Headers $AuthHeaders -ExpectedStatus 200
Test-Endpoint -Name "POST personnel" -Method "POST" -Url "/api/personnel" -Headers $AuthHeaders -Body '{"nomComplet":"Test Employé","poste":"chauffeur","typeContrat":"cdi"}' -ExpectedStatus 201

# Module Voyages
Write-Host "Module Voyages" -ForegroundColor Cyan
Test-Endpoint -Name "GET voyages" -Method "GET" -Url "/api/voyages" -Headers $AuthHeaders -ExpectedStatus 200

# Module Billets
Write-Host "Module Billets" -ForegroundColor Cyan
Test-Endpoint -Name "GET billets" -Method "GET" -Url "/api/billets" -Headers $AuthHeaders -ExpectedStatus 200

# Module Colis
Write-Host "Module Colis" -ForegroundColor Cyan
Test-Endpoint -Name "GET colis" -Method "GET" -Url "/api/colis" -Headers $AuthHeaders -ExpectedStatus 200

# Module Courriers
Write-Host "Module Courriers" -ForegroundColor Cyan
Test-Endpoint -Name "GET courriers" -Method "GET" -Url "/api/courriers" -Headers $AuthHeaders -ExpectedStatus 200

# Module Paie
Write-Host "Module Paie" -ForegroundColor Cyan
Test-Endpoint -Name "GET bulletins" -Method "GET" -Url "/api/paie/bulletins" -Headers $AuthHeaders -ExpectedStatus 200

# Module Comptabilité
Write-Host "Module Comptabilité" -ForegroundColor Cyan
Test-Endpoint -Name "GET écritures" -Method "GET" -Url "/api/comptabilite/ecritures" -Headers $AuthHeaders -ExpectedStatus 200
Test-Endpoint -Name "GET bilan" -Method "GET" -Url "/api/comptabilite/bilan" -Headers $AuthHeaders -ExpectedStatus 200

# Module Paramètres
Write-Host "Module Paramètres" -ForegroundColor Cyan
Test-Endpoint -Name "GET paramètres" -Method "GET" -Url "/api/parametres" -Headers $AuthHeaders -ExpectedStatus 200

# Module Dashboard
Write-Host "Module Dashboard" -ForegroundColor Cyan
Test-Endpoint -Name "GET dashboard" -Method "GET" -Url "/api/dashboard" -Headers $AuthHeaders -ExpectedStatus 200
Test-Endpoint -Name "GET stats" -Method "GET" -Url "/api/dashboard/stats" -Headers $AuthHeaders -ExpectedStatus 200

Write-Host ""
Write-Host "ÉTAPE 3: TESTS CAS LIMITES" -ForegroundColor Yellow
Write-Host ""

# Test ID inexistant
Test-Endpoint -Name "GET bus ID inexistant" -Method "GET" -Url "/api/bus/99999" -Headers $AuthHeaders -ExpectedStatus 404

# Test payload invalide
Test-Endpoint -Name "POST bus payload invalide" -Method "POST" -Url "/api/bus" -Headers $AuthHeaders -Body '{"immatriculation":""}' -ExpectedStatus 400

Write-Host ""
Write-Host "=== RÉSUMÉ DES TESTS ===" -ForegroundColor Cyan
Write-Host ""

$SuccessCount = ($Results | Where-Object { $_.Status -eq "SUCCESS" }).Count
$FailCount = ($Results | Where-Object { $_.Status -eq "FAIL" }).Count
$ErrorCount = ($Results | Where-Object { $_.Status -eq "ERROR" }).Count
$TotalCount = $Results.Count

Write-Host "Total tests: $TotalCount" -ForegroundColor White
Write-Host "Succès: $SuccessCount" -ForegroundColor Green
Write-Host "Échecs: $FailCount" -ForegroundColor Red
Write-Host "Erreurs: $ErrorCount" -ForegroundColor Yellow

Write-Host "Total tests: $TotalCount" -ForegroundColor White
Write-Host "Succès: $SuccessCount" -ForegroundColor Green
Write-Host "Échecs: $FailCount" -ForegroundColor Red
Write-Host "Erreurs: $ErrorCount" -ForegroundColor Yellow

Write-Host ""
Write-Host "Détails des échecs et erreurs:" -ForegroundColor Yellow
$Results | Where-Object { $_.Status -ne "SUCCESS" } | Format-Table -AutoSize

# Export results
if ($Results.Count -gt 0) {
    $Results | Export-Csv -Path "D:\sigavt\audit_results.csv" -NoTypeInformation
    Write-Host ""
    Write-Host "Résultats exportés dans: D:\sigavt\audit_results.csv" -ForegroundColor Cyan
} else {
    Write-Host ""
    Write-Host "Aucun résultat à exporter" -ForegroundColor Yellow
}
