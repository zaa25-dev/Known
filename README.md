Milestone 1 - Known (Unit 7)


Table of Contents

 



Overview
---------

Description



Known is a personal cartography and life-mapping platform that gamifies your daily commute. 

Rather than focusing on getting you from point A to B, Known encourages exploration by rewarding you with a point system for the distances you drive. Earn even more points by taking different roads outside of your normal commute and visit certain "high value" areas to earn your way to the top of the leaderboard. Gain personal insights into how you drive, and maybe explore a part of town you've never been before. 


App Evaluation

Category: Personal Analytics / Mapping / Lifestyle / Gamification

Mobile: Uniquely mobile experience utilizing continuous GPS Location tracking for route logging and distance scoring, Maps for rendering explored road networks and high-value territory zones, Accelerometer & Gyroscope sensors for driving detection and trip classification, and Compass (Magnetometer) data for directional travel analysis.

Story: Most drivers commute on autopilot, taking the exact same roads every day while overlooking the rest of their city. Known turns everyday driving into an exploration game—rewarding drivers for detouring off their usual routes, uncovering unvisited areas, and competing for high-value territory on a local leaderboard.

Market: Daily commuters, road-trip enthusiasts, rideshare/delivery drivers, newcomers to a city, and car owners who enjoy gamified tracking and exploring new routes.

Habit: Every walk/drive generates distance points, route-divergence bonuses, driving insights, and leaderboard movement—pulling users back regularly to maintain their leaderboard position. Taking the same route will reward you less, but diverging rewards more. 

Scope:

MVP: Live GPS route tracking, Uber H3 hexagonal territory grid, distance and route-novelty point scoring, driving insights feed, and personal profile stats.

V2: Cloud sync (Firebase) for real-time leaderboards, competitive high-value Pioneer zones, and advanced driving analytics.





### Product Spec

#### 1. User Features (Required and Optional)

**Required Features**

* **Personal Road & Territory Map:** Automatically logs driven routes and overlays an Uber H3 hexagonal grid, visually separating daily commute corridors from newly explored roads and unvisited areas.
* **Discovery & Leaderboard Feed:** Dynamic stream of driving insights, route-divergence callouts, nearby unvisited "Ghost Locations," high-value zone alerts, and local leaderboard rankings.
* **Commute Gamification & Point System:** Awards points for distance driven, bonus multipliers for taking new roads outside regular commute routines, and competitive payouts for reaching high-value zones.

**Optional Features**

* **Saved Places & Notes:** Users can bookmark notable stops with custom labels and personal notes.
* **Driving & Route Analytics:** Analyzes recurring commute patterns, route novelty ratios, trip mileage, and top destinations.

---

#### 2. Screen Archetypes

* **Map Screen (Known World)**
* *Associated Feature:* Personal Road & Territory Map — displays driven polyline trails, Uber H3 familiarity hexes (translucent vs. solid), dotted Ghost Locations, gold high-value target zones, and a live GPS puck with directional heading.


* **Stream Screen (Discovery Feed & Leaderboard)**
* *Associated Feature:* Discovery & Leaderboard Feed — displays current driver rank, weekly points, local leaderboard standings, high-value zone alerts, and personalized driving insight cards.


* **Profile Screen (Driver Profile)**
* *Associated Feature:* Commute Gamification & Point System — displays rank badge, XP progress bar, total miles driven, hexes unlocked, high-value zones claimed, and saved places.


* **Detail Modal Sheet (Location / Hex Profile)**
* *Associated Feature:* Personal Road & Territory Map — slides up when tapping a hex or pin to show resolved street/neighborhood names, visit counts, dwell time, point values, and editable notes.



---

#### 3. Navigation

**Tab Navigation (Tab to Screen)**

* **Tab 1 (Map):** Known World (Interactive road trails, H3 territory mesh, and high-value zones)
* **Tab 2 (Feed):** Discovery Feed & Leaderboard (Driving insights, route-divergence prompts, and local rankings)
* **Tab 3 (Profile):** Driver Profile (Mileage metrics, territory coverage, rank progression, and saved places)

**Flow Navigation (Screen to Screen)**

* **Map Screen (Known World)**
* → Tap Hex Tile or Pin → Detail Modal Sheet (Location / Hex Profile)
* → Tap Recenter FAB → Snaps camera to live GPS coordinates
* → Tap Layer Toggle FAB → Toggles H3 hexagonal mesh visibility


* **Stream Screen (Discovery Feed & Leaderboard)**
* → Tap Insight or High-Value Card → Switches to Map Screen centered on the target coordinate
* → Toggle Leaderboard View → Displays local driver rankings and weekly point totals


* **Profile Screen (Driver Profile)**
* → Tap Saved Place → Switches to Map Screen centered on the bookmarked location
* → Tap Edit/Delete on Bookmark → Updates or removes saved location notes



---

#### 4. Habit Loop

Known generates personalized updates directly from a user's daily driving. Every trip creates distance points, route-novelty bonuses, territory expansion, and leaderboard movement.

**Examples:**

* *"You took a new detour off Route 3 today: +150 Bonus Points."*
* *"You passed this exit 24 times without exploring it—250 XP High-Value Zone nearby."*
* *"You climbed to #4 on the local leaderboard this week."*
* *"Your local road network is now 32% mapped."*

This creates an ongoing feedback loop where drivers check back after trips to see what they gained. 



---



---

## Wireframes

### Hand-Sketched Low-Fidelity Wireframes

Not a very good drawing. It's not one of my skills. 

<img width="400" height="800" alt="Scan_20261103_222723" src="https://github.com/user-attachments/assets/4a1c36d5-6cff-443e-9178-eaabf71b50a6" />


```markdown

```

