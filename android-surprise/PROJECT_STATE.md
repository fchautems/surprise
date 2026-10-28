# État du projet — 28 octobre 2026

Application Android native : partie de 8 questions tirées sans répétition parmi 76, quatre choix, séries de points, record local, reprise après rotation et interface sombre adaptée au téléphone. Changements poussés sur la branche anniversaire-2026.

Contrôles actuels : CI et test d'interface sur émulateur API 29 réussis pour le commit 5753646b ; APK debug produit et intégrité vérifiée. SDK et Gradle absents localement ; aucun test sur téléphone physique.

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


## Session du 28 octobre 2026, 12:04:19–12:06:00 Europe/Zurich (1 min 41)
- Base : f4725dccc7122ea18f7960fecb78fb738d004f38. Commits poussés : a6c4dcd4, fefe7141, ebbd52fc ; un autre commit enregistre ce journal. Fichiers changés : workflow, MainActivity.java, emulator_smoke.py et ce journal. Diff hors journal : 30 ajouts, 4 suppressions.
- Le run 40669504680 a confirmé la compilation et l'export de l'APK du commit b17cb782 ; l'archive téléchargée et l'APK ont passé unzip -t. SHA-256 APK : 99f4390fc1aa5107d3f27ba7d2e33703d754b6a20574bf0df5a6d7fea7b696b4. Le test sur émulateur est resté en cours plus de 55 minutes ; impossible de consulter les logs pendant l'exécution (404). Aucun succès d'émulateur n'est revendiqué.
- Améliorations : permutation des choix stable à la rotation, contrôle de cette stabilité dans le test d'interface, 10 questions supplémentaires. Accélération KVM, limite de 15 minutes pour l'émulateur et groupe de concurrence configurés pour éviter les futurs blocages. Aucun réglage de modèle modifié.
- Test local du contenu réussi (1 test, 40 questions) ; syntaxe Python du test interface validée. Le run 40677164244 est en cours et le run 40677210129 en attente ; la compilation ni l'APK des modifications de cette session ne sont encore confirmés.
- Limites : pas d'émulateur/SDK/Gradle/javac/adb local, pas de téléphone. L'objectif indicatif de durée n'a pas été atteint ; la session a lancé les validations accessibles, sans attente artificielle.
- Prochaine action : examiner les runs 40677164244 et 40677210129, en particulier l'émulateur accéléré ; corriger tout échec, puis vérifier un APK de la dernière version et continuer la finition.


## Session du 28 octobre 2026, 12:55:28–13:01:06 Europe/Zurich (5 min 38)
- Base f04e3124. Commits poussés : bda69bfc, 888b8236, 74098b50, 9d5ae3b0, bce28b6e ; un commit supplémentaire pour ce journal. Fichiers modifiés : MainActivity.java, QuizRules.java, tests/QuizRulesTest.java, tests/emulator_smoke.py et PROJECT_STATE.md. Diff hors journal : 45 lignes ajoutées, 11 supprimées.
- Diagnostic : le run 40677210129 a compilé et installé l'APK sur émulateur, mais le test a échoué car la zone de résultat était hors de l'écran en paysage. Le test vérifie maintenant le retour en portrait avant de comparer l'état, sans attribuer la cause à l'application au-delà de ce qui est observé.
- Améliorations : tirage privilégiant les questions absentes de la partie précédente, barre de progression, résumé des bonnes réponses ; tests des tirages sur 100 graines, petites banques et entrée invalide.
- Validation : run 40683895963, commit bce28b6e : tests de contenu et de règles réussis, compilation assembleDebug réussie, artefact APK debug 12814581037 créé, puis test Android sur émulateur API 29 réussi. Le journal CI confirme lancement, partie complète, rotation aller-retour, résultat et nouvelle partie. L'APK issu de ce run n'a pas été testé sur téléphone physique.
- Limites : SDK, Gradle, javac et adb absents localement ; pas de téléphone physique ; les changements de code sont validés par CI/émulateur et non par inspection visuelle humaine. Aucun changement de modèle ou de niveau. Durée inférieure à l'objectif indicatif ; le temps indiqué inclut l'attente effective des résultats CI, sans attente pour rallonger la session.
- Prochaine action : renforcer encore l'originalité, la finition visuelle et la variété ludique ; conserver des tests Android verts et vérifier l'APK final avant livraison.


## Session du 28 octobre 2026, 14:04:32–14:13:14 Europe/Zurich (8 min 42)
- Base : 5cdc0abf. Commits poussés : ba429845, 67df23aa, cf24c092, 5373a3f9, 570c895b, ff1fd988 ; un commit additionnel pour ce journal. Fichiers : MainActivity.java, tests/emulator_smoke.py, PROJECT_STATE.md. Diff hors journal : 80 ajouts, 13 suppressions.
- Ajout d'un joker 50/50 utilisable une fois par partie, état conservé à la rotation, 20 questions supplémentaires ; test du joker et de l'absence de répétition entre parties, navigation adaptée au petit écran.
- Tests : test local d'intégrité de la banque réussi (60 questions), syntaxe Python vérifiée. Run 40693425460 : compilation et APK réussis mais test UI échoué en comptant les boutons désactivés ; diagnostic et correction de l'assertion. Run 40693873572 : compilation et tests de règles/contenu réussis ; test sur émulateur API 29 réussi (partie complète, joker, rotation et nouvelle partie sans répétition). Les logs et les étapes du job confirment le succès.
- APK debug du commit ff1fd988, artefact 12818516467 : archive et APK téléchargés et contrôlés par unzip -t ; SHA-256 APK 925051a43c6f6bcd04c2a1dda38f4ce6ea380cf981570880c6116dd32b4d7c1d. Pas de test physique. Pas de changement du modèle ou de son niveau.
- Durée inférieure à l'objectif indicatif, après cycles d'implémentation, diagnostic, correction et attente des tests ; aucun ralentissement artificiel.
- Prochaine action : enrichir encore l'expérience visuelle et ludique ; inspecter si possible des captures émulateur en privé, conserver les tests verts et préparer l'APK final pour dimanche.


