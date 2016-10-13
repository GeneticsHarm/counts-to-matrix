/*
 * Copyright (c) 2016 Harm Brugge [harmbrugge@gmail.com].
 * All rights reserved.
 */
package com.harmbrugge.expressionmatrix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Harm Brugge
 * @version 0.0.1
 */
public class RegularMatrixWriter implements MatrixWriter {

    private static final String FILENAME = "matrix.tsv";

    private File outputFile;
    private final ExpressionSparseMatrix matrix;

    public RegularMatrixWriter(Path outputPath, ExpressionSparseMatrix matrix) {
        this.matrix = matrix;

        this.outputFile = new File(Paths.get(outputPath.toString(), FILENAME).toString());
    }

    @Override
    public void write() throws IOException {

        if (!outputFile.exists()) outputFile.createNewFile();

        FileWriter fileWriter = new FileWriter(outputFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fileWriter);

        bw.write("gene-id");

        for (String sampleName : matrix.getSampleNames()) {
            bw.write("\t");
            bw.write(sampleName);
        }

        bw.write("\n");

        for (int rowIndex = 0; rowIndex < matrix.numRows(); rowIndex++) {

            bw.write(matrix.getGeneNames().get(rowIndex));

            for (int columnIndex = 0; columnIndex < matrix.numColumns(); columnIndex++) {
                bw.write("\t");
                bw.write(String.valueOf((int) matrix.get(rowIndex, columnIndex)));
            }

            bw.write("\n");
        }

        bw.close();

    }
}
