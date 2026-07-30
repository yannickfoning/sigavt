/**
 * Exemple de client JS pour brancher la maquette HTML (sigavt.html) sur l'API
 * Spring Boot. A inclure dans la page via <script src="api-client.js"></script>
 * puis a completer page par page (billets, colis, personnel, etc.).
 */

const API_BASE_URL = "http://localhost:8081/api";

const SigavtApi = {
  token: localStorage.getItem("sigavt_token") || null,

  headers() {
    const h = { "Content-Type": "application/json" };
    if (this.token) h["Authorization"] = "Bearer " + this.token;
    return h;
  },

  async login(email, motDePasse) {
    const res = await fetch(`${API_BASE_URL}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, motDePasse }),
    });
    if (!res.ok) throw new Error("Identifiants incorrects");
    const data = await res.json();
    this.token = data.token;
    localStorage.setItem("sigavt_token", data.token);
    return data;
  },

  async getDashboard() {
    const res = await fetch(`${API_BASE_URL}/dashboard`, { headers: this.headers() });
    return res.json();
  },

  async getLignes() {
    const res = await fetch(`${API_BASE_URL}/lignes`, { headers: this.headers() });
    return res.json();
  },

  async getVoyages(date) {
    const url = date ? `${API_BASE_URL}/voyages?date=${date}` : `${API_BASE_URL}/voyages`;
    const res = await fetch(url, { headers: this.headers() });
    return res.json();
  },

  async vendreBillet(payload) {
    // payload: { voyageId, numeroSiege, passagerNom, passagerTelephone, typeTarif, modePaiement }
    const res = await fetch(`${API_BASE_URL}/billets`, {
      method: "POST",
      headers: this.headers(),
      body: JSON.stringify(payload),
    });
    if (!res.ok) throw new Error((await res.json()).message || "Erreur lors de la vente");
    return res.json();
  },

  async enregistrerColis(payload) {
    const res = await fetch(`${API_BASE_URL}/colis`, {
      method: "POST",
      headers: this.headers(),
      body: JSON.stringify(payload),
    });
    return res.json();
  },

  async suivreColis(numeroTracking) {
    const res = await fetch(`${API_BASE_URL}/colis/tracking/${numeroTracking}`);
    if (!res.ok) throw new Error("Colis introuvable");
    return res.json();
  },

  async getPersonnel() {
    const res = await fetch(`${API_BASE_URL}/personnel`, { headers: this.headers() });
    return res.json();
  },

  async getBus() {
    const res = await fetch(`${API_BASE_URL}/bus`, { headers: this.headers() });
    return res.json();
  },
};

// Exemple d'utilisation au chargement du tableau de bord :
// SigavtApi.login("admin@sigavt.cm", "password")
//   .then(() => SigavtApi.getDashboard())
//   .then(data => console.log(data));
