package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Client.GameEngine.ClientGameEngine;
import com.liver_rus.Battleships.Client.GamePrimitives.FieldCoord;
import com.liver_rus.Battleships.Client.GamePrimitives.GameField;
import com.liver_rus.Battleships.Client.GamePrimitives.Ship;
import com.liver_rus.Battleships.Network.Client;
import com.liver_rus.Battleships.Network.GameServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class NetworkTest {
    private static final Logger log = Logger.getLogger(String.valueOf(NetworkTest.class));

    final static int TIMEOUT_FOR_SINGLE_MESSAGE = 500;
    static final int MAX_CONNECTIONS = 2;

    private static GameField[] gameFields;
    private static Client client1;
    private static Client client2;
    private static GameServer gameServer;

    @BeforeAll
    static void setUpClientAndServer() throws IOException {
        int port = 10071;
        String host = "127.0.0.1";
        gameFields = new GameField[2];
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            gameFields[i] = new GameField();
        }
        gameServer = new GameServer(host, port, gameFields);
        Thread serverThread = new Thread(gameServer);
        serverThread.start();
        try {
            client1 = new Client(host, port);
            client2 = new Client(host, port);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGameFieldChangingOnServerFromClientMessages() throws InterruptedException, IOException {
        gameServer.setTurnOrder(GameServer.TurnOrder.FIRST_CONNECTED);
        //client fleet - gameEngine.getGameField()
        //server fleet - gameFields
        ClientGameEngine gameEngine0 = new ClientGameEngine();
        ClientGameEngine gameEngine1 = new ClientGameEngine();
        FieldCoord shootCoord;
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

        //client1 make hit
        shootCoord = new FieldCoord(9, 1);
        send(client1, "SHOT" + shootCoord);
        Thread.sleep(250);
        gameEngine0.getGameField().shoot(shootCoord);
        //check after hit
        assertEquals(gameEngine0.getGameField(), gameFields[0]);
        System.out.println(gameEngine0.getGameField());
        System.out.println(gameFields[0]);
        assertEquals(gameEngine1.getGameField(), gameFields[1]);

        //client1 make miss
        shootCoord = new FieldCoord(8, 1);
        send(client1, "SHOT" + shootCoord);
        Thread.sleep(250);
        gameEngine0.getGameField().shoot(shootCoord);
        //check after hit
        assertEquals(gameEngine0.getGameField(), gameFields[0]);
        assertEquals(gameEngine1.getGameField(), gameFields[1]);

        //client2 destroy ship
        shootCoord = new FieldCoord(7, 8);
        send(client2, "SHOT" + shootCoord);
        Thread.sleep(250);
        gameEngine1.getGameField().shoot(shootCoord);
        //check after destroy
        assertEquals(gameEngine0.getGameField(), gameFields[0]);
        assertEquals(gameEngine1.getGameField(), gameFields[1]);
    }

    private void addShipsPreset1(ClientGameEngine gameEngine) {
        gameEngine.addShipOnField(Ship.create(new FieldCoord(1, 8), Ship.Type.SUBMARINE, true));
        gameEngine.addShipOnField(Ship.create(new FieldCoord(3, 2), Ship.Type.SUBMARINE, true));
        gameEngine.addShipOnField(Ship.create(new FieldCoord(1, 1), Ship.Type.DESTROYER, false));
        gameEngine.addShipOnField(Ship.create(new FieldCoord(3, 4), Ship.Type.DESTROYER, true));
        gameEngine.addShipOnField(Ship.create(new FieldCoord(2, 6), Ship.Type.CRUISER, true));
        gameEngine.addShipOnField(Ship.create(new FieldCoord(7, 4), Ship.Type.BATTLESHIP, false));
        gameEngine.addShipOnField(Ship.create(new FieldCoord(9, 1), Ship.Type.AIRCRAFT_CARRIER, false));
    }

    private void addShipsPreset2(ClientGameEngine gameEngine) {
        gameEngine.addShipOnField(Ship.create(new FieldCoord(6, 2), Ship.Type.BATTLESHIP, false));
        gameEngine.addShipOnField(Ship.create(new FieldCoord(2, 3), Ship.Type.CRUISER, false));
        gameEngine.addShipOnField(Ship.create(new FieldCoord(1, 0), Ship.Type.AIRCRAFT_CARRIER, true));
        gameEngine.addShipOnField(Ship.create(new FieldCoord(4, 7), Ship.Type.DESTROYER, true));
        gameEngine.addShipOnField(Ship.create(new FieldCoord(8, 4), Ship.Type.DESTROYER, false));
        gameEngine.addShipOnField(Ship.create(new FieldCoord(1, 8), Ship.Type.SUBMARINE, false));
        gameEngine.addShipOnField(Ship.create(new FieldCoord(7, 8), Ship.Type.SUBMARINE, false));
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
    //2. Отправка SEND_SHIPS сообщений
    //3. Проверка по размеру инбокса какого клиента ход первый
    //4. Отправка сообщений с клиентов на сервер
    //4.1 В случае если ход первого клиента необходимо пропустить одно сообщение выстрел (SHOT) (Тестовая последовательность
    //написана с дополнительным выстрелом в случае если превый ход второго клиента, чтобы выравнять очередность
    //5. Проверка инбоксов.
    //5.1 Пропуск лишних строк их файла инбокса с учетом очередности хода. См 4.1

    @Test
    void gameCycleInboxTest() throws InterruptedException {
        gameServer.setTurnOrder(GameServer.TurnOrder.RANDOM_TURN);
        Stream<String> sendInfoStream = getStringStreamFromFile("Case1/sendToServer.txt");
        Stream<String> client1ExpectedInboxStream = getStringStreamFromFile("Case1/awaitedInboxClient1.txt");
        Stream<String> client2ExpectedInboxStream = getStringStreamFromFile("Case1/awaitedInboxClient2.txt");
        //SEND SHIP INFO
        final int SHIPS_INFO_LIMIT = 2;
        String[] sendInfo = sendInfoStream.toArray(String[]::new);
        //SEND SHIP INFO
        for (int i = 0; i < SHIPS_INFO_LIMIT; i++)
            splitAndSend(sendInfo[i]);
        Thread.sleep(3000);
        System.out.println("Check inbox first turn");
        //SEND SHOTS
        boolean client1FirstTurn = isClient1FirstTurn();
        if (client1FirstTurn) {
            //skip fake shot (MISS_SHOT) for client2 first turn
            for (int i = SHIPS_INFO_LIMIT + 1; i < sendInfo.length; i++)
                splitAndSend(sendInfo[i]);
            System.out.println();
        } else {
            for (int i = SHIPS_INFO_LIMIT; i < sendInfo.length; i++)
                splitAndSend(sendInfo[i]);
        }
        System.out.println("Check inbox other turn");
        //TODO непонятен скип прописать где чего
        //CHECK INBOX
        final int SKIP_TURNS = 2;
        if (client1FirstTurn) {
            //TODO тут естьString[]::new
            assertTrue(Arrays.deepEquals(client1ExpectedInboxStream.skip(SKIP_TURNS).toArray(), client1.getInbox().toArray()));
            assertTrue(Arrays.deepEquals(client2ExpectedInboxStream.skip(SKIP_TURNS).toArray(), client2.getInbox().toArray()));
        } else {
            //TODO тут нет toArray()
            assertTrue(Arrays.deepEquals(client1ExpectedInboxStream.toArray(), client1.getInbox().toArray()));
            assertTrue(Arrays.deepEquals(client2ExpectedInboxStream.toArray(), client2.getInbox().toArray()));
        }
    }


    @AfterEach
    void reset() {
        log.info("Reset server state");
        gameServer.reset();
        client1.clearInbox();
        client2.clearInbox();
    }

    private boolean isClient1FirstTurn() {
        return client1.getInbox().get(client1.getInbox().size() - 1).equals(Constants.NetworkMessage.YOU_TURN);
    }

    private void splitAndSend(String str) {
        //TODO \\s+ можно убрать. заменить на только пробельные символы
        String[] splitStr = str.split("\\s");
        if (splitStr[0].equals("client1")) {
            send(client1, splitStr[1]);
        } else {
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

    private void send(Client client, String msg) {
        CountDownLatch sendMessageLatch = new CountDownLatch(1);
        new Thread(() -> {
            assertTimeout(Duration.ofMillis(TIMEOUT_FOR_SINGLE_MESSAGE), () -> client.sendMessage(msg));
            sendMessageLatch.countDown();
        }).start();
        try {
            sendMessageLatch.await();
            //Thread.sleep(250);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
