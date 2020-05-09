package com.liver_rus.Battleships.Network.NetworkEvent.Client.Events;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.ClientNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;

//client do nothing
public class SetEnemyNameNetworkEvent implements ClientNetworkEvent {

    private final String enemy_name;

    public SetEnemyNameNetworkEvent(String enemy_name) {
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
