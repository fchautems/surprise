# État du projet — 28 octobre 2026

Application Android native : partie de 8 questions tirées sans répétition parmi 116, quatre choix, séries de points, record local, reprise après rotation et interface sombre adaptée au téléphone. Changements poussés sur la branche anniversaire-2026.

Contrôles actuels : CI 40853835903 intégralement réussie sur le commit 0ac081e3, y compris parcours API 29 et 35 ; APK debug produit et intégrité vérifiée. SDK et Gradle absents localement ; aucun test sur téléphone physique.

Points à approfondir : qualité visuelle et sonore par une personne et fonctionnement sur appareil physique. Enrichir l’expérience et son contenu. Garder le projet secret jusqu’au 1er novembre.


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


## Session du 28 octobre 2026, 18:03:04–18:09:17 Europe/Zurich (6 min 13)
- Base 8d29dfe2 ; commits poussés 97628090, 706bd315, 55aaf0fe et un commit additionnel pour ce journal.
- Fichiers : MainActivity.java, tests/emulator_smoke.py, tests/test_bank.py et PROJECT_STATE.md ; diff hors journal : 57 ajouts, 8 suppressions.
- Développement : mode libre et six thèmes au choix, répétition du thème lors d'une revanche, retour à l'accueil, conservation du choix pendant la rotation ; adaptation du tirage au sous-ensemble sélectionné. Une question ajoutée pour assurer huit questions dans chaque thème. Lorsque le thème contient seulement huit questions, les mêmes questions reviennent nécessairement, dans un ordre mélangé.
- Validation : test local d'intégrité et de taille minimale des six thèmes réussi ; syntaxe Python vérifiée. Run 40732274979, commit 55aaf0fe : compilation, tests de règles et de contenu et parcours sur émulateur API 29 réussis, y compris taille de texte agrandie, rotations, joker, sélection du thème MUSIQUE et restauration du menu. Les autres thèmes ont un contrôle de taille mais n'ont pas été joués séparément sur émulateur.
- APK debug : artefact 12834882984 téléchargé ; archive et APK passent unzip -t ; SHA-256 APK deaf6eb2ed3ccd893e0f5a3769e6242c0c4f657729195685158e4efba660933c. Aucun test sur téléphone physique ni contrôle visuel humain. Modèle et niveau inchangés.
- Durée inférieure à l'objectif indicatif, incluant la validation CI effective. Prochaine action : enrichir les thèmes les plus courts pour limiter les répétitions, tester chaque thème et améliorer la finition visuelle.


## Session du 28 octobre 2026, 19:04:50–19:11:51 Europe/Zurich (7 min 01)
- Base 8fdfcf27 ; commits poussés 9f6a6a9f, b9649d4d, 9bdc2e9a et 3db7b758 ; un commit supplémentaire enregistre ce journal. Fichiers : MainActivity.java, tests/test_bank.py, tests/emulator_smoke.py, tests/QuizRulesTest.java et PROJECT_STATE.md. Diff hors journal : 43 lignes ajoutées, 2 supprimées.
- Développement : 23 questions supplémentaires, 100 au total et au moins 16 par thème, afin de permettre deux parties consécutives de huit sans répétition dans un thème. Tests renforcés : seuil minimal par thème, tirage sans répétition sur 100 graines et entrée dans chacun des six thèmes sur émulateur.
- Validation : test Python local de la banque et compilation syntaxique Python réussis ; CI 40742105182 sur 3db7b758 terminée avec succès : tests de contenu et règles, compilation Gradle et parcours sur émulateur Android API 29 réussis, dont six entrées thématiques. Les runs intermédiaires ont été annulés automatiquement après les commits suivants.
- APK debug : artefact 12838878597 téléchargé ; archive et APK contrôlés avec unzip -t ; SHA-256 APK 9bfadff6e94296333c0da332b076eb11b89dcdddeaa3c4c385edd358aff367d6. Aucun test sur téléphone physique ni inspection visuelle humaine. SDK Android, Gradle et javac indisponibles localement ; compilation et tests Java réalisés en CI. Aucun réglage de modèle ou de niveau modifié.
- Durée inférieure à l'objectif indicatif ; changements et contrôles ci-dessus effectués, sans attente artificielle ni limite de quota mesurée. Prochaine action : améliorer la finition visuelle et l'intérêt des parties, poursuivre les contrôles sur émulateur et prévoir une validation sur appareil physique.


## Session du 28 octobre 2026, 19:56:26–20:11:50 Europe/Zurich (15 min 24)
- Base 66096bd6 ; commits poussés fa672aea, d41c3f6b, 1dd7de81 et bd1278a8 ; un commit supplémentaire consigne ce journal. Fichiers modifiés : MainActivity.java, tests/emulator_smoke.py et PROJECT_STATE.md. Diff hors journal : 60 lignes ajoutées, 8 supprimées.
- Développement : récapitulatif consultable des réponses manquées ; conservation du contenu après rotation sans doublon ; confirmation avant de quitter une partie et navigation de retour adaptée aux autres écrans.
- Contrôles locaux : intégrité de la banque Python et compilation syntaxique du test Python réussies. CI 40749932471 : compilation et APK réussis mais test émulateur échoué sur la recherche d'un bouton de dialogue. Recherche rendue insensible à la casse et diagnostic enrichi. CI 40750457177 : compilation, tests et émulateur réussis. Puis ajout de vérifications supplémentaires de la rotation du bilan et de l'abandon confirmé ; CI 40751365380 au commit bd1278a8 terminée avec succès, y compris compilation, tests de contenu et règles et parcours sur émulateur API 29.
- APK debug de la dernière validation : artefact 12843598016 téléchargé, archive et APK vérifiés avec unzip -t ; SHA-256 APK 3761d6d4852745017cc33e93ea91e70d09ba15dce322557e1935c246cb2447a7. L'APK est compilé et testé automatiquement sur émulateur, sans test sur téléphone physique ni inspection visuelle humaine.
- Limites : SDK/Gradle/javac absents localement, contrôles Android réalisés en CI ; modèle et niveau inchangés. Durée inférieure à l'objectif indicatif, incluant diagnostic, correction et attente effective de deux validations CI ; aucune limite de quota précise mesurée. Prochaine action : enrichir la dimension ludique et la finition visuelle, puis maintenir les tests et préparer la livraison.


## Session du 28 octobre 2026, 20:56:38–21:10:27 Europe/Zurich (13 min 49)
- Base d5bd937f ; commits poussés ce920a98, 2b6473d5, c0af9f7d et d66fe263 ; un commit supplémentaire consigne ce journal. Fichiers : MainActivity.java, tests/emulator_smoke.py et PROJECT_STATE.md. Diff hors journal : 75 lignes ajoutées, 10 supprimées.
- Développement : défi quotidien reproductible avec ordre et choix déterministes selon la date locale du téléphone, record du jour persistant et isolation des questions récentes du mode libre ; accents et contours colorés pour identifier thèmes et défi. Partie, date et bilan conservés après rotation.
- Tests locaux : intégrité Python de la banque et syntaxe du scénario émulateur réussies. CI 40759015873 au commit c0af9f7d : compilation, tests et émulateur réussis. Après ajout de contrôles de non-répétition, rotations et revanche du défi, CI 40759892597 au commit d66fe263 : compilation et tests réussis, parcours sur émulateur API 29 réussi, dont deux parties, défi reproductible, score parfait et conservation du record après redémarrage. Runs intermédiaires annulés automatiquement par les commits suivants.
- APK debug : artefact 12847187697 téléchargé, archive et APK vérifiés avec unzip -t ; SHA-256 APK 533e4d108c892737fbc9bfc971aee8ba24185cdd417e8e39f6f85144d368cee9. Aucun test sur téléphone physique ni inspection visuelle humaine. L'interface n'a été testée automatiquement que sur émulateur API 29.
- Limites : SDK Android, Gradle et javac absents localement ; compilation et tests Java réalisés en CI. Aucun réglage de modèle ou de niveau modifié. Durée inférieure à l'objectif indicatif, après implémentation et deux validations CI effectives ; aucune limite de quota précise mesurée. Prochaine action : approfondir la dimension ludique et contrôler l'affichage sur une version Android plus récente et sur appareil physique si accessible.