Milestone 2 - Build Sprint 1 (Unit 8)

GitHub Project board

<img width="1913" height="965" alt="image" src="https://github.com/user-attachments/assets/84de1734-9180-46b5-a5b5-f9fc31cdf724" />

Issue cards

<img width="401" height="631" alt="image" src="https://github.com/user-attachments/assets/93b22813-62ba-42cd-9ecc-f44bff1137a4" />

<img width="392" height="456" alt="image" src="https://github.com/user-attachments/assets/ddd4ceb8-51cf-4040-bebb-0068330d1927" />

Issues worked on this sprint

<img width="1494" height="1241" alt="image" src="https://github.com/user-attachments/assets/f1c9170a-43d2-4f64-84d7-497b32cbcf1c" />


List the issues you completed this sprint
- Added Google Map that will be used.
- Added the three Fragments: Map, Feed, Profile
- More development will happen by the next Milestone

[Add giphy that shows current build progress for Milestone 2. Note: We will be looking for progression of work between Milestone 2 and 3. Make sure your giphys are not duplicated and clearly show the change from Sprint 1 to 2.]

<img width="400" height="800" alt="Milestone2" src="https://github.com/user-attachments/assets/10337409-5ecc-40bb-bcab-79d2fc307d6a" />

---

### User Stories

#### Screen 1: World Map View

* [x] Interactive dark-mode Base Map Canvas with pan, pinch-to-zoom, and tilt gestures
* [x] Uniform Square Territory Grid (~150m spatial cells via `GridHelper`) that remains invisible until explored
* [x] Borderless, 4-tier progressive color tiles (`Polygon` overlays) that deepen in shade based on repeat visit frequency (1 visit, 2–3 visits, 4–6 visits, 7+ visits)
* [x] Gold / highlighted tiles for unclaimed daily "High-Value" 3x bonus zones
* [x] Live blue GPS location puck with automatic initial camera lock
* [x] Tap-to-inspect tile dialog displaying Zone ID, familiarity tier, total visit count, last travel mode (`WALK`/`DRIVE`), and first explored timestamp
* [x] Floating action button to re-center camera on live GPS coordinates

#### Screen 2: Discovery Feed & Leaderboard

* [ ] Horizontal carousel of Nearby High-Value Zones showing XP bonus payouts, distance, and a "Show on Map" action button
* [x] Local Explorers Leaderboard card ranking users by total XP, level, and rank standing
* [ ] Privacy-safe Rival Stats dialog on leaderboard row tap (displays aggregated distance, zones unlocked, and bonus claims if public, while keeping GPS coordinates strictly private)
* [ ] Scrollable vertical activity stream displaying recent territory discoveries, ghost zone alerts, and pedestrian streak insights
* [ ] Wire "Show on Map" card action to center the Map tab on the selected zone's coordinates
* [x] Connect Leaderboard and Activity Feed cards to live Cloud Firestore queries

#### Screen 3: Explorer Profile

* [x] Header Level Card with Explorer Rank badge, level title, and animated Material 3 XP progress bar
* [x] 2x2 Territory Stats Dashboard (Territory Known %, Zones Unlocked, Miles Logged, Bonus Zones Claimed)
* [x] Account Privacy & Preferences card (Public Leaderboard Stats toggle and Preferred Mode selector)
* [x] Connect Profile counters and privacy toggle to authenticated Firebase user document (`users/{uid}`)

#### Persistent System Shell, Cloud Sync & Scoring Engine

* [x] Fixed 3-tab Bottom Navigation Bar (Map, Feed, Profile)
* [x] Point scoring & multiplier logic (`100 XP` new discovery, `25 XP` repeat visit with 1-hour anti-farming cooldown
* [x] Firebase Authentication & Cloud Firestore integration for cross-device account persistence (`users/{uid}` and `visited_cells`)
* [ ] Android status bar persistent foreground notification for continuous background exploration tracking

Milestone 3 - Build Sprint 2 (Unit 9)

GitHub Project board:

<img width="1908" height="962" alt="image" src="https://github.com/user-attachments/assets/6635aa6b-5dba-46e3-a5f1-e83dee24a12a" />



Completed user stories
List the completed user stories from this unit

- This unit was about making sure the basic functions of the app work.
- Firebase was created to hold user accounts and data for their use and the leaderboard.
- Sign in With Google used for accounts. Logout button also added.
- Map tiles generated as the user moves.
- Golden "bonus" tiles appear randomly on the map to be claimed for more XP.
- Leaderboard created. Currently have myself and a fake user inside to demonstrate that it works.
- Tapping on tiles shows data such as amount of times visited and changes color based on this amount. 

List any pending user stories / any user stories you decided to cut from the original requirements

- I originally wanted to separate walking and driving to give those separate scores but felt it would not make sense.
- Tiles need to show the amount of points that specific tile has awarded as you can still gain points from tiles you already visited.
- Need to add a custom username function as not everyone wants to use their Google account name.
- Need to add when you tap another user on the leaderboard, you can view their stats compared to yours. Privacy first, of course. 

[Add video/gif of your current application that shows build progress] 

Login and Feed Pages

<img width="400" height="800" alt="Login and Feed" src="https://github.com/user-attachments/assets/d07eef44-774d-4f82-a0ad-50681a1a5034" />

Exploring high value zones on the map and navigating to them. 

https://imgur.com/a/explore-map-navigate-tChgbSW

Had to put this one on imgur because I could not get the file size down low enough. 

Profile page and Logging Out

<img width="400" height="800" alt="Profile and Logout" src="https://github.com/user-attachments/assets/5a0647be-9a7b-4bc2-bb88-9ce47f81f8a9" />




App Demo Video

Embed the YouTube/Vimeo link of your Completed Demo Day prep video

---



---
