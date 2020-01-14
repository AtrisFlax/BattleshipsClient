package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Client.GamePrimitive.GameField;
import com.liver_rus.Battleships.Network.Client;
import com.liver_rus.Battleships.Network.GameServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NetworkTest {
    private static final Logger log = Logger.getLogger(String.valueOf(NetworkTest.class));
    Thread serverThread;
    Client client1;
    Client client2;

    static final int MAX_CONNECTIONS = 2;

    @BeforeEach
    void setUp() throws IOException {
        int port = 10071;
        String host = "127.0.0.1";

        GameField[] gameFields = new GameField[2];
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            gameFields[i] = new GameField();
        }

        serverThread = new Thread(new GameServer(port, gameFields, GameServer.TurnOrder.RANDOM_TURN));
        serverThread.start();

        try {
            client1 = new Client(host, port);
            client2 = new Client(host, port);
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //TODO asserton with time out

    //попробовать сделать send messege на треде теста
    //sendMessageLatch выполнять на треде теста
    //тест должен отрабатывать контакт
    //сейчас кажется что sendmessga синхронный
    //латч возможно лишний
    private void send(Client client, String msg) {
        CountDownLatch sendMessageLatch = new CountDownLatch(1);
        new Thread(() -> {
            client.sendMessage(msg);
            sendMessageLatch.countDown();
        }).start();
        try {
            sendMessageLatch.await();
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO написать тест с проверкой содержимого GameField'ов
    //после отправки SEND_SHIPS,SHOT, MISS


    //тестирование заранее прописаного цикла игры с проверкой инбоксов клиентов
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
    // ** файлы содержат лишний заголовок для проверки содержимого в случае "неправильной" генирации хода

    @Test
    void gameCycle() throws InterruptedException {
        //TODO TestCases/Case1 подсатвлять конкатить внутрь  getStringStreamFromFile
        Stream<String> sendInfoStream = getStringStreamFromFile("Case1/sendToServer.txt");
        Stream<String> client1ExpectedInboxStream = getStringStreamFromFile("Case1/awaitedInboxClient1.txt");
        Stream<String> client2ExpectedInboxStream = getStringStreamFromFile("Case1/awaitedInboxClient2.txt");
        //SEND SHIP INFO
        final int SHIPS_INFO_LIMIT = 2;
        String[] sendInfo = sendInfoStream.toArray(String[]::new);
        //SEND SHIP INFO
        for (int i = 0; i < SHIPS_INFO_LIMIT; i++)
            splitAndSend(sendInfo[i]);
        //SEND SHOTS
        boolean client1FirstTurn = client1.getInbox().get(client1.getInbox().size() - 1).equals(Constants.NetworkMessage.YOU_TURN);
        if (client1FirstTurn) {
            //skip fake shot (MISS_SHOT) for client2 first turn
            for (int i = SHIPS_INFO_LIMIT + 1; i < sendInfo.length; i++)
                splitAndSend(sendInfo[i]);
                System.out.println();
        } else {
            for (int i = SHIPS_INFO_LIMIT; i < sendInfo.length; i++)
                splitAndSend(sendInfo[i]);
        }

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
    void tearDown() {
        log.info("tearDown");
        serverThread.interrupt();
    }

    void splitAndSend(String str) {
        //TODO \\s+ можно убрать. заменить на только пробельные символы
        String[] splitStr = str.split("\\s+");
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

}
