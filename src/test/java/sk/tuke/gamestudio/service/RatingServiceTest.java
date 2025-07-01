package sk.tuke.gamestudio.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import sk.tuke.gamestudio.entity.Rating;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RatingServiceTest {
    private RatingService ratingService;

    @BeforeAll
    public void setUp(){
        ratingService = new RatingServiceJDBC();
        ratingService.reset();
    }

    @Test
    public void testAddRating() {
        var date = new Date();
        ratingService.setRating(new Rating("mines", "Jaro", 3, date));
        var rating = ratingService.getRating("mines", "Jaro");
        assertEquals(3,rating);
    }

    @Test
    public void testUpdateRating() {
        var date = new Date();
        ratingService.setRating(new Rating("mines", "Michaela", 2, date));
        ratingService.setRating(new Rating("mines", "Michaela", 1, date));
        var rating = ratingService.getRating("mines", "Michaela");
        assertEquals(1,rating);
    }

    @Test
    public void testGetAverageRating() {
        ratingService.reset();
        var date = new Date();
        ratingService.setRating(new Rating("mines", "Jaro", 3, date));
        ratingService.setRating(new Rating("mines", "Michaela", 2, date));
        ratingService.setRating(new Rating("mines", "Zuzka", 5, date));
        int average = ratingService.getAverageRating("mines");
        assertEquals(3, average);
    }

    @Test
    public void testDeleteRating() {
        var date = new Date();
        ratingService.setRating(new Rating("mines", "Jaro", 3, date));
        ratingService.reset();
        int ratingAfterDelete = ratingService.getRating("mines", "Jaro");
        assertEquals(0,ratingAfterDelete);
    }
}
