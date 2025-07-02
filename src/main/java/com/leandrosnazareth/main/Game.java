package com.leandrosnazareth.main;

import java.io.IOException;

import com.leandrosnazareth.render.Renderer;
import com.leandrosnazareth.world.World;

public class Game implements Runnable {
    private Thread gameThread;
    private boolean running = false;

    private GameWindow window;
    private InputHandler input;
    private Renderer renderer;
    private World world;

    public Game() {
        window = new GameWindow("Java Game", 1280, 720);
        // Não inicializa a janela aqui, será feito na thread do jogo
        renderer = new Renderer();
        world = new World();
    }

    public void start() {
        gameThread = new Thread(this, "Game Thread");
        gameThread.start();
    }

    private void init() throws IOException {
        System.out.println("Initializing window...");
        // Inicializa a janela e o contexto OpenGL na thread do jogo
        window.init();

        System.out.println("Creating input handler...");
        // Cria o input handler após a janela estar inicializada
        input = new InputHandler(window);

        System.out.println("Initializing renderer...");
        // Agora pode inicializar o renderer com o contexto OpenGL ativo
        renderer.init();

        System.out.println("Initializing world...");
        world.init();

        System.out.println("Initialization complete!");
    }

    private void update(float delta) {
        // Debug para verificar se está sendo chamado
        int updateCount = 0;
        if (updateCount % 60 == 0) {
            System.out.println("Game.update() called - Frame: " + updateCount);
        }
        updateCount++;

        world.update(delta, input);
    }

    private void render() {
        renderer.clear();
        renderer.getShader().bind();
        world.render(renderer);
        renderer.getShader().unbind();
        window.swapBuffers();
    }

    @Override
    public void run() {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        running = true;
        System.out.println("Starting game loop...");

        long lastTime = System.nanoTime();
        double nsPerTick = 1000000000.0 / 60.0;
        long lastTimer = System.currentTimeMillis();
        double delta = 0;
        int frames = 0;
        int ticks = 0;

        while (running && !window.shouldClose()) {
            window.pollEvents(); // Processa eventos primeiro

            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;

            while (delta >= 1) {
                ticks++;
                update(1.0f / 60.0f); // Delta fixo para 60 FPS
                delta--;
            }

            render();
            frames++;

            if (System.currentTimeMillis() - lastTimer >= 1000) {
                lastTimer += 1000;
                System.out.println("FPS: " + frames + ", TPS: " + ticks);
                frames = 0;
                ticks = 0;
            }

            // Pequena pausa para evitar uso excessivo de CPU
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }

        cleanup();
    }

    private void cleanup() {
        world.cleanup();
        renderer.cleanup();
        window.cleanup();
    }
}