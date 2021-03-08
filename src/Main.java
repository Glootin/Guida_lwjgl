import org.joml.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {

    static long windowIndex;

    static private double previousLoop;

    static private final ArrayList<Vector3f> polygon = new ArrayList<>();

    static private float rotation = 0, scale = 0.5f, posx = 0, posy = 0, red = 1, green = 1, blue = 1;

    static float speedx = 0, speedy = 0, rotSpeed = 0, scaleSpeed = 0, redSpeed = 0, greenSpeed = 0, blueSpeed = 0;


    public static void main(String[] args) {
        initWindow();
        initLoop();
        initPolygon();
        loop();
    }

    private static void loop() {
        double time;
        double sum = 0;
        double interval = 1d / 30d;

        //Il loop di aggiornamento continua fino a una richiesta di interruzione dell'utente
        while (!glfwWindowShouldClose(windowIndex)) {
            //Calcolo la differenza di tempo tra l'iterazione corrente e quella precedente
            time = getTimeDifference();
            sum += time;

            handleInput();

            //In caso siano stati saltati dei frame i modelli vengono aggiornati 1 volta per ogni interval
            while (sum >= interval) {
                windowUpdate();
                sum -= interval;
            }

            //renderizza il poligono
            render();

        }
    }

    public static double getTime() {
        return System.nanoTime() / 1000_000_000d;
    }

    private static void handleInput() {
        if (isKeyPressed(GLFW_KEY_UP))
            speedy = 0.02f;
        else if (isKeyPressed(GLFW_KEY_DOWN))
            speedy = -0.02f;
        else speedy = 0;


        if (isKeyPressed(GLFW_KEY_LEFT))
            speedx = -0.02f;
        else if (isKeyPressed(GLFW_KEY_RIGHT))
            speedx = 0.02f;
        else speedx = 0;

        if (isKeyPressed(GLFW_KEY_Z))
            rotSpeed = 3;
        else if (isKeyPressed(GLFW_KEY_C))
            rotSpeed = -3;
        else rotSpeed = 0;

        if (isKeyPressed(GLFW_KEY_KP_ADD) || isKeyPressed(GLFW_KEY_RIGHT_BRACKET))
            scaleSpeed = 0.02f;
        else if (isKeyPressed(GLFW_KEY_KP_SUBTRACT) || isKeyPressed(GLFW_KEY_SLASH))
            scaleSpeed = -0.02f;
        else scaleSpeed = 0;

        if (isKeyPressed(GLFW_KEY_1))
            redSpeed = 0.008f;
        else
            redSpeed = 0;

        if (isKeyPressed(GLFW_KEY_2))
            greenSpeed = 0.008f;
        else
            greenSpeed = 0;

        if (isKeyPressed(GLFW_KEY_3))
            blueSpeed = 0.008f;
        else
            blueSpeed = 0;
    }

    //Metodo che ritorna lo stato del pulsante richiesto
    private static boolean isKeyPressed(int keyCode) {
        return glfwGetKey(windowIndex, keyCode) == GLFW_PRESS;
    }

    private static void windowUpdate() {
        posy += speedy;
        posx += speedx;
        rotation += rotSpeed;

        scale+= scaleSpeed;
        red = red + redSpeed <= 1 ? red + redSpeed : 0;
        green = green + greenSpeed <= 1 ? green + greenSpeed : 0;
        blue = blue + blueSpeed <= 1 ? blue + blueSpeed : 0;
    }

    private static void render() {

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        //Inizia il rendering sotto forma di poligono
        glBegin(GL_POLYGON);
        //imposta il colore del poligono generato
        glColor3f(red, green, blue);
        //creo la matrice delle trasformate
        Matrix4f matr = new Matrix4f();
        //applico le trasformate e poi moltiplico ogni vertice per la matrice
        matr.identity().translate(posx, posy, 0)
                .rotateZ((float) Math.toRadians(rotation))
                .scale(scale);
        polygon.forEach(vec -> {
            var v = new Vector4f(vec.x, vec.y, vec.z, 1);
            v.mul(matr);
            glVertex3f(v.x, v.y, 0);
        });
        //fine del rendering, flush e swap del buffer della schermata
        glEnd();
        glFlush();
        glfwSwapBuffers(windowIndex);
        //Processa tutti gli eventi pendenti con le relative callback
        glfwPollEvents();
    }

    private static void initLoop() {
        previousLoop = getTime();
    }

    private static double getTimeDifference() {
        double time = getTime();
        double difference = time - previousLoop;
        previousLoop = time;
        return difference;
    }

    private static void initWindow() {
        final int width = 600, height = 400;

        GLFWErrorCallback.createPrint(System.err).set();

        //Inizializza la libreria, necessario al suo funzionamento
        if (!glfwInit()) {
            throw new IllegalStateException("Impossibile inzializzare GLFW");
        }

        // crea una finestra e ritorna un long
        windowIndex = glfwCreateWindow(width, height, "My first LWJGL game", NULL, NULL);

        if (windowIndex == NULL) {
            throw new RuntimeException("Creazione della finestra non riuscita");
        }

        // Rende la finestra non ridimensionabile
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);


        //Posizione la
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
                windowIndex,
                (Objects.requireNonNull(vidmode).width() - width) / 2,
                (vidmode.height() - height) / 2
        );

        //Imposta una callback sul tast esc che termina il ciclo di gioco
        glfwSetKeyCallback(windowIndex, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });

        // Imposta il context
        glfwMakeContextCurrent(windowIndex);
        GL.createCapabilities();

        //Attiva il vertical sync
        glfwSwapInterval(1);

        // Imposta il colore della finestra a nero
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);


        // Rende visibile la finestra
        glfwShowWindow(windowIndex);
    }


    public static void initPolygon() {
        polygon.add(new Vector3f(0, 1f, 0));
        polygon.add(new Vector3f(0.866f, 0.5f, 0));
        polygon.add(new Vector3f(0.866f, -0.5f, 0));
        polygon.add(new Vector3f(0, -1f, 0));
        polygon.add(new Vector3f(-0.866f, -0.5f, 0));
        polygon.add(new Vector3f(-0.866f, 0.5f, 0));
    }

}
