
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 *
 * @author proxz
 */
public class NewJFrame extends javax.swing.JFrame {

    // Главные поля игры
    private final String TITLE = "BitvaSlov"; // Название программы
    private static final String FILE_NAME = "/resources/dict.txt"; // Путь к словарю
    private static final String TMP_DIR = System.getenv("APPDATA") + "\\BitvaSlovTMP\\"; // Путь к временной папке 
    private static final String TMP_FILE_NAME = TMP_DIR + "winners.txt"; // Название файла с рейтингом
    private static String playerName; // Имя игрока
    private static ArrayList<String> wordsArray = new ArrayList<String>(); // Словарь
    private static String thisIsWord; // Слово которое отгадывается
    protected static int life = 5; // Поле с жизнями (по умолчанию 5 попыток на проигрышь)
    private static int xp = 0; // Поле с опытом
    private static double winFactor = 1.0; // Множитель опыта
    private static int wordStep = 0; // WinStreak - кол.подряд отгаданных слов на которых базируется множитель опыта
    private static int countWordsInDict = 0; // Счетчик всех слов в базе
    private static String tmp; // Тут храним предыдущее слово при инициализации кнопок (от повторов)
    private static int guessWords = 0; // Счетчик отгаданных слов
    private static final String PLAYER_DICT; // Переменная для обсалютного пути к программе
    private static boolean checkPlayerDict; // Чекер для проверки на наличие словаря игрока

    // Проверка на пользовательский словарь
    static {
        PLAYER_DICT = new File("").getAbsolutePath() + "\\bsdict.txt";
        if (new File(PLAYER_DICT).exists()) {
            checkPlayerDict = true;
        }
    }

