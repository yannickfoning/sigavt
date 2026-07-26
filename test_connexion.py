"""
Test de connexion et déconnexion
"""
from playwright.sync_api import sync_playwright
import time

def test_connexion():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=False, slow_mo=1000)
        page = browser.new_page()
        
        print("=== Test Connexion/Déconnexion ===")
        
        # 1. Test de connexion
        print("\n1. Test de connexion...")
        page.goto("http://localhost:8081/login.html")
        page.wait_for_timeout(1000)
        
        # Ouvrir le modal de connexion
        page.click("#login-toggle-btn")
        page.wait_for_timeout(500)
        
        # Remplir le formulaire
        page.fill("#email", "admin@sigavt.cm")
        page.fill("#motDePasse", "admin123")
        
        # Cliquer sur le bouton de connexion
        page.click("#login-btn")
        page.wait_for_timeout(3000)
        
        # Vérifier la connexion
        current_url = page.url
        print(f"   - URL actuelle: {current_url}")
        
        if "login" not in current_url or "sigavt" in current_url:
            print("   ✓ Connexion réussie")
        else:
            print("   ✗ Connexion échouée")
        
        # Naviguer vers l'interface
        page.goto("http://localhost:8081/sigavt.html")
        page.wait_for_timeout(2000)
        
        # 2. Test de déconnexion
        print("\n2. Test de déconnexion...")
        try:
            page.click("text=Déconnexion")
            page.wait_for_timeout(2000)
            current_url = page.url
            print(f"   - URL après déconnexion: {current_url}")
            
            if "login" in current_url:
                print("   ✓ Déconnexion réussie")
            else:
                print("   ✗ Déconnexion échouée")
        except Exception as e:
            print(f"   ✗ Erreur de déconnexion: {e}")
        
        browser.close()
        print("\n=== Test terminé ===")

if __name__ == "__main__":
    test_connexion()
