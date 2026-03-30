package service;

import model.Book;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class BookMetadataService {

	private final HttpClient client = HttpClient.newBuilder()
	        .followRedirects(HttpClient.Redirect.ALWAYS)
	        .build();


    public void enrichBook(Book book) throws Exception {
        String isbn = book.getIsbn();

        // Step 1: Fetch edition data
        JSONObject edition = fetchJson("https://openlibrary.org/isbn/" + isbn + ".json");
        if (edition == null) return;

        book.setTitle(edition.optString("title", null));
        book.setPages(edition.has("number_of_pages") ? edition.getInt("number_of_pages") : null);
        book.setPublishDate(edition.optString("publish_date", null));

        // Step 2: Get work key
        if (!edition.has("works")) return;
        JSONArray works = edition.getJSONArray("works");
        if (works.isEmpty()) return;

        String workKey = works.getJSONObject(0).getString("key");

        // Step 3: Fetch work metadata
        JSONObject work = fetchJson("https://openlibrary.org" + workKey + ".json");
        if (work == null) return;

        List<String> subjects = extractSubjects(work);

        // Step 4: Categorize
        List<String> genres = new ArrayList<>();
        List<String> awards = new ArrayList<>();
        List<String> bestsellers = new ArrayList<>();
        List<String> other = new ArrayList<>();

        for (String s : subjects) {
            String lower = s.toLowerCase();

            if (lower.startsWith("award:") || lower.contains("award") || lower.contains("hugo") || lower.contains("nebula")) {
                awards.add(s);
            } else if (lower.contains("new york times") || lower.startsWith("nyt:")) {
                bestsellers.add(s);
            } else if (lower.contains("fiction") || lower.contains("fantasy") || lower.contains("science fiction")) {
                genres.add(s);
            } else {
                other.add(s);
            }
        }

        book.setGenres(genres);
        book.setAwards(awards);
        book.setBestsellers(bestsellers);
        book.setOtherSubjects(other);
    }

    JSONObject fetchJson(String url) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "AlyssaBookApp (alyssa@example.com)")
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //System.out.println("DEBUG URL: " + url);
        //System.out.println("DEBUG Response Code: " + response.statusCode());
        //System.out.println("DEBUG Body: " + response.body());

        if (response.statusCode() != 200) return null;

        return new JSONObject(response.body());
    }


    private List<String> extractSubjects(JSONObject work) {
        List<String> subjects = new ArrayList<>();

        if (!work.has("subjects")) return subjects;

        JSONArray arr = work.getJSONArray("subjects");
        for (Object item : arr) {
            if (item instanceof String) {
                subjects.add((String) item);
            } else if (item instanceof JSONObject obj && obj.has("name")) {
                subjects.add(obj.getString("name"));
            }
        }

        return subjects;
    }
    
    public String fetchAuthorName(String authorKey) throws Exception {
        JSONObject authorJson = fetchJson("https://openlibrary.org" + authorKey + ".json");
        if (authorJson == null) return "Unknown Author";
        return authorJson.optString("name", "Unknown Author");
    }
    
    public String fetchDescription(JSONObject workJson) {
        if (!workJson.has("description")) return "No description available.";

        Object desc = workJson.get("description");

        if (desc instanceof JSONObject obj) {
            return obj.optString("value", "No description available.");
        }

        if (desc instanceof String s) {
            return s;
        }

        return "No description available.";
    }
    
    public JSONObject fetchRandomEdition() throws Exception {
        return fetchJson("https://openlibrary.org/random.json?type=/type/edition");
    }



}
