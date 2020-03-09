package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Client.GameEngine.ClientGameEngine;
import com.liver_rus.Battleships.Client.GamePrimitives.GameField;
import com.liver_rus.Battleships.Client.GamePrimitives.Ship;
import com.liver_rus.Battleships.Network.Client.NetworkClient;
import com.liver_rus.Battleships.Network.Server.GameServerThread;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;



class NetworkTest {
    private static final Logger log = Logger.getLogger(String.valueOf(NetworkTest.class));

    final static int TIMEOUT_FOR_SINGLE_MESSAGE = 500;
    static final int MAX_CONNECTIONS = 2;

    private int port = 10071;
    private String host = "127.0.0.1";

    private static GameField[] gameFields;
    private static NetworkClient client1;
    private static NetworkClient client2;
    private static GameServerThread server;

    List<String> inboxClient1 = new ArrayList<>();
    List<String> inboxClient2 = new ArrayList<>();

    @BeforeEach
    void setUpClientAndServer() throws IOException {
        gameFields = new GameField[2];
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            gameFields[i] = new GameField();
        }
        server = new GameServerThread(host, port, gameFields);
        server.start();
        client1 = new NetworkClient(host, port);
        client2 = new NetworkClient(host, port);
        client1.subscribeForInbox((message) -> inboxClient1.add(message));
        client2.subscribeForInbox((message) -> inboxClient2.add(message));
    }

    @AfterEach
    void closeConnections() throws IOException {
        log.info("Close all connection");
        client1.close();
        client2.close();
        server.close();
        inboxClient1.clear();
        inboxClient2.clear();
    }

    @Test
    void testGameFieldChangingOnServerFromClientMessages() throws InterruptedException {
        server.setTurnOrder(GameServerThread.TurnOrder.FIRST_CONNECTED);
        ClientGameEngine gameEngine0 = new ClientGameEngine();
        ClientGameEngine gameEngine1 = new ClientGameEngine();
        int x, y;
        addShipsPreset1(gameEngine0);
        addShipsPreset2(gameEngine1);
        send(client1, gameEngine0.getShipsInfoForSend());
        send(client2, gameEngine1.getShipsInfoForSend());
        Thread.sleep(250);
        //after SEND_SHIPS message fields on servers has swapped
        swapFields(gameFields);
        //check after init
        assertEquals(gameEngine0.getGameField(), gameFields[0]);
        assertEquals(gameEngine1.getGameField(), gameFields[1]);

        //networkClient1 make hit
        x = 9;
        y = 1;
        send(client1, "SHOT" + x + y);
        Thread.sleep(250);
        gameEngine0.getGameField().shoot(x, y);
        //check after hit
        assertEquals(gameEngine0.getGameField(), gameFields[0]);
        assertEquals(gameEngine1.getGameField(), gameFields[1]);

        //networkClient1 make miss
        x = 8;
        y = 1;
        send(client1, "SHOT" + x + y);
        Thread.sleep(250);
        gameEngine0.getGameField().shoot(x, y);
        //check after hit
        assertEquals(gameEngine0.getGameField(), gameFields[0]);
        assertEquals(gameEngine1.getGameField(), gameFields[1]);

        //networkClient2 destroy ship
        x = 7;
        y = 8;
        send(client2, "SHOT" + x + y);
        Thread.sleep(250);
        gameEngine1.getGameField().shoot(x, y);
        //check after destroy
        assertEquals(gameEngine0.getGameField(), gameFields[0]);
        assertEquals(gameEngine1.getGameField(), gameFields[1]);
    }

    private void addShipsPreset1(ClientGameEngine gameEngine) {
        gameEngine.addShipOnField(Ship.create(1, 8, Ship.Type.SUBMARINE, true));
        gameEngine.addShipOnField(Ship.create(3, 2, Ship.Type.SUBMARINE, true));
        gameEngine.addShipOnField(Ship.create(1, 1, Ship.Type.DESTROYER, false));
        gameEngine.addShipOnField(Ship.create(3, 4, Ship.Type.DESTROYER, true));
        gameEngine.addShipOnField(Ship.create(2, 6, Ship.Type.CRUISER, true));
        gameEngine.addShipOnField(Ship.create(7, 4, Ship.Type.BATTLESHIP, false));
        gameEngine.addShipOnField(Ship.create(9, 1, Ship.Type.AIRCRAFT_CARRIER, false));
    }

    private void addShipsPreset2(ClientGameEngine gameEngine) {
        gameEngine.addShipOnField(Ship.create(6, 2, Ship.Type.BATTLESHIP, false));
        gameEngine.addShipOnField(Ship.create(2, 3, Ship.Type.CRUISER, false));
        gameEngine.addShipOnField(Ship.create(1, 0, Ship.Type.AIRCRAFT_CARRIER, true));
        gameEngine.addShipOnField(Ship.create(4, 7, Ship.Type.DESTROYER, true));
        gameEngine.addShipOnField(Ship.create(8, 4, Ship.Type.DESTROYER, false));
        gameEngine.addShipOnField(Ship.create(1, 8, Ship.Type.SUBMARINE, false));
        gameEngine.addShipOnField(Ship.create(7, 8, Ship.Type.SUBMARINE, false));
    }

    //после отправки SEND_SHIPS,SHOT, MISS
    /*
    +test game server
    приходит сообщение
    проверяем game field как поменялся
    написать метод служебный для полученя gamefield
     */

    /*
    передают gameSever gamefield
    и смотрим как gameserver себя ведет. из теста имеет доступ gameField из теста
     */


    //тестирование заранее прописаного цикла игры с проверкой инбоксов клиентов
    // ** файлы содержат лишний заголовок для проверки содержимого inbox для случая генирации обоих ходов
    //В sendToServer.txt прописаны сообщения отправляемы от клиента к серверу
    //awaitedInboxClient1.txt и awaitedInboxClient2.txt описаны ожидаемые значения отправленные от сервера к клиентам
    //В тесте просиходит
    //1. Зачитывание файлов для отправки
    //1.1 Извлечение тагов(имен) клиентов ("client1", "client2")
    //2. Отправка SEND_SHIPS сообщений
    //3. Проверка по размеру инбокса какого клиента ход первый
    //4. Отправка сообщений с клиентов на сервер
    //4.1 В случае если ход первого клиента необходимо пропустить одно сообщение выстрел (SHOT) (Тестовая последовательность
    //написана с дополнительным выстрелом в случае если превый ход второго клиента, чтобы выравнять очередность
    //5. Проверка инбоксов.
    //5.1 Пропуск лишних строк их файла инбокса с учетом очередности хода. См 4.1

    @Test
    void gameCycleInboxTest() throws InterruptedException {
        server.setTurnOrder(GameServerThread.TurnOrder.RANDOM_TURN);
        //Load send info
        Stream<String> sendInfoStream = getStringStreamFromFile("Case1/sendToServer.txt");
        Stream<String> client1ExpectedInboxStream = getStringStreamFromFile("Case1/awaitedInboxClient1.txt");
        Stream<String> client2ExpectedInboxStream = getStringStreamFromFile("Case1/awaitedInboxClient2.txt");
        //Extract client tag
        //SEND SHIP_INFO
        String[] sendInfo = sendInfoStream.toArray(String[]::new);
        final int CLIENT_NUM = 2;
        final String FIRST_CLIENT_TAG = sendInfo[0].split("\\s")[0];
        final String SECOND_CLIENT_TAG = sendInfo[0].split("\\s")[1];
        for (int i = 1; i < CLIENT_NUM + 1; i++) {
            splitAndSend(sendInfo[i], FIRST_CLIENT_TAG, SECOND_CLIENT_TAG);
            Thread.sleep(3000);
        }
        final int skip_msg, skip_turns;
        boolean client1FirstTurn = isClient1FirstTurn();
        if (client1FirstTurn) {
            skip_msg = 1;
            skip_turns = 2;
        } else {
            skip_msg = 0;
            skip_turns = 0;
        }
        //SEND SHOTS
        for (int i = CLIENT_NUM + skip_msg + 1; i < sendInfo.length; i++)
            splitAndSend(sendInfo[i], FIRST_CLIENT_TAG, SECOND_CLIENT_TAG);
        //CHECK INBOX
        final Object[] awaitedInboxClient1 = client1ExpectedInboxStream.skip(skip_turns).toArray();
        final Object[] awaitedInboxClient2 = client2ExpectedInboxStream.skip(skip_turns).toArray();

        assertTrue(Arrays.deepEquals(awaitedInboxClient1, inboxClient1.toArray()));
        assertTrue(Arrays.deepEquals(awaitedInboxClient2, inboxClient2.toArray()));
    }

    private boolean isClient1FirstTurn() {
        int inbox1Size = inboxClient1.size();
        final int expectedSizeAfterSendShips = 1;
        assertEquals(expectedSizeAfterSendShips, inbox1Size);
        return inboxClient1.get(inbox1Size - 1).equals(Constants.NetworkCommand.YOU_TURN);
    }

    private void splitAndSend(String str, String first_client_tag, String second_client_tag) {
        String[] splitStr = str.split("\\s");
        if (splitStr[0].equals(first_client_tag)) {
            send(client1, splitStr[1]);
        }
        if (splitStr[0].equals(second_client_tag)) {
            send(client2, splitStr[1]);
        }
    }

    private Stream<String> getStringStreamFromFile(String fileName) {
        String testResourceFolder = "TestCases/";
        InputStreamReader inputStreamReader = new InputStreamReader(ClassLoader.getSystemResourceAsStream(testResourceFolder + fileName));
        return new BufferedReader(inputStreamReader).lines();
    }

    private void swapFields(GameField[] fields) {
        GameField temp = fields[0];
        fields[0] = fields[1];
        fields[1] = temp;
    }

    private void send(NetworkClient networkClient, String msg) {
        CountDownLatch sendMessageLatch = new CountDownLatch(1);
        new Thread(() -> {
            assertTimeout(Duration.ofMillis(TIMEOUT_FOR_SINGLE_MESSAGE), () -> networkClient.sendMessage(msg));
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendMessageLatch.countDown();
        }).start();
        try {
            sendMessageLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
