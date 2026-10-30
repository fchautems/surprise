# Installation et livraison

Cette application Android fonctionne hors ligne et nécessite Android 8.0 (API 26) ou une version plus récente. Les validations automatiques couvrent des émulateurs Android 10 et 15 ; aucun test sur téléphone physique ni contrôle humain du son ou de l'apparence n'a été effectué.

## Récupérer l'APK

1. Ouvrir dans GitHub le workflow Android du commit de livraison sur la branche anniversaire-2026. Se connecter à un compte autorisé pour le dépôt si nécessaire.
2. Télécharger l'artefact `android-debug-apk` du workflow réussi, puis extraire le fichier `app-debug.apk` de l'archive ZIP.
3. Transférer l'APK sur le téléphone si le téléchargement a été effectué ailleurs.

## Installer sur Android

1. Sur le téléphone, ouvrir le fichier APK depuis le gestionnaire de fichiers ou les téléchargements.
2. Si Android le demande, autoriser temporairement l'installation depuis l'application qui ouvre le fichier, puis revenir à l'installation.
3. Installer et ouvrir l'application. On peut ensuite retirer l'autorisation donnée à cette source.

Il s'agit d'un APK de débogage signé par l'environnement de compilation, pas d'une publication Google Play. Les clés de débogage peuvent différer d'un build CI à l'autre : une mise à jour peut être refusée si les signatures diffèrent. Désinstaller l'ancienne version efface ses données locales ; conserver la version précédente si l'on souhaite les préserver. Les artefacts CI expirent : vérifier la disponibilité du fichier de livraison avant de le transmettre.
