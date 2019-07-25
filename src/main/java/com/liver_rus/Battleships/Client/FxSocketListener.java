package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.SocketFX.SocketListener;

class FxSocketListener implements SocketListener {

    @Override
    public void onMessage(String line) {
        if (line != null && !line.equals(Constant.NetworkMessage.EMPTY_STRING)) {
            resolveSocketAndProceedMassage(line);
        }
    }
}

    @Override
    public void onClosedStatus(boolean isClosed) {
        if (isClosed) {
            displayState(FXMLDocumentMainController.ConnectionDisplayState.DISCONNECTED);
        } else {
            network.setIsConnected(true);
            displayState(FXMLDocumentMainController.ConnectionDisplayState.CONNECTED);
        }
    }
}