    /**
     * Creates new form NewJFrame
     */
    public NewJFrame() {
        initComponents();

        // Вывод окна для ввода логина
        playerName = JOptionPane.showInputDialog(rootPane, "Введите ваше имя: ", "БитваСлов", JOptionPane.PLAIN_MESSAGE);
        // Проверка на пустоту и нажатие Cancel
        if (playerName == null || playerName != null && ("".equals(playerName))) {
            System.exit(0);
        }

        setLocationRelativeTo(null);
        setResizable(false);
        setTitle(TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new java.awt.Color(221, 221, 221)); // Цвет фона формы
        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/images/logo.png")); //
        setIconImage(icon.getImage()); // Иконка

        // Убираем фокус с кнопок
        jButton1.setFocusPainted(false);
        jButton2.setFocusPainted(false);
        jButton3.setFocusPainted(false);
        jButton4.setFocusPainted(false);

        jLabel4.addMouseListener(new MyMouseListener()); // Обработчик нажатия на Label

        try {

            wordsArray = getWordsFromFile(FILE_NAME);
            countWordsInDict = wordsArray.size(); // Считаем количество слов в базе

            reliase();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Главный метод реализации механики игры
    private void reliase() {
        jLabel2.setText("Опыт: " + xp); // Выводим поле с опытом
        jLabel5.setText("Жизни: " + life); // Выводим поле с жизнями
        jLabel3.setText("Всего слов в словаре: " + countWordsInDict);
        jLabel6.setText("Отгадано слов: " + guessWords);
        String[] mass = getTwoWords(wordsArray); // Получаем массив из двух слов которые будут отгадываться
        tmp = mass[1];
        thisIsWord = mass[1];
        jLabel1.setText(firstCharUpper(mass[0])); // Вывод слова на угадывание

        // Проверка, если кончились слова в словаре но жизни ещё есть, значит игрок отгадал все слова
        if (wordsArray.size() == 4 && life >= 1) {
            try {

                // Запись в статистику (файл)
                writeWinner(playerName, xp, guessWords);

                ImageIcon icon = new ImageIcon(getClass().getResource("/resources/images/winner.png"));
                String[] options = {"Начать сначала", "Выйти"};
                int x = JOptionPane.showOptionDialog(rootPane, "Поздравляем, вы выиграли разгадав все слова :)", "Winner!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, icon, options, options[1]);
                restartGame(x);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Генерируем рандомное число от 1 до 4х чтобы знать в какую именно кнопку запихнуть правильный ответ
        int rnd = 1 + (int) (Math.random() * 4);

        // Засовываем правильный ответ в кнопку
        switch (rnd) {
            case 1:
                jButton1.setText(mass[1]);
                break;
            case 2:
                jButton2.setText(mass[1]);
                break;
            case 3:
                jButton3.setText(mass[1]);
                break;
            case 4:
                jButton4.setText(mass[1]);
                break;
        }

        //
        DrawButtonLabel(mass[1], rnd);

    }

    // Метод отрисовки кнопок
    private void DrawButtonLabel(String word, int numberOfButton) {
        int i = 1;

        while (i < 4) {

            // Заполняем кнопку словом ЕСЛИ эта кнопка уже не заполнена
            if (i == 1 && i != numberOfButton) {
                boolean t = true;
                while (t) { // Зацикливаем пока не выполнится условие (false:false)
                    String[] arr = getTwoWords(wordsArray);

                    if (!arr[1].equals(word) && !tmp.equals(arr[1])) {
                        jButton1.setText(arr[1]);
                        tmp = arr[1];
                        i++;
                        t = false; // Вырубаем зацикливание
                    }
                }
            } else {
                i++;
            }

            // Заполняем кнопку словом ЕСЛИ эта кнопка уже не заполнена
            if (i == 2 && i != numberOfButton) {
                boolean t = true;
                while (t) { // Зацикливаем пока не выполнится условие (false:false)
                    String[] arr = getTwoWords(wordsArray);

                    if (!arr[1].equals(word) && !tmp.equals(arr[1])) {
                        jButton2.setText(arr[1]);
                        tmp = arr[1];
                        i++;
                        t = false; // Вырубаем зацикливание
                    }
                }
            } else {
                i++;
            }

            // Заполняем кнопку словом ЕСЛИ эта кнопка уже не заполнена
            if (i == 3 && i != numberOfButton) {
                boolean t = true;
                while (t) { // Зацикливаем пока не выполнится условие (false:false)
                    String[] arr = getTwoWords(wordsArray);

                    if (!arr[1].equals(word) && !tmp.equals(arr[1])) {
                        jButton3.setText(arr[1]);
                        tmp = arr[1];
                        i++;
                        t = false; // Вырубаем зацикливание
                    }
                }
            } else {
                i++;
            }

            // Заполняем кнопку словом ЕСЛИ эта кнопка уже не заполнена
            if (i == 4 && i != numberOfButton) {
                boolean t = true;
                while (t) { // Зацикливаем пока не выполнится условие (false:false)
                    String[] arr = getTwoWords(wordsArray);

                    if (!arr[1].equals(word) && !tmp.equals(arr[1])) {
                        jButton4.setText(arr[1]);
                        tmp = arr[1];
                        i++;
                        t = false; // Вырубаем зацикливание
                    }
                }
            } else {
                i++;
            }
        }
    }

    // Метод считывающий топ10 игроков по опыту
    private String gettinWinners() {
        String textToTableRating = "<table><tr><td>#</td><td>Имя</td><td>Слов</td><td>Опыт</td></tr>";
        try {
            BufferedReader bf = new BufferedReader(new FileReader(TMP_FILE_NAME));
            String line;

            ArrayList<PlayersList> arr = new ArrayList<>();

            while ((line = bf.readLine()) != null) {
                String[] mass = line.split(":");
                arr.add(new PlayersList(Integer.parseInt(mass[0]), mass[1], Integer.parseInt(mass[2])));
            }

            // Сортируем по полю XP
            Collections.sort(arr, new SortByRoll());

            for (int i = 0; i < 10; i++) {

                // Проверка на пустой массив
                if (arr.isEmpty()) {
                    textToTableRating += "<tr><td colspan=3> Пока нет победителей</td><td> </td><td> </td><td> </td></tr>";
                    break;
                }

                // Вывод разметки таблицы
                if (arr.get(i) != null) {
                    int counter = i + 1;
                    textToTableRating += "<tr><td>" + counter + "</td>" + arr.get(i) + "</tr>";
                } else {
                    textToTableRating += "<tr><td> </td><td> </td><td> </td><td> </td></tr>";
                    break;
                }
            }

        } catch (Exception ex) {
            textToTableRating += "<tr><td> </td><td> </td><td> </td><td> </td></tr>";
            //ex.printStackTrace();
        }

        textToTableRating += "</table>";
        return textToTableRating;
    }

    // Метод записывающий статистику победителя в файл
    private void writeWinner(String winnerName, int winnerXP, int winnerGuessWords) throws Exception {

        // Проверяем, если временной папки нет, создаем
        File createTmpFolder = new File(TMP_DIR);
        if (!createTmpFolder.exists()) {
            createTmpFolder.mkdir();
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(TMP_FILE_NAME, true));

        bw.write(winnerXP + ":" + winnerName + ":" + winnerGuessWords);
        bw.newLine();

        bw.close();
    }

    // Метод для получения пары слов из массива всех слов вида - eng:ru 
    private String[] getTwoWords(ArrayList<String> arr) {
        int rnd = 0 + (int) (Math.random() * arr.size());

        String line = arr.get(rnd);
        String mass[] = line.split(":");

        return mass;
    }

    // Метод поднимающий 1ю букву слова
    private String firstCharUpper(String str) {
        // Проверка на пустоту
        if (str == null || str.isEmpty()) {
            return "";
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // Метод получения опыта
    protected void getXP(boolean result) throws Exception {

        // Насчитываем опыт или уменьшаем счетчик жизней если не правильно угадал
        if (result) {
            guessWords += 1;
            winFactor = getWinStreak(wordStep);
            wordStep += 1;
            double tmp = 10 * winFactor;
            xp += (int) tmp;
        } else {
            wordStep = 0;
            winFactor = 1.0;
            life -= 1;
        }

        // Если игрок стратил все свои жизни то..
        if (life == 0) {
            // Запись в статистику (файл)
            writeWinner(playerName, xp, guessWords);

            ImageIcon icon = new ImageIcon(getClass().getResource("/resources/images/lose.png"));
            String[] options = {"Начать сначала", "Выйти"};
            int x = JOptionPane.showOptionDialog(rootPane, "Увы но Вы истратили все жизни :(", "УПС!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, icon, options, options[1]);
            restartGame(x);
        }

    }

    // kill streak
    private double getWinStreak(int step) {
        double result = 1.0;
        if (step >= 5 && step <= 10) {
            result = 1.5;
        }

        if (step >= 11 && step <= 15) {
            result = 2.0;
        }

        if (step >= 16 && step <= 25) {
            result = 3.0;
        }

        if (step >= 26 && step <= 35) {
            result = 4.0;
        }

        if (step >= 36) {
            result = 5.5;
        }

        return result;
    }

    // Метод который будет обнулять и запускать всё заново при проигрыше
    private void restartGame(int x) throws Exception {
        // Если нажата кнопка "Заново" обнуляем всю статистику
        if (x == 0) {
            xp = 0;
            life = 5;
            wordStep = 0;
            winFactor = 1.0;
            guessWords = 0;
            wordsArray = getWordsFromFile(FILE_NAME);
        } else if (x == 1) { // Если нажата кнопка "выход" 
            System.exit(0); // Выходим из программы
        }
    }

    // Главный метод который вытаскивает слова из словаря 
    // и помещает в статический массив слов с которым мы и будем работать
    private ArrayList<String> getWordsFromFile(String fileName) throws Exception {

        ArrayList<String> arr = new ArrayList<>();

        // Если есть словарь игрока
        if (checkPlayerDict) {
            BufferedReader bf = new BufferedReader(new FileReader(PLAYER_DICT));
            String str;

            while ((str = bf.readLine()) != null) {
                arr.add(str);
            }

            bf.close();
        } else {
            InputStream in = getClass().getResourceAsStream(fileName);
            BufferedReader bf = new BufferedReader(new InputStreamReader(in, "Cp1251"));

            String str;

            while ((str = bf.readLine()) != null) {
                arr.add(str);
            }

            bf.close(); // Закрываем потоки
            in.close();
        }

        return arr;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(221, 221, 221));
        setIconImages(null);

        jLabel1.setFont(new java.awt.Font("Verdana", 1, 28)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Слово");

        jButton1.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButton1.setText("Слово1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButton2.setText("Слово2");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButton3.setText("Слово3");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButton4.setText("Слово4");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        jLabel2.setText("Опыт:");

        jLabel3.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        jLabel3.setText("Всего слов: ");

        jLabel4.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(153, 153, 153));
        jLabel4.setText("[by proxz]");

        jLabel5.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        jLabel5.setText("Жизни:");

        jLabel6.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        jLabel6.setText("Отгадано слов:");

        jButton5.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        jButton5.setText("Топ10");
        jButton5.setFocusPainted(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 47, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)))
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(52, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton5)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Обработчик нажатия 1й кнопки
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:

        try {
            if (jButton1.getText().equals(thisIsWord)) {

                // Костыль он же слушатель для изменения цвета кнопки при правильном ответе в момент нажатия (клика)
                jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        jButton1.setBackground(Color.GREEN);
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        jButton1.setBackground(UIManager.getColor("control"));
                    }
                });

                getXP(true); // Даем опыт

                // Перебираем массив слов для того что бы найти угадываемое слово и по его индексу его удалить из массива
                // Для того что бы оно больше не попадалось
                for (int i = 0; i < wordsArray.size(); i++) {
                    String[] mass = wordsArray.get(i).split(":");
                    if (mass[1].equals(thisIsWord)) {
                        wordsArray.remove(i);
                    }
                }
            } else {

                // Костыль он же слушатель для изменения цвета кнопки при НЕправильном ответе в момент нажатия (клика)
                jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        jButton1.setBackground(Color.RED);
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        jButton1.setBackground(UIManager.getColor("control"));
                    }
                });

                getXP(false); // Снимаем жизнь
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // костыль
        tmp = thisIsWord;
        reliase();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Обработчик нажатия 2й кнопки
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:

        try {
            if (jButton2.getText().equals(thisIsWord)) {

                // Костыль он же слушатель для изменения цвета кнопки при правильном ответе в момент нажатия (клика)
                jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        jButton2.setBackground(Color.GREEN);
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        jButton2.setBackground(UIManager.getColor("control"));
                    }
                });

                getXP(true); // Даем опыт

                // Перебираем массив слов для того что бы найти угадываемое слово и по его индексу его удалить из массива
                // Для того что бы оно больше не попадалось
                for (int i = 0; i < wordsArray.size(); i++) {
                    String[] mass = wordsArray.get(i).split(":");
                    if (mass[1].equals(thisIsWord)) {
                        wordsArray.remove(i);
                    }
                }
            } else {

                // Костыль он же слушатель для изменения цвета кнопки при НЕправильном ответе в момент нажатия (клика)
                jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        jButton2.setBackground(Color.RED);
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        jButton2.setBackground(UIManager.getColor("control"));
                    }
                });

                getXP(false); // Снимаем жизнь
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        tmp = thisIsWord;
        reliase();
    }//GEN-LAST:event_jButton2ActionPerformed

    // Обработчик нажатия 3й кнопки
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:

        try {
            if (jButton3.getText().equals(thisIsWord)) {

                // Костыль он же слушатель для изменения цвета кнопки при правильном ответе в момент нажатия (клика)
                jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        jButton3.setBackground(Color.GREEN);
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        jButton3.setBackground(UIManager.getColor("control"));
                    }
                });

                getXP(true); // Даем опыт

                // Перебираем массив слов для того что бы найти угадываемое слово и по его индексу его удалить из массива
                // Для того что бы оно больше не попадалось
                for (int i = 0; i < wordsArray.size(); i++) {
                    String[] mass = wordsArray.get(i).split(":");
                    if (mass[1].equals(thisIsWord)) {
                        wordsArray.remove(i);
                    }
                }
            } else {

                // Костыль он же слушатель для изменения цвета кнопки при НЕправильном ответе в момент нажатия (клика)
                jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        jButton3.setBackground(Color.RED);
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        jButton3.setBackground(UIManager.getColor("control"));
                    }
                });

                getXP(false); // Снимаем жизнь
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        tmp = thisIsWord;
        reliase();
    }//GEN-LAST:event_jButton3ActionPerformed

    // Обработчик нажатия 4й кнопки
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:

        try {
            if (jButton4.getText().equals(thisIsWord)) {

                // Костыль он же слушатель для изменения цвета кнопки при правильном ответе в момент нажатия (клика)
                jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        jButton4.setBackground(Color.GREEN);
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        jButton4.setBackground(UIManager.getColor("control"));
                    }
                });

                getXP(true); // Даем опыт

                // Перебираем массив слов для того что бы найти угадываемое слово и по его индексу его удалить из массива
                // Для того что бы оно больше не попадалось
                for (int i = 0; i < wordsArray.size(); i++) {
                    String[] mass = wordsArray.get(i).split(":");
                    if (mass[1].equals(thisIsWord)) {
                        wordsArray.remove(i);
                    }
                }
            } else {

                // Костыль он же слушатель для изменения цвета кнопки при НЕправильном ответе в момент нажатия (клика)
                jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        jButton4.setBackground(Color.RED);
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        jButton4.setBackground(UIManager.getColor("control"));
                    }
                });

                getXP(false); // Снимаем жизнь
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        tmp = thisIsWord;
        reliase();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:

        // Достаем в строку статистику для вывода рейтинга
        String g = "";
        try {

            g = gettinWinners();

        } catch (Exception ex) {

        }

        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/images/winlist.png"));
        JLabel jCopyRight = new JLabel("<html><b>Рейтинг</b> <br /><br />" + g + "</html>");
        jCopyRight.setFont(new java.awt.Font("Verdana", 0, 11));
        JOptionPane.showMessageDialog(rootPane, jCopyRight, "ТОП10", JOptionPane.PLAIN_MESSAGE, icon);
    }//GEN-LAST:event_jButton5ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new NewJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    // End of variables declaration//GEN-END:variables

}

