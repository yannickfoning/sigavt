# Script de nettoyage de la base de donnees via l'API
$BASE_URL = "http://localhost:8080/api"

# Authentification
$authResponse = Invoke-RestMethod -Uri "$BASE_URL/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"admin@sigavt.cm","motDePasse":"admin123"}'

$token = $authResponse.token
$headers = @{
    "Authorization" = "Bearer $token"
}

Write-Host "=== NETTOYAGE DE LA BASE DE DONNEES ===" -ForegroundColor Cyan
Write-Host ""

# 1. Nettoyage des agences doublons
Write-Host "1. Nettoyage des agences..." -ForegroundColor Yellow
$agencesResponse = Invoke-RestMethod -Uri "$BASE_URL/agences" -Method GET -Headers $headers
$agences = $agencesResponse

Write-Host "   Agences trouvees: $($agences.Count)"

if ($agences.Count -gt 1) {
    $agencesASupprimer = $agences[1..($agences.Count-1)]
    
    foreach ($agence in $agencesASupprimer) {
        try {
            $deleteResponse = Invoke-WebRequest -Uri "$BASE_URL/agences/$($agence.id)" -Method DELETE -Headers $headers
            if ($deleteResponse.StatusCode -eq 204) {
                Write-Host "   OK - Agence supprimee: $($agence.nom)" -ForegroundColor Green
            }
        } catch {
            Write-Host "   ERREUR - suppression agence $($agence.id)" -ForegroundColor Red
        }
    }
} else {
    Write-Host "   OK - Pas de doublons d'agences" -ForegroundColor Green
}

# 2. Nettoyage des lignes doublons
Write-Host ""
Write-Host "2. Nettoyage des lignes..." -ForegroundColor Yellow
$lignesResponse = Invoke-RestMethod -Uri "$BASE_URL/lignes" -Method GET -Headers $headers
$lignes = $lignesResponse

Write-Host "   Lignes trouvees: $($lignes.Count)"

$lignesUniques = @{}
$lignesDoublons = @{}

foreach ($ligne in $lignes) {
    $cle = "$($ligne.villeDepart)|$($ligne.villeArrivee)"
    if ($lignesUniques.ContainsKey($cle)) {
        if (-not $lignesDoublons.ContainsKey($cle)) {
            $lignesDoublons[$cle] = @()
        }
        $lignesDoublons[$cle] += $ligne
    } else {
        $lignesUniques[$cle] = $ligne
    }
}

Write-Host "   Lignes uniques: $($lignesUniques.Count)"
$doublonsCount = 0
foreach ($cle in $lignesDoublons.Keys) {
    $doublonsCount += $lignesDoublons[$cle].Count
}
Write-Host "   Lignes doublons: $doublonsCount"

foreach ($cle in $lignesDoublons.Keys) {
    foreach ($ligne in $lignesDoublons[$cle]) {
        try {
            $deleteResponse = Invoke-WebRequest -Uri "$BASE_URL/lignes/$($ligne.id)" -Method DELETE -Headers $headers
            if ($deleteResponse.StatusCode -eq 204) {
                Write-Host "   OK - Ligne supprimee: $($ligne.villeDepart) to $($ligne.villeArrivee)" -ForegroundColor Green
            }
        } catch {
            Write-Host "   ERREUR - suppression ligne $($ligne.id)" -ForegroundColor Red
        }
    }
}

# Verification finale
Write-Host ""
Write-Host "=== VERIFICATION FINALE ===" -ForegroundColor Cyan
$agencesFinal = Invoke-RestMethod -Uri "$BASE_URL/agences" -Method GET -Headers $headers
$lignesFinal = Invoke-RestMethod -Uri "$BASE_URL/lignes" -Method GET -Headers $headers

Write-Host "Agences restantes: $($agencesFinal.Count)"
Write-Host "Lignes restantes: $($lignesFinal.Count)"

Write-Host ""
Write-Host "Nettoyage termine." -ForegroundColor Green
