package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import com.example.demo.cloudinary.CloudinaryConfig;
import com.example.demo.models.Actor;
import com.example.demo.models.Movie;
import com.example.demo.repositories.ActorRepository;
import com.example.demo.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    ActorRepository actorRepository;

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String showIndex(Model model) {
        model.addAttribute("gotmovies", movieRepository.count());
        model.addAttribute("gotactors", actorRepository.count());
        model.addAttribute("actorList", actorRepository.findAll());
        model.addAttribute("movieList", movieRepository.findAll());
        model.addAttribute("title", "Movie Database");
        return "index";
    }

    @GetMapping("/addmovie")
    public String addMovie(Model model) {
        model.addAttribute("movie", new Movie());
        return "addmovie";
    }

    @PostMapping("/addmovie")
    public String saveMovie(@ModelAttribute("movie") Movie movie) {
        movieRepository.save(movie);
        return "redirect:/";
    }

    @GetMapping("/addactor")
    public String addActor(Model model) {
        model.addAttribute("actor", new Actor());
        return "addactor";
    }

    @PostMapping("/addactor")
    public String saveActor(@ModelAttribute("actor") Actor actor,
                            MultipartHttpServletRequest request) {
        MultipartFile f = request.getFile("file");
        if (f.isEmpty()) {
            return "redirect:/addactor";
        }
        try {
            Map uploadResult = cloudc.upload(f.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
            String uploadURL = uploadResult.get("url").toString();
            String uploadedName = uploadResult.get("public_id").toString();
            String transformedImage = cloudc.createUrl(uploadedName);

            System.out.println("Uploaded Url:" + uploadURL);
            System.out.println("Uploaded File Name:" + uploadedName);
            System.out.println("Transformed Url:" + transformedImage);

            actor.setHeadshot(transformedImage);
            actorRepository.save(actor);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/addactor";
        }
        return "redirect:/";
    }

    @GetMapping("/addactorstomovie/{id}")
    public String addActor(@PathVariable("id") long movieID, Model model) {
        Movie thisMovie = movieRepository.findById(movieID).get();
        Iterable actorsInMovie = thisMovie.getCast();

        model.addAttribute("mov", thisMovie);
        model.addAttribute("actorList", actorRepository.findAllByMoviesNotContaining(thisMovie));
        return "movieaddactor";
    }


    @GetMapping("/addmoviestoactor/{id}")
    public String addMovie(@PathVariable("id") long actorID, Model model) {
        model.addAttribute("actor", actorRepository.findById(actorID).get());
        model.addAttribute("movieList", movieRepository.findAll());
        return "movieaddactor";
    }


    @PostMapping("/addmoviestoactor/{movid}")
    public String addMoviesToActor(@RequestParam("actors") String actorID, @PathVariable("movid") long movieID, @ModelAttribute("anActor") Actor a, Model model) {
        Movie m = movieRepository.findById(movieID).get();
        m.addActor(actorRepository.findById(new Long(actorID)).get());
        movieRepository.save(m);
        model.addAttribute("actorList", actorRepository.findAll());
        model.addAttribute("movieList", movieRepository.findAll());
        return "redirect:/";
    }

    @RequestMapping("/search")
    public String SearchResult() {
        //Get actors matching a string
        Iterable<Actor> actors = actorRepository.findAllByRealnameContainingIgnoreCase("Sandra");

        for (Actor a : actors) {
            System.out.println(a.getName());
        }

        //Show the movies the actors were in
        for (Movie m : movieRepository.findAllByCastIsIn(actors)) {
            System.out.println(m.getTitle());
        }
        return "redirect:/";
    }

    @RequestMapping("/about")
    public String getAbout() {
        return "about";
    }
}
