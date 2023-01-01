# Id

Identification, authentication and authorization service.

---

### Description

This service is responsible for:
- registration of players and moderators
- granting rights to various areas of interaction with the infrastructure
- providing user information for other services

The key feature of the service is interaction via `AccessToken`.
Any internal service can request moderator or player data for one token,
and it will be guaranteed that the token is correct and up-to-date.
