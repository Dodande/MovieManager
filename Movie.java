package com.company;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class Movie {

    private static final int HOUR_TO_SECONDS = 3600;

    private static final int MINUTE_TO_SECONDS = 60;

    private String title;

    private Person director;

    private List<Person> actors;

    private int length;


    private Movie(String title, Person director, List<Person> actors){
        this(title,director);
        this.actors = actors;
    }

    public Movie(String title, Person director, List<Person> actors, int length) {
        this(title,director,actors);
        this.length = length;
    }
    public Movie(String title, Person director, List<Person> actors, String length) {
        this(title,director,actors);
        LocalTime localTime = LocalTime.parse(length);
        this.length = (localTime.getHour()*HOUR_TO_SECONDS)+(localTime.getMinute()*MINUTE_TO_SECONDS)+localTime.getSecond();
    }

    public Movie(String title,Person director){
        this.title=title;
        this.director=director;
    }

    private String getFormatLength(){
        return LocalTime.ofSecondOfDay(this.length).toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Person getDirector() {
        return director;
    }

    public void setDirector(Person director) {
        this.director = director;
    }

    public List<Person> getActors() {
        return actors;
    }

    public void setActors(List<Person> actors) {
        this.actors = actors;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
    public  String toString(boolean expanded){
        String info = "";
        if (expanded){
            StringBuilder actorString = new StringBuilder();
            this.actors.stream().map(Objects::toString).forEach(name -> actorString.append(name).append("\n"));
            info="\nStarring:\n"+actorString;
        }
        return this.title+" by "+this.director.getName()+","+ getFormatLength()+info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(title, movie.title) &&
                Objects.equals(director, movie.director);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, director);
    }
}
