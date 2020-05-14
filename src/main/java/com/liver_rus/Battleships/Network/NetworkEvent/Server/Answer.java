package com.liver_rus.Battleships.Network.NetworkEvent.Server;

import com.liver_rus.Battleships.Network.NetworkEvent.Client.ClientNetworkEvent;
import com.liver_rus.Battleships.Network.Server.Player;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Answer implements Iterable<Pair<Player, ClientNetworkEvent>> {
    List<Pair<Player, ClientNetworkEvent>> answer;

    public Answer() {
        this.answer = new LinkedList<>();
    }

    public void add(Player player, ClientNetworkEvent event) {
        answer.add(new Pair<>(player, event));
    }

    @NotNull
    public Iterator<Pair<Player, ClientNetworkEvent>> iterator() {
        return answer.iterator();
    }

    public boolean isEmpty() {
        return answer.isEmpty();
    }
}