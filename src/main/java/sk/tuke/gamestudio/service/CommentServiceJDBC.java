package sk.tuke.gamestudio.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import sk.tuke.gamestudio.entity.Comment;

public class CommentServiceJDBC implements CommentService {
    public static final String SELECT = "SELECT game, player, comment, commented_on FROM comment WHERE game = ?";
    public static final String DELETE = "DELETE FROM comment";
    public static final String INSERT = "INSERT INTO comment (game, player, comment, commented_on) VALUES (?, ?, ?, ?)";

    @Override
    public void addComment(Comment comment) throws CommentException {
        try (Connection connection = DriverManager.getConnection(DbInfo.URL, DbInfo.USER, DbInfo.PASSWORD);
             PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setString(1, comment.getGame());
            statement.setString(2, comment.getPlayer());
            statement.setString(3, comment.getComment());
            statement.setTimestamp(4, new Timestamp(comment.getCommentedOn().getTime()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new CommentException("Problem inserting comment", e);
        }
    }

    @Override
    public List<Comment> getComments(String game) throws CommentException {
        try (Connection connection = DriverManager.getConnection(DbInfo.URL, DbInfo.USER, DbInfo.PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT);) {
            statement.setString(1, game);
            try (ResultSet rs = statement.executeQuery()) {
                List<Comment> comments = new ArrayList<>();
                while (rs.next()) {
                    comments.add(new Comment(rs.getString(1), rs.getString(2), rs.getString(3), rs.getTimestamp(4)));
                }
                return comments;
            }
        } catch (SQLException e) {
            throw new CommentException("Problem selecting comments", e);
        }
    }

    @Override
    public void reset() throws CommentException {
        try (Connection connection = DriverManager.getConnection(DbInfo.URL, DbInfo.USER, DbInfo.PASSWORD);
             Statement statement = connection.createStatement();) {
            statement.executeUpdate(DELETE);
        } catch (SQLException e) {
            throw new CommentException("Problem deleting comments", e);
        }
    }

}
