# Test simple
Write-Host "Test simple"
$BASE_URL = "http://localhost:8080/api"
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"admin@sigavt.cm","motDePasse":"admin123"}'
    Write-Host "Token: $($response.token)"
} catch {
    Write-Host "Erreur: $($_.Exception.Message)"
}
