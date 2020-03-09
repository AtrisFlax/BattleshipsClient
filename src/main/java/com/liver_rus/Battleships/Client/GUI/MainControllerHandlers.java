//package com.liver_rus.Battleships.NetworkClient.GUI;
//
//import com.liver_rus.Battleships.NetworkClient.Constants.Constants;
//import com.liver_rus.Battleships.NetworkClient.Constants.SecondPlayerGUIConstants;
//import com.liver_rus.Battleships.NetworkClient.GameEngine.ClientGameEngine;
//import com.liver_rus.Battleships.NetworkClient.GamePrimitive.FieldCoord;
//import com.liver_rus.Battleships.NetworkClient.GamePrimitive.Ship;
//import javafx.scene.input.MouseButton;
//import javafx.scene.input.MouseEvent;
//
//public class MainControllerHandlers {
//    public static void handleToMouseClick(MouseEvent event, ClientGameEngine gameEngine, ) {
//        //меняем расположения корабля с горизонтального на вертикальное (или наоборот) на среднюю клавишу мыши
//        if (event.getButton().equals(MouseButton.MIDDLE)) {
//            if (gameEngine.getShipType() != Ship.Type.SUBMARINE) {
//                gameEngine.getCurrentGUIState().changeShipOrientation();
//                //перерисовываем после смены
//                overlayCanvas.clearRect(0, 0, Constants.Window.WIDTH, Constants.Window.HEIGHT);
//                if (gameEngine.getGameField().isNotIntersectionShipWithBorder(gameEngine.getCurrentGUIState()) &&
//                        gameEngine.getGameField().isPossibleLocateShip(gameEngine.getCurrentGUIState())) {
//                    Draw.ShipOnMyField(overlayCanvas, gameEngine.getCurrentGUIState());
//                }
//            }
//        }
//
//        if (event.getButton().equals(MouseButton.PRIMARY)) {
//            //Размещение корабля, после выбора
//            if (gameEngine.getGamePhase() == ClientGameEngine.phase.DEPLOYING_FLEET) {
//                if (SceneCoord.isFromFirstPlayerField(event.getSceneX(), event.getSceneY()) &&
//                        gameEngine.isShipSelected() &&
//                        gameEngine.getGameField().getFleet().getShipsLeft() >= 0) {
//                    resetFleetButton.setDisable(false);
//                    if (gameEngine.getGameField().isPossibleLocateShip(gameEngine.getCurrentGUIState()) &&
//                            gameEngine.getGameField().getFleet().getShipsLeft() >= 0) {
//                        gameEngine.addShipOnField(Ship.createShip(gameEngine.getCurrentGUIState()));
//                        Draw.ShipOnMyField(mainCanvas, gameEngine.getCurrentGUIState());
//                        gameEngine.setShipSelected(false);
//                    }
//                }
//                //Флот расставлен. Объявление о готовности игрока.. ################handleToMouseClick###################
//                if (!gameEngine.isShipSelected() && gameEngine.getGameField().getFleet().getShipsLeft() == 0) {
//                    gameEngine.setGamePhase(ClientGameEngine.phase.FLEET_IS_DEPLOYED);
//                    network.sendMessage(Constants.NetworkCommand.SEND_SHIPS + gameEngine.getShipsInfoForSend());
//                    resetFleetButton.setDisable(true);
//                    labelGameStatus.setText("Fleet is deployed. Waiting for second player...");
//                }
//            }
//            //выстрел в поле противника
//            if (gameEngine.getGamePhase() == ClientGameEngine.phase.MAKE_SHOT) {
//                if (SceneCoord.isFromSecondPlayerField(event.getSceneX(), event.getSceneY())) {
//                    FieldCoord shootFieldCoord = new FieldCoord(event.getSceneX(), event.getSceneY(), SecondPlayerGUIConstants.getGUIConstant());
//                    Draw.MissCellOnEnemyField(mainCanvas, shootFieldCoord);
//                    network.sendMessage(Constants.NetworkCommand.SHOT + shootFieldCoord);
//                    gameEngine.setGamePhase(ClientGameEngine.phase.WAITING_ANSWER);
//                    gameEngine.setShootCoord(shootFieldCoord);
//                }
//            }
//        }
//    }
//}
