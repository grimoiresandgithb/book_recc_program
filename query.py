import requests

# -----------------------------
# Categorize subjects into buckets
# -----------------------------
def categorize_subjects(subjects):
    genres = []
    awards = []
    bestsellers = []
    other = []

    for s in subjects:
        lower = s.lower()

        # Awards
        if lower.startswith("award:") or "award" in lower or "hugo" in lower or "nebula" in lower:
            awards.append(s)
            continue

        # Bestseller / NYT tags
        if "new york times" in lower or lower.startswith("nyt:"):
            bestsellers.append(s)
            continue

        # Genre-like subjects
        if any(keyword in lower for keyword in [
            "fiction",
            "fantasy",
            "science fiction",
            "sci-fi",
            "adventure",
            "romance",
            "mystery",
            "thriller",
            "horror",
            "young adult",
            "ya",
            "dystopian",
            "historical",
            "biography",
            "memoir"
        ]):
            genres.append(s)
            continue

        # Everything else
        other.append(s)

    return {
        "genres": genres,
        "awards": awards,
        "bestsellers": bestsellers,
        "other": other
    }


# -----------------------------
# Fetch book data from Open Library
# -----------------------------
def fetch_book_data(isbn: str):
    # Step 1: Fetch edition data
    edition_url = f"https://openlibrary.org/isbn/{isbn}.json"
    edition_response = requests.get(edition_url)

    if edition_response.status_code != 200:
        return None

    edition = edition_response.json()

    # Extract basic edition info
    title = edition.get("title")
    pages = edition.get("number_of_pages")
    publish_date = edition.get("publish_date")
    author = edition.get("author")


    # Step 2: Get the work key
    works = edition.get("works", [])
    if not works:
        subjects = []
    else:
        work_key = works[0]["key"]  # e.g. "/works/OL12345W"

        # Step 3: Fetch work metadata
        work_url = f"https://openlibrary.org{work_key}.json"
        work_response = requests.get(work_url)

        if work_response.status_code == 200:
            work_data = work_response.json()
            raw_subjects = work_data.get("subjects", [])
        else:
            raw_subjects = []

        # Normalize subjects into a list of strings
        subjects = []
        if isinstance(raw_subjects, list):
            for item in raw_subjects:
                if isinstance(item, str):
                    subjects.append(item)
                elif isinstance(item, dict) and "name" in item:
                    subjects.append(item["name"])
        elif isinstance(raw_subjects, str):
            subjects = [raw_subjects]

    # Step 4: Categorize subjects
    categories = categorize_subjects(subjects)

    return {
        "title": title,
        "pages": pages,
        "author": author,
        "genres": categories["genres"],
        "awards": categories["awards"],
        "bestsellers": categories["bestsellers"],
        "other_subjects": categories["other"]
    }


# -----------------------------
# Test the function
# -----------------------------
if __name__ == "__main__":
    print(fetch_book_data("9780441013593"))  # Dune
