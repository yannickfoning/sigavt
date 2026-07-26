"""
Script Playwright pour simuler l'utilisation réelle de SIGAVT via le navigateur
Teste l'interface utilisateur de sigavt.html
"""
from playwright.sync_api import sync_playwright
import time
import json

def test_sigavt_interface():
    with sync_playwright() as p:
        # Lancer le navigateur en mode headful pour voir l'interface
        browser = p.chromium.launch(headless=False, slow_mo=1000)
        page = browser.new_page()
        
        try:
            print("=== DÉMARRAGE DE LA SIMULATION INTERFACE SIGAVT ===")
            
            # Naviguer vers l'application
            print("1. Navigation vers http://localhost:8081/login.html")
            page.goto("http://localhost:8081/login.html")
            time.sleep(2)
            
            # Prendre une capture d'écran de la page de connexion
            page.screenshot(path="sigavt_login.png")
            print("   ✓ Capture d'écran prise: sigavt_login.png")
            
            # Cliquer sur le bouton de connexion pour ouvrir le modal
            print("2. Ouverture du modal de connexion")
            page.click('#login-toggle-btn')
            time.sleep(1)
            
            # Remplir le formulaire de connexion
            print("3. Remplissage du formulaire de connexion")
            page.fill('#email', 'admin@sigavt.cm')
            page.fill('#motDePasse', 'admin123')
            time.sleep(1)
            
            # Cliquer sur le bouton de connexion
            print("4. Clic sur le bouton de connexion")
            page.click('#login-btn')
            time.sleep(3)
            
            # Prendre une capture d'écran après connexion
            page.screenshot(path="sigavt_dashboard.png")
            print("   ✓ Capture d'écran prise: sigavt_dashboard.png")
            
            # Vérifier si la connexion a réussi
            if page.url != "http://localhost:8081/sigavt.html" or "dashboard" in page.url.lower():
                print("   ✓ Connexion réussie")
            else:
                print("   ⚠ Connexion peut avoir échoué - vérifier les captures")
            
            # Tester la navigation dans le menu
            print("4. Test de navigation dans le menu")
            
            # Essayer de cliquer sur différents éléments du menu
            menu_items = [
                "Dashboard",
                "Lignes", 
                "Bus",
                "Billets",
                "Colis"
            ]
            
            for menu_item in menu_items:
                try:
                    # Chercher le menu item par texte
                    menu_selector = f'text="{menu_item}"'
                    if page.is_visible(menu_selector):
                        page.click(menu_selector)
                        time.sleep(2)
                        page.screenshot(path=f"sigavt_{menu_item.lower()}.png")
                        print(f"   ✓ Navigation vers {menu_item} réussie")
                    else:
                        print(f"   ⚠ Menu item '{menu_item}' non trouvé")
                except Exception as e:
                    print(f"   ⚠ Erreur lors de la navigation vers {menu_item}: {e}")
            
            # Tester un formulaire de création si disponible
            print("5. Test de formulaire de création")
            
            # Chercher un bouton "Créer" ou "Ajouter"
            create_buttons = page.get_by_text("Créer").all() + page.get_by_text("Ajouter").all()
            
            if create_buttons:
                try:
                    create_buttons[0].click()
                    time.sleep(2)
                    page.screenshot(path="sigavt_formulaire.png")
                    print("   ✓ Formulaire ouvert")
                except Exception as e:
                    print(f"   ⚠ Erreur lors de l'ouverture du formulaire: {e}")
            else:
                print("   ⚠ Aucun bouton de création trouvé")
            
            print("\n=== SIMULATION TERMINÉE ===")
            print("Captures d'écran générées:")
            print("- sigavt_login.png")
            print("- sigavt_dashboard.png")
            print("- sigavt_lignes.png")
            print("- sigavt_bus.png")
            print("- sigavt_billets.png")
            print("- sigavt_colis.png")
            print("- sigavt_formulaire.png")
            
        except Exception as e:
            print(f"\n❌ ERREUR CRITIQUE: {e}")
            page.screenshot(path="sigavt_error.png")
            print("   Capture d'écran d'erreur prise: sigavt_error.png")
        
        finally:
            # Attendre un peu avant de fermer pour voir le résultat
            print("\nAppuyez sur Entrée dans le terminal pour fermer le navigateur...")
            time.sleep(10)
            browser.close()

if __name__ == "__main__":
    test_sigavt_interface()
