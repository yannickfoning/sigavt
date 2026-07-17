# Test spécifique pour voyages
$BASE = "http://localhost:8080"

# Login
$loginBody = @{email="admin@sigavt.cm"; motDePasse="admin123"} | ConvertTo-Json
$loginResponse = Invoke-RestMethod -Uri "$BASE/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
$token = $loginResponse.token
$headers = @{"Authorization" = "Bearer $token"}

# Test voyages
Write-Host "Test voyages endpoint:"
try {
    $response = Invoke-RestMethod -Uri "$BASE/api/voyages?page=0&size=5" -Method GET -Headers $headers
    Write-Host "SUCCESS: $($response.content.Count) voyages"
} catch {
    Write-Host "ERROR: $($_.Exception.Message)"
    if ($_.ErrorDetails) {
        Write-Host "Details: $($_.ErrorDetails.Message)"
    }
}
