/*
 * Copyright (c) 2016 Harm Brugge [harmbrugge@gmail.com].
 * All rights reserved.
 */
package com.harmbrugge.expressionmatrix;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * CountsToMatrix converts a list of count files, generated in featureCount, to a count matrix.
 * All the .txt files in the input directory will be used.
 *
 * Uses a parallel stream to read the files. Therefore the columns (representing a file) are not ordered
 *
 * The same annotation files should be used for generation of the count files, as this script assumes all the rows/lines
 * in the count files are in the same order.
 *
 * Example:
 *
 * cell_0001.txt
 * ENSG00000240361	1	62948	63887	+	940	20
 * ENSG00000186092	1	69091	70008	+	918	89
 *
 * cell_0002.txt
 * ENSG00000240361	1	62948	63887	+	940	0
 * ENSG00000186092	1	69091	70008	+	918	200
 *
 * Will be converted into:
 * gene-id  cell_0001	cell_00002
 * ENSG00000240361	20	0
 * ENSG00000186092	80	200
 *
 * @author Harm Brugge
 * @version 0.0.1
 */
public class CountsToMatrix {

    private int[][] expressionMatrix;
    private List<String> rowNames;
    private String[] columnNames;
    private int rowCount;
    private int columnCount;

    private Path outputPath;

    private File[] countsFiles;

    public CountsToMatrix(Path pathToCountFiles, Path outputPath) {

        if (outputPath == null) outputPath = Paths.get(pathToCountFiles.toString(), "output");

        this.outputPath = outputPath;

        File countsDir = pathToCountFiles.toFile();

        countsFiles = countsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

        columnNames = new String[countsFiles.length];
        rowNames = new ArrayList<>();
    }

    public CountsToMatrix(Path pathToCountFiles) {
        this(pathToCountFiles, null);
    }

    public void createMatrix() throws IOException {

        if (countsFiles != null && countsFiles.length > 0) {
            rowCount = extractGeneIds(countsFiles[0]);
            columnCount = countsFiles.length;
        }

        expressionMatrix = new int[rowCount][columnCount];

        readFiles();
        writeMatrix();

    }


    private void readFiles() throws IOException {
        IntStream.range(0, countsFiles.length).parallel().forEach(columnIndex -> {

            File countFile = countsFiles[columnIndex];
            columnNames[columnIndex] = countFile.getName();

            try (BufferedReader br = new BufferedReader(new FileReader(countFile))) {
                br.readLine();
                br.readLine();
                String line = br.readLine();

                int rowIndex = 0;

                while (line != null) {

                    String[] splitLine = line.split("\t");
                    int expressionCount = Integer.parseInt(splitLine[6]);

                    expressionMatrix[rowIndex][columnIndex] = expressionCount;

                    rowIndex++;
                    line = br.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void writeMatrix() throws IOException {

        outputPath.toFile().mkdirs();

        File outputFile = new File(outputPath + "/expression-matrix.tsv");

        if (!outputFile.exists()) outputFile.createNewFile();

        FileWriter fileWriter = new FileWriter(outputFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fileWriter);
        bw.write("gene-id");

        for (String geneId : columnNames) {
            bw.write("\t");
            bw.write(geneId);
        }

        bw.write("\n");

        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {

            bw.write(rowNames.get(rowIndex));

            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                bw.write("\t");
                bw.write(String.valueOf(expressionMatrix[rowIndex][columnIndex]));
            }

            bw.write("\n");
        }

        bw.close();
    }

    private int extractGeneIds(File file) throws IOException {
        int idCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            br.readLine();
            String line = br.readLine();

            while (line != null) {

                String[] splitLine = line.split("\t");
                if (splitLine.length > 0) {
                    String geneId = splitLine[0];
                    idCount++;
                    rowNames.add(geneId);
                }
                line = br.readLine();

            }
        }

        return idCount;
    }
}
