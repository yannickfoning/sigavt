#!/usr/bin/env python3
"""
Script de test pour vérifier tous les boutons de la plateforme SIGAVT
"""

import time
import json
from playwright.sync_api import sync_playwright

def test_all_buttons():
    with sync_playwright() as p:
        # Lancer le navigateur en mode headless
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()
        
        # Configuration initiale pour éviter la redirection login
        page.add_init_script("""
            localStorage.setItem('token', 'test-token');
            localStorage.setItem('user', JSON.stringify({id: 1, email: 'admin@sigavt.cm', role: 'ADMIN'}));
        """)
        
        print("=== DÉBUT DES TESTS DES BOUTONS SIGAVT ===\n")
        
        # Naviguer vers la page principale
        print("1. Navigation vers http://localhost:8080/sigavt.html")
        try:
            page.goto("http://localhost:8080/sigavt.html", timeout=10000)
            print("   ✓ Page chargée avec succès")
        except Exception as e:
            print(f"   ✗ Erreur de chargement: {e}")
            browser.close()
            return
        
        # Attendre que la page soit chargée
        time.sleep(2)
        
        # Tests des boutons de navigation dans la sidebar
        print("\n2. TEST DES BOUTONS DE NAVIGATION")
        nav_tests = [
            "Dashboard",
            "Billets", 
            "Colis",
            "Bus",
            "Lignes",
            "Personnel",
            "Voyages",
            "Paie",
            "Comptabilité",
            "Courriers",
            "Paramètres"
        ]
        
        for nav_item in nav_tests:
            try:
                # Chercher le bouton de navigation
                selector = f".nav-item:has-text('{nav_item}')"
                if page.query_selector(selector):
                    page.click(selector)
                    time.sleep(0.5)
                    print(f"   ✓ Bouton '{nav_item}' cliqué avec succès")
                else:
                    print(f"   ✗ Bouton '{nav_item}' non trouvé")
            except Exception as e:
                print(f"   ✗ Erreur sur bouton '{nav_item}': {e}")
        
        # Tests des boutons d'action (+ Nouveau)
        print("\n3. TEST DES BOUTONS D'ACTION (+ NOUVEAU)")
        try:
            cta_button = page.query_selector(".cta-button")
            if cta_button:
                cta_button.click()
                time.sleep(1)
                print("   ✓ Bouton '+ Nouveau' cliqué")
                
                # Vérifier si une modale s'ouvre
                modal = page.query_selector(".modal.is-open")
                if modal:
                    print("   ✓ Modale ouverte après clic sur + Nouveau")
                else:
                    print("   ✗ Aucune modale ouverte après clic sur + Nouveau")
            else:
                print("   ✗ Bouton '+ Nouveau' non trouvé")
        except Exception as e:
            print(f"   ✗ Erreur sur bouton + Nouveau: {e}")
        
        # Tests des boutons dans les formulaires
        print("\n4. TEST DES BOUTONS DE FORMULAIRE")
        form_buttons = [
            ("Boutons 'Suivant'", ".btn-next"),
            ("Boutons 'Précédent'", ".btn-prev"),
            ("Boutons 'Annuler'", ".btn-cancel"),
            ("Boutons 'Valider'", ".btn-validate"),
            ("Boutons 'Enregistrer'", ".btn-save")
        ]
        
        for button_name, selector in form_buttons:
            if page.query_selector(selector):
                count = len(page.query_selector_all(selector))
                print(f"   ✓ {count} bouton(s) '{button_name}' trouvé(s)")
            else:
                print(f"   ✗ Aucun bouton '{button_name}' trouvé")
        
        # Tests des boutons de déconnexion
        print("\n5. TEST DES BOUTONS DE DÉCONNEXION")
        try:
            logout_button = page.query_selector("button.logout-btn, .logout-btn, button:has-text('Déconnexion')")
            if logout_button:
                print("   ✓ Bouton de déconnexion trouvé")
                # Ne pas cliquer pour ne pas terminer le test
            else:
                print("   ✗ Bouton de déconnexion non trouvé")
        except Exception as e:
            print(f"   ✗ Erreur sur bouton déconnexion: {e}")
        
        # Tests des boutons de tableau (actions sur les lignes)
        print("\n6. TEST DES BOUTONS DE TABLEAU")
        table_actions = [
            ("Boutons Modifier", ".btn-edit, button:has-text('Modifier')"),
            ("Boutons Supprimer", ".btn-delete, button:has-text('Supprimer')"),
            ("Boutons Voir", ".btn-view, button:has-text('Voir')")
        ]
        
        for button_name, selector in table_actions:
            if page.query_selector(selector):
                count = len(page.query_selector_all(selector))
                print(f"   ✓ {count} bouton(s) '{button_name}' trouvé(s)")
            else:
                print(f"   ✗ Aucun bouton '{button_name}' trouvé")
        
        # Tests des boutons de paiement
        print("\n7. TEST DES BOUTONS DE PAIEMENT")
        payment_buttons = [
            ("Boutons Orange Money", ".btn-orange-money"),
            ("Boutons MTN MoMo", ".btn-mtn-momo"),
            ("Boutons Espèces", ".btn-cash")
        ]
        
        for button_name, selector in payment_buttons:
            if page.query_selector(selector):
                count = len(page.query_selector_all(selector))
                print(f"   ✓ {count} bouton(s) '{button_name}' trouvé(s)")
            else:
                print(f"   ✗ Aucun bouton '{button_name}' trouvé")
        
        # Résumé
        print("\n=== FIN DES TESTS ===")
        print("Application disponible sur: http://localhost:8080/sigavt.html")
        print("Documentation API: http://localhost:8080/swagger-ui.html")
        
        browser.close()

if __name__ == "__main__":
    test_all_buttons()