import java.awt.*;
import java.awt.EventQueue;
import java.awt.GridLayout;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeUnit;


public class IslandVisualizer {

    // delay in miliseconds (controls animation speed)
    private static final int DELAY = 1000;
    private static final int RESOLUTION = 800;
    private static final double ICON_SCALE = 0.30;
    private static boolean lockedMouse = false;

    // draw N-by-N percolation system
    public static void drawMountain(int col, int row, int N) {
        StdDraw.setPenColor(StdDraw.BLACK);
        double[] x = {col-0.9, col-0.8, col-0.7, col-0.55, col-0.4, col-0.35, col-0.15, col-0.9};
        double[] y = {N-row+0.1, N-row+0.5, N-row+0.4, N-row+0.85, N-row+0.6, N-row+0.7, N-row+0.1, N-row+0.1};
        StdDraw.setPenRadius(0.01);
        StdDraw.polygon(x, y);
    }

    public static void drawWeather(int col, int row, int rainState, int sunState, int N) {
        if (rainState > 0)
        {
            StdDraw.picture(col-0.75, N-row+0.75,"images/rain0" + Integer.toString(rainState) + ".png", ICON_SCALE, ICON_SCALE);
        }
        if (sunState > 0)
        {
            StdDraw.picture(col-0.25, N-row+0.75,"images/sun0" + Integer.toString(sunState) + ".png", ICON_SCALE, ICON_SCALE);
        }

    }

    private static void drawRabbit(int col, int row, TerrainField field, int N)
    {
        int rabbits = field.getRabbits();
        if (rabbits > 0)
        {
            StdDraw.picture(col-0.48, N-row+0.5, "images/rabbit0" + Integer.toString(rabbits) + ".png", ICON_SCALE, ICON_SCALE);
        }
    }

    private static void drawHunter(int col, int row, TerrainField field, int N) {
        int hunters = field.getHunters();
        if (hunters > 0)
        {
            StdDraw.picture(col-0.75, N-row+0.25, "images/hunter0" + Integer.toString(hunters) + ".png", ICON_SCALE + 0.05, ICON_SCALE + 0.05 );
        }
    }

    private static void drawWolf(int col, int row, TerrainField field, int N) {
        int hunters = field.getHunters();
        if (hunters > 0)
        {
            StdDraw.picture(col-0.25, N-row+0.25, "images/wolf0" + Integer.toString(hunters) + ".png", ICON_SCALE + 0.05, ICON_SCALE + 0.05 );
        }
    }

    public static void draw(Island island, int N) {
        System.out.println("draw");
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setXscale(0, N);
        StdDraw.setYscale(0, N);
        StdDraw.filledSquare(N/2.0, N/2.0, N/2.0);

        for (int row = N; row >= 1; row--) {
            for (int col = N; col >= 1; col--) {
                TerrainField terrain = island.getTerrainField(row, col);
                String text = "";
                if (terrain.getTerrainType() == TerrainField.WATER) {
                    StdDraw.setPenColor(StdDraw.BLUE);
                }
                else if (terrain.getTerrainType() == TerrainField.MOUNT) {
                    StdDraw.setPenColor(StdDraw.WHITE);
                }
                else if (terrain.getTerrainType() == TerrainField.MEADOW) {
                    // change meadow color, in order to juiciness
                    int juice = terrain.getJuiciness();
                    if (juice > 0) {
                        StdDraw.setPenColor(new Color(0, 255 - (juice -1) * 45, 0));
                    }
                    else {
                        StdDraw.setPenColor(StdDraw.WHITE);
                    }
                }
                StdDraw.filledSquare(col - 0.5, N - row + 0.5, 0.48);
                StdDraw.setPenColor(StdDraw.BLACK);

                if (terrain.getTerrainType() == TerrainField.MOUNT)
                {
                    drawMountain(col, row, N);
                }

                drawWeather(col, row, terrain.getRain(), terrain.getSun(), N);
                drawRabbit(col, row, terrain, N);
                drawHunter(col, row, terrain, N);
                drawWolf(col, row, terrain, N);
            }
        }
    }

