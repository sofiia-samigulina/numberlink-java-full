package sk.tuke.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.service.CommentService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;


@Controller
@RequestMapping("/numberlink")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class CommentsController {

    @Autowired
    CommentService commentService;

    @RequestMapping("/comments")
    public String printAllComments() {
        return "comments";
    }

    public String getAllComments() {
        StringBuilder sb = new StringBuilder();
        List<Comment> comments = commentService.getComments("NumberLink");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);

        if (!comments.isEmpty()) {
            for (int i=comments.size()-1; i>=0; i--) {
                sb.append("<div class = 'comment-box'>\n");
                sb.append("<div class = 'playerDate'>\n");
                sb.append("<div class = 'comment-player'>\n");
                sb.append(comments.get(i).getPlayer());
                sb.append("</div>\n");
                sb.append("<div class = 'comment-date'>\n");
                Date date = comments.get(i).getCommentedOn();
                LocalDateTime time = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                sb.append("Posted on ");
                sb.append(time.format(formatter));
                sb.append("</div>\n");
                sb.append("</div>\n");
                sb.append("<div class='comment-text'>\n");
                sb.append(comments.get(i).getComment());
                sb.append("</div>\n");
                sb.append("</div>\n");
            }
        }

        else {
            sb.append("<p>We don't have any comments. Your comment will be first!</p>\n");
        }
        return sb.toString();
    }
}
