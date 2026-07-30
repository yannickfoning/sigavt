# Script de simulation SIGAVT via API PowerShell
$BASE_URL = "http://localhost:8080/api"
$rapport = @()

Write-Host "=== SIMULATION SIGAVT - JOURNEE TYPE ===" -ForegroundColor Cyan
Write-Host ""

# Étape 0: Authentification
Write-Host "ÉTAPE 0 - AUTHENTIFICATION" -ForegroundColor Yellow
try {
    $authResponse = Invoke-RestMethod -Uri "$BASE_URL/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"admin@sigavt.cm","motDePasse":"admin123"}'
    $token = $authResponse.token
    $headers = @{"Authorization" = "Bearer $token"}
    Write-Host "OK - Authentification reussie" -ForegroundColor Green
    $rapport += "SUCCES - Authentification"
} catch {
    Write-Host "ERREUR - Authentification" -ForegroundColor Red
    $rapport += "ECHEC - Authentification"
    exit 1
}

# Étape 1: Dashboard
Write-Host ""
Write-Host "ÉTAPE 1 - DASHBOARD" -ForegroundColor Yellow
try {
    $dashboardResponse = Invoke-RestMethod -Uri "$BASE_URL/dashboard/stats" -Method GET -Headers $headers
    Write-Host "OK - Dashboard accessible" -ForegroundColor Green
    $rapport += "SUCCES - Dashboard"
} catch {
    Write-Host "ERREUR - Dashboard" -ForegroundColor Red
    $rapport += "ECHEC - Dashboard"
}

# Étape 2: Lignes
Write-Host ""
Write-Host "ÉTAPE 2 - LIGNES" -ForegroundColor Yellow
try {
    $nouvelleLigne = '{"villeDepart":"Douala","villeArrivee":"Bafoussam","distanceKm":250,"dureeMinutes":240,"tarifBase":15000,"frequenceJour":2,"statut":"ACTIVE"}'
    $ligneResponse = Invoke-RestMethod -Uri "$BASE_URL/lignes" -Method POST -ContentType "application/json" -Headers $headers -Body $nouvelleLigne
    Write-Host "OK - Ligne creee: $($ligneResponse.villeDepart) vers $($ligneResponse.villeArrivee)" -ForegroundColor Green
    $ligneId = $ligneResponse.id
    $rapport += "SUCCES - Lignes"
} catch {
    Write-Host "ERREUR - Lignes" -ForegroundColor Red
    $rapport += "ECHEC - Lignes"
    $ligneId = $null
}

# Étape 3: Flotte (Bus)
Write-Host ""
Write-Host "ÉTAPE 3 - FLOTTE (BUS)" -ForegroundColor Yellow
try {
    $timestamp = Get-Date -Format "HHmmss"
    $nouveauBus = "{""immatriculation"":""CM-$timestamp-AB"",""modele"":""Mercedes-Benz Sprinter"",""nombrePlaces"":18,""statut"":""OPERATIONNEL""}"
    $busResponse = Invoke-RestMethod -Uri "$BASE_URL/bus" -Method POST -ContentType "application/json" -Headers $headers -Body $nouveauBus
    Write-Host "OK - Bus cree: $($busResponse.immatriculation)" -ForegroundColor Green
    $busId = $busResponse.id
    $rapport += "SUCCES - Bus"
} catch {
    Write-Host "ERREUR - Bus" -ForegroundColor Red
    $rapport += "ECHEC - Bus"
    $busId = $null
}

# Étape 4: Personnel
Write-Host ""
Write-Host "ÉTAPE 4 - PERSONNEL" -ForegroundColor Yellow
try {
    $timestamp = Get-Date -Format "HHmmss"
    $nouveauPersonnel = "{""nomComplet"":""Jean Test$timestamp"",""telephone"":""+237 677 123 456"",""poste"":""CHAUFFEUR"",""salaireBase"":150000,""statut"":""ACTIF"",""busAssigneId"":$busId}"
    $personnelResponse = Invoke-RestMethod -Uri "$BASE_URL/personnel" -Method POST -ContentType "application/json" -Headers $headers -Body $nouveauPersonnel
    Write-Host "OK - Personnel cree: $($personnelResponse.nomComplet)" -ForegroundColor Green
    $personnelId = $personnelResponse.id
    $rapport += "SUCCES - Personnel"
} catch {
    Write-Host "ERREUR - Personnel" -ForegroundColor Red
    $rapport += "ECHEC - Personnel"
    $personnelId = $null
}

