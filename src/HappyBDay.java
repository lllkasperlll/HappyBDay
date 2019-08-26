import javax.swing.*;
import java.io.*;
import java.util.Calendar;
import static java.awt.Frame.MAXIMIZED_BOTH;

public class HappyBDay{

    public Calendar day = Calendar.getInstance();

    public static void main(String args[]) {
        HappyBDay program  = new HappyBDay();
    }

    public HappyBDay(){

        //создать фрейм
        JFrame frame = new JFrame("Happy Birthday");
        //создаь свою панель
        MyPanel myPanel = new MyPanel();
        //добавить картинку на панель
        myPanel.setImageFile(new File("image/SPP.png"));

        //добавить панель во фрейм
        frame.getContentPane().add(myPanel);
        //для закрытия окна
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //стандартный размер фрейма
        frame.setSize(640, 480);
        //растянуть фрейм на весь экран
        frame.setExtendedState(MAXIMIZED_BOTH);
        //сделать фрейм видимым
        frame.setVisible(true);

        //цикл для перезапуска
        while (true){
            Calendar newDay = Calendar.getInstance();
            //при изменении даты
            if (!(String.format("%td, %<tB", day).equals(String.format("%td, %<tB", newDay)))){
                day = newDay;
                //очистить myPanel
                myPanel.removeAll();
                //создаь свою панель
                myPanel = new MyPanel();
                //добавить картинку на панель
                myPanel.setImageFile(new File("Images/SPP.png"));
                //добавить панель во фрейм
                frame.getContentPane().add(myPanel);
                //для закрытия окна
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //стандартный размер фрейма
                frame.setSize(640, 480);
                //растянуть фрейм на весь экран
                frame.setExtendedState(MAXIMIZED_BOTH);
                //сделать фрейм видимым
                frame.setVisible(true);
            }
        }
    }
}