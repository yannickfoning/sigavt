# ⚠️ Avertissement - Code JavaScript Frontend

## Fichiers JS non chargés

Les fichiers JavaScript suivants existent dans `static/` mais **ne sont pas chargés** par les pages HTML actuelles :

- **`api-client.js`** : Client API pour l'intégration frontend
- **`app.js`** : Application JavaScript principale (2732 lignes)

## Pages HTML actuelles

Les pages existantes (`login.html`, `sigavt.html`) utilisent uniquement des scripts **inline** et ne chargent aucun fichier JS externe.

## Pourquoi ces fichiers existent ?

Ces fichiers semblent être :
- Du code de développement ou de prototype
- Des utilitaires pour une future intégration
- Du code mort provenant d'une ancienne version

## Recommandation

1. **Ne pas modifier** ces fichiers en pensant qu'ils affectent l'application actuelle
2. Si vous voulez les utiliser, ajoutez-les explicitement dans les pages HTML :
   ```html
   <script src="api-client.js"></script>
   <script src="app.js"></script>
   ```
3. Sinon, envisagez de les supprimer ou de les déplacer dans un dossier `archive/`

## Endpoint corrigé

Le fichier `api-client.js` contenait un endpoint incorrect pour le suivi de colis :
- **Avant** : `/api/colis/suivi/{numero}` (incorrect)
- **Après** : `/api/colis/tracking/{numero}` (corrigé pour correspondre au contrôleur)