# Étape 5: Billets
Write-Host ""
Write-Host "ÉTAPE 5 - BILLETS" -ForegroundColor Yellow
try {
    $dateVoyage = (Get-Date).ToString("yyyy-MM-dd")
    $nouveauVoyage = "{""ligneId"":$ligneId,""busId"":$busId,""chauffeurId"":$personnelId,""dateVoyage"":""$dateVoyage"",""heureDepart"":""08:00"",""placesDisponibles"":18,""statut"":""PLANIFIE""}"
    $voyageResponse = Invoke-RestMethod -Uri "$BASE_URL/voyages" -Method POST -ContentType "application/json" -Headers $headers -Body $nouveauVoyage
    Write-Host "OK - Voyage cree: ID $($voyageResponse.id)" -ForegroundColor Green
    $voyageId = $voyageResponse.id
    
    $nouveauBillet = "{""voyageId"":$voyageId,""passagerNom"":""Paul Passager"",""passagerTelephone"":""+237 699 987 654"",""typeTarif"":""NORMAL"",""prix"":15000,""modePaiement"":""ESPECES"",""statut"":""VALIDE""}"
    $billetResponse = Invoke-RestMethod -Uri "$BASE_URL/billets" -Method POST -ContentType "application/json" -Headers $headers -Body $nouveauBillet
    Write-Host "OK - Billet cree: $($billetResponse.numeroBillet)" -ForegroundColor Green
    $rapport += "SUCCES - Billets"
} catch {
    Write-Host "ERREUR - Billets" -ForegroundColor Red
    $rapport += "ECHEC - Billets"
}

# Étape 6: Colis
Write-Host ""
Write-Host "ÉTAPE 6 - COLIS" -ForegroundColor Yellow
try {
    $nouveauColis = "{""expediteurNom"":""Marie Expediteur"",""expediteurTel"":""+237 677 111 222"",""destinataireNom"":""Pierre Destinataire"",""destinataireTel"":""+237 699 333 444"",""poidsKg"":5.5,""typeColis"":""STANDARD"",""description"":""Livraison documents urgents"",""statut"":""ENREGISTRE""}"
    $colisResponse = Invoke-RestMethod -Uri "$BASE_URL/colis" -Method POST -ContentType "application/json" -Headers $headers -Body $nouveauColis
    Write-Host "OK - Colis cree: $($colisResponse.numeroTracking)" -ForegroundColor Green
    $rapport += "SUCCES - Colis"
} catch {
    Write-Host "ERREUR - Colis" -ForegroundColor Red
    $rapport += "ECHEC - Colis"
}

# Étape 7: Courriers
Write-Host ""
Write-Host "ÉTAPE 7 - COURRIERS" -ForegroundColor Yellow
try {
    $nouveauCourrier = "{""type"":""ENTRANT"",""objet"":""Demande de partenariat"",""expediteur"":""Societe Partenaire SA"",""contenu"":""Nous souhaitons etablir un partenariat"",""statut"":""NON_LU""}"
    $courrierResponse = Invoke-RestMethod -Uri "$BASE_URL/courriers" -Method POST -ContentType "application/json" -Headers $headers -Body $nouveauCourrier
    Write-Host "OK - Courrier cree: $($courrierResponse.objet)" -ForegroundColor Green
    $courrierId = $courrierResponse.id
    
    $courrierUpdate = "{""statut"":""TRAITE""}"
    $courrierUpdateResponse = Invoke-RestMethod -Uri "$BASE_URL/courriers/$courrierId" -Method PUT -ContentType "application/json" -Headers $headers -Body $courrierUpdate
    Write-Host "OK - Courrier marque comme traite" -ForegroundColor Green
    $rapport += "SUCCES - Courriers"
} catch {
    Write-Host "ERREUR - Courriers" -ForegroundColor Red
    $rapport += "ECHEC - Courriers"
}

