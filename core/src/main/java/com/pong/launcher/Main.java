package com.pong.launcher;

import com.badlogic.gdx.ApplicationAdapter;
import com.pong.controller.Controller;

public class Main extends ApplicationAdapter {
    @Override
    public void create() {
        Controller.getSingleInstance();
    }

    @Override
    public void render() {
        Controller.getSingleInstance().render();
    }

    @Override
    public void dispose() {
        Controller.getSingleInstance().dispose();
    }
}