## Session du 28 octobre 2026, 22:03:57–22:34:54 Europe/Zurich (30 min 57)
- Base 5f1d7a3d ; commits poussés a0e8e07f, 983b772d, efbd24d7, 0829b06c, 76cc7284, 7632ee4a et 5f5a108f ; un commit supplémentaire consigne ce journal. Fichiers : MainActivity.java, tests/emulator_smoke.py, .github/workflows/android-surprise.yml et PROJECT_STATE.md. Diff hors journal : 86 lignes ajoutées, 11 supprimées.
- Développement : revanche qui remet en jeu les questions manquées, complétées par d'autres questions ; conservation de la revanche après rotation et protection de la partie quotidienne contre la mise à jour de son record par la revanche. Ajout d'un test Android 15 (API 35) utilisant le même APK que le parcours Android 10 (API 29).
- Contrôles locaux : intégrité de la banque Python, syntaxe Python, syntaxe YAML et analyse des coordonnées de boutons réussies. CI 40768982687 : compilation et parcours API 29 réussis, mais API 35 a quitté l'application durant le scénario. Les coordonnées du test ont été adaptées pour éviter la zone système ; la cause exacte n'a pas été prouvée. CI 40770277228 puis 40770737984 : erreurs du test introduites lors de cette adaptation (expression régulière, puis zone tactile trop restrictive), corrigées. CI 40771187656 au commit 5f5a108f : compilation, tests de données/règles et parcours sur émulateurs API 29 et 35 réussis, dont lancement et rotation de la revanche et partie quotidienne complète. Une revanche complète de huit réponses n'a pas été testée.
- APK debug : artefact 12851932964 téléchargé ; archive et APK passent unzip -t ; SHA-256 APK 84a082a0c2b27fc73cac4ca635bbf8090199f5dc2cd99f519ec5faf9402bcb35. Aucun test sur téléphone physique ni inspection visuelle humaine.
- Limites : SDK Android, Gradle et javac absents localement ; compilations et tests Android exécutés en CI. Aucun réglage de modèle ou de niveau modifié. Durée incluant développement, diagnostic, corrections et attente effective des CI ; aucune attente artificielle ni limite de quota précise mesurée. Prochaine action : tester une revanche complète, poursuivre la qualité ludique et visuelle et préparer la livraison.


## Session du 28 octobre 2026, 22:36:06–23:06:08 Europe/Zurich (30 min 02)
- Base 064af482 ; commits poussés 07f712bf, e93d4f7c et 57dd0dfd ; un commit supplémentaire enregistre ce suivi. Fichiers : MainActivity.java, tests/emulator_smoke.py et PROJECT_STATE.md. Diff hors journal : 27 lignes ajoutées, 8 supprimées.
- Développement : la revanche complète ses questions manquées par des questions nouvelles plutôt que celles qui viennent d'être réussies ; résultat adapté à la revanche et au sans-faute. Un scénario joue et vérifie toute la revanche, son score, sa diversité et la sortie vers une nouvelle partie.
- Contrôles locaux : intégrité de banque Python et syntaxe du scénario Python réussies. CI 40773386404 : compilation, tests et parcours complet sur API 29 réussis, y compris la revanche ; API 35 a échoué au contrôle de navigation avec texte agrandi. Le contrôle a été ajusté pour faire défiler vers le haut avant de lire l'état ; la cause exacte de l'échec reste à confirmer. Nouveau run 40775225634 pour 57dd0dfd : compilation et APK réussis, tests émulateurs encore en cours au moment de ce suivi.
- APK debug du nouveau run : artefact 12855285007 téléchargé ; archive et APK passent unzip -t ; SHA-256 34594a1dfb21250344293e8940524df2a007e9605e7e6fe33a1654240c992827. Pas de test physique ni d'inspection visuelle humaine. Prochaine action : inspecter les deux jobs du run 40775225634, diagnostiquer tout échec et compléter le journal avec la durée et les résultats finaux.

- Validation finale : run 40775225634 au commit 57dd0dfd terminé avec succès. Les parcours complets, dont revanche de huit réponses, score parfait, non-répétition et défi quotidien, ont réussi sur émulateurs API 29 et 35 ; les tests de données et règles et la compilation ont réussi. Le résultat est celui de cette version précise.
- Limites : aucun appareil physique ni inspection visuelle humaine ; SDK/Gradle/javac absents localement. Modèle et niveau non modifiés. Durée incluant développement, diagnostic, correction et attente effective des CI, sans attente artificielle ; limite de quota précise non mesurée. Prochaine action : poursuivre l'amélioration ludique et visuelle et préparer la livraison. Le suivi provisoire a été committé en fea45743 ; ce complément fait l'objet d'un dernier commit.


## Session du 28 octobre 2026, 23:10:56–23:47:47 Europe/Zurich (36 min 51)
- Base 4606859a ; commits poussés 929b3718, c249f837, a748d5c4, 5553b6f8, d2aec71b et 0040e1b8 ; un commit supplémentaire consigne ce journal. Fichiers : MainActivity.java, AndroidManifest.xml, res/drawable/ic_surprise.xml, tests/test_bank.py, tests/emulator_smoke.py et PROJECT_STATE.md. Diff hors journal : 51 lignes ajoutées, 5 supprimées.
- Développement : nouvelle catégorie crypto personnalisée, 16 questions, soit 116 questions au total et sept thèmes d'au moins 16 questions ; couleur dédiée, icône vectorielle installable et tests d'accès à toutes les catégories. Un second cycle de test parcourt entièrement la nouvelle catégorie et vérifie huit questions distinctes et un score parfait.
- Contrôles locaux : test d'intégrité de la banque et syntaxe Python réussis. CI 40778094988 au commit d2aec71b : compilation et parcours API 29/35 réussis. CI 40780277789 au commit 0040e1b8 : compilation, tests de contenu et règles, parcours complets API 29 et 35 réussis, y compris nouvelle catégorie, revanche et défi. Contrôle automatique de la banque structurel ; pas de revue factuelle externe exhaustive des nouvelles questions.
- APK debug : artefact 12856547444 téléchargé ; archive et APK passent unzip -t ; SHA-256 APK 0e72a28459219b71306cb2d00bf48627a6318d7a3e12c94c5e5f2229e032adb6. Aucun test sur téléphone physique ni inspection visuelle humaine. La compilation valide la ressource d'icône mais son rendu n'a pas été vérifié par une personne.
- Limites : SDK Android, Gradle et javac absents localement ; compilations et tests Android exécutés en CI. Modèle et niveau inchangés. Durée incluant implémentation, deux cycles de validation et attente effective de CI ; aucune attente artificielle ni limite précise de quota mesurée. Prochaine action : renforcer la variété ludique au-delà du quiz, contrôler les faits et poursuivre la finition.