# Étape 8: Paie
Write-Host ""
Write-Host "ÉTAPE 8 - PAIE" -ForegroundColor Yellow
try {
    $periode = (Get-Date).ToString("yyyy-MM")
    $nouveauBulletin = "{""personnelId"":$personnelId,""periode"":""$periode"",""salaireBase"":150000,""indemniteTransport"":20000,""primeRendement"":10000,""salaireBrut"":180000,""cotisationCnps"":18000,""retenueIrpp"":15000,""netAPayer"":147000,""chargesPatronales"":36000,""coutEmployeur"":216000,""statutPaiement"":""PAYE""}"
    $bulletinResponse = Invoke-RestMethod -Uri "$BASE_URL/paie/bulletins" -Method POST -ContentType "application/json" -Headers $headers -Body $nouveauBulletin
    Write-Host "OK - Bulletin cree: Periode $periode" -ForegroundColor Green
    Write-Host "  CNPS: $($bulletinResponse.cotisationCnps), IRPP: $($bulletinResponse.retenueIrpp)" -ForegroundColor Green
    $rapport += "SUCCES - Paie"
} catch {
    Write-Host "ERREUR - Paie" -ForegroundColor Red
    $rapport += "ECHEC - Paie"
}

# Étape 9: Comptabilite
Write-Host ""
Write-Host "ÉTAPE 9 - COMPTABILITÉ" -ForegroundColor Yellow
try {
    $journalResponse = Invoke-RestMethod -Uri "$BASE_URL/comptabilite/journal" -Method GET -Headers $headers
    Write-Host "OK - Journal consulte: $($journalResponse.content.Count) ecritures" -ForegroundColor Green
    $rapport += "SUCCES - Comptabilite"
} catch {
    Write-Host "ERREUR - Comptabilite" -ForegroundColor Red
    $rapport += "ECHEC - Comptabilite"
}

# Étape 10: Parametres
Write-Host ""
Write-Host "ÉTAPE 10 - PARAMÈTRES" -ForegroundColor Yellow
try {
    $parametresResponse = Invoke-RestMethod -Uri "$BASE_URL/parametres" -Method GET -Headers $headers
    $nouveauTelephone = "+237 677 999 888"
    $parametresUpdate = "{""nomAgence"":""$($parametresResponse.nomAgence)"",""telephone"":""$nouveauTelephone"",""email"":""$($parametresResponse.email)"",""villePrincipale"":""$($parametresResponse.villePrincipale)"",""adresse"":""$($parametresResponse.adresse)""}"
    $parametresUpdateResponse = Invoke-RestMethod -Uri "$BASE_URL/parametres" -Method PUT -ContentType "application/json" -Headers $headers -Body $parametresUpdate
    Write-Host "OK - Parametres mis a jour" -ForegroundColor Green
    $rapport += "SUCCES - Parametres"
} catch {
    Write-Host "ERREUR - Parametres" -ForegroundColor Red
    $rapport += "ECHEC - Parametres"
}

# Étape 11: Verification persistance
Write-Host ""
Write-Host "ÉTAPE 11 - VÉRIFICATION PERSISTANCE" -ForegroundColor Yellow
try {
    $authResponse2 = Invoke-RestMethod -Uri "$BASE_URL/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"admin@sigavt.cm","motDePasse":"admin123"}'
    $token2 = $authResponse2.token
    $headers2 = @{"Authorization" = "Bearer $token2"}
    
    $lignesVerify = Invoke-RestMethod -Uri "$BASE_URL/lignes" -Method GET -Headers $headers2
    $lignePersiste = $lignesVerify.content | Where-Object { $_.id -eq $ligneId }
    
    if ($lignePersiste) {
        Write-Host "OK - Donnees persistees apres reconnexion" -ForegroundColor Green
        $rapport += "SUCCES - Persistance"
    } else {
        Write-Host "ERREUR - Donnees non persistees" -ForegroundColor Red
        $rapport += "ECHEC - Persistance"
    }
} catch {
    Write-Host "ERREUR - Verification persistance" -ForegroundColor Red
    $rapport += "ECHEC - Persistance"
}

# Rapport final
Write-Host ""
Write-Host "=== RAPPORT FINAL ===" -ForegroundColor Cyan
$rapport | ForEach-Object { Write-Host $_ }

$succes = ($rapport | Where-Object { $_ -like "*SUCCES*" }).Count
$echec = ($rapport | Where-Object { $_ -like "*ECHEC*" }).Count

Write-Host ""
Write-Host "STATISTIQUES: $succes SUCCES, $echec ECHEC" -ForegroundColor Cyan

$rapport | Out-File -FilePath "rapport_simulation.txt" -Encoding UTF8
Write-Host "Rapport exporté dans rapport_simulation.txt" -ForegroundColor Green
