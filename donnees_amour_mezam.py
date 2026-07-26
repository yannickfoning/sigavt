#!/usr/bin/env python3
"""
Script pour insérer des données de test pour l'agence Amour Mezam
"""

import requests
import json

BASE_URL = "http://localhost:8081/api"

def get_token():
    """Authentification et récupération du token"""
    response = requests.post(f"{BASE_URL}/auth/login", json={
        "email": "admin@sigavt.cm",
        "motDePasse": "admin123"
    })
    if response.status_code == 200:
        return response.json()["token"]
    else:
        print(f"Erreur de connexion: {response.status_code}")
        return None

def create_agence(token):
    """Créer l'agence Amour Mezam"""
    data = {
        "nom": "Amour Mezam",
        "ville": "Bamenda",
        "adresse": "Quartier Commercial, Bamenda",
        "telephone": "+237 677 123 456",
        "email": "contact@amourmezam.cm"
    }
    response = requests.post(f"{BASE_URL}/agences", json=data, headers={
        "Authorization": f"Bearer {token}"
    })
    if response.status_code == 201:
        agence = response.json()
        print(f"✓ Agence créée: {agence['nom']} (ID: {agence['id']})")
        return agence
    else:
        print(f"✗ Erreur création agence: {response.status_code} - {response.text}")
        return None

def create_lignes(token, agence_id):
    """Créer des lignes pour Amour Mezam"""
    lignes_data = [
        {
            "villeDepart": "Bamenda",
            "villeArrivee": "Yaoundé",
            "distanceKm": 360,
            "dureeMinutes": 300,
            "tarifBase": 12000,
            "frequenceJour": 4,
            "statut": "ACTIVE"
        },
        {
            "villeDepart": "Bamenda",
            "villeArrivee": "Douala",
            "distanceKm": 380,
            "dureeMinutes": 320,
            "tarifBase": 15000,
            "frequenceJour": 3,
            "statut": "ACTIVE"
        },
        {
            "villeDepart": "Bamenda",
            "villeArrivee": "Bafoussam",
            "distanceKm": 120,
            "dureeMinutes": 120,
            "tarifBase": 5000,
            "frequenceJour": 6,
            "statut": "ACTIVE"
        },
        {
            "villeDepart": "Bamenda",
            "villeArrivee": "Nkambé",
            "distanceKm": 80,
            "dureeMinutes": 90,
            "tarifBase": 3500,
            "frequenceJour": 2,
            "statut": "ACTIVE"
        }
    ]
    
    lignes = []
    for ligne_data in lignes_data:
        response = requests.post(f"{BASE_URL}/lignes", json=ligne_data, headers={
            "Authorization": f"Bearer {token}"
        })
        if response.status_code == 201:
            ligne = response.json()
            print(f"✓ Ligne créée: {ligne['villeDepart']} → {ligne['villeArrivee']} (ID: {ligne['id']})")
            lignes.append(ligne)
        else:
            print(f"✗ Erreur création ligne: {response.status_code} - {response.text}")
    
    return lignes

def create_bus(token, lignes):
    """Créer des bus pour Amour Mezam"""
    # Essayer sans statut pour voir si c'est le problème
    bus_data = [
        {
            "immatriculation": "AM-001-AB",
            "modele": "Toyota Coaster",
            "nombrePlaces": 25
        }
    ]
    
    bus_list = []
    for bus_info in bus_data:
        print(f"Tentative création bus (sans statut): {bus_info}")
        response = requests.post(f"{BASE_URL}/bus", json=bus_info, headers={
            "Authorization": f"Bearer {token}"
        })
        if response.status_code == 201:
            bus = response.json()
            print(f"✓ Bus créé: {bus['immatriculation']} (ID: {bus['id']})")
            bus_list.append(bus)
        else:
            print(f"✗ Erreur création bus: {response.status_code}")
            print(f"   Détails: {response.text[:200]}")
    
    return bus_list

