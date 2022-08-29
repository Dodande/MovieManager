package com.company;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    private static MovieBase movieBase;
    private static FileHandler fileHandler;

    public static void main(String[] args) {
        initMovieBase();

        printMenu();
        executeCommand();

    }

    private static void executeCommand() {
        Scanner input = new Scanner(System.in);
        Map<String, Pattern> patternMap = processInput(input);
        if (patternMap.containsKey("l")) {
            movieBase.listMovieBase(patternMap).forEach(System.out::println);
        }
        if (patternMap.containsKey("a")) {
            if (patternMap.containsKey("p")) {
                handleAddPerson(input);
            }
            if (patternMap.containsKey("m")) {
                handleAddMovie(input);
            }
        }
    }

    private static void initMovieBase() {
        fileHandler = new FileHandler();
        movieBase = fileHandler.processFile();
    }

    private static void printMenu() {
        System.out.println("[l] list movies line by line");
        System.out.println("[-v] list movies by director,length,actors");
        System.out.println("[-t] regex for movie title");
        System.out.println("[-d] regex for director");
        System.out.println("[-a] regex for actors");
        System.out.println("[-la] ascending order");
        System.out.println("[-ld] descending order");

        System.out.println("[a] new entry");
        System.out.println("[-p] new people");
        System.out.println("[-m] new movie");

        System.out.println("[d -p] delete people");
    }

    private static Map<String, Pattern> processInput(Scanner in) {
        String input = in.nextLine();

        return createCommandPattern(input);
    }

    private static Map<String, Pattern> createCommandPattern(String input) {
        Map<String, Pattern> commandPattern = new HashMap<>();
        List<String> commandLines = Arrays.stream(input.split(" -")).collect(Collectors.toList());
        for (String commandLine : commandLines) {
            String key = String.valueOf(commandLine.toCharArray()[0]);
            Pattern value = null;
            int commandLineLength = commandLine.length();
            if (commandLineLength == 1) {
                commandPattern.put(key, value);
            }
            if (commandLine.length() > 1) {
                value = Pattern.compile(commandLine.substring(3, commandLineLength - 1));
                commandPattern.put(key, value);
            }
        }
        return commandPattern;
    }

    private static void handleAddPerson(Scanner input) {
        boolean exit = false;
        while (!exit) {
            System.out.println("Name: ");
            String name = input.nextLine();
            Person person = movieBase.findPersonByName(name);
            if (person == null) {
                System.out.println("Nationality: ");
                String nationality = input.nextLine();
                person = new Person(name, nationality);
                boolean addedPerson = movieBase.addPerson(person);
                if (addedPerson) {
                    fileHandler.writePersonToFile(person);
                }
                exit = true;
            }
        }
    }

    private static void handleAddMovie(Scanner input) {
        System.out.println("Title:");
        String title = input.nextLine();
        System.out.println("Length:");
        String length = input.nextLine();
        Person director = handleDirectorSearch(input);
        List<Person> actors = handleCollectActors(input);
        if (!movieBase.isMovieExist(title,director.getName())){
            Movie movie = movieBase.createAndaddMovie(title, director, actors, length);
            fileHandler.writeMovieToFile(movie);
        }
    }

    private static Person handleDirectorSearch(Scanner input) {
        Person director = null;
        while (director == null) {
            System.out.println("Director's name");
            String directorName = input.nextLine();
            director = handlePersonSearch(directorName);
        }
        return director;
    }

    private static List<Person> handleCollectActors(Scanner input) {
        List<Person> actors = new ArrayList<>();
        boolean exitActor = false;
        while (!exitActor) {
            System.out.println("Actors's name");
            String actorName = input.nextLine();
            if (!actorName.equals("exit")) {
                Person actor = handlePersonSearch(actorName);
                if (actor != null) {
                    actors.add(actor);
                }
            } else {
                exitActor = true;
            }
        }
        return actors;
    }

    private static Person handlePersonSearch(String name) {
        Person person = null;
        if (movieBase.isPersonExist(name)) {
            person = movieBase.findPersonByName(name);
        } else {
            System.out.println("Name not found in the database!");
        }
        return person;
    }

    private static boolean validInput(Map<String, Pattern> patternMap) {
        boolean valid = true;
        boolean ascending = patternMap.containsKey("la");
        boolean descending = patternMap.containsKey("ld");
        if (ascending && descending) {
            System.out.println("Can't decide order");
            valid = false;
        }
        if (patternMap.containsKey("t") && patternMap.get("t") == null) {
            System.out.println("No regex was given");
        }
        if (patternMap.containsKey("d") && patternMap.get("d") == null) {
            System.out.println("No regex was given");
        }
        if (patternMap.containsKey("a") && patternMap.get("a") == null) {
            System.out.println("No regex was given");
        }
        return valid;
    }
}
