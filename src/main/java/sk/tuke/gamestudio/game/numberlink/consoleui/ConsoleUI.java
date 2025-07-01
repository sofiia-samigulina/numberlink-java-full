package sk.tuke.gamestudio.game.numberlink.consoleui;


import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.numberlink.core.Field;
import sk.tuke.gamestudio.game.numberlink.core.FieldMap;
import sk.tuke.gamestudio.game.numberlink.core.Tile;
import sk.tuke.gamestudio.service.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.logging.log4j.internal.map.UnmodifiableArrayBackedMap.getMap;

public class ConsoleUI {
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private final Pattern tileCommandPatter = Pattern.compile("\\D*(?<row>[0-9]+) (?<col>[0-9]+) (?<num>[0-9]+).*");

    @Autowired
    private CommentService commentService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private ScoreService scoreService;

    private int steps;
    private long startTime;
    private long endTime;
    private String player;
    private Field field;
    private int startScore;

    public ConsoleUI() {

    }

    public void menuLoop(){
        String welcomeText = "Welcome to the game NumberLink. You should match all similar numbers. Ways between numbers can't cross each other. The game field can't contain empty tiles at the end. If you want exit, write 'x'.";
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println(printLongText(welcomeText,85));
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a("If you are ready, please enter your name: ").reset());

        //ak chce hned vyjst
        String name = readLine();
        if (name.equals("x")) {
            exit();
        }

        this.setName(name);
        do {
            play();
            System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a("Do you want to continue or exit? Press any key to continue, 'x' - exit").reset());
            String input = readLine();
            if (input.equals("x")) {
                endingGame();
                exit();
            }
        } while (true);
    }

    private String printLongText(String message, int maxWidth) {
        StringBuilder sb = new StringBuilder();
        int i=0;
        while(i<message.length()){
            int end = Math.min(maxWidth+i, message.length());
            if (end<message.length() && message.charAt(end)!=' ') {
                int lastSpace = message.lastIndexOf(' ', end);
                if (lastSpace > i) {
                    end=lastSpace;
                }
            }
            sb.append(message, i, end).append(System.lineSeparator());
            i = end+1;
        }
        return sb.toString();
    }

    private void play() {
        this.field = new Field("console");
        field.loadMap(field.getMap());

        startTime = System.currentTimeMillis();
        startScore = ((field.getRowCount()* field.getColumnCount())-(field.getMap().getNumberCount()*2))*20;
        System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a("Starting score: ").reset().a(startScore));
        printTime(startTime);
        printGame();

        do {
            handleInput();
            System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a("Starting score: ").reset().a(startScore));
            printTime(startTime);
            printGame();
        } while (!field.isGameWon());

        endTime = System.currentTimeMillis();

        System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("CONGRATULATIONS! GAME WON!").reset());
        printScore();
        scoreService.addScore(new Score("NumberLink", this.getPlayer(), field.countScore(field, startTime, endTime, steps, startScore), new Date()));
        printTopScores();
    }

    private void exit() {
        System.exit(0);
    }

    private void printComments() {
        var commentList = commentService.getComments("NumberLink");
        int i=0;
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a("Comments: ").reset());
        for (var comment : commentList) {
            i++;
            System.out.printf("\n %s, %s, %s\n %s\n", comment.getGame(), comment.getPlayer(), comment.getCommentedOn(), comment.getComment());
        }
        System.out.println("-------------------------------------------------------------------------------------");
    }

    private void printRating() {
        System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a("Rating of game: ").reset().a(ratingService.getAverageRating("NumberLink")));
    }

    private String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            printErrorMessage("Invalid input");
        }
        return "";
    }

    private void endingGame() {
        System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a("Please write your comment about this game: ").reset());
        String commentLine = readLine();
        Comment comment = new Comment("NumberLink", this.getPlayer(), commentLine, new Date());
        commentService.addComment(comment);
        boolean validInput = false;
        int numberRating=0;
        do {
            System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a("Please rating this game, 1 - the best, 5 - the  worst").reset());
            String ratingLine = readLine();
            try {
                numberRating = Integer.parseInt(ratingLine);
                if (numberRating <=5 && numberRating >=1){
                    validInput = true;
                }
                else {
                    System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("Please write the number between 1 and 5").reset());
                }
            } catch (NumberFormatException e) {
                System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("You have incorrect input, try again (without spaces and any symbols)").reset());
            }
        }while (!validInput);
        Rating rating = new Rating("NumberLink", this.getPlayer(), numberRating, new Date());
        ratingService.setRating(rating);
        printRating();
        printComments();
    }

    private void handleInput() {
        System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a("Please enter tile (row col num): ").reset());
        String input = readLine();

        if (input.equals("x")) {
            exit();
        }

        Matcher tileCommandMatcher = tileCommandPatter.matcher(input);
        if (tileCommandMatcher.matches()) {
            int row = Integer.parseInt(tileCommandMatcher.group("row")) - 1;
            int col = Integer.parseInt(tileCommandMatcher.group("col")) - 1;
            int number = Integer.parseInt(tileCommandMatcher.group("num"));
            field.setTile(field, row, col, number);

            if (field.setTile(field, row, col, number)) {
                steps++;
                return;
            }
            steps++;
        }
        System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("You have incorrect input, try again").reset());
    }

    private void printGame() {
        System.out.println(field.toString());
    }

    private void printScore() {
        System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a("Your actual score: ").reset().a(field.countScore(field, startTime, endTime, steps, startScore)));
    }

    private void printTime(long startTime) {
        System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a("Time(sec): ").reset().a((System.currentTimeMillis()-startTime)/1000));
    }

    private void printErrorMessage(String message) {
        System.err.println(message);
    }

    private void printTopScores() {
        var scoreList = scoreService.getTopScores("NumberLink");
        int i=0;
        System.out.println("-------------------------------------------------------------------------------------");
        for (var score : scoreList) {
            i++;
            System.out.printf("\n%d. %s, %d\n", i, score.getPlayer(), score.getPoints());
        }
        System.out.println("-------------------------------------------------------------------------------------");
    }

    private void setName(String name) {
        this.player = name;
    }

    private String getPlayer() {
        return player;
    }

}
