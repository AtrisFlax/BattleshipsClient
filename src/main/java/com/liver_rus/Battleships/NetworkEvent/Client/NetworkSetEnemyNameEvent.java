package com.liver_rus.Battleships.NetworkEvent.Client;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.NetworkEvent.NetworkClientEvent;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;

//client do nothing
public class NetworkSetEnemyNameEvent implements NetworkClientEvent {

    String enemy_name;

    public NetworkSetEnemyNameEvent(String enemy_name) {
        this.enemy_name = enemy_name;
    }

    @Override
    public String proceed(GUIActions action) {
        action.setEnemyName(enemy_name);
        return null;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.SET_ENEMY_NAME + enemy_name;
    }
}
