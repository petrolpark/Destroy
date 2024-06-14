package com.petrolpark.destroy.chemistry.serializer;

import com.petrolpark.destroy.chemistry.Bond.BondType;

public class Edge {
    public BondType bondType;  
    private Node srcNode;
    private Node destNode;
    public boolean marked = false;

    public Edge(Node srcNode, Node destNode, BondType bondType) {
        this.srcNode = srcNode;
        this.destNode = destNode;
        this.bondType = bondType;
    };

    public Node getSourceNode() {
        return this.srcNode;
    };

    public Node getDestinationNode() {
        return this.destNode;
    };

    public void flip() {
        Node temp = srcNode;
        srcNode = destNode;
        destNode = temp;
    };
 };
