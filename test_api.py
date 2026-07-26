#!/usr/bin/env python3
"""
Script de test complet de l'API SIGAVT
"""

import requests
import json
from typing import Dict, List

BASE_URL = "http://localhost:8081"

class APITester:
    def __init__(self):
        self.results = []
        self.token = None
        
    def test_endpoint(self, name: str, method: str, url: str, 
                     headers: Dict = None, body: Dict = None, 
                     expected_success: bool = True) -> bool:
        """Test a single API endpoint"""
        print(f"Test: {name}")
        print(f"  {method} {url}")
        
        try:
            if method == "GET":
                response = requests.get(url, headers=headers, timeout=10)
            elif method == "POST":
                response = requests.post(url, headers=headers, json=body, timeout=10)
            elif method == "PUT":
                response = requests.put(url, headers=headers, json=body, timeout=10)
            elif method == "DELETE":
                response = requests.delete(url, headers=headers, timeout=10)
            else:
                print(f"  ✗ Method not supported")
                self.results.append({"test": name, "result": "FAILED"})
                return False
            
            if expected_success:
                if response.status_code in [200, 201]:
                    print(f"  ✓ SUCCESS (Status: {response.status_code})")
                    self.results.append({"test": name, "result": "SUCCESS"})
                    return True
                else:
                    print(f"  ✗ FAILED (Status: {response.status_code})")
                    print(f"  Response: {response.text[:200]}")
                    self.results.append({"test": name, "result": "FAILED"})
                    return False
            else:
                if response.status_code >= 400:
                    print(f"  ✓ SUCCESS (Expected failure, Status: {response.status_code})")
                    self.results.append({"test": name, "result": "SUCCESS"})
                    return True
                else:
                    print(f"  ✗ FAILED (Should have failed, Status: {response.status_code})")
                    self.results.append({"test": name, "result": "FAILED"})
                    return False
                    
        except Exception as e:
            print(f"  ✗ ERROR: {str(e)}")
            self.results.append({"test": name, "result": "ERROR"})
            return False
    
    def run_tests(self):
        """Run all API tests"""
        print("=" * 50)
        print("TEST COMPLET DE L'API SIGAVT")
        print("=" * 50)
        print()
        
        # 1. Health check
        print("=== HEALTH CHECKS ===")
        self.test_endpoint("Health Check", "GET", f"{BASE_URL}/actuator/health", expected_success=False)
        print()
        
        # 2. Authentication tests
        print("=== AUTHENTICATION TESTS ===")
        
        # Test login with wrong credentials
        self.test_endpoint(
            "Login (invalid credentials)",
            "POST",
            f"{BASE_URL}/api/auth/login",
            body={"email": "test@example.com", "password": "wrongpassword"},
            expected_success=False
        )
        
        # Test login with correct credentials
        print("Test: Login (admin)")
        print(f"  POST {BASE_URL}/api/auth/login")
        try:
            response = requests.post(
                f"{BASE_URL}/api/auth/login",
                json={"email": "admin@sigavt.cm", "password": "admin123"},
                timeout=10
            )
            if response.status_code == 200:
                print(f"  ✓ SUCCESS (Status: {response.status_code})")
                self.token = response.json().get("token")
                print(f"  Token obtained: {self.token[:20] if self.token else 'None'}...")
                self.results.append({"test": "Login (admin)", "result": "SUCCESS"})
            else:
                print(f"  ✗ FAILED (Status: {response.status_code})")
                print(f"  Response: {response.text[:200]}")
                self.results.append({"test": "Login (admin)", "result": "FAILED"})
        except Exception as e:
            print(f"  ✗ ERROR: {str(e)}")
            self.results.append({"test": "Login (admin)", "result": "ERROR"})
        print()
        
        # 3. Protected endpoints tests
        if self.token:
            headers = {"Authorization": f"Bearer {self.token}"}
            
            print("=== PROTECTED ENDPOINTS TESTS ===")
            
            # Dashboard endpoints
            self.test_endpoint("Dashboard", "GET", f"{BASE_URL}/api/dashboard", headers=headers)
            self.test_endpoint("Dashboard Stats", "GET", f"{BASE_URL}/api/dashboard/stats", headers=headers)
            self.test_endpoint("Departs du jour", "GET", f"{BASE_URL}/api/dashboard/departs", headers=headers)
            self.test_endpoint("Recettes semaine", "GET", f"{BASE_URL}/api/dashboard/recettes-semaine", headers=headers)
            self.test_endpoint("Top lignes", "GET", f"{BASE_URL}/api/dashboard/top-lignes", headers=headers)
            self.test_endpoint("Alertes", "GET", f"{BASE_URL}/api/dashboard/alertes", headers=headers)
            
            print("=== ENTITY TESTS ===")
            
            # Entity endpoints
            self.test_endpoint("Liste des agences", "GET", f"{BASE_URL}/api/agences", headers=headers)
            self.test_endpoint("Liste des bus", "GET", f"{BASE_URL}/api/bus", headers=headers)
            self.test_endpoint("Liste des lignes", "GET", f"{BASE_URL}/api/lignes", headers=headers)
            self.test_endpoint("Liste du personnel", "GET", f"{BASE_URL}/api/personnel", headers=headers)
            self.test_endpoint("Liste des voyages", "GET", f"{BASE_URL}/api/voyages", headers=headers)
            self.test_endpoint("Liste des billets", "GET", f"{BASE_URL}/api/billets", headers=headers)
            self.test_endpoint("Liste des colis", "GET", f"{BASE_URL}/api/colis", headers=headers)
            self.test_endpoint("Ecritures comptables", "GET", f"{BASE_URL}/api/comptabilite/ecritures", headers=headers)
            
            print("=== PUBLIC COLIS TRACKING ===")
            
            # Public tracking
            self.test_endpoint("Tracking colis public", "GET", f"{BASE_URL}/api/colis/tracking/TEST001")
        else:
            print("Cannot test protected endpoints: no token obtained")
            self.results.append({"test": "Protected endpoints", "result": "SKIPPED"})
        print()
        
        # 4. HTML pages tests
        print("=== HTML PAGES TESTS ===")
        pages = ["/", "/login", "/dashboard"]
        for page in pages:
            self.test_endpoint(f"Page {page}", "GET", f"{BASE_URL}{page}", expected_success=False)
        print()
        
        # 5. Results summary
        self.print_summary()
    
    def print_summary(self):
        """Print test summary"""
        print("=" * 50)
        print("TEST SUMMARY")
        print("=" * 50)
        print()
        
        success_count = sum(1 for r in self.results if r["result"] == "SUCCESS")
        fail_count = sum(1 for r in self.results if r["result"] in ["FAILED", "ERROR"])
        skip_count = sum(1 for r in self.results if r["result"] == "SKIPPED")
        
        for result in self.results:
            if result["result"] == "SUCCESS":
                print(f"✓ {result['test']}: {result['result']}")
            elif result["result"] == "SKIPPED":
                print(f"○ {result['test']}: {result['result']}")
            else:
                print(f"✗ {result['test']}: {result['result']}")
        
        print()
        print(f"Total: {len(self.results)} tests")
        print(f"Success: {success_count}")
        print(f"Failed: {fail_count}")
        print(f"Skipped: {skip_count}")
        print()
        
        if fail_count == 0:
            print("ALL TESTS PASSED! ✓")
        else:
            print(f"SOME TESTS FAILED! ({fail_count} failures)")
        print()

if __name__ == "__main__":
    tester = APITester()
    tester.run_tests()
