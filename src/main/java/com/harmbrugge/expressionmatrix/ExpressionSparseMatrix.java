/*
 * Copyright (c) 2016 Harm Brugge [harmbrugge@gmail.com].
 * All rights reserved.
 */
package com.harmbrugge.expressionmatrix;

import no.uib.cipr.matrix.sparse.LinkedSparseMatrix;

import java.util.List;

/**
 * A linked sparse matrix that keeps track of the total amount of entries.
 *
 * Doesn't account for resets, so if a row and column is already present while setting it keeps counting.
 *
 * @author Harm Brugge
 * @version 0.0.1
 */
public class ExpressionSparseMatrix extends LinkedSparseMatrix {

    private int entriesCount;

    private List<String> geneNames;
    private List<String> sampleNames;

    public ExpressionSparseMatrix(List<String> geneNames, List<String> sampleNames) {
        super(geneNames.size(), sampleNames.size());

        this.geneNames = geneNames;
        this.sampleNames = sampleNames;
    }

    public void set(int row, int column, int value) {
        entriesCount++;
        super.set(row, column, value);
    }

    public int getEntriesCount() {
        return entriesCount;
    }

    public List<String> getGeneNames() {
        return geneNames;
    }

    public List<String> getSampleNames() {
        return sampleNames;
    }
}
