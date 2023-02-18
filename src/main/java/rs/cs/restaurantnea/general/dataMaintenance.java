package rs.cs.restaurantnea.general;

public class dataMaintenance { // I was planning on adding a mergesort algorithm in this class but decided against it
    public static boolean binarySearch(String[][] arr, int val, int mid) { // This recursive algorithm calls itself to find a value in an ordered 2d array
        boolean found = false; // If not changed, then the value has not been found, and it will return false
        mid /= 2; // Finds the midpoint of the array or subarray to be searched
        if (Integer.parseInt(arr[mid][0]) == val) { // If the midpoint = the value we are searching for, it returns true
            found = true;
        } else if (mid >= arr.length - 1) { // If the search goes on for too long, it goes out of bounds - this stops it from going too high
            return found;
        } else if (Integer.parseInt(arr[mid][0]) < val) { // If the value is higher than the midpoint, the midpoint increases by 1.25 to become the 75th percentile of the array
            found = binarySearch(arr, val, (mid * 2) + mid);
        }
        else if (mid == 0) { // If the search goes on for too long, it goes out of bounds - this stops it from going too low
            return found;
        }
        else {
            found = binarySearch(arr, val, mid); // If the array is lower than the midpoint, then the midpoint will eventually get halved to become the 25th percentile of the array
        }
        return found;
    }
}
