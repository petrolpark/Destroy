package com.petrolpark.destroy.chemistry.serializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Bond.BondType;

public class Node {
    private Atom atom;
    public Boolean visited;
    private List<Edge> edges;
    private Branch branch;
    private Map<Branch, BondType> sideBranches;

    public Node(Atom atom) {
        this.atom = atom;
        visited = false;
        edges = new ArrayList<>();
        sideBranches = new HashMap<>();
    };

    public String serialize() {
        String string = getAtom().getElement().getSymbol();
        Boolean isTerminal = true;
        Edge nextEdge = null;
        for (Edge edge : edges) {
            if (edge.getSourceNode() == this) {
                isTerminal = false;
                nextEdge = edge;
                break;
            };
        };
        if (atom.rGroupNumber != 0 && atom.getElement() == Element.R_GROUP) {
            string += atom.rGroupNumber;
        };
        if (!isTerminal) {
            string += nextEdge.bondType.getFROWNSCode(); // It thinks 'nextEdge' can be null
        };
        for (Entry<Branch, BondType> entry : getSideBranches().entrySet()) {
            string += "(" + entry.getValue().getFROWNSCode() + entry.getKey().serialize() + ")"; // It thinks "nextEdge" is null
        };
        if (!isTerminal) {
            string += nextEdge.getDestinationNode().serialize();
        };
        return string;
    };

    public Atom getAtom() {
        return this.atom;
    };

    public Node addEdge(Edge edge) {
        edges.add(edge);
        return this;
    };

    public Node deleteEdge(Edge edge) {
        edges.remove(edge);
        return this;
    };

    public List<Edge> getEdges() {
        return edges;
    };

    public Node setBranch(Branch branch) {
        this.branch = branch;
        return this;
    };

    public Branch getBranch() {
        return this.branch;
    };

    public Node addSideBranch(Branch branch, BondType bondType) {
        sideBranches.put(branch, bondType);
        return this;
    };

    public Map<Branch, BondType> getSideBranches() {
        return sideBranches;
    };

    public List<Entry<Branch, BondType>> getOrderedSideBranches() {
        List<Entry<Branch, BondType>> sideBranchesAndBondTypes = new ArrayList<>(getSideBranches().entrySet());
        Collections.sort(sideBranchesAndBondTypes, (entry1, entry2) -> entry1.getKey().getMassOfLongestChain().compareTo(entry2.getKey().getMassOfLongestChain()));
        return sideBranchesAndBondTypes;
    };
};