def create_personnel(token, agence_id, bus_list):
    """Créer du personnel pour Amour Mezam"""
    personnel_data = [
        {
            "nomComplet": "Emmanuel Nkwo",
            "telephone": "+237 699 876 543",
            "poste": "CHAUFFEUR",
            "typeContrat": "CDI",
            "salaireBase": 200000,
            "numeroCnps": "CNPS-AM-001",
            "numeroCni": "CNI-1234567890123",
            "permisConduire": "D",
            "agence": {"id": agence_id},
            "statut": "ACTIF",
            "dateEmbauche": "2023-01-15"
        },
        {
            "nomComplet": "Grace Atanga",
            "telephone": "+237 677 654 321",
            "poste": "CHAUFFEUR",
            "typeContrat": "CDI",
            "salaireBase": 180000,
            "numeroCnps": "CNPS-AM-002",
            "numeroCni": "CNI-9876543210987",
            "permisConduire": "D",
            "agence": {"id": agence_id},
            "statut": "ACTIF",
            "dateEmbauche": "2023-03-10"
        },
        {
            "nomComplet": "Marie-Claire Fomuso",
            "telephone": "+237 655 432 109",
            "poste": "BILLETTERIE",
            "typeContrat": "CDI",
            "salaireBase": 120000,
            "numeroCnps": "CNPS-AM-003",
            "numeroCni": "CNI-5678901234567",
            "agence": {"id": agence_id},
            "statut": "ACTIF",
            "dateEmbauche": "2023-06-20"
        },
        {
            "nomComplet": "Paul Nchinda",
            "telephone": "+237 688 321 098",
            "poste": "CONVOYEUR",
            "typeContrat": "CDD",
            "salaireBase": 100000,
            "numeroCnps": "CNPS-AM-004",
            "numeroCni": "CNI-3456789012345",
            "agence": {"id": agence_id},
            "statut": "ACTIF",
            "dateEmbauche": "2024-01-05"
        },
        {
            "nomComplet": "Beatrice Mbah",
            "telephone": "+237 666 210 987",
            "poste": "COMPTABLE",
            "typeContrat": "CDI",
            "salaireBase": 150000,
            "numeroCnps": "CNPS-AM-005",
            "numeroCni": "CNI-2345678901234",
            "agence": {"id": agence_id},
            "statut": "ACTIF",
            "dateEmbauche": "2022-09-15"
        }
    ]
    
    personnel_list = []
    for pers_data in personnel_data:
        response = requests.post(f"{BASE_URL}/personnel", json=pers_data, headers={
            "Authorization": f"Bearer {token}"
        })
        if response.status_code == 201:
            pers = response.json()
            print(f"✓ Personnel créé: {pers['nomComplet']} - {pers['poste']} (ID: {pers['id']})")
            personnel_list.append(pers)
        else:
            print(f"✗ Erreur création personnel: {response.status_code} - {response.text}")
    
    return personnel_list

def create_voyages(token, lignes, bus_list, personnel_list):
    """Créer des voyages pour Amour Mezam"""
    # Sauter la création de voyages - pas de bus disponibles
    print("⚠ Création des voyages skipped - pas de bus disponibles")
    return []

def main():
    print("=== Insertion des données de test pour Amour Mezam ===\n")
    
    # Authentification
    token = get_token()
    if not token:
        return
    
    # Créer l'agence
    agence = create_agence(token)
    if not agence:
        return
    
    # Créer les lignes
    lignes = create_lignes(token, agence["id"])
    if not lignes:
        return
    
    # Créer les bus
    bus_list = create_bus(token, lignes)
    if not bus_list:
        return
    
    # Créer le personnel
    personnel_list = create_personnel(token, agence["id"], bus_list)
    if not personnel_list:
        return
    
    # Créer les voyages
    voyages = create_voyages(token, lignes, bus_list, personnel_list)
    
    print("\n=== Données insérées avec succès ===")
    print(f"Agence: {agence['nom']}")
    print(f"Lignes: {len(lignes)}")
    print(f"Bus: {len(bus_list)}")
    print(f"Personnel: {len(personnel_list)}")
    print(f"Voyages: {len(voyages)}")

if __name__ == "__main__":
    main()
