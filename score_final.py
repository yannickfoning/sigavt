# score_final.py — Lancer avec : python score_final.py
import urllib.request, json, time, sys

BASE = "http://localhost:8080"
resultats = []

def http(url, method="GET", data=None, token=None):
    h = {"Content-Type": "application/json"}
    if token: h["Authorization"] = f"Bearer {token}"
    try:
        r = urllib.request.Request(f"{BASE}{url}",
            data=json.dumps(data).encode() if data else None,
            headers=h, method=method)
        resp = urllib.request.urlopen(r, timeout=8)
        try: body = json.loads(resp.read())
        except: body = {}
        return body, resp.getcode()
    except urllib.error.HTTPError as e:
        try: body = json.loads(e.read())
        except: body = {}
        return body, e.code
    except Exception as ex:
        return {"error": str(ex)}, 0

def t(categorie, nom, ok, detail=""):
    resultats.append((categorie, nom, ok, detail))
    icone = "✅" if ok else "❌"
    print(f"  {icone} {nom}" + (f"  [{detail}]" if detail and not ok else ""))

# ── Login ──
print("\n🔐 AUTH")
d, c = http("/api/auth/login", "POST", {"email":"admin@sigavt.cm","motDePasse":"admin123"})
tok = d.get("token", d.get("accessToken",""))
t("Auth", "Login admin OK", bool(tok) and c < 400, f"HTTP {c}")
_, c = http("/api/auth/login","POST",{"email":"x@x.cm","motDePasse":"faux"})
t("Auth", "Mauvais mdp → 401", c == 401, f"HTTP {c}")
_, c = http("/api/dashboard/stats")
t("Auth", "Sans token → 401", c == 401, f"HTTP {c}")

# ── Sécurité ──
print("\n🔒 SÉCURITÉ")
insc, _ = http("/api/auth/inscription","POST",{"nom":"H","email":"h99@t.cm","motDePasse":"p","role":"SUPERADMIN"})
role_hack = any(r in str(insc).upper() for r in ["SUPERADMIN","ADMIN"])
t("Sécurité", "Inscription sans escalade de rôle", not role_hack, "CRITIQUE: rôle ADMIN accordé publiquement !")
_, c = http("/api/billets/999999", token=tok)
t("Sécurité", "ID inexistant → 404", c == 404, f"HTTP {c}")
_, c = http("/api/colis/tracking/COL-2025-00001")
t("Sécurité", "Tracking public sans token → 200", c == 200, f"HTTP {c}")

# ── Pages HTML ──
print("\n🌐 PAGES")
for path, label in [("/","Accueil"),("/login","Login"),("/dashboard","Dashboard")]:
    _, c = http(path)
    t("Pages", f"HTTP 200 sur {label}", c == 200, f"HTTP {c}")

# ── API Métier ──
print("\n🎯 API")
endpoints = [
    ("/api/dashboard/stats",                    "Stats dashboard"),
    ("/api/dashboard/departs",                  "Départs du jour"),
    ("/api/dashboard/recettes-semaine",         "Recettes semaine"),
    ("/api/dashboard/top-lignes",               "Top lignes"),
    ("/api/voyages?page=0&size=5",              "Liste voyages"),
    ("/api/billets?page=0&size=5",              "Liste billets"),
    ("/api/colis?page=0&size=5",                "Liste colis"),
    ("/api/employes?page=0&size=5",             "Liste employés"),
    ("/api/paie/stats?mois=5&annee=2025",       "Stats paie"),
    ("/api/paie?mois=5&annee=2025&page=0&size=5","Liste fiches paie"),
    ("/api/bus?page=0&size=10",                 "Liste bus"),
    ("/api/lignes",                             "Liste lignes"),
    ("/api/compta/ecritures?page=0&size=5",     "Écritures"),
    ("/api/compta/bilan?mois=5&annee=2025",     "Bilan mensuel"),
    ("/api/courriers",                          "Courriers"),
]
for url, label in endpoints:
    _, c = http(url, token=tok)
    t("API", label, c < 400, f"HTTP {c}")

# ── Pagination ──
print("\n📄 PAGINATION")
for url, label in [("/api/billets?page=0&size=5","Billets"),
                   ("/api/colis?page=0&size=3","Colis"),
                   ("/api/employes?page=0&size=3","Employés")]:
    d, _ = http(url, token=tok)
    pagine = "content" in d or "totalElements" in d or ("data" in d and "total" in d)
    t("Pagination", f"{label} paginée", pagine, "Manque content/totalElements")

# ── Rapport ──
total = len(resultats)
passes = sum(1 for _,_,ok,_ in resultats if ok)
fails  = total - passes
pct    = round(passes/total*100) if total else 0

print(f"\n{'='*55}")
print(f"  SCORE FINAL : {passes}/{total} tests ({pct}%)")
if fails == 0:    print("  🎉 PLATEFORME 100% OPÉRATIONNELLE !")
elif fails <= 3:  print("  ✅ Très bon — corriger les derniers points ❌")
elif fails <= 8:  print("  ⚠️  Quelques corrections nécessaires")
else:             print("  ❌ Corrections importantes requises")

if fails:
    print(f"\n  {fails} TEST(S) ÉCHOUÉ(S) :")
    for cat, nom, ok, detail in resultats:
        if not ok:
            print(f"    ❌ [{cat}] {nom}" + (f" → {detail}" if detail else ""))
print("="*55)
