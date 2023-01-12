package pgdp.infinite.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InfiniteNode<T> {

    private final InfiniteTree<T> tree;
    private final InfiniteNode<T> parent;
    private final T value;
    private Iterator<T> infNodeIterator;
    private List<InfiniteNode<T>> children;

    public InfiniteNode(InfiniteTree<T> tree, T value, InfiniteNode<T> parent) {
        this.parent = parent;
        this.tree = tree;
        this.value = value;
        infNodeIterator = tree.children.apply(value);
        children = new ArrayList<>();
    }

    public T getValue() {
        return value;
    }

    public InfiniteNode<T> getParent() {
        return parent;
    }

    /**
     * @return Gibt alle bisher berechneten Kinder des Knotens zurück.
     */
    public List<InfiniteNode<T>> getChildren() {
        // TODO: Implementieren.
        return children;
    }

    /**
     * Berechnet das nächste Kind des Knotens und gibt es zurück.
     * @return das neue Kind oder null, wenn es keine weiteren Kinder gibt.
     */
    public InfiniteNode<T> calculateNextChild() {
        // TODO: Implementieren.
        if (infNodeIterator.hasNext()) {
            InfiniteNode temp = new InfiniteNode<>(tree, infNodeIterator.next(), this);
            children.add(temp);
            return temp;
        }
        return null;
    }

    /**
     * Berechnet alle Kinder des Knotens.
     */
    public void calculateAllChildren() {
        // TODO: Implementieren.
        infNodeIterator.forEachRemaining(valueT -> children.add(new InfiniteNode<>(tree, valueT, this)));
    }

    /**
     * @return true, wenn alle Kinder berechnet wurden, false sonst.
     */
    public boolean isFullyCalculated() {
        // TODO: Implementieren.
        return !infNodeIterator.hasNext();
    }

    /**
     * Setzt die gesamte Berechnung des Knotens zurück.
     */
    public void resetChildren() {
        // TODO: Implementieren.
        infNodeIterator = tree.children.apply(value);
        children.clear();
    }

    public void deleteChildren() {
        children.clear();
    }

    /*public String toString() {
        return value.toString();
    }*/
}
