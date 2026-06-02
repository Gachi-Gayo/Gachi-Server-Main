package com.gachi.server.domain.review.repository;

import com.gachi.server.domain.itinerary.entity.Block;
import com.gachi.server.domain.place.entity.Place;
import com.gachi.server.domain.review.entity.Review;
import com.gachi.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByPlace(Place place);

    List<Review> findByUser(User user);

    Optional<Review> findByUserAndBlock(User user, Block block);
}
