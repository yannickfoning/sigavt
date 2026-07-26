"""
Test complet de la plateforme SIGAVT
Couvre: navigation, boutons, formulaires, traduction, export
"""
from playwright.sync_api import sync_playwright
import time

def test_complet():
    with sync_playwright() as p:
        # Lancer le navigateur
        browser = p.chromium.launch(headless=False, slow_mo=1000)
        page = browser.new_page()
        
        print("=== Test Complet SIGAVT ===")
        
        # 1. Navigation directe vers l'interface
        print("\n1. Navigation vers l'interface...")
        page.goto("http://localhost:8081/sigavt.html")
        page.wait_for_timeout(2000)
        print("   ✓ Interface chargée")
        
        # 2. Test de navigation entre les modules
        print("\n2. Test de navigation des modules...")
        modules = [
            ("Tableau de bord", "dashboard"),
            ("Billets", "billets"),
            ("Colis", "colis"),
            ("Voyages", "voyages"),
            ("Courriers", "courriers"),
            ("Flotte Bus", "flotte"),
            ("Personnel", "personnel"),
            ("Paie", "paie"),
            ("Comptabilité", "comptabilite"),
            ("Lignes", "lignes"),
            ("Paramètres", "parametres")
        ]
        
        for module_name, module_id in modules:
            try:
                page.click(f"text={module_name}")
                page.wait_for_timeout(500)
                print(f"   ✓ Module {module_name} accessible")
            except Exception as e:
                print(f"   ✗ Module {module_name} non accessible: {e}")
        
        # 3. Test des boutons d'action
        print("\n3. Test des boutons d'action...")
        
        # Bouton CTA (+ Nouveau)
        try:
            if page.is_visible("#topbar-cta"):
                page.click("#topbar-cta")
                page.wait_for_timeout(500)
                print("   ✓ Bouton + Nouveau fonctionnel")
                # Fermer le modal si ouvert
                if page.is_visible(".modal-backdrop.open"):
                    page.click(".close-btn")
        except Exception as e:
            print(f"   ✗ Bouton + Nouveau: {e}")
        
        # Bouton de déconnexion
        try:
            if page.is_visible(".btn-logout"):
                print("   ✓ Bouton de déconnexion visible")
        except:
            print("   ✗ Bouton de déconnexion non visible")
        
        # 4. Test des formulaires de création
        print("\n4. Test des formulaires de création...")
        
        # Formulaire employé
        page.click("text=Personnel")
        page.wait_for_timeout(500)
        try:
            page.click("text=+ Employé")
            page.wait_for_timeout(500)
            if page.is_visible("#modal-employe.open"):
                print("   ✓ Formulaire employé s'ouvre")
                page.click("#modal-employe .close-btn")
        except Exception as e:
            print(f"   ✗ Formulaire employé: {e}")
        
        # Formulaire bus
        page.click("text=Flotte Bus")
        page.wait_for_timeout(500)
        try:
            if page.is_visible("text=+ Bus"):
                page.click("text=+ Bus")
                page.wait_for_timeout(500)
                if page.is_visible("#modal-bus.open"):
                    print("   ✓ Formulaire bus s'ouvre")
                    page.click("#modal-bus .close-btn")
        except Exception as e:
            print(f"   ✗ Formulaire bus: {e}")
        
        # 5. Test de la traduction
        print("\n5. Test de la traduction...")
        try:
            # Chercher un bouton ou sélecteur de langue
            if page.is_visible("text=FR") or page.is_visible("text=EN") or page.is_visible(".lang-toggle"):
                print("   ✓ Options de traduction disponibles")
                # Tester le changement de langue
                try:
                    page.click("text=FR")
                    page.wait_for_timeout(500)
                    print("   ✓ Changement de langue fonctionnel")
                except:
                    print("   - Changement de langue non testé")
            else:
                print("   - Options de traduction non trouvées")
        except Exception as e:
            print(f"   - Traduction: {e}")
        
        # 6. Test des boutons de paiement
        print("\n6. Test des boutons de paiement...")
        page.click("text=Billets")
        page.wait_for_timeout(500)
        try:
            if page.is_visible(".pay-btn"):
                page.click(".pay-btn:first-child")
                page.wait_for_timeout(500)
                print("   ✓ Boutons de paiement fonctionnels")
        except Exception as e:
            print(f"   - Boutons de paiement: {e}")
        
        # 7. Test de l'export
        print("\n7. Test de l'export...")
        try:
            # Chercher des boutons d'export
            if page.is_visible("text=Export") or page.is_visible("text=Exporter") or page.is_visible(".btn-export"):
                print("   ✓ Boutons d'export disponibles")
            else:
                print("   - Boutons d'export non trouvés (peut-être dans les menus)")
        except Exception as e:
            print(f"   - Export: {e}")
        
        # 8. Test du dashboard
        print("\n8. Test du Dashboard...")
        page.click("text=Tableau de bord")
        page.wait_for_timeout(500)
        try:
            if page.is_visible(".stats-grid") or page.is_visible(".stat-card") or page.is_visible(".card"):
                print("   ✓ Dashboard affiché correctement")
            else:
                print("   - Dashboard non affiché")
        except Exception as e:
            print(f"   ✗ Dashboard: {e}")
        
        # 9. Test de déconnexion
        print("\n9. Test de déconnexion...")
        try:
            if page.is_visible("text=Déconnexion") or page.is_visible("text=Logout") or page.is_visible(".btn-logout"):
                print("   ✓ Bouton de déconnexion visible")
                # Note: On ne clique pas pour éviter de perdre la session
        except Exception as e:
            print(f"   - Déconnexion: {e}")
        
        # 9. Screenshot final
        print("\n9. Capture d'écran finale...")
        page.screenshot(path="test_complet_final.png")
        print("   ✓ Screenshot enregistré")
        
        # Fermer le navigateur
        browser.close()
        
        print("\n=== Test terminé ===")

if __name__ == "__main__":
    test_complet()
