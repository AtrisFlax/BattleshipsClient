package com.liver_rus.Battleships.NetworkEvent;

import com.liver_rus.Battleships.Network.Server.Player;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Answer implements Iterable<Pair<Player, NetworkEventClient>> {
    List<Pair<Player, NetworkEventClient>> answer;

    public Answer() {
        this.answer = new LinkedList<>();
    }

    public void add(Player player, NetworkEventClient event) {
        answer.add(new Pair<>(player, event));
    }

    @NotNull
    public Iterator<Pair<Player, NetworkEventClient>> iterator() {
        return answer.iterator();
    }
}