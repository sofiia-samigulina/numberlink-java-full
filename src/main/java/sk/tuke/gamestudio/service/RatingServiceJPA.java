package sk.tuke.gamestudio.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import sk.tuke.gamestudio.entity.Rating;

@Transactional
public class RatingServiceJPA implements RatingService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) throws RatingException {
        Rating existingRating = entityManager.createQuery("SELECT r FROM Rating r WHERE r.game = :game AND r.player = :player", Rating.class)
                .setParameter("game", rating.getGame())
                .setParameter("player", rating.getPlayer())
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
        if (existingRating == null) {
            entityManager.persist(rating);
        }
        else {
            existingRating.setRating(rating.getRating());
            existingRating.setRatedOn(rating.getRatedOn());
            entityManager.merge(existingRating);
        }
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        try {
            Object object = entityManager.createNamedQuery("Rating.getAverage").setParameter("game", game).getSingleResult();
            if (object == null) {
                return 0;
            }
            return ((Number)object).intValue();
        }
        catch (NoResultException e) {
            return 0;
        }
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try {
            Object object = entityManager.createNamedQuery("Rating.getRating").setParameter("game", game).setParameter("player", player).getSingleResult();
            if (object == null) {
                return 0;
            }
            return ((Rating)object).getRating();
        }
        catch (NoResultException e) {
            return 0;
        }
    }

    @Override
    public void reset() throws RatingException {
        entityManager.createNamedQuery("Rating.resetRating").executeUpdate();
    }
}
