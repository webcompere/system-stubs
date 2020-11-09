package uk.org.webcompere.systemstubs;

class ThrowingRunnableMock implements ThrowingRunnable {
    boolean hasBeenEvaluated = false;

    @Override
    public void run() throws Exception {
        hasBeenEvaluated = true;
    }
}
