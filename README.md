#### Projet Cloud Emilien Barbaud Julie Queiros

##### Lien vers le portail d'API

https://endpointsportal.tinyinsta-257116.cloud.goog/

##### Lien appspot

https://tinyinsta-257116.appspot.com
Si vous avez l'accès sinon :
https://apis-explorer.appspot.com/apis-explorer/?base=https%3A%2F%2Ftinyinsta-257116.appspot.com%2F_ah%2Fapi&root=https%3A%2F%2Ftinyinsta-257116.appspot.com%2F_ah%2Fapi#p/

#### Kinds

| Post                  | User                      | ShardedLike | Like            |   |
 |-----------------------|---------------------------|-------------|-----------------|---|
 | @Id Long id           | @Id Long id               | String name | String name     |   |
 | String desc           | @Index String userAt      | Long count  | Long shardCount |   |
 | @Index String UserA   | @Index String userName    |             | shards          |   |
 | String lieu           | HashSet<String> Followers |             |                 |   |
 | Long user             | HashSet<String> Following |             |                 |   |
 | Long Likes            |                           |             |                 |   |
 | HashSet<Long> likedBy |                           |             |                 |   |
 |                       |                           |             |                 |   |
 
 #### Benchmark
  Les fonctions de benchmark sont dans le tinyinstaendpoint
Moyenne sur 30 mesures :

Ajout d'un poste pour un utilisateur avec 10, 100, 500 followers.
On ne prend en compte que l'action d'ajouter un post à l'utilisateur. 
test sur endpoint (si vous souhaitez 

  |10|100| 500|
  |---|---|---|
 |0.89 s |0.91 s| 0.90 s|
 
 Ce temps s'explique par le fait qu'on n'ajoute pas littéralement le poste au fil d'actualité des followers mais plutôt ils iront eux même chercher l'information en charger leur fil.
 
Afficher 10, 100, 500 postes.
On ne prend en compte que l'action d'afficher les postes.
test1 sur endpoint (si vous souhaitez tester)

 |10|100| 500|
 |---|---|---|
 |0.15 s |0.23 s| 0.22 s|
 
 Cela semble un peu court (en tout cas pour 500), cependant nous prenons en compte seulement l'action d'afficher les posts 
 
 Au vue des résultats il semble que ces deux actions passent à l'échelle.
 
 Impossibilité de tester le like.
  
 #### Sharded Counter
 Identitique à celui de Minh-Hoang Dang, il nous a beaucoup aidé sur cette partie
 
 #### Pour vous connectez au site
 
 Simplement ajouter vous un nom d'utilisateur et un @utilisateur
 Vous pouvez vous abonner à : acousseau, ebarbaud, jqueiros, mhd (les utilisateurs de la base)