    private static void displayPopup(TerrainField field) {
        String[] items = {"ВОДА", "ГОРЫ", "ЛУГ"};
        JComboBox combo = new JComboBox(items);
        combo.setSelectedIndex(field.getTerrainType() - 1);
        JTextField fieldSun = new JTextField(String.valueOf(field.getSun()));
        JTextField fieldRain = new JTextField(String.valueOf(field.getRain()));
        JTextField field1 = new JTextField(String.valueOf(field.getJuiciness()));
        JTextField fieldRabbits = new JTextField(String.valueOf(field.getRabbits()));
        JTextField fieldHunter = new JTextField(String.valueOf(field.getHunters()));
        JTextField fieldWolves = new JTextField(String.valueOf(field.getWolves()));

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(combo);
        panel.add(new JLabel("Сонце:"));
        panel.add(fieldSun);
        panel.add(new JLabel("Дождь:"));
        panel.add(fieldRain);
        panel.add(new JLabel("Трава:"));
        panel.add(field1);
        panel.add(new JLabel("Кролики:"));
        panel.add(fieldRabbits);
        panel.add(new JLabel("Охотники: "));
        panel.add(fieldHunter);
        panel.add(new JLabel("Волки: "));
        panel.add(fieldWolves);
        int result = JOptionPane.showConfirmDialog(null, panel, "Тонкая настройка",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            field.setSun(Integer.parseInt(fieldSun.getText()));
            field.setRain(Integer.parseInt(fieldRain.getText()));
            field.setJuiciness(Integer.parseInt(field1.getText()));
            field.setRabbits(Integer.parseInt(fieldRabbits.getText()));
            field.setHunters(Integer.parseInt(fieldHunter.getText()));
            field.setWolves(Integer.parseInt(fieldWolves.getText()));
            field.setTerrainType(combo.getSelectedIndex() + 1);
            System.out.println("OK_OPTION");
        } else {
            System.out.println("Cancelled");
        }
    }

    private static TerrainField findTerrain(Island island, int x, int y, int N) {
        StdOut.println("find");
        StdOut.println(island.getTerrainField(y, x).getTerrainType());
        return island.getTerrainField(y, x);
    }

    private static int InvertCoord(int y, int N) {
        int arr[] = new int[N + 1];
        int st = N;
        for (int i = 1; i <= N; ++i) {
            arr[i] = st--;
        }

        return arr[y];
    }

    public static void main(String[] args) {

        // turn on animation mode
        //StdDraw.show(0);

        // repeatedly read in sites to open and draw resulting system
        int N = 4;
        Island island = new Island(N);
        island.setLifeTime(42);
        //StdDraw.show(0);


        StdDraw.setCanvasSize(RESOLUTION, RESOLUTION);
        draw(island, N);

        while (true) {
            if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) {
                System.out.println("tickTack");
                island.tickTack();
                draw(island, N);
            }

            if (StdDraw.mousePressed())
            {
                StdOut.println(StdDraw.mouseX());
                StdOut.println(StdDraw.mouseY());
                lockedMouse = true;
            }

            if (StdDraw.isKeyPressed(KeyEvent.VK_R)) {
                for (int i = 0; i < N; ++i) {
                    for (int j = 0; j < N; ++j) {
                        TerrainField curr = island.getTerrainField(i + 1, j + 1);
                        if (curr.getTerrainType() == TerrainField.MEADOW) {
                            int rand = StdRandom.uniform(0, 15);
                            if (rand <= 3 ) {
                                curr.setRabbits(rand);
                            }
                        }
                    }
                }
                draw(island, N);
            }

            if (StdDraw.isKeyPressed(KeyEvent.VK_H)) {
                for (int i = 0; i < N; ++i) {
                    for (int j = 0; j < N; ++j) {
                        TerrainField curr = island.getTerrainField(i + 1, j + 1);
                        if (curr.getTerrainType() == TerrainField.MEADOW) {
                            int rand = StdRandom.uniform(0, 15);
                            if (rand <= 3 ) {
                                curr.setHunters(rand);
                            }
                        }
                    }
                }
                draw(island, N);
            }

            if (StdDraw.isKeyPressed(KeyEvent.VK_W)) {
                for (int i = 0; i < N; ++i) {
                    for (int j = 0; j < N; ++j) {
                        TerrainField curr = island.getTerrainField(i + 1, j + 1);
                        if (curr.getTerrainType() == TerrainField.MEADOW) {
                            int rand = StdRandom.uniform(0, 15);
                            if (rand <= 3 ) {
                                curr.setWolves(rand);
                            }
                        }
                    }
                }
                draw(island, N);
            }

            if (StdDraw.isKeyPressed(KeyEvent.VK_ENTER) && lockedMouse)
            {
                try {
                    int x = (int) StdDraw.mouseX() + 1;
                    int y = InvertCoord((int) StdDraw.mouseY() + 1, N);
                    displayPopup(findTerrain(island, x, y, N));
                    draw(island, N);
                    lockedMouse = false;
                }
                catch (IndexOutOfBoundsException e) {
                    lockedMouse = false;
                    continue;
                }
            }

            if (StdDraw.isKeyPressed(KeyEvent.VK_N)) {
                island = null;
                island = new Island(N);
                draw(island, N);
            }

            StdDraw.show(250);
        }
    }
}