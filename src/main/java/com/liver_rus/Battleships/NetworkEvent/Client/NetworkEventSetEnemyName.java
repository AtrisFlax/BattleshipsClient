package com.liver_rus.Battleships.NetworkEvent.Client;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventClient;

//client do nothing
public class NetworkEventSetEnemyName implements NetworkEventClient {

    String enemy_name;

    public NetworkEventSetEnemyName(String enemy_name) {
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