## Session du 29 octobre 2026, 00:04:39–00:37:30 Europe/Zurich (32 min 51)
- Base c97eb5b7 ; commits poussés 9f83dd4e, 68d4f45f, d3cccc14, 3243c6c5, 6c40fcc2, b57b5975, 1806aed1, 4ac35a4a et a02eeb14 ; un commit supplémentaire consigne ce suivi. Fichiers : MemoryActivity.java, MainActivity.java, AndroidManifest.xml, tests/memory_smoke.py, .github/workflows/android-surprise.yml et PROJECT_STATE.md. Diff hors journal : 273 lignes ajoutées, 1 supprimée.
- Développement : jeu de mémoire autonome et sans minuterie, quatre paires, compteur d'essais, record local et conservation après rotation ; accès depuis l'accueil. Test d'interface qui découvre les cartes, vérifie la rotation, résout les quatre paires et revient à l'accueil.
- Validation : compilation et APK réussis dans CI 40784967448 ; le scénario combiné a dépassé son délai de 15 minutes. Tests séparés dans CI 40787021908 : compilation, ancien parcours API 29 et nouveau jeu sur émulateurs API 29 et 35 réussis ; ancien parcours API 35 a échoué en revenant à l'accueil juste après une interaction. Adaptation des marges aux barres système dans les deux activités ; cause exacte encore à confirmer. Nouveau run 40788364690 au commit a02eeb14 : compilation réussie, tests en cours. Tests Python compilés syntaxiquement, YAML validé localement.
- APK debug du nouveau run : artefact 12860132526 téléchargé ; archive et APK passent unzip -t ; SHA-256 06dba9a86eae38b3b99695746b61c95162f30c69452d17cb9d7e113564d1011c. Pas de test sur téléphone physique ni d'inspection visuelle humaine. Prochaine action : vérifier les quatre jobs du nouveau run, corriger tout échec puis finaliser ce journal avec heure et durée réelles.

- Complément de validation : CI 40788364690 terminée avec échec sur le parcours existant API 35, qui a affiché l'écran d'accueil Android après une interaction ; compilation, parcours API 29 et jeu de mémoire complet sur API 29 et 35 réussis. Les marges ne suffisent donc pas à garantir ce parcours. Le diagnostic et une nouvelle validation sont repris à la session suivante. Aucun téléphone physique ni contrôle visuel humain.


## Session du 29 octobre 2026, 00:37:30–01:07:53 Europe/Zurich (30 min 23 s)
- Base 6a5cf791 ; commits poussés 0e9bbaee, 721e77cb et c117fd75 ; un commit supplémentaire enregistre ce suivi. Fichiers : MemoryActivity.java, tests/memory_smoke.py, tests/emulator_smoke.py et PROJECT_STATE.md. Diff hors journal : 28 lignes ajoutées, 10 supprimées.
- Développement : pioche de quatre paires parmi huit motifs pour varier les parties, indication après une paire manquée, tests de remise à zéro et de persistance du record ; ajustement et journalisation des zones tactiles du parcours API 35 pour diagnostiquer le retour inattendu à l'accueil Android.
- Contrôles locaux : syntaxe Python et intégrité structurelle de la banque réussies. CI précédente 40788364690 : compilation, parcours API 29 et jeu de mémoire complet API 29/35 réussis ; parcours initial API 35 échoué, cause non établie. Nouveau run 40790159254 au commit c117fd75 : compilation réussie, tests sur émulateur en cours ; ne pas considérer les nouvelles modifications testées avant leurs résultats.
- APK debug : artefact 12860434758 téléchargé ; archive et APK passent unzip -t ; SHA-256 c6cd1d12b157209caef1449d66e35d9b18a79a1286939365d6cf5cab727ea59b. Aucun test sur téléphone physique ni inspection visuelle humaine. Prochaine action : inspecter les quatre jobs du nouveau run, corriger les échecs, puis consigner durée et résultats finaux.

- Validation finale à 01:07:53 : run 40790159254, commit c117fd75 : compilation, tests de données et de règles, parcours complet API 29 et nouveau parcours API 29 et 35 réussis ; parcours API 35 encore en cours, résultat final inconnu. L'échec antérieur API 35 reste à diagnostiquer si ce run échoue. Suivi provisoire poussé en 46ed5a71 ; ce complément est committé séparément.
- Limites : SDK, Gradle et javac absents localement ; tests Android exécutés en CI, aucun téléphone physique ni contrôle visuel humain. Modèle et niveau non modifiés. La durée comprend travail effectif et attente des validations, sans attente artificielle. Prochaine action : consulter le résultat du job API 35 123177177643, diagnostiquer tout échec et vérifier l'APK de la version retenue avant livraison.


## Session du 29 octobre 2026, 01:12:30–01:36:59 Europe/Zurich (24 min 29 s)
- Base 5abb9634 ; commits poussés d5ccb7bb, 06b71859, 67250500, 61d4a0a4. Fichiers : MemoryActivity.java, tests/memory_smoke.py et PROJECT_STATE.md ; diff hors journal : 63 ajouts, 7 suppressions.
- Développement : confirmation avant d'effacer ou d'abandonner une partie en cours, annulation sans perte de l'état, célébration d'un nouveau record conservée à la rotation. Tests automatisés ajoutés pour ces cas et la conservation du record.
- Contrôles locaux : compilation syntaxique du test Python et test de structure de la banque réussis. CI précédente 40790159254 terminée avec succès sur tous les jobs : compilation, tests de contenu et règles, et parcours API 29/35 des deux jeux. APK debug de ce run 12860434758, intégrité ZIP vérifiée, SHA-256 c6cd1d12b157209caef1449d66e35d9b18a79a1286939365d6cf5cab727ea59b.
- Nouveau run 40792642660 sur 61d4a0a4 en attente au moment de ce suivi ; les modifications de cette session ne sont pas encore compilées ni testées sur émulateur. Runs intermédiaires annulés automatiquement après commits suivants. Pas de téléphone physique ni inspection visuelle humaine, SDK/Gradle/javac locaux absents ; aucun réglage de modèle modifié. Prochaine action : vérifier les quatre jobs et l'APK de la nouvelle version et corriger tout échec ; consigner durée et résultats réels.

- Complément CI 01:15 Europe/Zurich : run 40792642660 sur 61d4a0a4, compilation et production d'APK réussies, parcours API 29 en cours ; autres parcours non démarrés. Artefact 12862865095 téléchargé ; archive et APK passent unzip -t ; SHA-256 APK ee5515b91312de6007038040cc4feba28b23af6d5c32cd40e9e8874c9556b6ef. La réussite des tests Android reste à établir.

- Complément CI : run 40792642660, compilation, tests de contenu et règles, parcours complet API 29 et tests du nouveau scénario API 29 et 35 réussis. Le parcours complet API 35 reste en cours ; aucun résultat final n'est encore déclaré.

- Résultat final à 01:36:59 : run 40792642660 intégralement réussi ; compilation, tests de données et de règles et parcours sur émulateurs API 29 et 35 réussis pour le commit 61d4a0a4. Les changements sont poussés ; commits de suivi 5a1ba73c, 92dca0b5 et ba1ce739, plus ce dernier complément. Aucun test sur téléphone physique ni inspection visuelle humaine. Durée comprenant le travail et l'attente effective de la CI ; aucune limite de quota mesurée et aucun changement de modèle ou d'effort. Prochaine action : poursuivre l'amélioration de l'expérience et confirmer à nouveau l'APK final avant livraison.


