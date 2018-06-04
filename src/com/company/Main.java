package com.company;

import com.opencsv.*;
import javafx.util.Pair;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

public class Main {

    public static HashMap<String, Pair<Label, Integer>> entriesMap = new HashMap<>();

    public static void main(String[] args) throws IOException {


        Pair<Label[], NdimPoint[]> tmp = parseFile(
                // source of data sheets
                //https://catalog.data.gov/dataset?q=education+age+gender&sort=views_recent+desc&res_format=CSV&as_sfid=AAAAAAWrcApUV3CPP00NLoWQSRorWRwc0YpkcquUjUOWBKUz1zWfkPwaO_LxGzoNWL5OV9aAm-oo3tfqQzBonDuXonc0wb_Lsl7SFF6SdxKiEzxS7W5ZzazBv8wUM5vfVLj6Yh4%3D&as_fid=a8ee4b572e8a8d7ff57f32462dc9f17212d17433&ext_location=&ext_bbox=&ext_prev_extent=-142.03125%2C8.754794702435618%2C-59.0625%2C61.77312286453146
                //"data3.csv"
                "Behavioral_Risk_Factor_Data__Tobacco_Use__2011_to_presentc.csv"
                //"Heart_Disease_Mortality_Data_Among_US_Adults__35___by_State_Territory_and_County.csv"
        );
        Label[] labels = tmp.getKey();
        NdimPoint[] data = tmp.getValue();
        Cluster[] clusters = kmean(labels, data, 2);

        PrintClusters(labels, clusters, -1, false);
    }

    static Pair<Label[], NdimPoint[]> parseFile(String filePath) throws IOException {
        ArrayList<NdimPoint> data = new ArrayList<>(500);
        Label[] labels;

        CSVReader reader = new CSVReader(new FileReader(filePath));

        labels = Arrays.stream(reader.readNext()).map((String label) -> {
            return new Label(label);
        }).toArray(Label[]::new);

        String[] nextLine;
        String[] NdimPointData;
        boolean firstLineFlag = true;

        while ((nextLine = reader.readNext()) != null) {
            NdimPointData = new String[labels.length];
            for (int i = 0; i < nextLine.length; i++) {
                String value = nextLine[i];
                if (!value.matches("\\d+")) {
                    if (!Main.entriesMap.containsKey(value)) {
                        Main.entriesMap.put(value, new Pair<>(labels[i], labels[i].getDistinctIndex()));
                        labels[i].setDistinctIndex(labels[i].getDistinctIndex() + 1);
                    }
                    NdimPointData[i] = String.valueOf(Main.entriesMap.get(value).getValue());
                } else {
                    NdimPointData[i] = value;
                }
            }

            /* NdimPointData = Arrays.stream(nextLine).map(value -> {

                if (!value.matches("\\d+")) {
                    if (!Main.entriesMap.containsKey(value)) {
                        Main.entriesMap.put(value, new Pair<>(labels[2], Main.entriesMap.size() + 1));
                    }
                    return String.valueOf(Main.entriesMap.get(value).getValue());
                }
                return value;
            }).toArray(String[]::new);*/
            data.add(new NdimPoint(Arrays.stream(NdimPointData).mapToDouble(Double::parseDouble).toArray()));

            if (firstLineFlag) {
                for (int j = 0; j < nextLine.length; j++) {
                    if (!nextLine[j].matches("\\d+")) {
                        labels[j].setType(String.class);
                    } else {
                        labels[j].setType(Double.class);
                    }
                }
                firstLineFlag = false;
            }
        }
        return new Pair<>(labels, data.toArray(new NdimPoint[data.size()]));
    }

    static Cluster[] kmean(Label labels[], NdimPoint data[], int noClusters) {

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

            //PrintClusters(labels, clusters, n,true);

        } while (noOCorrectCentroid < newCentroids.length);

