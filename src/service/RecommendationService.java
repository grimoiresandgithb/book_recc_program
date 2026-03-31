package service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class RecommendationService {
	
	private final BookMetadataService metadata = new BookMetadataService();
	
	public void recommendRandomBook() throws Exception {
		
		JSONObject edition = metadata.fetchRandomEdition();
		if (edition == null) {
			System.out.print("Could not fetch a random book.");
			return;
		}
		
		String title = edition.optString("title", "Uknown Title");
		
		String authorName = "Unknown Author";
		if (edition.has("authors")) {
			JSONArray authors = edition.getJSONArray("authors");
			if(!authors.isEmpty()) {
				String authorKey = authors.getJSONObject(0).getString("key");
				authorName = metadata.fetchAuthorName(authorKey);
			}
		
		}
		
		String workKey = null;
		if (edition.has("works")) {
			workKey = edition.getJSONArray("works").getJSONObject(0).getString("key");
		}
		
		JSONObject workJson = (workKey != null)
                ? metadata.fetchJson("https://openlibrary.org" + workKey + ".json")
                : null;

        // 4. Extract genres + tags
        String genre = "Unknown";
        String tags = "None";

        if (workJson != null && workJson.has("subjects")) {
            JSONArray subjects = workJson.getJSONArray("subjects");

            if (!subjects.isEmpty()) {
                genre = subjects.getString(0);

                // pick up to 3 tags
                int limit = Math.min(3, subjects.length());
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < limit; i++) {
                    sb.append(subjects.getString(i));
                    if (i < limit - 1) sb.append(", ");
                }
                tags = sb.toString();
            }
        }

        // 5. Extract description
        String description = "No description available.";
        if (workJson != null) {
            description = metadata.fetchDescription(workJson);
        }
        
        printRecommendation(title, authorName, genre, tags, description);

        
	}
	
	private void printRecommendation(String title, String author, String genre, String tags, String description) {
	    System.out.println("\n========================================");
	    System.out.println("📚  Your Book Recommendation");
	    System.out.println("========================================");
	    System.out.println("Title:       " + title);
	    System.out.println("Author:      " + author);
	    System.out.println("Genre:       " + genre);
	    System.out.println("Tags:        " + tags);
	    System.out.println("----------------------------------------");
	    System.out.println("Summary:");
	    System.out.println(description);
	    System.out.println("========================================\n");
	}
	
	private String extractGenre(JSONObject workJson) {
	    if (workJson == null || !workJson.has("subjects")) return "Unknown";

	    JSONArray subjects = workJson.getJSONArray("subjects");

	    for (Object obj : subjects) {
	        String s = obj.toString().toLowerCase();
	        if (s.contains("fiction")) return s;
	        if (s.contains("fantasy")) return s;
	        if (s.contains("science fiction")) return s;
	        if (s.contains("romance")) return s;
	        if (s.contains("mystery")) return s;
	        if (s.contains("thriller")) return s;
	        if (s.contains("horror")) return s;
	    }

	    return "Unknown";
	}

	private String extractTags(JSONObject workJson) {
	    if (workJson == null || !workJson.has("subjects")) return "None";

	    JSONArray subjects = workJson.getJSONArray("subjects");

	    int limit = Math.min(3, subjects.length());
	    StringBuilder sb = new StringBuilder();

	    for (int i = 0; i < limit; i++) {
	        sb.append(subjects.getString(i));
	        if (i < limit - 1) sb.append(", ");
	    }

	    return sb.toString();
	}


	
	private boolean isFiction(JSONArray subjects) {
	    if (subjects == null) return false;

	    String[] fictionKeywords = {
	            "fiction",
	            "science fiction",
	            "fantasy",
	            "romance",
	            "mystery",
	            "thriller",
	            "horror",
	            "young adult",
	            "adventure"
	    };

	    for (Object obj : subjects) {
	        String s = obj.toString().toLowerCase();
	        for (String keyword : fictionKeywords) {
	            if (s.contains(keyword)) {
	                return true;
	            }
	        }
	    }
	    return false;
	}
	
	public void startInteractiveMode() throws Exception {
		Scanner scanner = new Scanner(System.in);
		
		while(true) {
			System.out.println("\nHow would you like your book recommendation today?");
			System.out.println("1. Keyword Search");
			System.out.println("2. Genre Filter");
			System.out.println("3. Mood filter");
			System.out.println("4. Choas Mode (totally random fiction)");
			System.out.println("5. Exit");
			
			String choice = scanner.nextLine();
			
			switch (choice) {
			case "1":
				keywordMode(scanner);
				break;
			case "2":
				genreMode(scanner);
				break;
			case "3":
				moodMode(scanner);
				break;
			case "4":
				chaosMode();
				break;
			case "5":
				System.out.println("Goodbye!");
				return;
			default:
				System.out.println("Invalid choice.");
			}
		}
	}
		
	
	private void chaosMode() throws Exception {
		recommendRandomBook();
	}
	private void moodMode(Scanner scanner) throws Exception {
		System.out.println("Choose a mood:");
		System.out.println("1. Cozy");
		System.out.println("2. Dark");
		System.out.println("3. Adventurous");
		System.out.println("4. Romantic");
		System.out.println("5. Thought-provoking");
		
		String mood = scanner.nextLine();
		
		String keyword = switch (mood) {
			case "1" -> "friendship";
			case "2" -> "dystopia";
			case "3" -> "adventure";
			case "4" -> "romance";
			case "5" -> "philosophy";
			default -> null;
		};
		
		if (keyword == null) {
			System.out.println("Invalid mood.");
			return;
		}
		
		JSONObject search = metadata.fetchJson("https://openlibrary.org/search.json?q=" + keyword);
		
		JSONArray docs = search.optJSONArray("docs");
		if(docs == null || docs.isEmpty()) {
			System.out.println("No books found");
			return;
		}
		
		JSONObject pick = docs.getJSONObject(new Random().nextInt(docs.length()));
	    recommendFromSearchDoc(pick);
		
	}
	
	private void keywordMode(Scanner scanner) throws Exception {
		System.out.print("Enter a keyword: ");
		String keyword = scanner.nextLine();
		
		JSONObject search = metadata.fetchJson(
		        "https://openlibrary.org/search.json?q=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8)
			    );
		
		JSONArray docs = search.optJSONArray("docs");
		if(docs == null || docs.isEmpty()) {
			System.out.println("No books found for that keyword");
		}
		
		JSONObject pick = docs.getJSONObject(new Random().nextInt(docs.length()));
	    recommendFromSearchDoc(pick);
	}
	
	private void recommendFromSearchDoc(JSONObject doc) throws Exception {
		String title = doc.optString("title", "Uknown Title");
		
		String author = "Unknown Author";
		if(doc.has("author_key")) {
			String authorKey = "/authors/" + doc.getJSONArray("author_key").getString(0);
			author = metadata.fetchAuthorName(authorKey);
		}
		
		String workKey = null;
		if (doc.has("key")) {
			workKey = doc.getString("key");
		}
		
		JSONObject workJson = (workKey != null)
				? metadata.fetchJson("https://openlibrary.org" + workKey + ".json")
				: null;
		
		String genre = extractGenre(workJson);
		String tags = extractTags(workJson);
		
		String description = metadata.fetchDescription(workJson);
		printRecommendation(title, author, genre, tags, description);
	}
	private void genreMode(Scanner scanner) throws Exception {
		System.out.println("Choose a genre:");
		System.out.println("1. Science Fiction");
		System.out.println("2. Fantasy");
		System.out.println("3. Romance");
		System.out.println("4. Mystery");
		System.out.println("5. Horror");
		
		String choice = scanner.nextLine();
		String genre = switch (choice) {
		case "1" -> "science_fiction";
		case "2" -> "fantasy";
		case "3" -> "romance";
		case "4" -> "mystery";
		case "5" -> "horror";
		default -> null;
		};
		
		if (genre == null) {
			System.out.println("Invalid genre.");
			return;
		}
		
		JSONObject subject = metadata.fetchJson("https://openlibrary.org/subjects/" + genre + ".json?limit=50");
		
		JSONArray works = subject.optJSONArray("works");
		if (works == null || works.isEmpty()) {
			System.out.println("No books found");
		}
	}

	
	} // end class
	


