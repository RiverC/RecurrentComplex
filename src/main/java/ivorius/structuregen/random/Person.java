/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.structuregen.random;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Person
{
    public static enum NameType
    {
        NORDIC,
        CHAOTIC
    }

    private static List<String> vowels = Arrays.asList("a", "e", "i", "o", "u", "ei", "ai", "ou", "j", "ji", "y", "oi", "au", "oo");
    private static List<String> startConsonants = Arrays.asList("b", "c", "d", "f", "g", "h", "k", "l", "m", "n", "p", "q", "r", "s", "t", "v", "w", "x", "z", "ch", "bl", "br", "fl", "gl", "gr", "kl", "pr", "st", "sh", "th");
    private static List<String> endConsonants = Arrays.asList("b", "d", "f", "g", "h", "k", "l", "m", "n", "p", "r", "s", "t", "v", "w", "z", "ch", "gh", "nn", "st", "sh", "th", "tt", "ss", "pf", "nt");
    private static List<String> chaoticNameBlueprints = Arrays.asList("vdv", "cvdvd", "cvd", "vdvd");

    public static List<String> nordicNamesMale = Arrays.asList("Erik", "Magnus", "John", "William", "Lukas", "Elias",
            "Malik", "Aron", "Enuk", "Christian", "Peter", "Hans", "Jens", "Niels", "Kristian", "Aage", "Johannes",
            "Carl", "Svend", "Sven", "Jakup", "Benjamin", "Danjal", "Hanus", "Rei", "Simun", "Bardur", "Johan", "Jonas",
            "Aleksi", "Ville", "Niko", "Juho", "Teemu", "Joonas", "Jesse", "Joni", "Jere", "Antti", "Ole", "Lars", "Jorgen",
            "Jakob", "Jon", "Daniel", "Sigurdur", "Arnar", "Kristofer", "Einar", "Gunnar", "Alexander", "Andri", "Viktor",
            "Olof", "Lennart", "Pall");
    public static List<String> nordicNamesFemale = Arrays.asList("Emma", "Eva", "Sofia", "Pipaluk", "Emilia", "Alice",
            "Marie", "Anna", "Margrethe", "Kristine", "Johanne", "Karen", "Elisabeth", "Ellen", "Ingeborg", "Rebekka",
            "Helena", "Vir", "Ronja", "Katrin", "Liv", "Maria", "Sara", "Jenna", "Laura", "Roosa", "Veera", "Emilia",
            "Julia", "Sara", "Jenni", "Noora", "Ane", "Johanne", "Dorthe", "Margrethe", "Sofie", "Else", "Amalie", "Gudrun",
            "Helga", "Birta", "Maria");

    private String firstName;
    private String middleName;
    private String lastName;

    public Person(String firstName, String middleName, String lastName)
    {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

    public static Person randomHuman(Random random, boolean male)
    {
        NameType nameType = random.nextFloat() < 0.95f ? NameType.NORDIC : NameType.CHAOTIC;

        return randomPerson(random, male, nameType);
    }

    public static Person randomPerson(Random random, boolean male, NameType type)
    {
        switch (type)
        {
            case CHAOTIC:
                return randomChaoticPerson(random, male);
            case NORDIC:
                return randomNordicPerson(random, male);
        }

        throw new RuntimeException();
    }

    private static Person randomChaoticPerson(Random random, boolean male)
    {
        return new Person(chaoticName(random, male), random.nextFloat() < 0.15f ? chaoticName(random, male) : null, random.nextFloat() < 0.4f ? chaoticName(random, male) : null);
    }

    private static Person randomNordicPerson(Random random, boolean male)
    {
        String middleName = random.nextFloat() < 0.3f ? nordicName(random, male) : null;
        String lastName = random.nextFloat() < 0.95f ? nordicLastName(random, male, random.nextFloat() < 0.1f) : null; // People were named after their fathers. It's a fact, not sexist :P
        return new Person(nordicName(random, male), middleName, lastName);
    }

    public static String nordicName(Random random, boolean male)
    {
        return male ? getRandomElementFrom(nordicNamesMale, random) : getRandomElementFrom(nordicNamesFemale, random);
    }

    public static String nordicLastName(Random random, boolean male, boolean parentMale)
    {
        return nordicName(random, parentMale) + (male ? "sson" : "sdottir");
    }

    public static String chaoticName(Random random, boolean male)
    {
        StringBuilder name = new StringBuilder();

        name.append(firstCharUppercase(parseChaoticName(getRandomElementFrom(chaoticNameBlueprints, random), random)));

        if (random.nextFloat() < 0.2f)
        {
            name.append("-").append(firstCharUppercase(parseChaoticName(getRandomElementFrom(chaoticNameBlueprints, random), random)));
        }

        return name.toString();
    }

    private static String parseChaoticName(String blueprint, Random random)
    {
        StringBuilder name = new StringBuilder();

        for (int i = 0; i < blueprint.length(); i++)
        {
            char ch = blueprint.charAt(i);

            switch (ch)
            {
                case 'v':
                    name.append(getRandomElementFrom(vowels, random));
                    break;

                case 'c':
                    name.append(getRandomElementFrom(startConsonants, random));
                    break;

                case 'd':
                    name.append(getRandomElementFrom(endConsonants, random));
                    break;
            }
        }

        return name.toString();
    }

    private static String firstCharUppercase(String name)
    {
        return Character.toString(name.charAt(0)).toUpperCase() + name.substring(1);
    }

    private static <O> O getRandomElementFrom(List<O> list, Random random)
    {
        return list.get(random.nextInt(list.size()));
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getMiddleName()
    {
        return middleName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getFullName()
    {
        StringBuilder builder = new StringBuilder();

        if (firstName != null)
        {
            builder.append(firstName);
        }

        if (middleName != null)
        {
            builder.append(' ').append(middleName);
        }

        if (lastName != null)
        {
            builder.append(' ').append(lastName);
        }

        return builder.toString().trim();
    }
}