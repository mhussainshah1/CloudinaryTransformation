package com.example.demo.repositories;


import com.example.demo.models.Actor;
import com.example.demo.models.Movie;
import org.springframework.data.repository.CrudRepository;

public interface MovieRepository extends CrudRepository<Movie,Long> {
    Iterable <Movie> findAllByCastIsIn(Iterable<Actor> actors);
}