## Session du 29 octobre 2026, 02:01:09–02:40:05 Europe/Zurich (38 min 56 s)
- Base 77f6f4ca ; commits poussés 48bd606b, e598605b, 0af96f33, 66375e12, f0e64f19, b2bdbdfb, af655196 ; un commit supplémentaire enregistre ce suivi. Fichiers : MemoryActivity.java, tests/memory_smoke.py et PROJECT_STATE.md ; diff hors journal 107 ajouts, 25 suppressions.
- Développement : second niveau à six paires, record indépendant conservé, conservation du niveau et de la partie à la rotation, confirmation avant changement de niveau en cours de partie. Test automatisé d'une partie complète du nouveau niveau, rotation, séparation des records et retour au niveau initial ; défilement amélioré pour parcourir le grand plateau.
- Tests locaux : syntaxe du script Python et test de structure de banque réussis. Dernière validation complète connue : run 40792642660 sur 61d4a0a4, compilation et tous les parcours API 29 et 35 réussis, APK debug 12862865095 (SHA-256 ee5515b91312de6007038040cc4feba28b23af6d5c32cd40e9e8874c9556b6ef, archive et APK vérifiés). Nouveau run 40797998095 sur af655196 lancé, résultat encore inconnu ; les modifications de cette session ne sont pas déclarées compilées ou testées sur émulateur.
- Limites : SDK/Gradle/javac absents localement ; aucune inspection visuelle humaine ni test sur téléphone physique. Modèle et niveau inchangés. Prochaine action : inspecter compilation, APK et tous les jobs du run, corriger les éventuels échecs et consigner l'heure de fin et la durée réelles.

