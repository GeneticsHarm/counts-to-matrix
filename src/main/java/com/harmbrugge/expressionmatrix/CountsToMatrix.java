/*
 * Copyright (c) 2016 Harm Brugge [harmbrugge@gmail.com].
 * All rights reserved.
 */
package com.harmbrugge.expressionmatrix;

import org.apache.commons.io.FileUtils;

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

    private ExpressionSparseMatrix expressionMatrix;

    private Path outputPath;
    private File[] countsFiles;

    public CountsToMatrix(Path pathToCountFiles, Path outputPath) {

        if (outputPath == null) outputPath = Paths.get(pathToCountFiles.toString(), "output");

        this.outputPath = outputPath;

        File countsDir = pathToCountFiles.toFile();

        countsFiles = FileUtils.listFiles(countsDir, new String[] {"txt"}, true).toArray(new File[0]);
    }

    public CountsToMatrix(Path pathToCountFiles) {
        this(pathToCountFiles, null);
    }

    public void createMatrix() throws IOException {

        List<String> geneNames = extractGeneNames(countsFiles[0]);
        List<String> sampleNames = extractSampleNames();

        expressionMatrix = new ExpressionSparseMatrix(geneNames, sampleNames);

        readFiles();
        writeMatrix();

    }


    private void readFiles() throws IOException {
        IntStream.range(0, countsFiles.length).parallel().forEach(columnIndex -> {

            File countFile = countsFiles[columnIndex];

            try (BufferedReader br = new BufferedReader(new FileReader(countFile))) {
                br.readLine();
                br.readLine();
                String line = br.readLine();

                int rowIndex = 0;

                while (line != null) {

                    String[] splitLine = line.split("\t");
                    int expressionCount = Integer.parseInt(splitLine[6]);

                    if (expressionCount != 0) expressionMatrix.set(rowIndex, columnIndex, expressionCount);

                    rowIndex++;
                    line = br.readLine();
                }
            } catch (IOException e) {
                //todo: Log it probably...
            }

        });
    }

    private void writeMatrix() throws IOException {
        SparseMtxWriter sparseWriter = new SparseMtxWriter(outputPath, expressionMatrix);
        sparseWriter.write();
    }

    private List<String> extractSampleNames() {
        List<String> sampleNames = new ArrayList<>();

        for (File sampleFile :countsFiles) {
            sampleNames.add(sampleFile.getName());
        }

        return sampleNames;
    }

    private List<String> extractGeneNames(File file) throws IOException {
        List<String> geneNames = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            br.readLine();
            String line = br.readLine();

            while (line != null) {

                String[] splitLine = line.split("\t");
                if (splitLine.length > 0) {
                    String geneName = splitLine[0];
                    geneNames.add(geneName);
                }
                line = br.readLine();

            }
        }

        return geneNames;
    }
}
