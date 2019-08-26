import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MyPanel extends JPanel{

    //2 изображения: оригинальное и текущее.
    //Оригинальное используется для получения текущего в зависимости от размеров панели.
    private BufferedImage originalImage = null;
    private Image image = null;

    public MyPanel()
    {
        HashMap<String, Calendar> mapBDay = ExcelWorking.readExcel();
        JLabel birthday = ExcelWorking.birthdayLabel(mapBDay);
        JLabel postBirthday = ExcelWorking.postBirthdayLabel(mapBDay);

        Font fontBirthday = new Font("Gabriola", Font.PLAIN, 40);
        birthday.setFont(fontBirthday);
        birthday.setHorizontalAlignment(JLabel.CENTER);
        birthday.setVerticalAlignment(JLabel.CENTER);

        Font fontPostBirthday = new Font("Gabriola", Font.PLAIN, 20);
        postBirthday.setFont(fontPostBirthday);
        postBirthday.setForeground(new Color(25, 83, 175));
        postBirthday.setHorizontalAlignment(JLabel.CENTER);

        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, birthday);
        add(BorderLayout.SOUTH, postBirthday);

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
    }

    //реакция на изменение размеров панели - изменение размера изображения.
    private void formComponentResized(ComponentEvent evt) {
        int w = this.getWidth();
        int h = this.getHeight();
        //System.out.println("Width - " + this.getWidth() + "| Height - " + this.getHeight());
        if ((originalImage != null) && (w > 0) && (h > 0)) {
            image = originalImage.getScaledInstance(w, h, Image.SCALE_DEFAULT);
            this.repaint();
        }
    }

    //сам прорисовываю
    public void paint(Graphics g) {
        //рисую картинку
        if (image != null) {
            g.drawImage(image, 0, 0, null);
        }

        //рисую подкомпоненты.
        super.paintChildren(g);
        //рисую рамку
        super.paintBorder(g);
    }

    //загрузка картинки
    public void setImageFile(File imageFile) {
        try {
            if (imageFile == null) {
                originalImage = null;
            }
            BufferedImage bi = ImageIO.read(imageFile);
            originalImage = bi;
        } catch (IOException ex) {
            System.out.println("Ошибка загрузки фона: " + ex);
            ex.printStackTrace();
        }
        repaint();
    }

    //класс для работы с excel
    static class ExcelWorking{
        //метод чтения excel
        static HashMap<String, Calendar> readExcel(){

            HashMap<String, Calendar> mapBDay = new HashMap<String, Calendar>();
            FileInputStream file = null;
            XSSFWorkbook workbook = null;
            try {
                //получить файл
                file = new FileInputStream(new File("C:\\HappyBDay\\BDays.xlsx"));
                //считать его в память
                workbook = new XSSFWorkbook(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("Ошибка загрузки excel: " + e);
            }catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Ошибка считывания excel: " + ex);
            }

            //работать с первым листом
            XSSFSheet sheet = workbook.getSheetAt(0);
            //получить вторую строку листа excel файла
            Row row = sheet.getRow(1);
            //получить ячейку D2
            Cell cell = row.getCell(3);
            //что бы знать кол-во строк
            int rowNum = (int) cell.getNumericCellValue();
            //пройтись по всем строкам и заполнить mapName
            for (int i = 1; i <= rowNum; i++){
                row = sheet.getRow(i);
                cell = row.getCell(0);
                String fio = cell.getStringCellValue();
                cell = row.getCell(1);
                String sBDay = cell.getStringCellValue();

                Calendar bDay = Calendar.getInstance();
                //для преобразования String в Date
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                Date date = null;
                try {
                    //записать дату
                    date = format.parse(sBDay);
                    //Записать Date в Calendar
                    bDay.setTime(date);
                }catch (NullPointerException ex){
                    ex.printStackTrace();
                    System.out.println("Ошибка календаря: " + ex);
                } catch (ParseException e) {
                    e.printStackTrace();
                    System.out.println("Ошибка форматирования: " + e);
                }
                //System.out.println(fio + " " + String.format("%td, %<tB", bDay));
                mapBDay.put(fio, bDay);
            }
            return mapBDay;
        }

        //Логика заполнения поздравления
        static JLabel birthdayLabel(HashMap<String, Calendar> mapBDay){

            JLabel birthday;
            //для записи в JLable
            String textBirthday = "<html><body><div align='center'><br><b>Сегодня День Рождения празднует</b><br><font color='#1953af'><b>";
            String textBirthday1 = "<html><body><div align='center'><br><b>Сегодня День Рождения празднуют</b><br><font color='#1953af'><b>";

            //счетчик для др
            int numBDay = 0;

            //для каждого элемента map
            for (HashMap.Entry<String, Calendar> test : mapBDay.entrySet()) {
                String fio = test.getKey();                     //ключ
                Calendar bDay = test.getValue();                //значение
                //System.out.println(fio + " : " + String.format("%td, %<tB", bDay));

                //Текущая дата
                Calendar calendar = Calendar.getInstance();
                //Сборка текста поздравления
                if (String.format("%td, %<tB", calendar).equals(String.format("%td, %<tB", bDay)) && numBDay == 0){
                    numBDay++;
                    textBirthday += fio;
                    textBirthday1 += fio;
                }else if (String.format("%td, %<tB", calendar).equals(String.format("%td, %<tB", bDay)) && numBDay == 1){
                    numBDay++;
                    textBirthday = textBirthday1 + ",<br> " + fio;
                    textBirthday1 += "<br>и " + fio;
                }else if (String.format("%td, %<tB", calendar).equals(String.format("%td, %<tB", bDay)) && numBDay > 1){
                    numBDay++;
                    textBirthday1 = textBirthday + "<br> и " + fio;
                    textBirthday += ",<br>" + fio;
                }
            }
            //в зависимости от кол-ва др сегодня собираю поздравление
            if (numBDay == 1){
                textBirthday += "</b></font><br>От дружной команды СП «Практика» желаем осуществления всех<br>планов, достижения поставленных целей, а также профессиональных<br>успехов и творческого вдохновения!<br>Крепкого здоровья, счастья, удачи!</div></body></html>";
            }else if (numBDay > 1){
                textBirthday = textBirthday1 + "</b></font><br>От дружной команды СП «Практика» желаем осуществления всех<br>планов, достижения поставленных целей, а также профессиональных<br>успехов и творческого вдохновения!<br>Крепкого здоровья, счастья, удачи!</div></body></html>";
            }else{
                textBirthday = "<html><body><div align='center'><h1>Дни рождения сотрудников</h1><font color='#1953af'></div></body></html>";
            }
            birthday = new JLabel(textBirthday);
            return birthday;
        }

        //Логика заполнения предстоящих др
        static JLabel postBirthdayLabel(HashMap<String, Calendar> mapBDay){

            JLabel postBirthday;
            //для записи в JLable
            String textPostBirthday = "<html><body><div align='center'><h1>Ближайшие дни рождения</h1><table border='1' bordercolor='red'><tr>";

            //счетчик для др
            int numPostBDay = 0;
            //хранение ближайших др
            ArrayList<String> postBDayList = new ArrayList<String>();

            //для каждого элемента map
            for (HashMap.Entry<String, Calendar> test : mapBDay.entrySet()) {
                String fio = test.getKey();                     //ключ
                Calendar bDay = test.getValue();                //значение
                //System.out.println(fio + " : " + String.format("%td, %<tB", bDay));
                //цикл проверяет у кого ближайший месяц др
                for (int i = 1; i < 31; i++){
                    //каждый раз берем текущую дату
                    Calendar calendar = Calendar.getInstance();
                    //устанавливаем текущий год для сравнения дат
                    bDay.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                    //прибавляю i дней к установленной дате
                    calendar.add(Calendar.DATE, i);
                    if (String.format("%td, %<tB", calendar).equals(String.format("%td, %<tB", bDay))){
                        numPostBDay++;
                        postBDayList.add(fio);
                    }
                }
            }
            //для каждого элемента list
            for (int i = 1; i < postBDayList.size(); i++){
                if (mapBDay.get(postBDayList.get(i)).before(mapBDay.get(postBDayList.get(i - 1)))){
                    String s = postBDayList.get(i);
                    String s1 = postBDayList.get(i - 1);
                    postBDayList.set(i - 1, s);
                    postBDayList.set(i, s1);
                    if (i != 1){
                        i -= 2;
                    }else{
                        i--;
                    }
                }
            }
            //беру 4 самых ближайших др, если они есть
            if (postBDayList.size() > 4){
                for (int i = 0; i < 4; i++){
                    textPostBirthday += "<td width='300'><div align='center'>" + postBDayList.get(i) + "<br>" + String.format("%td, %<tB", mapBDay.get(postBDayList.get(i))) + "</div></td>";
                }
            }else{//если нет 4х ближайших др
                for (int i = 0; i < postBDayList.size(); i++){
                    textPostBirthday += "<td width='300'><div align='center'>" + postBDayList.get(i) + "<br>" + String.format("%td, %<tB", mapBDay.get(postBDayList.get(i))) + "</div></td>";
                }
            }


            textPostBirthday += "</tr></table></div></body></html>";
            postBirthday = new JLabel(textPostBirthday);
            return postBirthday;
        }
    }
}