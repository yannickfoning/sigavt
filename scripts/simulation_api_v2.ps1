[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$BASE_URL = "http://localhost:8080/api"
$rapport = @()

Write-Output "=== SIMULATION SIGAVT - JOURNEE TYPE ==="
Write-Output ""

# Étape 0: Authentification
Write-Output "ÉTAPE 0 - AUTHENTIFICATION"
try {
    $authResponse = Invoke-RestMethod -Uri "$BASE_URL/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"admin@sigavt.cm","motDePasse":"admin123"}'
    $token = $authResponse.token
    $headers = @{"Authorization" = "Bearer $token"}
    Write-Output "OK - Authentification reussie"
    $rapport += "SUCCES - Authentification"
} catch {
    Write-Output "ERREUR - Authentification: $($_.Exception.Message)"
    $rapport += "ECHEC - Authentification"
    exit 1
}

# Étape 1: Dashboard
Write-Output ""
Write-Output "ÉTAPE 1 - DASHBOARD"
try {
    $dashboardResponse = Invoke-RestMethod -Uri "$BASE_URL/dashboard/stats" -Method GET -Headers $headers
    Write-Output "OK - Dashboard accessible"
    $rapport += "SUCCES - Dashboard"
} catch {
    Write-Output "ERREUR - Dashboard: $($_.Exception.Message)"
    $rapport += "ECHEC - Dashboard"
}

# Étape 2: Lignes
Write-Output ""
Write-Output "ÉTAPE 2 - LIGNES"
try {
    $nouvelleLigne = '{"villeDepart":"Douala","villeArrivee":"Bafoussam","distanceKm":250,"dureeMinutes":240,"tarifBase":15000,"frequenceJour":2,"statut":"ACTIVE"}'
    $ligneResponse = Invoke-RestMethod -Uri "$BASE_URL/lignes" -Method POST -ContentType "application/json" -Headers $headers -Body $nouvelleLigne
    Write-Output "OK - Ligne creee: $($ligneResponse.villeDepart) vers $($ligneResponse.villeArrivee)"
    $ligneId = $ligneResponse.id
    $rapport += "SUCCES - Lignes"
} catch {
    Write-Output "ERREUR - Lignes: $($_.Exception.Message)"
    $rapport += "ECHEC - Lignes"
    $ligneId = $null
}

# Étape 3: Flotte (Bus)
Write-Output ""
Write-Output "ÉTAPE 3 - FLOTTE (BUS)"
try {
    $timestamp = Get-Date -Format "HHmmss"
    $nouveauBus = "{""immatriculation"":""CM-$timestamp-AB"",""modele"":""Mercedes-Benz Sprinter"",""nombrePlaces"":18,""statut"":""OPERATIONNEL""}"
    $busResponse = Invoke-RestMethod -Uri "$BASE_URL/bus" -Method POST -ContentType "application/json" -Headers $headers -Body $nouveauBus
    Write-Output "OK - Bus cree: $($busResponse.immatriculation)"
    $busId = $busResponse.id
    $rapport += "SUCCES - Bus"
} catch {
    Write-Output "ERREUR - Bus: $($_.Exception.Message)"
    $rapport += "ECHEC - Bus"
    $busId = $null
}

# Étape 4: Personnel
Write-Output ""
Write-Output "ÉTAPE 4 - PERSONNEL"
try {
    $timestamp = Get-Date -Format "HHmmss"
    $nouveauPersonnel = "{""nomComplet"":""Jean Test$timestamp"",""telephone"":""+237677123456"",""poste"":""CHAUFFEUR"",""salaireBase"":150000,""statut"":""ACTIF"",""busAssigneId"":$busId}"
    $personnelResponse = Invoke-RestMethod -Uri "$BASE_URL/personnel" -Method POST -ContentType "application/json" -Headers $headers -Body $nouveauPersonnel
    Write-Output "OK - Personnel cree: $($personnelResponse.nomComplet)"
    $personnelId = $personnelResponse.id
    $rapport += "SUCCES - Personnel"
} catch {
    Write-Output "ERREUR - Personnel: $($_.Exception.Message)"
    $rapport += "ECHEC - Personnel"
    $personnelId = $null
}

# Étape 5: Billets
Write-Output ""
Write-Output "ÉTAPE 5 - BILLETS"
try {
    $dateVoyage = (Get-Date).ToString("yyyy-MM-dd")
    $nouveauVoyage = "{""ligneId"":$ligneId,""busId"":$busId,""chauffeurId"":$personnelId,""dateVoyage"":""$dateVoyage"",""heureDepart"":""08:00"",""placesDisponibles"":18,""statut"":""PLANIFIE""}"
    $voyageResponse = Invoke-RestMethod -Uri "$BASE_URL/voyages" -Method POST -ContentType "application/json" -Headers $headers -Body $nouveauVoyage
    Write-Output "OK - Voyage cree: ID $($voyageResponse.id)"
    $voyageId = $voyageResponse.id
    
    # Récupérer les sièges disponibles pour ce voyage
    $siegesResponse = Invoke-RestMethod -Uri "$BASE_URL/voyages/$voyageId/sieges" -Method GET -Headers $headers
    $siegeDisponible = $siegesResponse | Where-Object { $_.statut -eq "LIBRE" } | Select-Object -First 1
    
    if ($siegeDisponible) {
        $nouveauBillet = "{""voyageId"":$voyageId,""siegeId"":$($siegeDisponible.id),""passagerNom"":""Paul Passager"",""passagerTelephone"":""+237699987654"",""typeTarif"":""PLEIN_TARIF_ADULTE"",""modePaiement"":""ESPECES""}"
        $billetResponse = Invoke-RestMethod -Uri "$BASE_URL/billets" -Method POST -ContentType "application/json" -Headers $headers -Body $nouveauBillet
        Write-Output "OK - Billet cree: $($billetResponse.numeroBillet), Siege: $($siegeDisponible.numero)"
        $rapport += "SUCCES - Billets"
    } else {
        Write-Output "ERREUR - Billets: Aucun siege disponible"
        $rapport += "ECHEC - Billets"
    }
} catch {
    Write-Output "ERREUR - Billets: $($_.Exception.Message)"
    Write-Output "Status: $($_.Exception.Response.StatusCode.value__)"
    if ($_.Exception.Response) {
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        $responseBody = $reader.ReadToEnd()
        Write-Output "Response: $responseBody"
    }
    $rapport += "ECHEC - Billets"
}

# Étape 6: Colis
Write-Output ""
Write-Output "ÉTAPE 6 - COLIS"
try {
    $nouveauColis = "{""expediteurNom"":""Marie Expediteur"",""expediteurTelephone"":""+237677111222"",""destinataireNom"":""Pierre Destinataire"",""destinataireTelephone"":""+237699333444"",""villeDepart"":""Douala"",""villeArrivee"":""Yaounde"",""poidsKg"":5.5,""typeColis"":""AUTRE"",""description"":""Livraison documents urgents"",""fragile"":false,""urgent"":false,""assure"":false,""modePaiement"":""ESPECES""}"
    $colisResponse = Invoke-RestMethod -Uri "$BASE_URL/colis" -Method POST -ContentType "application/json" -Headers $headers -Body $nouveauColis
    Write-Output "OK - Colis cree: $($colisResponse.numeroTracking)"
    $rapport += "SUCCES - Colis"
} catch {
    Write-Output "ERREUR - Colis: $($_.Exception.Message)"
    Write-Output "Status: $($_.Exception.Response.StatusCode.value__)"
    if ($_.Exception.Response) {
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        $responseBody = $reader.ReadToEnd()
        Write-Output "Response: $responseBody"
    }
    $rapport += "ECHEC - Colis"
}

# Étape 7: Courriers
Write-Output ""
Write-Output "ÉTAPE 7 - COURRIERS"
try {
    $nouveauCourrier = "{""type"":""ENTRANT"",""objet"":""Demande de partenariat"",""expediteur"":""Societe Partenaire SA"",""contenu"":""Nous souhaitons etablir un partenariat"",""statut"":""NON_LU""}"
    $courrierResponse = Invoke-RestMethod -Uri "$BASE_URL/courriers" -Method POST -ContentType "application/json" -Headers $headers -Body $nouveauCourrier
    Write-Output "OK - Courrier cree: $($courrierResponse.objet)"
    $courrierId = $courrierResponse.id
    
    $courrierUpdate = "{""statut"":""TRAITE""}"
    $courrierUpdateResponse = Invoke-RestMethod -Uri "$BASE_URL/courriers/$courrierId" -Method PUT -ContentType "application/json" -Headers $headers -Body $courrierUpdate
    Write-Output "OK - Courrier marque comme traite"
    $rapport += "SUCCES - Courriers"
} catch {
    Write-Output "ERREUR - Courriers: $($_.Exception.Message)"
    $rapport += "ECHEC - Courriers"
}

# Étape 8: Paie
Write-Output ""
Write-Output "ÉTAPE 8 - PAIE"
try {
    $periode = (Get-Date).ToString("yyyy-MM")
    $nouveauBulletin = "{""personnelId"":$personnelId,""periode"":""$periode"",""indemniteTransport"":20000,""primePerformance"":10000}"
    $bulletinResponse = Invoke-RestMethod -Uri "$BASE_URL/paie/bulletins" -Method POST -ContentType "application/json" -Headers $headers -Body $nouveauBulletin
    Write-Output "OK - Bulletin cree: Periode $periode"
    Write-Output "  CNPS: $($bulletinResponse.cotisationCnps), IRPP: $($bulletinResponse.retenueIrpp)"
    $rapport += "SUCCES - Paie"
} catch {
    Write-Output "ERREUR - Paie: $($_.Exception.Message)"
    $rapport += "ECHEC - Paie"
}

# Étape 9: Comptabilite
Write-Output ""
Write-Output "ÉTAPE 9 - COMPTABILITÉ"
try {
    $journalResponse = Invoke-RestMethod -Uri "$BASE_URL/comptabilite/ecritures" -Method GET -Headers $headers
    Write-Output "OK - Journal consulte: $($journalResponse.Count) ecritures"
    $rapport += "SUCCES - Comptabilite"
} catch {
    Write-Output "ERREUR - Comptabilite: $($_.Exception.Message)"
    $rapport += "ECHEC - Comptabilite"
}

# Étape 10: Parametres
Write-Output ""
Write-Output "ÉTAPE 10 - PARAMÈTRES"
try {
    $parametresResponse = Invoke-RestMethod -Uri "$BASE_URL/parametres" -Method GET -Headers $headers
    $nouveauTelephone = "+237 677 999 888"
    $parametresUpdate = "{""nomAgence"":""$($parametresResponse.nomAgence)"",""telephone"":""$nouveauTelephone"",""email"":""$($parametresResponse.email)"",""villePrincipale"":""$($parametresResponse.villePrincipale)"",""adresse"":""$($parametresResponse.adresse)""}"
    $parametresUpdateResponse = Invoke-RestMethod -Uri "$BASE_URL/parametres" -Method PUT -ContentType "application/json" -Headers $headers -Body $parametresUpdate
    Write-Output "OK - Parametres mis a jour"
    $rapport += "SUCCES - Parametres"
} catch {
    Write-Output "ERREUR - Parametres: $($_.Exception.Message)"
    $rapport += "ECHEC - Parametres"
}

# Étape 11: Verification persistance
Write-Output ""
Write-Output "ÉTAPE 11 - VÉRIFICATION PERSISTANCE"
try {
    $authResponse2 = Invoke-RestMethod -Uri "$BASE_URL/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"admin@sigavt.cm","motDePasse":"admin123"}'
    $token2 = $authResponse2.token
    $headers2 = @{"Authorization" = "Bearer $token2"}
    
    $lignesVerify = Invoke-RestMethod -Uri "$BASE_URL/lignes" -Method GET -Headers $headers2
    $lignePersiste = $lignesVerify.content | Where-Object { $_.id -eq $ligneId }
    
    if ($lignePersiste) {
        Write-Output "OK - Donnees persistees apres reconnexion"
        $rapport += "SUCCES - Persistance"
    } else {
        Write-Output "ERREUR - Donnees non persistees"
        $rapport += "ECHEC - Persistance"
    }
} catch {
    Write-Output "ERREUR - Verification persistance: $($_.Exception.Message)"
    $rapport += "ECHEC - Persistance"
}

# Rapport final
Write-Output ""
Write-Output "=== RAPPORT FINAL ==="
$rapport | ForEach-Object { Write-Output $_ }

$succes = ($rapport | Where-Object { $_ -like "*SUCCES*" }).Count
$echec = ($rapport | Where-Object { $_ -like "*ECHEC*" }).Count

Write-Output ""
Write-Output "STATISTIQUES: $succes SUCCES, $echec ECHEC"

$rapport | Out-File -FilePath "rapport_simulation.txt" -Encoding UTF8
Write-Output "Rapport exporte dans rapport_simulation.txt"
