# État du projet — 28 octobre 2026

Application Android native : partie de 8 questions tirées sans répétition parmi 16, quatre choix, séries de points, record local, reprise après rotation et interface sombre adaptée au téléphone. Changements poussés sur la branche anniversaire-2026.

Contrôles : analyse automatisée du contenu vérifiant 16 questions, quatre choix distincts et nombre suffisant pour une partie. Compilation et test sur appareil non effectués : SDK et Gradle absents localement. Workflow GitHub Actions ajouté pour construire et publier un APK de debug ; son exécution et son résultat restent à vérifier. Aucun APK vérifié à ce stade.

Points à vérifier : parcours complet, rotation après réponse et fin de partie, affichage sur petits écrans, compilation effective et récupération de l’APK. Enrichir l’expérience et son contenu. Garder le projet secret jusqu’au 1er novembre.


## Session du 28 octobre 2026, 09:41:20–09:44:00 Europe/Zurich (environ 2 min 40)
- Base de départ : 559dc7ad2a0b0e09c52fd6ad768862078ba5d305. Six commits poussés : 1bdefeb4, f25cd8ee, a822e0df, a7a41e9a, f7b3ac53, b7b69691. Le présent fichier fait l'objet d'un commit supplémentaire.
- Fichiers modifiés : MainActivity.java, QuizRules.java, tests/QuizRulesTest.java, tests/test_bank.py, .github/workflows/android-surprise.yml et PROJECT_STATE.md. Diff des cinq fichiers de code/configuration avant ce journal : 74 lignes ajoutées, 5 supprimées.
- Changements : banque enrichie à 30 questions distinctes, points isolés en Java et testés, affichage des points préservé après rotation, vérification automatique des données et des règles ajoutée au workflow.
- Contrôles : test Python du contenu réussi localement (1 test) et en CI ; test Java des scores réussi en CI (20 séries, entrées invalides). Java local ne possède pas javac ni SDK ni Gradle. Compilation Gradle et production d'APK réussies en CI pour a7a41e9a (run 40658782305, artefact 12803410818). L'ancienne archive APK du run 40646993824 a été téléchargée et son intégrité ZIP vérifiée ; cela ne valide pas le fonctionnement Android.
- La compilation du commit intermédiaire 1bdefeb4 a échoué car la classe ajoutée au commit suivant n'existait pas encore ; les compilations ultérieures f25cd8ee, a822e0df et a7a41e9a ont réussi. Le run du commit b7b69691 est en cours, ses deux tests ont réussi, compilation non encore confirmée. Aucun test sur téléphone ni émulateur. Aucun modèle ou niveau n'a été modifié.
- Prochaine action : vérifier le run b7b69691 et l'artefact correspondant ; tester sur émulateur ou appareil si disponible, puis améliorer substantiellement visuel et rejouabilité. Durée inférieure à l'objectif indicatif ; la session s'arrête après ces vérifications et modifications, sans attente artificielle.


## Session du 28 octobre 2026, 11:04:02–11:06:18 Europe/Zurich (2 min 16)
- Base 7af4e5a9 ; commits poussés 99629d7a, 1c531912, e4c0fa02, 0e4cbd89 et a89ededa ; un commit supplémentaire enregistre ce journal.
- Fichiers : MainActivity.java, tests/emulator_smoke.py, .github/workflows/android-surprise.yml et PROJECT_STATE.md. Avant journal : 101 lignes ajoutées et 12 supprimées dans les trois fichiers de code/configuration.
- Améliorations : accueil, bilan plus lisible, affichage du score à jour, tailles de boutons adaptables et conservation des données de partie après rotation. Ajout d'un test d'interface sur émulateur couvrant lancement, partie complète, rotation, résultat et nouvelle partie. Le journal seul ne relance plus la compilation.
- Validation : contrôle local Python de la banque réussi ; syntaxe Python du test émulateur validée ; tests du contenu et des scores réussis dans les runs 40669345484 et 40669373304. Run initial 40658936321 : compilation réussie et APK debug présent (artefact 12803171751, commit 7af4e5a9). La compilation des changements de cette session et le nouveau test sur émulateur sont encore en cours ; ils ne sont pas déclarés réussis.
- Limites : absence locale de SDK Android, Gradle, javac, adb et appareil. Pas de test confirmé sur émulateur ni téléphone physique. Modèle et effort non modifiés. La session a duré moins que la durée indicative ; les vérifications CI ont été lancées et restent à contrôler au prochain passage.
- Prochaine action : consulter le résultat des runs 40669345484, 40669373304 et du run associé à a89ededa ; diagnostiquer tout échec de l'émulateur, vérifier l'APK et poursuivre l'amélioration visuelle et ludique.


### Complément à 11:06:47
- Les runs 40669294053, 40669327985 et 40669345484 ont échoué à la compilation : caractères littéraux inversés introduits dans MainActivity.java. La cause a été lue dans les logs.
- Correction poussée : b17cb782 ; vérification textuelle de l'absence de ces séquences hors chaînes réussie. Nouveau workflow lancé par ce commit : résultat encore inconnu. La version la plus récente ne possède pas encore d'APK confirmé ; la dernière version compilée avec succès reste 7af4e5a9.
- Durée cumulée de la session : environ 2 min 45. La comparaison chiffrée ci-dessus précède le correctif ; consulter le diff complet au prochain passage. Prochaine action impérative : inspecter la compilation du correctif et le test émulateur, corriger tout échec.
