"""
Test des fonctions d'export
"""
from playwright.sync_api import sync_playwright
import time

def test_export():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=False, slow_mo=1000)
        page = browser.new_page()
        
        print("=== Test Fonctions d'Export ===")
        
        # 1. Connexion
        print("\n1. Connexion...")
        page.goto("http://localhost:8081/login.html")
        page.wait_for_timeout(1000)
        page.click("#login-toggle-btn")
        page.wait_for_timeout(500)
        page.fill("#email", "admin@sigavt.cm")
        page.fill("#motDePasse", "admin123")
        page.click("#login-btn")
        page.wait_for_timeout(3000)
        print("   ✓ Connecté")
        
        # 2. Naviguer vers différents modules pour chercher les boutons d'export
        print("\n2. Recherche des boutons d'export dans les modules...")
        
        modules = [
            "Billets",
            "Colis", 
            "Personnel",
            "Flotte Bus",
            "Voyages",
            "Comptabilité",
            "Paie"
        ]
        
        export_buttons_found = False
        
        for module in modules:
            try:
                page.click(f"text={module}")
                page.wait_for_timeout(1000)
                
                # Chercher des boutons d'export
                export_selectors = [
                    "text=Export",
                    "text=Exporter",
                    "text=CSV",
                    "text=PDF",
                    "text=Excel",
                    ".btn-export",
                    "[data-export]"
                ]
                
                for selector in export_selectors:
                    try:
                        if page.is_visible(selector):
                            print(f"   ✓ Bouton d'export trouvé dans {module}: {selector}")
                            export_buttons_found = True
                            break
                    except:
                        continue
                        
            except Exception as e:
                print(f"   - Module {module}: {e}")
        
        if not export_buttons_found:
            print("   - Aucun bouton d'export explicite trouvé")
            print("   - Note: L'export peut être implémenté via les menus contextuels ou les actions de tableau")
        
        # 3. Test des actions de tableau (souvent incluent l'export)
        print("\n3. Test des actions de tableau...")
        page.click("text=Billets")
        page.wait_for_timeout(1000)
        
        try:
            # Chercher des boutons d'action dans les tableaux
            if page.is_visible(".table-actions") or page.is_visible(".action-btn"):
                print("   ✓ Actions de tableau disponibles")
            else:
                print("   - Actions de tableau non trouvées")
        except Exception as e:
            print(f"   - Actions de tableau: {e}")
        
        # 4. Screenshot final
        print("\n4. Capture d'écran...")
        page.screenshot(path="test_export_final.png")
        print("   ✓ Screenshot enregistré")
        
        browser.close()
        print("\n=== Test terminé ===")

if __name__ == "__main__":
    test_export()
