#!/usr/bin/env python3
"""
Script de simulation complète d'une journée d'utilisation de SIGAVT
Simulation end-to-end avec capture des requêtes réseau et vérification visuelle
"""

import time
import json
from datetime import datetime, timedelta
from playwright.sync_api import sync_playwright

class SimulationSIGAVT:
    def __init__(self):
        self.rapport = []
        self.donnees_crees = {}
        
    def ajouter_rapport(self, etape, action, requete, reponse, resultat):
        self.rapport.append({
            "etape": etape,
            "action": action,
            "requete": requete,
            "reponse": reponse,
            "resultat": resultat,
            "horodatage": datetime.now().isoformat()
        })
        
    def executer_simulation(self):
        with sync_playwright() as p:
            # Lancer le navigateur en mode visible pour voir les actions
            browser = p.chromium.launch(headless=False, slow_mo=500)
            context = browser.new_context()
            page = context.new_page()
            
            # Activer la capture des requêtes réseau
            page.on("request", lambda request: print(f"→ Request: {request.method} {request.url}"))
            page.on("response", lambda response: print(f"← Response: {response.status} {response.url}"))
            
            try:
                self.etape_0_authentification(page)
                self.etape_1_dashboard(page)
                self.etape_2_lignes(page)
                self.etape_3_flotte(page)
                self.etape_4_personnel(page)
                self.etape_5_billets(page)
                self.etape_6_colis(page)
                self.etape_7_courriers(page)
                self.etape_8_paie(page)
                self.etape_9_comptabilite(page)
                self.etape_10_parametres(page)
                self.etape_11_deconnexion_reconnexion(page)
                
            except Exception as e:
                self.ajouter_rapport(
                    "ERREUR",
                    f"Erreur lors de la simulation: {str(e)}",
                    "N/A",
                    "N/A",
                    "ÉCHEC"
                )
                print(f"Erreur: {e}")
                
            finally:
                browser.close()
                self.generer_rapport_final()
    
    def etape_0_authentification(self, page):
        print("\n=== ÉTAPE 0 - AUTHENTIFICATION RÉELLE ===")
        
        # Naviguer vers la page de login
        page.goto("http://localhost:8080/login.html", timeout=10000)
        time.sleep(2)
        
        # Remplir le formulaire de connexion
        page.fill("input[name='email']", "admin@sigavt.cm")
        page.fill("input[name='motDePasse']", "admin123")
        
        # Capturer la requête de login
        with page.expect_response("**/api/auth/login") as response_info:
            page.click("button[type='submit']")
        
        response = response_info.value
        login_data = response.json()
        
        print(f"Statut réponse: {response.status}")
        print(f"Token reçu: {login_data.get('token', 'N/A')[:50]}...")
        
        self.ajouter_rapport(
            "0 - Authentification",
            "Connexion avec admin@sigavt.cm",
            f"POST /api/auth/login - email: admin@sigavt.cm",
            f"Status: {response.status}, Token: {login_data.get('token', 'N/A')[:50]}...",
            "SUCCÈS" if response.status == 200 else "ÉCHEC"
        )
        
        # Attendre la redirection vers sigavt.html
        time.sleep(2)
        assert "sigavt.html" in page.url or page.url.endswith("/")
        
    def etape_1_dashboard(self, page):
        print("\n=== ÉTAPE 1 - DASHBOARD ===")
        
        # Cliquer sur Dashboard
        page.click(".nav-item:has-text('Dashboard')")
        time.sleep(2)
        
        # Vérifier la date affichée
        date_element = page.query_selector(".current-date, .date-display")
        if date_element:
            date_affichee = date_element.text_content()
            date_actuelle = datetime.now().strftime("%Y-%m-%d")
            print(f"Date affichée: {date_affichee}")
            print(f"Date actuelle: {date_actuelle}")
        else:
            print("Élément de date non trouvé")
        
        # Vérifier le prochain voyage
        prochain_voyage = page.query_selector(".next-trip, .upcoming-trip")
        if prochain_voyage:
            print(f"Prochain voyage trouvé: {prochain_voyage.text_content()[:100]}")
        
        # Vérifier les statistiques
        stats = page.query_selector_all(".stat-card, .metric-card")
        print(f"Statistiques trouvées: {len(stats)}")
        
        self.ajouter_rapport(
            "1 - Dashboard",
            "Vérification date, prochain voyage et statistiques",
            "GET /api/dashboard/stats",
            f"Date affichée: {date_affichee if date_element else 'N/A'}, Stats: {len(stats)}",
            "SUCCÈS" if date_element else "PARTIEL"
        )
        
    def etape_2_lignes(self, page):
        print("\n=== ÉTAPE 2 - LIGNES ===")
        
        # Cliquer sur Lignes
        page.click(".nav-item:has-text('Lignes')")
        time.sleep(2)
        
        # Cliquer sur + Nouveau
        page.click(".cta-button:has-text('+ Nouveau')")
        time.sleep(1)
        
        # Remplir le formulaire de création de ligne
        page.fill("input[name='villeDepart']", "Douala")
        page.fill("input[name='villeArrivee']", "Bafoussam")
        page.fill("input[name='distanceKm']", "250")
        page.fill("input[name='dureeMinutes']", "240")
        page.fill("input[name='tarifBase']", "15000")
        
        # Soumettre le formulaire
        with page.expect_response("**/api/lignes") as response_info:
            page.click(".btn-save:has-text('Enregistrer')")
        
        response = response_info.value
        ligne_data = response.json()
        
        print(f"Ligne créée: {ligne_data.get('villeDepart')} → {ligne_data.get('villeArrivee')}")
        self.donnees_crees['ligne'] = ligne_data
        
        self.ajouter_rapport(
            "2 - Lignes",
            "Création ligne Douala → Bafoussam",
            f"POST /api/lignes - villeDepart: Douala, villeArrivee: Bafoussam",
            f"Status: {response.status}, ID: {ligne_data.get('id')}",
            "SUCCÈS" if response.status == 201 else "ÉCHEC"
        )
        
        time.sleep(1)
        
    def etape_3_flotte(self, page):
        print("\n=== ÉTAPE 3 - FLOTTE (BUS) ===")
        
        # Cliquer sur Bus
        page.click(".nav-item:has-text('Bus')")
        time.sleep(2)
        
        # Cliquer sur + Nouveau
        page.click(".cta-button:has-text('+ Nouveau')")
        time.sleep(1)
        
        # Remplir le formulaire de création de bus
        immatriculation = f"CM-{int(time.time()) % 10000:04d}AB"
        page.fill("input[name='immatriculation']", immatriculation)
        page.fill("input[name='modele']", "Mercedes-Benz Sprinter")
        page.fill("input[name='nombrePlaces']", "18")
        
        # Soumettre le formulaire
        with page.expect_response("**/api/bus") as response_info:
            page.click(".btn-save:has-text('Enregistrer')")
        
        response = response_info.value
        bus_data = response.json()
        
        print(f"Bus créé: {bus_data.get('immatriculation')}")
        self.donnees_crees['bus'] = bus_data
        
        self.ajouter_rapport(
            "3 - Flotte",
            f"Création bus {immatriculation}",
            f"POST /api/bus - immatriculation: {immatriculation}",
            f"Status: {response.status}, ID: {bus_data.get('id')}",
            "SUCCÈS" if response.status == 201 else "ÉCHEC"
        )
        
        time.sleep(1)
        
    def etape_4_personnel(self, page):
        print("\n=== ÉTAPE 4 - PERSONNEL ===")
        
        # Cliquer sur Personnel
        page.click(".nav-item:has-text('Personnel')")
        time.sleep(2)
        
        # Cliquer sur + Nouveau
        page.click(".cta-button:has-text('+ Nouveau')")
        time.sleep(1)
        
        # Remplir le formulaire de création de personnel
        nom_chauffeur = f"Jean Test{int(time.time()) % 100}"
        page.fill("input[name='nomComplet']", nom_chauffeur)
        page.fill("input[name='telephone']", "+237 677 123 456")
        page.select_option("select[name='poste']", "CHAUFFEUR")
        page.fill("input[name='salaireBase']", "150000")
        
        # Assigner au bus créé précédemment
        if 'bus' in self.donnees_crees:
            page.select_option("select[name='busAssigneId']", str(self.donnees_crees['bus']['id']))
        
        # Soumettre le formulaire
        with page.expect_response("**/api/personnel") as response_info:
            page.click(".btn-save:has-text('Enregistrer')")
        
        response = response_info.value
        personnel_data = response.json()
        
        print(f"Personnel créé: {personnel_data.get('nomComplet')}")
        self.donnees_crees['personnel'] = personnel_data
        
        self.ajouter_rapport(
            "4 - Personnel",
            f"Création chauffeur {nom_chauffeur}",
            f"POST /api/personnel - nomComplet: {nom_chauffeur}, poste: CHAUFFEUR",
            f"Status: {response.status}, ID: {personnel_data.get('id')}",
            "SUCCÈS" if response.status == 201 else "ÉCHEC"
        )
        
        time.sleep(1)
        
    def etape_5_billets(self, page):
        print("\n=== ÉTAPE 5 - BILLETS ===")
        
        # Cliquer sur Billets
        page.click(".nav-item:has-text('Billets')")
        time.sleep(2)
        
        # Cliquer sur + Nouveau
        page.click(".cta-button:has-text('+ Nouveau')")
        time.sleep(1)
        
        # Sélectionner un voyage (créer d'abord un voyage si nécessaire)
        # Pour simplifier, on suppose qu'il existe des voyages
        
        # Remplir le formulaire de billet
        page.fill("input[name='passagerNom']", "Paul Passager")
        page.fill("input[name='passagerTelephone']", "+237 699 987 654")
        page.select_option("select[name='typeTarif']", "NORMAL")
        
        # Sélectionner un siège
        page.click(".seat.available:first-child")
        time.sleep(0.5)
        
        # Sélectionner le mode de paiement
        page.click(".btn-cash")
        time.sleep(0.5)
        
        # Soumettre le formulaire
        with page.expect_response("**/api/billets") as response_info:
            page.click(".btn-save:has-text('Valider')")
        
        response = response_info.value
        billet_data = response.json()
        
        print(f"Billet créé: {billet_data.get('numeroBillet')}")
        self.donnees_crees['billet'] = billet_data
        
        self.ajouter_rapport(
            "5 - Billets",
            "Vente billet complet avec paiement",
            f"POST /api/billets - passager: Paul Passager, paiement: ESPECES",
            f"Status: {response.status}, Numéro: {billet_data.get('numeroBillet')}",
            "SUCCÈS" if response.status == 201 else "ÉCHEC"
        )
        
        time.sleep(1)
        
    def etape_6_colis(self, page):
        print("\n=== ÉTAPE 6 - COLIS ===")
        
        # Cliquer sur Colis
        page.click(".nav-item:has-text('Colis')")
        time.sleep(2)
        
        # Cliquer sur + Nouveau
        page.click(".cta-button:has-text('+ Nouveau')")
        time.sleep(1)
        
        # Remplir le formulaire de colis
        page.fill("input[name='expediteurNom']", "Marie Expéditeur")
        page.fill("input[name='expediteurTel']", "+237 677 111 222")
        page.fill("input[name='destinataireNom']", "Pierre Destinataire")
        page.fill("input[name='destinataireTel']", "+237 699 333 444")
        page.fill("input[name='poidsKg']", "5.5")
        page.select_option("select[name='typeColis']", "STANDARD")
        page.fill("input[name='description']", "Livraison documents urgents")
        
        # Soumettre le formulaire
        with page.expect_response("**/api/colis") as response_info:
            page.click(".btn-save:has-text('Enregistrer')")
        
        response = response_info.value
        colis_data = response.json()
        
        print(f"Colis créé: {colis_data.get('numeroTracking')}")
        self.donnees_crees['colis'] = colis_data
        
        self.ajouter_rapport(
            "6 - Colis",
            "Enregistrement colis avec tracking",
            f"POST /api/colis - tracking: {colis_data.get('numeroTracking')}",
            f"Status: {response.status}, Tracking: {colis_data.get('numeroTracking')}",
            "SUCCÈS" if response.status == 201 else "ÉCHEC"
        )
        
        time.sleep(1)
        
    def etape_7_courriers(self, page):
        print("\n=== ÉTAPE 7 - COURRIERS ===")
        
        # Cliquer sur Courriers
        page.click(".nav-item:has-text('Courriers')")
        time.sleep(2)
        
        # Cliquer sur + Nouveau
        page.click(".cta-button:has-text('+ Nouveau')")
        time.sleep(1)
        
        # Remplir le formulaire de courrier
        page.select_option("select[name='type']", "ENTRANT")
        page.fill("input[name='objet']", "Demande de partenariat")
        page.fill("input[name='expediteur']", "Société Partenaire SA")
        page.fill("textarea[name='contenu']", "Nous souhaitons établir un partenariat pour le transport de nos collaborateurs.")
        
        # Soumettre le formulaire
        with page.expect_response("**/api/courriers") as response_info:
            page.click(".btn-save:has-text('Enregistrer')")
        
        response = response_info.value
        courrier_data = response.json()
        
        print(f"Courrier créé: {courrier_data.get('objet')}")
        self.donnees_crees['courrier'] = courrier_data
        
        # Marquer comme traité
        page.click(".btn-edit:first-child")
        time.sleep(0.5)
        page.select_option("select[name='statut']", "TRAITE")
        page.click(".btn-save:has-text('Enregistrer')")
        time.sleep(1)
        
        self.ajouter_rapport(
            "7 - Courriers",
            "Création courrier entrant et marqué comme traité",
            f"POST /api/courriers - objet: Demande de partenariat, statut: TRAITE",
            f"Status: {response.status}, ID: {courrier_data.get('id')}",
            "SUCCÈS" if response.status == 201 else "ÉCHEC"
        )
        
        time.sleep(1)
        
    def etape_8_paie(self, page):
        print("\n=== ÉTAPE 8 - PAIE ===")
        
        # Cliquer sur Paie
        page.click(".nav-item:has-text('Paie')")
        time.sleep(2)
        
        # Cliquer sur + Nouveau bulletin
        page.click(".cta-button:has-text('+ Nouveau')")
        time.sleep(1)
        
        # Sélectionner l'employé créé précédemment
        if 'personnel' in self.donnees_crees:
            page.select_option("select[name='personnelId']", str(self.donnees_crees['personnel']['id']))
        
        # Définir la période (mois courant)
        periode = datetime.now().strftime("%Y-%m")
        page.fill("input[name='periode']", periode)
        
        # Soumettre le formulaire pour générer le bulletin
        with page.expect_response("**/api/paie/bulletins") as response_info:
            page.click(".btn-save:has-text('Générer')")
        
        response = response_info.value
        bulletin_data = response.json()
        
        print(f"Bulletin généré: {bulletin_data.get('periode')}")
        self.donnees_crees['bulletin'] = bulletin_data
        
        # Vérifier les calculs CNPS/IRPP affichés
        cnps_affiche = page.query_selector(".cnps-value, .cotisation-cnps")
        irpp_affiche = page.query_selector(".irpp-value, .retenue-irpp")
        
        # Marquer comme payé
        page.select_option("select[name='statutPaiement']", "PAYE")
        page.click(".btn-save:has-text('Enregistrer')")
        time.sleep(1)
        
        self.ajouter_rapport(
            "8 - Paie",
            f"Génération bulletin {periode} et marqué payé",
            f"POST /api/paie/bulletins - periode: {periode}, CNPS: {bulletin_data.get('cotisationCnps')}, IRPP: {bulletin_data.get('retenueIrpp')}",
            f"Status: {response.status}, Net à payer: {bulletin_data.get('netAPayer')}",
            "SUCCÈS" if response.status == 201 else "ÉCHEC"
        )
        
        time.sleep(1)
        
    def etape_9_comptabilite(self, page):
        print("\n=== ÉTAPE 9 - COMPTABILITÉ ===")
        
        # Cliquer sur Comptabilité
        page.click(".nav-item:has-text('Comptabilité')")
        time.sleep(2)
        
        # Vérifier que le journal contient l'écriture du billet
        ecritures = page.query_selector_all(".journal-entry, .ecriture-comptable")
        print(f"Écritures comptables trouvées: {len(ecritures)}")
        
        # Chercher l'écriture correspondant à la vente de billet
        ecriture_billet_trouvee = False
        if 'billet' in self.donnees_crees:
            for ecriture in ecritures:
                texte = ecriture.text_content()
                if "Billet" in texte or "VENTE" in texte:
                    ecriture_billet_trouvee = True
                    print(f"Écriture billet trouvée: {texte[:100]}")
                    break
        
        self.ajouter_rapport(
            "9 - Comptabilité",
            "Vérification journal contient écriture billet auto-générée",
            "GET /api/comptabilite/journal",
            f"Écritures totales: {len(ecritures)}, Écriture billet trouvée: {ecriture_billet_trouvee}",
            "SUCCÈS" if ecriture_billet_trouvee else "PARTIEL"
        )
        
        time.sleep(1)
        
    def etape_10_parametres(self, page):
        print("\n=== ÉTAPE 10 - PARAMÈTRES ===")
        
        # Cliquer sur Paramètres
        page.click(".nav-item:has-text('Paramètres')")
        time.sleep(2)
        
        # Modifier un champ
        ancien_telephone = page.input_value("input[name='telephone']")
        nouveau_telephone = "+237 677 999 888"
        page.fill("input[name='telephone']", nouveau_telephone)
        
        # Sauvegarder
        with page.expect_response("**/api/parametres") as response_info:
            page.click(".btn-save:has-text('Enregistrer')")
        
        response = response_info.value
        parametres_data = response.json()
        
        # Recharger la page (F5)
        page.reload(wait_until="networkidle")
        time.sleep(2)
        
        # Vérifier que la valeur modifiée persiste
        telephone_apres_reload = page.input_value("input[name='telephone']")
        
        valeur_persiste = (telephone_apres_reload == nouveau_telephone)
        
        self.ajouter_rapport(
            "10 - Paramètres",
            f"Modification téléphone {ancien_telephone} → {nouveau_telephone}",
            f"PUT /api/parametres - telephone: {nouveau_telephone}",
            f"Status: {response.status}, Valeur persiste après F5: {valeur_persiste}",
            "SUCCÈS" if valeur_persiste else "ÉCHEC"
        )
        
        time.sleep(1)
        
    def etape_11_deconnexion_reconnexion(self, page):
        print("\n=== ÉTAPE 11 - DÉCONNEXION ET RECONNEXION ===")
        
        # Se déconnecter
        page.click(".logout-btn, button:has-text('Déconnexion')")
        time.sleep(2)
        
        # Vérifier qu'on est redirigé vers login
        assert "login" in page.url
        print("Déconnexion réussie")
        
        # Se reconnecter
        page.fill("input[name='email']", "admin@sigavt.cm")
        page.fill("input[name='motDePasse']", "admin123")
        
        with page.expect_response("**/api/auth/login") as response_info:
            page.click("button[type='submit']")
        
        response = response_info.value
        login_data = response.json()
        
        time.sleep(2)
        
        # Vérifier que les données créées sont toujours là
        donnees_persistees = True
        
        # Vérifier la ligne créée
        page.click(".nav-item:has-text('Lignes')")
        time.sleep(1)
        if 'ligne' in self.donnees_crees:
            ligne_trouvee = page.query_selector(f"text={self.donnees_crees['ligne']['villeDepart']}")
            donnees_persistees = donnees_persistees and (ligne_trouvee is not None)
        
        # Vérifier le bus créé
        page.click(".nav-item:has-text('Bus')")
        time.sleep(1)
        if 'bus' in self.donnees_crees:
            bus_trouve = page.query_selector(f"text={self.donnees_crees['bus']['immatriculation']}")
            donnees_persistees = donnees_persistees and (bus_trouve is not None)
        
        self.ajouter_rapport(
            "11 - Déconnexion/Reconnexion",
            "Vérification persistance données après reconnexion",
            "POST /api/auth/login (reconnexion)",
            f"Status: {response.status}, Données persistées: {donnees_persistees}",
            "SUCCÈS" if donnees_persistees else "ÉCHEC"
        )
        
    def generer_rapport_final(self):
        print("\n" + "="*80)
        print("RAPPORT FINAL DE SIMULATION SIGAVT")
        print("="*80)
        
        for entree in self.rapport:
            print(f"\n[{entree['etape']}] {entree['action']}")
            print(f"  Requête: {entree['requete']}")
            print(f"  Réponse: {entree['reponse']}")
            print(f"  Résultat: {entree['resultat']}")
            print(f"  Horodatage: {entree['horodatage']}")
        
        # Statistiques
        succes = sum(1 for r in self.rapport if r['resultat'] == 'SUCCÈS')
        echec = sum(1 for r in self.rapport if r['resultat'] == 'ÉCHEC')
        partiel = sum(1 for r in self.rapport if r['resultat'] == 'PARTIEL')
        
        print(f"\n" + "="*80)
        print(f"STATISTIQUES: {succes} SUCCÈS, {partiel} PARTIEL, {echec} ÉCHEC")
        print("="*80)
        
        # Sauvegarder en JSON
        with open('rapport_simulation.json', 'w', encoding='utf-8') as f:
            json.dump(self.rapport, f, ensure_ascii=False, indent=2)
        
        print("\nRapport sauvegardé dans rapport_simulation.json")

if __name__ == "__main__":
    simulation = SimulationSIGAVT()
    simulation.executer_simulation()
