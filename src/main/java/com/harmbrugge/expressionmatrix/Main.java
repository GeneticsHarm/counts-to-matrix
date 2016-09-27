/*
 * Copyright (c) 2016 Harm Brugge [harmbrugge@gmail.com].
 * All rights reserved.
 */
package com.harmbrugge.expressionmatrix;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main class to parse commandline arguments and instantiate CountsToMatrix
 *
 * @see CountsToMatrix
 *
 * @author Harm Brugge
 * @version 0.0.1
 */
public class Main {


    public static void main(String[] args) {
        Main main = new Main();
        main.start(args);
    }

    private void start(String[] args) {
        Path pathToCountFiles;
        Path outputPath = null;

        CommandLineParser parser = new DefaultParser();

        Options options = new Options();
        options.addOption("i", "input", true, "Input path/directory to count files (.txt)");
        options.addOption("o", "output", true, "output folder for the matrix");
        options.addOption("h", "help", false, "Print help");

        try {
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(this.getInfo(), options);
                System.exit(1);
            }

            if (!line.hasOption("i")) {
                System.out.println("No input file specified \n");
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(this.getInfo(), options);
                System.exit(1);
            }

            pathToCountFiles = Paths.get(line.getOptionValue("i"));

            if (line.hasOption("o")) outputPath = Paths.get(line.getOptionValue("o"));

            CountsToMatrix countsToMatrix = new CountsToMatrix(pathToCountFiles, outputPath);
            countsToMatrix.createMatrix();
        } catch (ParseException exp) {
            System.out.println("Parse exception:" + exp.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private String getInfo() {
        return " * CountsToMatrix converts a list of count files, generated in featureCount, to a count matrix.\n" +
                " * All the .txt files in the input directory will be used.\n" +
                " * \n" +
                " * Uses a parallel stream to read the files. Therefore the columns (representing a file) are not ordered\n" +
                " * \n" +
                " * The same annotation files should be used for generation of the count files, as this script assumes all the rows/lines\n" +
                " * in the count files are in the same order.\n" +
                " * \n" +
                " * Example:\n" +
                " * \n" +
                " * cell_0001.txt\n" +
                " * ENSG00000240361\t1\t62948\t63887\t+\t940\t20\n" +
                " * ENSG00000186092\t1\t69091\t70008\t+\t918\t89\n" +
                " * \n" +
                " * cell_0002.txt\n" +
                " * ENSG00000240361\t1\t62948\t63887\t+\t940\t0\n" +
                " * ENSG00000186092\t1\t69091\t70008\t+\t918\t200\n" +
                " * \n" +
                " * Will be converted into:\n" +
                " * gene-id  cell_0001\tcell_00002\n" +
                " * ENSG00000240361\t20\t0\n" +
                " * ENSG00000186092\t80\t200";
    }

}
