/*
 * Copyright (c) 2016 Harm Brugge [harmbrugge@gmail.com].
 * All rights reserved.
 */
package com.harmbrugge.expressionmatrix;

import no.uib.cipr.matrix.MatrixEntry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * @author Harm Brugge
 * @version 0.0.1
 */
public class SparseMtxWriter implements MatrixWriter {

    private static final String FILENAME = "matrix.mtx";

    private File outputFile;
    private final ExpressionSparseMatrix matrix;

    public SparseMtxWriter(Path outputPath, ExpressionSparseMatrix matrix) {
        outputPath.toFile().mkdirs();

        this.outputFile = new File(Paths.get(outputPath.toString(), FILENAME).toString());
        this.matrix = matrix;
    }

    @Override
    public void write() throws IOException {

        if (!outputFile.exists()) outputFile.createNewFile();

        FileWriter fileWriter = new FileWriter(outputFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fileWriter);

        bw.write("%%MatrixMarket matrix coordinate real general\n");
        bw.write("%\n");
        // The header line
        bw.write(matrix.numRows() + " " + matrix.numColumns() + " " + matrix.getEntriesCount() + "\n");

        Iterator<MatrixEntry> iterator = matrix.iterator();

        // first entry is empty for some reason, I think it's supposed to be the header line
        if (iterator.hasNext()) {
            MatrixEntry entry = iterator.next();
            if (entry.get() != 0) {
                bw.write((entry.row() + 1) + " " + (entry.column() + 1) + " " + ((int) entry.get()) + "\n");
            }
        }

        while (iterator.hasNext()) {
            MatrixEntry entry = iterator.next();
            bw.write((entry.row() + 1) + " " + (entry.column() + 1) + " " + ((int) entry.get()) + "\n");
        }

        bw.close();

    }
}
