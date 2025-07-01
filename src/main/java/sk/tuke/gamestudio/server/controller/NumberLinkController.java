package sk.tuke.gamestudio.server.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.numberlink.core.Field;
import sk.tuke.gamestudio.game.numberlink.core.GameState;
import sk.tuke.gamestudio.service.CommentService;
import sk.tuke.gamestudio.service.RatingService;
import sk.tuke.gamestudio.service.ScoreService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/numberlink")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class NumberLinkController {
    private Field field = new Field("web");
    private int number=-1;

    //dorobit score
    private int steps;
    private long startTime;
    private long endTime;
    private int startScore;
    private String username;
    private boolean startGame = false;
    private boolean addedScore = false;

    @Autowired
    ScoreService scoreService;
    @Autowired
    CommentService commentService;
    @Autowired
    RatingService ratingService;

    public NumberLinkController() {
        field.loadMap(field.getMap());
        startScore = ((field.getRowCount()* field.getColumnCount())-(field.getMap().getNumberCount()*2))*20;
    }

    @RequestMapping
    public String numberLink(@RequestParam(required = false) Integer row, @RequestParam(required = false) Integer col, Model model,HttpSession session) {
        username = (String) session.getAttribute("loggedUser");
        if (username != null) {
            model.addAttribute("username", username);
        }
        if ((row!=null && col!=null) && field.getTile(row,col).isReadonly()) {
            if (!startGame) {
                startTime = System.currentTimeMillis();
                endTime = System.currentTimeMillis();
                steps=0;
                field.setState(GameState.PLAYING);
                startGame = true;
            }
            number = field.getTile(row,col).getNumber();
        }
        if (number!=-1 && (row!=null && col!=null) && !(field.getTile(row,col).isReadonly()) && !(field.isGameWon()) && (field.getState() == GameState.PLAYING))  {
            field.setTile(field, row,col,number);
            steps++;
            endTime = System.currentTimeMillis();
        }
        if (field.isGameWon() && !addedScore) {
            if (username != null) {
                Score score = new Score("NumberLink", username, field.countScore(field, startTime, endTime, steps, startScore), new Date());
                scoreService.addScore(score);
                addedScore=true;
            }
            field.setState(GameState.OFF);
            startGame = false;
        }
        return "numberlink";
    }

    @RequestMapping("/new")
    public String newNumberLink() {
        field.loadMap(field.getMap());
        startScore = ((field.getRowCount()* field.getColumnCount())-(field.getMap().getNumberCount()*2))*20;
        startTime = 0;
        endTime = 0;
        addedScore = false;
        return "numberlink";
    }

    @RequestMapping("/mycomment")
    public String mycomment(@RequestParam String comment, Model model, HttpSession session) {
        username = (String) session.getAttribute("loggedUser");
        if (username != null) {
            model.addAttribute("username", username);
        }
        if (username != null) {
            Comment myComment = new Comment("NumberLink", username, comment, new Date());
            commentService.addComment(myComment);
        }
        return "redirect:/numberlink";
    }

    @RequestMapping("/setrating")
    public String setRating(@RequestParam String rating, HttpSession session, Model model) {
        username = (String) session.getAttribute("loggedUser");
        if (username != null) {
            model.addAttribute("username", username);
            int ratingInt = Integer.parseInt(rating);
            Rating rating1 = new Rating("NumberLink", username, ratingInt, new Date());
            ratingService.setRating(rating1);
        }
        return "redirect:/numberlink";
    }

    public String getHtmlField() {
        StringBuilder sb = new StringBuilder();

        sb.append("<table>\n");

        for (int row = 0; row < field.getRowCount(); row++) {
            sb.append("<tr>\n");

            for (int col = 0; col < field.getColumnCount(); col++) {
                var Tile = field.getTile(row, col);
                if (field.getTile(row, col).getNumber()!=0) {
                    if (field.getTile(row, col).isReadonly()) {
                        sb.append("<td class='readOnlyNumber-" + field.getTile(row, col).getNumber() + "'>\n");
                    }
                    else {
                        sb.append("<td class='correctNumber-" + field.getTile(row, col).getNumber() + "'>\n");
                    }
                }
                else {
                    sb.append("<td class='notMarked'>\n");
                }
                if (!field.isGameWon()) {
                    sb.append("<a href='/numberlink?row=" + row + "&col=" + col + "'>\n");
                }
                sb.append(field.getTile(row, col));
                sb.append("</a>\n");
                sb.append("</td>\n");
            }
            sb.append("</tr>\n");
        }
        sb.append("</table>\n");
        return sb.toString();
    }

    public String getTopScores() {
        StringBuilder sb = new StringBuilder();
        List<Score> topScores = scoreService.getTopScores("NumberLink");

        if (!topScores.isEmpty()) {
            sb.append("<table>\n");
            sb.append("<tr>\n");
            sb.append("<th>N</th>\n");
            sb.append("<th>Username</th>\n");
            sb.append("<th>Points</th>\n");
            for (int i=0; i<topScores.size(); i++) {
                if (i%2!=0) {
                    sb.append("<tr class='unpaired'>\n");
                }
                else {
                    sb.append("<tr class='paired'>\n");
                }
                sb.append("<td>\n");
                sb.append(i+1);
                sb.append("</td>\n");
                sb.append("<td>\n");
                sb.append(topScores.get(i).getPlayer());
                sb.append("</td>\n");
                sb.append("<td>");
                sb.append(topScores.get(i).getPoints());
                sb.append("</td>\n");
                sb.append("</tr>\n");
            }
            sb.append("</table>\n");
        }
        else {
            sb.append("<p>We don't have any players. You will be first!</p>\n");
        }

        return sb.toString();
    }

    public String getThreeComments() {
        StringBuilder sb = new StringBuilder();
        List<Comment> comments = commentService.getComments("NumberLink");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);

        if (!comments.isEmpty()) {
            int length = comments.size();
            int myIndex = Math.min(length,3);
            for (int i=comments.size()-1; i>(comments.size()-1)-myIndex; i--) {
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

        return sb.toString();
    }

    public String getTime() {
        StringBuilder sb = new StringBuilder();
        int currentTime = (int)((endTime-startTime)/1000);
        sb.append("<div>Time: " + currentTime + "</div>\n");
        return sb.toString();
    }

    public String getScore() {
        StringBuilder sb = new StringBuilder();
        if (field.isGameWon()) {
            int actualScore = field.countScore(field, startTime, endTime, steps, startScore);
            sb.append("<div> Your actual score: " + actualScore + "</div>\n");
        }
        else {
            sb.append("<div> Start score: " + startScore + "</div>\n");
        }
        return sb.toString();
    }

    public String getAverage() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div>\n");
        sb.append("Average rating: ");
        sb.append(ratingService.getAverageRating("NumberLink"));
        sb.append("</div>\n");
        return sb.toString();
    }

    public String getUserRating() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div>\n");
        sb.append("Your rating: ");
        if (ratingService.getRating("NumberLink", username) !=0) {
            sb.append(ratingService.getRating("NumberLink", username));
        }
        sb.append("</div>\n");
        return sb.toString();
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/numberlink";
    }
}
