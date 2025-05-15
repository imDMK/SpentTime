package com.github.imdmk.spenttime.configuration;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CustomRepresenter extends Representer {

    public CustomRepresenter(DumperOptions options) {
        super(options);
        this.representers.put(String.class, new RepresentString());
        this.representers.put(Boolean.class, new RepresentBoolean());
        this.representers.put(Integer.class, new RepresentInteger());
        this.representers.put(ArrayList.class, new RepresentList());
        this.representers.put(Map.class, new RepresentMap());
        this.representers.put(HashMap.class, new RepresentMap());
        this.representers.put(LinkedHashMap.class, new RepresentMap());
    }

    private class RepresentString implements Represent {
        @Override
        public Node representData(Object data) {
            if (data instanceof String string) {
                if (string.isEmpty() || string.contains(" ")) {
                    return representScalar(Tag.STR, string, DumperOptions.ScalarStyle.DOUBLE_QUOTED);
                }
            }

            return representScalar(Tag.STR, data.toString(), DumperOptions.ScalarStyle.PLAIN);
        }
    }

    private class RepresentBoolean implements Represent {
        @Override
        public Node representData(Object data) {
            return representScalar(Tag.BOOL, data.toString(), DumperOptions.ScalarStyle.PLAIN);
        }
    }

    private class RepresentInteger implements Represent {
        @Override
        public Node representData(Object data) {
            return representScalar(Tag.INT, data.toString(), DumperOptions.ScalarStyle.PLAIN);
        }
    }

    private class RepresentList implements Represent {
        @Override
        public Node representData(Object data) {
            return representSequence(getTag(data.getClass(), Tag.SEQ), (Iterable<?>) data, DumperOptions.FlowStyle.BLOCK);
        }
    }

    private class RepresentMap implements Represent {
        public Node representData(Object data) {
            return representMapping(Tag.MAP, (Map<?, ?>) data, DumperOptions.FlowStyle.BLOCK);
        }
    }
}
