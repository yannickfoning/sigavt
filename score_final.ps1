# score_final.ps1 — Script de test SIGAVT
$BASE = "http://localhost:8080"
$resultats = @()

function http($url, $method="GET", $data=$null, $token=$null) {
    $headers = @{"Content-Type"="application/json"}
    if ($token) { $headers["Authorization"] = "Bearer $token" }
    
    try {
        $body = if ($data) { $data | ConvertTo-Json } else { $null }
        $response = Invoke-RestMethod -Uri "$BASE$url" -Method $method -Body $body -Headers $headers -ErrorAction Stop
        return @{body=$response; code=200}
    } catch {
        $code = $_.Exception.Response.StatusCode.value__
        try {
            $errorBody = $_.ErrorDetails.Message | ConvertFrom-Json
            return @{body=$errorBody; code=$code}
        } catch {
            return @{body=@{error=$_.Exception.Message}; code=$code}
        }
    }
}

function t($categorie, $nom, $ok, $detail="") {
    $global:resultats += @{categorie=$categorie; nom=$nom; ok=$ok; detail=$detail}
    $icone = if ($ok) { "OK" } else { "FAIL" }
    $suffix = if (-not $ok -and $detail) { "  [$detail]" } else { "" }
    Write-Host "  [$icone] $nom$suffix"
}

# Login
Write-Host "`nAUTH"
$d = http "/api/auth/login" "POST" @{email="admin@sigavt.cm"; motDePasse="admin123"}
$tok = if ($d.body.token) { $d.body.token } elseif ($d.body.accessToken) { $d.body.accessToken } else { $null }
t "Auth" "Login admin OK" ($tok -ne $null -and $d.code -lt 400) "HTTP $($d.code)"

$d = http "/api/auth/login" "POST" @{email="x@x.cm"; motDePasse="faux"}
t "Auth" "Mauvais mdp -> 401" ($d.code -eq 401) "HTTP $($d.code)"

$d = http "/api/dashboard/stats"
t "Auth" "Sans token -> 401" ($d.code -eq 401) "HTTP $($d.code)"

# Securite
Write-Host "`nSECURITE"
$insc = http "/api/auth/inscription" "POST" @{nom="H"; email="h99@t.cm"; motDePasse="p"; role="SUPERADMIN"}
$role_hack = $insc.body.role -eq "SUPERADMIN" -or $insc.body.role -eq "ADMIN"
t "Securite" "Inscription sans escalade de role" (-not $role_hack) "CRITIQUE: role ADMIN accorde publiquement !"

$d = http "/api/billets/999999" "GET" $null $tok
t "Securite" "ID inexistant -> 404" ($d.code -eq 404) "HTTP $($d.code)"

$d = http "/api/colis/tracking/COL-2025-00001"
t "Securite" "Tracking public sans token -> 200" ($d.code -eq 200) "HTTP $($d.code)"

# Pages HTML
Write-Host "`nPAGES"
$pages = @{"/"="Accueil"; "/login"="Login"; "/dashboard"="Dashboard"}
foreach ($path in $pages.Keys) {
    $d = http $path
    t "Pages" "HTTP 200 sur $($pages[$path])" ($d.code -eq 200) "HTTP $($d.code)"
}

# API Metier
Write-Host "`nAPI"
$endpoints = @{
    "/api/dashboard/stats"="Stats dashboard"
    "/api/dashboard/departs"="Departs du jour"
    "/api/dashboard/recettes-semaine"="Recettes semaine"
    "/api/dashboard/top-lignes"="Top lignes"
    "/api/voyages?page=0&size=5"="Liste voyages"
    "/api/billets?page=0&size=5"="Liste billets"
    "/api/colis?page=0&size=5"="Liste colis"
    "/api/employes?page=0&size=5"="Liste employes"
    "/api/paie/stats?mois=5&annee=2025"="Stats paie"
    "/api/paie?mois=5&annee=2025&page=0&size=5"="Liste fiches paie"
    "/api/bus?page=0&size=10"="Liste bus"
    "/api/lignes"="Liste lignes"
    "/api/compta/ecritures?page=0&size=5"="Ecritures"
    "/api/compta/bilan?mois=5&annee=2025"="Bilan mensuel"
    "/api/courriers"="Courriers"
}
foreach ($url in $endpoints.Keys) {
    $d = http $url "GET" $null $tok
    t "API" $endpoints[$url] ($d.code -lt 400) "HTTP $($d.code)"
}

# Pagination
Write-Host "`nPAGINATION"
$pagination_tests = @{
    "/api/billets?page=0&size=5"="Billets"
    "/api/colis?page=0&size=3"="Colis"
    "/api/employes?page=0&size=3"="Employes"
}
foreach ($url in $pagination_tests.Keys) {
    $d = http $url "GET" $null $tok
    $pagine = $d.body.PSObject.Properties.Name -contains "content" -or $d.body.PSObject.Properties.Name -contains "totalElements"
    t "Pagination" "$($pagination_tests[$url]) paginee" $pagine "Manque content/totalElements"
}

# Rapport
$total = $resultats.Count
$passes = ($resultats | Where-Object { $_.ok }).Count
$fails = $total - $passes
$pct = if ($total -gt 0) { [math]::Round(($passes / $total) * 100) } else { 0 }

Write-Host "`n$('='*55)"
Write-Host "  SCORE FINAL : $passes/$total tests ($pct%)"
if ($fails -eq 0) { Write-Host "  PLATEFORME 100% OPERATIONNELLE !" }
elseif ($fails -le 3) { Write-Host "  Tres bon - corriger les derniers points" }
elseif ($fails -le 8) { Write-Host "  Quelques corrections necessaires" }
else { Write-Host "  Corrections importantes requises" }

if ($fails -gt 0) {
    Write-Host "`n  $fails TEST(S) ECHOUE(S) :"
    foreach ($r in $resultats) {
        if (-not $r.ok) {
            $suffix = if ($r.detail) { " -> $($r.detail)" } else { "" }
            Write-Host "    [FAIL] [$($r.categorie)] $($r.nom)$suffix"
        }
    }
}
Write-Host "="*55