- Complément : 10 000 simulations Python de parties aléatoires et du parcours de résolution du grand plateau réussies (simulation locale distincte de l'application Android). Run 40797998095 : compilation réussie ; APK debug artefact 12863949157 téléchargé, archive et APK passent unzip -t, SHA-256 d674320121fbd6c5ce2aa536168492019a4752511d8be34e5cacc1a826704e6b. Parcours Android encore en cours ; ne pas déclarer leur réussite avant confirmation.

- Diagnostic CI : parcours mémoire API 35 du run 40797998095 échoué car les expressions régulières de repérage des cartes contenaient un antislash en trop ; compilation et parcours principal API 29 réussis. Correctif du test poussé en 3d2d5494 ; nouveau run 40799461717 lancé, compilation et tests à confirmer. Les autres jobs du run précédent ont pu être annulés par le nouveau commit.

- Correctif 3d2d5494 : syntaxe Python et test local du repérage sur une hiérarchie XML synthétique réussis. Run 40799461717 : compilation réussie ; APK artefact 12865058230 téléchargé, archive et APK passent unzip -t ; SHA-256 f737b029dae3adcd54c5e70ac1c6390a0d744a6c03b3f20acd8e95f1119fccb6. Les parcours Android de ce run restent à confirmer.

- Résultat final à 02:40:05 : CI 40799461717 entièrement réussie, compilation, tests de contenu et de règles et parcours complets des deux modes sur API 29 et 35 compris. APK artefact 12865058230, SHA-256 f737b029dae3adcd54c5e70ac1c6390a0d744a6c03b3f20acd8e95f1119fccb6, archive et APK vérifiés. Régression du test CI diagnostiquée puis corrigée en 3d2d5494, validation locale de son repérage puis nouvelle CI réussie. Les commits de suivi sont 56546f82, c24dab20, 6aa94782 et c4c8551e ; ce complément fait l'objet d'un dernier commit. Diff de code hors journal : 107 ajouts et 25 suppressions ; correction de l'expression régulière comprise dans ce total.
- Limites : aucun téléphone physique ni inspection visuelle humaine ; absence de SDK/Gradle/javac local. Aucun changement effectif du modèle ou de l'effort ; aucune limite précise de quota observée. Durée comprenant implémentation, diagnostic, correction et attente réelle des CI. Prochaine action : continuer la finition ludique et visuelle et vérifier de nouveau la version retenue pour la livraison.


## Session du 29 octobre 2026, 02:56:58–03:35:29 Europe/Zurich (38 min 31 s)
- Base 31d18203 ; commits poussés 480b73d6, 6280bb21, 872af625, 54cf4992 et ef348846 ; un commit supplémentaire consigne ce suivi. Fichiers : MemoryActivity.java, tests/memory_smoke.py, PROJECT_STATE.md ; diff hors journal 37 ajouts, 6 suppressions.
- Développement : plateau à trois colonnes plus compact, barre de progression avec description accessible, récompenses visuelles proportionnelles aux essais ; ajout de vérifications sur les deux niveaux, les récompenses et l'annulation d'un changement de niveau en cours. Une erreur d'expression régulière dans le nouveau test a été détectée et corrigée avant validation CI.
- Contrôles locaux : syntaxe Python, test de banque et essai du parseur de score réussis. Validation antérieure CI 40799461717 entièrement réussie sur API 29 et 35 ; APK 12865058230, SHA-256 f737b029dae3adcd54c5e70ac1c6390a0d744a6c03b3f20acd8e95f1119fccb6, archives vérifiées. Nouveau run 40803551852 sur ef348846 en attente ; compilation et parcours de cette session encore à confirmer.
- Limites : SDK/Gradle/javac absents localement ; aucun téléphone physique ni inspection visuelle humaine. Aucun changement de modèle ou de niveau. Prochaine action : consulter compilation, APK et tous les jobs CI, corriger les éventuelles régressions et consigner les résultats et la durée réels.

- Complément : CI 40803551852, compilation réussie et parcours API 29 en cours ; APK 12866908678 téléchargé, archive et APK passent unzip -t, SHA-256 4a7f4c45f03208d29cba4c7b61a8934c88b888f03a7e7af98ec97f5038dece8d. Tous les tests Android restent à confirmer pour ce commit.

- Diagnostic CI : run 40803551852, compilation et parcours principal API 29 réussis ; test mémoire API 35 échoué car l'écran attendu n'était pas ouvert après une interaction. Cause exacte non établie ; une nouvelle tentative bornée d'ouverture a été ajoutée au scénario en 398ed130, sans masquer un échec persistant. Syntaxe Python et test de banque réussis localement. Nouveau run 40804994601 lancé ; ses résultats restent à vérifier.

- CI 40804994601 : compilation du correctif réussie ; APK artefact 12867812287, archive et APK téléchargés et vérifiés avec unzip -t, SHA-256 30d33b22651591ad074be8450de1590ba6cc99d74b2b779c690cd81e3fcbc016. Les tests Android sont encore en cours ou en attente.

- Complément CI : run 40804994601 sur 398ed130 : compilation, parcours principal API 29 et scénarios mémoire API 29 et 35 réussis, y compris les nouveaux contrôles. Parcours principal API 35 toujours en cours.

- Résultat final à 03:35:29 : CI 40804994601 entièrement réussie sur 398ed130 ; compilation, tests de contenu et de règles, parcours complets API 29 et 35 réussis. APK debug 12867812287, SHA-256 30d33b22651591ad074be8450de1590ba6cc99d74b2b779c690cd81e3fcbc016 ; archive et APK vérifiés par unzip -t. L'échec intermittent d'ouverture du précédent test API 35 n'a pas eu de cause déterminée ; son scénario corrigé a réussi aux deux niveaux d'API.
- Diff final hors journal par rapport à 31d18203 : MemoryActivity.java 17 ajouts/6 suppressions, tests/memory_smoke.py 31 ajouts/6 suppressions, total 48 ajouts/12 suppressions. Commits de code supplémentaires : 398ed130 ; suivis e6cb60cb, f5c43ed3, ddd8fdfd, 09f3cd08 et ff17ae4c, plus ce complément. Aucune inspection visuelle humaine ni test sur téléphone physique. Pas de changement de modèle ou d'effort ; aucune limite de quota précise observée. Durée comprenant travail, diagnostic et attente réelle des deux CI. Prochaine action : poursuivre la finition visuelle, l'expérience ludique et vérifier l'APK final avant livraison.


## Session du 29 octobre 2026, 03:55:14–04:19:28 Europe/Zurich (24 min 14 s)
- Base c713d98c ; commits poussés 9093219d, 6f8ae468, e21bf220 et e7985c37 ; un commit supplémentaire consigne ce suivi. Fichiers : MemoryActivity.java, tests/memory_smoke.py et PROJECT_STATE.md ; diff hors journal 27 ajouts, 2 suppressions.
- Développement : 16 motifs au lieu de huit, couleurs distinctes pour les cartes révélées, contrastes renforcés ; une revanche sur le même niveau ne reprend pas exactement le même ensemble de motifs. Test automatisé de deux parties consécutives, y compris le maintien ou l'amélioration du record.
- Contrôles locaux : syntaxe Python et test de banque réussis ; rapports de contraste calculés pour toutes les couleurs sur les deux fonds >= 4,5 ; 20 000 tirages/revanches simulés séparément de l'application réussis. CI précédente 40804994601 entièrement réussie ; APK debug 12867812287 (SHA-256 30d33b22651591ad074be8450de1590ba6cc99d74b2b779c690cd81e3fcbc016, archive et APK vérifiés). Nouveau run 40809295370 sur e7985c37 en attente ; compilation et parcours de cette session encore inconnus.
- Limites : SDK/Gradle/javac absents localement ; aucun téléphone physique ni inspection visuelle humaine des motifs et couleurs. Aucun changement de modèle ou de niveau. Prochaine action : consulter compilation, APK et tous les jobs CI, corriger tout échec et consigner les résultats et la durée réels.

- CI 40809295370 : compilation réussie, parcours Android API 29 en cours. APK debug 12869741901 téléchargé ; archive et APK passent unzip -t ; SHA-256 5cdd360127a48b964526bfbda6342a6c9367143104243819c9fd8a7d432de498. Les tests Android restent à confirmer.

- Complément CI 40809295370 : compilation, parcours principal API 29 et tests de mémoire API 29 et 35 réussis, y compris le contrôle de variété des parties ; parcours principal API 35 encore en cours.

- Résultat final à 04:19:28 : run 40809295370 entièrement réussi sur e7985c37, y compris compilation, tests de données et règles, parcours API 29 et 35 et vérification de variété. APK debug 12869741901, SHA-256 5cdd360127a48b964526bfbda6342a6c9367143104243819c9fd8a7d432de498 ; archive et APK vérifiés par unzip -t. Commits de suivi 393ecfa9, 42e3325d et 6e6b9c26, plus ce complément.
- Limites : absence de SDK/Gradle/javac local, aucun téléphone physique ni inspection visuelle humaine, en particulier des nouveaux symboles. Modèle et effort non modifiés, limite de quota non mesurée. Durée comprenant implémentation, tests et attente réelle de la CI. Prochaine action : examiner la lisibilité visuelle et poursuivre la finition, puis revalider l'APK final pour livraison.


## Session du 29 octobre 2026, 04:55:58–05:20:48 Europe/Zurich (24 min 50 s)
- Base c4d19484 ; commits poussés 0ba9e434, b0e84506 et 7fc13666 ; un commit supplémentaire consigne ce suivi. Fichiers : MainActivity.java, tests/emulator_smoke.py, PROJECT_STATE.md ; diff hors journal 44 ajouts, 2 suppressions.
- Développement : records distincts pour les thèmes de jeu, consultables depuis l'accueil et au résultat ; conservation des records à la rotation et au redémarrage, absence de record pour un thème non terminé. Recentrage automatique de l'accueil et des résultats. Tests Android renforcés pour les records, la rotation et la persistance.
- Contrôles locaux : syntaxe du script Python et test de banque réussis. CI précédente 40809295370 entièrement réussie sur API 29 et 35 ; APK 12869741901, SHA-256 5cdd360127a48b964526bfbda6342a6c9367143104243819c9fd8a7d432de498, archives vérifiées. Nouveau run 40815008504 sur 7fc13666 en attente ; compilation et tests Android de cette session non encore confirmés.
- Limites : SDK/Gradle/javac absents localement ; aucun téléphone physique ni inspection visuelle humaine. Modèle et niveau non modifiés. Prochaine action : consulter compilation, APK et les quatre jobs CI, corriger les éventuels échecs et consigner durée et résultats réels.

- CI 40815008504 : compilation réussie, parcours API 29 en cours. APK debug 12872585010 téléchargé ; archive et APK passent unzip -t ; SHA-256 1d23298a67237192292437e06ad8004a9d32ecd4158dc49dcba603e01f29de92. Les tests Android restent à confirmer.

- CI 40815008504 : compilation, tests de données et de règles, parcours principal complet sur API 29 réussis, y compris records par thème et persistance ; parcours API 35 et tests mémoire API 29/35 en cours.

- Complément CI : tests mémoire complets réussis sur API 29 et 35 ; le parcours principal API 35 reste en cours.

- Résultat final à 05:20:48 : run 40815008504 entièrement réussi sur 7fc13666 : compilation, tests de données/règles, parcours API 29 et 35 et vérification de persistance des nouveaux records réussis. APK debug 12872585010, SHA-256 1d23298a67237192292437e06ad8004a9d32ecd4158dc49dcba603e01f29de92 ; archive et APK vérifiés par unzip -t. Commits de suivi 25261884, 97283526 et 21af4420 et 17ee2525, plus ce complément.
- Limites : pas de SDK/Gradle/javac local, aucun téléphone physique ni inspection visuelle humaine. Modèle et effort non modifiés, limite de quota non mesurée. Durée incluant implémentation, tests et attente réelle de la CI. Prochaine action : poursuivre la finition ludique et visuelle et revalider l'APK retenu pour la livraison.


## Session du 29 octobre 2026, 06:03:07–06:28:25 Europe/Zurich (25 min 18 s)
- Base 23419481 ; commits poussés cc74d323 et 031be47b ; un commit supplémentaire consigne ce suivi. Fichiers : MemoryActivity.java, tests/memory_smoke.py, PROJECT_STATE.md ; diff hors journal 59 ajouts, 8 suppressions.
- Développement : troisième niveau à huit paires avec record distinct, plateau 4 × 4 pour conserver une disposition compacte, conservation du niveau à la rotation. Test Android d'une partie complète au nouveau niveau, de la rotation et des records séparés pour les trois niveaux.
- Contrôles locaux : syntaxe Python et intégrité de la banque réussies. CI précédente 40815008504 entièrement réussie, compilation et parcours API 29/35 inclus ; APK 12872585010, SHA-256 1d23298a67237192292437e06ad8004a9d32ecd4158dc49dcba603e01f29de92, archive et APK vérifiés. Nouveau run 40821273009 sur 031be47b en attente ; compilation et tests Android de cette session à confirmer.
- Limites : SDK/Gradle/javac absents localement ; aucun téléphone physique ni inspection visuelle humaine. Modèle et niveau non modifiés. Prochaine action : vérifier compilation, APK et tous les jobs CI, corriger les échecs et noter la durée réelle.

- Complément : 30 000 plateaux aléatoires simulés séparément de l'application pour les trois niveaux (paires et variation) réussis. CI 40821273009 : compilation réussie, parcours Android API 29 en cours ; APK debug 12874372742 téléchargé, archive et APK passent unzip -t ; SHA-256 b8823de3b0943384d75aeabf7a530b498ac055c0797e577b528863f91c67d473. Tests Android encore à confirmer.

- Complément CI 40821273009 : compilation, parcours principal API 29 et parties de mémoire complètes sur API 29 et 35 réussis, y compris le nouveau niveau et ses records ; parcours principal API 35 encore en cours.

- Résultat final à 06:28:25 : run 40821273009 entièrement réussi sur 031be47b ; compilation, tests de contenu et de règles et parcours complets API 29/35 réussis, y compris le nouveau niveau et ses records. APK debug 12874372742, SHA-256 b8823de3b0943384d75aeabf7a530b498ac055c0797e577b528863f91c67d473 ; archive et APK vérifiés avec unzip -t. Commits de suivi 2cddc86b, c7657ddc et 47fd7c00, plus ce complément.
- Limites : aucun test sur téléphone physique ni inspection visuelle humaine, notamment des cibles tactiles sur très petits écrans ; SDK/Gradle/javac absents localement. Modèle et effort inchangés, limite de quota non mesurée. Durée comprenant implémentation, tests et attente réelle de la CI. Prochaine action : poursuivre la finition ludique et visuelle et vérifier l'APK final avant livraison.


## Session du 29 octobre 2026, 07:03:26–07:28:00 Europe/Zurich (24 min 34 s)
- Base 31e49f5f ; commits poussés dec90a03 et 9875d5f2 ; un commit supplémentaire consigne ce suivi. Fichiers : MemoryActivity.java, tests/memory_smoke.py, PROJECT_STATE.md ; diff hors journal 42 ajouts, 6 suppressions.
- Développement : plateau à deux, trois ou quatre colonnes selon largeur disponible, afin de conserver des cibles tactiles d'au moins 48 dp sur les largeurs de 160 à 800 dp ; repositionnement des cartes et conservation de l'état à la rotation. Test Android ajouté sur écran étroit 240 dp, mesures des cibles et rotation.
- Contrôles locaux : syntaxe Python et intégrité de banque réussies ; calcul géométrique sur 641 largeurs simulées réussi (modèle indépendant, pas une mesure Android). CI précédente 40821273009 entièrement réussie sur API 29/35 ; APK 12874372742, SHA-256 b8823de3b0943384d75aeabf7a530b498ac055c0797e577b528863f91c67d473, archive et APK vérifiés. Run 40827020425 sur 9875d5f2 lancé ; compilation et tests Android de cette session non encore confirmés.
- Limites : SDK/Gradle/javac absents localement ; aucun téléphone physique ni inspection visuelle humaine. Modèle et effort inchangés. Prochaine action : vérifier compilation, APK et jobs CI, corriger tout échec, noter durée et résultats réels.

- CI 40827020425 : compilation réussie, parcours API 29 en cours ; APK debug 12877132019 téléchargé, archive et APK passent unzip -t ; SHA-256 4af45366bf0ba4d708a95b03f218c3d3e370b7b6d9bf75de8c649545719e5cef. Les tests Android restent à confirmer.

- Complément CI : parcours principal API 29 et parcours mémoire API 29 et 35 réussis, y compris cibles tactiles mesurées et rotation sur émulateur 240 dp ; parcours principal API 35 en cours.

- Résultat final à 07:28:00 : CI 40827020425 entièrement réussie sur 9875d5f2, compilation, tests de contenu/règles et parcours API 29/35 inclus ; test d'écran étroit 240 dp, mesures de largeur et rotation réussis sur les deux versions. APK debug 12877132019, SHA-256 4af45366bf0ba4d708a95b03f218c3d3e370b7b6d9bf75de8c649545719e5cef ; archive et APK contrôlés par unzip -t. Commits de suivi d94e2640, a5dd4595 et 8f73e347, plus ce complément.
- Limites : pas de SDK/Gradle/javac local, aucun téléphone physique ni inspection visuelle humaine ; 160–239 dp couverts par modèle géométrique et non testés sur émulateur. Modèle et effort non modifiés, limite de quota non mesurée. Durée comprenant implémentation, tests et attente réelle de la CI. Prochaine action : poursuivre la finition ludique et visuelle et revalider l'APK final avant livraison.


## Session du 29 octobre 2026, 08:04:28–08:30:00 Europe/Zurich (25 min 32 s)
- Base 6f2dd71b ; commits poussés 8ad0b743, b85f8e00, 28ddd45a, e6a98310, 9b1a7972, 0cba16a7, c2154a39 et 3318efe7 ; un commit supplémentaire consigne ce suivi. Fichiers : SoundWave.java, MusicActivity.java, MainActivity.java, AndroidManifest.xml, tests/SoundWaveTest.java, tests/music_smoke.py, workflow et PROJECT_STATE.md. Diff hors journal : 277 ajouts, 1 suppression.
- Développement : troisième activité ludique hors ligne, jeu d'écoute et de reproduction de mélodies, sons synthétisés à enveloppe douce, difficulté progressive, record local, reprise à la rotation et retour à l'accueil. Tests unitaires Java du signal et scénario Android complet, erreur puis réussite, rotation et persistance, ajoutés aux jobs API 29 et 35.
- Contrôles locaux : syntaxe Python, intégrité de banque et lecture YAML réussies. CI précédente 40827020425 entièrement réussie sur API 29/35 ; APK 12877132019, SHA-256 4af45366bf0ba4d708a95b03f218c3d3e370b7b6d9bf75de8c649545719e5cef, archive et APK vérifiés. Run 40833355037 sur 3318efe7 en attente ; compilation, tests unitaires Java et Android de cette session à confirmer.
- Limites : SDK/Gradle/javac absents localement ; aucun téléphone physique, écoute réelle du son ni inspection visuelle humaine. Modèle et niveau non modifiés. Prochaine action : inspecter compilation, APK et six jobs CI, corriger tout échec et noter durée et résultats réels.

- CI 40833355037 : tests Java de synthèse des notes réussis, compilation réussie ; APK debug 12879811321 téléchargé, archive et APK passent unzip -t, SHA-256 0be423a99532d0ca2721bfdcb2bde6e54a3a30291eb03c94b95b4e8b9943be42. Parcours Android encore en cours ; qualité audible non vérifiée.

- Complément CI 40833355037 : compilation, tests du signal, parcours principal API 29 et nouvelle activité API 29 et 35 réussis, y compris erreur, réussite, rotation et persistance ; trois autres jobs Android en cours. L'activation de l'audio et sa qualité perçue n'ont pas été vérifiées par écoute.

- Résultat final à 08:30:00 : run 40833355037 entièrement réussi sur 3318efe7 : compilation, tests de données/règles, tests Java du signal et six jobs Android réussis, dont les parcours de la nouvelle activité sur API 29 et 35. APK debug 12879811321, SHA-256 0be423a99532d0ca2721bfdcb2bde6e54a3a30291eb03c94b95b4e8b9943be42 ; archive et APK contrôlés par unzip -t. Commits de suivi 187c088b, f5182223 et c66a2f52, plus ce complément.
- Limites : aucun appareil physique ni inspection visuelle humaine ; qualité et restitution audibles non vérifiées, les tests vérifient la synthèse numérique et l'interface sans écoute. SDK/Gradle/javac absents localement. Modèle et effort non modifiés ; limite de quota non mesurée. Durée comprenant implémentation, tests et attente réelle de la CI. Prochaine action : signaler clairement une indisponibilité audio éventuelle, puis poursuivre la finition et vérifier l'APK final avant livraison.


## Session du 29 octobre 2026, 09:01:17–09:55:32 Europe/Zurich (54 min 15 s)
- Base 31b10293 ; commits poussés 1748db21, 8b60d3dd, 5eafc14f, c7d7a3dd, bd7af296, 90adab21 et a01ca52f ; un commit supplémentaire consigne ce suivi. Fichiers : MusicActivity.java, tests/music_smoke.py, PROJECT_STATE.md ; diff hors journal 55 ajouts, 13 suppressions.
- Développement : mode musical libre et retour au défi, conservation du mode et de la progression à la rotation, indication d'un volume multimédia coupé ou d'une erreur de lecture audio. Test Android du mode libre, de son retour, de la persistance des records et du signalement de volume coupé.
- Contrôles locaux : syntaxe Python et intégrité de banque réussies. CI précédente 40833355037 entièrement réussie, compilation et six jobs API 29/35 ; APK 12879811321, SHA-256 0be423a99532d0ca2721bfdcb2bde6e54a3a30291eb03c94b95b4e8b9943be42, archive et APK vérifiés. Nouveau run 40839409874 sur a01ca52f en attente ; compilation et tests Android non encore confirmés.
- Limites : SDK/Gradle/javac absents localement ; aucun téléphone physique, écoute réelle ni inspection visuelle humaine. Les erreurs de restitution audio ne sont pas encore testées par injection ; le volume coupé est testé en CI. Modèle et effort non modifiés. Prochaine action : vérifier compilation, APK et six jobs CI, corriger tout échec, consigner durée et résultats réels.

- CI 40839409874 : tests Java de synthèse et compilation réussis ; parcours API 29 en cours. APK debug 12882286662 téléchargé ; archive et APK passent unzip -t ; SHA-256 911c5d2ebc2de30eba709634aa9f3caaf65865be63d1f8dfc5451a80832afd40. Tests Android non encore confirmés.

- Deuxième cycle : le volume coupé est aussi signalé lorsqu'une note est jouée en mode libre ; contrôle Android ajouté. Commits dbf6dc81 et d7583f81 ; diff final de code à ce stade 61 ajouts et 13 suppressions. Run 40839409874 annulé automatiquement par ces commits ; run 40840111062 sur d7583f81 lancé, résultats encore inconnus. Syntaxe Python et test de banque de nouveau réussis localement.

- CI 40840111062 : compilation et tests Java de synthèse réussis ; parcours principal API 29 en cours. APK debug 12881968881 téléchargé, archive et APK passent unzip -t ; SHA-256 e352b1a71ffdbe2a91bfe81a953798ad5bc465bcc08d8deb1ab6556a919085d3. Tests Android encore à confirmer.

- Diagnostic CI 40840111062 : compilation, tests de signal et parcours principal API 29 réussis ; tests musique API 29 et 35 échoués. Sur API 35 la commande shell `media` était absente ; sur API 29 un échec audio a remplacé l'indication de volume coupé. Correction : message préservé quand le volume est nul (44da9a8c), simulation du volume par touches Android (bd84fd27). Syntaxe Python et test de banque réussis localement ; run 40841889095 lancé, résultats inconnus. Les autres jobs du run précédent ont été annulés par la correction.

- CI 40841889095 : tests Java et compilation réussis, parcours API 29 en cours ; APK debug 12883376315 téléchargé, archive et APK passent unzip -t, SHA-256 90877cf57203347efe6175b8ca10c0e2036a7e16ccce2a7b7aadf897e7591491. Les tests Android restent à confirmer.

- CI 40841889095 : compilation et test musique API 35 réussis, y compris volume coupé et mode libre ; test musique API 29 échoué car le test refusait une commande basse malgré son affichage. Le journal affiche également un avertissement de restitution audio sur l'émulateur API 29, sans prouver le comportement sur téléphone. Ajustement borné de la zone tactile du test en 0350b2dd ; nouveau run 40843703802 lancé. Autres jobs précédents susceptibles d'annulation automatique.

- Nouveau correctif du scénario : 0391a291 ajoute les coordonnées au diagnostic et suit 0350b2dd, qui tolère une commande basse jusqu'à 600 pixels après défilement. Run 40843703802 annulé automatiquement par le nouveau commit ; run 40843750660 sur 0391a291 en cours. Syntaxe Python et intégrité de banque réussies. Diff final hors journal à ce stade : MusicActivity.java +40/-13, tests/music_smoke.py +30/-3, total +70/-16.

- CI 40843750660 : compilation et tests Java réussis ; parcours API 29 en cours. APK debug 12883792732 téléchargé ; archive et APK passent unzip -t ; SHA-256 d31091c30d29d35afc6adb883d5eda5fac715da89f72b82634a23f91336e9f16. Les tests Android de ce commit restent à confirmer.

- État à 09:55:32 : CI 40843750660 sur 0391a291 : compilation, tests de synthèse et parcours principal API 29 réussis ; tests de la nouvelle activité sur API 29 et 35 réussis, y compris volume coupé, mode libre, rotation et persistance. Parcours principal API 35 et parcours mémoire API 29/35 encore en cours ; succès global non confirmé. APK 12883792732, SHA-256 d31091c30d29d35afc6adb883d5eda5fac715da89f72b82634a23f91336e9f16, archive et APK vérifiés par unzip -t.
- Diff final de code hors journal : MusicActivity.java +40/-13, tests/music_smoke.py +30/-3, total +70/-16. Commits supplémentaires dbf6dc81, d7583f81, 44da9a8c, bd84fd27, 0350b2dd et 0391a291 ; suivis 32966779, de3bbd09, b49165cc, 81f3ef3c, 6fde80e0, 9ac4a355, 1eb086cb, 18e68db3 et 4e424ad6, plus ce complément.
- Limites : aucun téléphone physique, écoute réelle ni inspection visuelle humaine ; l'émulateur API 29 a signalé un problème de restitution audio, cause et comportement sur appareil non établis. SDK/Gradle/javac absents localement ; modèle et effort inchangés. Durée comprenant travail, diagnostic, correctifs et attente réelle des CI ; limite de quota non mesurée. Prochaine action prioritaire : inspecter les trois jobs restants de 40843750660, diagnostiquer le problème de restitution audio sur API 29 et compléter les contrôles avant livraison.


## Session du 29 octobre 2026, 09:58:18–10:39:26 Europe/Zurich (41 min 8 s)
- Base 3d6b9665 ; commits poussés 3b10b6d0 et 55905dab ; un commit supplémentaire consigne ce suivi. Fichiers : MusicActivity.java, tests/music_smoke.py, PROJECT_STATE.md ; diff hors journal 16 ajouts, 5 suppressions.
- Diagnostic et amélioration : allocation du tampon audio tenant compte du minimum Android ; journalisation de l'initialisation, des erreurs et du démarrage de lecture ; le test d'interface exige désormais la preuve que la lecture est entrée en état actif. Une lecture effectivement audible reste à vérifier sur téléphone.
- Contrôles locaux : syntaxe Python et intégrité de banque réussies. Run précédent 40843750660 : compilation, parcours principal API 29, mémoire API 29/35 et musique API 29/35 réussis ; parcours principal API 35 annulé automatiquement par le nouveau commit, donc CI globale non réussie. APK précédent 12883792732, SHA-256 d31091c30d29d35afc6adb883d5eda5fac715da89f72b82634a23f91336e9f16, archive et APK vérifiés. Run 40846196889 sur 55905dab lancé, résultats encore inconnus.
- Limites : SDK/Gradle/javac absents localement ; aucun téléphone physique ni inspection visuelle humaine ; avertissement audio précédent sur émulateur API 29 non encore expliqué. Modèle et effort non modifiés. Prochaine action : inspecter compilation, APK et tous les jobs CI ; lire les diagnostics audio, corriger les échecs et consigner résultats et durée réels.

- CI 40846196889 : compilation et tests Java de synthèse réussis ; parcours principal API 29 en cours. APK debug 12885366144 téléchargé, archive et APK passent unzip -t ; SHA-256 576683793c48ff56f238f9c9d02d55b38b183286c9691d87892b5170e1b4bfcf. Tests Android du nouveau diagnostic à confirmer.

- Diagnostic CI 40846196889 : sur API 29 et 35, le nouveau contrôle échoue et le journal indique que la piste audio n'atteint pas l'état attendu avant l'écriture ; aucune lecture active n'a été observée. Hypothèse : une piste statique sans données possède l'état NO_STATIC_DATA avant son premier remplissage. Correctif 260aa2eb : accepter cet état initial, vérifier INITIALIZED après écriture, puis confirmer PLAYING. Run 40848237189 lancé ; cette hypothèse et le correctif ne sont pas encore validés. Diff code courant +22/-5.

- CI 40848237189 : compilation et tests Java réussis ; parcours principal API 29 en cours. APK debug 12886425482 téléchargé ; archive et APK passent unzip -t ; SHA-256 4ff82f36f017ce11fffb94a2b617a276cd650ad14d96722cbcfe834553d1d388. Tests de lecture Android toujours en attente.

- Complément CI 40848237189 : les parcours musique API 29 et 35 réussissent ; leurs journaux confirment plusieurs passages en état PLAYING après écriture, contrairement à la version précédente. L'hypothèse sur l'état initial est confortée par le succès du correctif, sans écoute réelle de la sortie. Les trois autres jobs sont encore en cours.

- Résultat final à 10:39:26 : CI 40848237189 entièrement réussie sur 260aa2eb : compilation, tests de signal et de données/règles, six jobs Android API 29/35 ; journaux des deux parcours musique confirmant l'état PLAYING après écriture. Le correctif de l'état statique a résolu l'échec observé sur les deux émulateurs ; une écoute réelle et le comportement sur appareil physique ne sont pas vérifiés. APK debug 12886425482, SHA-256 4ff82f36f017ce11fffb94a2b617a276cd650ad14d96722cbcfe834553d1d388 ; archive et APK vérifiés par unzip -t.
- Diff final hors journal : MusicActivity.java +18/-5, tests/music_smoke.py +4/-0, total +22/-5 ; commit supplémentaire 260aa2eb ; suivis d3091196, 4fda639b, c5634bf1, 2698224d et 873ed112, plus ce complément. Aucun test sur téléphone physique ni inspection visuelle humaine ; SDK/Gradle/javac absents localement. Modèle et effort inchangés ; limite de quota non mesurée. Durée comprenant diagnostic, correctif et attente effective de deux CI. Prochaine action : poursuivre la finition ludique et visuelle et tester l'APK final avant livraison.


## Session du 29 octobre 2026, 10:57:58–11:22:09 Europe/Zurich (24 min 11 s, attente CI comprise)
- Base : 60f02dc0551b083f0451df6586f092dccd4d2588. Commit de code poussé : 0ac081e3dbcf0765b9140a3f89f87f627a4f60c5.
- Fichiers : MusicActivity.java (+29/-1), SoundWave.java (+14/-0), SoundWaveTest.java (+20/-0), music_smoke.py (+8/-0) ; total code +71/-1, hors journal.
- Ajout de trois accords synthétisés en mode libre, conservés après rotation, avec contrôle de volume et tests de composition spectrale et de parcours sur émulateur.
- Contrôles : syntaxe Python locale valide ; SDK, Gradle, javac et adb absents localement. CI 40853835903 intégralement réussie : compilation, tests unitaires et six jobs sur Android 10 et 15. Les journaux des deux tests ciblés confirment PLAYING ; aucune écoute humaine n'a vérifié la qualité du son. APK debug 12888038098 produit, archive et APK vérifiés par unzip -t ; SHA-256 APK 212a8f8bd048c59a594af4595bd4e689d6e12ea703f18aff5c175430be90d5b1. Dernière CI confirmée : 40848237189, six jobs réussis ; APK debug 12886425482, intégrité ZIP vérifiée. Aucun test sur téléphone ni écoute humaine de l'audio.
- Autres commits poussés : 320ccc54, 5d406483, 96038ec7 et le commit de ce bilan (journal seulement). Aucun échec de CI pendant cette session ; aucun appareil physique disponible. Réglage de modèle ou effort non modifié.
- Prochaine action : poursuivre la finition, vérifier les futurs runs et préparer la livraison avec APK et instructions d'installation.


## Session du 29 octobre 2026, début 11:58:36 Europe/Zurich (en cours)
- Base 8a7b1c23098c200c607ae532effee712a783d88f ; commit de code poussé 63e3fcc4de4034c6883827838f71343a604a08b9.
- Fichiers modifiés : MemoryActivity.java +26/-6, tests/memory_smoke.py +30/-0 ; total code +56/-6 hors journal.
- Jeu : indice facultatif utilisable une fois, coût de deux essais, sans clignotement ni temporisation ; conservation lors de la rotation, désactivation après usage, remise à zéro par nouvelle partie. Test du parcours, du coût et de la rotation ajouté.
- Contrôles locaux : syntaxe Python valide. SDK Android, Gradle, javac et adb absents ; CI 40861840953 en cours : compilation, tests unitaires et parcours principal Android 10 réussis ; quatre jobs réussis dont le parcours de l'indice sur Android 10 ; deux parcours Android 15 en cours. APK debug 12890909718 produit, archive et APK vérifiés par unzip -t, SHA-256 c47ee5dd9cc5f5b98d98d23a4c4da18551311988934782b8dd1445dd0fde36e1. CI précédente 40853835903 : six jobs réussis ; APK 12888038098 vérifié par unzip -t, SHA-256 212a8f8bd048c59a594af4595bd4e689d6e12ea703f18aff5c175430be90d5b1. Aucun test sur téléphone physique.
- Prochaine action : vérifier compilation, APK et six jobs ; corriger tout échec puis consigner durée et résultats.
