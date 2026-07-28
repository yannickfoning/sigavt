import requests
import os
import sys

BASE_URL = "http://localhost:8081/api"

def get_auth_token():
    """Obtenir un token d'authentification"""
    response = requests.post(f"{BASE_URL}/auth/login", json={
        "email": "admin@sigavt.cm",
        "motDePasse": "admin123"
    })
    if response.status_code == 200:
        return response.json().get("token")
    return None

def verifier_jwt_secret():
    """Vérifier que JWT_SECRET est défini en variable d'environnement"""
    print("=== Vérification JWT_SECRET ===\n")
    
    jwt_secret = os.environ.get('JWT_SECRET')
    
    if jwt_secret:
        print(f"✅ JWT_SECRET défini en variable d'environnement")
        print(f"   Longueur: {len(jwt_secret)} caractères")
        print(f"   Préfixe: {jwt_secret[:10]}..." if len(jwt_secret) > 10 else f"   Valeur: {jwt_secret}")
        
        # Vérifier que ce n'est pas une valeur par défaut évidente
        valeurs_par_defaut = ['', 'secret', 'changeme', 'default', 'test']
        if jwt_secret.lower() in valeurs_par_defaut:
            print("   ⚠️ ATTENTION: Valeur par défaut détectée - à changer en prod")
        else:
            print("   ✅ Valeur semble personnalisée")
    else:
        print("❌ JWT_SECRET NON défini en variable d'environnement")
        print("   Action requise: Définir JWT_SECRET avant déploiement en production")
        print("   Exemple (Windows): $env:JWT_SECRET='votre-secret-ici'")
        print("   Exemple (Linux/Mac): export JWT_SECRET='votre-secret-ici'")
    
    print()

def verifier_mysql_password():
    """Vérifier que MySQL n'utilise pas le mot de passe vide par défaut"""
    print("=== Vérification Mot de passe MySQL ===\n")
    
    # Vérifier les variables d'environnement
    db_username = os.environ.get('DB_USERNAME')
    db_password = os.environ.get('DB_PASSWORD')
    
    if db_username and db_password:
        print("✅ DB_USERNAME et DB_PASSWORD définis en variable d'environnement")
        print(f"   DB_USERNAME: {db_username}")
        print(f"   DB_PASSWORD: {'*' * len(db_password)} (masqué)")
        
        # Vérifier que ce n'est pas une valeur par défaut évidente
        if db_password == '' or db_password is None:
            print("   ⚠️ ATTENTION: Mot de passe vide - à changer en prod")
        else:
            print("   ✅ Mot de passe configuré")
    else:
        print("⚠ DB_USERNAME et/ou DB_PASSWORD NON définis en variable d'environnement")
        print("   Utilisation des valeurs par défaut de application.yml")
        print("   Action requise pour production:")
        print("   1. Définir un mot de passe MySQL sécurisé")
        print("   2. Définir DB_USERNAME et DB_PASSWORD en variables d'environnement")
        print("   Exemple (Windows):")
        print("      $env:DB_USERNAME='root'")
        print("      $env:DB_PASSWORD='votre-mot-de-passe-secure'")
        print("   Exemple (Linux/Mac):")
        print("      export DB_USERNAME='root'")
        print("      export DB_PASSWORD='votre-mot-de-passe-secure'")
    
    print()

def verifier_authentification():
    """Vérifier que l'authentification fonctionne avec JWT"""
    print("=== Vérification Authentification JWT ===\n")
    
    token = get_auth_token()
    if token:
        print("✅ Authentification fonctionnelle")
        print(f"   Token généré: {token[:30]}...")
        
        # Vérifier que le token est valide
        headers = {"Authorization": f"Bearer {token}"}
        response = requests.get(f"{BASE_URL}/dashboard", headers=headers)
        if response.status_code == 200:
            print("✅ Token accepté par l'API")
        else:
            print(f"❌ Token rejeté: {response.status_code}")
    else:
        print("❌ Authentification échouée")
        print("   Vérifiez que JWT_SECRET est correctement configuré")
    
    print()

def main():
    """Exécuter toutes les vérifications d'infrastructure"""
    print("=" * 60)
    print("VERIFICATION INFRASTRUCTURE SIGAVT")
    print("=" * 60)
    print()
    
    verifier_jwt_secret()
    verifier_mysql_password()
    verifier_authentification()
    
    print("=" * 60)
    print("FIN DES VERIFICATIONS")
    print("=" * 60)
    print()
    print("Résumé des actions requises pour la production:")
    print("1. Définir JWT_SECRET en variable d'environnement")
    print("2. Configurer un mot de passe MySQL sécurisé")
    print("3. Définir DB_USERNAME et DB_PASSWORD en variables d'environnement")
    print("4. Relancer l'application après configuration")
    print()
    print("Exemple de configuration complète (Windows):")
    print("  $env:JWT_SECRET='votre-secret-256-caracteres'")
    print("  $env:DB_USERNAME='root'")
    print("  $env:DB_PASSWORD='votre-mot-de-passe-secure'")
    print()
    print("Exemple de configuration complète (Linux/Mac):")
    print("  export JWT_SECRET='votre-secret-256-caracteres'")
    print("  export DB_USERNAME='root'")
    print("  export DB_PASSWORD='votre-mot-de-passe-secure'")

if __name__ == "__main__":
    main()
