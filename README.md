Milestone 1 - Known (Unit 7)


Table of Contents

 



Overview


Description



Known is a personal cartography and life-mapping platform that transforms a user's everyday movement into a living map of their world.

Unlike Google Maps, Waze, and HERE Maps, which focus on navigation and destinations, Known focuses on a user's relationship with those places. Using GPS, motion sensors, compass orientation, and camera interactions, the app continuously learns which locations are significant, which routes define a user's daily life, which regions are familiar, and which areas remain unexplored.

Known automatically builds a personalized world map that evolves over time. The platform combines location analytics, personalized insights, territory completion, and progression systems to help users visualize how they move through and interact with the world around them.

Rather than answering:

"How do I get somewhere?"

Known answers:

"What places do I truly know?"



App Evaluation



Category: Personal Analytics / Mapping / Lifestyle / Gamification




Mobile: Uniquely mobile experience utilizing five distinct hardware and platform capabilities: continuous GPS Location tracking for route and territory generation, Maps for rendering familiarity heatmaps and progression layers, Accelerometer & Gyroscope Sensors for visit detection and movement classification, Compass (Magnetometer) data for directional travel analysis, and Camera integration for attaching memories and discoveries to significant locations.




Story: Traditional mapping platforms understand geography but not personal significance. Modern navigation apps can identify where a place exists but cannot determine which places define a user's routine, lifestyle, or experiences. Known creates a dynamic personal atlas that reveals familiar territories, significant locations, overlooked regions, and life patterns automatically generated from sensor data.




Market: College students, travelers, self-tracking enthusiasts, geography and map hobbyists, walkers, cyclists, runners, commuters, and users interested in understanding their movement patterns and personal geography.




Habit: Known continuously generates personalized content based on user activity. Discovery cards, ghost locations, exploration milestones, route progression, territory completion percentages, and daily insights create an infinite feedback loop where users regularly return to see what new patterns and discoveries their world map has uncovered.




Scope: The core technical scope centers on GPS route collection, significant-location clustering, familiarity scoring, territory generation, route recognition, movement analytics, and heatmap visualization. The MVP can be built around route tracking, personal map generation, discovery feeds, and progression systems while reserving advanced analytics and social features for future milestones.





Product Spec


1. User Features (Required and Optional)


Required Features



Personal World Map: Automatically generates a personalized geographic representation of the user's life, identifying familiar territories, significant locations, frequently traveled routes, and unexplored regions.

Discovery Feed: Infinite stream of automatically generated insights based on user behavior and movement patterns. Examples include new route discoveries, location milestones, ghost locations, and territory progression updates.

Cartographer Progression System: Users gain experience points, achievements, and completion percentages through territory expansion, route creation, location discovery, and atlas development.



Optional Features



Memory Markers: Users can attach photos, notes, tags, and memories to significant locations throughout their map.

RouteDNA Analytics: Automatically analyzes recurring routines, route habits, movement patterns, behavioral changes, and location significance rankings.





2. Screen Archetypes



Stream Screen (Discovery Feed)

Associated Required Feature: Discovery Feed — displays personalized insights, route discoveries, ghost locations, territory progression, daily exploration missions, achievements, and map-generated discoveries.


 


Detail Screen (Location Profile)

Associated Required Feature: Personal World Map — displays visit count, familiarity score, time spent, discovery history, route connections, attached memories, and location significance rankings.


 


Map Screen (Known World)

Associated Required Feature: Personal World Map — displays familiarity heatmaps, route overlays, territory boundaries, exploration progress, and completion percentages.


 


Creation Screen (Memory Marker)

Associated Optional Feature: Memory Markers — allows users to create location memories using photos, notes, descriptions, and custom tags attached to significant places.


 


Profile Screen (Cartographer Profile)

Associated Required Feature: Cartographer Progression System — displays exploration score, completion percentage, total distance mapped, route count, achievement collection, streak history, and cartographer level.


 
 



3. Navigation


Tab Navigation (Tab to Screen)



Tab 1 (Stream): Discovery Feed (Automatically generated location insights, ghost locations, territory milestones, achievements, and daily missions)

Tab 2 (Map): Known World (Personal atlas visualization featuring routes, territories, significant locations, and familiarity heatmaps)

Tab 3 (Profile): Cartographer Profile (Statistics, achievements, progression systems, exploration history, and completion metrics)





Flow Navigation (Screen to Screen)



Stream Screen (Discovery Feed)

→ Tap Discovery Card → Detail Screen (Location Profile)

→ Tap Ghost Location → Map Screen (Known World) centered on selected area

→ Tap "Add Memory" → Creation Screen (Memory Marker)


 


Detail Screen (Location Profile)

→ Tap Back Button → Stream Screen (Discovery Feed)

→ Tap Route Connection → Map Screen (Known World)

→ Tap "Create Memory" → Creation Screen (Memory Marker)


 


Creation Screen (Memory Marker)

→ Tap "Save" → Detail Screen (Location Profile) updated with newly attached memory

→ Tap "Cancel" → Returns to previous screen


 


Map Screen (Known World)

→ Tap Territory Region → Territory Details Overlay

→ Tap Route Overlay → Route Statistics Overlay

→ Tap Location Marker → Detail Screen (Location Profile)


 


Profile Screen (Cartographer Profile)

→ Tap Achievement → Achievement Details Screen

→ Tap Statistics → Analytics Dashboard

→ Tap Discovery History → Filtered Discovery Feed


Doomscroll / Habit Loop


Known's content is automatically generated from a user's daily movement. Every day creates new discoveries, territory progress, route milestones, ghost locations, achievements, and behavioral insights.

Examples:

"You passed this location 24 times but never stopped."

"Your world is now 47% mapped."

"You discovered a new route today."

"This location became one of your Top 10 places."

This creates an infinite stream of personalized discoveries where users continuously return to answer a single question:

"What did my map learn about me today?"



---

## Wireframes

### Hand-Sketched Low-Fidelity Wireframes

Not a very good drawing. It's not one of my skills. 

<img width="2019" height="2611" alt="Scan_20261103_222723" src="https://github.com/user-attachments/assets/4a1c36d5-6cff-443e-9178-eaabf71b50a6" />


```markdown

```

---
