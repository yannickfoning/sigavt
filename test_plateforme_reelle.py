"""
Test complet de la plateforme SIGAVT avec données réelles (pas de démo)
Teste la création initiale et le CRUD complet de chaque module
"""
import requests
import json
import time

BASE_URL = "http://localhost:8081/api"

class RealDataTester:
    def __init__(self):
        self.token = None
        self.headers = {}
        self.created_ids = {}
        
    def login(self):
        """Test de connexion avec admin par défaut"""
        print("\n=== Test Connexion ===")
        try:
            # Essayer de se connecter avec admin par défaut
            response = requests.post(f"{BASE_URL}/auth/login", json={
                "email": "admin@sigavt.cm",
                "motDePasse": "admin123"
            })
            if response.status_code == 200:
                data = response.json()
                self.token = data.get("token")
                self.headers = {"Authorization": f"Bearer {self.token}"}
                print("✓ Connexion admin réussie")
                return True
            else:
                print(f"✗ Connexion admin échouée: {response.status_code}")
                print(f"  Response: {response.text}")
                return False
        except Exception as e:
            print(f"✗ Erreur de connexion: {e}")
            return False
    
    def test_initial_setup(self):
        """Tester l'état initial de la base (vide)"""
        print("\n=== Test État Initial (Base Vide) ===")
        
        endpoints = [
            ("agences", "Agences"),
            ("lignes", "Lignes"),
            ("bus", "Bus"),
            ("personnel", "Personnel"),
            ("voyages", "Voyages"),
            ("billets", "Billets"),
            ("colis", "Colis"),
            ("courriers", "Courriers")
        ]
        
        for endpoint, name in endpoints:
            try:
                response = requests.get(f"{BASE_URL}/{endpoint}", headers=self.headers)
                if response.status_code == 200:
                    data = response.json()
                    count = len(data) if isinstance(data, list) else 0
                    print(f"  {name}: {count} enregistrements")
                else:
                    print(f"  {name}: Erreur {response.status_code}")
            except Exception as e:
                print(f"  {name}: Erreur {e}")
    
    def test_create_agence(self):
        """Créer une agence"""
        print("\n=== Test Création Agence ===")
        try:
            agence_data = {
                "nom": "Agence Voyage CM - Yaoundé",
                "ville": "Yaoundé",
                "adresse": "Carrefour Nlongkak, Yaoundé",
                "telephone": "+237 677 000 111",
                "email": "contact@sigavt.cm"
            }
            response = requests.post(f"{BASE_URL}/agences", headers=self.headers, json=agence_data)
            print(f"POST /agences: {response.status_code}")
            if response.status_code in [200, 201]:
                agence = response.json()
                self.created_ids['agence'] = agence.get('id')
                print(f"✓ Agence créée: {agence['nom']} (ID: {agence['id']})")
                return agence
            else:
                print(f"✗ Erreur création agence: {response.text}")
                return None
        except Exception as e:
            print(f"✗ Erreur création agence: {e}")
            return None
    
    def test_create_ligne(self, agence_id):
        """Créer une ligne de transport"""
        print("\n=== Test Création Ligne ===")
        try:
            ligne_data = {
                "villeDepart": "Yaoundé",
                "villeArrivee": "Douala",
                "distanceKm": 240,
                "dureeMinutes": 240,
                "tarifBase": 8000,
                "frequenceJournaliere": 8,
                "statut": "ACTIVE"
            }
            response = requests.post(f"{BASE_URL}/lignes", headers=self.headers, json=ligne_data)
            print(f"POST /lignes: {response.status_code}")
            if response.status_code in [200, 201]:
                ligne = response.json()
                self.created_ids['ligne'] = ligne.get('id')
                print(f"✓ Ligne créée: {ligne['villeDepart']} -> {ligne['villeArrivee']} (ID: {ligne['id']})")
                return ligne
            else:
                print(f"✗ Erreur création ligne: {response.text}")
                return None
        except Exception as e:
            print(f"✗ Erreur création ligne: {e}")
            return None
    
    def test_create_bus(self, agence_id, ligne_id):
        """Créer un bus"""
        print("\n=== Test Création Bus ===")
        try:
            import random
            bus_num = random.randint(100, 999)
            bus_data = {
                "immatriculation": f"LT-{bus_num}-AB",
                "modele": "Toyota Coaster",
                "nombrePlaces": 24,
                "statut": "OPERATIONNEL",
                "prochainEntretien": "2024-08-25",
                "assuranceExpiration": "2025-01-25"
            }
            response = requests.post(f"{BASE_URL}/bus", headers=self.headers, json=bus_data)
            print(f"POST /bus: {response.status_code}")
            if response.status_code in [200, 201]:
                bus = response.json()
                self.created_ids['bus'] = bus.get('id')
                print(f"✓ Bus créé: {bus['immatriculation']} - {bus['modele']} (ID: {bus['id']})")
                return bus
            else:
                print(f"✗ Erreur création bus: {response.text}")
                return None
        except Exception as e:
            print(f"✗ Erreur création bus: {e}")
            return None
    
    def test_create_personnel(self, agence_id, bus_id):
        """Créer un personnel"""
        print("\n=== Test Création Personnel ===")
        try:
            personnel_data = {
                "nomComplet": "Jean Pierre Mbarga",
                "telephone": "+237 699 111 222",
                "email": "jp.mbarga@sigavt.cm",
                "poste": "CHAUFFEUR",
                "typeContrat": "CDI",
                "salaireBase": 180000,
                "numeroCnps": "CNPS-001",
                "numeroCni": "CNI-1234567890",
                "permisConduire": "D",
                "statut": "ACTIF",
                "dateEmbauche": "2023-01-15"
            }
            response = requests.post(f"{BASE_URL}/personnel", headers=self.headers, json=personnel_data)
            print(f"POST /personnel: {response.status_code}")
            if response.status_code in [200, 201]:
                personnel = response.json()
                self.created_ids['personnel'] = personnel.get('id')
                print(f"✓ Personnel créé: {personnel['nomComplet']} - {personnel['poste']} (ID: {personnel['id']})")
                return personnel
            else:
                print(f"✗ Erreur création personnel: {response.text}")
                return None
        except Exception as e:
            print(f"✗ Erreur création personnel: {e}")
            return None
    
    def test_create_voyage(self, bus_id, ligne_id, chauffeur_id):
        """Créer un voyage"""
        print("\n=== Test Création Voyage ===")
        try:
            voyage_data = {
                "ligneId": ligne_id,
                "busId": bus_id,
                "chauffeurId": chauffeur_id,
                "dateVoyage": "2024-07-27",
                "heureDepart": "08:00",
                "statut": "PLANIFIE",
                "tarif": 8000,
                "placesDisponibles": 24
            }
            response = requests.post(f"{BASE_URL}/voyages", headers=self.headers, json=voyage_data)
            print(f"POST /voyages: {response.status_code}")
            if response.status_code in [200, 201]:
                voyage = response.json()
                self.created_ids['voyage'] = voyage.get('id')
                print(f"✓ Voyage créé (ID: {voyage['id']})")
                return voyage
            else:
                print(f"✗ Erreur création voyage: {response.text}")
                return None
        except Exception as e:
            print(f"✗ Erreur création voyage: {e}")
            return None
    
    def test_create_billet(self, voyage_id):
        """Créer un billet"""
        print("\n=== Test Création Billet ===")
        try:
            billet_data = {
                "numeroBillet": "BIL-001",
                "passagerNom": "Paul Nkodo",
                "passagerTelephone": "+237677123456",
                "voyageId": voyage_id,
                "tarifPaye": 8000,
                "modePaiement": "ESPECES",
                "typeTarif": "PLEIN_TARIF_ADULTE"
            }
            response = requests.post(f"{BASE_URL}/billets", headers=self.headers, json=billet_data)
            print(f"POST /billets: {response.status_code}")
            if response.status_code in [200, 201]:
                billet = response.json()
                self.created_ids['billet'] = billet.get('id')
                print(f"✓ Billet créé: {billet['numeroBillet']} (ID: {billet['id']})")
                return billet
            else:
                print(f"✗ Erreur création billet: {response.text}")
                return None
        except Exception as e:
            print(f"✗ Erreur création billet: {e}")
            return None
    
    def test_create_colis(self):
        """Créer un colis"""
        print("\n=== Test Création Colis ===")
        try:
            colis_data = {
                "numeroTracking": "COL-001",
                "expediteurNom": "Marie Kouam",
                "expediteurTelephone": "+237677987654",
                "destinataireNom": "Pierre Tchoumi",
                "destinataireTelephone": "+237677555333",
                "villeDepart": "Yaoundé",
                "villeArrivee": "Douala",
                "poidsKg": 15.5,
                "tarif": 5000,
                "statut": "EN_TRANSIT",
                "modePaiement": "ESPECES"
            }
            response = requests.post(f"{BASE_URL}/colis", headers=self.headers, json=colis_data)
            print(f"POST /colis: {response.status_code}")
            if response.status_code in [200, 201]:
                colis = response.json()
                self.created_ids['colis'] = colis.get('id')
                print(f"✓ Colis créé: {colis['numeroTracking']} (ID: {colis['id']})")
                return colis
            else:
                print(f"✗ Erreur création colis: {response.text}")
                return None
        except Exception as e:
            print(f"✗ Erreur création colis: {e}")
            return None
    
    def test_read_operations(self):
        """Tester les opérations de lecture"""
        print("\n=== Test Opérations Lecture (GET) ===")
        
        endpoints = [
            ("agences", "Agences"),
            ("lignes", "Lignes"),
            ("bus", "Bus"),
            ("personnel", "Personnel"),
            ("voyages", "Voyages"),
            ("billets", "Billets"),
            ("colis", "Colis")
        ]
        
        for endpoint, name in endpoints:
            try:
                response = requests.get(f"{BASE_URL}/{endpoint}", headers=self.headers)
                print(f"  GET /{endpoint}: {response.status_code}")
                if response.status_code == 200:
                    data = response.json()
                    if isinstance(data, dict) and 'content' in data:
                        count = len(data['content'])
                        total = data.get('totalElements', count)
                        print(f"    ✓ Page: {count} enregistrements (Total: {total})")
                        if count > 0:
                            print(f"    Premier ID: {data['content'][0].get('id')}")
                    elif isinstance(data, list):
                        count = len(data)
                        print(f"    ✓ ({count} enregistrements)")
                        if count > 0:
                            print(f"    Premier ID: {data[0].get('id')}")
                    else:
                        print(f"    Response type: {type(data)}")
                else:
                    print(f"    ✗ Error: {response.text[:200]}")
            except Exception as e:
                print(f"  GET /{endpoint}: ✗ ({e})")
        
        # Test GET by ID
        print("\n=== Test GET by ID ===")
        if 'agence' in self.created_ids:
            try:
                response = requests.get(f"{BASE_URL}/agences/{self.created_ids['agence']}", headers=self.headers)
                print(f"  GET /agences/{self.created_ids['agence']}: {response.status_code}")
                if response.status_code == 200:
                    print(f"    ✓ Données récupérées: {response.json()['nom']}")
            except Exception as e:
                print(f"  GET by ID: ✗ ({e})")
    
    def test_update_operations(self):
        """Tester les opérations de mise à jour"""
        print("\n=== Test Opérations Mise à Jour (PUT) ===")
        
        if 'agence' in self.created_ids:
            try:
                update_data = {
                    "nom": "Agence Voyage CM - Yaoundé (Modifié)",
                    "ville": "Yaoundé",
                    "adresse": "Carrefour Nlongkak, Yaoundé",
                    "telephone": "+237 677 000 111",
                    "email": "contact@sigavt.cm"
                }
                response = requests.put(f"{BASE_URL}/agences/{self.created_ids['agence']}", 
                                      headers=self.headers, json=update_data)
                print(f"  PUT /agences/{self.created_ids['agence']}: {response.status_code}")
                if response.status_code in [200, 201]:
                    print("    ✓ Mise à jour agence réussie")
                else:
                    print(f"    ✗ Erreur: {response.text}")
            except Exception as e:
                print(f"  PUT /agences: ✗ ({e})")
    
    def test_delete_operations(self):
        """Tester les opérations de suppression"""
        print("\n=== Test Opérations Suppression (DELETE) ===")
        
        # Supprimer dans l'ordre inverse des dépendances
        if 'colis' in self.created_ids:
            try:
                response = requests.delete(f"{BASE_URL}/colis/{self.created_ids['colis']}", 
                                         headers=self.headers)
                print(f"  DELETE /colis/{self.created_ids['colis']}: {response.status_code}")
                if response.status_code == 204:
                    print("    ✓ Suppression colis réussie")
                else:
                    print(f"    ✗ Erreur: {response.text[:200]}")
            except Exception as e:
                print(f"  DELETE /colis: ✗ ({e})")
        
        if 'billet' in self.created_ids:
            try:
                response = requests.delete(f"{BASE_URL}/billets/{self.created_ids['billet']}", 
                                         headers=self.headers)
                print(f"  DELETE /billets/{self.created_ids['billet']}: {response.status_code}")
                if response.status_code == 204:
                    print("    ✓ Suppression billet réussie")
                else:
                    print(f"    ✗ Erreur: {response.text[:200]}")
            except Exception as e:
                print(f"  DELETE /billets: ✗ ({e})")
        
        if 'voyage' in self.created_ids:
            try:
                response = requests.delete(f"{BASE_URL}/voyages/{self.created_ids['voyage']}", 
                                         headers=self.headers)
                print(f"  DELETE /voyages/{self.created_ids['voyage']}: {response.status_code}")
                if response.status_code == 204:
                    print("    ✓ Suppression voyage réussie")
                else:
                    print(f"    ✗ Erreur: {response.text[:200]}")
            except Exception as e:
                print(f"  DELETE /voyages: ✗ ({e})")
    
    def run_complete_test(self):
        """Exécuter le test complet"""
        print("=== TEST COMPLET PLATEFORME SIGAVT (DONNÉES RÉELLES) ===")
        
        if not self.login():
            print("Impossible de se connecter. Arrêt des tests.")
            return
        
        self.test_initial_setup()
        
        # Création des données
        agence = self.test_create_agence()
        if agence:
            ligne = self.test_create_ligne(agence.get('id'))
            if ligne:
                bus = self.test_create_bus(agence.get('id'), ligne.get('id'))
                if bus:
                    personnel = self.test_create_personnel(agence.get('id'), bus.get('id'))
                    if personnel:
                        voyage = self.test_create_voyage(bus.get('id'), ligne.get('id'), personnel.get('id'))
                        if voyage:
                            self.test_create_billet(voyage.get('id'))
            
            self.test_create_colis()
        
        # Test CRUD
        self.test_read_operations()
        self.test_update_operations()
        self.test_delete_operations()
        
        print("\n=== FIN DES TESTS ===")
        print(f"\nIDs créés: {self.created_ids}")

if __name__ == "__main__":
    tester = RealDataTester()
    tester.run_complete_test()
