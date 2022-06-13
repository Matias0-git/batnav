package batnav.ui.boards;

import batnav.instance.Game;
import batnav.notifications.Notification;
import batnav.online.model.Bomb;
import batnav.online.model.Ship;
import batnav.utils.Colour;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Board extends JPanel
{
   private final int tileSize = 26;
   private final int boardSize = tileSize * 10;

   private boolean disabled;

   private boolean filled;

   public Board()
   {
      // As the Board is a button, disabling the default values for styling is necessary.
      this.setOpaque(this.filled);
   }

   @Override
   public void paint(Graphics g)
   {
      Graphics2D g2d = (Graphics2D) g;

      // Set board position and size.
      final int paddingX = (this.getWidth() - boardSize) / 2;
      final int paddingY = (this.getHeight() - boardSize) / 2;

      // Draw the main board background.
      GradientPaint gp = new GradientPaint(0, 0,
           new Colour(171, 202, 224), getWidth(), getHeight(), new Colour(240, 248, 255));

      g2d.setPaint(gp);
      g.fillRect(paddingX, paddingY, boardSize, boardSize);

      // Draw each line.
      g.setColor(disabled ? Colour.DarkSlateGray : Colour.Black);
      for (int i = 0; i < 11; i++)
      {
         g.drawLine(paddingX + i * tileSize, paddingY, paddingX + i * tileSize, paddingY + boardSize);
         g.drawLine(paddingX, paddingY + i * tileSize, paddingX + boardSize, paddingY + i * tileSize);
      }

      if (disabled)
      {
         g.setColor(Colour.Gray.alpha(90));
         g.fillRect(paddingX, paddingY, boardSize, boardSize);
      }
   }

   public void update()
   {
      this.repaint();
   }

   public void drawBomb(Graphics g, final Bomb bomb)
   {
      final int paddingX = (this.getWidth() - boardSize) / 2;
      final int paddingY = (this.getHeight() - boardSize) / 2;

      final int x = paddingX + bomb.getX() * tileSize;
      final int y = paddingY + bomb.getY() * tileSize;

      g.setColor(bomb.isHasHit() ? Colour.DarkRed : Colour.DarkGray);
      final int bombSize = 15;
      final int padding = (tileSize - bombSize) / 2;
      g.fillOval(x + padding, y + padding, bombSize, bombSize);
   }

   public void drawShip(Graphics g, final Ship ship)
   {
      if (ship.getX() != null && ship.getY() != null)
      {
         final Graphics2D g2d = (Graphics2D) g;

         final int paddingX = (this.getWidth() - boardSize) / 2;
         final int paddingY = (this.getHeight() - boardSize) / 2;

         final int x = paddingX + ship.getX() * tileSize;
         final int y = paddingY + ship.getY() * tileSize;

         try
         {
            final BufferedImage image = ImageIO.read(new File("assets/ships/ship" + ship.getSize() + ".png"));

            final AffineTransform affineTransform = new AffineTransform();
            affineTransform.translate(x + (ship.isVertical() ? tileSize : 0), y);
            final double scale = tileSize * 0.01;
            affineTransform.scale(scale, scale);

            if (ship.isVertical())
            {
               affineTransform.rotate(1.5708); // π / 2 rad = 90 deg
            }

            g2d.drawImage(image, affineTransform, null);

         } catch (IOException e)
         {
            Game.getInstance().getNotificationManager().addNotification(
                 new Notification(
                      Notification.Priority.CRITICAL,
                      "Ha ocurrido un error",
                      "Hubo un error leyendo las texturas para los barcos",
                      a -> {
                      }
                 )
            );
         }
      }
   }

   public int[] handleClick(final Point point)
   {
      if (!disabled)
      {
         // Set board position and size.
         final int paddingX = (this.getWidth() - boardSize) / 2 + this.getX();
         final int paddingY = (this.getHeight() - boardSize) / 2 + this.getY();

         // Check if the click was executed inside the board.
         if (point.x > paddingX && point.y > paddingY && point.x < paddingX + boardSize && point.y < paddingY + boardSize)
         {
            // Perform calculations to get coordinates and return the values in an array.
            return new int[]{(point.x - paddingX) / tileSize, (point.y - paddingY) / tileSize};
         } else
            return null;
      } else
         return null;
   }

   public boolean isDisabled()
   {
      return disabled;
   }

   public void setDisabled(boolean disabled)
   {
      this.disabled = disabled;
      this.repaint();
   }

   public void setFilled(boolean filled)
   {
      this.filled = filled;
   }
}