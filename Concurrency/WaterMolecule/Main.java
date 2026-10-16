package Concurrency.WaterMolecule;

public class Main {
    public static void main(String[] args) {
        H2OMachine machine = new H2OMachine();

        int hydrogenCount = 11;
        int oxygenCount = 5;

        for(int i=1; i<=oxygenCount; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    machine.oxygen(() -> System.out.println("Oxygen atom "+id+" supplied"));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        for(int i=1; i<=hydrogenCount; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    machine.hydrogen(() -> System.out.println("Hydrogen atom "+id+" supplied"));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }
}
