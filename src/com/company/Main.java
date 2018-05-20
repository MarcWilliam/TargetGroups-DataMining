package com.company;

import com.opencsv.*;
import javafx.util.Pair;

import java.io.*;
import java.util.Arrays;

public class Main {


    public static void main(String[] args) throws IOException {

        String[] labels = {"something", "somethingelse"};

        NdimPoint data[] = {
                new NdimPoint(1, 1),
                new NdimPoint(2, 1),
                new NdimPoint(1, 4),
                new NdimPoint(2, 4),
        };

        /*
        Pair<String[],NdimPoint[]> tmp = parseFile("data.csv");
        String[] labels = tmp.getKey();
        NdimPoint data = tmp.getValue();
        */

        Cluster[] clusters = kmean(labels, data, 2);

    }


    static Pair<String[], NdimPoint[]> parseFile(String filePath) throws IOException {
        // <<<<<<<<<<<<<<<<<< Abdelhamed ur job starts here feel free to delete the body just try to keep the function signature the same
        NdimPoint data[] = {};
        String labels[] = {};

        CSVReader reader = new CSVReader(new FileReader(filePath));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            System.out.println(nextLine[0] + nextLine[1]);
        }

        return new Pair<>(labels, data);
    }


    static Cluster[] kmean(String labels[], NdimPoint data[], int noClusters) {

        int n = 0, noOCorrectCentroid;

        Cluster clusters[] = new Cluster[noClusters];
        NdimPoint newCentroids[] = new NdimPoint[noClusters];
        System.arraycopy(data, 0, newCentroids, 0, noClusters);

        do {
            n++;

            // initialize the clusters
            for (int i = 0; i < noClusters; i++) {
                clusters[i] = new Cluster(newCentroids[i], data.length);
            }

            // fill the clusters
            for (int i = 0; i < data.length; i++) {

                int nearestCluster = -1;
                double nearestClusterDist = Double.MAX_VALUE;
                for (int j = 0; j < clusters.length; j++) {
                    double tempDist = clusters[j].calcDistance(data[i]);
                    if (tempDist <= nearestClusterDist) {
                        nearestCluster = j;
                        nearestClusterDist = tempDist;
                    }
                }

                clusters[nearestCluster].push(data[i]);
            }

            // find the new centroids
            noOCorrectCentroid = 0;
            for (int j = 0; j < clusters.length; j++) {
                newCentroids[j] = clusters[j].calcNewCentroid();
                if (newCentroids[j].equals(clusters[j].centroid)) {
                    noOCorrectCentroid++;
                }
            }

            PrintClusters(labels, clusters, n);

        } while (noOCorrectCentroid < newCentroids.length);

        return clusters;
    }


    private static void PrintClusters(String labels[], Cluster clusters[], int n) {
        System.out.println("\n\n============================================= \tLoop " + n + "\t ==============================================");
        for (int i = 0; i < clusters.length; i++) {
            for (int j = 0; j < clusters[i].centroid.dims.length; j++) {
                System.out.print(labels[j] + ": " + clusters[i].centroid.dims[j] + "\t\t");
            }
            System.out.println();
            System.out.println(Arrays.toString(clusters[i].data).replace(", ", "\t").replace("]", "").replace("[", ""));
            System.out.println("-------------------------------------------------------------------------------------------------------");
        }

        System.out.println();
    }


    static class Cluster {
        NdimPoint data[], centroid, sum;
        int last;

        public Cluster(NdimPoint centroid, int size) {
            this.data = new NdimPoint[size];
            this.sum = new NdimPoint(new double[centroid.dims.length]);
            this.centroid = centroid;
        }

        public void push(NdimPoint point) {
            data[last++] = point;
            sum.sum(point);
        }

        public NdimPoint calcNewCentroid() {
            return sum.div(last);
        }

        public double calcDistance(NdimPoint point) {
            return centroid.calcDistance(point);
        }
    }


    static class NdimPoint {
        double dims[];

        public NdimPoint(double... dims) {
            this.dims = dims;
        }

        public boolean equals(NdimPoint obj) {
            return Arrays.equals(obj.dims, this.dims);
        }

        public NdimPoint div(double div) {

            NdimPoint ret = new NdimPoint(new double[this.dims.length]);
            for (int i = 0; i < this.dims.length; i++) {
                ret.dims[i] = Math.round(this.dims[i] / div);
            }
            return ret;
        }

        public NdimPoint sum(NdimPoint point) {
            if (this.dims.length != point.dims.length) {
                throw new ArithmeticException("both point are not in same dimension");
            }

            NdimPoint ret = new NdimPoint(new double[this.dims.length]);
            for (int i = 0; i < this.dims.length; i++) {
                ret.dims[i] = this.dims[i] + point.dims[i];
            }
            return ret;
        }

        /**
         * calculates euclidian distance bettwen 2 points
         *
         * @param point
         * @return
         */
        public double calcDistance(NdimPoint point) {
            if (this.dims.length != point.dims.length) {
                throw new ArithmeticException("both point are not in same dimension");
            }

            double result = 0;
            for (int i = 0; i < this.dims.length; i++) {
                result += Math.pow(this.dims[i] - point.dims[i], 2);
            }
            return Math.sqrt(result);
        }

        @Override
        public String toString() {
            return Arrays.toString(dims).replace("[", "(").replace("]", ")");
        }
    }
}
