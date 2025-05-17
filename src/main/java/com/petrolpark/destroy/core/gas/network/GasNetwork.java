package com.petrolpark.destroy.core.gas.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleDirectedGraph;

import com.petrolpark.destroy.chemistry.api.util.Holder;
import com.petrolpark.destroy.core.gas.IGasVessel;
import com.petrolpark.destroy.core.gas.valve.IGasValve;
import com.petrolpark.destroy.core.gas.valve.OrValve;

public class GasNetwork {

    public static final int getMaxConnectedVessels() {
        return 16;
    };
    
    protected final SimpleDirectedGraph<GasNetwork.Section, Holder<IGasValve>> network = new SimpleDirectedGraph<Section, Holder<IGasValve>>((Class<? extends Holder<IGasValve>>) (new Holder<IGasValve>()).getClass());
    protected final Map<GasNetwork.Section, Map<GasNetwork.Section, GraphPath<GasNetwork.Section, IGasValve>>> paths = new HashMap<>();

    protected int connectedVesselsNo = 0;

    public void connect(GasNetwork.Section section1, GasNetwork.Section section2, IGasValve valve) {
        if (network.addEdge(section1, section2, Holder.hold(valve))) {
            
        } else {
            network.addEdge(section1, section2, Holder.hold(OrValve.or(network.removeEdge(section1, section2).get(), valve)));
            // No need to recalculate paths
        };
    };

    public void tickAllConnections() {

        DijkstraShortestPath.findPathBetween(network, null, null);
    };

    public class Section {

        public final Set<IGasVessel> connectedVessels = new HashSet<>();

        /**
         * Attempt to connect another {@link IGasVessel} to this Section of the {@link GasNetwork}.
         * @param vessel
         * @return {@code true} only if the new Vessel was added (for the first time)
         */
        public boolean connect(IGasVessel vessel) {
            if (connectedVessels.size() >= getMaxConnectedVessels()) return false;
            boolean added = connectedVessels.add(vessel);
            if (added) {
                connectedVesselsNo++;
                for (Map.Entry<GasNetwork.Section, Map<GasNetwork.Section, GraphPath<GasNetwork.Section, IGasValve>>> entry : paths.entrySet()) {
                   
                };
            };
            return added;
        };
    };
};
