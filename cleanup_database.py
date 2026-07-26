#!/usr/bin/env python3
"""
Script de nettoyage de la base de données via l'API
"""

import requests
import json

BASE_URL = "http://localhost:8080/api"

# Authentification
auth_response = requests.post(f"{BASE_URL}/auth/login", json={
    "email": "admin@sigavt.cm",
    "motDePasse": "admin123"
})

if auth_response.status_code != 200:
    print(f"Erreur d'authentification: {auth_response.status_code}")
    exit(1)

token = auth_response.json()["token"]
headers = {"Authorization": f"Bearer {token}"}

print("=== NETTOYAGE DE LA BASE DE DONNÉES ===\n")

# 1. Nettoyage des agences doublons
print("1. Nettoyage des agences...")
agences_response = requests.get(f"{BASE_URL}/agences", headers=headers)
if agences_response.status_code == 200:
    agences = agences_response.json()
    print(f"   Agences trouvées: {len(agences)}")
    
    if len(agences) > 1:
        # Garder la première agence, supprimer les autres
        premiere_agence = agences[0]
        agences_a_supprimer = agences[1:]
        
        for agence in agences_a_supprimer:
            delete_response = requests.delete(f"{BASE_URL}/agences/{agence['id']}", headers=headers)
            if delete_response.status_code == 204:
                print(f"   ✓ Agence supprimée: {agence['nom']}")
            else:
                print(f"   ✗ Erreur suppression agence {agence['id']}: {delete_response.status_code}")
    else:
        print("   ✓ Pas de doublons d'agences")
else:
    print(f"   ✗ Erreur récupération agences: {agences_response.status_code}")

# 2. Nettoyage des lignes doublons
print("\n2. Nettoyage des lignes...")
lignes_response = requests.get(f"{BASE_URL}/lignes", headers=headers)
if lignes_response.status_code == 200:
    lignes = lignes_response.json()
    print(f"   Lignes trouvées: {len(lignes)}")
    
    # Identifier les doublons (même ville_depart et ville_arrivee)
    lignes_uniques = {}
    lignes_doublons = []
    
    for ligne in lignes:
        cle = (ligne['villeDepart'], ligne['villeArrivee'])
        if cle in lignes_uniques:
            lignes_doublons.append(ligne)
        else:
            lignes_uniques[cle] = ligne
    
    print(f"   Lignes uniques: {len(lignes_uniques)}")
    print(f"   Lignes doublons: {len(lignes_doublons)}")
    
    # Supprimer les doublons
    for ligne in lignes_doublons:
        delete_response = requests.delete(f"{BASE_URL}/lignes/{ligne['id']}", headers=headers)
        if delete_response.status_code == 204:
            print(f"   ✓ Ligne supprimée: {ligne['villeDepart']} → {ligne['villeArrivee']}")
        else:
            print(f"   ✗ Erreur suppression ligne {ligne['id']}: {delete_response.status_code}")
else:
    print(f"   ✗ Erreur récupération lignes: {lignes_response.status_code}")

# Vérification finale
print("\n=== VÉRIFICATION FINALE ===")
agences_final = requests.get(f"{BASE_URL}/agences", headers=headers).json()
lignes_final = requests.get(f"{BASE_URL}/lignes", headers=headers).json()

print(f"Agences restantes: {len(agences_final)}")
print(f"Lignes restantes: {len(lignes_final)}")

print("\nNettoyage terminé.")