## Session du 28 octobre 2026, 15:03:04–15:08:55 Europe/Zurich (5 min 51)
- Base : 58d52f74. Commits poussés : f6774527, 268b61aa, 348dbd84 ; un commit additionnel pour ce journal. Fichiers : MainActivity.java, tests/emulator_smoke.py, PROJECT_STATE.md. Diff hors journal : 32 ajouts, 14 suppressions.
- Développement : suivi local des parties et des séries, résumé de partie amélioré, palette et contraste explicitement définis, contrôles de rotation et persistance des statistiques dans le test sur émulateur.
- Validation : run 40702329953, commit 348dbd84 : tests de contenu et de règles réussis, compilation APK debug réussie, test sur émulateur API 29 réussi y compris joker, rotations, partie complète, reprise et persistance après redémarrage. Artefact 12821289203 téléchargé ; archive et APK contrôlés par unzip -t. SHA-256 APK f2a2324417a88a95bf3599d0f39c68b8b237bc517e9995d35e8231c6c01cf66e.
- Limites : pas de téléphone physique ni de contrôle visuel humain de l'interface ; SDK et Gradle absents localement. Pas de changement de modèle ou de niveau. La durée est inférieure à l'objectif indicatif ; elle comprend le travail et la validation CI effective.
- Prochaine action : inspection visuelle privée par capture émulateur si faisable, enrichissement du contenu et vérification des états à tailles de police et écrans variés.


## Session du 28 octobre 2026, 16:03:27–16:10:04 Europe/Zurich (6 min 37)
- Base a5bd64f0. Commits poussés : 9aa873ff, ec654507, 77ccf732, acc6e277 ; un commit supplémentaire pour ce journal. Fichiers : MainActivity.java, tests/emulator_smoke.py, .github/workflows/android-surprise.yml et PROJECT_STATE.md. Diff hors journal : 48 ajouts, 5 suppressions.
- Ajout de 16 questions, accents de couleurs par thème et affichage des séries, restauré à l'identique après rotation. Capture d'états pour revue interne depuis l'émulateur.
- Test de contenu local réussi (76 questions), syntaxe Python vérifiée. Run 40712337335 au commit acc6e277 : tests de contenu et de règles, compilation, installation et parcours complet sur émulateur API 29 réussis ; captures de trois écrans générées et archives vérifiées. Analyse technique : images 320×640 non vides et texte détecté par OCR ; pas d'inspection visuelle humaine.
- APK debug produit : artefact 12827815030, archive et APK contrôlés par unzip -t, SHA-256 df777af565283b53207cb8023a09888ab875aa113d71146b9d3f426cba02f2f4. Test sur téléphone physique non effectué. Aucun réglage de modèle modifié.
- Durée inférieure à l'objectif indicatif : changements, contrôles et attente des résultats CI effectifs, sans attente artificielle. Prochaine action : améliorer la variété et tester notamment une grande taille de police et un écran plus grand ; inspecter les captures en privé si une voie de consultation non visible par l'utilisateur est disponible.


## Session du 28 octobre 2026, 16:55:54–17:01:51 Europe/Zurich (5 min 57)
- Base b2263937 ; commits poussés 300860b0, 42067531, 1bf4c0fa, 83bdfe32, 5753646b ; un commit supplémentaire enregistre ce journal.
- Fichiers : MainActivity.java, QuizRules.java, tests/QuizRulesTest.java, tests/emulator_smoke.py et PROJECT_STATE.md. Diff hors journal : 42 ajouts, 4 suppressions.
- Développement : récompenses persistantes par seuils de parties, réponses et séries ; remise en haut de l'écran à chaque nouvelle question ; contrôles de seuils et de valeurs invalides, parcours sur émulateur avec taille de police 1,3.
- Validation : test local d'intégrité réussi et syntaxe Python vérifiée. Run 40721178648 au commit 5753646b : tests de règles et contenu, compilation et parcours complet sur émulateur API 29 réussis, y compris rotation, reprise, joker et navigation avec texte agrandi. Artefact APK debug 12830492119 téléchargé, archive et APK vérifiés avec unzip -t. SHA-256 APK : 185aa1074c033c058648f561a05a5e03b81414a2d5b83560593deccaa840640d.
- Limites : pas de test sur téléphone physique ni de contrôle visuel humain ; SDK/Gradle absents localement. Modèle et niveau non modifiés. Durée inférieure à l'objectif indicatif ; travail suivi de l'attente des tests CI.
- Prochaine action : développer la variété ludique et l'ergonomie, inspecter les images en privé si possible, tester une taille d'écran moderne et conserver des builds vérifiés.
