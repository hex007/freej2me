/*
	This file is part of FreeJ2ME.

	FreeJ2ME is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	FreeJ2ME is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with FreeJ2ME.  If not, see http://www.gnu.org/licenses/
*/

/*
	FreeJ2ME - JavaFx
*/

package org.recompile.freej2me;

import org.recompile.mobile.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class JavaFx extends Application
{
	private List<String> args;
	private MIDletLoader loader;

	private int lcdWidth = 240;
	private int lcdHeight = 320;

	private java.awt.image.BufferedImage mobilelcd;
	private ImageView lcdview;

	private WritableImage[] frames;
	private PixelWriter[] pixelwriters;
	private int currentFrame = 0;

	private Stage stage;

	private boolean needsUpdated = true;

	Rectangle2D screenRect;

	public static void main(String args[])
	{
		Application.launch(args);
	}

	public void stop() { Platform.exit(); System.exit(0); }

	public void start(Stage startStage)
	{
		stage = startStage;

		args = getParameters().getRaw();

		// Setup UI //
		stage.setTitle("FreeJ2ME");
		stage.setOnCloseRequest(e -> stop() );

		stage.getIcons().add(new Image(getClass().getResourceAsStream("/org/recompile/icon.png")));

		screenRect = Screen.getPrimary().getVisualBounds();

		BorderPane Root = new BorderPane();

		BackgroundFill bgfill = new BackgroundFill(Color.rgb(0,0,64), null, null);
		Root.setBackground(new Background(bgfill));

		// Setup Device //

		int argCount = 0;
		for (String arg : args)
		{
			argCount++;
			System.out.print("Args: ");
			System.out.println(arg);
		}

		if(argCount==3)
		{
			lcdWidth = Integer.parseInt(args.get(1));
			lcdHeight = Integer.parseInt(args.get(2));
		}

		Mobile.setPlatform(new MobilePlatform(lcdWidth, lcdHeight));
		mobilelcd = Mobile.getPlatform().getLCD();

		lcdview = new ImageView();
		setLCDViewSize();

		lcdview.setSmooth(false);
		lcdview.setPreserveRatio(false);

		stage.widthProperty().addListener((obs, oldVal, newVal) -> { setLCDViewSize(); });
		stage.heightProperty().addListener((obs, oldVal, newVal) -> { setLCDViewSize(); });

		Root.setCenter(lcdview);

		frames = new WritableImage[]{ new WritableImage(lcdWidth, lcdHeight), new WritableImage(lcdWidth, lcdHeight) };
		pixelwriters = new PixelWriter[]{ frames[0].getPixelWriter(), frames[1].getPixelWriter() };

		Runnable painter = new Runnable()
		{
			public void run()
			{
				if(needsUpdated)
				{
					for(int y=0; y<lcdHeight; y++)
					{
						for(int x=0; x<lcdWidth; x++)
						{
							pixelwriters[currentFrame].setArgb(x, y, mobilelcd.getRGB(x,y));
						}
					}
					lcdview.setImage(frames[currentFrame]);
					if(currentFrame>0) { currentFrame=0; } else { currentFrame=1; }
				}
			}
		};
		Mobile.getPlatform().setPainter(painter);

		// full screen //
		stage.setX(screenRect.getMinX());
		stage.setY(screenRect.getMinY());
		stage.setWidth(screenRect.getWidth());
		stage.setHeight(screenRect.getHeight());

		stage.setScene(new Scene(Root));
		stage.show();

		stage.addEventHandler(KeyEvent.KEY_PRESSED, e->
		{
			Mobile.getPlatform().keyPressed(findKeyCode(e.getCode()));
		});

		stage.addEventHandler(KeyEvent.KEY_RELEASED, e->
		{
			Mobile.getPlatform().keyReleased(findKeyCode(e.getCode()));
		});

		lcdview.addEventHandler(MouseEvent.MOUSE_PRESSED, e->
		{
			double x = e.getX();
			double y = e.getY();
			double rw = lcdWidth/lcdview.getFitWidth();
			double rh = lcdHeight/lcdview.getFitHeight();

			Mobile.getPlatform().pointerPressed((int)(x*rw), (int)(y*rh));
		});

		lcdview.addEventHandler(MouseEvent.MOUSE_RELEASED, e->
		{
			double x = e.getX();
			double y = e.getY();
			double rw = lcdWidth/lcdview.getFitWidth();
			double rh = lcdHeight/lcdview.getFitHeight();

			Mobile.getPlatform().pointerReleased((int)(x*rw), (int)(y*rh));
		});

		if(argCount>0)
		{
			if(Mobile.getPlatform().loadJar(args.get(0)))
			{
				Mobile.getPlatform().runJar();
			}
			else
			{
				System.out.println("Couldn't load jar...");
			}
		}

	}

	private void setLCDViewSize()
	{
		double vw = stage.getWidth()*0.9;
		double vh = stage.getHeight()*0.9;

		double max = vh;
		if(vh > vw) { max = vw; }

		if(lcdWidth<lcdHeight)
		{
			lcdview.setFitHeight(max);
			lcdview.setFitWidth(max*((double)lcdWidth)/((double)lcdHeight));
		}
		else
		{
			lcdview.setFitWidth(max);
			lcdview.setFitHeight(max*((double)lcdHeight)/((double)lcdWidth));
		}
	};

	private int findKeyCode(KeyCode code)
	{
		if(code==KeyCode.DIGIT0) { return Mobile.KEY_NUM0; }
		if(code==KeyCode.DIGIT1) { return Mobile.KEY_NUM1; }
		if(code==KeyCode.DIGIT2) { return Mobile.KEY_NUM2; }
		if(code==KeyCode.DIGIT3) { return Mobile.KEY_NUM3; }
		if(code==KeyCode.DIGIT4) { return Mobile.KEY_NUM4; }
		if(code==KeyCode.DIGIT5) { return Mobile.KEY_NUM5; }
		if(code==KeyCode.DIGIT6) { return Mobile.KEY_NUM6; }
		if(code==KeyCode.DIGIT7) { return Mobile.KEY_NUM7; }
		if(code==KeyCode.DIGIT8) { return Mobile.KEY_NUM8; }
		if(code==KeyCode.DIGIT9) { return Mobile.KEY_NUM9; }
		if(code==KeyCode.ASTERISK) { return Mobile.KEY_STAR; }
		if(code==KeyCode.NUMBER_SIGN) { return Mobile.KEY_POUND; }
		if(code==KeyCode.UP)    { return Mobile.GAME_UP; }
		if(code==KeyCode.DOWN)  { return Mobile.GAME_DOWN; }
		if(code==KeyCode.LEFT)  { return Mobile.GAME_LEFT; }
		if(code==KeyCode.RIGHT) { return Mobile.GAME_RIGHT; }
		if(code==KeyCode.ENTER) { return Mobile.GAME_FIRE; }
		if(code==KeyCode.Z) { return Mobile.GAME_A; }
		if(code==KeyCode.X) { return Mobile.GAME_B; }
		if(code==KeyCode.C) { return Mobile.GAME_C; }
		if(code==KeyCode.V) { return Mobile.GAME_D; }
		if(code==KeyCode.W) { return Mobile.NOKIA_UP; }
		if(code==KeyCode.S) { return Mobile.NOKIA_DOWN; }
		if(code==KeyCode.A) { return Mobile.NOKIA_LEFT; }
		if(code==KeyCode.D) { return Mobile.NOKIA_RIGHT; }
		if(code==KeyCode.Q) { return Mobile.NOKIA_SOFT1; }
		if(code==KeyCode.E) { return Mobile.NOKIA_SOFT2; }
		if(code==KeyCode.R) { return Mobile.NOKIA_SOFT3; }
		if(code==KeyCode.M) { stop(); }
		if(code==KeyCode.O) { needsUpdated = true; }
		if(code==KeyCode.P) { needsUpdated = false; }
		return 0;
	}

}
