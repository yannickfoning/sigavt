"""
Test complet de toutes les fonctionnalités API de SIGAVT
Couvre tous les modules: auth, agences, lignes, bus, personnel, voyages, billets, colis, courriers, paie, comptabilite
"""
import requests
import json
import time

BASE_URL = "http://localhost:8081/api"

class APITester:
    def __init__(self):
        self.token = None
        self.headers = {}
        
    def login(self):
        """Test de connexion"""
        print("\n=== Test Authentification ===")
        try:
            response = requests.post(f"{BASE_URL}/auth/login", json={
                "email": "admin@sigavt.cm",
                "motDePasse": "admin123"
            })
            if response.status_code == 200:
                data = response.json()
                self.token = data.get("token")
                self.headers = {"Authorization": f"Bearer {self.token}"}
                print("✓ Connexion réussie")
                print(f"  Token: {self.token[:50]}...")
                return True
            else:
                print(f"✗ Connexion échouée: {response.status_code}")
                print(f"  Response: {response.text}")
                return False
        except Exception as e:
            print(f"✗ Erreur de connexion: {e}")
            return False
    
    def test_agences(self):
        """Test module Agences"""
        print("\n=== Test Module Agences ===")
        try:
            # GET all
            response = requests.get(f"{BASE_URL}/agences", headers=self.headers)
            print(f"GET /agences: {response.status_code}")
            if response.status_code == 200:
                agences = response.json()
                print(f"  ✓ {len(agences)} agences trouvées")
                if agences:
                    print(f"  Exemple: {agences[0]['nom']}")
            
            # POST (si endpoint existe)
            try:
                response = requests.post(f"{BASE_URL}/agences", headers=self.headers, json={
                    "nom": "Agence Test",
                    "ville": "Testville",
                    "adresse": "Adresse Test",
                    "telephone": "+237 600 000 000",
                    "email": "test@test.com"
                })
                print(f"POST /agences: {response.status_code}")
            except:
                print("  POST non testé")
                
        except Exception as e:
            print(f"✗ Erreur agences: {e}")
    
    def test_lignes(self):
        """Test module Lignes"""
        print("\n=== Test Module Lignes ===")
        try:
            response = requests.get(f"{BASE_URL}/lignes", headers=self.headers)
            print(f"GET /lignes: {response.status_code}")
            if response.status_code == 200:
                lignes = response.json()
                print(f"  ✓ {len(lignes)} lignes trouvées")
                if lignes:
                    print(f"  Exemple: {lignes[0]['villeDepart']} -> {lignes[0]['villeArrivee']}")
        except Exception as e:
            print(f"✗ Erreur lignes: {e}")
    
    def test_bus(self):
        """Test module Bus"""
        print("\n=== Test Module Bus ===")
        try:
            response = requests.get(f"{BASE_URL}/bus", headers=self.headers)
            print(f"GET /bus: {response.status_code}")
            if response.status_code == 200:
                bus = response.json()
                print(f"  ✓ {len(bus)} bus trouvés")
                if bus:
                    print(f"  Exemple: {bus[0]['immatriculation']} - {bus[0]['modele']}")
        except Exception as e:
            print(f"✗ Erreur bus: {e}")
    
    def test_personnel(self):
        """Test module Personnel"""
        print("\n=== Test Module Personnel ===")
        try:
            response = requests.get(f"{BASE_URL}/personnel", headers=self.headers)
            print(f"GET /personnel: {response.status_code}")
            if response.status_code == 200:
                personnel = response.json()
                print(f"  ✓ {len(personnel)} membres du personnel trouvés")
                if personnel:
                    print(f"  Exemple: {personnel[0]['nomComplet']} - {personnel[0]['poste']}")
        except Exception as e:
            print(f"✗ Erreur personnel: {e}")
    
    def test_voyages(self):
        """Test module Voyages"""
        print("\n=== Test Module Voyages ===")
        try:
            response = requests.get(f"{BASE_URL}/voyages", headers=self.headers)
            print(f"GET /voyages: {response.status_code}")
            if response.status_code == 200:
                voyages = response.json()
                print(f"  ✓ {len(voyages)} voyages trouvés")
                if voyages:
                    print(f"  Exemple: Voyage ID {voyages[0]['id']}")
        except Exception as e:
            print(f"✗ Erreur voyages: {e}")
    
    def test_billets(self):
        """Test module Billets"""
        print("\n=== Test Module Billets ===")
        try:
            response = requests.get(f"{BASE_URL}/billets", headers=self.headers)
            print(f"GET /billets: {response.status_code}")
            if response.status_code == 200:
                billets = response.json()
                print(f"  ✓ {len(billets)} billets trouvés")
                if billets:
                    print(f"  Exemple: Billet {billets[0]['numeroBillet']}")
        except Exception as e:
            print(f"✗ Erreur billets: {e}")
    
    def test_colis(self):
        """Test module Colis"""
        print("\n=== Test Module Colis ===")
        try:
            response = requests.get(f"{BASE_URL}/colis", headers=self.headers)
            print(f"GET /colis: {response.status_code}")
            if response.status_code == 200:
                colis = response.json()
                print(f"  ✓ {len(colis)} colis trouvés")
                if colis:
                    print(f"  Exemple: {colis[0]['numeroTracking']}")
        except Exception as e:
            print(f"✗ Erreur colis: {e}")
    
    def test_courriers(self):
        """Test module Courriers"""
        print("\n=== Test Module Courriers ===")
        try:
            response = requests.get(f"{BASE_URL}/courriers", headers=self.headers)
            print(f"GET /courriers: {response.status_code}")
            if response.status_code == 200:
                courriers = response.json()
                print(f"  ✓ {len(courriers)} courriers trouvés")
                if courriers:
                    print(f"  Exemple: {courriers[0]['objet']}")
        except Exception as e:
            print(f"✗ Erreur courriers: {e}")
    
    def test_paie(self):
        """Test module Paie"""
        print("\n=== Test Module Paie ===")
        try:
            response = requests.get(f"{BASE_URL}/paie/bulletins", headers=self.headers)
            print(f"GET /paie/bulletins: {response.status_code}")
            if response.status_code == 200:
                bulletins = response.json()
                print(f"  ✓ {len(bulletins)} bulletins trouvés")
        except Exception as e:
            print(f"✗ Erreur paie: {e}")
    
    def test_comptabilite(self):
        """Test module Comptabilité"""
        print("\n=== Test Module Comptabilité ===")
        try:
            response = requests.get(f"{BASE_URL}/comptabilite/ecritures", headers=self.headers)
            print(f"GET /comptabilite/ecritures: {response.status_code}")
            if response.status_code == 200:
                ecritures = response.json()
                print(f"  ✓ {len(ecritures)} écritures comptables trouvées")
        except Exception as e:
            print(f"✗ Erreur comptabilite: {e}")
    
    def test_dashboard(self):
        """Test Dashboard"""
        print("\n=== Test Dashboard ===")
        try:
            response = requests.get(f"{BASE_URL}/dashboard", headers=self.headers)
            print(f"GET /dashboard: {response.status_code}")
            if response.status_code == 200:
                dashboard = response.json()
                print(f"  ✓ Dashboard accessible")
                print(f"  Données: {list(dashboard.keys())}")
        except Exception as e:
            print(f"✗ Erreur dashboard: {e}")
    
    def run_all_tests(self):
        """Exécuter tous les tests"""
        print("=== DÉBUT DES TESTS API COMPLETS ===")
        
        if not self.login():
            print("Impossible de se connecter. Arrêt des tests.")
            return
        
        self.test_agences()
        self.test_lignes()
        self.test_bus()
        self.test_personnel()
        self.test_voyages()
        self.test_billets()
        self.test_colis()
        self.test_courriers()
        self.test_paie()
        self.test_comptabilite()
        self.test_dashboard()
        
        print("\n=== FIN DES TESTS API ===")

if __name__ == "__main__":
    tester = APITester()
    tester.run_all_tests()
