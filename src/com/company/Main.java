package com.company;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        int arr[] = {2, 4, 10, 12, 3, 20, 30, 11, 25};    // initial data
        int n = 0;
        int noOCorrectCentroid;
        int noclusters = 2;

        Cluster clusters[] = new Cluster[noclusters];
        int newCentroids[] = new int[noclusters];
        System.arraycopy(arr, 0, newCentroids, 0, noclusters);

        do {
            n++;

            for (int i = 0; i < noclusters ; i++) {
                clusters[i] = new Cluster(newCentroids[i],arr.length);
            }

            for (int i = 0; i < arr.length; i++) {

                int nearestCluster = -1, nearestClusterDist = Integer.MAX_VALUE;
                for (int j = 0; j < clusters.length; j++) {
                    int tempDist = clusters[j].calcDistance(arr[i]);
                    if(tempDist<= nearestClusterDist){
                        nearestCluster = j;
                        nearestClusterDist = tempDist;
                    }
                }

                clusters[nearestCluster].push(arr[i]);
            }

            noOCorrectCentroid = 0;
            for (int j = 0; j < clusters.length ; j++) {
                newCentroids[j] = clusters[j].calcNewCentroid();
                if(newCentroids[j] == clusters[j].centroid){
                    noOCorrectCentroid++;
                }
            }

            System.out.println("Loop " + n );

            for (int i = 0; i < clusters.length; i++) {
                System.out.println("Centroid:\t" + clusters[i].centroid + "\t => \t" + Arrays.toString(clusters[i].data).replace(", ","\t"));
            }

            System.out.println();

        } while (noOCorrectCentroid<newCentroids.length);

    }



    static class Cluster {
        int data[], last, centroid;
        float sum;

        public Cluster(int centroid,int size) {
            this.data = new int[size];
            this.sum = 0;
            this.centroid = centroid;
        }

        public void push(int point){
            data[last++] = point;
            sum+=point;
        }

        public int calcNewCentroid(){
            return Math.round(sum / last);
        }

        public int calcDistance(int point){
            return Math.abs(point - centroid);
        }
    }


    static class NdimPoint{

    }
}
