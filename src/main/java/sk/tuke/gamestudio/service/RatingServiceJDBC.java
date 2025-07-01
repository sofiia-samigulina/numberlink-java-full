package sk.tuke.gamestudio.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import sk.tuke.gamestudio.entity.Rating;

public class RatingServiceJDBC implements RatingService {
    public static final String SELECT = "select game, player, rating, ratedOn from rating where game = ? and player = ?";
    public static final String SELECT_AVERAGE = "select AVG(rating) from rating where game = ?";
    public static final String DELETE = "delete from rating";
    public static final String INSERT = "insert Into rating (game, player, rating, ratedOn) values (?, ?, ?, ?)";
    public static final String UPDATE = "update rating set rating = ?, ratedOn = ? where game = ? and player = ?";

    @Override
    public void setRating(Rating rating) throws RatingException {
        int existingRating = 0;

        try {
            existingRating = getRating(rating.getGame(), rating.getPlayer());
        } catch (Exception e) {
        }

        if (existingRating == 0) {
            // Insert
            try (Connection connection = DriverManager.getConnection(DbInfo.URL, DbInfo.USER, DbInfo.PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(INSERT);) {
                statement.setString(1, rating.getGame());
                statement.setString(2, rating.getPlayer());
                statement.setInt(3, rating.getRating());
                statement.setTimestamp(4, new Timestamp(rating.getRatedOn().getTime()));

                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RatingException("Problem inserting rating", e);
            }
        } else {
            // Update
            try (Connection connection = DriverManager.getConnection(DbInfo.URL, DbInfo.USER, DbInfo.PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(UPDATE);) {
                statement.setInt(1, rating.getRating());
                statement.setTimestamp(2, new Timestamp(rating.getRatedOn().getTime()));
                statement.setString(3, rating.getGame());
                statement.setString(4, rating.getPlayer());

                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RatingException("Problem updating rating", e);
            }
        }
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        try (Connection connection = DriverManager.getConnection(DbInfo.URL, DbInfo.USER, DbInfo.PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT_AVERAGE);) {
            statement.setString(1, game);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return (int) rs.getFloat(1);
                }
            }
        } catch (SQLException e) {
            throw new RatingException("Problem selecting average rating", e);
        }
        return 0;
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try (Connection connection = DriverManager.getConnection(DbInfo.URL, DbInfo.USER, DbInfo.PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT);) {
            statement.setString(1, game);
            statement.setString(2, player);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(3);
                }
            }

        } catch (SQLException e) {
            throw new RatingException("Problem selecting rating", e);
        }
        return 0;
    }

    @Override
    public void reset() throws RatingException {
        try (Connection connection = DriverManager.getConnection(DbInfo.URL, DbInfo.USER, DbInfo.PASSWORD);
             Statement statement = connection.createStatement();) {
            statement.executeUpdate(DELETE);
        } catch (SQLException e) {
            throw new ScoreException("Problem deleting rating", e);
        }

    }}
