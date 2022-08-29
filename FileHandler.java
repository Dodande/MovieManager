package com.company;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/*
Lost London;Georgee Dond;7800;Liam Neeson: Hungary,Ewan McGregor:Taiwan,Natalie Portman:Poland;
 */

public class FileHandler {

    private static final String MOVIES_PATH = "src\\com\\company\\Movies.txt";
    private static final String PEOPLE_PATH = "src\\com\\company\\People.txt";




    public MovieBase processFile(){
        List<String> movieBaseLines = new ArrayList<>();
        List<String> peopeStringList = new ArrayList<>();
        try {
           movieBaseLines = Files.readAllLines(Paths.get(MOVIES_PATH));
           peopeStringList = Files.readAllLines(Paths.get(PEOPLE_PATH));
            System.out.println("File read is success");
        } catch (IOException e) {
            System.out.println("Failed to read the file");
        }
        List<Person> people = loadPeople(peopeStringList);
        return createMovieBase(movieBaseLines,people);
    }

    public void writePersonToFile(Person person){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(PEOPLE_PATH,true))) {
            writer.append(person.getName()).append(":").append(person.getNationality()).append(";\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeMovieToFile(Movie movie){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(MOVIES_PATH,true))) {
            StringBuilder actorBuilder = new StringBuilder();
            movie.getActors().stream().map(person -> new String(person.getName()+":"+person.getNationality())).forEach(actorBuilder::append);
            String movieString = movie.getTitle()+";"
                    +movie.getDirector().getName()+";"
                    +movie.getLength()+";"
                    + actorBuilder;
            writer.append("\n").append(movieString).append(";\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private List<Person> loadPeople(List<String> peopleStringList){
        List<Person> people = new ArrayList<>();
        for (String line : peopleStringList) {
            String[] personArray = line.split(":");
            if (personArray.length==2){
                Person person = new Person(personArray[0], personArray[1]);
                people.add(person);
            }
        }
        return people;
    }

    private  MovieBase createMovieBase(List<String> movieBaseLines,List<Person> people)  {
        List<Movie> movies = movieBaseLines.stream()
                .map(line -> createMovie(line,people))
                .collect(Collectors.toList());

        return MovieBase.getInstance(movies,people);
    }

    private Movie createMovie(String line, List<Person> people){
        String title="";
        int length=0;
        Person director=null;
        List<Person> actors =null;
        String[] lineArray = line.split(";");
        for (int i = 0; i < lineArray.length; i++) {
             title= lineArray[0];
             length = Integer.parseInt(lineArray[2]);
             director = addPersonToList(people,createPersonByLine(lineArray[1]));
            String[] actorArray = lineArray[3].split(",");
            actors = Arrays
                    .stream(actorArray)
                    .map(this::createPersonByLine)
                    .map(person -> addPersonToList(people,person))
                    .collect(Collectors.toList());
        }
        return new Movie(title,director,actors,length);
    }

    private Person createPersonByLine(String personLine){
        String [] personArray = personLine.split(":");
        Person person =null;
        if (personArray.length==1){
            person = new Person(personArray[0]);
        }
        else if (personArray.length==2){
            person = new Person(personArray[0],personArray[1]);
        }
        else {
            System.out.println("Wrong format");
        }
        return person;
    }

    private Person addPersonToList(List<Person> people, Person personToAdd)  {
        Person addedPerson =null;
        if (!people.contains(personToAdd)){
            people.add(personToAdd);
            writePersonToFile(personToAdd);
            addedPerson = personToAdd;
        }
        else {
            try {
                addedPerson = people.stream().filter(person -> person.equals(personToAdd)).findFirst().orElseThrow(Exception::new);
            } catch (Exception e) {
                System.out.println("Person not found");
            }
        }
        return addedPerson;
    }



}
