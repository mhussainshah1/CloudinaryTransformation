package com.example.demo.repositories;


import com.example.demo.models.Actor;
import com.example.demo.models.Movie;
import org.springframework.data.repository.CrudRepository;

public interface ActorRepository extends CrudRepository<Actor,Long> {
    Iterable <Actor> findAllByRealnameContainingIgnoreCase(String s);
    Iterable <Actor> findAllByMoviesNotContaining(Movie thisMovie);
}
