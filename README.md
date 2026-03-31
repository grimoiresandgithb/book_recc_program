# 📚 Random Book Recommendation Engine
*A Java console app powered by the Open Library API*

## 🌟 Overview
This project is a Java‑based **book recommendation engine** that fetches real‑time data from the Open Library API. It generates fun, dynamic book recommendations based on user‑selected filters such as:
- **Keyword search **
- **Genre filter**
- **Mood Filter**
- **Chaos Mode** (random fiction only - unpredictable and delightful)

Each recommendation is enriched with metadata pulled from Open Library, including:
- Title
- Author
- Genre
- Subject tags
- Summary/description

The app currently runs in the console, but is designed to be easily integrated into a **web backend** for a book‑review or reading‑tracking site.

---

## ✨ Features
### 🔍 Keyword Search
Enter any word (“dragons”, “space”, “romance”, “witches”) and get a random book whose metadata matches your keyword.

### 🎭 Genre Filter  
Choose from popular genres like:

- Science Fiction
- Fantasy
- Romance
- Mystery
- Horror

The app fetches a random book from that subject category.

### 💫 Mood Filter
Pick a vibe, not a genre. Options include:

- Cozy
- Dark
- Adventurous
- Romantic
- Thought-provoking

Each mood maps to thematic keywords in Open Library's subject system.

### 🎲 Chaos Mode
A totally random **fiction only** recommendation.
No textbooks. No nursing manuals. Just vibes.

### 📖 Enriched Metadata
Every recommendation includes:

- **Title**
- **Author name** (via author API)
- **Genre** (derived from subjects)
- **Tags** (top 3 subjects)
- **Summary** (from the work description)

---

## 🛠️ Tech Stack

- **Java 17**
- **Java HttpClient** for API calls
- **org.json** for JSON parsing
- **Open Library API**
  - '/random.json'
  - '/search.json'
  - '/subjects/{genre}.json'
  - '/works/{id}.json'
  - '/authors/{id}.json'
 
---

## 📦 Project Structure
src/
├── model/
│    └── Book.java
│
├── service/
│    ├── BookMetadataService.java
│    ├── RecommendationService.java
│    └── (helpers for filtering, parsing, etc.)
│
└── Main.java

---

## 🚀 How It Works
1. The user selects a filter mode from the interactive menu
2. The app queries the appropriate Open Library endpoint
3. A random matching book is selected
4. The app fetches additional metadata:
     - Edition -> Work -> Author
5. The app prints a formatted recommendation card to the console

---

## 🧪 Example Output
========================================
📚  Your Book Recommendation
========================================
Title:       Le roman du masque de fer
Author:      Alexandre Dumas
Genre:       fiction
Tags:        History, Fiction, Classic Literature
----------------------------------------
Summary:
You are about to hear," said Aramis, "an account which few could now give; for it refers to a secret which they buried with their dead...." So begins the magnificent concluding story of the swashbuckling Musketeers--Aramis, Athos, Porthos, and D'Artagnan. Aramis--plotting against the King of France--bribes his way into the jail cells of the Bastille where a certain prisoner has been entombed for eight long years. The prisoner knows neither his real name nor the crime he has committed. But Aramis knows the secret of the prisoner's identity ... a secret so dangerous that its revelation could topple the King from his throne! Aramis ... plotting against the King? The motto of the Musketeers has been "All for one, and one for all." Has Aramis betrayed his friends? Is this the end of the Musketeers?
========================================

---

## 🔮 Future Plans

- Add cover image URLs
- Add "vibe" classification
- Add award-winner filters
- Add reading-level filters
- Build a REST API wrapper
- Integrate into a personal book-review website
- Add a UI widget ("Surprise Me!" button)
- Recommendation based on a book selection/reading list



