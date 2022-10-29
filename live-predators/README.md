# Predators

A card game in which you can choose your **predator**
and try to survive and not stay hungry!

---

### Description

I want to build a system that will consist of:
* game backend - calculates logic and processes events from players and modules
* frontend of the game - minimal mechanics to participate in the game
* moderator module - the ability to distribute cards and monitor the process
* snapshot service - recording and storing game states for recovery and analysis
* chips control service - accounting of used chips
* configuration service - dynamic configuration of the gameplay
* kubernetes cluster - aggregates and runs all services

Goals:
- [ ] Use features introduced in Scala 3
- [ ] Configure the interaction of actors
- [ ] Build an event-driven application architecture
- [ ] Use new libraries
