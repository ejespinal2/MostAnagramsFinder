import java.util.Arrays;
import java.util.Iterator;
import java.io.*;
/**
 * Class that parses through a .txt fileand finds the word or words with the most anagrams
 *
 * @author Elijah Espinal
 * @date 5/7/2024
 */

public class MostAnagramsFinder {
    /**
     * The main method for the MostAnagramsFinder program.
     * Checks Command Line Arguments
     * Throws an IOException
     */

    public static void main(String[] args) throws IOException {
        //Parse the command line arguments
        // Check if the number of arguments provided is correct
        if (args.length != 2) {
            System.err.println("Usage: java MostAnagramsFinder <dictionary file> <bst|rbt|hash>");
            System.exit(1);
        }

        String dictionaryFile = args[0];
        File file = new File(dictionaryFile);


        // Check if the dictionary file exists
        if (!file.exists()) {
            System.err.println("Error: Cannot open file '" + args[0] + "' for input.");
            System.exit(1);
        }

        String dataStructure = args[1];
        MyMap<String, MyList<String>> map = null;

        //Parse the command lne to instantiate the appropriate data structure
        if (dataStructure.equals("bst")) {
            map = new BSTreeMap<>();
        } else if (dataStructure.equals("rbt")) {
            map = new RBTreeMap<>();
        } else if (dataStructure.equals("hash")) {
            map = new MyHashMap<>();
        } else {
            System.err.println("Error: Invalid data structure '" + args[1] + "' received.");
            System.exit(1);
        }

        //Try to populate map
        try {
            populateMap(dictionaryFile, map);
        } catch (IOException e) {
            System.err.println("Error: An I/O error occurred reading '" + args[0] + "'.");
            System.exit(1);
        }

        //Call the function that displays the output
        findMostAnagrams(map);
    }

    /**
     * instantiates map based on data structure chosen by user
     *
     * @param dataStructure the chosen data structure the user decides
     * @return new Map type correlated with data structure or null if user did not type in bst,hash,rbt
     */
    private static MyMap<String, MyList<String>> createMap(String dataStructure) {
        //Use switch-cases to designate what map should be created based on the input data structure
        switch (dataStructure) {
            case "bst":
                return new BSTreeMap<>();
            case "rbt":
                return new RBTreeMap<>();
            case "hash":
                return new MyHashMap<>();
            default:
                return null;
        }

    }

    /**
     * Populates the map with words from the dictionary file.
     *
     * @param dictionaryFileName The name of the dictionary file.
     * @param map                The map data structure.
     * @param args               Command line arguments.
     */
    private static void populateMap(String file, MyMap<String, MyList<String>> map) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim().toLowerCase();
                String sortedWord = sortChars(word);

                MyList<String> anagramsList = map.get(sortedWord);
                if (anagramsList == null) {
                    anagramsList = new MyLinkedList<>();
                    map.put(sortedWord, anagramsList);
                }
                //Adds original version of word to ensure capitalization is preserved
                anagramsList.add(line);
            }
        }
    }

    /**
     * Sorts the characters of a word lexicographically.
     *
     * @param arr: the input word in the form of an array of charachters's
     */
    private static void insertionSort(char[] arr) {
        for (int idx = 1; idx < arr.length; idx++) {
            char temp = arr[idx];
            int j = idx - 1;
            while (j >= 0 && arr[j] > temp) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = temp;
        }
    }

    /**
     * Finalizes creation of key mapping for an input word. Works with insertionsort(char[arr]
     * to ensure that a word becomes its key by being completely lowercase and in alphabetical order.
     *
     * @param line: word that is taken in to parse into key
     * @return String of the key to be inserted in populateMap
     */
    private static String sortChars(String line) {
        //finish creating the key: make lowercase and sort letters alphabetically
        char[] arr = line.toLowerCase().toCharArray();
        insertionSort(arr);
        return new String(arr);
    }

    /**
     * Performs insertionSort on the groups of anagrams to ensure
     * that the words in each group are alphabetically sorted with capitalized letters first
     *
     * @param group: list of anagrams in one group
     */

    private static void insertionSort(MyList<String> group) {
        for (int idx = 1; idx < group.size(); idx++) {
            String line = group.get(idx);
            int j = idx - 1;
            while (j >= 0 && group.get(j).compareTo(line) > 0) {
                group.set(j + 1, group.get(j));
                j = j - 1;
            }
            group.set(j + 1, line);
        }
    }

    /**
     * Performs insertionSort on the separate groups to make sure that the groups are in alphabetical order
     *
     * @param groups: lists of different anagram sets
     */
    private static void groupSort(MyList<MyList<String>> groups) {
        for (int idx = 1; idx < groups.size(); idx++) {
            MyList<String> currentGroup = groups.get(idx);
            int j = idx - 1;

            // Compare current group's first word with previous groups' first words
            while (j >= 0 && compareFirstWords(currentGroup, groups.get(j)) < 0) {
                groups.set(j + 1, groups.get(j));
                j--;
            }

            groups.set(j + 1, currentGroup);
        }
    }

    /**
     * sortAnagramsGroups helper method: compares groups based on the first word lexicographically
     *
     * @param group1 first group being compared in the while loop
     * @param group2 second group being compared in the while loop
     * @return which group should go ahead of the other based on alphebetization
     */
    private static int compareFirstWords(MyList<String> group1, MyList<String> group2) {
        String word1 = group1.get(0);
        String word2 = group2.get(0);
        return word1.compareToIgnoreCase(word2);
    }


    /**
     * Compares anagram groups to find which group has the highest number of anagrams
     * Outputs the group, the number of anagrams it contains, and the group in list form
     * In the case several groups have the max number of anagrams,
     * the number of groups is displayed along with their anagram count and the group themselves
     *
     * @param map - map being parsed to find anagram groups
     */
    private static void findMostAnagrams(MyMap<String, MyList<String>> map) {
        int maxAnagramCount = 0;
        //Is the Group count in output
        MyList<MyList<String>> maxAnagramGroups = new MyLinkedList<>();
        //Is the lists displayed in output

        Iterator<Entry<String, MyList<String>>> entryIterator = map.iterator();
        while (entryIterator.hasNext()) {
            Entry<String, MyList<String>> entry = entryIterator.next();
            MyList<String> anagramsList = entry.value;

            //logic to find group with most anagrams, stored in maxAnagramGroups
            if (anagramsList.size() > 1) {
                int currentAnagramCount = anagramsList.size();

                if (currentAnagramCount > maxAnagramCount) {
                    maxAnagramCount = currentAnagramCount;
                    maxAnagramGroups.clear();
                    maxAnagramGroups.add(anagramsList);
                } else if (currentAnagramCount == maxAnagramCount) {
                    maxAnagramGroups.add(anagramsList);
                }
            }
        }

        //sorts the max anagram groups alphabetically
        groupSort(maxAnagramGroups);


        //1/2 of final output
        if (maxAnagramCount == 0) {
            System.out.println("No anagrams found.");
        } else {
            System.out.println("Groups: " + maxAnagramGroups.size() + ", Anagram count: " + maxAnagramCount);
        }


        Iterator<MyList<String>> iterator = maxAnagramGroups.iterator();
        //adds the max anagram groups to what will be shown in the output
        while (iterator.hasNext()) {
            MyList<String> group = iterator.next();
            //sorts the words in each group
            insertionSort(group);


            System.out.print("[");
            Iterator<String> wordIterator = group.iterator();
            while (wordIterator.hasNext()) {
                System.out.print(wordIterator.next());
                if (wordIterator.hasNext()) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
        }

    }
}