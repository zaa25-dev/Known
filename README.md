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
Story: People get stuck commuting on autopilot, ignoring 90% of their own city. Known transforms everyday movement into a point-based exploration game—highlighting nearby unvisited roads and walkways 

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

<img width="600" height="1200" alt="Scan_20261103_222723" src="https://github.com/user-attachments/assets/4a1c36d5-6cff-443e-9178-eaabf71b50a6" />


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

User Stories: 

Screen 1: World Map View

[x] Interactive Base Map Canvas with pan, pinch-to-zoom, and tilt gestures

[ ] Uber H3 Hexagonal Territory Mesh

[ ] Dim / translucent hexes for areas visited 1–2 times

[ ] Solid colored hexes for high-frequency familiar territory

[ ] Dotted / outlined hexes ("Ghost Locations") for unvisited bordering cells

[ ] Pulsating blue location puck with directional heading cone

[ ] Color-coded polyline route trails categorized by movement type (e.g., walking, driving)

[ ] Floating action button to re-center camera on live GPS coordinates

[ ] Floating action button to toggle hexagonal mesh visibility on/off

Screen 2: Discovery Feed

[ ] Top metric banner displaying Cartographer Level and weekly XP counter

[ ] Scrollable vertical feed with insight cards

[ ] Ghost Location Card (mini-map preview, distance, XP reward tag)

[ ] Routine Divergence Card (divergence stats and habit callouts)

[ ] Territory Expansion Card (celebratory banner for newly unlocked hexes)

[ ] Card action to highlight and navigate to the selected coordinate on the Map tab

Screen 3: Cartographer Profile

[ ] Header Level Card with Cartographer Rank badge and visual XP progress bar

[ ] 2x2 Territory Stats Dashboard

[ ] Territory Known percentage

[ ] Hexes Claimed counter

[ ] Miles Logged counter

[ ] Ghost Spots Conquered counter

[ ] Saved Places & Bookmarks list with custom labels, visit counts, notes, and edit/delete controls

Persistent System Shell
[x] Fixed 3-tab Bottom Navigation Bar (Map, Feed, Profile)

[ ] Android status bar persistent foreground notification for background tracking status



---
