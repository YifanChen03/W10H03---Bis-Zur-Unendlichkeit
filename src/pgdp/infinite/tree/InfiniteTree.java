package pgdp.infinite.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class InfiniteTree<T> {

    public final Function<T, Iterator<T>> children;

    public InfiniteTree(Function<T, Iterator<T>> children) {
        this.children = children;
    }

    /**
     * Erstellt einen Wurzelknoten mit dem gegebenen Wert und ohne Elternknoten (null) für diesen Baum.
     *
     * @param value Der Wert des Knotens.
     * @return Der neuen Wurzelknoten ohne weiter berechnete Kinder.
     */
    public InfiniteNode<T> withRoot(T value) {
        // TODO: Implementieren.
        return new InfiniteNode<>(this, value, null);
    }

    /**
     * Führt eine Tiefensuche durch, auf einem Baum welcher als Wurzelknoten den gegebenen Wert from hat.
     * Die Suche wird maximal bis zur Tiefe maxDepth durchgeführt.
     *
     * @param from        Der Wert des Wurzelknotens.
     * @param maxDepth    Die Anzahl der Ebenen unterhalb des Startwerts, die maximal durchsucht werden sollen.
     * @param optimizable Das Optimizable-Objekt, welches für die Suche verwendet werden soll.
     * @return Der optimale Wert.
     */
    public T find(T from, int maxDepth, Optimizable<T> optimizable) {
        // TODO: Implementieren.
        int currentDepth = 0;
        InfiniteNode<T> root = withRoot(from);
        T optimalValue = find_helper(root, maxDepth, currentDepth, optimizable);

        //return optimalValue
        return optimalValue;
    }

    public T find_helper(InfiniteNode<T> currentNode, int maxDepth, int currentDepth, Optimizable<T> optimizable) {
        //find value recursively
        InfiniteNode<T> currentParent = currentNode;
        T currentParentValue = currentParent.getValue();

        //stop if we found optimum
        //System.out.println(currentParentValue);
        if (optimizable.process(currentParentValue)) {
            return currentParentValue;
        }

        //stop if maxDepth has been reached so we don't want to check children of this node
        if (currentDepth >= maxDepth) {
            //System.out.println(currentParentValue);
            return currentParentValue;
        }

        //for every node, go through all nodes until there are no nodes left
        while (!currentParent.isFullyCalculated()) {
            currentParent.deleteChildren();
            currentNode = currentParent.calculateNextChild();
            int temporalDepth = currentDepth + 1;
            T currentValue = find_helper(currentNode, maxDepth, temporalDepth, optimizable);
            if (optimizable.process(currentValue)) {
                return currentValue;
            }
        }

        //return current optimum
        return optimizable.getOptimum();
    }

    public static void main(String[] args) {
        // Der Binärzahlbaum aus der Aufgabenstellung.
        InfiniteTree<Long> binaryValueTree = new InfiniteTree<>(i -> i == null ?
                List.of(0L, 1L).iterator() : List.of(i * 2, i * 2 + 1).iterator());

        // Ein Baum, wo jeder Knoten als Kinder die Werte hat, die seine Vorgänger in der Collatz-Folge hätten sein
        // können.
        InfiniteTree<Long> collatzTree = new InfiniteTree<>(i -> {
            boolean hasUnevenPredecessor = (i - 1) % 3 == 0 && (i - 1) / 3 % 2 != 0;
            return (hasUnevenPredecessor ? List.of(i * 2, (i - 1) / 3) : List.of(i * 2)).iterator();
        });

        // Ein Baum mit schweren Objekten
        InfiniteTree<String[]> heavyTree = new InfiniteTree<>(S ->
                List.of(new String[S.length * 2], new String[S.length * 3]).iterator());


        // Ein Optimizable welches den Wert findet welcher am nächsten an 1234 ist.
        Supplier<Optimizable<Long>> optimizableFind = () -> new Optimizable<>() {
            private long optimum = Long.MAX_VALUE;
            private long optimumDistance = Long.MAX_VALUE;

            @Override
            public boolean process(Long value) {
                long searchValue = 1234L;
                long distance = Math.abs(value - searchValue);
                if (distance < optimumDistance) {
                    optimum = value;
                    optimumDistance = distance;
                }
                return distance == 0;
            }

            @Override
            public Long getOptimum() {
                return optimum;
            }
        };

        // Ein Optimizable welches den größten ungeraden Wert findet.
        Supplier<Optimizable<Long>> optimizableBiggest = () -> new Optimizable<>() {
            private long optimum = Long.MIN_VALUE;

            @Override
            public boolean process(Long value) {
                if (value % 2 == 1 && value > optimum) {
                    optimum = value;
                }
                return false;
            }

            @Override
            public Long getOptimum() {
                return optimum;
            }
        };

        // Ein Optimizable welches das längeste Array findet.
        Supplier<Optimizable<String[]>> optimizableLongest = () -> new Optimizable<>() {
            private String[] optimum = new String[1000000];

            @Override
            public boolean process(String[] value) {
                if (value.length > optimum.length) {
                    optimum = value;
                }
                return false;
            }

            @Override
            public String[] getOptimum() {
                return optimum;
            }
        };

        System.out.println(collatzTree.find(1L, 30, optimizableFind.get()));
        long l = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1_000_000;
        System.out.println("Used: " + l + "MB of RAM");
        // sollte 1236 ausgeben.

        System.out.println(collatzTree.find(1L, 30, optimizableBiggest.get()));
        // sollte 89478485 ausgeben.

        System.out.println(binaryValueTree.find(0L, 10, optimizableFind.get()));
        // sollte 1023 ausgeben, da 1234 nicht in den ersten 10 Ebenen des Baumes vorkommt (2^10 - 1 = 1023) und 1023
        // am nächsten dran ist.

        System.out.println(binaryValueTree.find(0L, 12, optimizableFind.get()));
        // sollte 1234 ausgeben.

        //System.out.println(binaryValueTree.find(1L, 3, optimizableFind.get()));

        //System.out.println(heavyTree.find(new String[100000], 5, optimizableLongest.get()).length);
    }
}
