package org.example.moviesapi.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoviesCrudRepository extends JpaRepository<MovieEntity, String> {
}