// Класс слушатель нажатия мыши для jLabel4 (by proxz - пасхалка)
// Дает +10 жизней :)
class MyMouseListener implements MouseListener {

    private static int eggs = 0;

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

        // Пасхалочка
        eggs += 1;

        int rnd = 8 + (int) (Math.random() * 13); // Генерируем случайное число :)

        if (eggs == rnd) {
            NewJFrame nf = new NewJFrame();
            ImageIcon icon = new ImageIcon(getClass().getResource("/resources/images/duck.png"));
            JLabel jCopyRight = new JLabel("<html>Powered by Proxz :) <br />Written on Java/Swing<br /><br />Version: 1.6.1</html>");
            jCopyRight.setFont(new java.awt.Font("Verdana", 0, 11));

            try {
                // Пасхалка
                nf.life = 11; // Даем любознательным +10 хп :)
                nf.getXP(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(nf, jCopyRight, "Пасхалочка", JOptionPane.PLAIN_MESSAGE, icon);

        }

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}

class PlayersList {

    int xp, guessWords;
    String name;

    public PlayersList(int xp, String name, int gw) {
        this.name = name;
        this.xp = xp;
        this.guessWords = gw;
    }

    @Override
    public String toString() {
        return "<td>" + this.name + "</td><td>" + this.guessWords + "</td><td>" + this.xp + "</td>";
    }

}

class SortByRoll implements Comparator<PlayersList> {

    @Override
    public int compare(PlayersList o1, PlayersList o2) {
        return o2.xp - o1.xp;
    }

}
