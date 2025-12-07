package com.geohunt.backend.comments;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Evan Julson
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

}
