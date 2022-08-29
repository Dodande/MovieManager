package com.company;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MovieBase {

    private static MovieBase movieBase = null;

    private List<Movie> movieList;
    private List<Person> people;

    public static MovieBase getInstance(List<Movie> movieList, List<Person> people) {
        if (movieBase == null) {
            movieBase = new MovieBase(movieList, people);
        }
        return movieBase;
    }

    public List<String> listMovieBase(Map<String, Pattern> searchPattern) {
        List<Movie> filteredMovies = movieList
                .stream()
                .filter(movie -> checkPattern(movie, searchPattern))
                .collect(Collectors.toList());
        handleSorting(filteredMovies, searchPattern);

        return handleExpandedSearch(filteredMovies, searchPattern);
    }

    public boolean addPerson(Person person) {
        boolean added = false;
        if (!people.contains(person)) {
            people.add(person);
            added = true;
        }
        return added;
    }

    public Movie createAndaddMovie(String title, Person director, List<Person> actors, String length) {
        Movie movie = new Movie(title, director, actors, length);
        movieList.add(movie);
        return movie;
    }

    public boolean isPersonExist(String findName) {
        return people.stream().map(Person::getName).anyMatch(name -> name.equals(findName));
    }

    public boolean isMovieExist(String title, String directorName) {
        Person director = findPersonByName(directorName);
        Movie movie = new Movie(title, director);
        return movieList.contains(movie);
    }

    public Person findPersonByName(String name) {
        return people.stream().filter(person -> person.getName().equals(name)).findFirst().orElse(null);
    }

    private List<String> handleExpandedSearch(List<Movie> filteredMovies, Map<String, Pattern> searchPattern) {
        boolean expanded = searchPattern.containsKey("v");
        return filteredMovies.stream().map(movie -> movie.toString(expanded)).collect(Collectors.toList());
    }

    private void handleSorting(List<Movie> filteredMovies, Map<String, Pattern> searchPattern) {
        boolean ascending = searchPattern.containsKey("la");
        boolean descending = searchPattern.containsKey("ld");
        if (ascending) {
            filteredMovies.sort(Comparator.comparing(Movie::getLength));
        } else if (descending) {
            filteredMovies.sort(Comparator.comparing(Movie::getLength).reversed());
        } else {
            filteredMovies.sort(Comparator.comparing(Movie::getTitle));
        }
    }

    private boolean checkPattern(Movie movie, Map<String, Pattern> patternMap) {
        return patternMap.entrySet().stream().allMatch(pattern -> isValidMovieByPattern(movie, pattern));
    }

    private boolean isValidMovieByPattern(Movie movie, Map.Entry<String, Pattern> searchPattern) {
        boolean valid = true;
        switch (searchPattern.getKey()) {
            case "t":
                valid = searchPattern.getValue().matcher(movie.getTitle()).matches();
                break;
            case "d":
                valid = searchPattern.getValue().matcher(movie.getDirector().getName()).matches();
                break;
            case "a":
                valid = movie.getActors()
                        .stream()
                        .map(Person::getName)
                        .anyMatch(name -> searchPattern.getValue().matcher(name).matches());
                break;
        }
        return valid;
    }


    private MovieBase(List<Movie> movieList, List<Person> people) {
        this.movieList = movieList;
        this.people = people;
    }


}
