const { chromium } = require('playwright');

async function testAllButtons() {
    const browser = await chromium.launch({ headless: true });
    const page = await browser.newPage();
    
    console.log("=== DÉBUT DES TESTS DES BOUTONS SIGAVT ===\n");
    
    // Configuration initiale pour éviter la redirection login
    await page.addInitScript(() => {
        localStorage.setItem('token', 'test-token');
        localStorage.setItem('user', JSON.stringify({id: 1, email: 'admin@sigavt.cm', role: 'ADMIN'}));
    });
    
    // Naviguer vers la page principale
    console.log("1. Navigation vers http://localhost:8080/sigavt.html");
    try {
        await page.goto("http://localhost:8080/sigavt.html", { timeout: 10000 });
        console.log("   ✓ Page chargée avec succès");
    } catch (e) {
        console.log(`   ✗ Erreur de chargement: ${e.message}`);
        await browser.close();
        return;
    }
    
    // Attendre que la page soit chargée
    await page.waitForTimeout(2000);
    
    // Tests des boutons de navigation dans la sidebar
    console.log("\n2. TEST DES BOUTONS DE NAVIGATION");
    const navTests = [
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
    ];
    
    for (const navItem of navTests) {
        try {
            const selector = `.nav-item:has-text("${navItem}")`;
            const element = await page.$(selector);
            if (element) {
                await element.click();
                await page.waitForTimeout(500);
                console.log(`   ✓ Bouton '${navItem}' cliqué avec succès`);
            } else {
                console.log(`   ✗ Bouton '${navItem}' non trouvé`);
            }
        } catch (e) {
            console.log(`   ✗ Erreur sur bouton '${navItem}': ${e.message}`);
        }
    }
    
    // Tests des boutons d'action (+ Nouveau)
    console.log("\n3. TEST DES BOUTONS D'ACTION (+ NOUVEAU)");
    try {
        const ctaButton = await page.$('.cta-button');
        if (ctaButton) {
            await ctaButton.click();
            await page.waitForTimeout(1000);
            console.log("   ✓ Bouton '+ Nouveau' cliqué");
            
            // Vérifier si une modale s'ouvre
            const modal = await page.$('.modal.is-open');
            if (modal) {
                console.log("   ✓ Modale ouverte après clic sur + Nouveau");
            } else {
                console.log("   ✗ Aucune modale ouverte après clic sur + Nouveau");
            }
        } else {
            console.log("   ✗ Bouton '+ Nouveau' non trouvé");
        }
    } catch (e) {
        console.log(`   ✗ Erreur sur bouton + Nouveau: ${e.message}`);
    }
    
    // Tests des boutons dans les formulaires
    console.log("\n4. TEST DES BOUTONS DE FORMULAIRE");
    const formButtons = [
        { name: "Boutons 'Suivant'", selector: ".btn-next" },
        { name: "Boutons 'Précédent'", selector: ".btn-prev" },
        { name: "Boutons 'Annuler'", selector: ".btn-cancel" },
        { name: "Boutons 'Valider'", selector: ".btn-validate" },
        { name: "Boutons 'Enregistrer'", selector: ".btn-save" }
    ];
    
    for (const { name, selector } of formButtons) {
        const elements = await page.$$(selector);
        if (elements.length > 0) {
            console.log(`   ✓ ${elements.length} bouton(s) '${name}' trouvé(s)`);
        } else {
            console.log(`   ✗ Aucun bouton '${name}' trouvé`);
        }
    }
    
    // Tests des boutons de déconnexion
    console.log("\n5. TEST DES BOUTONS DE DÉCONNEXION");
    try {
        const logoutButton = await page.$('button.logout-btn, .logout-btn, button:has-text("Déconnexion")');
        if (logoutButton) {
            console.log("   ✓ Bouton de déconnexion trouvé");
        } else {
            console.log("   ✗ Bouton de déconnexion non trouvé");
        }
    } catch (e) {
        console.log(`   ✗ Erreur sur bouton déconnexion: ${e.message}`);
    }
    
    // Tests des boutons de tableau (actions sur les lignes)
    console.log("\n6. TEST DES BOUTONS DE TABLEAU");
    const tableActions = [
        { name: "Boutons Modifier", selector: ".btn-edit, button:has-text('Modifier')" },
        { name: "Boutons Supprimer", selector: ".btn-delete, button:has-text('Supprimer')" },
        { name: "Boutons Voir", selector: ".btn-view, button:has-text('Voir')" }
    ];
    
    for (const { name, selector } of tableActions) {
        const elements = await page.$$(selector);
        if (elements.length > 0) {
            console.log(`   ✓ ${elements.length} bouton(s) '${name}' trouvé(s)`);
        } else {
            console.log(`   ✗ Aucun bouton '${name}' trouvé`);
        }
    }
    
    // Tests des boutons de paiement
    console.log("\n7. TEST DES BOUTONS DE PAIEMENT");
    const paymentButtons = [
        { name: "Boutons Orange Money", selector: ".btn-orange-money" },
        { name: "Boutons MTN MoMo", selector: ".btn-mtn-momo" },
        { name: "Boutons Espèces", selector: ".btn-cash" }
    ];
    
    for (const { name, selector } of paymentButtons) {
        const elements = await page.$$(selector);
        if (elements.length > 0) {
            console.log(`   ✓ ${elements.length} bouton(s) '${name}' trouvé(s)`);
        } else {
            console.log(`   ✗ Aucun bouton '${name}' trouvé`);
        }
    }
    
    // Résumé
    console.log("\n=== FIN DES TESTS ===");
    console.log("Application disponible sur: http://localhost:8080/sigavt.html");
    console.log("Documentation API: http://localhost:8080/swagger-ui.html");
    
    await browser.close();
}

testAllButtons().catch(console.error);