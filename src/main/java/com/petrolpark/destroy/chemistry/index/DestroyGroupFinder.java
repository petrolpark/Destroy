package com.petrolpark.destroy.chemistry.index;

import java.util.List;
import java.util.Map;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Bond;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.GroupFinder;

public class DestroyGroupFinder extends GroupFinder {

    @Override
    public List<Group> findGroups(Map<Atom, Bond> structure) {
        //TODO Group Finder
        return null;
    };

    public static void register() {
        DestroyGroupFinder finder = new DestroyGroupFinder();
    };
    
};
