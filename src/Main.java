import service.RecommendationService;

public class Main {
    public static void main(String[] args) throws Exception {
        RecommendationService rec = new RecommendationService();
        rec.recommendRandomBook();
    }
}
