package img.mutex;

public class Bakery implements Lock {

    int N;
    volatile boolean choosing[];
    volatile int number[];

    public Bakery (int numProc) {
	N = numProc;
	choosing = new boolean[N];
	number = new int[N];

	for(int j=0; j<N; j++) {
	    choosing[j] = false;
	    number[j] = 0;
	}
    }

    @Override
    public void requestCR(int i) {
	//step 1: doorway: choose a number
	choosing[i] = true;

	for(int j = 0; j < N; j++) {
	    if(number[j] > number[i]) {
		number[i] = number[j];
	    }
	}

	number[i]++;
	choosing[i] = false;

	//step 2:  check if my number is the smallest
	for(int j = 0; j < N; j++) {
	    while(choosing[j]);
	    while((number[j] != 0) && 
		    (number[j] < number[i]) || 
		    (number[j] == number[i]) && 
		    j < i);
	    //busy wait
	}
    }

    @Override
    public void releaseCR(int i) {
	number[i] = 0;
    }
}