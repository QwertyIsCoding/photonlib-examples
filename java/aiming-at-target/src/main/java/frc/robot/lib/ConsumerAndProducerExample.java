package org.aa8426.lib;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConsumerAndProducerExample {
    public class Thing {

    }
    public Thing giveThing() {
        System.out.println("called provider");
        return null;        
    }

    public void takeThing(Thing mytest) {
        System.out.println("called consumer");
    }

    public void testSupplier(Supplier<Thing> supplierFunc) {
        supplierFunc.get();
    }

    public void testConsumer(Consumer<Thing> consumerFunc) {
        consumerFunc.accept(null);
        return;
    }

    public static void main(String[] args) {
        ConsumerAndProducerExample cape = new ConsumerAndProducerExample();
        cape.testSupplier(cape::giveThing);
        cape.testConsumer(cape::takeThing);
        cape.testSupplier(() -> {
            System.out.println("Anonymous function supplier demo");
            return null;
        });
        cape.testConsumer((Thing t) -> {
            System.out.println("Anonymous function consumer demo");
        });
    }
}