        return clusters;
    }

    private static void PrintClusters(Label labels[], Cluster clusters[], int n, boolean printClusterData) {

        String ANSI_RESET = "\u001B[0m";
        String ANSI_BLACK = "\u001B[30m";
        String ANSI_RED = "\u001B[31m";
        String ANSI_GREEN = "\u001B[32m";
        String ANSI_YELLOW = "\u001B[33m";
        String ANSI_BLUE = "\u001B[34m";
        String ANSI_PURPLE = "\u001B[35m";
        String ANSI_CYAN = "\u001B[36m";
        String ANSI_WHITE = "\u001B[37m";

        //System.out.print(ANSI_BLUE);
        if (n == -1) {
            //System.out.println(ANSI_CYAN + "RESULT\tRESULT\tRESULT\tRESULT\tRESULT\tRESULT\tRESULT\tRESULT\tRESULT\tRESULT\tRESULT\tRESULT\tRESULT\tRESULT\tRESULT\tRESULT\t" + ANSI_RESET);
        } else {
            System.out.println("\n\n============================================= \tLoop " + n + "\t ==============================================");
        }


        String leftAlignFormat = "%-2s ";
        String[] tempLabels = new String[labels.length + 2];
        tempLabels[0] = "n*";
        tempLabels[tempLabels.length - 1] = "% from total data";

        for (int i = 0; i < labels.length; i++) {
            tempLabels[i + 1] = labels[i].getText();
            leftAlignFormat += ANSI_CYAN + "|" + ANSI_RESET + " %-18.18s ";
        }
        leftAlignFormat += ANSI_CYAN + "|" + ANSI_RESET + " %4s %n";

        System.out.format(leftAlignFormat, tempLabels);
        System.out.println();
        System.out.print(ANSI_RESET);

        String[] dataToPrint = new String[clusters[0].centroid.dims.length + 2];

        for (int i = 0; i < clusters.length; i++) {
            dataToPrint[0] = "" + i;
            for (int j = 0; j < clusters[i].centroid.dims.length; j++) {

                String centroidValue = "";
                if (labels[j].getType().equals(String.class)) {
                    for (Entry<String, Pair<Label, Integer>> entry : Main.entriesMap.entrySet()) {
                        String key = entry.getKey();
                        Pair<Label, Integer> item = entry.getValue();
                        if (labels[j].getText().equals(item.getKey().getText()) && Math.round(clusters[i].centroid.dims[j]) == item.getValue()) {
                            centroidValue = key;
                        }
                    }

                } else {
                    centroidValue = String.valueOf(Math.round(clusters[i].centroid.dims[j]));
                }
                dataToPrint[j + 1] = centroidValue;
                //System.out.print(labels[j].getText() + ": " + ANSI_CYAN + centroidValue + ANSI_RESET + "\t\t");
            }
            dataToPrint[dataToPrint.length - 1] = (clusters[i].last * 100 / clusters[i].data.length) + "%";
            System.out.format(leftAlignFormat, dataToPrint);
            //System.out.println(ANSI_RED + (clusters[i].last * 100 / clusters[i].data.length) + "%" + ANSI_RESET + " of the data set is in this cluster");

            if (printClusterData) {
                String tmp = Arrays.toString(clusters[i].data)
                        .replace(", ", "\t")
                        .replace("]", "")
                        .replace("[", "")
                        .replace("\tnull", "");

                char[] ar = new char[tmp.length() * 2];
                Arrays.fill(ar, '-');
                System.out.println(tmp);
                System.out.println(ar);
            }
        }

        System.out.println();
    }

    static class Label {

        private String text;
        private Class type;
        private int distinctIndex;

        public Label(String text) {
            this.text = text;
            this.distinctIndex = 1;
        }

        public Label(String text, Class type) {
            this.text = text;
            this.type = type;
            this.distinctIndex = 1;
        }

        public Label(String text, Class type, int distinctIndex) {
            this.text = text;
            this.type = type;
            this.distinctIndex = distinctIndex;
        }

        public String getText() {
            return text;
        }

        public Class getType() {
            return type;
        }

        public int getDistinctIndex() {
            return distinctIndex;
        }

        public void setType(Class type) {
            this.type = type;
        }

        public void setDistinctIndex(int distinctIndex) {
            this.distinctIndex = distinctIndex;
        }
    }

    static class Cluster {

        NdimPoint data[], centroid, sum;
        int last = 0;

        public Cluster(NdimPoint centroid, int size) {
            this.data = new NdimPoint[size];
            this.sum = new NdimPoint(new double[centroid.dims.length]);
            this.centroid = centroid;
        }

        public void push(NdimPoint point) {
            data[last++] = point;
            sum = sum.sum(point);
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
                ret.dims[i] = this.dims[i] / div;
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
