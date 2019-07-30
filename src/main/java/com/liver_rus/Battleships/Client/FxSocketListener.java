package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.SocketFX.SocketListener;
import javafx.collections.ObservableList;

class FxSocketListener implements SocketListener {

    ObservableList<String> rcvdMsgsData;

    void setDataReceiver(ObservableList<String> rcvdMsgsData){
        this.rcvdMsgsData = rcvdMsgsData;
    }

    @Override
    public void onMessage(String line) {
        if (line != null && !line.equals(Constant.NetworkMessage.EMPTY_STRING)) {
            //resolveSocketAndProceedMassage(line);
            rcvdMsgsData.add(line);
        }
    }


    @Override
    public void onClosedStatus(boolean isClosed) {
//        if (isClosed) {
//            displayState(FXMLDocumentMainController.ConnectionDisplayState.DISCONNECTED);
//        } else {
//            network.setIsConnected(true);
//            displayState(FXMLDocumentMainController.ConnectionDisplayState.CONNECTED);
//        }
    }
}

