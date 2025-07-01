package sk.tuke.gamestudio.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import sk.tuke.gamestudio.entity.Comment;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommentServiceTest {
    private CommentService commentService;

    @BeforeAll
    public void setUp() {
        commentService = new CommentServiceJDBC();
        commentService.reset();
    }

    @Test
    public void testAddComment() {
        var date = new Date();
        commentService.reset();
        commentService.addComment(new Comment("mines", "Jaro", "cool game", date));
        var comments = commentService.getComments("mines");
        assertEquals(1, comments.size());
        assertEquals("mines", comments.get(0).getGame());
        assertEquals("cool game", comments.get(0).getComment());
        assertEquals("Jaro", comments.get(0).getPlayer());
        assertEquals(date, comments.get(0).getCommentedOn());
    }

    @Test
    public void testGetComments() {
        var date = new Date();
        commentService.reset();
        commentService.addComment(new Comment("mines", "Jaro", "cool game", date));
        commentService.addComment(new Comment("mines", "Samuel", "difficult", date));
        commentService.addComment(new Comment("tiles", "Zuzka", "super", date));
        commentService.addComment(new Comment("mines", "Jaro", "wauuu", date));

        var comments = commentService.getComments("mines");

        assertEquals(3, comments.size());

        assertEquals("mines", comments.get(0).getGame());
        assertEquals("Jaro", comments.get(0).getPlayer());
        assertEquals("cool game", comments.get(0).getComment());
        assertEquals(date, comments.get(0).getCommentedOn());

        assertEquals("mines", comments.get(1).getGame());
        assertEquals("Samuel", comments.get(1).getPlayer());
        assertEquals("difficult", comments.get(1).getComment());
        assertEquals(date, comments.get(1).getCommentedOn());

        assertEquals("mines", comments.get(2).getGame());
        assertEquals("Jaro", comments.get(2).getPlayer());
        assertEquals("wauuu", comments.get(2).getComment());
        assertEquals(date, comments.get(2).getCommentedOn());
    }

    @Test
    public void testReset() {
        var date = new Date();
        commentService.addComment(new Comment("mines", "Jaro", "cool game", date));
        commentService.reset();
        assertEquals(0, commentService.getComments("mines").size());
    }

}
