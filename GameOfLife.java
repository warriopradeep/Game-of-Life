package life;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Random;

public class GameOfLife extends JFrame {

    JPanel p1 = new JPanel(new BorderLayout());
    JPanel p2 = new JPanel();
    JLabel genL = new JLabel("Generation #");
    JLabel aliveL = new JLabel("Alive: ");
    JPanel buttonRow = new JPanel();
    JToggleButton toggle = new JToggleButton("Pause");
    JButton reset = new JButton("Reset");
    private final Object pauseLock = new Object();
    boolean toggleFlag = false;

    final int WIDTH = 320;
    final int HEIGHT = 380;

    private int gen = 0;
    private int alive = 0;

    //No. of cells
    private int N = 10;

    //No. of generations
    private int M = 1000;

    //Random object
    Random r;

    //arrays for the cell status
    boolean[][] curGen = new boolean[N][N];
    boolean[][] nexGen = new boolean[N][N];

    //grid Panels
    JPanel[][] grid = new JPanel[10][10];

    int tracker = 0;

    //Constructor
    public GameOfLife() {
        super("Game of Life");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        initArray();
        initGUIComponents();
        setVisible(true);
        setName("Game of Life");



        while (tracker < M) {
            calcNextGen(curGen, nexGen, N);
            copyToCurrent(curGen, nexGen, N);
            tracker++;
            showCurrentGen(curGen, N);
            if (toggleFlag) {
                try {
                    Thread.currentThread().join(10000);
                } catch (Exception e) {}
            } else {
                try {
                    Thread.currentThread().notifyAll();
                } catch (Exception e) {}
            }
            try {
                Thread.sleep(100);
            } catch (Exception e) {}
        }
    }

    private void initGUIComponents() {

        //p1.setBackground(Color.BLUE);
        p1.setBounds(0, 0, WIDTH, 30);

        p2.setBackground(Color.BLACK);
        p2.setBounds(0, 30, WIDTH, 300);

        setLayout(new BorderLayout());

        add(p1, BorderLayout.NORTH);
        //p1.setVisibpanel1le(false);
        add(p2, BorderLayout.CENTER);


        p1.setLayout(new BorderLayout());

        //Generation label
        genL.setName("GenerationLabel");
        genL.setSize(WIDTH, HEIGHT);
        genL.setEnabled(true);
        genL.setVisible(true);
        p1.add(genL, BorderLayout.NORTH);

        //Alive label
        aliveL.setName("AliveLabel");
        aliveL.setSize(WIDTH, HEIGHT);
        p1.add(aliveL, BorderLayout.CENTER);

        //Toggle Button
        toggle.setName("PlayToggleButton");
        toggle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                toggleActionPerformed(actionEvent);
            }

            private void toggleActionPerformed(ActionEvent actionEvent) {
                String name = toggle.getText();
                if ("Pause".equals(name)) {
                    toggle.setText("Resume");
                    toggleFlag = false;

                } else {
                    toggle.setText("Pause");
                    toggleFlag = true;
                }
            }
        });
        buttonRow.add(toggle);

        //Reset Button
        reset.setName("ResetButton");
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                resetActionPerformed(actionEvent);
            }

            private void resetActionPerformed(ActionEvent actionEvent) {
                reInit();
            }
        });
        buttonRow.add(reset);

        p1.add(buttonRow, BorderLayout.SOUTH);

        //grid
        //let n = 10
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                grid[i][j] = new JPanel();
                grid[i][j].setSize(WIDTH / 10, WIDTH / 10);
                grid[i][j].setVisible(true);
                grid[i][j].setBackground(Color.WHITE);
                p2.add(grid[i][j]);
            }
        }

        p2.setLayout(new GridLayout(10, 10, 1, 1));

    }

    //initialize current array cells
    private void initArray() {
        r  = new Random();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                boolean b = r.nextBoolean();
                curGen[i][j] = b;
            }
        }
    }

    public void reInit() {
        initArray();
        tracker = 0;
        gen = 0;
        for (boolean[] row : nexGen) {
            Arrays.fill(row, false);
        }
    }

    private void copyToCurrent(boolean[][] curGen, boolean[][] nexGen, int n) {
        alive = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (nexGen[i][j]) {
                    alive++;
                }
                curGen[i][j] = nexGen[i][j];
            }
        }
    }

    private void showCurrentGen(boolean[][] curGen, int N) {
        System.out.println("Generation #" + gen);
        genL.setText("Generation #" + gen);
        gen++;
        System.out.println("Alive: " + alive);
        aliveL.setText("Alive: " + alive);

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (curGen[i][j]) {
                    System.out.print("O");
                    grid[i][j].setBackground(Color.BLACK);
                } else {
                    System.out.print("*");
                    grid[i][j].setBackground(Color.WHITE);
                }
            }
            System.out.println("\n");
        }
    }

    public void calcNextGen(boolean[][] curGen, boolean[][] nexGen, int N) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                nexGen[i][j] = deadAlive(curGen, i, j, curGen[i][j]);
            }
        }
    }

    public boolean deadAlive(boolean[][] curGen, int i, int j, boolean flag) {
        int n = curGen.length;
        int count = 0;
        boolean[] nbr = new boolean[8];

        int A = i - 1;
        int B = i + 1;
        int C = j - 1;
        int D = j + 1;

        if (A < 0) {
            A = n - 1;
        }

        if (B > n - 1) {
            B = 0;
        }

        if (C < 0) {
            C = n - 1;
        }

        if (D > n - 1) {
            D = 0;
        }

        nbr[0] = curGen[A][C];
        nbr[1] = curGen[A][j];
        nbr[2] = curGen[A][D];

        nbr[3] = curGen[i][C];
        nbr[4] = curGen[i][D];

        nbr[5] = curGen[B][C];
        nbr[6] = curGen[B][j];
        nbr[7] = curGen[B][D];

        for (boolean b : nbr) {
            if (b) {
                count++;
            }
        }
        if (flag) {
            return count == 2 || count == 3;
        } else {
            return count == 3;
        }
    }



    public static void main(final String[] args) {
        new GameOfLife();
    }

}
